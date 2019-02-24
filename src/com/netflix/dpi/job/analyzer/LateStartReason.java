package com.netflix.dpi.job.analyzer;

import java.time.LocalDate;
import java.util.Map;

import com.netflix.dpi.job.model.Job;
import com.netflix.dpi.job.model.JobStats;
import com.netflix.dpi.job.model.LateJobDetails;

/**
 * Finds the reason for late starting jobs.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class LateStartReason {

  private LateStartReason() {
  }

  /**
   * Builds and returns a String that explains why the supplied jobName started late.
   * 
   * @param jobName
   * @param startDate
   * @param jobs
   * @param lateJobs
   * @param runs
   * @return
   */
  public static String findReason(String jobName, LocalDate startDate, Map<String, Job> jobs,
      Map<String, LateJobDetails> lateJobs, Map<String, JobStats> runs) {
    String result = null;
    Job job = jobs.get(jobName);
    if (job == null) {
      result = String.format("%s does not exist", jobName);
    } else if (lateJobs.get(jobName) == null) {
      result = String.format("%s did not start late on the specified date", jobName);
    } else if (job.getParents().isEmpty()) {
      result = String.format(
          "%s has no upstream dependencies, so it started late for unknown reasons", jobName);
    }
    if (result == null) {
      result = String.format("%s started late on %s because upstream %s", jobName, startDate,
          findReason(jobName, jobs, lateJobs, runs));
    }
    return result;
  }

  /**
   * Recurses up the dependency tree to find the parent job that caused the supplied jobName to
   * start late. Possible reasons are that the parent job took longer than usual to finish, the
   * parent job started late, or the job itself failed the first time it started.
   * 
   * @param jobName
   * @param jobs
   * @param lateJobs
   * @param runs
   * @return
   */
  private static String findReason(String jobName, Map<String, Job> jobs,
      Map<String, LateJobDetails> lateJobs, Map<String, JobStats> runs) {
    String result = null;
    Job job = jobs.get(jobName);
    for (Job parent : job.getParents()) {
      if (lateJobs.get(parent.getName()) == null) {
        if (runs.get(parent.getName()).getLastRunEndTime().isAfter(job.getExpectedStart())) {
          result = parent.getName() + " took longer than usual";
        } else {
          result = jobName + " started late due to failures";
        }
      } else {
        result = findReason(parent.getName(), jobs, lateJobs, runs);
      }
    }
    if (result == null && lateJobs.get(jobName) != null) {
      result = jobName + " started late";
    }
    return result;
  }

}
