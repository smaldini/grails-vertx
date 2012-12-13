package org.vertx.grails.deploy.impl;

import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.lang.Script;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.vertx.groovy.core.Vertx;
import org.vertx.groovy.deploy.*;
import org.vertx.java.core.impl.VertxInternal;
import org.vertx.java.deploy.Verticle;
import org.vertx.java.deploy.VerticleFactory;
import org.vertx.java.deploy.impl.VertxLocator;

import java.lang.reflect.Method;

/**
 * Author: smaldini
 * Date: 8/21/12
 * Project: grails-vertx
 */
public class GrailsVerticleFactory implements VerticleFactory {

    public static final String GRAILS_SUFFIX = ".grails";

    private VerticleManager mgr;
    private GrailsApplication grailsApplication;

    public GrailsVerticleFactory() {
        super();
    }

    @Override
    public void init(org.vertx.java.deploy.impl.VerticleManager mgr) {
        this.mgr = (VerticleManager)mgr;
        this.grailsApplication = this.mgr.grailsApplication;
    }

    public String getLanguage() {
        return "grails";
    }

    public boolean isFactoryFor(String main) {
        return main.endsWith(GRAILS_SUFFIX);
    }

    public Verticle createVerticle(String main, ClassLoader loader) throws Exception {

        String className = main.substring(0, main.indexOf(GRAILS_SUFFIX));
        Class<?> clazz = grailsApplication.getClassLoader().loadClass(className);
        if(GroovyObject.class.isAssignableFrom(clazz)){
            return loadGroovyClass(clazz);
        }
        return (Verticle) clazz.newInstance();
    }

    public void reportException(Throwable t) {
        mgr.getLogger().error("Exception in Java verticle script", t);
    }

    private Verticle loadGroovyClass(Class clazz) throws IllegalAccessException, InstantiationException {
        Method stop;
        try {
            stop = clazz.getMethod("vertxStop", (Class<?>[])null);
        } catch (NoSuchMethodException e) {
            stop = null;
        }
        final Method mstop = stop;

        Method run;
        try {
            run = clazz.getMethod("run", (Class<?>[])null);
        } catch (NoSuchMethodException e) {
            run = null;
        }
        final Method mrun = run;

        if (run == null) {
            throw new IllegalStateException("Groovy script must have run() method [whether implicit or not]");
        }

        final Script verticle = (Script)clazz.newInstance();

        // Inject vertx into the script binding
        Binding binding = new Binding();
        binding.setVariable("vertx", new Vertx((VertxInternal) VertxLocator.vertx));
        binding.setVariable("container", new org.vertx.groovy.deploy.Container(new org.vertx.java.deploy.Container((mgr))));
        binding.setVariable("grailsApplication", grailsApplication);
        verticle.setBinding(binding);

        return new Verticle() {
            public void start() {
                try {
                    mrun.invoke(verticle, (Object[])null);
                } catch (Throwable t) {
                    reportException(t);
                }
            }

            public void stop() {
                if (mstop != null) {
                    try {
                        mstop.invoke(verticle, (Object[])null);
                    } catch (Throwable t) {
                        reportException(t);
                    }
                }
            }
        };
    }
}
