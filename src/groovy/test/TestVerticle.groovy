package test

import static org.vertx.groovy.core.streams.Pump.createPump

/**
 * Author: smaldini
 * Date: 8/21/12
 * Project: grails-vertx
 */

container.deployVerticle AnotherConsumerVerticle.name+'.grails', 1
container.deployVerticle AnotherVerticle.name+'.grails', 1