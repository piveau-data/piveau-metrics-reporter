package de.fhg.fokus.edp.mqa_report_generator.chart;

import de.fhg.fokus.edp.mqa_report_generator.model.ReportElement;

import java.util.Set;

/**
 * Created by fritz on 17.11.16.
 */
public class ChartBundle {
    private String title, yAxisLabel, valueName;
    private ChartType chartType;
    private Set<ReportElement> diagram;

    private ChartBundle (Builder builder) {
        this.diagram = builder.diagram;
        this.chartType = builder.chartType;
        this.title = builder.title;
        this.yAxisLabel = builder.yAxisLabel;
        this.valueName = builder.valueName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getyAxisLabel() {
        return yAxisLabel;
    }

    public void setyAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public Set<ReportElement> getDiagram() {
        return diagram;
    }

    public void setDiagram(Set<ReportElement> diagram) {
        this.diagram = diagram;
    }

    public static class Builder {
        // mandatory
        private Set<ReportElement> diagram;
        private ChartType chartType;

        // optional
        private String title, yAxisLabel, valueName;

        public Builder(Set<ReportElement> diagram, ChartType chartType) {
            this.diagram = diagram;
            this.chartType = chartType;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder yAxisLabel(String yAxisLabel) {
            this.yAxisLabel = yAxisLabel;
            return this;
        }

        public Builder valueName(String valueName) {
            this.valueName = valueName;
            return this;
        }

        public ChartBundle build() {
            return new ChartBundle(this);
        }
    }

    public enum ChartType {
        PIE("pie"),         // requires title
        BAR("bar"),         // requires title and valueName
        COLUMN("column");   // requires title, valueName, and yAxisLabel

        private final String label;

        ChartType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
