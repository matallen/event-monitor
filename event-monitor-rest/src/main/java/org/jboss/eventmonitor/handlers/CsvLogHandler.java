package org.jboss.eventmonitor.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class CsvLogHandler implements LogHandler{
  private static final Logger log=Logger.getLogger(CsvLogHandler.class);
  private Map<String, Object> properties=new HashMap<String, Object>();
  private static DecimalFormat toForceFieldOrder=new DecimalFormat("00"); 
  
  public static void main(String[]asd){
    new CsvLogHandler().with("component","test").with("duration",100).debug();
  }
  
  public LogHandler with(String key, Object value){
    properties.put(toForceFieldOrder.format(properties.size()+1)+"_"+key, value);
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
