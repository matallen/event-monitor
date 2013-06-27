package org.jboss.eventmonitor;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class DroolsEventProcessing {
  private static final Logger log=Logger.getLogger(DroolsEventProcessing.class);
  public static boolean started=false;
	private WorkingMemoryEntryPoint stream;
	private StatefulKnowledgeSession session;
	
	public DroolsEventProcessing(){
    KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
    conf.setOption(ClockTypeOption.get("realtime"));
    session=getKnowledgeBase().newStatefulKnowledgeSession(conf, null);
    stream=session.getWorkingMemoryEntryPoint("EventStream");
	}
	
  public void stop(){
		if (started){
      log.info("Stopping drools event processor");
      session.halt();
      session.dispose();
      started=false;
		}
  }

	public void start() {
	  if (!started){
//	    log.info("Starting drools event processor");
	    System.out.println("Starting drools event processor");
  		new Thread("RULES EVENT THREAD") {
  			@Override
  			public void run() {
  				session.fireUntilHalt();
  			}
  		}.start();
  		started=true;
	  }
	}

	public void insertEvent(Event event){
	  if (started){
	    System.out.println("EventProcessor.insertEvent called with event ["+event+"]");
	    stream.insert(event);
	  }else
	    System.err.println("EventProcessor.insertEvent called but processor not started");
	}
	
  private KnowledgeBase getKnowledgeBase(){
    try {
      KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//      InputStream is=DroolsEventProcessing.class.getClassLoader().getResourceAsStream("events.drl");
//      builder.add(org.drools.io.ResourceFactory.newByteArrayResource(IOUtils.toByteArray(is)), ResourceType.DRL);
      
//      builder.add(org.drools.io.ResourceFactory.newClassPathResource("WEB-INF/events.drl"), ResourceType.DRL);
//      System.out.println("XXX = "+new File("WEB-INF/events.drl").getAbsolutePath());
      
//      builder.add(org.drools.io.ResourceFactory.newFileResource("src/main/webapp/WEB-INF/events.drl"), ResourceType.DRL);
      builder.add(org.drools.io.ResourceFactory.newClassPathResource("events.drl"), ResourceType.DRL);
      
      if (builder.hasErrors())
        throw new RuntimeException(builder.getErrors().toString());
      
      KnowledgeBaseConfiguration configuration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
      configuration.setOption(EventProcessingOption.STREAM);
      KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(configuration);
      
      
      for(KnowledgePackage kp:kbase.getKnowledgePackages()){
        System.out.println(" EventProcessor.getKnowledgeBase\n  Package="+kp.getName());
        for(Rule r:kp.getRules()){
          System.out.println("  - "+r.getName());
        }
      }
      
      kbase.addKnowledgePackages(builder.getKnowledgePackages());
      return kbase;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }


}
