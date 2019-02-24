package com.netflix.dpi.job.analyzer;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.netflix.dpi.job.model.Job;
import com.netflix.dpi.job.model.JobStats;
import com.netflix.dpi.job.model.LateJobDetails;
import com.netflix.dpi.job.reader.JobReader;
import com.netflix.dpi.job.reader.JobRunReader;
import com.netflix.dpi.job.reader.LateStartReader;

/**
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class JobAnalyzer {
  private static final String STATS_HEADER = "|job_name  | last_run_date | last_run_time | avg_runtime_7d|";
  private static final String STATS_ROW_FORMAT = "|%-10s| %-14s| %-14s| %-14s|";

  private static final String LATE_DATE_PREFIX = "User provided date - %s";
  private static final String LATE_HEADER = "| job_name | actual_start_time | expected_start_time |";
  private static final String LATE_ROW_FORMAT = "| %-9s| %-18s| %-20s|";

  private static final String LATE_JOB_PREFIX = "User provided job_name - %s";

  private JobAnalyzer() {
  }

  /**
   * Writes the lineage report to the console.
   * 
   * @param jobsIs
   */
  public static void writeLineage(InputStream jobsIs) {
    Set<Job> jobs = new TreeSet<>(JobReader.readJobs(jobsIs).values());
    writeLineage(jobs);
  }

  /**
   * Calls a recursive method to write the job dependency tree to the console.
   * 
   * @param jobList
   */
  public static void writeLineage(Set<Job> jobList) {
    for (Job job : jobList) {
      if (job.getParents().isEmpty()) {
        writeTree(job, 0);
      }
    }
  }

  /**
   * Writes the last and average run time job report to the console.
   * 
   * @param lastRunDateIs
   * @param runIs
   */
  public static void writeStats(InputStream lastRunDateIs, InputStream runIs) {
    LocalDate lastRunDate = JobRunReader.findLastRunDate(lastRunDateIs);
    Map<String, JobStats> runs = JobRunReader.readJobRuns(runIs, lastRunDate.minusDays(7),
        lastRunDate);
    writeStats(runs);
  }

  /**
   * Writes to the console the last and average run times for all successful jobs during the most
   * recent week of available data.
   * 
   * @param statsMap
   */
  public static void writeStats(Map<String, JobStats> statsMap) {
    System.out.println(STATS_HEADER);
    for (Map.Entry<String, JobStats> entry : statsMap.entrySet()) {
      JobStats stats = entry.getValue();
      System.out.println(String.format(STATS_ROW_FORMAT, entry.getKey(), stats.getLastRunDate(),
          formatDuration(stats.getLastRunDuration()),
          formatDuration(stats.getCumulativeDuration().dividedBy(stats.getRunCount()))));
    }
  }

  /**
   * Writes the late start report to the console.
   * 
   * @param startDate
   * @param jobsIs
   * @param runIs
   */
  public static void writeLateStarts(LocalDate startDate, InputStream jobsIs, InputStream runIs) {
    Map<String, LateJobDetails> map = LateStartReader.readJobRuns(runIs, startDate,
        JobReader.readJobs(jobsIs));
    writeLateStarts(startDate, map);
  }

  /**
   * Writes to the console all jobs that started late on the supplied startDate.
   * 
   * @param startDate
   * @param lateMap
   */
  public static void writeLateStarts(LocalDate startDate, Map<String, LateJobDetails> lateMap) {
    System.out.println(String.format(LATE_DATE_PREFIX, startDate));
    System.out.println();
    System.out.println(LATE_HEADER);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    for (Map.Entry<String, LateJobDetails> entry : lateMap.entrySet()) {
      LateJobDetails details = entry.getValue();
      System.out.println(
          String.format(LATE_ROW_FORMAT, entry.getKey(), details.getActualStart().format(formatter),
              details.getExpectedStart().format(formatter)));
    }
  }

  /**
   * Writes the late start reason report to the console.
   * 
   * @param jobName
   * @param startDate
   * @param jobsIs
   * @param runIs
   * @param runIs2
   */
  public static void writeLateStartReason(String jobName, LocalDate startDate, InputStream jobsIs,
      InputStream runIs, InputStream runIs2) {
    Map<String, Job> jobs = JobReader.readJobs(jobsIs);
    Map<String, JobStats> runs = JobRunReader.readJobRuns(runIs, startDate, startDate);
    Map<String, LateJobDetails> lateJobs = LateStartReader.readJobRuns(runIs2, startDate, jobs);
    writeLateStartReason(jobName, startDate, jobs, lateJobs, runs);
  }

  /**
   * Writes to the console the reason that the job with the supplied jobName started late on the
   * supplied startDate.
   * 
   * @param jobName
   * @param startDate
   * @param jobs
   * @param lateJobs
   * @param runs
   */
  public static void writeLateStartReason(String jobName, LocalDate startDate,
      Map<String, Job> jobs, Map<String, LateJobDetails> lateJobs, Map<String, JobStats> runs) {
    System.out.println(String.format(LATE_JOB_PREFIX, jobName));
    System.out.println(String.format(LATE_DATE_PREFIX, startDate));
    System.out.println();
    System.out.println(LateStartReason.findReason(jobName, startDate, jobs, lateJobs, runs));
  }

  /**
   * Formats a Duration in ISO (HH:MM:SS) format. From here:
   * https://stackoverflow.com/questions/266825/how-to-format-a-duration-in-java-e-g-format-hmmss/44343699
   * 
   * @param duration
   * @return
   */
  private static String formatDuration(Duration duration) {
    long seconds = duration.getSeconds();
    long absSeconds = Math.abs(seconds);
    String positive = String.format("%02d:%02d:%02d", absSeconds / 3600, (absSeconds % 3600) / 60,
        absSeconds % 60);
    return seconds < 0 ? "-" + positive : positive;
  }

  /**
   * Recursively writes the job dependency tree to the console.
   * 
   * @param job
   * @param level
   */
  private static void writeTree(Job job, int level) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < level; i++) {
      sb.append("    ");
    }
    sb.append("- ").append(job.getName());
    System.out.println(sb.toString());
    for (Job child : job.getChildren()) {
      writeTree(child, level + 1);
    }
  }
}
