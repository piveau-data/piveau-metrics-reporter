# MQA Report Generator

Computes various metrics for the MQA and stores the results in a database.

## Setup

#### 1. Install all of the following software
        
* Java JDK >= 1.8
* Git >= 2.17

#### 2. Set up PhantomJS for generating charts for report

For the PDF report generation to work, a highcharts export service must have been set up. The required files can be found in the `highcharts` folder.
To start the server, execute the following commands:

    cd highcharts/phantomjs/bin
    ./phantomjs highcharts-convert.js -host 127.0.0.1 -port 3003

Don't forget to add the address/ports you have chosen to the configuration.

  
#### 3. Configure and deploy report generator

Clone the directory and enter it
    
        git clone git@gitlab.com:european-data-portal/mqa-metric-service.git
        
The, edit the environment variables in the `Dockerfile` to your liking. Variables and their purpose are listed below:
   
| Key | Description | Default |
| :--- | :--- | :--- |
| PORT | Port this service will run on | 8089 |
| API_KEY | Authorization secret required for certain endpoints. Must be configured for service to run. | null |
| METRIC_HOST | Host name of the metric service | 127.0.0.1 |
| METRIC_PORT | Port number of the metric service | 8083 |
| CHART_ENDPOINT | Address at which the Highcharts server is available | http://127.0.0.1:3003 |
| REPORT_DIRECTORY | Directory into which reports will be written | /tmp/mqa-reports |
| ELEMENT_LIMIT | Maximum number of elements to display in charts | 10 |
| PIVEAU_PIPE_LOG_LEVEL | Log level | INFO |

        
## Run

### Production

Build the project by using the provided Maven wrapper. This ensures everyone this software is provided to can use the exact same version of the maven build tool.
The generated _fat-jar_ can then be found in the `target` directory.

* Linux
    
        ./mvnw clean package
        java -jar target/mqa-report-generator-0.1-fat.jar

* Windows

        mvnw.cmd clean package
        java -jar target/mqa-report-generator-0.1-fat.jar
      
* Docker (this comes bundled with phantomjs)

    1. Start your docker daemon 
    2. Build the application as described in Windows or Linux
    3. Adjust the port number (`EXPOSE` in the `Dockerfile`)
    4. Build the image: `docker build -t edp/mqa-report-generator .`
    5. Run the image, adjusting the port number as set in step _iii_: `docker run -i -p 8089:8089 edp/mqa-report-generator`
    6. Configuration can be changed without rebuilding the image by overriding variables: `-e PORT=8090`

### Development

For use in development two scripts are provided in the project's root folder. These enable hot deployment (dynamic recompiling when changes are made to the source code).
Linux users should run the `redeploy.sh` and Windows users the `redeploy.bat` file.

_Note_: The files generated by [VertX Codegen]([https://github.com/vert-x3/vertx-codegen]) may not be detected by your IDE. 
In this case, mark the directory `src/main/generated` as `Generated Sources Root`.

## CI

The repository uses the gitlab in-build CI Framework. The .gitlab-ci.yaml file starts as soon a new push event occurs. After running the test cases the application is build, a new docker image is created and stored in the gitlab registry. 

## API

A formal OpenAPI 3 specification can be found in the `src/main/resources/webroot/openapi.yaml` file.
A visually more appealing version is available at `{url}:{port}` once the application has been started.
