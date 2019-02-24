package com.netflix.dpi.job.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a job run read from a log file of job executions.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class JobRun {
  private final String jobName;
  private final LocalTime startTime;
  private final LocalTime endTime;
  private final String status;
  private final LocalDate runDate;

  public JobRun(String jobName, LocalTime startTime, LocalTime endTime, String status,
      LocalDate runDate) {
    super();
    this.jobName = jobName;
    this.startTime = startTime;
    this.endTime = endTime;
    this.status = status;
    this.runDate = runDate;
  }

  public String getJobName() {
    return jobName;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public String getStatus() {
    return status;
  }

  public LocalDate getRunDate() {
    return runDate;
  }
}
