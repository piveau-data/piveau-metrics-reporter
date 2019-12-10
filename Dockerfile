FROM openjdk:8


# Application configuration - change these
# Details can be found in the README
ENV PORT 8089
ENV PIVEAU_PIPE_LOG_LEVEL INFO
ENV METRIC_HOST mqa-metric-service
ENV METRIC_PORT 8083


# Don't change these unless you know what you're doing
ENV PIVEAU_PIPE_LOG_APPENDER PIPEFILE
ENV PIVEAU_PIPE_LOG_PATH logs/mqa-report-generator.%d{yyyy-MM-dd}.log

ENV VERTICLE_FILE mqa-report-generator-0.1-fat.jar
ENV VERTICLE_HOME /usr/verticles


EXPOSE $PORT

RUN useradd vertx


COPY highcharts $VERTICLE_HOME/
COPY launch.sh $VERTICLE_HOME/launch.sh

# Copy your fat jar to the container
COPY target/$VERTICLE_FILE $VERTICLE_HOME/

RUN chown -R vertx $VERTICLE_HOME
RUN chmod -R g+w $VERTICLE_HOME

USER vertx

RUN chmod +x $VERTICLE_HOME/phantomjs/bin/phantomjs
RUN chmod +x $VERTICLE_HOME/launch.sh

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "launch.sh"]
