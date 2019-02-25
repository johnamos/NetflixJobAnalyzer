# NetflixJobAnalyzer
Analyzes job log files and generates four reports from the command line:
1. **Job lineage** shows job dependencies in a tree
1. **Last and average run times** shows the statistics by job over the most recent week
1. **Late starts** shows all jobs that started late on a given date
1. **Upstream late start** provides an explanation for why a job started late on a given date

## Usage
#### Prerequisites
JDK 8 or higher.  The `java` and `javac` executables must be on your path for the following commands to work.
#### Compilation
cd into the project root folder and then run the following command to compile the source code:
`javac -d bin src/com/netflix/dpi/job/model/*.java src/com/netflix/dpi/job/reader/*.java src/com/netflix/dpi/job/analyzer/*.java`
#### Generating reports
After compiling, running the following command from the project root will generate a usage guide `java -cp bin com.netflix.dpi.job.analyzer.JobAnalyzerCli`:
```
usage:
JobAnalyzerCli [type] [parameter 1] .. [parameter N]
    where type is one of: lineage, stats, lateStarts, lateStartReason
    and parameters depend on type
    for type = lineage
        parameter 1 = path to job metadata csv file
    for type = stats
        parameter 1 = path to job execution log csv file
    for type = lateStarts
        parameter 1 = start date in ISO format (e.g. 2019-02-23)
        parameter 2 = path to job metadata csv file
        parameter 3 = path to job execution log csv file
    for type = lateStartReason
        parameter 1 = job name
        parameter 2 = start date in ISO format (e.g. 2019-02-23)
        parameter 3 = path to job metadata csv file
        parameter 4 = path to job execution log csv file
 ```
 #### Examples
 The following are examples of the four report types from Windows:
 ```
 C:\eclipse-workspace\NetflixJobAnalyzer>java -cp bin com.netflix.dpi.job.analyzer.JobAnalyzerCli lineage C:\eclipse-workspace\NetflixJobs\test\com\netflix\dpi\job\analyzer\job_metadata.csv
- job_A
    - job_B
        - job_E
            - job_F
    - job_C
        - job_G
        - job_H
            - job_J
    - job_D
        - job_I
            - job_J

C:\eclipse-workspace\NetflixJobAnalyzer>java -cp bin com.netflix.dpi.job.analyzer.JobAnalyzerCli stats C:\eclipse-workspace\NetflixJobs\test\com\netflix\dpi\job\analyzer\job_execution_log.csv
|job_name  | last_run_date | last_run_time | avg_runtime_7d|
|job_A     | 2018-07-07    | 00:50:00      | 01:04:17      |
|job_B     | 2018-07-07    | 01:35:00      | 01:35:00      |
|job_C     | 2018-07-07    | 01:00:00      | 00:59:10      |
|job_D     | 2018-07-07    | 04:00:00      | 04:05:42      |
|job_E     | 2018-07-07    | 00:30:00      | 00:30:00      |
|job_F     | 2018-07-07    | 00:00:10      | 00:00:10      |
|job_G     | 2018-07-07    | 00:15:00      | 00:19:10      |
|job_H     | 2018-07-07    | 03:00:00      | 03:04:10      |
|job_I     | 2018-07-07    | 00:10:00      | 00:12:51      |
|job_J     | 2018-07-07    | 00:02:00      | 00:03:40      |

C:\eclipse-workspace\NetflixJobAnalyzer>java -cp bin com.netflix.dpi.job.analyzer.JobAnalyzerCli lateStarts 2018-07-07 C:\eclipse-workspace\NetflixJobs\test\com\netflix\dpi\job\analyzer\job_metadata.c
sv C:\eclipse-workspace\NetflixJobs\test\com\netflix\dpi\job\analyzer\job_execution_log.csv
User provided date - 2018-07-07

| job_name | actual_start_time | expected_start_time |
| job_E    | 03:15:00          | 02:40:00            |
| job_F    | 03:45:00          | 03:10:00            |

C:\eclipse-workspace\NetflixJobAnalyzer>java -cp bin com.netflix.dpi.job.analyzer.JobAnalyzerCli lateStartReason job_F 2018-07-07 C:\eclipse-workspace\NetflixJobs\test\com\netflix\dpi\job\analyzer\job
_metadata.csv C:\eclipse-workspace\NetflixJobs\test\com\netflix\dpi\job\analyzer\job_execution_log.csv
User provided job_name - job_F
User provided date - 2018-07-07

job_F started late on 2018-07-07 because upstream job_E started late due to failures

 ```
 ## Assumptions
 1. The job name is a unique identifier.
 1. Each job is scheduled to run only once per day, although it may fail several times before succeeding.
 1. The jobs start and finish within a calendar day.
 1. The order of the data in the log files is irrelevant.  The job metadata and job execution data can be in any order.
 1. Lines of data that cannot be parsed are skipped and processing continues with the remainder of the input file.  The reason the line was skipped is written to the log by *CsvReader* at *java.util.logging.Level.FINE* level.

## JUnit tests and code coverage
There is one JUnit test class, *JobAnalyzerTest*, that tests all four report types and achieves 77% code coverage for all source files in the project, as shown in the image below from the [EclEmma Eclipse plugin](https://www.eclemma.org/):
![code coverage](https://i.imgur.com/qG5Vs3A.png)

## Scalability
Please refer to Goal5_Bonus.md in this folder for performance analysis and scalability recommendations.
