package org.jboss.eventmonitor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.BeanUtils;

public class BeanCopy implements Processor {

  @Override
  public void process(Exchange exchange) throws Exception {
    Event event=new Event();
    System.out.println("############ TYPE = "+ exchange.getIn().getBody().getClass().getSimpleName());
    
    // body should be either model.ws.Event or model.json.Event
    if ( exchange.getIn().getBody() instanceof org.jboss.eventmonitor.model.ws.Event ||
    		exchange.getIn().getBody() instanceof org.jboss.eventmonitor.model.json.Event
    		){
	    BeanUtils.copyProperties(exchange.getIn().getBody(), event);
	    exchange.getIn().setBody(event);
  }else
	  throw new RuntimeException("Only works on a 'org.jboss.eventmonitor.model.ws.Event' body");
  }

}
