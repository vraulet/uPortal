/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portal.rendering;

import java.io.IOException;

import javax.portlet.WindowState;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jasig.portal.IUserPreferencesManager;
import org.jasig.portal.layout.IUserLayoutStore;
import org.jasig.portal.layout.dlm.DistributedUserLayout;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.url.IPortalUrlBuilder;
import org.jasig.portal.url.IPortalUrlProvider;
import org.jasig.portal.url.IPortletUrlBuilder;
import org.jasig.portal.url.UrlType;
import org.jasig.portal.user.IUserInstance;
import org.jasig.portal.user.IUserInstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * PortalRenderingPipeline that issues a redirect to a single portlet present on the user's layout.  Selected portlet
 * is configured with an Xpath string applied to the post-structure XML representation.  By default this is the
 * first portlet on the first tab.  If no portlets are found, invokes the <code>noPortletsPipeline</code> to render
 * the pipeline.
 *
 * Intended as a Pipeline implementation that might be plugged into a branch in the overall rendering pipeline and
 * conditionally actuated.
 *
 * @since uPortal 4.2
 * @author James Wennmacher, jwennmacher@unicon.net
 */

public class SinglePortletRedirectRenderingPipelineTerminator implements IPortalRenderingPipeline {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private IUserInstanceManager userInstanceManager;
    private IUserLayoutStore userLayoutStore;
    // xPath expression to get the first channel in the first tab that has any portlets on it (may return
    // multiple nodes due to multiple tabs and multiple columns on a tab).
    private String xpathExpression = "/layout/folder/folder[@hidden = 'false' and @type = 'regular']//channel[@hidden = 'false'][1]";

    private IPortalUrlProvider portalUrlProvider;
    private IPortalRenderingPipeline noPortletsPipeline;

    @Required
    public void setNoPortletsPipeline(IPortalRenderingPipeline noPortletsPipeline) {
        this.noPortletsPipeline = noPortletsPipeline;
    }

    @Autowired
    public void setPortalUrlProvider(IPortalUrlProvider portalUrlProvider) {
        this.portalUrlProvider = portalUrlProvider;
    }

    /**
     * Sets the Xpath expression to identify a portlet on the user's layout to redirect to.
     * @param xpathExpression
     */
    public void setXpathExpression(String xpathExpression) {
        this.xpathExpression = xpathExpression;
    }

    @Autowired
    public void setUserInstanceManager(IUserInstanceManager userInstanceManager) {
        this.userInstanceManager = userInstanceManager;
    }

    @Autowired
    public void setUserLayoutStore(IUserLayoutStore userLayoutStore) {
        this.userLayoutStore = userLayoutStore;
    }

    /*
     * Determines the portlet to invoke and render the pipeline.
     */
    @Override
    public void renderState(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Document userLayoutDoc = getUserLayoutAsDocument(req);

        String focusedPortletUrl = computeFirstFocusedPortletUrl(req, userLayoutDoc);
        // If there are no portlets, return the fallback
        if (focusedPortletUrl == null) {
            logger.info("Found 0 portlets in layout for user. Reverting to noPortlets pipeline {}",
                    noPortletsPipeline);
            noPortletsPipeline.renderState(req, res);
        }
        logger.debug("Redirecting to {} .", focusedPortletUrl);
        res.sendRedirect(focusedPortletUrl);
    }

    /**
     * Computes an URL to target the first portlet identified by the configured <code>xPathExpression</code> found in
     * the user's layout, or null if there are no portlets in the layout.
     * @param req HttpRequest
     * @param userLayoutDoc userLayout as an XML Document (effectively post-structure transform)
     * @return url to redirect to, or null if there are no portlets identified by the xPathExpression in the
     *         user's layout.
     */
    private String computeFirstFocusedPortletUrl(HttpServletRequest req, Document userLayoutDoc) {
        String url = null;

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        try {
            XPathExpression contentPortletsXpathExpression = xpath.compile(xpathExpression);
            NodeList list = (NodeList)contentPortletsXpathExpression.evaluate(userLayoutDoc, XPathConstants.NODESET);
            // If no portlets returned from the Xpath expression, return null.
            if (list.getLength() == 0) {
                return null;
            } else {
                // Compute an URL in Maximized state for the first portlet found in the user's layout
                Node node = list.item(0);
                String fname = node.getAttributes().getNamedItem("fname").getNodeValue();
                final IPortalUrlBuilder portalUrlBuilder = this.portalUrlProvider.getPortalUrlBuilderByPortletFName
                        (req, fname, UrlType.RENDER);
                IPortletUrlBuilder portletUrlBuilder = portalUrlBuilder.getPortletUrlBuilder(portalUrlBuilder.getTargetPortletWindowId());
                portletUrlBuilder.setWindowState(WindowState.MAXIMIZED);
                url = portalUrlBuilder.getUrlString();
            }

        } catch (XPathExpressionException e) {
            logger.error("Error evaluating xpath", e);
        }

        return url;
    }

    /**
     * Get the {@link org.jasig.portal.layout.IUserLayout} for the user making the request.
     * @param request HttpRequest
     */
    protected Document getUserLayoutAsDocument(HttpServletRequest request) {
        final IUserInstance userInstance = this.userInstanceManager.getUserInstance(request);
        final IUserPreferencesManager preferencesManager = userInstance.getPreferencesManager();

        IPerson person = userInstance.getPerson();
        DistributedUserLayout userLayout = userLayoutStore.getUserLayout(person, preferencesManager.getUserProfile());
        return userLayout.getLayout();
    }
}
