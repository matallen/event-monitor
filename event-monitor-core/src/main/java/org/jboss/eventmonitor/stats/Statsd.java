package org.jboss.eventmonitor.stats;

import org.jboss.eventmonitor.Configuration;

public class Statsd {
	private static synchronized StatsdClient getStatsdClient(){
		return new StatsdClient(new Configuration());
	}
    public static void increment(String key) {
    	getStatsdClient().increment(key);
    }
    public static void increment(String key, int magnitude) {
    	getStatsdClient().increment(key, magnitude);
    }
    public static void decrement(String key) {
    	getStatsdClient().decrement(key);
    }
    public static void decrement(String key, int magnitude) {
    	getStatsdClient().decrement(key, magnitude);
    }
    public static void timing(String key, long value) {
    	getStatsdClient().timing(key, value);
    }
    public static void gauge(String key, double magnitude) {
    	getStatsdClient().gauge(key, magnitude);
    }
}
