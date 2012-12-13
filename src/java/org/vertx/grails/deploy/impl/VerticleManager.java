package org.vertx.grails.deploy.impl;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.vertx.java.core.impl.VertxInternal;

/**
 * Author: smaldini
 * Date: 8/21/12
 * Project: grails-vertx
 */
public class VerticleManager extends org.vertx.java.deploy.impl.VerticleManager {

    GrailsApplication grailsApplication;

    public VerticleManager(VertxInternal vertx, GrailsApplication grailsApplication) {
        super(vertx);
        this.grailsApplication = grailsApplication;
    }

    public VerticleManager(VertxInternal vertx, GrailsApplication grailsApplication, String defaultRepo) {
        super(vertx, defaultRepo);
        this.grailsApplication = grailsApplication;
    }


}
