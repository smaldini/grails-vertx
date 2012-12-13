package org.grails.plugin.vertx.events;

import com.google.gson.Gson;
import groovy.lang.Closure;
import org.grails.plugin.platform.events.EventMessage;
import org.grails.plugin.platform.events.ListenerId;
import org.grails.plugin.platform.events.registry.DefaultEventsRegistry;
import org.grails.plugin.platform.events.registry.EventHandler;
import org.grails.plugin.platform.events.registry.EventsRegistry;
import org.grails.plugin.vertx.VertxService;
import org.springframework.util.ReflectionUtils;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.deploy.impl.VerticleManager;

import java.lang.reflect.Method;

/**
 * User: smaldini
 * Date: 8/21/12
 */
public class VertxEventsRegistry implements EventsRegistry {

    private EventBus eventBus;
    private org.vertx.groovy.core.eventbus.EventBus groovyEventBus;
    private VerticleManager verticleManager;
    private Vertx vertx;
    private org.vertx.groovy.core.Vertx groovyVertx;


    public void setVertx(VertxService vertx) {
        this.verticleManager = vertx.getVerticleManager();
        this.groovyVertx = vertx.getInstance();
        this.vertx = groovyVertx.toJavaVertx();
        this.groovyEventBus = groovyVertx.getEventBus();
        this.eventBus = this.vertx.eventBus();
    }

    public String on(String namespace, String topic, Closure callback) {
        return registerHandler(callback, namespace, topic);
    }

    public String on(String namespace, String topic, Object bean, String callbackName) {
        return registerHandler(bean, ReflectionUtils.findMethod(bean.getClass(), callbackName), namespace, topic);
    }

    public String on(String namespace, String topic, Object bean, Method callback) {
        return registerHandler(bean, callback, namespace, topic);
    }

    @SuppressWarnings("unchecked")
    private String registerHandler(Object bean, Method method, String namespace, String topic) {
        //verticleManager.deployVerticle();
        final DefaultEventsRegistry.ListenerHandler eventHandler = new DefaultEventsRegistry.ListenerHandler(bean, method, null);

        eventBus.registerHandler((VertxConstants.VERTX_NS.equals(namespace) ? "" : (namespace + ListenerId.ID_NAMESPACE_SEPARATOR)) + topic, new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> event) {
                Gson gson = new Gson();
                EventMessage evt = gson.fromJson(event.body, EventMessage.class);
                Object result = null;
                try {
                    result = eventHandler.invoke(evt);
                } catch (Throwable throwable) {
                    result = throwable.getMessage();
                }
                if (evt.getHeaders() != null && evt.getHeaders().containsKey(VertxEventsPublisher.REPLY_TO)) {
                    eventBus.send(evt.getHeaders().get(VertxEventsPublisher.REPLY_TO).toString(),
                            gson.toJson(new EventMessage(evt.getEvent(), result, "", false, evt.getHeaders())));
                }
            }
        });
        return null;
    }


    private String registerHandler(Closure callback, String namespace, String topic) {
        groovyEventBus.registerHandler((VertxConstants.VERTX_NS.equals(namespace) ? "" : (namespace + ListenerId.ID_NAMESPACE_SEPARATOR)) + topic, callback);
        return null;
    }


    @Override
    public int removeListeners(String callbackId) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int countListeners(String callbackId) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
