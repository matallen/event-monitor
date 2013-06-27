package org.jboss.eventmonitor;

import javax.xml.ws.Holder;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.jboss.eventmonitor.model.ws.EventMonitor;
import org.jboss.eventmonitor.model.ws.NewEventResponse;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class SoapTest 
  extends CamelSpringTestSupport {

  @Override
  protected AbstractXmlApplicationContext createApplicationContext() {
    return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
  }
  
  private <T> T createService(String address, Class<T> clazz){
    JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
    factory.setServiceClass(clazz);
    factory.setAddress(address);
    return (T) factory.create();
  }
  
  @Test
  public void test() throws Exception {
//    String input = IOUtils.toString(this.getClass().getResourceAsStream("/newEvent/in.xml"));

    getMockEndpoint("mock:result").expectedMessageCount(2);
    context.getRouteDefinition("event-monitor-service").adviceWith(context, new AdviceWithRouteBuilder() {
      @Override
      public void configure() throws Exception {
          weaveAddLast().to("mock:result");
      }
  });

    
    org.jboss.eventmonitor.model.ws.Event e=new org.jboss.eventmonitor.model.ws.Event();
    e.setRequestId("1");
    e.setComponentName("component name");
    e.setEventName("start");
    EventMonitor service=createService("http://localhost:9090/EventMonitorPort", EventMonitor.class);
//    service.newEvent(new Holder<org.jboss.eventmonitor.model.ws.Event>(e));
    service.newEvent(e);
    
    Thread.sleep(1100l);
    e.setEventName("end");
//    service.newEvent(new Holder<org.jboss.eventmonitor.model.ws.Event>(e));
    service.newEvent(e);
    
    
    // working
//    Client client = Client.create();
//    WebResource webResource = client.resource("http://localhost:9090/EventMonitorPort");
//    webResource.post(input);
    
//    ClientResponse response = webResource.get(ClientResponse.class);

    
//    EventMonitorService service=createService("http://localhost:9090/eventmonitor/WebService", EventMonitorService.class);
//    org.jboss.eventmonitor.model.ws.Event e=new org.jboss.eventmonitor.model.ws.Event();
//    service.newEvent(new Holder<org.jboss.eventmonitor.model.ws.Event>(e));

//    org.jboss.eventmonitor.EventMonitorService service=createService("http://localhost:9090/eventmonitor/WebService", org.jboss.eventmonitor.EventMonitorService.class);
//    service.newEvent(new Event());
    

//    template.sendBody("direct:soap", input);

    assertMockEndpointsSatisfied();
    
    NewEventResponse output=(NewEventResponse)getMockEndpoint("mock:result").getExchanges().get(0).getIn().getBody();
    System.out.println("OUTPUT = "+output);

  }
}
