package org.jboss.eventmonitor;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.osgi.CamelContextFactory;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestTest extends CamelSpringTestSupport{
  
  @Override
  protected AbstractXmlApplicationContext createApplicationContext() {
      return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
  }
  
  @Test
		public void test() throws Exception{
    
//    Client client = Client.create();
//    WebResource webResource = client.resource("http://localhost:9002/rest/account/3");
//    ClientResponse response = webResource.get(ClientResponse.class);
    
//      String input = IOUtils.toString(this.getClass().getResourceAsStream("/newEvent/in.json"));
      String start="{\"requestId\": \"1\",\"component\":\"X\",\"eventName\":\"start\"}";
      String end="{\"requestId\": \"1\",\"component\":\"X\",\"eventName\":\"end\"}";
      
      getMockEndpoint("mock:result").expectedMessageCount(2);
      context.getRouteDefinition("event-monitor-service").adviceWith(context, new AdviceWithRouteBuilder() {
          @Override
          public void configure() throws Exception {
              weaveAddLast().to("mock:result");
          }
      });
      
      template.sendBody("direct:rest", start);
      Thread.sleep(1100);
      template.sendBody("direct:rest", end);

      assertMockEndpointsSatisfied();
      
      Object output=getMockEndpoint("mock:result").getExchanges().get(0).getIn().getBody();
      System.out.println("OUTPUT = "+output);
    }
    
    
//    @Override
//    protected Properties useOverridePropertiesWithPropertiesComponent() {
//      Properties result=super.useOverridePropertiesWithPropertiesComponent();
//      result.put("", "");
//      return result;
//    }
    
    
}
