package test;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.deploy.Verticle;

/**
 * Author: smaldini
 * Date: 8/22/12
 * Project: grails-vertx
 */
public class InitVerticle extends Verticle {

    public void send(final int count){
        vertx.eventBus().publish("ping-c", 1);
    }

    public void start() {
        System.out.println( "Starting Producer "+Thread.currentThread() );
        send(0);
    }
}
