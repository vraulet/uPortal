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
import org.jasig.portal.security.IPerson;
import org.jasig.portal.user.IUserInstance;
import org.jasig.portal.user.IUserInstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Predicate determining whether the user for the given request has the configured user attribute, and optionally
 * if it has the indicated (non-null) value.
 * @since uPortal 4.2
 * @author James Wennmacher jwennmacher@unicon.net
 */
public class UserAttributePredicate
    implements Predicate<HttpServletRequest> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // auto-wired
    private IUserInstanceManager userInstanceManager;

    // dependency-injected
    private String userAttributeName;
    private String userAttributeValue;


    @Override
    public boolean apply(final HttpServletRequest request) {
        final IUserInstance userInstance = this.userInstanceManager.getUserInstance(request);
        return attributeApplies(userInstance.getPerson());
    }

    /**
     * Return true if the person has a user attribute with the configured name and optionally has the configured
     * value.
     * @param person Person whose attribute to check
     * @return True if the user has the indicated user attribute and optionally configured value.
     */
    private boolean attributeApplies(IPerson person) {
        // used for logging
        final String username = person.getUserName();

        Object[] attributeValues = person.getAttributeValues(userAttributeName);
        if (attributeValues == null) {
            logger.debug("User {} does not have attribute {}.",username, userAttributeName );
            return false;  // not present
        }
        if (userAttributeValue == null) {
            logger.debug("User {} does have attribute {} (with any value).",username, userAttributeName );
            return true; // present but don't care about value
        }

        for (int i = 0; i < attributeValues.length; i++) {
            if (userAttributeValue.equals(attributeValues[i])) {
                logger.debug("User {} does have attribute {} with value {}.",username, userAttributeName,
                        userAttributeValue);
                return true;
            }
        }
        logger.debug("User {} does not have attribute {}.",username, userAttributeName );
        return false;
    }

    @Autowired
    public void setUserInstanceManager(final IUserInstanceManager userInstanceManager) {
        Assert.notNull(userInstanceManager);
        this.userInstanceManager = userInstanceManager;
    }

    /**
     * Sets the name of the user attribute to check.
     * @param userAttributeName user attribute name
     */
    @Required
    public void setUserAttributeName(final String userAttributeName) {
        this.userAttributeName = userAttributeName;
    }

    /**
     * Sets the case-ignore value the user attribute must have for the predicate to apply.  Null (default) if
     * any attribute value is considered a match.
     * @param userAttributeValue value user attribute must have
     */
    public void setUserAttributeValue(final String userAttributeValue) {
        this.userAttributeValue = userAttributeValue;
    }

    @Override
    public String toString() {
        return "Predicate: true where user has attribute with name " + this.userAttributeName
                + (userAttributeValue != null ? " and value " + userAttributeValue : ".");
    }

}
