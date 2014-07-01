package org.jboss.eventmonitor;

import static com.jayway.restassured.RestAssured.given;
import java.io.IOException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import net.java.quickcheck.generator.PrimitiveGenerators;
import com.jayway.restassured.response.Response;

public class EventIntegrationTest {
  private static final String url="http://localhost:8081/event-monitor/rest/events/new/%s/%s/%s";
  private static final String stopStartUrl="http://localhost:8081/event-monitor/rest/events/%s";
  private static final String print="http://localhost:8081/event-monitor/rest/events/print";
  enum Event{start,end};
  enum State{stop,start,clean};
  private void send(String component, String correlationId, Event event, long timestamp){
    Response response = given().redirects().follow(true).when().get(String.format(url+"?timestamp="+timestamp,component,correlationId,event.name()));
    if (response.getStatusCode()!=200) throw new RuntimeException("Response Code was: "+response.getStatusCode());
  }
  private void send(String component, String correlationId, Event event){
    Response response = given().redirects().follow(true).when().get(String.format(url,component,correlationId,event.name()));
    if (response.getStatusCode()!=200) throw new RuntimeException("Response Code was: "+response.getStatusCode());
  }
  private void send(State state){
    Response response = given().redirects().follow(true).when().get(String.format(stopStartUrl,state.name()));
    if (response.getStatusCode()!=200) throw new RuntimeException("Response Code was: "+response.getStatusCode());
  }
  private WorkingMemoryState print() throws JsonParseException, JsonMappingException, IOException{
    Response response = given().redirects().follow(true).when().get(String.format(print));
    if (response.getStatusCode()!=200) throw new RuntimeException("Response Code was: "+response.getStatusCode());
    String responseString=response.asString();
    return new ObjectMapper().readValue(responseString,WorkingMemoryState.class);
  }
  
  
  private String newCorrelationId(){
    return PrimitiveGenerators.letterStrings(5,5).next().toLowerCase();
  }
  
  @Before
//  @After
  public void clean(){
    send(State.clean);
  }
  
  @org.junit.Test
  public void testThatLeavesWMStateOK() throws Exception{
    String cId=newCorrelationId();
    send("comp1",cId,Event.start);
    Thread.sleep(500);
    send("comp1",cId,Event.end);
    
    cId=newCorrelationId();
    send("comp1",cId,Event.start);
    Thread.sleep(4000);
    send("comp1",cId,Event.end);
    
    WorkingMemoryState state=print();
    Assert.assertEquals(0, state.getInStream().getEvents().size());
    Assert.assertEquals(0, state.getInStream().getProcesses().size());
    Assert.assertEquals(0, state.getInMemory().getEvents().size());
    Assert.assertEquals(0, state.getInMemory().getProcesses().size());
  }    
  
  @org.junit.Test
  public void testOddStartEndSequence() throws Exception{
    String cId=newCorrelationId();
    String cId2=newCorrelationId();
    
    send("comp2",cId,Event.end);
    Thread.sleep(500);
    send("comp2",cId2,Event.start);
    send("comp2",cId,Event.start);
    send("comp2",cId2,Event.end);
    
    // assert that there are some objects remaining in WM
    
    WorkingMemoryState state=print();
    Assert.assertEquals(2, state.getInStream().getEvents().size());
    Assert.assertEquals(0, state.getInStream().getProcesses().size());
    Assert.assertEquals(0, state.getInMemory().getEvents().size());
    Assert.assertEquals(0, state.getInMemory().getProcesses().size());
  }


  @org.junit.Test
  public void testOutOfSequenceStartStopEvents() throws Exception{
    String cId=newCorrelationId();
    send("comp3",cId,Event.end, 10000);
    send("comp3",cId,Event.start, 2000);
    
    // assert that there are no objects remaining in WM
    
    WorkingMemoryState state=print();
    Assert.assertEquals(0, state.getInStream().getEvents().size());
    Assert.assertEquals(0, state.getInStream().getProcesses().size());
    Assert.assertEquals(0, state.getInMemory().getEvents().size());
    Assert.assertEquals(0, state.getInMemory().getProcesses().size());
  }
}
