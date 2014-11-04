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

package org.jasig.portal.utils.predicates;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Predicate;
import org.jasig.portal.layout.dao.IStylesheetDescriptorDao;
import org.jasig.portal.layout.om.IStylesheetDescriptor;
import org.jasig.portal.user.IUserInstance;
import org.jasig.portal.user.IUserInstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Predicate determining whether the current user uses a stylesheet theme name matching the configured theme name.
 * @since uPortal 4.2
 * @author James Wennmacher jwennmacher@unicon.net
 */
public class ThemeNamePredicate
    implements Predicate<HttpServletRequest> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // auto-wired
    private IUserInstanceManager userInstanceManager;
    private IStylesheetDescriptorDao stylesheetDescriptorDao;

    // dependency-injected
    private String themeNameToMatch;


    @Override
    public boolean apply(final HttpServletRequest request) {
        final IUserInstance userInstance = this.userInstanceManager.getUserInstance(request);

        IStylesheetDescriptor descriptor = getCurrentUserProfileStyleSheetDescriptor(userInstance);
        String uiTheme = descriptor.getName();

        // used for logging
        final String username = userInstance.getPerson().getUserName();

        if (themeNameToMatch.equals(uiTheme)) {
            logger.debug("User {} does use UI theme matching name {}.",username, uiTheme );
            return true;
        }

        logger.debug("Request for user {} uses UI theme {} which does not match configured theme {}.",
                username, uiTheme, themeNameToMatch);
        return false;
    }

    /**
     * Gets the current user's stylesheet descriptor.
     * @param userInstance user instance
     * @return Current user's stylesheet descriptor.
     */
    private IStylesheetDescriptor getCurrentUserProfileStyleSheetDescriptor(IUserInstance userInstance) {
        int profileId = userInstance.getPreferencesManager().getUserProfile().getThemeStylesheetId();
        return this.stylesheetDescriptorDao.getStylesheetDescriptor(profileId);
    }

    @Autowired
    public void setUserInstanceManager(final IUserInstanceManager userInstanceManager) {
        Assert.notNull(userInstanceManager);
        this.userInstanceManager = userInstanceManager;
    }

    @Autowired
    public void setStylesheetDescriptorDao(IStylesheetDescriptorDao stylesheetDescriptorDao) {
        this.stylesheetDescriptorDao = stylesheetDescriptorDao;
    }

    /**
     * Set the name of the theme (style sheet descriptor) to check for.
     * @param themeNameToMatch
     */
    @Required
    public void setThemeNameToMatch(final String themeNameToMatch) {
        this.themeNameToMatch = themeNameToMatch;
    }

    @Override
    public String toString() {
        return "Predicate: true where theme name is " + this.themeNameToMatch + ".";
    }

}
