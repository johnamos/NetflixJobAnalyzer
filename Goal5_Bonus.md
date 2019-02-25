## Scaling to higher data volume
This document describes the performance characteristics of the solution and proposes changes to accommodate higher data volumes.
### Performance
I created larger data files to test the performance characteristics of the solution.  The job metadata file contained 36K rows and the job execution file contained 350K rows ([zip file](https://github.com/johnamos/NetflixJobAnalyzer/blob/master/test/com/netflix/dpi/job/analyzer/large-data.zip)).  Running all four reports in sequence completes in less than two minutes on my laptop, with low memory and CPU usage.  The images below are from a Java Flight Recorder dump:
##### Memory usage
![Imgur](https://i.imgur.com/GgLd5Vc.png)
##### CPU usage
![Imgur](https://i.imgur.com/rJDtETa.png)
##### Profile data
The profiling data from the Java Flight Recorder dump shows that over 70% of the total CPU time was consumed by parsing dates and times in the following two methods:
1. *java.time.LocalTime.parse(CharSequence)*
1. *java.time.LocalDate.parse(CharSequence)*
### Recommended changes for improving performance and scalability
##### Performance
The analysis above shows that over 70% of the CPU time is consumed by parsing dates and times, so single-thread performance may be improved by using an improved parser or avoiding parsing by using cached values.
##### Scalability
The current solution runs in a single thread, and scalability can be improved by distributing the work among multiple threads and multiple nodes.  The best way to do this is to use a data processing engine like Apach Spark.  The workload can be divided up in several ways:
* **Log file** where each log file is read and preprocessed by one thread
* **Date** one thread can process a single day of data from a log file
* **Owner** one thread can process jobs from a single owner


