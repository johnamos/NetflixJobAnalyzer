package com.netflix.dpi.job.model;

import java.time.LocalTime;

/**
 * A container for details about jobs that start late.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class LateJobDetails {
  private final LocalTime actualStart;
  private final LocalTime expectedStart;

  public LateJobDetails(LocalTime actualStart, LocalTime expectedStart) {
    super();
    this.actualStart = actualStart;
    this.expectedStart = expectedStart;
  }

  public LocalTime getActualStart() {
    return actualStart;
  }

  public LocalTime getExpectedStart() {
    return expectedStart;
  }
}
