package de.fhg.fokus.edp.mqa_report_generator.metrics;

// Catalogue endpoints contain '%s' as a placeholder for the catalogueId
public enum Metric {

    // Catalogue info

    INFO_CATALOGUES("/info/catalogues"),

    // Render sections

    GLOBAL_RENDER_DISTRIBUTIONS("/metric/global/render/distributions"),
    CATALOGUE_RENDER_DISTRIBUTIONS("/metric/catalogues/%s/render/distributions"),

    GLOBAL_RENDER_VIOLATIONS("/metric/global/render/violations"),
    CATALOGUE_RENDER_VIOLATIONS("/metric/catalogues/%s/render/violations"),

    GLOBAL_RENDER_LICENCES("/metric/global/render/licences"),
    CATALOGUE_RENDER_LICENCES("/metric/catalogues/%s/render/licences"),


    // Distribution metrics

    GLOBAL_DISTRIBUTION_ACCESSIBILITY_ACCESS_URL("/metric/global/distributions/accessibility/access_url"),
    CATALOGUE_DISTRIBUTION_ACCESSIBILITY_ACCESS_URL("/metric/catalogues/%s/distributions/accessibility/access_url"),

    GLOBAL_DISTRIBUTION_ACCESSIBILITY_DOWNLOAD_URL("/metric/global/distributions/accessibility/download_url"),
    CATALOGUE_DISTRIBUTION_ACCESSIBILITY_DOWNLOAD_URL("/metric/catalogues/%s/distributions/accessibility/download_url"),


    GLOBAL_MACHINE_READABILITY("/metric/global/datasets/machine_readable"),
    CATALOGUE_MACHINE_READABILITY("/metric/catalogues/%s/distributions/machine_readable"),

    GLOBAL_STATUS_CODES("/metric/global/distributions/status_codes"),
    CATALOGUE_STATUS_CODES("/metric/catalogues/%s/distributions/status_codes"),

    GLOBAL_DOWNLOAD_URL_EXIST("/metric/global/distributions/download_url_exists"),
    CATALOGUE_DOWNLOAD_URL_EXIST("/metric/catalogues/%s/distributions/download_url_exists"),

    GLOBAL_DISTRIBUTION_FORMATS("/metric/global/distributions/formats"),
    CATALOGUE_DISTRIBUTION_FORMATS("/metric/catalogues/%s/distributions/formats"),


    // Compliance metrics

    GLOBAL_DATASET_VIOLATIONS("/metric/global/datasets/violations"),
    CATALOGUE_DATASET_VIOLATIONS("/metric/catalogues/%s/datasets/violations"),

    GLOBAL_DATASET_COMPLIANCE("/metric/global/datasets/compliance"),
    CATALOGUE_DATASET_COMPLIANCE("/metric/catalogues/%s/datasets/compliance"),


    // Licence metrics

    GLOBAL_DATASET_LICENCES("/metric/global/datasets/licences"),
    CATALOGUE_DATASET_LICENCES("/metric/catalogues/%s/datasets/licences"),

    GLOBAL_DATASET_KNOWN_LICENCES("/metric/global/datasets/known_licences"),
    CATALOGUE_DATASET_KNOWN_LICENCES("/metric/catalogues/%s/datasets/known_licences");



    private final String endpoint;

    Metric(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
