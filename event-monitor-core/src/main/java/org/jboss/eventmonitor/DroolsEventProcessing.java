package org.jboss.eventmonitor;

import java.io.IOException;

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
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.jboss.eventmonitor.domain.Event;
import org.jboss.eventmonitor.domain.EventProcess;
import org.jboss.eventmonitor.stats.Statsd;

public class DroolsEventProcessing {
    private static final Logger log=Logger.getLogger(DroolsEventProcessing.class);
    private WorkingMemoryEntryPoint stream;
    private StatefulKnowledgeSession session;
    private boolean isStarted=false;

    public DroolsEventProcessing() {
        start();
    }

    public void stop() {
        log.info("Stopping drools event processor");
        thread.stop();
        thread=null;
        session.halt();
        // session.removeEventListener((WorkingMemoryEventListener) el);
        // session.removeEventListener((AgendaEventListener) l);
        session.dispose();
        isStarted=false;
    }

    private static Thread thread;

    public void start() {
        if (isStarted) {
            System.out.println("Already started - please check code.");
            return;
        }
        log.info("Starting drools event processor");
        KnowledgeSessionConfiguration conf=KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption(ClockTypeOption.get("realtime"));
        session=getKnowledgeBase().newStatefulKnowledgeSession(conf,null);
        
        stream=session.getWorkingMemoryEntryPoint("EventStream");
        thread=new Thread("RULE FIRED THREAD") {
            @Override
            public void run() {
                session.fireUntilHalt();
            }
        };
        thread.start();
        isStarted=true;
    }

    public void insertEvent(Event event) {
        if (!isStarted)
            start();
        Statsd.increment("eventmonitor.events.count");
        stream.insert(event);
    }

    public boolean isStarted() {
        return isStarted;
    }

    private KnowledgeBase getKnowledgeBase() {
        try {
            KnowledgeBaseConfiguration configuration=KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            configuration.setOption(EventProcessingOption.STREAM);
            KnowledgeBase kBase=KnowledgeBaseFactory.newKnowledgeBase(configuration);

            Configuration config=new Configuration();
            KnowledgeBuilder builder=KnowledgeBuilderFactory.newKnowledgeBuilder();
            builder.add(org.drools.io.ResourceFactory.newClassPathResource("rules/eventmonitorcore/1.0/core.drl"),ResourceType.DRL);

            if (config.isDevMode()) {
                System.out.println("DEV MODE");
                builder.add(org.drools.io.ResourceFactory.newClassPathResource("rules/eventmonitor/1.0/events.drl"),ResourceType.DRL);
            } else {
                RulePackageDownloader downloader=new RulePackageDownloader(config.getBaseServerUrl(), config.getUsername(), config.getPassword());
                try {
                    kBase.addKnowledgePackages(downloader.download(config.getPackageName(),config.getPackageVersion()));
                } catch (java.net.ConnectException e) {
                    throw new RuntimeException("Unable to connect to Guvnor on " + config.getBaseServerUrl() + config.getPackageName() + "/" + config.getPackageVersion());
                }
            }

            if (builder.hasErrors())
                throw new RuntimeException(builder.getErrors().toString());
            kBase.addKnowledgePackages(builder.getKnowledgePackages());

            System.out.println("KnowledgePackage.RuleName's found:");
            for (KnowledgePackage kp : kBase.getKnowledgePackages())
                for (Rule r : kp.getRules())
                    System.out.println(String.format("[%-40s][%s]",kp.getName(),r.getName()));

            return kBase;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void clean() {
        log.info("Cleaning " + stream.getFactCount() + " facts from stream");

        for (FactHandle fh : stream.getFactHandles()) {
            log.debug("  - " + stream.getObject(fh));
            stream.retract(fh);
        }

        System.out.println("Cleaning " + session.getFactCount() + " facts from session");
        for (FactHandle fh : session.getFactHandles()) {
            log.debug("  - " + session.getObject(fh));
            session.retract(fh);
        }
    }

    public WorkingMemoryState getWorkingMemoryState() throws IOException {
        WorkingMemoryState result=new WorkingMemoryState();
        for (FactHandle fh : stream.getFactHandles()) {
            Object o=stream.getObject(fh);
            if (Event.class.isAssignableFrom(o.getClass()))
                result.getInStream().getEvents().add((Event) o);
            if (EventProcess.class.isAssignableFrom(o.getClass()))
                result.getInStream().getProcesses().add((EventProcess) o);
        }
        for (FactHandle fh : session.getFactHandles()) {
            Object o=session.getObject(fh);
            if (Event.class.isAssignableFrom(o.getClass()))
                result.getInMemory().getEvents().add((Event) o);
            if (EventProcess.class.isAssignableFrom(o.getClass()))
                result.getInMemory().getProcesses().add((EventProcess) o);
        }
        return result;
    }

}
