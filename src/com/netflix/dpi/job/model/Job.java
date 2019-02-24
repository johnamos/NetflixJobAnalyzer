package com.netflix.dpi.job.model;

import java.time.LocalTime;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a job read from a job metadata file, including references to parents and children.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class Job implements Comparable<Job> {
  private final Set<Job> parents = new TreeSet<>();
  private final Set<Job> children = new TreeSet<>();
  private final String name;
  private final LocalTime expectedStart;

  public Job(String name, LocalTime expectedStart) {
    this.name = name;
    this.expectedStart = expectedStart;
  }

  public Set<Job> getParents() {
    return parents;
  }

  public String getName() {
    return name;
  }

  public Set<Job> getChildren() {
    return children;
  }

  public LocalTime getExpectedStart() {
    return expectedStart;
  }

  @Override
  public boolean equals(Object arg0) {
    return (arg0 instanceof Job) && toString().equals(arg0.toString());
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int compareTo(Job o) {
    return toString().compareTo(o.toString());
  }

}
