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

import org.jasig.portal.IUserProfile;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.user.IUserInstance;
import org.jasig.portal.user.IUserInstanceManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for UserAttributePredicateTest.
 *
 * Implementation note: this test currently suffers from the TestMirrorsImplementation testing anti-pattern,
 * in that it relies upon mocking up exactly the lookup path in the implementation.  It would be better to use more
 * real objects and less mock objects by mocking up only the bits necessary to make real objects interpret the mocks.
 * @since uPortal 4.2
 */
public class UserAttributePredicateTest {

    @Mock private HttpServletRequest request;

    @Mock private IUserInstanceManager userInstanceManager;

    @Mock private IUserInstance userInstance;

    @Mock private IPerson person;

    @Mock private IUserProfile userProfile;

    private Object[] values;

    private UserAttributePredicate predicate;

    @Before
    public void beforeTests() {

        initMocks(this);

        when(userInstanceManager.getUserInstance(request)).thenReturn(userInstance);

        when(userInstance.getPerson()).thenReturn(person);

        when(person.getUserName()).thenReturn("exampleUserName");

        values = new Object[]{"item1", "item2"};
        when(person.getAttributeValues("attributeName")).thenReturn(values);

        predicate = new UserAttributePredicate();
        predicate.setUserInstanceManager(userInstanceManager);

    }


    /**
     * When the person associated with the request has the expected attribute with any value,
     * the predicate returns true.
     */
    @Test
    public void whenUserAttributeMatchesAnyValueReturnsTrue() {

        // configure to look for the attribute name that will be found
        predicate.setUserAttributeName("attributeName");

        assertTrue( predicate.apply(request) );

    }

    /**
     * When the person associated with the request has the expected attribute with the specified value,
     * the predicate returns true.
     */
    @Test
    public void whenUserAttributeMatchesValueReturnsTrue() {

        // configure to look for the attribute name that will be found
        predicate.setUserAttributeName("attributeName");
        predicate.setUserAttributeValue("item1");

        assertTrue( predicate.apply(request) );

    }

    /**
     * When the person associated with the request does not have the expected attribute,
     * the predicate returns false.
     */
    @Test
    public void whenUserAttributeNotFoundReturnsFalse() {

        // configure to look for an attribute name that will not be found.
        predicate.setUserAttributeName("willNotFindThis");

        assertFalse( predicate.apply(request) );

    }

    /**
     * When the person associated with the request does not have the expected attribute,
     * the predicate returns false.
     */
    @Test
    public void whenUserAttributeValueNotFoundReturnsFalse() {

        // configure to look for an attribute value that will not be found.
        predicate.setUserAttributeName("attributeName");
        predicate.setUserAttributeValue("willNotBeFound");

        assertFalse( predicate.apply(request) );

    }

    @Test
    public void hasFriendlyToString() {

        predicate.setUserAttributeName("someName");
        predicate.setUserAttributeValue("someValue");

        assertEquals("Predicate: true where user has attribute with name someName and value someValue",
                predicate.toString());

    }


}
