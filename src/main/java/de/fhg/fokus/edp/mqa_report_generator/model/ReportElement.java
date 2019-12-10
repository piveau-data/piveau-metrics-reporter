package de.fhg.fokus.edp.mqa_report_generator.model;

public class ReportElement implements Comparable<ReportElement> {

    private Long value;
    private String label;

    public ReportElement(Long value, String label) {
        this.value = value;
        this.label = label;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int compareTo(ReportElement other) {
        if (!this.getValue().equals(other.getValue()))
            return Math.toIntExact(other.getValue() - this.getValue());

        return this.getLabel().compareTo(other.getLabel());
    }
}
