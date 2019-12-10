package de.fhg.fokus.edp.mqa_report_generator.generator;

import de.fhg.fokus.edp.mqa_report_generator.model.ReportElement;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportFormat;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportLabels;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportValues;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static de.fhg.fokus.edp.mqa_report_generator.ApplicationConfig.DEFAULT_REPORT_DIRECTORY;
import static de.fhg.fokus.edp.mqa_report_generator.ApplicationConfig.ENV_REPORT_DIRECTORY;

public abstract class ReportGenerator {

    Vertx vertx;

    private String reportDirectory;

    ReportGenerator(Vertx vertx) {
        this.vertx = vertx;
        this.reportDirectory = vertx.getOrCreateContext().config().getString(ENV_REPORT_DIRECTORY, DEFAULT_REPORT_DIRECTORY);
    }

    Path getFileName(ReportFormat format, Locale locale) {
        return Paths.get(reportDirectory, String.format("mqa-report_%s.%s", locale.getLanguage().toUpperCase(), format.name().toLowerCase()));
    }

    String translateLabel(String label, ReportLabels reportLabels) {
        switch (label) {
            case "yes":
                return reportLabels.getMetricLabelYes();
            case "no":
                return reportLabels.getMetricLabelNo();
            case "unknown":
                return reportLabels.getMetricLabelUnknown();
            default:
                return label;
        }
    }

    Set<ReportElement> translateLabels(Set<ReportElement> reportElements, ReportLabels reportLabels) {
        return reportElements.stream().map(reportElement ->
            new ReportElement(reportElement.getValue(), translateLabel(reportElement.getLabel(), reportLabels)))
            .collect(Collectors.toSet());
    }

    public abstract Future<Void> generateReport(ReportLabels reportLabels, List<ReportValues> reportValues);
}
