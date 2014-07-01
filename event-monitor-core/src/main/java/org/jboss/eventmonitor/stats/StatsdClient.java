package org.jboss.eventmonitor.stats;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.jboss.eventmonitor.Configuration;

public class StatsdClient {
    private final String prefix;
    private final DatagramSocket clientSocket;

    public StatsdClient(Configuration c){
    	String[] server=c.getStatsdServer().split(":"); //localhost:8125
    	String host="unknown";
    	try{
	    	host=InetAddress.getLocalHost().getHostName();
	    	if (host.indexOf(".")>0)
	    		host=host.substring(0, host.indexOf(".")); // strip the domain from the machine name
	    } catch (UnknownHostException sink) {
//	        LOG.error("Cannot read host name, will use unknown");
	    }
    	this.prefix=host;
        try {
            this.clientSocket = new DatagramSocket();
            this.clientSocket.connect(new InetSocketAddress(server[0], Integer.parseInt(server[1])));
        } catch (Exception e) {
            throw new RuntimeException("Failed to start StatsD client", e);
        }
    }
    public StatsdClient(String prefix, String hostname, int port/*, StatsDClientErrorHandler errorHandler*/) {
        this.prefix = prefix;
//        this.handler = errorHandler;
        try {
            this.clientSocket = new DatagramSocket();
            this.clientSocket.connect(new InetSocketAddress(hostname, port));
        } catch (Exception e) {
            throw new RuntimeException("Failed to start StatsD client", e);
        }
    }
        
    private void count(String aspect, int delta) {
        send(String.format("%s.%s:%d|c", prefix, aspect, delta));
    }
    
    public void increment(String key) {
        count(key,1);
    }

    public void increment(String key, int magnitude) {
        count(key,magnitude);
    }
    public void decrement(String key) {
        count(key,-1);
    }

    public void decrement(String key, int magnitude) {
        magnitude=magnitude < 0 ? magnitude : -magnitude;
        count(key,magnitude);
    }

    public void timing(String key, long value) {
        send(String.format(Locale.ENGLISH,"%s.%s:%d|ms", prefix, key, value));
    }
    
    public void gauge(String key, double magnitude) {
        send(String.format(Locale.ENGLISH,"%s.%s:%f|g", prefix, key, magnitude));
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        final ThreadFactory delegate = Executors.defaultThreadFactory();
        @Override public Thread newThread(Runnable r) {
            Thread result = delegate.newThread(r);
            result.setName("StatsD-" + result.getName());
            return result;
        }
    });

    @Override
    protected void finalize() throws Throwable {
        try {
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        }
        catch (Exception e) {
//            handler.handle(e);
        }
        finally {
            if (clientSocket != null) {
                clientSocket.close();
            }
        }
    }
    
    private synchronized void send(final String stat) {
        try {
            executor.execute(new Runnable() {
                @Override public void run() {
                	System.out.println("STATSD-> "+stat);
                    blockingSend(stat);
                }
            });
        }
        catch (Exception e) {
//            handler.handle(e);
        	e.printStackTrace();
        }
    }
    
    private void blockingSend(String message) {
        try {
            final DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length);
            clientSocket.send(sendPacket);
        } catch (Exception e) {
//            handler.handle(e);
        	e.printStackTrace();
        }
    }
}
