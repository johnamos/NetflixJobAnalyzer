package com.netflix.dpi.job.reader;

import java.io.InputStream;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.netflix.dpi.job.model.Job;

/**
 * Converts comma-separated lines into a Map of Job objects.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class JobReader implements CsvClient {
  private static final Logger LOGGER = Logger.getLogger(JobReader.class.getName());
  private final InputStream is;
  private final Map<String, Job> map = new TreeMap<>();
  private final Map<String, Set<Job>> unresolvedParents = new ConcurrentHashMap<>();

  private JobReader(InputStream is) {
    this.is = is;
  }

  @Override
  public InputStream getInputStream() {
    return is;
  }

  @Override
  public void readLine(String[] columns) {
    String name = columns[0];
    Job job = map.computeIfAbsent(name, k -> new Job(name, LocalTime.parse(columns[2])));
    resolveParent(columns[3], map, job, unresolvedParents);
  }

  /**
   * Converts comma-separated lines into a Map of Job objects.
   * 
   * @param is
   * @return
   */
  public static Map<String, Job> readJobs(InputStream is) {
    JobReader jr = new JobReader(is);
    CsvReader.readStream(jr);
    resolveParents(jr.map, jr.unresolvedParents);
    return jr.map;
  }

  /**
   * Establishes parent and child relationships between Job objects for the previously unresolved
   * parents contained in the supplied unresolvedParents.
   * 
   * @param map
   * @param unresolvedParents
   */
  private static void resolveParents(Map<String, Job> map,
      Map<String, Set<Job>> unresolvedParents) {
    for (Map.Entry<String, Set<Job>> entry : unresolvedParents.entrySet()) {
      Job parent = map.get(entry.getKey());
      if (parent == null) {
        LOGGER.log(Level.SEVERE, "parent job not found: \"{0}\"", entry.getKey());
      } else {
        for (Job job : entry.getValue()) {
          relate(job, parent);
        }
      }
    }
  }

  /**
   * Creates parent and child links between a Job with the supplied parentName and the supplied job.
   * If there is no Job in the supplied map named parentName, then this method adds the parent and
   * child to the supplied unresolvedParents.
   * 
   * @param parentName
   * @param map
   * @param job
   * @param unresolvedParents
   */
  private static void resolveParent(String parentName, Map<String, Job> map, Job job,
      Map<String, Set<Job>> unresolvedParents) {
    if (parentName != null && !parentName.isEmpty()) {
      Job parent = map.get(parentName);
      if (parent == null) {
        unresolvedParents.putIfAbsent(parentName, new HashSet<>());
        unresolvedParents.get(parentName).add(job);
      } else {
        relate(job, parent);
      }
    }
  }

  /**
   * Creates two-way parent-child links between the two Jobs.
   * 
   * @param job
   * @param parent
   */
  private static void relate(Job job, Job parent) {
    job.getParents().add(parent);
    parent.getChildren().add(job);
  }
}
