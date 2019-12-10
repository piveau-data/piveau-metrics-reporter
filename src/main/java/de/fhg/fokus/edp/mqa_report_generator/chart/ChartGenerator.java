package de.fhg.fokus.edp.mqa_report_generator.chart;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import de.fhg.fokus.edp.mqa_report_generator.model.ReportElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ChartGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(ChartGenerator.class);

    private ChartBundle chartBundle;
    private String chartUrl;

    public ChartGenerator(String chartUrl) {
        this.chartUrl = chartUrl;
    }

    /**
     * Generates a chart png file using the export server set in the config
     * @param chartBundle an object containing all relevant data for generating a chart
     * @return In case of successful generation a file object of the resulting image, null otherwise
     */
    public File generateChartFile(ChartBundle chartBundle) {
        this.chartBundle = chartBundle;
        File chartFile;

        try {
            chartFile = File.createTempFile("chart_", ".png", null);
            return handleHighChartsCall(chartFile) ? chartFile : null;
        } catch (IOException e) {
            LOG.error("Failed to create tmp file", e);
            return null;
        }
    }

    // send a post request to chart render engine and return true in case of http response 200
    private boolean handleHighChartsCall(File chartFile) {
        boolean success = true;

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(chartUrl);
            request.addHeader("content-type", "application/json; charset=utf-8");
            request.setEntity(new StringEntity(generateJsonRequest(chartFile.getAbsolutePath())));
            HttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                success = false;
                LOG.warn("Sending POST to chart render engine returned status code " + response.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            LOG.error("Executing POST to chart render engine failed: " + e);
        }

        return success;
    }

    // creates the outer json structure required by highcharts render engine
    private String generateJsonRequest(String fileName) {
        String request = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JsonFactory jsonFactory = new JsonFactory();
            JsonGenerator jg = jsonFactory.createGenerator(baos);

            jg.writeStartObject();
            jg.writeStringField("infile", buildConfigString());
            jg.writeStringField("outfile", fileName);
            jg.writeEndObject();
            jg.close();

            request = baos.toString();
        } catch (IOException e) {
            LOG.warn("Could not generate JSON request for chart [{}]", chartBundle.getTitle(), e);
        }

        LOG.debug("Highcharts request is: " + request);

        return request;
    }

    // builds the inner config string containing the chart data
    private String buildConfigString() {
        // open config and add chart type
        StringBuilder sb = new StringBuilder("{chart: {type: \"")
                .append(chartBundle.getChartType().getLabel())
                .append("\"}, ");

        // chart title
        if (chartBundle.getTitle() != null && !chartBundle.getTitle().isEmpty())
            sb.append("title: {text: \"").append(chartBundle.getTitle()).append("\"}, ");

        switch (chartBundle.getChartType()) {
            case PIE:
                sb.append(buildPieChart());
                break;

            case BAR:
                sb.append(buildBarChart());
                break;

            case COLUMN:
                sb.append(buildColumnChart());
                break;
        }

        // close config
        return sb.append("}").toString();
    }

    private String buildPieChart() {
        // open series and data
        StringBuilder sb = new StringBuilder("series: [{data: [");

        // add data. each entry has the following format: {name: "test", y: 123}
        ArrayList<String> data = new ArrayList<>();
        for (Object reportElementBean : chartBundle.getDiagram()) {
            ReportElement element = (ReportElement) reportElementBean;
            String label = "{name: \"" + element.getLabel() + "\", ";
            String value = "y: " + element.getValue() + "}";
            data.add(label + value);
        }

        // close data and series
        sb.append(StringUtils.join(data, ", ")).append("]}]");
        return sb.toString();
    }

    private String buildBarChart() {
        // make xAxis label invisible (somehow in yAxis block??)
        StringBuilder sb = new StringBuilder("yAxis: {title: {text: \"\" }},");

        // collect data and labels for xAxis
        sb.append("xAxis: {categories: [");
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<String> data = new ArrayList<>();

        // store data and labels in same loop because set is unordered
        for (Object reportElementBean : chartBundle.getDiagram()) {
            ReportElement element = (ReportElement) reportElementBean;
            categories.add("\"" + element.getLabel() + "\"");
            data.add(String.valueOf(element.getValue()));
        }
        sb.append(StringUtils.join(categories, ", "));

        // close categories, open series and add name
        sb.append("]}, series: [{").append("name: \"").append(chartBundle.getValueName()).append("\", ");

        // add data collected earlier and close arrays
        sb.append("data: [").append(StringUtils.join(data, ", ")).append("]}]");
        return sb.toString();
    }

    private String buildColumnChart() {
        // add yAxis label
        StringBuilder sb = new StringBuilder("yAxis: {title: {text: \"").append(chartBundle.getyAxisLabel()).append("\" }},");

        // collect data and labels for xAxis
        sb.append("xAxis: { categories: [");
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<String> data = new ArrayList<>();

        // store data and labels in same loop because set is unordered
        for (Object reportElementBean : chartBundle.getDiagram()) {
            ReportElement element = (ReportElement) reportElementBean;
            categories.add("\"" + element.getLabel() + "\"");
            data.add(String.valueOf(element.getValue()));
        }
        sb.append(StringUtils.join(categories, ", "));

        // close categories, open series and add name
        sb.append("]}, series: [{name: \"").append(chartBundle.getValueName()).append("\", ");

        // add data collected earlier and close arrays
        sb.append("data: [").append(StringUtils.join(data, ", ")).append("]}]");
        return sb.toString();
    }
}
