package org.jboss.eventmonitor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class MyProcessor implements Processor{
  static DroolsEventProcessing service=null;
  @Override
  public void process(Exchange exchange) throws Exception {
    Event body=(Event)exchange.getIn().getBody();
    if (null==service){
    	service=new DroolsEventProcessing();
    	service.start();
    }
    service.insertEvent(body);
    exchange.getIn().setBody(new org.jboss.eventmonitor.model.ws.NewEventResponse());
  }

}
