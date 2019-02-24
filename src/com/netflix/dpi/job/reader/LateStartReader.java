package com.netflix.dpi.job.reader;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.netflix.dpi.job.model.Job;
import com.netflix.dpi.job.model.JobRun;
import com.netflix.dpi.job.model.LateJobDetails;

/**
 * Creates a Map of LateJobDetails from an InputStream of job executions.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class LateStartReader implements CsvClient {
  private static final Logger LOGGER = Logger.getLogger(LateStartReader.class.getName());
  private final InputStream is;
  private final Map<String, LateJobDetails> map = new TreeMap<>();
  private final LocalDate startDate;
  private final Map<String, Job> jobs;

  private LateStartReader(InputStream is, LocalDate startDate, Map<String, Job> jobs) {
    this.is = is;
    this.startDate = startDate;
    this.jobs = jobs;
  }

  @Override
  public InputStream getInputStream() {
    return is;
  }

  @Override
  public void readLine(String[] columns) {
    JobRun jobRun = new JobRun(columns[0], LocalTime.parse(columns[1]), LocalTime.parse(columns[2]),
        columns[3], LocalDate.parse(columns[4]));
    Job job = jobs.get(jobRun.getJobName());
    if (job == null) {
      LOGGER.log(Level.SEVERE, "no such job \"{0}\"", jobRun.getJobName());
    } else if (jobRun.getRunDate().isEqual(startDate)
        && jobRun.getStartTime().isAfter(job.getExpectedStart())) {
      map.put(jobRun.getJobName(),
          new LateJobDetails(jobRun.getStartTime(), job.getExpectedStart()));
    }
  }

  /**
   * Creates a Map of LateJobDetails from an InputStream of job executions.
   * 
   * @param is
   * @return
   */
  public static Map<String, LateJobDetails> readJobRuns(InputStream is, LocalDate startDate,
      Map<String, Job> jobs) {
    LateStartReader jrr = new LateStartReader(is, startDate, jobs);
    CsvReader.readStream(jrr);
    return jrr.map;
  }

}
