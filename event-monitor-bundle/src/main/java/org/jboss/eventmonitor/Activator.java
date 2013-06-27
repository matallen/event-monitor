package org.jboss.eventmonitor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator{

  @Override
  public void start(BundleContext ctx) throws Exception {
    new DroolsEventProcessing().start();
  }

  @Override
  public void stop(BundleContext ctx) throws Exception {
    new DroolsEventProcessing().stop();
  }

}
