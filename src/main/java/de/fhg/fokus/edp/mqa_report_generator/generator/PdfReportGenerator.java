package de.fhg.fokus.edp.mqa_report_generator.generator;

import com.google.common.io.ByteStreams;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import de.fhg.fokus.edp.mqa_report_generator.chart.ChartBundle;
import de.fhg.fokus.edp.mqa_report_generator.chart.ChartGenerator;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportFormat;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportLabels;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportValues;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;


public class PdfReportGenerator extends ReportGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(PdfReportGenerator.class);

    private ChartGenerator chartGenerator;

    private PdfDocument pdfReport;
    private Document document;
    private PageSize pageSize;

    private List<AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, Integer>>> toc;

    private final static String EDP_LOGO = "mqa_report_header.png";

    private PdfFont bold;

    private String edpUrl;

    public PdfReportGenerator(Vertx vertx, String chartUrl) {
        super(vertx);
        chartGenerator = new ChartGenerator(chartUrl);
    }

    @Override
    public Future<Void> generateReport(ReportLabels reportLabels, List<ReportValues> reportValues) {
        LOG.info("Generating PDF report for language [{}]", reportLabels.getLanguage());

        Future<Void> completionFuture = Future.future();
        edpUrl = reportLabels.getEdpUrl();

        vertx.executeBlocking(workHandler -> {
            try {
                Path reportFile = getFileName(ReportFormat.PDF, reportLabels.getLanguage());

                // pdf config
                bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);

                // variables
                toc = new ArrayList<>();

                // create pdf file
                PdfWriter pdfWriter = new PdfWriter(reportFile.toAbsolutePath().toString(),
                    new WriterProperties().addXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7));

                pdfReport = new PdfDocument(pdfWriter);
                pageSize = new PageSize(PageSize.A4);
                document = new Document(pdfReport);
                document.setMargins(60, 60, 60, 60);

                // set meta data
                PdfDocumentInfo info = pdfReport.getDocumentInfo();
                info.setTitle(reportLabels.getDashboardTitle());
                info.setAuthor(reportLabels.getDashboardTitle());
//                info.setKeywords(rb.getString("report.pdf.keywords"));

                // watermark
                //IEventHandler handler = new TransparentImage();
                //pdfReport.addEventHandler(PdfDocumentEvent.START_PAGE, handler);

                // header and footer
                pdfReport.addEventHandler(PdfDocumentEvent.START_PAGE, new Header(reportLabels.getDashboardTitle()));
                PageXofY pageNumberEvent = new PageXofY();
                pdfReport.addEventHandler(PdfDocumentEvent.END_PAGE, pageNumberEvent);

                // create title page
                createTitlePage(reportLabels);

                // create info page
                createInfoPage(reportLabels);

                // create metric pages
                reportValues.forEach(value ->
                    createPages(reportLabels, value));

                // generate toc and page numbers
                createPdfToc(reportLabels);
                pageNumberEvent.writeTotal(pdfReport);

                //Close document
                document.close();
                pdfReport.close();

                workHandler.complete();
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error("Failed: {}", e.getMessage());
                workHandler.fail("Failed to generate PDF report for language [" + reportLabels.getLanguage() + "] : " + e.getMessage());
            }

        }, resultHandler -> {
            if (resultHandler.succeeded()) {
                LOG.info("Created PDF report file: [{}]", getFileName(ReportFormat.PDF, reportLabels.getLanguage()).toAbsolutePath());
                completionFuture.complete();
            } else {
                completionFuture.fail(resultHandler.cause());
            }
        });

        return completionFuture;
    }

    /**
     * Creates the first report page. If the image which is meant to appear in the center of the page
     * cannot be loaded, a text message is rendered instead.
     */
    private void createTitlePage(ReportLabels reportLabels) {
        Paragraph p = new Paragraph();

        Image edpLogo = createImgFromFile(EDP_LOGO);
        if (edpLogo != null) {
            edpLogo.setAutoScale(true);
            p.add(edpLogo);
            p.add(reportLabels.getDashboardTitle()).setFontSize(16).setTextAlignment(TextAlignment.CENTER);
            document.add(p.setVerticalAlignment(VerticalAlignment.MIDDLE));
        }

        /*
        // add date of last run to bottom right of page
        p = new Paragraph(dashboardLastUpdate + ": " + lastRun)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.BOTTOM)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(p);
        */


        // add current date to title page
        p = new Paragraph(reportLabels.getDashboardCurrentDateLabel() + ": " + reportLabels.getDashboardCurrentDate())
                .setTextAlignment(TextAlignment.CENTER);
        document.add(p);

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
    }

    // generates a new page which contains introductory text about the available statistics
    private void createInfoPage(ReportLabels reportLabels) {
        newTocEntry(reportLabels.getMqaTitle());
        addIntroText(reportLabels.getMqaTitle(), reportLabels.getMqaIntro());
    }

    // generates the pages needed to fit all diagrams belonging to a catalog
    private void createPages(ReportLabels reportLabels, ReportValues reportValues) {
        newTocEntry(reportValues.getPageTitle());

        Paragraph p = new Paragraph(reportValues.getPageTitle())
                .setFontSize(16)
                .setFont(bold)
                .setTextAlignment(TextAlignment.CENTER)
                .setKeepWithNext(true);
        p.setKeepTogether(true); // not needed?;

        document.add(p);

        ChartBundle bundle, left, right;

        // generates the pages needed to fit all diagrams from the MQAs general dashboard
        addDataHeader(reportLabels.getDistributionHeader());

        if (reportValues.getRenderDistributions()) {
            bundle = new ChartBundle.Builder(translateLabels(reportValues.getDistributionsAccessUrl(), reportLabels), ChartBundle.ChartType.PIE)
                    .title(reportLabels.getDistAccessibilityAccessUrlAll()).build();

            if (!reportValues.getDistributionsStatusCodes().isEmpty()) {
                right = new ChartBundle.Builder(translateLabels(reportValues.getDistributionsStatusCodes(), reportLabels), ChartBundle.ChartType.PIE)
                        .title(reportLabels.getDistStatusCodes()).build();
                addTwoChartsWithTextInColumns(reportLabels.getHelpOverallAccessibility(), reportLabels.getHelpStatusCodes(), bundle, right, 1, 1);
            } else {
                addFullWidthChart(bundle, reportLabels.getHelpOverallAccessibility());
            }

            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

            left = new ChartBundle.Builder(translateLabels(reportValues.getDistributionsDownloadUrlExists(), reportLabels), ChartBundle.ChartType.PIE)
                    .title(reportLabels.getDistWithoutDownloadUrl()).build();
            right = new ChartBundle.Builder(translateLabels(reportValues.getDistributionsMachineReadability(), reportLabels), ChartBundle.ChartType.PIE)
                    .title(reportLabels.getDistMachineReadablePercentage()).build();
            addTwoChartsWithTextInColumns(reportLabels.getHelpDownloadUrlExists(), reportLabels.getHelpMachineReadability(), left, right, 1, 1);

            bundle = new ChartBundle.Builder(translateLabels(reportValues.getMostUsedDistributionFormats(), reportLabels), ChartBundle.ChartType.COLUMN)
                    .title(reportLabels.getDistFormatMostUsed())
                    .yAxisLabel(reportLabels.getMetricLabelPercentage())
                    .valueName(reportLabels.getAxisLabelFormats())
                    .build();
            addFullWidthChart(bundle, reportLabels.getHelpFormatsMostUsed());

        } else {
            addSimpleTextBlock(reportLabels.getDistributionsNotAvailable());
        }
        document.add(new Paragraph());

        // Violations statistics
        addDataHeader(reportLabels.getViolationHeader());

        if (reportValues.getRenderViolations()) {
            left = new ChartBundle.Builder(translateLabels(reportValues.getMostOccurredViolations(), reportLabels), ChartBundle.ChartType.PIE)
                    .title(reportLabels.getViolationMostOccurred()).build();
            right = new ChartBundle.Builder(translateLabels(reportValues.getDatasetsCompliance(), reportLabels), ChartBundle.ChartType.PIE)
                    .title(reportLabels.getViolationComplianceAll()).build();
            addTwoChartsWithTextInColumns(reportLabels.getHelpViolationOccurances(), reportLabels.getHelpCompliantDatasets(), left, right, 1, 1);
        } else {
            addSimpleTextBlock(reportLabels.getViolationsNotAvailable());
        }

        // Licence statistics
        addDataHeader(reportLabels.getLicenceHeader());

        if (reportValues.getRenderLicences()) {
            left = new ChartBundle.Builder(translateLabels(reportValues.getKnownLicencesPercentages(), reportLabels), ChartBundle.ChartType.PIE)
                    .title(reportLabels.getLicenceKnownPercentage()).build();
            right = new ChartBundle.Builder(translateLabels(reportValues.getMostUsedLicences(), reportLabels), ChartBundle.ChartType.COLUMN)
                    .title(reportLabels.getLicenceMostUsed()).yAxisLabel(reportLabels.getMetricLabelPercentage()).valueName(reportLabels.getAxisLabelLicences()).build();
            addTwoChartsWithTextInColumns(reportLabels.getHelpKnownLicences(), reportLabels.getHelpLicencesMostUsed(), left, right, 1, 2);
        } else {
            addSimpleTextBlock(reportLabels.getLicencesNotAvailable());
        }

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
    }

    // helper method for adding text to the intro page
    private void addIntroText(String title, String text) {
        addDataHeader(title);
        addSimpleTextBlock(htmlToText(text));
    }

    // helper method for setting headers separating the various data blocks
    private void addDataHeader(String text) {
        Paragraph p = new Paragraph(text).setFontSize(14).setKeepWithNext(true);
        document.add(p);
    }

    // helper method for adding regular blocks of text
    private void addSimpleTextBlock(String text) {
        Paragraph p = new Paragraph(text);
        p.setFontSize(12);
        document.add(p);
        newLine();
    }

    /**
     * Adds a new, borderless table to the document containing four cells (2x2).
     * Ratios can be set to allow for dynamic spacing
     *
     * @param textLeft   the text visible in the upper left cell
     * @param textRight  the text visible in the upper right cell
     * @param left       the image visible in the bottom left cell
     * @param right      the image visible in the bottom right cell
     * @param ratioLeft  the weight the left column will have
     * @param ratioRight the weight the right column will have
     */
    private void addTwoChartsWithTextInColumns(String textLeft, String textRight, ChartBundle left, ChartBundle right, int ratioLeft, int ratioRight) {
        // calculate ratio values
        int totalWidth = ratioLeft + ratioRight;
        float leftWidth = (pageSize.getWidth() / totalWidth) * ratioLeft;
        float rightWidth = (pageSize.getWidth() / totalWidth) * ratioRight;

        // initAllLanguages table
        Table table = new Table(new float[]{leftWidth, rightWidth});
        table.setWidthPercent(100).setBorder(Border.NO_BORDER);

        // add content to table
        table.addCell(new Cell().add(htmlToText(textLeft)).setMargin(10).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(htmlToText(textRight)).setMargin(10).setBorder(Border.NO_BORDER));

        Image leftImage = generateImage(left);
        Image rightImage = generateImage(right);

        if (leftImage != null) {
            leftImage.setAutoScale(true);
            table.addCell(new Cell().add(leftImage).setBorder(Border.NO_BORDER));
        }

        if (rightImage != null) {
            rightImage.setAutoScale(true);
            table.addCell(new Cell().add(rightImage).setBorder(Border.NO_BORDER));
        }

        document.add(table);
        newLine();
        newLine();
    }

    // adds text and an image spanning the whole width of the document
    private void addFullWidthChart(ChartBundle bundle, String text) {
        document.add(new Paragraph(text));

        Image image = generateImage(bundle);

        if (image != null)
            document.add(generateImage(bundle).setAutoScale(true));

        newLine();
    }

    // generates an Image object from a ChartBundle, using the ChartGenerator class
    private Image generateImage(ChartBundle bundle) {
        File imgFile = chartGenerator.generateChartFile(bundle);
        Image img = null;

        if (imgFile != null) {
            try {
                img = new Image(ImageDataFactory.create(imgFile.getAbsolutePath()));

                // clean up
                Files.delete(Paths.get(imgFile.getAbsolutePath()));
            } catch (Exception e) {
                LOG.warn("Conversion of chart file to iText image failed for chart with title [{}].", bundle.getTitle(), e.getMessage());
            }
        }
        return img;
    }

    // adds a new entry to the table of contents, using the entry name specified
    private void newTocEntry(String tocAnchor) {
        Paragraph p = new Paragraph("");
        AbstractMap.SimpleEntry<String, Integer> page = new AbstractMap.SimpleEntry<>(tocAnchor, pdfReport.getNumberOfPages());
        p.setDestination(tocAnchor).setNextRenderer(new UpdatePageRenderer(p, page));
        document.add(p);
        toc.add(new AbstractMap.SimpleEntry<>(tocAnchor, page));
    }

    // generates the table of contents for the document. Must be called after all other pages have been added.
    private void createPdfToc(ReportLabels reportLabels) throws IOException {
        Paragraph p = new Paragraph().setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD))
                .add(reportLabels.getReportTocHeader()).setDestination("toc");
        document.add(p);

        toc.remove(0);
        List<TabStop> tabStops = new ArrayList<>();
        tabStops.add(new TabStop(580, TabAlignment.RIGHT, new DottedLine()));
        for (AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, Integer>> entry : toc) {
            AbstractMap.SimpleEntry<String, Integer> text = entry.getValue();
            p = new Paragraph()
                    .addTabStops(tabStops)
                    .add(text.getKey())
                    .add(new Tab())
                    .add(String.valueOf(text.getValue()))
                    .setAction(PdfAction.createGoTo(entry.getKey()));
            document.add(p);
        }
    }

    // removes html tags from strings
    private String htmlToText(String htmlText) {
        return Jsoup.parse(htmlText).text();
    }

    // adds an empty paragraph which to enable spacing
    private void newLine() {
        document.add(new Paragraph(" "));
    }

    // creates img object from resource file
    private Image createImgFromFile(String path) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {

            if (inputStream != null) {
                // convert file to byte array required for next step
                byte[] img = ByteStreams.toByteArray(inputStream);

                // create image file and add to paragraph
                return new Image(ImageDataFactory.create(img));
            }

        } catch (IOException e) {
            LOG.warn("Could not load Image from [{}]", path);
        }

        return null;
    }

    // required for the table of contents
    private class UpdatePageRenderer extends ParagraphRenderer {
        AbstractMap.SimpleEntry<String, Integer> entry;

        UpdatePageRenderer(Paragraph modelElement, AbstractMap.SimpleEntry<String, Integer> entry) {
            super(modelElement);
            this.entry = entry;
        }

        @Override
        public LayoutResult layout(LayoutContext layoutContext) {
            LayoutResult result = super.layout(layoutContext);
            entry.setValue(layoutContext.getArea().getPageNumber());
            return result;
        }
    }

    protected class Header implements IEventHandler {
        String header;

        Header(String header) {
            this.header = header;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();

            if (pdf.getPageNumber(page) > 1) {
                Rectangle pageSize = page.getPageSize();
                PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
                Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);
                canvas.showTextAligned(header,
                        pageSize.getWidth() / 2,
                        pageSize.getTop() - 30, TextAlignment.CENTER);
            }
        }
    }

    // handles displaying of page numbers
    protected class PageXofY implements IEventHandler {

        protected PdfFormXObject placeholder;
        protected float side = 20;
        protected float xUrl = 50;
        protected float xPageNumber = 500;
        protected float y = 25;
        protected float space = 4.5f;
        protected float descent = 3;

        public PageXofY() {
            placeholder = new PdfFormXObject(new Rectangle(0, 0, side, side));
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();

            int pageNumber = pdf.getPageNumber(page);
            if (pageNumber > 2) {
                Rectangle pageSize = page.getPageSize();
                PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
                Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);

                // edp url
                Paragraph p = new Paragraph(edpUrl);
                canvas.showTextAligned(p, xUrl, y, TextAlignment.LEFT);

                // page xPageNumber of y
                p = new Paragraph().add(String.valueOf(pageNumber)).add(" /");
                canvas.showTextAligned(p, xPageNumber, y, TextAlignment.RIGHT);
                pdfCanvas.addXObject(placeholder, xPageNumber + space, y - descent);

                pdfCanvas.release();
            }
        }

        public void writeTotal(PdfDocument pdf) {
            Canvas canvas = new Canvas(placeholder, pdf);
            canvas.showTextAligned(String.valueOf(pdf.getNumberOfPages()),
                    0, descent, TextAlignment.LEFT);
        }
    }

    // Add EDP watermark to pages
    protected class TransparentImage implements IEventHandler {

        protected PdfExtGState gState;
        protected Image img;

        public TransparentImage() {
            img = createImgFromFile(EDP_LOGO);
            gState = new PdfExtGState().setFillOpacity(0.2f);
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();

            if (pdf.getPageNumber(page) > 1) {
                PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
                pdfCanvas.saveState().setExtGState(gState);
                Canvas canvas = new Canvas(pdfCanvas, pdf, page.getPageSize());
                canvas.add(img.setAutoScale(true));
                pdfCanvas.restoreState();
                pdfCanvas.release();
            }
        }
    }
}
