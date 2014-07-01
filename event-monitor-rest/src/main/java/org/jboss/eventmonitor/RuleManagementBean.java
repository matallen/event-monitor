package org.jboss.eventmonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;

public class RuleManagementBean {
    private static Configuration config=new Configuration();
    
    public String readRules() {
        try {
            if (!new File(config.getRuleRepository(),"events.drl").exists()){
                // copy the default one there
                IOUtils.copy(this.getClass().getClassLoader().getResourceAsStream("rules/eventmonitor/1.0/events.drl"),new FileOutputStream(new File(config.getRuleRepository(),"events.drl")));
            }
            return IOUtils.toString(new FileInputStream(new File(config.getRuleRepository(),"events.drl")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public void save(){
        
    }
}
