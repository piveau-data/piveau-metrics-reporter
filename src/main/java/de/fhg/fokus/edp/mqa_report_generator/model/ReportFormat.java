package de.fhg.fokus.edp.mqa_report_generator.model;

public enum ReportFormat {
    ODS("Open Document Spreadsheet (.ods)", "application/vnd.oasis.opendocument.spreadsheet"),
    XLS("Microsoft Excel (.xls)", "application/vnd.ms-excel"),
    PDF("Portable Document Format (.pdf)", "application/pdf");

    private final String label;
    private final String mimeType;

    ReportFormat(String label, String mimeType) {
        this.label = label;
        this.mimeType = mimeType;
    }

    public String getLabel() {
        return label;
    }

    public String getMimeType() {
        return mimeType;
    }
}
