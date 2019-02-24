package com.netflix.dpi.job.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A container for job statistics like durations and counts that can be used to calculate average
 * run times.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class JobStats {
  private final int runCount;
  private final Duration cumulativeDuration;
  private final LocalDate lastRunDate;
  private final Duration lastRunDuration;
  private final LocalTime lastRunEndTime;

  public JobStats(int runCount, Duration cumulativeDuration, LocalDate lastRunDate,
      Duration lastRunDuration, LocalTime lastRunEndTime) {
    super();
    this.runCount = runCount;
    this.cumulativeDuration = cumulativeDuration;
    this.lastRunDate = lastRunDate;
    this.lastRunDuration = lastRunDuration;
    this.lastRunEndTime = lastRunEndTime;
  }

  public int getRunCount() {
    return runCount;
  }

  public Duration getCumulativeDuration() {
    return cumulativeDuration;
  }

  public LocalDate getLastRunDate() {
    return lastRunDate;
  }

  public Duration getLastRunDuration() {
    return lastRunDuration;
  }

  public LocalTime getLastRunEndTime() {
    return lastRunEndTime;
  }
}
