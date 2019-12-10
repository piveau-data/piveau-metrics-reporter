package de.fhg.fokus.edp.mqa_report_generator.metrics;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public interface MetricProvider {

    void getCatalogues(Handler<AsyncResult<JsonObject>> resultHandler);

    void getRenderDistributions(Handler<AsyncResult<JsonObject>> resultHandler);
    void getRenderDistributions(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);

    void getRenderViolations(Handler<AsyncResult<JsonObject>> resultHandler);
    void getRenderViolations(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);

    void getRenderLicences(Handler<AsyncResult<JsonObject>> resultHandler);
    void getRenderLicences(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);


    void getDistributionAccessibilityAccessUrl(Handler<AsyncResult<JsonObject>> resultHandler);
    void getDistributionAccessibilityAccessUrl(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);

    void getDistributionAccessibilityDownloadUrl(Handler<AsyncResult<JsonObject>> resultHandler);
    void getDistributionAccessibilityDownloadUrl(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);


    void getDistributionStatusCodes(Handler<AsyncResult<JsonObject>> resultHandler);
    void getDistributionStatusCodes(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);

    void getDistributionDownloadUrlExists(Handler<AsyncResult<JsonObject>> resultHandler);
    void getDistributionDownloadUrlExists(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);

    void getDistributionMachineReadability(Handler<AsyncResult<JsonObject>> resultHandler);
    void getDistributionMachineReadability(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);

    void getDistributionFormats(Handler<AsyncResult<JsonObject>> resultHandler);
    void getDistributionFormats(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);


    void getDatasetViolations(Handler<AsyncResult<JsonObject>> resultHandler);
    void getDatasetViolations(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);

    void getDatasetCompliance(Handler<AsyncResult<JsonObject>> resultHandler);
    void getDatasetCompliance(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);


    void getDatasetLicences(Handler<AsyncResult<JsonObject>> resultHandler);
    void getDatasetLicences(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);

    void getDatasetKnownLicences(Handler<AsyncResult<JsonObject>> resultHandler);
    void getDatasetKnownLicences(String catalogueId, Handler<AsyncResult<JsonObject>> resultHandler);
}
