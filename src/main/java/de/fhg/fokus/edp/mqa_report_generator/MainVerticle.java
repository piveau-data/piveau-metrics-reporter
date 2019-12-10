package de.fhg.fokus.edp.mqa_report_generator;

import de.fhg.fokus.edp.mqa_report_generator.model.ReportFormat;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.contract.RouterFactoryOptions;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static de.fhg.fokus.edp.mqa_report_generator.ApplicationConfig.*;


public class MainVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

    private JsonObject config;
    private ApiKeyHandler apiKeyHandler;

    @Override
    public void start() {
        LOG.info("Launching MQA-Report-Generator...");

        // startup is only successful if no step failed
        Future<Void> steps = loadConfig()
            .compose(handler -> initApiKey())
            .compose(handler -> bootstrapVerticles())
            .compose(handler -> startServer());

        steps.setHandler(handler -> {
            if (handler.succeeded()) {
                LOG.info("MQA-Report-Generator successfully launched");
            } else {
                handler.cause().printStackTrace();
                LOG.error("Failed to launch MQA-Metric-Service: " + handler.cause());
            }
        });
    }

    private Future<Void> loadConfig() {
        Future<Void> future = Future.future();

        ConfigRetriever configRetriever = ConfigRetriever.create(vertx);

        configRetriever.getConfig(handler -> {
            if (handler.succeeded()) {
                config = handler.result();
                LOG.info(config.encodePrettily());
                future.complete();
            } else {
                future.fail("Failed to load config: " + handler.cause());
            }
        });

        configRetriever.listen(change ->
            config = change.getNewConfiguration());

        return future;
    }

    private Future<Void> initApiKey() {
        Future<Void> future = Future.future();

        String apiKey = config.getString(ENV_API_KEY);

        if (apiKey != null && !apiKey.isEmpty()) {
            apiKeyHandler = new ApiKeyHandler(apiKey);
            future.complete();
        } else {
            future.fail("No API key specified");
        }

        return future;
    }

    private CompositeFuture bootstrapVerticles() {
        DeploymentOptions options = new DeploymentOptions()
            .setConfig(config)
            .setWorker(true);

        List<Future> deploymentFutures = new ArrayList<>();
        deploymentFutures.add(startVerticle(options, ReportGeneratorVerticle.class.getName()));

        return CompositeFuture.join(deploymentFutures);
    }

    private Future<Void> startServer() {
        Future<Void> startFuture = Future.future();
        Integer port = config.getInteger(ENV_APPLICATION_PORT, DEFAULT_APPLICATION_PORT);

        OpenAPI3RouterFactory.create(vertx, "webroot/openapi.yaml", handler -> {
            if (handler.succeeded()) {
                OpenAPI3RouterFactory routerFactory = handler.result();
                RouterFactoryOptions options = new RouterFactoryOptions().setMountNotImplementedHandler(true).setMountValidationFailureHandler(true);
                routerFactory.setOptions(options);

                routerFactory.addSecurityHandler("ApiKeyAuth", apiKeyHandler::checkApiKey);

                routerFactory.addHandlerByOperationId("generateReports", this::handleGenerateReportsRequest);
                routerFactory.addHandlerByOperationId("downloadReport", this::handleDownloadReportRequest);

                Router router = routerFactory.getRouter();
                router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.GET).allowedHeader("Access-Control-Allow-Origin: *"));
                router.route("/*").handler(StaticHandler.create());

                HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(port));
                server.requestHandler(router).listen();

                // generate reports once on startup
                vertx.eventBus().send(GENERATE_REPORT_ADDRESS, "msg.generate");

                LOG.info("Server successfully launched on port [{}]", port);
                startFuture.complete();
            } else {
                // Something went wrong during router factory initialization
                LOG.error("Failed to start server at [{}]: {}", port, handler.cause());
                startFuture.fail(handler.cause());
            }
        });




        return startFuture;
    }

    private void handleGenerateReportsRequest(RoutingContext context) {
        vertx.eventBus().send(GENERATE_REPORT_ADDRESS, "msg.generate");
        context.response().setStatusCode(202).end();
    }

    private void handleDownloadReportRequest(RoutingContext context) {

        String language = context.pathParam("languageCode");
        ReportFormat format = ReportFormat.valueOf(context.pathParam("format").toUpperCase());

        String fileName = Paths.get(config.getString(ENV_REPORT_DIRECTORY, DEFAULT_REPORT_DIRECTORY),
            String.format("mqa-report_%s.%s", language.toUpperCase(), format.name().toLowerCase())).toString();
        LOG.debug("Report file [{}] requested", fileName);

        context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, format.getMimeType())
            .putHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=EuropeanDataPortal_MQA_Report_" + language.toUpperCase() + "." + format.name().toLowerCase())
            .putHeader(HttpHeaders.TRANSFER_ENCODING, "chunked")
            .sendFile(fileName).end();
    }

    private Future<Void> startVerticle(DeploymentOptions options, String className) {
        Future<Void> future = Future.future();

        vertx.deployVerticle(className, options, handler -> {
            if (handler.succeeded()) {
                future.complete();
            } else {
                LOG.error("Failed to deploy verticle [{}] : {}", className, handler.cause());
                future.fail("Failed to deploy [" + className + "] : " + handler.cause());
            }
        });

        return future;
    }
}
