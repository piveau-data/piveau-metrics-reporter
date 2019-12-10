package de.fhg.fokus.edp.mqa_report_generator.generator;

import de.fhg.fokus.edp.mqa_report_generator.model.ReportFormat;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportLabels;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportValues;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class OdsReportGenerator extends ReportGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(OdsReportGenerator.class);

    public OdsReportGenerator(Vertx vertx) {
        super(vertx);
    }

    @Override
    public Future<Void> generateReport(ReportLabels reportLabels, List<ReportValues> reportValues) {
        LOG.debug("Generating ODS report for language [{}]", reportLabels.getLanguage());

        Future<Void> completionFuture = Future.future();

        vertx.executeBlocking(workHandler -> {
            try {
                // read xls file
                File xlsFile = getFileName(ReportFormat.XLS, reportLabels.getLanguage()).toFile();
                NPOIFSFileSystem fs = new NPOIFSFileSystem(xlsFile);
                HSSFWorkbook xlsReport = new HSSFWorkbook(fs.getRoot(), true);

                // get row and column counts
                int maxRowCount = 0;
                int maxColumnCount = 0;
                for (org.apache.poi.ss.usermodel.Sheet sheet : xlsReport) {
                    maxRowCount = sheet.getLastRowNum() + 1 > maxRowCount ? sheet.getLastRowNum() + 1 : maxRowCount;

                    for (Row row : sheet)
                        maxColumnCount = row.getLastCellNum() > maxColumnCount ? row.getLastCellNum() : maxColumnCount;
                }

                // create ods workbook
                SpreadSheet odsReport = SpreadSheet.create(xlsReport.getNumberOfSheets(), maxColumnCount, maxRowCount);

                // copy each sheet to ods file
                for (int sheetNumber = 0; sheetNumber < xlsReport.getNumberOfSheets(); sheetNumber++) {
                    org.apache.poi.ss.usermodel.Sheet xlsSheet = xlsReport.getSheetAt(sheetNumber);
                    Sheet odsSheet = odsReport.getSheet(sheetNumber);
                    odsSheet.setName(xlsSheet.getSheetName());

                    for (Row row : xlsSheet) {
                        for (int columnNumber = row.getFirstCellNum(); columnNumber < row.getLastCellNum(); columnNumber++) {
                            // copy cell value to according xls cell
                            odsSheet.setValueAt(row.getCell(columnNumber).getStringCellValue(), columnNumber, row.getRowNum());
                        }
                    }
                }

                // Save the report to an ODS tmp file
                Path fileName = getFileName(ReportFormat.ODS, reportLabels.getLanguage());
                odsReport.saveAs(fileName.toFile());
                workHandler.complete();
            } catch (IOException e) {
                workHandler.fail("Failed to generate ODS report for language [" + reportLabels.getLanguage() + "] : " + e.getMessage());
            }
        }, resultHandler -> {
            if (resultHandler.succeeded()) {
                LOG.info("Created ODS report file: [{}]", getFileName(ReportFormat.ODS, reportLabels.getLanguage()).toAbsolutePath());
                completionFuture.complete();
            } else {
                completionFuture.fail(resultHandler.cause());
            }
        });

        return completionFuture;
    }
}
