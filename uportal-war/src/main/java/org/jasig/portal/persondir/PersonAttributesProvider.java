/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portal.persondir;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.IPersonManager;
import org.jasig.portal.url.IPortalRequestUtils;
import org.jasig.services.persondir.IPersonAttributeDao;
import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.NamedPersonImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Provides the person attributes of the requested user, which may be the current user
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
@Service
public class PersonAttributesProvider implements IPersonAttributesProvider {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private IPersonManager personManager;
    private IPortalRequestUtils portalRequestUtils;
    private IPersonAttributeDao personDirectory;

    public IPersonManager getPersonManager() {
        return personManager;
    }
    /**
     * @param personManager the personManager to set
     */
    @Autowired
    public void setPersonManager(IPersonManager personManager) {
        this.personManager = personManager;
    }

    public IPortalRequestUtils getPortalRequestUtils() {
        return portalRequestUtils;
    }
    /**
     * @param portalRequestUtils the portalRequestUtils to set
     */
    @Autowired
    public void setPortalRequestUtils(IPortalRequestUtils portalRequestUtils) {
        this.portalRequestUtils = portalRequestUtils;
    }

    public IPersonAttributeDao getPersonDirectory() {
        return personDirectory;
    }

    @Autowired
    @Qualifier(value = "personAttributeDao")
    public void setPersonDirectory(IPersonAttributeDao personDirectory) {
        this.personDirectory = personDirectory;
    }

    /**
     * Returns the personAttributes of the indicated username, returning the attributes of the current user
     * if the requested usrename matches the current user.  This allows returning all the attributes of the
     * current user, including any attached via request or session-based attributes.
     * @param username username to return the person attributes for
     * @return personAttributes of the current user or requested uesr
     */
    @Override
    public IPersonAttributes getPersonAttributes(String username) {
        final HttpServletRequest portalRequest;
        try {
            portalRequest = this.portalRequestUtils.getCurrentPortalRequest();
        }
        catch (IllegalStateException ise) {
            // Not necessarily an error.  Happens when running ant initdb
            logger.debug("No current portal request available, cannot determine current user name.");
            return null;
        }
        
        final IPerson person = this.personManager.getPerson(portalRequest);
        if (person == null) {
            logger.warn("IPersonManager returned no IPerson for request, cannot determine current user name. " + portalRequest);
            return null;
        }

        if (person.getUserName().equals(username)) {
            return new NamedPersonImpl(username, person.getAttributeMap());
        }
        return personDirectory.getPerson(username);
    }
}
