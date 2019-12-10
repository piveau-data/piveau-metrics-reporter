package de.fhg.fokus.edp.mqa_report_generator;

public final class ApplicationConfig {

    static final String GENERATE_REPORT_ADDRESS = "address.generate";

    static final String ENV_APPLICATION_PORT = "PORT";
    static final Integer DEFAULT_APPLICATION_PORT = 8089;

    static final String ENV_API_KEY = "API_KEY";

    static final String ENV_METRIC_HOST = "METRIC_HOST";
    static final String DEFAULT_METRIC_HOST = "127.0.0.1";

    static final String ENV_METRIC_PORT = "METRIC_PORT";
    static final Integer DEFAULT_METRIC_PORT = 8083;

    static final String ENV_CHART_ENDPOINT = "CHART_ENDPOINT";
    static final String DEFAULT_CHART_ENDPOINT = "http://127.0.0.1:3003";

    public static final String ENV_REPORT_DIRECTORY = "REPORT_DIRECTORY";
    public static final String DEFAULT_REPORT_DIRECTORY = "/tmp/mqa-reports";

    static final String ENV_METRIC_ELEMENT_LIMIT = "ELEMENT_LIMIT";
    static final Integer DEFAULT_METRIC_ELEMENT_LIMIT = 10;
}
