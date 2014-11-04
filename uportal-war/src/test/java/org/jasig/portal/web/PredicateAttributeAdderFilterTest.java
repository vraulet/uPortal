package org.jasig.portal.web;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Predicate;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.provider.PersonImpl;
import org.jasig.portal.user.IUserInstance;
import org.jasig.portal.user.IUserInstanceManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PredicateAttributeAdderFilterTest {

    @Mock private IUserInstanceManager userInstanceManager;
    @Mock private IUserInstance userInstance;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;
    @Mock private Predicate predicate;

    private IPerson person;
    private PredicateAttributeAdderFilter filter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        person = new PersonImpl();

        when(userInstanceManager.getUserInstance(request)).thenReturn(userInstance);
        when(userInstance.getPerson()).thenReturn(person);

        filter = new PredicateAttributeAdderFilter();
        filter.setUserInstanceManager(userInstanceManager);
        filter.setPersonAttributeName("name");
        filter.setPersonAttributeValue("foo");
        filter.setPredicate(predicate);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDoFilterTruePredicate() throws Exception {
        when(predicate.apply(request)).thenReturn(true);

        filter.doFilter(request, response, filterChain);
        assertEquals("Attribute not set", "foo", person.getAttribute("name"));
    }

    @Test
    public void testDoFilterFalsePredicate() throws Exception {
        when(predicate.apply(request)).thenReturn(false);

        filter.doFilter(request, response, filterChain);
        assertNull("Attribute was set", person.getAttributes());
    }
}