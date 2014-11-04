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

import org.jasig.portal.IUserPreferencesManager;
import org.jasig.portal.IUserProfile;
import org.jasig.portal.layout.dao.IStylesheetDescriptorDao;
import org.jasig.portal.layout.om.IStylesheetDescriptor;
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
 * Unit tests for ThemeNamePredicateTest.
 *
 * Implementation note: this test currently suffers from the TestMirrorsImplementation testing anti-pattern,
 * in that it relies upon mocking up exactly the lookup path in the implementation.  It would be better to use more
 * real objects and less mock objects by mocking up only the bits necessary to make real objects interpret the mocks
 * as meaning the indicated active theme.
 * @since uPortal 4.2
 */
public class ThemeNamePredicateTest {

    @Mock private HttpServletRequest request;

    @Mock private IUserInstanceManager userInstanceManager;

    @Mock private IUserInstance userInstance;

    @Mock private IPerson person;

    @Mock private IUserPreferencesManager userPreferencesManager;

    @Mock private IUserProfile userProfile;

    @Mock private IStylesheetDescriptorDao stylesheetDescriptorDao;

    @Mock private IStylesheetDescriptor stylesheetDescriptor;

    private ThemeNamePredicate predicate;

    @Before
    public void beforeTests() {

        initMocks(this);

        when(userInstanceManager.getUserInstance(request)).thenReturn(userInstance);

        when(userInstance.getPerson()).thenReturn(person);

        when(userInstance.getPreferencesManager()).thenReturn(userPreferencesManager);
        when(userPreferencesManager.getUserProfile()).thenReturn(userProfile);

        int styleSheetId = 5;
        when(userProfile.getThemeStylesheetId()).thenReturn(styleSheetId);

        when(stylesheetDescriptorDao.getStylesheetDescriptor(styleSheetId)).thenReturn(stylesheetDescriptor);
        when(stylesheetDescriptor.getName()).thenReturn("styleDescriptorName");

        when(person.getUserName()).thenReturn("exampleUserName");

        predicate = new ThemeNamePredicate();
        predicate.setUserInstanceManager(userInstanceManager);
        predicate.setStylesheetDescriptorDao(stylesheetDescriptorDao);

    }


    /**
     * When the theme associated with the request has the expected name,
     * the predicate returns true.
     */
    @Test
    public void whenThemeNameMatchesReturnsTrue() {

        // configure to look for the theme name that will be found
        predicate.setThemeNameToMatch("styleDescriptorName");

        assertTrue( predicate.apply(request) );

    }

    /**
     * When the theme associated with the request does not have the configured name,
     * the predicate returns false.
     */
    @Test
    public void whenThemeNameDoesNotMatchReturnsFalse() {

        // configure to look for a theme name that will not be found.
        predicate.setThemeNameToMatch("willNotFindThis");

        assertFalse( predicate.apply(request) );

    }

    @Test
    public void hasFriendlyToString() {

        predicate.setThemeNameToMatch("someName");

        assertEquals("Predicate: true where theme name is someName.", predicate.toString());

    }


}
