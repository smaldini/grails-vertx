package test

import grails.events.Listener

/**
 * Author: smaldini
 * Date: 8/21/12
 * Project: grails-vertx
 */
class SampleService {

    private counter = 0L
    private start = 0L
    private last = 0L

    static transactional = false

    // counting and print out stats every 10M msgs
    private void count() {
        println counter
        ++counter
        if( start == 0L )
            start = System.nanoTime()
        if( counter % 10000000L == 0 ) {
            long tot = ((System.nanoTime() - start)/1000000)
            println tot-last
            last = tot
        }
    }

    @Listener(namespace='vertx')
    void test(String msg){
       count()
    }
}
