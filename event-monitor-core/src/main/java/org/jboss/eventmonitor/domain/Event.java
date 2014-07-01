package org.jboss.eventmonitor.domain;

public class Event{
  private String component;
  private String eventName;
  private String correlationId;
  private long timestamp;
  
  public String getCorrelationId() { return correlationId; }
  public String getComponent()     { return component;     }
  public String getEventName()     { return eventName;     }
  public long getTimestamp()       { return timestamp;     }
  public Event(){/*for json/jackson only*/}
  public Event(String component, String correlationId, String eventName, long timestamp) {
    super();
    this.component = component;
    this.correlationId=correlationId;
    this.eventName = eventName;
    this.timestamp=timestamp;
//    timestamp=System.currentTimeMillis();
  }
  public String toString(){
    return "Event(component:"+getComponent()+", correlationId: "+getCorrelationId()+", eventName: "+eventName+", timestamp: "+timestamp+")";
  }
}
