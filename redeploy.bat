@echo off

SET LAUNCHER="io.vertx.core.Launcher"
SET VERTICLE="de.fhg.fokus.edp.mqa_report_generator.MainVerticle"
SET CMD="mvn compile"
SET VERTX_CMD="run"
SET CMD_LINE_ARGS=%*

call mvn compile dependency:copy-dependencies

java -cp  "target\dependency\*;target\classes" %LAUNCHER% %VERTX_CMD% %VERTICLE% --redeploy="src\main\**\*" --on-redeploy=%CMD% --launcher-class=%LAUNCHER% %CMD_LINE_ARGS% --java-opts="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
