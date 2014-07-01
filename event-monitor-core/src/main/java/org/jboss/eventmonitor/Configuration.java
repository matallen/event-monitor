package org.jboss.eventmonitor;

import java.io.File;
import org.apache.commons.lang.StringUtils;

public class Configuration {
    private String serverUri;
    private String packageName;
    private String packageVersion;
    private String devMode;
    private String username;
    private String password;
    private String ruleRepository;
    private String statsdServer;
    
    public Configuration(){
        // env vars for docker
        serverUri=System.getenv("BRMS_PORT_8080_TCP_GUVNORURI");
        username=System.getenv("BRMS_PORT_8080_TCP_GUVNORUSERNAME");
        password=System.getenv("BRMS_PORT_8080_TCP_GUVNORPASSWORD");
        packageName=System.getenv("BRMS_PORT_8080_TCP_GUVNORPACKAGENAME");
        packageVersion=System.getenv("BRMS_PORT_8080_TCP_GUVNORPACKAGEVERSION");
        ruleRepository=System.getenv("BRMS_PORT_8080_TCP_RULEREPOSITORY");
        devMode=System.getenv("BRMS_PORT_8080_TCP_DEVMODE");
        statsdServer=System.getenv("BRMS_PORT_8080_TCP_STATSDSERVER");
        
        // runtime properties
        if (StringUtils.isEmpty(serverUri))      serverUri=      readSystemProperty("guvnorUri","http://localhost:8080/jboss-brms");
        if (StringUtils.isEmpty(username))       username=       readSystemProperty("guvnorUsername", "admin");
        if (StringUtils.isEmpty(password))       password=       readSystemProperty("guvnorPassword", "admin");
        if (StringUtils.isEmpty(packageName))    packageName=    readSystemProperty("guvnorPackageName", "eventmonitor");
        if (StringUtils.isEmpty(packageVersion)) packageVersion= readSystemProperty("guvnorPackageVersion", "1.0");
        if (StringUtils.isEmpty(ruleRepository)) ruleRepository= readSystemProperty("ruleRepository", "repository");
        if (StringUtils.isEmpty(devMode))        devMode=        readSystemProperty("devMode", "false");
        if (StringUtils.isEmpty(statsdServer))   statsdServer=   readSystemProperty("statsdServer", "localhost:8125"); //localhost:8125
        
        // checks / initialisation
        if (new File("target").exists()) ruleRepository="target"+File.separator+ruleRepository; // for maven dev purposes
        if (!new File(ruleRepository).exists()){
            System.out.println("Creating new RuleRepository: "+new File(ruleRepository).getAbsolutePath());
            new File(ruleRepository).mkdirs();
        }
    }
    
    private String readSystemProperty(String propertyName, String defaultValue){
        return StringUtils.isEmpty(System.getProperty(propertyName))?defaultValue:System.getProperty(propertyName);
    }
    
    public String getBaseServerUrl() { return serverUri+"/org.drools.guvnor.Guvnor/package/"; }
    public boolean isDevMode()       { return devMode.equalsIgnoreCase("true"); }
    public String getPackageName()   { return packageName; }
    public String getPackageVersion(){ return packageVersion; }
    public String getUsername()      { return username; }
    public String getPassword()      { return password; }
    public File getRuleRepository()  { return new File(ruleRepository); }
    public String getStatsdServer()  { return statsdServer; }
}
