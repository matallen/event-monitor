package org.jboss.eventmonitor;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RouteTest extends CamelSpringTestSupport {

  @Override
  protected AbstractXmlApplicationContext createApplicationContext() {
    return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
  }

  @Test
  public void test() throws Exception {
    
    getMockEndpoint("mock:result").expectedMessageCount(2);
    context.getRouteDefinition("event-monitor-service").adviceWith(context, new AdviceWithRouteBuilder() {
        @Override
        public void configure() throws Exception {
            weaveAddLast().to("mock:result");
        }
    });
    
    template.sendBody("direct:in", new Event("component", "1", "start"));
    Thread.sleep(1100);
    template.sendBody("direct:in", new Event("component", "1", "end"));

    assertMockEndpointsSatisfied();

    System.out.println(
    		(Event) getMockEndpoint("mock:result").getExchanges().get(0).getIn().getBody()
    		);
    System.out.println(
    		(Event) getMockEndpoint("mock:result").getExchanges().get(1).getIn().getBody()
    		);
    
  }

  // @Override
  // protected Properties useOverridePropertiesWithPropertiesComponent() {
  // Properties result=super.useOverridePropertiesWithPropertiesComponent();
  // result.put("", "");
  // return result;
  // }

}
