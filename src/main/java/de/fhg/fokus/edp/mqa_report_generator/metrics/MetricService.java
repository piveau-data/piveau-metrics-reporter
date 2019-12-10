package de.fhg.fokus.edp.mqa_report_generator.metrics;

import de.fhg.fokus.edp.mqa_report_generator.model.ReportElement;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportValues;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MetricService {

    private static final Logger LOG = LoggerFactory.getLogger(MetricService.class);

    private MetricProvider metricProvider;
    private Integer chartElementLimit;

    public MetricService(MetricProvider metricProvider, Integer chartElementLimit) {
        this.metricProvider = metricProvider;
        this.chartElementLimit = chartElementLimit;
    }

    public Future<ReportValues> getGlobalReportValues() {
        LOG.info("Fetching global metrics...");

        Future<ReportValues> completionFuture = Future.future();

        List<Future> metricFutures = new ArrayList<>();
        ReportValues reportValues = new ReportValues();

        Future distributionFuture = Future.future();
        metricFutures.add(distributionFuture);
        metricProvider.getRenderDistributions(distributionHandler -> {
            boolean renderDistributions = distributionHandler.succeeded() && distributionHandler.result().getBoolean("result");
            reportValues.setRenderDistributions(renderDistributions);

            if (renderDistributions) {
                List<Future> distributionFutures = new ArrayList<>();

                Future<JsonObject> distributionAccessUrlFuture = Future.future();
                metricProvider.getDistributionAccessibilityAccessUrl(distributionAccessUrlFuture);
                distributionFutures.add(distributionAccessUrlFuture);

                Future<JsonObject> distributionDownloadUrlFuture = Future.future();
                metricProvider.getDistributionAccessibilityDownloadUrl(distributionDownloadUrlFuture);
                distributionFutures.add(distributionDownloadUrlFuture);

                Future<JsonObject> distributionStatusCodesFuture = Future.future();
                metricProvider.getDistributionStatusCodes(distributionStatusCodesFuture);
                distributionFutures.add(distributionStatusCodesFuture);

                Future<JsonObject> distributionsDownloadUrlExistsFuture = Future.future();
                metricProvider.getDistributionDownloadUrlExists(distributionsDownloadUrlExistsFuture);
                distributionFutures.add(distributionsDownloadUrlExistsFuture);

                Future<JsonObject> distributionMachineReadabilityFuture = Future.future();
                metricProvider.getDistributionMachineReadability(distributionMachineReadabilityFuture);
                distributionFutures.add(distributionMachineReadabilityFuture);

                Future<JsonObject> distributionFormatsFuture = Future.future();
                metricProvider.getDistributionFormats(distributionFormatsFuture);
                distributionFutures.add(distributionFormatsFuture);

                CompositeFuture.all(distributionFutures).setHandler(completionHandler -> {
                    if (completionHandler.succeeded()) {
                        LOG.info("Finished fetching global distribution metrics");
                        reportValues.setDistributionsAccessUrl(getSetWithDynamicKeysAndMultipleValues(distributionAccessUrlFuture.result()));
                        reportValues.setDistributionsDownloadUrl(getSetWithDynamicKeysAndMultipleValues(distributionDownloadUrlFuture.result()));
                        reportValues.setDistributionsDownloadUrlExists(getSetWithBinaryValues(distributionsDownloadUrlExistsFuture.result()));
                        reportValues.setDistributionsStatusCodes(getSetWithFixedKeysAndMultipleValues(distributionStatusCodesFuture.result()));
                        reportValues.setDistributionsMachineReadability(getSetWithBinaryValues(distributionMachineReadabilityFuture.result()));
                        reportValues.setMostUsedDistributionFormats(getSetWithMultipleValuesSorted(distributionFormatsFuture.result(), chartElementLimit));
                    } else {
                        LOG.error("Failed to retrieve global distribution metrics: {}", completionHandler.cause());
                    }

                    distributionFuture.complete();
                });
            } else {
                distributionFuture.complete();
            }
        });

        Future violationFuture = Future.future();
        metricFutures.add(violationFuture);
        metricProvider.getRenderViolations(complianceHandler -> {
            boolean renderViolations = complianceHandler.succeeded() && complianceHandler.result().getBoolean("result");
            reportValues.setRenderViolations(renderViolations);

            if (renderViolations) {
                List<Future> violationFutures = new ArrayList<>();

                Future<JsonObject> datasetViolationFuture = Future.future();
                metricProvider.getDatasetViolations(datasetViolationFuture);
                violationFutures.add(datasetViolationFuture);

                Future<JsonObject> datasetComplianceFuture = Future.future();
                metricProvider.getDatasetCompliance(datasetComplianceFuture);
                violationFutures.add(datasetComplianceFuture);

                CompositeFuture.all(violationFutures).setHandler(completionHandler -> {
                    if (completionHandler.succeeded()) {
                        LOG.info("Finished fetching global violation metrics");
                        reportValues.setMostOccurredViolations(getSetWithFixedKeysAndMultipleValues(datasetViolationFuture.result()));
                        reportValues.setDatasetsCompliance(getSetWithBinaryValues(datasetComplianceFuture.result()));
                    } else {
                        LOG.error("Failed to retrieve global violation metrics: {}", completionHandler.cause());
                    }

                    violationFuture.complete();
                });
            } else {
                violationFuture.complete();
            }
        });

        Future licenceFuture = Future.future();
        metricFutures.add(licenceFuture);
        metricProvider.getRenderLicences(licenceHandler -> {
            boolean renderLicences = licenceHandler.succeeded() && licenceHandler.result().getBoolean("result");
            reportValues.setRenderLicences(renderLicences);

            if (renderLicences) {
                List<Future> licenceFutures = new ArrayList<>();

                Future<JsonObject> datasetKnownLicencesFuture = Future.future();
                metricProvider.getDatasetKnownLicences(datasetKnownLicencesFuture);
                licenceFutures.add(datasetKnownLicencesFuture);

                Future<JsonObject> datasetLicencesFuture = Future.future();
                metricProvider.getDatasetLicences(datasetLicencesFuture);
                licenceFutures.add(datasetLicencesFuture);

                CompositeFuture.all(licenceFutures).setHandler(completionHandler -> {
                    if (completionHandler.succeeded()) {
                        LOG.info("Finished fetching global licence metrics");
                        reportValues.setKnownLicencesPercentages(getSetWithBinaryValues(datasetKnownLicencesFuture.result()));
                        reportValues.setMostUsedLicences(getSetWithMultipleValuesSorted(datasetLicencesFuture.result(), chartElementLimit));
                    } else {
                        LOG.error("Failed to retrieve global licence metrics: {}", completionHandler.cause());
                    }

                    licenceFuture.complete();
                });
            } else {
                licenceFuture.complete();
            }
        });

        CompositeFuture.all(metricFutures).setHandler(handler -> {
            if (handler.succeeded()) {
                LOG.info("Finished fetching global metrics");
                completionFuture.complete(reportValues);
            } else {
                completionFuture.fail("Failed to retrieve global metrics");
            }
        });

        return completionFuture;
    }

    public Future<List<ReportValues>> getCatalogueReportValues() {
        LOG.info("Fetching catalogue metrics...");

        Future<List<ReportValues>> completionFuture = Future.future();

        metricProvider.getCatalogues(handler -> {
            if (handler.succeeded() && handler.result().getBoolean("success")) {

                // FIXME collect results in futures
                List<Future> catalogueFutures = new ArrayList<>();
//                List<ReportValues> catalogueValues = new ArrayList<>();

                handler.result().getJsonArray("result").forEach(catalogue -> {
                    String catalogueId = ((JsonObject) catalogue).getString("id");

                    Future<ReportValues> catalogueCompletionFuture = Future.future();
                    catalogueFutures.add(catalogueCompletionFuture);

                    List<Future> metricFutures = new ArrayList<>();

                    ReportValues reportValues = new ReportValues();
                    reportValues.setPageTitle(((JsonObject) catalogue).getString("title"));

                    Future distributionFuture = Future.future();
                    metricFutures.add(distributionFuture);
                    metricProvider.getRenderDistributions(catalogueId, distributionHandler -> {
                        boolean renderDistributions = distributionHandler.succeeded() && distributionHandler.result().getBoolean("result");
                        reportValues.setRenderDistributions(renderDistributions);

                        if (renderDistributions) {
                            List<Future> distributionFutures = new ArrayList<>();

                            Future<JsonObject> distributionAccessUrlFuture = Future.future();
                            metricProvider.getDistributionAccessibilityAccessUrl(catalogueId, distributionAccessUrlFuture);
                            distributionFutures.add(distributionAccessUrlFuture);

                            Future<JsonObject> distributionDownloadUrlFuture = Future.future();
                            metricProvider.getDistributionAccessibilityDownloadUrl(catalogueId, distributionDownloadUrlFuture);
                            distributionFutures.add(distributionDownloadUrlFuture);

                            Future<JsonObject> distributionStatusCodesFuture = Future.future();
                            metricProvider.getDistributionStatusCodes(catalogueId, distributionStatusCodesFuture);
                            distributionFutures.add(distributionStatusCodesFuture);

                            Future<JsonObject> distributionsDownloadUrlExistsFuture = Future.future();
                            metricProvider.getDistributionDownloadUrlExists(catalogueId, distributionsDownloadUrlExistsFuture);
                            distributionFutures.add(distributionsDownloadUrlExistsFuture);

                            Future<JsonObject> distributionMachineReadabilityFuture = Future.future();
                            metricProvider.getDistributionMachineReadability(catalogueId, distributionMachineReadabilityFuture);
                            distributionFutures.add(distributionMachineReadabilityFuture);

                            Future<JsonObject> distributionFormatsFuture = Future.future();
                            metricProvider.getDistributionFormats(catalogueId, distributionFormatsFuture);
                            distributionFutures.add(distributionFormatsFuture);

                            CompositeFuture.all(distributionFutures).setHandler(completionHandler -> {
                                if (completionHandler.succeeded()) {
                                    reportValues.setDistributionsAccessUrl(getSetWithDynamicKeysAndMultipleValues(distributionDownloadUrlFuture.result()));
                                    reportValues.setDistributionsDownloadUrl(getSetWithDynamicKeysAndMultipleValues(distributionDownloadUrlFuture.result()));
                                    reportValues.setDistributionsDownloadUrlExists(getSetWithBinaryValues(distributionsDownloadUrlExistsFuture.result()));
                                    reportValues.setDistributionsStatusCodes(getSetWithFixedKeysAndMultipleValues(distributionStatusCodesFuture.result()));
                                    reportValues.setDistributionsMachineReadability(getSetWithBinaryValues(distributionMachineReadabilityFuture.result()));
                                    reportValues.setMostUsedDistributionFormats(getSetWithMultipleValuesSorted(distributionFormatsFuture.result(), chartElementLimit));
                                } else {
                                    LOG.error("Failed to retrieve catalogue distribution metrics for catalogue with ID [{}] : {}", catalogueId, completionHandler.cause());
                                }

                                distributionFuture.complete();
                            });
                        } else {
                            distributionFuture.complete();
                        }
                    });

                    Future violationFuture = Future.future();
                    metricFutures.add(violationFuture);
                    metricProvider.getRenderViolations(catalogueId, complianceHandler -> {
                        boolean renderViolations = complianceHandler.succeeded() && complianceHandler.result().getBoolean("result");
                        reportValues.setRenderViolations(renderViolations);

                        if (renderViolations) {
                            List<Future> violationFutures = new ArrayList<>();

                            Future<JsonObject> datasetViolationFuture = Future.future();
                            metricProvider.getDatasetViolations(catalogueId, datasetViolationFuture);
                            violationFutures.add(datasetViolationFuture);

                            Future<JsonObject> datasetComplianceFuture = Future.future();
                            metricProvider.getDatasetCompliance(catalogueId, datasetComplianceFuture);
                            violationFutures.add(datasetComplianceFuture);

                            CompositeFuture.all(violationFutures).setHandler(completionHandler -> {
                                if (completionHandler.succeeded()) {
                                    reportValues.setMostOccurredViolations(getSetWithFixedKeysAndMultipleValues(datasetViolationFuture.result()));
                                    reportValues.setDatasetsCompliance(getSetWithBinaryValues(datasetComplianceFuture.result()));
                                } else {
                                    LOG.error("Failed to retrieve catalogue violation metrics for catalogue with ID [{}] : {}", catalogueId, completionHandler.cause());
                                }

                                violationFuture.complete();
                            });
                        } else {
                            violationFuture.complete();
                        }
                    });

                    Future licenceFuture = Future.future();
                    metricFutures.add(licenceFuture);
                    metricProvider.getRenderLicences(catalogueId, licenceHandler -> {
                        boolean renderLicences = licenceHandler.succeeded() && licenceHandler.result().getBoolean("result");
                        reportValues.setRenderLicences(renderLicences);

                        if (renderLicences) {
                            List<Future> licenceFutures = new ArrayList<>();

                            Future<JsonObject> datasetKnownLicencesFuture = Future.future();
                            metricProvider.getDatasetKnownLicences(catalogueId, datasetKnownLicencesFuture);
                            licenceFutures.add(datasetKnownLicencesFuture);

                            Future<JsonObject> datasetLicencesFuture = Future.future();
                            metricProvider.getDatasetLicences(catalogueId, datasetLicencesFuture);
                            licenceFutures.add(datasetLicencesFuture);

                            CompositeFuture.all(licenceFutures).setHandler(completionHandler -> {
                                if (completionHandler.succeeded()) {
                                    reportValues.setKnownLicencesPercentages(getSetWithBinaryValues(datasetKnownLicencesFuture.result()));
                                    reportValues.setMostUsedLicences(getSetWithMultipleValuesSorted(datasetLicencesFuture.result(), chartElementLimit));
                                } else {
                                    LOG.error("Failed to retrieve catalogue licence metrics for catalogue with ID [{}] : {}", catalogueId, completionHandler.cause());
                                }

                                licenceFuture.complete();
                            });
                        } else {
                            licenceFuture.complete();
                        }
                    });

                    CompositeFuture.all(metricFutures).setHandler(catalogueMetricCompletionFuture -> {
                        if (catalogueMetricCompletionFuture.succeeded()) {
                            catalogueCompletionFuture.complete(reportValues);
                        } else {
                            catalogueCompletionFuture.fail("Failed to retrieve catalogue metrics");
                        }
                    });
                });

                CompositeFuture.all(catalogueFutures).setHandler(catalogueFuturesHandler -> {
                   if (handler.succeeded()) {
                       LOG.info("Finished fetching metrics for [{}] catalogues", catalogueFutures.size());
                       List<ReportValues> catalogueValues = catalogueFutures.stream().map(future -> (ReportValues) future.result()).collect(Collectors.toList());
                       completionFuture.complete(catalogueValues);
                   } else {
                       completionFuture.fail("Failed to retrieve catalogue metrics: " + catalogueFuturesHandler.cause());
                   }
                });
            } else {
                completionFuture.fail("Failed to retrieve catalogue metrics: " + handler.cause());
            }
        });

        return completionFuture;
    }

    // intended for yes/no percentage pie charts
    private Set<ReportElement> getSetWithBinaryValues(JsonObject metric) {
        if (metric == null || !metric.getBoolean("success"))
            metric = getDefaultBinaryPercentages();

        TreeSet<ReportElement> result = new TreeSet<>();

        try {
            result.add(new ReportElement(Math.round(metric.getJsonObject("result").getDouble("yes")), "yes"));
            result.add(new ReportElement(Math.round(metric.getJsonObject("result").getDouble("no")), "no"));
        } catch (ClassCastException e) {
            LOG.warn("Invalid metric encountered: {}", metric.encode());
        }

        return result;
    }

    // intended for pie charts with multiple values, no ordering or limiting of size
    private Set<ReportElement> getSetWithFixedKeysAndMultipleValues(JsonObject metric) {
        if (metric == null || !metric.getBoolean("success"))
            metric = getDefaultListWithFixedKeysPercentages();

        Set<ReportElement> result = new TreeSet<>();
        metric.getJsonArray("result").forEach(entry ->
            result.add(new ReportElement(
                Math.round(((JsonObject) entry).getDouble("percentage")),
                ((JsonObject) entry).getString("name").replaceAll("^\"|\"$", "")))); // remove quotes from start & end

        return result;
    }

    // intended for pie charts with multiple values, no ordering or limiting of size
    private TreeSet<ReportElement> getSetWithDynamicKeysAndMultipleValues(JsonObject metric) {
        if (metric == null || !metric.getBoolean("success"))
            metric = getDefaultListWithDynamicKeysPercentages();

        TreeSet<ReportElement> result = new TreeSet<>();

        JsonObject jsonResult = metric.getJsonObject("result");
        jsonResult.fieldNames().forEach(key ->
            result.add(new ReportElement(Math.round(jsonResult.getDouble(key)), key)));

        return result;
    }

    // intended for sorted bar charts, applies a size limit
    private TreeSet<ReportElement> getSetWithMultipleValuesSorted(JsonObject metric, int limit) {
        if (metric == null || !metric.getBoolean("success"))
            metric = getDefaultListWithFixedKeysPercentages();

        List<ReportElement> result = new ArrayList<>();
        metric.getJsonArray("result").forEach((entry) ->
            result.add(new ReportElement(
                Math.round(((JsonObject) entry).getDouble("percentage")),
                ((JsonObject) entry).getString("name").replaceAll("^\"|\"$", "")))); // remove quotes from start & end

        result.sort((r1, r2) -> {
            if (r1.getValue() != r2.getValue())
                return Math.toIntExact(r2.getValue() - r1.getValue());

            return r1.getLabel().compareTo(r2.getLabel());
        });

        return new TreeSet<>(result.subList(0, limit < result.size() ? limit : result.size()));
    }

    private JsonObject getDefaultRenderResult() {
        return new JsonObject()
            .put("success", true)
            .put("result", false);
    }

    // return empty array
    private JsonObject getDefaultListWithFixedKeysPercentages() {
        return new JsonObject()
            .put("success", true)
            .put("result", new JsonArray());
    }

    // return empty object
    private JsonObject getDefaultListWithDynamicKeysPercentages() {
        return new JsonObject()
            .put("success", true)
            .put("result", new JsonObject());
    }

    // return 100 % 'no'
    private JsonObject getDefaultBinaryPercentages() {
        return new JsonObject()
            .put("success", true)
            .put("result", new JsonObject()
                .put("yes", 0.0)
                .put("no", 100.0));
    }
}
