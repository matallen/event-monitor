package org.jboss.eventmonitor.test;

import org.drools.definition.type.FactType;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.runtime.StatefulKnowledgeSession;

public class Test{
  
  public static void main(String[] arg){
    new Test().run();
  }
  public void run(){
    KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    
    builder.add(org.drools.io.ResourceFactory.newFileResource("src/main/resources/test.drl"), ResourceType.DRL);
    
    if (builder.hasErrors())
      throw new RuntimeException(builder.getErrors().toString());
    KnowledgeBaseConfiguration configuration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
    configuration.setOption(EventProcessingOption.STREAM);
    
    KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(configuration);
    kbase.addKnowledgePackages(builder.getKnowledgePackages());
    
    StatefulKnowledgeSession session=kbase.newStatefulKnowledgeSession();
    KnowledgeRuntimeLogger logger=org.drools.logger.KnowledgeRuntimeLoggerFactory.newFileLogger(session, "/tmp/drools.log");
//    KnowledgeRuntimeLogger logger=org.drools.logger.KnowledgeRuntimeLoggerFactory.newConsoleLogger(session);//, "/tmp/drools.log");
    
    try{
      FactType factType = kbase.getFactType("testpackage", "MyFact");
      Object fact = factType.newInstance();
      factType.set(fact, "name", "fred");
      factType.set(fact, "amount", 25000);
      factType.set(fact, "approved", Boolean.TRUE);
      
      session.insert(new String("test"));
      
      session.insert(fact);
      
      session.fireAllRules();
    }catch(Exception e){
      e.printStackTrace();
    }
    
  }
}