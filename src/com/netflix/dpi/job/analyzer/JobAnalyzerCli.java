package com.netflix.dpi.job.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a command-line interface to the JobAnalyzer.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class JobAnalyzerCli {
  private static final Logger LOGGER = Logger.getLogger(JobAnalyzerCli.class.getName());

  public static void main(String[] args) {
    if (args.length < 2) {
      writeUsage();
    } else if ("lineage".equals(args[0])) {
      writeLineage(args);
    } else if ("stats".equals(args[0])) {
      writeStats(args);
    } else if ("lateStarts".equals(args[0])) {
      writeLateStarts(args);
    } else if ("lateStartReason".equals(args[0])) {
      writeLateStartReason(args);
    } else {
      writeUsage();
    }
  }

  /**
   * Writes instructions for usage to the console.
   */
  private static void writeUsage() {
    System.out.println("usage:");
    System.out.println("JobAnalyzerCli [type] [parameter 1] .. [parameter N]");
    System.out.println("    where type is one of: lineage, stats, lateStarts, lateStartReason");
    System.out.println("    and parameters depend on type");
    System.out.println("    for type = lineage");
    System.out.println("        parameter 1 = path to job metadata csv file");
    System.out.println("    for type = stats");
    System.out.println("        parameter 1 = path to job execution log csv file");
    System.out.println("    for type = lateStarts");
    System.out.println("        parameter 1 = start date in ISO format (e.g. 2019-02-23)");
    System.out.println("        parameter 2 = path to job metadata csv file");
    System.out.println("        parameter 3 = path to job execution log csv file");
    System.out.println("    for type = lateStartReason");
    System.out.println("        parameter 1 = job name");
    System.out.println("        parameter 2 = start date in ISO format (e.g. 2019-02-23)");
    System.out.println("        parameter 3 = path to job metadata csv file");
    System.out.println("        parameter 4 = path to job execution log csv file");
  }

  /**
   * Writes the lineage report to the console.
   * 
   * @param args
   */
  private static void writeLineage(String[] args) {
    try (FileInputStream jobIs = new FileInputStream(new File(args[1]))) {
      JobAnalyzer.writeLineage(jobIs);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "", e);
    }
  }

  /**
   * Writes the last and average run time job report to the console.
   * 
   * @param args
   */
  private static void writeStats(String[] args) {
    try (FileInputStream lastRunDateIs = new FileInputStream(new File(args[1]));
        FileInputStream runIs = new FileInputStream(new File(args[1]))) {
      JobAnalyzer.writeStats(lastRunDateIs, runIs);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "", e);
    }
  }

  /**
   * Writes the late start report to the console.
   * 
   * @param args
   */
  private static void writeLateStarts(String[] args) {
    try (FileInputStream jobsIs = new FileInputStream(new File(args[2]));
        FileInputStream runIs = new FileInputStream(new File(args[3]))) {
      JobAnalyzer.writeLateStarts(LocalDate.parse(args[1]), jobsIs, runIs);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "", e);
    }
  }

  /**
   * Writes the late start reason report to the console.
   * 
   * @param args
   */
  private static void writeLateStartReason(String[] args) {
    try (FileInputStream jobsIs = new FileInputStream(new File(args[3]));
        FileInputStream runIs = new FileInputStream(new File(args[4]));
        FileInputStream runIs2 = new FileInputStream(new File(args[4]))) {
      JobAnalyzer.writeLateStartReason(args[1], LocalDate.parse(args[2]), jobsIs, runIs, runIs2);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "", e);
    }
  }
}
