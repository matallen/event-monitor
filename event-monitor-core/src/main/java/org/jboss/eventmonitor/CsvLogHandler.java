package org.jboss.eventmonitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class CsvLogHandler implements LogHandler{
  private static final Logger log=Logger.getLogger(CsvLogHandler.class);
  private Map<String, Object> properties=new HashMap<String, Object>();
  
  public LogHandler with(String key, Object value){
    properties.put(key, value);
    return this;
  }
  
  public void debug(){
//    Collections.sort(list);
    StringBuffer sb=new StringBuffer();
    for(Map.Entry<String, Object> o:properties.entrySet()){
      sb.append(",").append(o.getValue());
    }
    if (sb.length()>0){
      log.debug(sb.toString().substring(1));
    }else
      log.debug("Nothing to log");
  }
  public void info(){
    for(Map.Entry<String, Object> o:properties.entrySet())
      log.info(o.getValue());
  }
  public void warn(){
    for(Map.Entry<String, Object> o:properties.entrySet())
      log.warn(o.getValue());
  }
  public void error(){
    for(Map.Entry<String, Object> o:properties.entrySet())
      log.error(o.getValue());
  }
  
  public void log(long duration, String route) throws IOException {
    File parent;
    if (new File("target").exists()) {
      parent = new File("target");
    } else
      parent = new File("/tmp");
    File logfile = new File(parent, "event-monitor.log");
    FileOutputStream out = new FileOutputStream(logfile);
    
    // argh! this code is truncating the file contents
    if (!logfile.exists()) {
      IOUtils.write("Time(ms), Duration, Route Name", out);
    }
    IOUtils.write(System.currentTimeMillis()+","+duration+","+route+"", out);
  }
}
