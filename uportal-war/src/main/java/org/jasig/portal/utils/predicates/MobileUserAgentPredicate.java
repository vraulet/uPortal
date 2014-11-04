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

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * True if the HttpRequest User-Agent HTTP header matches a mobile user agent string regex pattern.
 * @since uPortal 4.2
 */
public class MobileUserAgentPredicate
    implements Predicate<HttpServletRequest> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String USER_AGENT_HEADER = "User-Agent";

    private String userAgentRegexString;
    Pattern userAgentRegex;

    /**
     * Sets the user agent regex pattern string to compare with the user agent header from the browser.
     * @param userAgentRegexString
     */
    @Required
    public void setUserAgentRegexString(String userAgentRegexString) {
        this.userAgentRegexString = userAgentRegexString;
        userAgentRegex = Pattern.compile(userAgentRegexString);
    }

    @Override
    public boolean apply(final HttpServletRequest request) {

        boolean result = userAgentRegex.matcher(request.getHeader(USER_AGENT_HEADER)).matches();
        logger.debug("User agent {} match mobile user-agent RegEx string.",
                result ? "does" : "does not");
        return result;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}
