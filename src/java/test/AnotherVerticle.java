package test;

import org.vertx.java.core.Handler;
import org.vertx.java.deploy.Verticle;

import java.util.UUID;

/**
 * Author: smaldini
 * Date: 8/22/12
 * Project: grails-vertx
 */
public class AnotherVerticle extends Verticle {

    public void send(final int count){
        vertx.runOnLoop(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                vertx.eventBus().send("message.string", "simple string ");
                if(count<100_000_000L){
                    send(count+1);
                }else{
                    System.out.println("100M msg sent");
                }
            }
        });
    }

    public void start() {
        System.out.println( "Starting Producer "+Thread.currentThread() );
        send(0);
    }
}
