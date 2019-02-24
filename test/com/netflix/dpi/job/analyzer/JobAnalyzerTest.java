package com.netflix.dpi.job.analyzer;

import java.time.LocalDate;
import java.util.logging.Logger;

import org.junit.Test;

/**
 * JUnit test for JobAnalyzer.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class JobAnalyzerTest {
  private static final String JOB_METADATA_REORDERED_CSV = "job_metadata_reordered.csv";
  private static final String JOB_METADATA_CSV = "job_metadata.csv";
  private static final String JOB_EXECUTION_LOG_CSV = "job_execution_log.csv";
  private static final Logger LOGGER = Logger.getLogger(JobAnalyzerTest.class.getName());

  @Test
  public void testWriteLineage() {
    LOGGER.info("original ordering:");
    JobAnalyzer.writeLineage(getClass().getResourceAsStream(JOB_METADATA_CSV));
    LOGGER.info("reordered source file:");
    JobAnalyzer.writeLineage(getClass().getResourceAsStream(JOB_METADATA_REORDERED_CSV));
  }

  @Test
  public void testWriteStats() {
    JobAnalyzer.writeStats(getClass().getResourceAsStream(JOB_EXECUTION_LOG_CSV),
        getClass().getResourceAsStream(JOB_EXECUTION_LOG_CSV));
  }

  @Test
  public void testWriteLateStarts() {
    JobAnalyzer.writeLateStarts(LocalDate.of(2018, 7, 7),
        getClass().getResourceAsStream(JOB_METADATA_CSV),
        getClass().getResourceAsStream(JOB_EXECUTION_LOG_CSV));
  }

  @Test
  public void testWriteLateStartReason() {
    JobAnalyzer.writeLateStartReason("job_F", LocalDate.of(2018, 7, 7),
        getClass().getResourceAsStream(JOB_METADATA_CSV),
        getClass().getResourceAsStream(JOB_EXECUTION_LOG_CSV),
        getClass().getResourceAsStream(JOB_EXECUTION_LOG_CSV));
    JobAnalyzer.writeLateStartReason("job_f", LocalDate.of(2018, 7, 7),
        getClass().getResourceAsStream(JOB_METADATA_CSV),
        getClass().getResourceAsStream(JOB_EXECUTION_LOG_CSV),
        getClass().getResourceAsStream(JOB_EXECUTION_LOG_CSV));
    JobAnalyzer.writeLateStartReason("job_A", LocalDate.of(2018, 8, 7),
        getClass().getResourceAsStream(JOB_METADATA_CSV),
        getClass().getResourceAsStream(JOB_EXECUTION_LOG_CSV),
        getClass().getResourceAsStream(JOB_EXECUTION_LOG_CSV));
  }

}
