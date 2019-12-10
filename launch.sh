#!/bin/sh

# wait 1 hour for the metric service to generate metrics before launching the report generator
#sleep 3600

cd /usr/verticles/phantomjs/bin
./phantomjs highcharts-convert.js -host 127.0.0.1 -port 3003 &

cd /usr/verticles
exec java -Xmx2048m -jar mqa-report-generator-0.1-fat.jar
