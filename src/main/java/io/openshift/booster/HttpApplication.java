package io.openshift.booster;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 *
 */
public class HttpApplication extends AbstractVerticle {

    private ConfigRetriever conf;
    private String message;

    private static final Logger LOGGER = LogManager.getLogger(HttpApplication.class);
    private JsonObject config;

    @Override
    public void start() {
        conf = ConfigRetriever.create(vertx);

        Router router = Router.router(vertx);
        router.get("/api/greeting").handler(this::greeting);
        router.get("/api/cf").handler(this::getCF);
        router.get("/health").handler(rc -> rc.response().end("OK"));
        router.get("/").handler(StaticHandler.create());

        retrieveMessageTemplateFromConfiguration().setHandler(ar -> {
            // Once retrieved, store it and start the HTTP server.
            message = ar.result();
            vertx.createHttpServer().requestHandler(router::accept).listen(
                    // Retrieve the port from the configuration,
                    // default to 8080.
                    config().getInteger("http.port", 8080));

        });

        // It should use the retrieve.listen method, however it does not catch the
        // deletion of the config map.
        // https://github.com/vert-x3/vertx-config/issues/7
        vertx.setPeriodic(2000, l -> {
            conf.getConfig(ar -> {
                if (ar.succeeded()) {
                    if (config == null || !config.encode().equals(ar.result().encode())) {
                        config = ar.result();
                        LOGGER.info("New configuration retrieved: {}", ar.result().getString("message"));
                        message = ar.result().getString("message");
                        String level = ar.result().getString("level", "INFO");
                        LOGGER.info("New log level: {}", level);
                        setLogLevel(level);
                    }
                } else {
                    message = null;
                }
            });
        });
    }

    private void setLogLevel(String level) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(Level.getLevel(level));
        ctx.updateLoggers();
    }

    private void getCF(RoutingContext rc) {
        try {
            if (message == null) {
                rc.response().setStatusCode(500).putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
                        .end(new JsonObject().put("content", "no config map").encode());
                return;
            }
            String nome = rc.request().getParam("nome");
            if (nome == null) {
                nome = "Giuseppe";
            }
            String cognome = rc.request().getParam("cognome");
            if (cognome == null) {
                cognome = "Marino";
            }
            String comune = rc.request().getParam("comune");
            if (comune == null) {
                comune = "Novellara";
            }
            comune = comune.toUpperCase();

            String m = rc.request().getParam("m");
            if (m == null) {
                m = "Luglio";
            }

            String annoString = rc.request().getParam("anno");
            if (annoString == null) {
                annoString = "1982";
            }
            int anno = Integer.parseInt(annoString);

            String giornoString = rc.request().getParam("giorno");
            if (giornoString == null) {
                giornoString = "22";
            }
            int giorno = Integer.parseInt(giornoString);

            String sesso = rc.request().getParam("sesso");
            if (sesso == null) {
                sesso = "M";
            }
            CFGenerator gen = new CFGenerator(nome, cognome, comune, m, anno, giorno, sesso);
            LOGGER.info("Replying to request, nome={}, cognome={}, comune={}, m={}, anno={}, giorno={}, sesso={}", nome,
                    cognome, comune, m, anno, giorno, sesso);
            JsonObject response = new JsonObject().put("content", gen.toString());

            rc.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8").end(response.encodePrettily());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
    }

    private void greeting(RoutingContext rc) {
        if (message == null) {
            rc.response().setStatusCode(500).putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
                    .end(new JsonObject().put("content", "no config map").encode());
            return;
        }
        String name = rc.request().getParam("name");
        if (name == null) {
            name = "World";
        }

        LOGGER.debug("Replying to request, parameter={}", name);
        JsonObject response = new JsonObject().put("content", String.format(message, name));

        rc.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8").end(response.encodePrettily());
    }

    private Future<String> retrieveMessageTemplateFromConfiguration() {
        Future<String> future = Future.future();
        conf.getConfig(ar -> future.handle(ar.map(json -> json.getString("message")).otherwise(t -> null)));
        return future;
    }
}