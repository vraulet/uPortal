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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for MobileUserAgentPredicate.
 * @since uPortal 4.2
 */
public class MobileUserAgentPredicateTest {

    @Mock private HttpServletRequest mockRequest;

    private MobileUserAgentPredicate predicate;

    @Before
    public void beforeTests() {

        initMocks(this);

        when(mockRequest.getHeader("User-Agent")).thenReturn("userAgentHeaderString");

        this.predicate = new MobileUserAgentPredicate();
    }

    /**
     * When no regex match, returns false.
     */
    @Test
    public void falseWhenNoMatch() {

        predicate.setUserAgentRegexString("NotUser.+String");
        assertFalse(predicate.apply(this.mockRequest));

    }

    /**
     * When regex matches, returns true
     */
    @Test
    public void trueWhenAddressesSpecificPortletMaximized() {

        predicate.setUserAgentRegexString("user.+String");
        assertTrue(predicate.apply(this.mockRequest));

    }

    @Test
    public void hasFriendlyToString() {

        assertEquals("MobileUserAgentPredicate", predicate.toString());

    }


}
