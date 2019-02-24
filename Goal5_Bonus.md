## Scaling to higher data volume
This document describes the performance characteristics of the solution and proposes changes to accommodate higher data volumes.
### Performance
I created larger data files to test the performance characteristics of the solution.  The job metadata file contained 36K rows and the job execution file contained 350K rows.  Running all four reports in sequence completes in less than two minutes, with low memory and CPU usage.  The images below are from a Java Flight Recorder dump:
##### Memory usage
![Imgur](https://i.imgur.com/GgLd5Vc.png)
##### CPU usage
![Imgur](https://i.imgur.com/rJDtETa.png)
##### Profile data
The profiling data from the Java Flight Recorder dump shows that over 70% of the total CPU time was consumed by parsing dates and times in the following two methods:
1. *java.time.LocalTime.parse(CharSequence)*
1. *java.time.LocalDate.parse(CharSequence)*

