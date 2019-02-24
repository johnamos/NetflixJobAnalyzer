package com.netflix.dpi.job.reader;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.TreeMap;

import com.netflix.dpi.job.model.JobRun;
import com.netflix.dpi.job.model.JobStats;

/**
 * Creates a Map of JobStats objects from a comma-separated InputStream of job executions.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class JobRunReader implements CsvClient {
  private final InputStream is;
  private final Map<String, JobStats> map = new TreeMap<>();
  private static final String SUCCESS = "SUCCESS";
  private final LocalDate startDate;
  private final LocalDate endDate;

  private JobRunReader(InputStream is, LocalDate startDate, LocalDate endDate) {
    this.is = is;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  @Override
  public InputStream getInputStream() {
    return is;
  }

  @Override
  public void readLine(String[] columns) {
    JobRun jobRun = new JobRun(columns[0], LocalTime.parse(columns[1]), LocalTime.parse(columns[2]),
        columns[3], LocalDate.parse(columns[4]));
    if (SUCCESS.equals(jobRun.getStatus()) && (jobRun.getRunDate().isEqual(endDate)
        || (jobRun.getRunDate().isAfter(startDate) && jobRun.getRunDate().isBefore(endDate)))) {
      Duration duration = Duration.between(jobRun.getStartTime(), jobRun.getEndTime());
      int runCount = 1;
      LocalDate lastRunDate = jobRun.getRunDate();
      Duration lastRunDuration = duration;
      JobStats oldStats = map.get(jobRun.getJobName());
      if (oldStats != null) {
        runCount += oldStats.getRunCount();
        duration = oldStats.getCumulativeDuration().plus(duration);
        if (jobRun.getRunDate().isBefore(oldStats.getLastRunDate())) {
          lastRunDate = oldStats.getLastRunDate();
          lastRunDuration = oldStats.getLastRunDuration();
        }
      }
      map.put(jobRun.getJobName(),
          new JobStats(runCount, duration, lastRunDate, lastRunDuration, jobRun.getEndTime()));
    }
  }

  /**
   * Finds and returns the most recent job execution date from the job runs in the supplied
   * InputStream.
   * 
   * @param is
   * @return
   */
  public static LocalDate findLastRunDate(InputStream is) {
    LastRunReader lrr = new LastRunReader(is);
    CsvReader.readStream(lrr);
    return lrr.getResult();
  }

  /**
   * Creates a Map of JobStats objects from a comma-separated InputStream of job executions.
   * 
   * @param is
   * @return
   */
  public static Map<String, JobStats> readJobRuns(InputStream is, LocalDate startDate,
      LocalDate endDate) {
    JobRunReader jrr = new JobRunReader(is, startDate, endDate);
    CsvReader.readStream(jrr);
    return jrr.map;
  }

}
