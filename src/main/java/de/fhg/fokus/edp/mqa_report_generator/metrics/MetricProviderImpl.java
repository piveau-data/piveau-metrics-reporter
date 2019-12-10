package de.fhg.fokus.edp.mqa_report_generator.metrics;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.fhg.fokus.edp.mqa_report_generator.metrics.Metric.*;

public class MetricProviderImpl implements MetricProvider {

    private static final Logger LOG = LoggerFactory.getLogger(MetricProvider.class);

    private WebClient webClient;
//    private CloseableHttpClient httpClient;
//    private String host;
//    private Integer port;

    public MetricProviderImpl(Vertx vertx, String defaultHost, int defaultPort) {
        WebClientOptions webClientOptions = new WebClientOptions()
            .setDefaultHost(defaultHost)
            .setDefaultPort(defaultPort);

        webClient = WebClient.create(vertx, webClientOptions);

//        httpClient = vertx.createHttpClient(options);

//        this.host = defaultHost;
//        this.port = defaultPort;
    }

    @Override
    public void getCatalogues(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(INFO_CATALOGUES, resultHandler);
    }

    @Override
    public void getRenderDistributions(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_RENDER_DISTRIBUTIONS, resultHandler);
    }

    @Override
    public void getRenderDistributions(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_RENDER_DISTRIBUTIONS, catalogueId, resultHandler);
    }

    @Override
    public void getRenderViolations(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_RENDER_VIOLATIONS, resultHandler);
    }

    @Override
    public void getRenderViolations(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_RENDER_VIOLATIONS, catalogueId, resultHandler);
    }

    @Override
    public void getRenderLicences(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_RENDER_LICENCES, resultHandler);
    }

    @Override
    public void getRenderLicences(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_RENDER_LICENCES, catalogueId, resultHandler);
    }

    @Override
    public void getDistributionAccessibilityAccessUrl(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_DISTRIBUTION_ACCESSIBILITY_ACCESS_URL, resultHandler);
    }

    @Override
    public void getDistributionAccessibilityAccessUrl(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_DISTRIBUTION_ACCESSIBILITY_ACCESS_URL, catalogueId, resultHandler);
    }

    @Override
    public void getDistributionAccessibilityDownloadUrl(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_DISTRIBUTION_ACCESSIBILITY_DOWNLOAD_URL, resultHandler);
    }

    @Override
    public void getDistributionAccessibilityDownloadUrl(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_DISTRIBUTION_ACCESSIBILITY_DOWNLOAD_URL, catalogueId, resultHandler);
    }

    @Override
    public void getDistributionStatusCodes(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_STATUS_CODES, resultHandler);
    }

    @Override
    public void getDistributionStatusCodes(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_STATUS_CODES, catalogueId, resultHandler);
    }

    @Override
    public void getDistributionDownloadUrlExists(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_DOWNLOAD_URL_EXIST, resultHandler);
    }

    @Override
    public void getDistributionDownloadUrlExists(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_DOWNLOAD_URL_EXIST, catalogueId, resultHandler);
    }

    @Override
    public void getDistributionMachineReadability(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_MACHINE_READABILITY, resultHandler);
    }

    @Override
    public void getDistributionMachineReadability(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_MACHINE_READABILITY, catalogueId, resultHandler);
    }

    @Override
    public void getDistributionFormats(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_DISTRIBUTION_FORMATS, resultHandler);
    }

    @Override
    public void getDistributionFormats(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_DISTRIBUTION_FORMATS, catalogueId, resultHandler);
    }

    @Override
    public void getDatasetViolations(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_DATASET_VIOLATIONS, resultHandler);
    }

    @Override
    public void getDatasetViolations(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_DATASET_VIOLATIONS, catalogueId, resultHandler);
    }

    @Override
    public void getDatasetCompliance(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_DATASET_COMPLIANCE, resultHandler);
    }

    @Override
    public void getDatasetCompliance(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_DATASET_COMPLIANCE, catalogueId, resultHandler);
    }

    @Override
    public void getDatasetLicences(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_DATASET_LICENCES, resultHandler);
    }

    @Override
    public void getDatasetLicences(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_DATASET_LICENCES, catalogueId, resultHandler);
    }

    @Override
    public void getDatasetKnownLicences(Handler<AsyncResult<JsonObject>> resultHandler) {
        getGlobalMetric(GLOBAL_DATASET_KNOWN_LICENCES, resultHandler);
    }

    @Override
    public void getDatasetKnownLicences(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getCatalogueMetric(CATALOGUE_DATASET_KNOWN_LICENCES, catalogueId, resultHandler);
    }
    

    private void getCatalogueMetric(Metric metric, String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getMetric(String.format(metric.getEndpoint(), catalogueId), resultHandler);
    }

    private void getGlobalMetric(Metric metric, Handler<AsyncResult<JsonObject>> resultHandler) {
        getMetric(metric.getEndpoint(), resultHandler);
    }

    private void getMetric(String endpoint, Handler<AsyncResult<JsonObject>> resultHandler) {
        webClient.get(endpoint)
            .expect(ResponsePredicate.SC_OK)
            .expect(ResponsePredicate.JSON)
            .send(requestHandler -> {
                if (requestHandler.succeeded()) {
                    LOG.debug("Successfully retrieved metric from endpoint [{}]: {}", endpoint, requestHandler.result().bodyAsJsonObject());
                    resultHandler.handle(Future.succeededFuture(requestHandler.result().bodyAsJsonObject()));
                } else {
                    LOG.error("Failed to retrieve metric from endpoint [{}]", endpoint);
                    resultHandler.handle(Future.failedFuture(requestHandler.cause()));
                }
            });
    }
}
