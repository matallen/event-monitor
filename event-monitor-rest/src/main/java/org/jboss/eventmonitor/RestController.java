package org.jboss.eventmonitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.eventmonitor.domain.Event;
import org.jboss.eventmonitor.WorkingMemoryState;

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
  @Path("/new/{component}/{correlationId}/{event}")
  public Response newEvent(@PathParam("component") String component, @PathParam("correlationId") String correlationId, @PathParam("event") String eventName, @Context HttpServletRequest request) {
  	Event event=new Event(component, correlationId, eventName, request.getParameter("timestamp")!=null?Long.parseLong(request.getParameter("timestamp")):System.currentTimeMillis());
  	getEventProcessor(true).insertEvent(event);
  	System.out.println("newEvent [component='"+component+"', correlationId='"+correlationId+"', event='"+eventName+"', ts='"+event.getTimestamp()+"']");
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
