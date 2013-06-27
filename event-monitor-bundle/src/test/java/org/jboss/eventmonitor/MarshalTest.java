package org.jboss.eventmonitor;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class MarshalTest extends CamelTestSupport {

  @Test
  public void test() throws Exception {
    Event e = new Event();
    String input = IOUtils.toString(this.getClass().getResourceAsStream("/newEvent/in.json"));
    getMockEndpoint("mock:result").expectedBodiesReceived(e);
    template.sendBody("direct:start", input);
    // resultEndpoint.assertIsSatisfied();
    System.out.println("RESULT=" + getMockEndpoint("mock:result").getExchanges().get(0).getIn().getBody());
  }

  @Override
  public RouteBuilder createRouteBuilder() {

//    final JsonDataFormat df = new JsonDataFormat(JsonLibrary.Gson);
    return new RouteBuilder() {
      public void configure() {
        from("direct:start")
        .unmarshal().json(JsonLibrary.Jackson, org.jboss.eventmonitor.model.json.Event.class)
        .bean(new BeanCopy(), "process")
        .to("mock:result");
      }
    };

  }
}
