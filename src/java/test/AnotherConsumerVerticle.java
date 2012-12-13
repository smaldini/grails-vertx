package test;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.deploy.Verticle;

/**
 * Author: smaldini
 * Date: 8/22/12
 * Project: grails-vertx
 */
public class AnotherConsumerVerticle extends Verticle {

    private long counter = 0L;
    private long start = 0L;
    private long last = 0L;

    public void start() {
        System.out.println( "Starting Consumer "+Thread.currentThread() );
        vertx.eventBus().registerHandler( "message.string", new Handler<Message<String>>() {
            public void handle(Message<String> message) {
                count();
            }
        });
    }

    // counting and print out stats every 10M msgs
    private void count() {
        ++counter;
        if( start == 0L )
            start = System.nanoTime();
        if( counter % 10_000_000L == 0 ) {
            long tot = ((System.nanoTime() - start)/1_000_000_000);
            System.out.println(tot-last);
            last = tot;
        }
    }

}
