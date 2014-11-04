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

package org.jasig.portal.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Predicate;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.user.IUserInstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

/**
 * {@link javax.servlet.Filter} that executes a {@link com.google.common.base.Predicate}> and if true
 * adds a configured userAttribute to the person (replaces if already present).  The attribute is
 * not cached; each person, including the guest person, has their own session-based person instance
 * so adding the attribute does not affect other sessions with the same user (critical for proper guest behavior).
 * The Predicate processing occurs after the filter chain execution to allow the filter to be used with
 * the login processing to authenticate and configure the person before the Predicate evaluation occurs.
 *
 * @author James Wennmacher
 * @since 4.2
 */
public class PredicateAttributeAdderFilter extends GenericFilterBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private IUserInstanceManager userInstanceManager;
    private Predicate<HttpServletRequest> predicate;
    private String personAttributeName;
    private String personAttributeValue;

    /**
     * Sets the user instance manager to use to look up persons with.
     * @param userInstanceManager user instance manager
     */
    @Autowired
    public void setUserInstanceManager(final IUserInstanceManager userInstanceManager) {
        Assert.notNull(userInstanceManager);
        this.userInstanceManager = userInstanceManager;
    }

    public Predicate<HttpServletRequest> getPredicate() {
        return predicate;
    }

    /**
     * Set the predicate to apply to the request to determine whether to add the attribute to the user.
     * @param predicate Predicate to apply
     */
    @Required
    public void setPredicate(Predicate<HttpServletRequest> predicate) {
        this.predicate = predicate;
    }

    public String getPersonAttributeName() {
        return personAttributeName;
    }

    /**
     * Sets the attribute name to add to the person.  If the attribute is already present, all values are
     * replaced with <code>attributeValue</code>
     * @param personAttributeName Name of the attribute to add/replace
     */
    @Required
    public void setPersonAttributeName(String personAttributeName) {
        this.personAttributeName = personAttributeName;
    }

    public String getPersonAttributeValue() {
        return personAttributeValue;
    }

    /**
     * Sets the attribute value to add to the person.
     * @param personAttributeValue Attribute value.
     */
    @Required
    public void setPersonAttributeValue(String personAttributeValue) {
        this.personAttributeValue = personAttributeValue;
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public final void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        chain.doFilter(servletRequest, servletResponse);

        // After the filter chain has run, if the predicate applies set the configured attribute and value
        // on the current person.
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (predicate.apply(httpServletRequest)) {
            IPerson person = userInstanceManager.getUserInstance(httpServletRequest).getPerson();
            person.setAttribute(personAttributeName, personAttributeValue);
            logger.debug("Set attribute {} with value {} for {}", personAttributeName, personAttributeValue,
                    person.getUserName());
        }
    }
}
