package org.vertx.grails.deploy.impl;

import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.impl.VerticleManager;

import java.io.File;
import java.net.URL;

/**
 * Author: smaldini
 * Date: 8/21/12
 * Project: grails-vertx
 */
public class Container extends org.vertx.java.deploy.Container {

    private VerticleManager mgr;

    public Container(VerticleManager mgr) {
        super(mgr);
        this.mgr = mgr;
    }

    public void deployWorkerVerticle(Class main) {
        deployWorkerVerticle(main, null, 1);
    }

    /**
     * Deploy a worker verticle programmatically
     *
     * @param main      The main of the verticle
     * @param instances The number of instances to deploy (defaults to 1)
     */
    public void deployWorkerVerticle(Class main, int instances) {
        deployWorkerVerticle(main, null, instances);
    }

    /**
     * Deploy a worker verticle programmatically
     *
     * @param main   The main of the verticle
     * @param config JSON config to provide to the verticle
     */
    public void deployWorkerVerticle(Class main, JsonObject config) {
        deployWorkerVerticle(main, config, 1);
    }

    /**
     * Deploy a worker verticle programmatically
     *
     * @param main      The main of the verticle
     * @param config    JSON config to provide to the verticle
     * @param instances The number of instances to deploy (defaults to 1)
     */
    public void deployWorkerVerticle(Class main, JsonObject config, int instances) {
        deployWorkerVerticle(main, config, instances, null);
    }


    /**
     * Deploy a module programmatically
     *
     * @param moduleName The main of the module to deploy
     */
    public void deployModule(Class moduleName) {
        deployModule(moduleName, null, 1);
    }

    /**
     * Deploy a module programmatically
     *
     * @param moduleName The main of the module to deploy
     * @param instances  The number of instances to deploy (defaults to 1)
     */
    public void deployModule(Class moduleName, int instances) {
        deployModule(moduleName, null, instances);
    }

    /**
     * Deploy a module programmatically
     *
     * @param moduleName The main of the module to deploy
     * @param config     JSON config to provide to the module
     */
    public void deployModule(Class moduleName, JsonObject config) {
        deployModule(moduleName, config, 1);
    }

    /**
     * Deploy a module programmatically
     *
     * @param moduleName The main of the module to deploy
     * @param config     JSON config to provide to the module
     * @param instances  The number of instances to deploy (defaults to 1)
     */
    public void deployModule(Class moduleName, JsonObject config, int instances) {
        deployModule(moduleName, config, instances, null);
    }

    /**
     * Deploy a worker verticle programmatically
     *
     * @param main The main of the verticle
     */
    public void deployVerticle(Class main) {
        deployVerticle(main, null, 1);
    }

    /**
     * Deploy a verticle programmatically
     *
     * @param main      The main of the verticle
     * @param instances The number of instances to deploy (defaults to 1)
     */
    public void deployVerticle(Class main, int instances) {
        deployVerticle(main, null, instances);
    }

    /**
     * Deploy a verticle programmatically
     *
     * @param main   The main of the verticle
     * @param config JSON config to provide to the verticle
     */
    public void deployVerticle(Class main, JsonObject config) {
        deployVerticle(main, config, 1);
    }

    /**
     * Deploy a verticle programmatically
     *
     * @param main      The main of the verticle
     * @param config    JSON config to provide to the verticle
     * @param instances The number of instances to deploy (defaults to 1)
     */
    public void deployVerticle(Class main, JsonObject config, int instances) {
        deployVerticle(main, config, instances, null);
    }


    public void deployModule(Class clazz, JsonObject config, int instances, Handler<String> doneHandler) {
        deployModule(clazz.getName() + GrailsVerticleFactory.GRAILS_SUFFIX, config, instances, doneHandler);
    }

    @Override
    public void deployModule(String moduleName, JsonObject config, int instances, Handler<String> doneHandler) {
        File modDir = mgr.getDeploymentModDir();
        mgr.deployMod(moduleName, config, instances, modDir, doneHandler);
    }

    public void deployWorkerVerticle(Class clazz, JsonObject config, int instances, Handler<String> doneHandler) {
        deployWorkerVerticle(clazz.getName() + GrailsVerticleFactory.GRAILS_SUFFIX, config, instances, doneHandler);
    }

    @Override
    public void deployWorkerVerticle(String main, JsonObject config, int instances, Handler<String> doneHandler) {
        URL[] currURLs = new URL[]{};
        File modDir = mgr.getDeploymentModDir();
        mgr.deployVerticle(true, main, config, currURLs, instances, modDir, null, doneHandler);
    }

    public void deployVerticle(Class clazz, JsonObject config, int instances, Handler<String> doneHandler) {
        deployVerticle(clazz.getName() + GrailsVerticleFactory.GRAILS_SUFFIX, config, instances, doneHandler);
    }

    @Override
    public void deployVerticle(String main, JsonObject config, int instances, Handler<String> doneHandler) {
        URL[] currURLs = new URL[]{};
        File modDir = mgr.getDeploymentModDir();
        mgr.deployVerticle(false, main, config, currURLs, instances, modDir,  null, doneHandler);
    }
}
