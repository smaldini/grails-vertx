package org.grails.plugin.vertx.events;

import com.google.gson.Gson;
import grails.converters.JSON;
import groovy.json.JsonSlurper;
import groovy.lang.Closure;
import org.apache.log4j.Logger;
import org.grails.plugin.platform.events.EventMessage;
import org.grails.plugin.platform.events.EventReply;
import org.grails.plugin.platform.events.ListenerId;
import org.grails.plugin.platform.events.publisher.EventsPublisher;
import org.grails.plugin.platform.events.registry.DefaultEventsRegistry;
import org.grails.plugin.vertx.VertxService;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * User: smaldini
 * Date: 8/21/12
 */
public class VertxEventsPublisher implements EventsPublisher {

    static final private Logger log = Logger.getLogger(VertxEventsPublisher.class);

    public final static String REPLY_TO = "reply_to";

    private EventBus eventBus;
    private Vertx vertx;

    public void setVertx(VertxService vertx) {
        this.vertx = vertx.getInstance().toJavaVertx();
        this.eventBus = this.vertx.eventBus();
    }

    @Override
    public EventReply event(EventMessage event) {
        return eventAsync(event, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventReply eventAsync(EventMessage event, Map<String, Object> params) {

        final String replyId = "grails-gen-" + UUID.randomUUID().toString();

        final Handler<Message<String>> stringHandler = new Handler<Message<String>>() {

            @Override
            public void handle(Message<String> event) {
                new JsonSlurper().parseText(event.body);

                eventBus.unregisterHandler(replyId, this);
            }
        };

        eventBus.registerHandler(replyId, stringHandler);

        if (event.getHeaders() == null) {
            event = new EventMessage(event.getEvent(), event.getData(), event.getNamespace(),
                    event.isGormSession(), new HashMap<String, String>());
        }

        if (!event.getHeaders().containsKey(REPLY_TO)) {
            event.getHeaders().put(REPLY_TO, replyId);
        }

        String json = new JSON(event).toString();
        log.debug(
                "sending \n" + json
        );
        eventBus.publish((VertxConstants.VERTX_NS.equals(event.getNamespace()) ? "" : (event.getNamespace() + ListenerId.ID_NAMESPACE_SEPARATOR)) + event.getEvent(), json);
        return new EventReply(stringHandler, -1);
    }

    private static class WrappedFuture extends EventReply {

        private CountDownLatch latch = new CountDownLatch(1);

        public WrappedFuture(Future<?> wrapped, int receivers) {
            super(wrapped, receivers);
        }

        @Override
        protected void initValues(Object val) {
            DefaultEventsRegistry.InvokeResult message = (DefaultEventsRegistry.InvokeResult) val;
            setReceivers(message.getInvoked());
            super.initValues(message.getResult());
        }


        public void setCallingError(Throwable e) {
            super.initValues(e);
            if (getOnError() != null) {
                getOnError().call(this);
            }
        }

    }
}
