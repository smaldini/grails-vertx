package org.grails.plugin.vertx;

import org.apache.log4j.Logger;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.jboss.netty.channel.ChannelException;
import org.vertx.groovy.core.Vertx;
import org.vertx.groovy.core.streams.ReadStream;
import org.vertx.java.core.Handler;
import org.vertx.java.core.impl.Context;
import org.vertx.java.core.impl.VertxInternal;
import org.vertx.grails.deploy.impl.Container;
import org.vertx.java.deploy.impl.VerticleManager;
import org.vertx.java.deploy.impl.VertxLocator;

import java.util.Date;

/**
 * Author: smaldini
 * Date: 8/21/12
 */
public class VertxService {

    static final private Logger log = Logger.getLogger(VertxService.class);

    private VerticleManager verticleManager;
    private Container container;
    private Vertx instance;
    private GrailsApplication grailsApplication;

    public VerticleManager getVerticleManager() {
        return verticleManager;
    }

    public Container getContainer() {
        return container;
    }

    public Vertx getInstance() {
        return instance;
    }

    public void setGrailsApplication(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
    }

    public void init() {

        if (System.getProperties().get("org.vertx.logger-delegate-factory-class-name") == null) {
            System.getProperties().put("org.vertx.logger-delegate-factory-class-name",
                    "org.vertx.java.core.logging.impl.Log4jLogDelegateFactory");
        }

        Vertx vertx = null;
        int port = 25500;
        while (vertx == null && port < 65535) {
            try {
                vertx = Vertx.newVertx(port, "localhost");
            } catch (ChannelException e) {
                log.debug("failed to bind on port " + port);
                port++;
            }
        }

        this.verticleManager = new org.vertx.grails.deploy.impl.VerticleManager((VertxInternal)vertx.toJavaVertx(), grailsApplication);
        this.container = new Container(verticleManager);
        VertxLocator.container = this.container;

        this.instance = vertx;

        /*vertx.fileSystem.open(System.properties['base.dir'] + '/grails-app/conf/a.groovy') {
            def a = it.result
            vertx.fileSystem.open(System.properties['base.dir'] + '/grails-app/conf/b.groovy') {
                def b = it.result
                vertx.fileSystem.open(System.properties['base.dir'] + '/grails-app/conf/MyVerticle.groovy') {
                    r->
                            println 'test'
                    if (r.succeeded()) {
                        println 'reading'

                        ReadStream src = r.result.readStream
                        src.exceptionHandler {
                            println it
                        }
                        src.endHandler {
                            println 'end'
                        }
                        def pumpA = Pump.createPump(src, a.writeStream)
                        def pumpB = Pump.createPump(src, b.writeStream)
                        pumpA.start()
                        pumpB.start()
                    }
                }
            }
        }*/


        /*def eb = vertx.eventBus

        eb.registerHandler('toto') {
            println it.dump()
            println Thread.currentThread()
            println 'work'
        }*/


//        def verticle = "grails-app/conf/MyVerticle.groovy"
//        def vmgr = new VerticleManager(vertx.toJavaVertx())
//        vmgr.deployVerticle(false, verticle , null, [('file://'+System.properties['base.dir']+'/').toURL()] as URL[], 1, null, null)

//        eb.publish('toto', new Date().toString())
    }

    public void shutdown() {
        verticleManager.undeployAll(new Handler<Void>() {
            @Override
            public void handle(Void aVoid) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

}
