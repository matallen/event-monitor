package org.jboss.eventmonitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

@Path("/events")
public class RestController {
	private static DroolsEventProcessing eventProcessor=null;
	
	private DroolsEventProcessing getEventProcessor(boolean startIfNotInit){
		if (null==eventProcessor){
			eventProcessor=new DroolsEventProcessing();
			if (startIfNotInit) eventProcessor.start();
		}
		return eventProcessor;
	}
	
  @GET
  @Path("/new/{correlationId}/{component}/{event}")
  public Response newEvent(@PathParam("component") String component, @PathParam("correlationId") String correlationId, @PathParam("event") String eventName) {
  	Event event=new Event(component, correlationId, eventName);
  	getEventProcessor(true).insertEvent(event);
  	System.out.println("RestController.newEvent [component='"+component+"', event='"+eventName+"']");
  	return Response.status(200).entity("newEvent [component='"+component+"', event='"+eventName+"'] ").build();
  }
  
  @GET
  @Path("/start")
  public Response start(){
  	getEventProcessor(false).start();
  	return Response.status(200).entity("OK").build();
  }
  
  @GET
  @Path("/stop")
  public Response stop(){
  	getEventProcessor(false).stop();
  	return Response.status(200).entity("OK").build();
  }
}
