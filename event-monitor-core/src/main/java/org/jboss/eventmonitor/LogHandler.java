package org.jboss.eventmonitor;


public interface LogHandler {
  
  public LogHandler with(String name, Object value);
  public void debug();
  public void info();
  public void warn();
  public void error();
}
