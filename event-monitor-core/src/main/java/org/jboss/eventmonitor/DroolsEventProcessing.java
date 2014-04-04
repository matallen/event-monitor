package org.jboss.eventmonitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.jboss.eventmonitor.domain.Event;
import org.jboss.eventmonitor.domain.EventProcess;

public class DroolsEventProcessing {
	private WorkingMemoryEntryPoint stream;
	private StatefulKnowledgeSession session;
//	private List<Callback> callbacks;
	
//	public void notifyCallbacks(String description){
//		if (null!=callbacks){
//			for(Callback cb:callbacks)
//				cb.onCallback(description);
//		}
//	}
	
//	public DroolsEventProcessing(List<Callback> callbacks){
//    KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
//    conf.setOption(ClockTypeOption.get("realtime"));
//    session=getKnowledgeBase().newStatefulKnowledgeSession(conf, null);
////    session.setGlobal("eventProcessing", this);
//    stream=session.getWorkingMemoryEntryPoint("EventStream");
//    this.callbacks=callbacks;
//	}
	public DroolsEventProcessing(){
//    KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
//    conf.setOption(ClockTypeOption.get("realtime"));
//    session=getKnowledgeBase().newStatefulKnowledgeSession(conf, null);
//    stream=session.getWorkingMemoryEntryPoint("EventStream");
	  start();
	}
	
  public void stop(){
		System.out.println("Stopping drools event processor");
    thread.stop();
    thread=null;
		session.halt();
//    session.removeEventListener((WorkingMemoryEventListener) l);
//    session.removeEventListener((AgendaEventListener) l);
    session.dispose();
  }

  private static Thread thread;
	public void start() {
		System.out.println("Starting drools event processor");
		KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
    conf.setOption(ClockTypeOption.get("realtime"));
    session=getKnowledgeBase().newStatefulKnowledgeSession(conf, null);
    stream=session.getWorkingMemoryEntryPoint("EventStream");
    thread=new Thread("RULE FIRED THREAD") {
      @Override public void run() {
				session.fireUntilHalt();
		}};
    thread.start();
	}

	public void insertEvent(Event event){
		stream.insert(event);
	}
	
  private KnowledgeBase getKnowledgeBase(){
    try {
      KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//      InputStream is=DroolsEventProcessing.class.getClassLoader().getResourceAsStream("events.drl");
//      builder.add(org.drools.io.ResourceFactory.newByteArrayResource(IOUtils.toByteArray(is)), ResourceType.DRL);
      
//      builder.add(org.drools.io.ResourceFactory.newClassPathResource("WEB-INF/events.drl"), ResourceType.DRL);
//      System.out.println("XXX = "+new File("WEB-INF/events.drl").getAbsolutePath());
      
      builder.add(org.drools.io.ResourceFactory.newFileResource("src/main/webapp/WEB-INF/events.drl"), ResourceType.DRL);
      
      if (builder.hasErrors())
        throw new RuntimeException(builder.getErrors().toString());
      KnowledgeBaseConfiguration configuration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
      configuration.setOption(EventProcessingOption.STREAM);
      KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(configuration);
      kbase.addKnowledgePackages(builder.getKnowledgePackages());
      return kbase;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public void clean() {
    System.out.println("Cleaning "+stream.getFactCount()+" facts from stream");
    
    for(FactHandle fh:stream.getFactHandles()){
      System.out.println("  - "+stream.getObject(fh));
      stream.retract(fh);
    }
    
    System.out.println("Cleaning "+session.getFactCount()+" facts from session");
    for(FactHandle fh:session.getFactHandles()){
      System.out.println("  - "+session.getObject(fh));
      session.retract(fh);
    }
  }

  public WorkingMemoryState getWorkingMemoryState() throws IOException {
    WorkingMemoryState result=new WorkingMemoryState();
    for(FactHandle fh:stream.getFactHandles()){
      Object o=stream.getObject(fh);
      if (Event.class.isAssignableFrom(o.getClass()))
        result.getInStream().getEvents().add((Event)o);
      if (EventProcess.class.isAssignableFrom(o.getClass()))
        result.getInStream().getProcesses().add((EventProcess)o);
    }
    for(FactHandle fh:session.getFactHandles()){
      Object o=session.getObject(fh);
      if (Event.class.isAssignableFrom(o.getClass()))
        result.getInMemory().getEvents().add((Event)o);
      if (EventProcess.class.isAssignableFrom(o.getClass()))
        result.getInMemory().getProcesses().add((EventProcess)o);
    }
    return result;
  }


}
