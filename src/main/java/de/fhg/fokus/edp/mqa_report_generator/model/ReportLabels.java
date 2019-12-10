package de.fhg.fokus.edp.mqa_report_generator.model;

import java.util.Locale;

public class ReportLabels {

    private Locale language;

    private String mqaTitle,
        mqaIntro,
        edpUrl,
    reportTocHeader,

    dashboardTitle,
        dashboardOverview,
        dashboardLastUpdate,
        dashboardLastUpdateLabel,
        dashboardCurrentDate,
        dashboardCurrentDateLabel,


    distributionHeader,
        distributionIntro,
        distributionsNotAvailable,

    distAccessibilityAccessUrlAll,
        distAccessibilityDownloadUrlAll,
        distStatusCodes,
        distWithoutDownloadUrl,
        distMachineReadablePercentage,
        distFormatMostUsed,


    violationHeader,
        violationIntro,
        violationsNotAvailable,

    violationMostOccurred,
        violationComplianceAll,


    licenceHeader,
        licenceIntro,
        licencesNotAvailable,

    licenceKnownPercentage,
        licenceMostUsed,


    helpOverallAccessibility,
        helpStatusCodes,
        helpDownloadUrlExists,
        helpMachineReadability,
        helpFormatsMostUsed,
        helpViolationOccurances,
        helpCompliantDatasets,
        helpKnownLicences,
        helpLicencesMostUsed,

    axisLabelFormats,
        axisLabelLicences,
        metricLabelPercentage,
        metricLabelYes,
        metricLabelNo,
        metricLabelUnknown;


    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public String getMqaTitle() {
        return mqaTitle;
    }

    public void setMqaTitle(String mqaTitle) {
        this.mqaTitle = mqaTitle;
    }

    public String getMqaIntro() {
        return mqaIntro;
    }

    public void setMqaIntro(String mqaIntro) {
        this.mqaIntro = mqaIntro;
    }

    public String getEdpUrl() {
        return edpUrl;
    }

    public void setEdpUrl(String edpUrl) {
        this.edpUrl = edpUrl;
    }

    public String getReportTocHeader() {
        return reportTocHeader;
    }

    public void setReportTocHeader(String reportTocHeader) {
        this.reportTocHeader = reportTocHeader;
    }

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
    }

    public String getDashboardOverview() {
        return dashboardOverview;
    }

    public void setDashboardOverview(String dashboardOverview) {
        this.dashboardOverview = dashboardOverview;
    }

    public String getDashboardLastUpdate() {
        return dashboardLastUpdate;
    }

    public void setDashboardLastUpdate(String dashboardLastUpdate) {
        this.dashboardLastUpdate = dashboardLastUpdate;
    }

    public String getDashboardLastUpdateLabel() {
        return dashboardLastUpdateLabel;
    }

    public void setDashboardLastUpdateLabel(String dashboardLastUpdateLabel) {
        this.dashboardLastUpdateLabel = dashboardLastUpdateLabel;
    }

    public String getDashboardCurrentDate() {
        return dashboardCurrentDate;
    }

    public void setDashboardCurrentDate(String dashboardCurrentDate) {
        this.dashboardCurrentDate = dashboardCurrentDate;
    }

    public String getDashboardCurrentDateLabel() {
        return dashboardCurrentDateLabel;
    }

    public void setDashboardCurrentDateLabel(String dashboardCurrentDateLabel) {
        this.dashboardCurrentDateLabel = dashboardCurrentDateLabel;
    }

    public String getDistributionHeader() {
        return distributionHeader;
    }

    public void setDistributionHeader(String distributionHeader) {
        this.distributionHeader = distributionHeader;
    }

    public String getDistributionIntro() {
        return distributionIntro;
    }

    public void setDistributionIntro(String distributionIntro) {
        this.distributionIntro = distributionIntro;
    }

    public String getDistributionsNotAvailable() {
        return distributionsNotAvailable;
    }

    public void setDistributionsNotAvailable(String distributionsNotAvailable) {
        this.distributionsNotAvailable = distributionsNotAvailable;
    }

    public String getDistAccessibilityAccessUrlAll() {
        return distAccessibilityAccessUrlAll;
    }

    public void setDistAccessibilityAccessUrlAll(String distAccessibilityAccessUrlAll) {
        this.distAccessibilityAccessUrlAll = distAccessibilityAccessUrlAll;
    }

    public String getDistAccessibilityDownloadUrlAll() {
        return distAccessibilityDownloadUrlAll;
    }

    public void setDistAccessibilityDownloadUrlAll(String distAccessibilityDownloadUrlAll) {
        this.distAccessibilityDownloadUrlAll = distAccessibilityDownloadUrlAll;
    }

    public String getDistStatusCodes() {
        return distStatusCodes;
    }

    public void setDistStatusCodes(String distStatusCodes) {
        this.distStatusCodes = distStatusCodes;
    }

    public String getDistWithoutDownloadUrl() {
        return distWithoutDownloadUrl;
    }

    public void setDistWithoutDownloadUrl(String distWithoutDownloadUrl) {
        this.distWithoutDownloadUrl = distWithoutDownloadUrl;
    }

    public String getDistMachineReadablePercentage() {
        return distMachineReadablePercentage;
    }

    public void setDistMachineReadablePercentage(String distMachineReadablePercentage) {
        this.distMachineReadablePercentage = distMachineReadablePercentage;
    }

    public String getDistFormatMostUsed() {
        return distFormatMostUsed;
    }

    public void setDistFormatMostUsed(String distFormatMostUsed) {
        this.distFormatMostUsed = distFormatMostUsed;
    }

    public String getViolationHeader() {
        return violationHeader;
    }

    public void setViolationHeader(String violationHeader) {
        this.violationHeader = violationHeader;
    }

    public String getViolationIntro() {
        return violationIntro;
    }

    public void setViolationIntro(String violationIntro) {
        this.violationIntro = violationIntro;
    }

    public String getViolationsNotAvailable() {
        return violationsNotAvailable;
    }

    public void setViolationsNotAvailable(String violationsNotAvailable) {
        this.violationsNotAvailable = violationsNotAvailable;
    }

    public String getViolationMostOccurred() {
        return violationMostOccurred;
    }

    public void setViolationMostOccurred(String violationMostOccurred) {
        this.violationMostOccurred = violationMostOccurred;
    }

    public String getViolationComplianceAll() {
        return violationComplianceAll;
    }

    public void setViolationComplianceAll(String violationComplianceAll) {
        this.violationComplianceAll = violationComplianceAll;
    }

    public String getLicenceHeader() {
        return licenceHeader;
    }

    public void setLicenceHeader(String licenceHeader) {
        this.licenceHeader = licenceHeader;
    }

    public String getLicenceIntro() {
        return licenceIntro;
    }

    public void setLicenceIntro(String licenceIntro) {
        this.licenceIntro = licenceIntro;
    }

    public String getLicencesNotAvailable() {
        return licencesNotAvailable;
    }

    public void setLicencesNotAvailable(String licencesNotAvailable) {
        this.licencesNotAvailable = licencesNotAvailable;
    }

    public String getLicenceKnownPercentage() {
        return licenceKnownPercentage;
    }

    public void setLicenceKnownPercentage(String licenceKnownPercentage) {
        this.licenceKnownPercentage = licenceKnownPercentage;
    }

    public String getLicenceMostUsed() {
        return licenceMostUsed;
    }

    public void setLicenceMostUsed(String licenceMostUsed) {
        this.licenceMostUsed = licenceMostUsed;
    }

    public String getHelpOverallAccessibility() {
        return helpOverallAccessibility;
    }

    public void setHelpOverallAccessibility(String helpOverallAccessibility) {
        this.helpOverallAccessibility = helpOverallAccessibility;
    }

    public String getHelpStatusCodes() {
        return helpStatusCodes;
    }

    public void setHelpStatusCodes(String helpStatusCodes) {
        this.helpStatusCodes = helpStatusCodes;
    }

    public String getHelpDownloadUrlExists() {
        return helpDownloadUrlExists;
    }

    public void setHelpDownloadUrlExists(String helpDownloadUrlExists) {
        this.helpDownloadUrlExists = helpDownloadUrlExists;
    }

    public String getHelpMachineReadability() {
        return helpMachineReadability;
    }

    public void setHelpMachineReadability(String helpMachineReadability) {
        this.helpMachineReadability = helpMachineReadability;
    }

    public String getHelpFormatsMostUsed() {
        return helpFormatsMostUsed;
    }

    public void setHelpFormatsMostUsed(String helpFormatsMostUsed) {
        this.helpFormatsMostUsed = helpFormatsMostUsed;
    }

    public String getHelpViolationOccurances() {
        return helpViolationOccurances;
    }

    public void setHelpViolationOccurances(String helpViolationOccurances) {
        this.helpViolationOccurances = helpViolationOccurances;
    }

    public String getHelpCompliantDatasets() {
        return helpCompliantDatasets;
    }

    public void setHelpCompliantDatasets(String helpCompliantDatasets) {
        this.helpCompliantDatasets = helpCompliantDatasets;
    }

    public String getHelpKnownLicences() {
        return helpKnownLicences;
    }

    public void setHelpKnownLicences(String helpKnownLicences) {
        this.helpKnownLicences = helpKnownLicences;
    }

    public String getHelpLicencesMostUsed() {
        return helpLicencesMostUsed;
    }

    public void setHelpLicencesMostUsed(String helpLicencesMostUsed) {
        this.helpLicencesMostUsed = helpLicencesMostUsed;
    }

    public String getAxisLabelFormats() {
        return axisLabelFormats;
    }

    public void setAxisLabelFormats(String axisLabelFormats) {
        this.axisLabelFormats = axisLabelFormats;
    }

    public String getAxisLabelLicences() {
        return axisLabelLicences;
    }

    public void setAxisLabelLicences(String axisLabelLicences) {
        this.axisLabelLicences = axisLabelLicences;
    }

    public String getMetricLabelPercentage() {
        return metricLabelPercentage;
    }

    public void setMetricLabelPercentage(String metricLabelPercentage) {
        this.metricLabelPercentage = metricLabelPercentage;
    }

    public String getMetricLabelYes() {
        return metricLabelYes;
    }

    public void setMetricLabelYes(String metricLabelYes) {
        this.metricLabelYes = metricLabelYes;
    }

    public String getMetricLabelNo() {
        return metricLabelNo;
    }

    public void setMetricLabelNo(String metricLabelNo) {
        this.metricLabelNo = metricLabelNo;
    }

    public String getMetricLabelUnknown() {
        return metricLabelUnknown;
    }

    public void setMetricLabelUnknown(String metricLabelUnknown) {
        this.metricLabelUnknown = metricLabelUnknown;
    }
}
