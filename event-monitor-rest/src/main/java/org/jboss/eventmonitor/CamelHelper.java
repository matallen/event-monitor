package org.jboss.eventmonitor;

import org.apache.camel.ProducerTemplate;
import org.jboss.eventmonitor.stats.Statsd;

public class CamelHelper {
	protected static ProducerTemplate producer;

	public static void send(String to, String payload) {
		// System.out.println("CamelHelper.send('"+to+"', '"+payload+"')");
		Statsd.increment("eventmonitor.events.alerts");
		producer.sendBody(to, payload);
	}
}
