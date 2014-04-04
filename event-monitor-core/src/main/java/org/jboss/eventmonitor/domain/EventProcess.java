package org.jboss.eventmonitor.domain;

public class EventProcess {
  Event start;
  Event end;
  long duration;
  public EventProcess(Event start, Event end){
    this.start=start;
    this.end=end;
    if (!start.getComponent().equals(end.getComponent())) throw new RuntimeException("invalid event correlation on component name");
    duration=end.getTimestamp()-start.getTimestamp();
  }
  public long getDuration(){
    return duration;
  }
  public String getComponent(){
    return start.getComponent();
  }
  public String getCorrelationId(){
    return start.getCorrelationId();
  }
  public String toString(){
    return "Process(component:"+getComponent()+", correlationId: "+getCorrelationId()+", duration: "+getDuration()+")";
  }
} 
