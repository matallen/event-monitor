package org.jboss.eventmonitor;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(targetNamespace = "http://ws.model.eventmonitor.jboss.org/", name = "EventMonitor")
public interface EventMonitorService {

    @WebMethod//(action = "http://ws.model.eventmonitor.jboss.org/newEvent")
//    public void newEvent(@WebParam String requestId, @WebParam String componentName, @WebParam String eventName);
    public void newEvent(
        @WebParam(mode = WebParam.Mode.INOUT, name = "event", targetNamespace = "")
        Event event
    );
}
