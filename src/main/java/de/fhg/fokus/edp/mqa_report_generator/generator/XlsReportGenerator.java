package de.fhg.fokus.edp.mqa_report_generator.generator;

import de.fhg.fokus.edp.mqa_report_generator.model.ReportElement;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportFormat;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportLabels;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportValues;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class XlsReportGenerator extends ReportGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(XlsReportGenerator.class);

    public XlsReportGenerator(Vertx vertx) {
        super(vertx);
    }

    @Override
    public Future<Void> generateReport(ReportLabels reportLabels, List<ReportValues> reportValues) {
        LOG.debug("Generating XLS report for language [{}]", reportLabels.getLanguage());

        Future<Void> completionFuture = Future.future();

        vertx.executeBlocking(workHandler -> {
            // create workbook
            Workbook xlsReport = new HSSFWorkbook();

            for (ReportValues report : reportValues) {
                // rid catalogue name of special chars
                String pageTitle = WorkbookUtil.createSafeSheetName(report.getPageTitle());
                Sheet sheet = xlsReport.createSheet(pageTitle);
                sheet.createRow(0).createCell(0).setCellValue(pageTitle);

                Row runRow = sheet.createRow(1);
                runRow.createCell(0).setCellValue(reportLabels.getDashboardLastUpdateLabel());
                runRow.createCell(1).setCellValue(reportLabels.getDashboardLastUpdate());

                Row dateRow = sheet.createRow(2);
                dateRow.createCell(0).setCellValue(reportLabels.getDashboardCurrentDateLabel());
                dateRow.createCell(1).setCellValue(reportLabels.getDashboardCurrentDate());

                // store offset to prevent empty blocks when not all data is available
                int rowOffset = 6;

                // distribution statistics
                if (report.getRenderDistributions()) {

                    if (!report.getDistributionsAccessUrl().isEmpty()) {
                        writeXlsLine(sheet, rowOffset, report.getDistributionsAccessUrl(), reportLabels.getDistAccessibilityAccessUrlAll(), "%", reportLabels);
                        rowOffset += 4;
                    }

                    if (!report.getDistributionsDownloadUrl().isEmpty()) {
                        writeXlsLine(sheet, rowOffset, report.getDistributionsDownloadUrl(), reportLabels.getDistAccessibilityDownloadUrlAll(), "%", reportLabels);
                        rowOffset += 4;
                    }

                    if (!report.getDistributionsStatusCodes().isEmpty()) {
                        writeXlsLine(sheet, rowOffset, report.getDistributionsStatusCodes(), reportLabels.getDistStatusCodes(), null, reportLabels);
                        rowOffset += 4;
                    }

                    if (!report.getDistributionsDownloadUrlExists().isEmpty()) {
                        writeXlsLine(sheet, rowOffset, report.getDistributionsDownloadUrlExists(), reportLabels.getDistWithoutDownloadUrl(), "%", reportLabels);
                        rowOffset += 4;
                    }

                    if (!report.getDistributionsMachineReadability().isEmpty()) {
                        writeXlsLine(sheet, rowOffset, report.getDistributionsMachineReadability(), reportLabels.getDistMachineReadablePercentage(), "%", reportLabels);
                        rowOffset += 4;
                    }

                    if (!report.getMostUsedDistributionFormats().isEmpty()) {
                        writeXlsLine(sheet, rowOffset, report.getMostUsedDistributionFormats(), reportLabels.getDistFormatMostUsed(), "%", reportLabels);
                        rowOffset += 4;
                    }
                }

                // Violations statistics
                if (report.getRenderViolations()) {
                    if (!report.getMostOccurredViolations().isEmpty()) {
                        writeXlsLine(sheet, rowOffset, report.getMostOccurredViolations(), reportLabels.getViolationMostOccurred(), null, reportLabels);
                        rowOffset += 4;
                    }

                    if (!report.getDatasetsCompliance().isEmpty()) {
                        writeXlsLine(sheet, rowOffset, report.getDatasetsCompliance(), reportLabels.getViolationComplianceAll(), "%", reportLabels);
                        rowOffset += 4;
                    }
                }

                // Licence statistics
                if (report.getRenderLicences()) {
                    if (!report.getKnownLicencesPercentages().isEmpty()) {
                        writeXlsLine(sheet, rowOffset, report.getKnownLicencesPercentages(), reportLabels.getLicenceKnownPercentage(), "%", reportLabels);
                        rowOffset += 4;
                    }

                    if (!report.getMostUsedLicences().isEmpty()) {
                        writeXlsLine(sheet, rowOffset, report.getMostUsedLicences(), reportLabels.getLicenceMostUsed(), "%", reportLabels);
                    }
                }
            }

            try {
                // save workbook to disk
                Path fileName = getFileName(ReportFormat.XLS, reportLabels.getLanguage());
                FileOutputStream fileOut = new FileOutputStream(fileName.toFile());
                xlsReport.write(fileOut);
                fileOut.close();

                workHandler.complete();
            } catch (IOException e) {
                workHandler.fail("Failed to generate XLS report for language [" + reportLabels.getLanguage() + "] : " + e.getMessage());
            }
        }, resultHandler -> {
            if (resultHandler.succeeded()) {
                LOG.info("Created XLS report file: [{}]", getFileName(ReportFormat.XLS, reportLabels.getLanguage()).toAbsolutePath());
                completionFuture.complete();
            } else {
                completionFuture.fail(resultHandler.cause());
            }
        });

        return completionFuture;
    }

    private void writeXlsLine(org.apache.poi.ss.usermodel.Sheet sheet, int startRow, Set<ReportElement> reportElements, String description, String unit, ReportLabels reportLabels) {

        int startCol = 0;

        // set description
        sheet.createRow(startRow).createCell(startCol).setCellValue(description);

        if (unit == null || unit.isEmpty())
            unit = "";

        // set values
        Row labelRow = sheet.createRow(startRow + 1);
        Row valueRow = sheet.createRow(startRow + 2);

        for (ReportElement reportElement : reportElements) {
            labelRow.createCell(startCol).setCellValue(translateLabel(reportElement.getLabel(), reportLabels));
            valueRow.createCell(startCol).setCellValue(reportElement.getValue() + " " + unit);
            startCol++;
        }
    }
}
