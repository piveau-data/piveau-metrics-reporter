package de.fhg.fokus.edp.mqa_report_generator.model;

import java.util.Set;

public class ReportValues {

    private String pageTitle;

    private Boolean renderDistributions;
    private Set<ReportElement> distributionsAccessUrl;
    private Set<ReportElement> distributionsDownloadUrl;
    private Set<ReportElement> distributionsDownloadUrlExists;
    private Set<ReportElement> distributionsStatusCodes;
    private Set<ReportElement> distributionsMachineReadability;
    private Set<ReportElement> mostUsedDistributionFormats;

    private Boolean renderViolations;
    private Set<ReportElement> mostOccurredViolations;
    private Set<ReportElement> datasetsCompliance;

    private Boolean renderLicences;
    private Set<ReportElement> knownLicencesPercentages;
    private Set<ReportElement> mostUsedLicences;


    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }


    public Boolean getRenderDistributions() {
        return renderDistributions;
    }

    public void setRenderDistributions(Boolean renderDistributions) {
        this.renderDistributions = renderDistributions;
    }

    public Set<ReportElement> getDistributionsAccessUrl() {
        return distributionsAccessUrl;
    }

    public void setDistributionsAccessUrl(Set<ReportElement> distributionsAccessUrl) {
        this.distributionsAccessUrl = distributionsAccessUrl;
    }

    public Set<ReportElement> getDistributionsDownloadUrl() {
        return distributionsDownloadUrl;
    }

    public void setDistributionsDownloadUrl(Set<ReportElement> distributionsDownloadUrl) {
        this.distributionsDownloadUrl = distributionsDownloadUrl;
    }

    public Set<ReportElement> getDistributionsDownloadUrlExists() {
        return distributionsDownloadUrlExists;
    }

    public void setDistributionsDownloadUrlExists(Set<ReportElement> distributionsDownloadUrlExists) {
        this.distributionsDownloadUrlExists = distributionsDownloadUrlExists;
    }

    public Set<ReportElement> getDistributionsStatusCodes() {
        return distributionsStatusCodes;
    }

    public void setDistributionsStatusCodes(Set<ReportElement> distributionsStatusCodes) {
        this.distributionsStatusCodes = distributionsStatusCodes;
    }

    public Set<ReportElement> getDistributionsMachineReadability() {
        return distributionsMachineReadability;
    }

    public void setDistributionsMachineReadability(Set<ReportElement> distributionsMachineReadability) {
        this.distributionsMachineReadability = distributionsMachineReadability;
    }

    public Set<ReportElement> getMostUsedDistributionFormats() {
        return mostUsedDistributionFormats;
    }

    public void setMostUsedDistributionFormats(Set<ReportElement> mostUsedDistributionFormats) {
        this.mostUsedDistributionFormats = mostUsedDistributionFormats;
    }

    public Boolean getRenderViolations() {
        return renderViolations;
    }

    public void setRenderViolations(Boolean renderViolations) {
        this.renderViolations = renderViolations;
    }

    public Set<ReportElement> getMostOccurredViolations() {
        return mostOccurredViolations;
    }

    public void setMostOccurredViolations(Set<ReportElement> mostOccurredViolations) {
        this.mostOccurredViolations = mostOccurredViolations;
    }

    public Set<ReportElement> getDatasetsCompliance() {
        return datasetsCompliance;
    }

    public void setDatasetsCompliance(Set<ReportElement> datasetsCompliance) {
        this.datasetsCompliance = datasetsCompliance;
    }

    public Boolean getRenderLicences() {
        return renderLicences;
    }

    public void setRenderLicences(Boolean renderLicences) {
        this.renderLicences = renderLicences;
    }

    public Set<ReportElement> getKnownLicencesPercentages() {
        return knownLicencesPercentages;
    }

    public void setKnownLicencesPercentages(Set<ReportElement> knownLicencesPercentages) {
        this.knownLicencesPercentages = knownLicencesPercentages;
    }

    public Set<ReportElement> getMostUsedLicences() {
        return mostUsedLicences;
    }

    public void setMostUsedLicences(Set<ReportElement> mostUsedLicences) {
        this.mostUsedLicences = mostUsedLicences;
    }
}
