package de.fhg.fokus.edp.mqa_report_generator;

import de.fhg.fokus.edp.mqa_report_generator.generator.OdsReportGenerator;
import de.fhg.fokus.edp.mqa_report_generator.generator.PdfReportGenerator;
import de.fhg.fokus.edp.mqa_report_generator.generator.XlsReportGenerator;
import de.fhg.fokus.edp.mqa_report_generator.metrics.MetricProviderImpl;
import de.fhg.fokus.edp.mqa_report_generator.metrics.MetricService;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportLabels;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

import static de.fhg.fokus.edp.mqa_report_generator.ApplicationConfig.*;

public class ReportGeneratorVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(ReportGeneratorVerticle.class);

    private MetricService metricService;

    private XlsReportGenerator xlsReportGenerator;
    private OdsReportGenerator odsReportGenerator;
    private PdfReportGenerator pdfReportGenerator;

    private JsonObject reportLabels;

    // TODO use Locale constants
    private final Locale[] languages = new Locale[] {
        new Locale("bg"),
        new Locale("cs"),
        new Locale("da"),
        new Locale("de"),
        new Locale("el"),
        new Locale("en"),
        new Locale("es"),
        new Locale("et"),
        new Locale("fi"),
        new Locale("fr"),
        new Locale("ga"),
        new Locale("hr"),
        new Locale("hu"),
        new Locale("it"),
        new Locale("lt"),
        new Locale("lv"),
        new Locale("mt"),
        new Locale("nl"),
        new Locale("no"),
        new Locale("pl"),
        new Locale("pt"),
        new Locale("ro"),
        new Locale("sk"),
        new Locale("sl"),
        new Locale("sv"),
    };

    @Override
    public void start(Future<Void> startFuture) {
        xlsReportGenerator = new XlsReportGenerator(vertx);
        odsReportGenerator = new OdsReportGenerator(vertx);
        pdfReportGenerator = new PdfReportGenerator(vertx, config().getString(ENV_CHART_ENDPOINT, DEFAULT_CHART_ENDPOINT));

        try {
            reportLabels = new JsonObject(getLanguageFile());

            vertx.eventBus().consumer(GENERATE_REPORT_ADDRESS, handler -> generateReports());

            String metricHost = config().getString(ENV_METRIC_HOST, DEFAULT_METRIC_HOST);
            Integer metricPort = config().getInteger(ENV_METRIC_PORT, DEFAULT_METRIC_PORT);

            metricService = new MetricService(new MetricProviderImpl(vertx, metricHost, metricPort),
                config().getInteger(ENV_METRIC_ELEMENT_LIMIT, DEFAULT_METRIC_ELEMENT_LIMIT));

            String reportDirectory = config().getString(ENV_REPORT_DIRECTORY, DEFAULT_REPORT_DIRECTORY);
            vertx.fileSystem().exists(reportDirectory, existsHandler -> {
                // create directory if it doesn't exist
                if (existsHandler.succeeded() && !existsHandler.result()) {
                    vertx.fileSystem().mkdirs(reportDirectory, directoryHandler -> {
                        if (directoryHandler.succeeded()) {
                            // generate reports once on startup
                            generateReports();
                            startFuture.complete();
                        } else {
                            startFuture.fail("Could not create directory [" + reportDirectory + "]");
                        }
                    });
                } else {
                    startFuture.complete();
                }
            });
        } catch (IOException | DecodeException e) {
            e.printStackTrace();
            startFuture.fail("Could not read language keys: " + e.getMessage());
        }
    }

    private void generateReports() {

        // FIXME phantomJS returns status 500, but works fine
        //  newer version of highcharts: https://github.com/highcharts/node-export-server

//        // check once if chart render engine required by PDF report is online
//        webClient.headAbs(config().getString(ENV_CHART_ENDPOINT, DEFAULT_CHART_ENDPOINT))
//            .expect(ResponsePredicate.SC_OK)
//            .send(chartEngineAvailabilityHandler ->

                getReportValues().setHandler(valueHandler -> {
                        if (valueHandler.succeeded()) {

                            Arrays.stream(languages).forEach(language -> {

                                ReportLabels labels = getReportLabels(language);

                                if (labels != null) {
                                    LOG.info("Successfully retrieved labels for language [{}]", language);

                                    List<ReportValues> reportValues = valueHandler.result();
                                    reportValues.get(0).setPageTitle(labels.getDashboardTitle()); // first in list is always dashboard values

//                                    if (chartEngineAvailabilityHandler.succeeded()) {
                                    if (true) {
                                        LOG.debug("Chart render engine available, generating PDF reports");
                                        pdfReportGenerator.generateReport(labels, reportValues).setHandler(pdfReportHandler -> {
                                            if (pdfReportHandler.failed())
                                                LOG.error("Generating PDF report for language [" + language.getLanguage() + "] failed: " + pdfReportHandler.cause());
                                        });
                                    } else {
                                        LOG.warn("Could not connect to chart render engine. Skipping PDF report");
                                    }

                                    // the ods report is based on the xls report
                                    xlsReportGenerator.generateReport(labels, reportValues).setHandler(xlsReportHandler -> {
                                        if (xlsReportHandler.succeeded()) {
                                            odsReportGenerator.generateReport(labels, reportValues).setHandler(odsReportHandler -> {
                                                if (odsReportHandler.failed())
                                                    LOG.error("Generating ODS report for language [" + language.getLanguage() + "] failed: " + odsReportHandler.cause());
                                            });
                                        } else {
                                            LOG.error("Generating XLS report for language [" + language.getLanguage() + "] failed: " + xlsReportHandler.cause());
                                        }});
                                } else {
                                    LOG.error("Could not find labels for language [{}]", language);
                                }
                            });
                        }
                    });
//                }));
    }

    private ReportLabels getReportLabels(Locale locale) throws MissingResourceException {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(locale);

        String currentDate = LocalDate.now().format(dateFormatter);

        JsonObject languageRoot = reportLabels.getJsonObject(locale.getLanguage().toLowerCase());

        if (languageRoot != null) {

            JsonObject msg = languageRoot.getJsonObject("message");

            ReportLabels labels = new ReportLabels();
            labels.setLanguage(locale);


            labels.setDashboardTitle(getKeySave(msg, new String[]{"common", "edp", "title"}));
            labels.setMqaTitle(getKeySave(msg, new String[]{"title"}));
            labels.setMqaIntro(getKeySave(msg, new String[]{"common", "edp", "description"}));
            labels.setEdpUrl("https://www.europeandataportal.eu");

            labels.setDashboardOverview(getKeySave(msg, new String[]{"dashboard", "overview", "headline"}));
            labels.setDashboardLastUpdate(currentDate);
            labels.setDashboardLastUpdateLabel(getKeySave(msg, new String[]{"footer", "lastUpdate"}));
            labels.setDashboardCurrentDate(currentDate);
            labels.setDashboardCurrentDateLabel(getKeySave(msg, new String[]{"common", "dateCreated"}));


            labels.setDistributionHeader(getKeySave(msg, new String[]{"dashboard", "accessibility", "headline"}));
            labels.setDistributionIntro(getKeySave(msg, new String[]{"dashboard", "accessibility", "description"}));
            labels.setDistributionsNotAvailable(getKeySave(msg, new String[]{"catalogue_detail", "distributions", "nodistributions"}));

            labels.setDistAccessibilityAccessUrlAll(getKeySave(msg, new String[]{"dashboard", "accessibility", "plots", "title", "accessibility_access_url"}));
            labels.setDistAccessibilityDownloadUrlAll(getKeySave(msg, new String[]{"dashboard", "accessibility", "plots", "title", "accessibility_download_url"}));
            labels.setDistStatusCodes(getKeySave(msg, new String[]{"dashboard", "accessibility", "plots", "title", "status_error_code"}));
            labels.setDistWithoutDownloadUrl(getKeySave(msg, new String[]{"dashboard", "accessibility", "plots", "title", "existing_download_url"}));
            labels.setDistMachineReadablePercentage(getKeySave(msg, new String[]{"dashboard", "machine_readability", "plots", "title", "ratio_of_mr"}));
            labels.setDistFormatMostUsed(getKeySave(msg, new String[]{"dashboard", "machine_readability", "plots", "title", "distribution_formats"}));


            labels.setViolationHeader(getKeySave(msg, new String[]{"dashboard", "dcat_ap_compliance", "headline"}));
            labels.setViolationIntro(getKeySave(msg, new String[]{"dashboard", "dcat_ap_compliance", "description"}));
            labels.setViolationsNotAvailable(getKeySave(msg, new String[]{"dashboard", "dcat_ap_compliance", "all_datasets_conform"}));

            labels.setViolationMostOccurred(getKeySave(msg, new String[]{"dashboard", "dcat_ap_compliance", "plots", "title", "dcat_ap_violation_places"}));
            labels.setViolationComplianceAll(getKeySave(msg, new String[]{"dashboard", "dcat_ap_compliance", "plots", "title", "dcat_ap_compliant"}));


            labels.setLicenceHeader(getKeySave(msg, new String[]{"dashboard", "licence_usage", "headline"}));
            labels.setLicenceIntro(getKeySave(msg, new String[]{"dashboard", "licence_usage", "description"}));
            labels.setLicencesNotAvailable(getKeySave(msg, new String[]{"dashboard", "licence_usage", "not_applicable"}));

            labels.setLicenceKnownPercentage(getKeySave(msg, new String[]{"dashboard", "licence_usage", "plots", "title", "licence_ratio"}));
            labels.setLicenceMostUsed(getKeySave(msg, new String[]{"dashboard", "licence_usage", "plots", "title", "most_used_licence"}));


            labels.setHelpOverallAccessibility(getKeySave(msg, new String[]{"help", "accessibility_url", "desc"}));
            labels.setHelpStatusCodes(getKeySave(msg, new String[]{"help", "statusErrorCodes", "desc"}));
            labels.setHelpDownloadUrlExists(getKeySave(msg, new String[]{"help", "downloadUrl", "desc"}));
            labels.setHelpMachineReadability(getKeySave(msg, new String[]{"help", "ratioMr", "desc"}));
            labels.setHelpFormatsMostUsed(getKeySave(msg, new String[]{"help", "formatsMr", "desc"}));
            labels.setHelpViolationOccurances(getKeySave(msg, new String[]{"help", "topViolations", "desc"}));
            labels.setHelpCompliantDatasets(getKeySave(msg, new String[]{"help", "dcatCompliance", "desc"}));
            labels.setHelpKnownLicences(getKeySave(msg, new String[]{"help", "ratioLicence", "desc"}));
            labels.setHelpLicencesMostUsed(getKeySave(msg, new String[]{"help", "usedLicence", "desc"}));


            labels.setAxisLabelFormats(getKeySave(msg, new String[]{"catalogue_detail", "distributions", "format", "name"}));
            labels.setAxisLabelLicences(getKeySave(msg, new String[]{"dashboard", "licence_usage", "name_singular"}));
            labels.setMetricLabelPercentage(getKeySave(msg, new String[]{"common", "percentage"}));
            labels.setMetricLabelYes(getKeySave(msg, new String[]{"common", "yes"}));
            labels.setMetricLabelNo(getKeySave(msg, new String[]{"common", "no"}));
            labels.setMetricLabelUnknown(getKeySave(msg, new String[]{"dashboard", "licence_usage", "plots", "unknown"}));
            labels.setReportTocHeader(getKeySave(msg, new String[]{"common", "toc"}));

            return labels;
        } else {
            return null;
        }
    }

    private Future<List<ReportValues>> getReportValues() {
        Future<List<ReportValues>> completionFuture = Future.future();

        Future<ReportValues> globalReportValues = metricService.getGlobalReportValues();
        Future<List<ReportValues>> catalogueReportValues = metricService.getCatalogueReportValues();

        CompositeFuture.all(globalReportValues, catalogueReportValues).setHandler(handler -> {
            List<ReportValues> reportValues = new ArrayList<>();

            if (handler.succeeded()) {
                reportValues.add(globalReportValues.result());
                reportValues.addAll(catalogueReportValues.result());

                LOG.info("Finished retrieving [{}] sets of report values", reportValues.size());
                completionFuture.complete(reportValues);
            } else {
                completionFuture.fail("Failed to retrieve some metrics, skipping report");
            }
        });

        return completionFuture;
    }

    private String getLanguageFile() throws IOException {

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("lang.json")) {
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            } else {
                throw new IOException("Language file InputStream is null");
            }
        }
    }

    private String getKeySave(JsonObject root, String[] keys) {
        JsonObject lastNode = getKeyRecursively(root, keys);

        if (lastNode != null) {
            String label = lastNode.getString(keys[keys.length - 1]);
            return label != null ? label : "Key not found";
        } else {
            LOG.warn("Failed to find key(s) for [{}]", Arrays.asList(keys));
            return "Key not found";
        }
    }

    private JsonObject getKeyRecursively(JsonObject root, String[] keys) {

        if (root == null)
            return null;

        if (keys.length == 1)
            return root;

        return getKeyRecursively(root.getJsonObject(keys[0]), Arrays.copyOfRange(keys, 1, keys.length));
    }
}
