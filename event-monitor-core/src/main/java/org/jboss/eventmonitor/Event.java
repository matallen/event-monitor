package org.jboss.eventmonitor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang.builder.ToStringBuilder.reflectionToString;

//@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(name = "event", propOrder = { "requestId", "component", "eventName", "timestamp" })
public class Event {
	
	public String toString(){
		return reflectionToString(this);
	}
	@Override
	public boolean equals(Object obj) {
		return reflectionEquals(this, obj);
	}

//  @XmlElement(required = true)
  protected String requestId;
//  @XmlElement(required = true)
  protected String component;
//  @XmlElement(required = true)
  protected String eventName;
  private long timestamp;

  public String getComponent() {
    return component;
  }

  public String getEventName() {
    return eventName;
  }

  public String getRequestId() {
    return requestId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public Event() {
	    this.timestamp = System.currentTimeMillis();
  }

  public Event(String component, String requestId, String eventName) {
    super();
    this.component = component;
    this.requestId = requestId;
    this.eventName = eventName;
    this.timestamp = System.currentTimeMillis();
  }

  public void setEventName(String value) {
    this.eventName = value;
  }

  public void setComponent(String value) {
    this.component = value;
  }

  public void setRequestId(String value) {
	    this.requestId = value;
	  }
  public void setTimestamp(long value) {
	    this.timestamp = value;
	  }

}
