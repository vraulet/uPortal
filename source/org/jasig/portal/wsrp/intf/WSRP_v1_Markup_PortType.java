/**
 * WSRP_v1_Markup_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.jasig.portal.wsrp.intf;

/**
 * @deprecated As of uPortal 2.4, the WSRP producer in uPortal is no longer being maintained. If reintroduced, it will migrate to one based on WSRP4J.
 */
public interface WSRP_v1_Markup_PortType extends java.rmi.Remote {
    public void getMarkup(org.jasig.portal.wsrp.types.RegistrationContext registrationContext, org.jasig.portal.wsrp.types.PortletContext portletContext, org.jasig.portal.wsrp.types.RuntimeContext runtimeContext, org.jasig.portal.wsrp.types.UserContext userContext, org.jasig.portal.wsrp.types.MarkupParams markupParams, org.jasig.portal.wsrp.types.holders.MarkupContextHolder markupContext, org.jasig.portal.wsrp.types.holders.SessionContextHolder sessionContext, org.jasig.portal.wsrp.types.holders.ExtensionArrayHolder extensions) throws java.rmi.RemoteException, org.jasig.portal.wsrp.types.InconsistentParametersFault, org.jasig.portal.wsrp.types.InvalidRegistrationFault, org.jasig.portal.wsrp.types.MissingParametersFault, org.jasig.portal.wsrp.types.OperationFailedFault, org.jasig.portal.wsrp.types.UnsupportedMimeTypeFault, org.jasig.portal.wsrp.types.UnsupportedModeFault, org.jasig.portal.wsrp.types.UnsupportedLocaleFault, org.jasig.portal.wsrp.types.InvalidUserCategoryFault, org.jasig.portal.wsrp.types.InvalidSessionFault, org.jasig.portal.wsrp.types.InvalidCookieFault, org.jasig.portal.wsrp.types.AccessDeniedFault, org.jasig.portal.wsrp.types.InvalidHandleFault, org.jasig.portal.wsrp.types.UnsupportedWindowStateFault;
    public void performBlockingInteraction(org.jasig.portal.wsrp.types.RegistrationContext registrationContext, org.jasig.portal.wsrp.types.PortletContext portletContext, org.jasig.portal.wsrp.types.RuntimeContext runtimeContext, org.jasig.portal.wsrp.types.UserContext userContext, org.jasig.portal.wsrp.types.MarkupParams markupParams, org.jasig.portal.wsrp.types.InteractionParams interactionParams, org.jasig.portal.wsrp.types.holders.UpdateResponseHolder updateResponse, javax.xml.rpc.holders.StringHolder redirectURL, org.jasig.portal.wsrp.types.holders.ExtensionArrayHolder extensions) throws java.rmi.RemoteException, org.jasig.portal.wsrp.types.InconsistentParametersFault, org.jasig.portal.wsrp.types.InvalidRegistrationFault, org.jasig.portal.wsrp.types.MissingParametersFault, org.jasig.portal.wsrp.types.OperationFailedFault, org.jasig.portal.wsrp.types.UnsupportedMimeTypeFault, org.jasig.portal.wsrp.types.UnsupportedModeFault, org.jasig.portal.wsrp.types.UnsupportedLocaleFault, org.jasig.portal.wsrp.types.InvalidUserCategoryFault, org.jasig.portal.wsrp.types.InvalidSessionFault, org.jasig.portal.wsrp.types.InvalidCookieFault, org.jasig.portal.wsrp.types.PortletStateChangeRequiredFault, org.jasig.portal.wsrp.types.AccessDeniedFault, org.jasig.portal.wsrp.types.InvalidHandleFault, org.jasig.portal.wsrp.types.UnsupportedWindowStateFault;
    public org.jasig.portal.wsrp.types.Extension[] releaseSessions(org.jasig.portal.wsrp.types.RegistrationContext registrationContext, java.lang.String[] sessionIDs) throws java.rmi.RemoteException, org.jasig.portal.wsrp.types.InvalidRegistrationFault, org.jasig.portal.wsrp.types.OperationFailedFault, org.jasig.portal.wsrp.types.MissingParametersFault, org.jasig.portal.wsrp.types.AccessDeniedFault;
    public org.jasig.portal.wsrp.types.Extension[] initCookie(org.jasig.portal.wsrp.types.RegistrationContext registrationContext) throws java.rmi.RemoteException, org.jasig.portal.wsrp.types.InvalidRegistrationFault, org.jasig.portal.wsrp.types.OperationFailedFault, org.jasig.portal.wsrp.types.AccessDeniedFault;
}
