package org.jboss.eventmonitor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.eventmonitor.domain.Event;
import org.jboss.eventmonitor.stats.Statsd;

@Path("/events")
public class RestController /*extends Application*/{
	public static DroolsEventProcessing eventProcessor=null;
	private static CamelContext camelContext=null;
	
	static{
	    init();
	}
	
	public static void init(){
        if (null==eventProcessor){
            initConnectors();
            eventProcessor=new DroolsEventProcessing();
        }
	}
	private static void initConnectors(){
        camelContext=new DefaultCamelContext();
        camelContext.getProperties().put(Exchange.MAXIMUM_CACHE_POOL_SIZE, "1");
        CamelHelper.producer=camelContext.createProducerTemplate();
        CamelHelper.producer.setMaximumCacheSize(1);
        
        try {
        	camelContext.addRoutes(new RouteBuilder() {@Override public void configure() throws Exception {
        		from("timer://health?fixedRate=true&period=10000")// 10 seconds
        		.id("health")
        		.process(new Processor() { @Override public void process(Exchange exchange) throws Exception {
//        			System.out.println("health");
        			Statsd.gauge("eventmonitor.jvm.freeMemoryMB", Runtime.getRuntime().freeMemory()/(1024*1024));
        			Statsd.gauge("eventmonitor.jvm.totalMemoryMB", Runtime.getRuntime().totalMemory()/(1024*1024));
        			Statsd.gauge("eventmonitor.jvm.maxMemoryMB", Runtime.getRuntime().maxMemory()/(1024*1024));
        			Statsd.gauge("eventmonitor.jvm.availableProcessors", Runtime.getRuntime().availableProcessors());
				}})
        		.to("log:sink");
        	}});
        	
          camelContext.addRoutes(new RouteBuilder() {@Override public void configure() throws Exception {
            
            // TODO: create N routes based on config
            from("direct:alert")
            .process(new Processor() {
              @Override public void process(Exchange exchange) throws Exception {
                System.out.println("CAMEL->"+exchange.getIn().getBody());
            }})
            .to("log:Alert"); // TODO: change this to a configurable output
            
          }});
          
            // TODO: need to fire the route rules here to create the RouteBuilders
            
          camelContext.start();
        } catch (Exception e) {
          e.printStackTrace();
        }
	}
	
	private DroolsEventProcessing getEventProcessor(boolean startIfNotInit){
		return eventProcessor;
	}
	
  @GET
  @Path("/new/{component}/{correlationId}/{event}")
  public Response newEvent(@PathParam("component") String component, @PathParam("correlationId") String correlationId, @PathParam("event") String eventName, @Context HttpServletRequest request) {
  	Event event=new Event(component, correlationId, eventName, request.getParameter("timestamp")!=null?Long.parseLong(request.getParameter("timestamp")):System.currentTimeMillis());
  	getEventProcessor(true).insertEvent(event);
  	return Response.status(200).entity("newEvent [component='"+component+"', correlationId='"+correlationId+"', event='"+eventName+"'] ").build();
  }
  
  @GET
  @Path("/start")
  public Response start(){
  	getEventProcessor(false).start();
  	return Response.status(200).entity("Started").build();
  }
  
  @GET
  @Path("/stop")
  public Response stop(){
  	getEventProcessor(false).stop();
  	return Response.status(200).entity("Stopped").build();
  }
  
  @GET
  @Path("/clean")
  public Response clear(){
    getEventProcessor(false).clean();
    return Response.status(200).entity("Cleaned").build();
  }
  @GET
  @Path("/print")
  public Response print() throws JsonGenerationException, JsonMappingException, IOException{
    WorkingMemoryState result=getEventProcessor(false).getWorkingMemoryState();
    String json=new ObjectMapper().writeValueAsString(result);
    return Response.status(200).entity(json).build();
  }
}
