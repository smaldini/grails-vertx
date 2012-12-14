package test

import org.grails.plugin.vertx.VertxService
import org.vertx.java.core.Handler
import org.vertx.java.core.SimpleHandler
import org.vertx.java.core.json.JsonObject

import java.util.concurrent.CountDownLatch

/**
 * Author: smaldini
 * Date: 8/21/12
 * Project: grails-vertx
 */
class SampleController {

    VertxService vertxService

    def index(){
        render 'test'
    }

    def test2() {
        for( long i = 0L; i < 10L; i++ )
            event for: 'vertx', topic: 'test', data: 'lol'

        render 'test2'
    }

    def test() {

        println servletContext.getRealPath('WEB-INF/mods')

//        CountDownLatch d = new CountDownLatch(1)
//        vertxService.verticleManager.undeployAll([handle: {
//            d.countDown()
//        }] as Handler<Void>)
//
//        d.await()

        //vertxService.container.deployVerticle AnotherConsumerVerticle, 8
        def consumer = "9222" == System.properties['server.port']
        println consumer

        vertxService.instance.eventBus.registerHandler('ping-'+(consumer?'c':'p')){
            if(consumer){
                vertxService.container.deployVerticle AnotherConsumerVerticle, null, 8 , {} as Handler<String>
                vertxService.instance.eventBus.publish('ping-p',1)
            }
            else{
                vertxService.container.deployVerticle AnotherVerticle, null, 8 , {} as Handler<String>
            }
        }

        if(!consumer)
            vertxService.container.deployVerticle InitVerticle, null, 1 , {} as Handler<String>


        vertxService.container.deployVerticle TestVerticle, null, 1 , {} as Handler<String>

        //event for: 'vertx', topic: 'test', data: 'lol'

        render 'ok'
    }
}
