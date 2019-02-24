package com.netflix.dpi.job.reader;

import java.io.InputStream;
import java.time.LocalDate;

/**
 * Finds the most recent run date from an InputStream of comma-separated job execution data.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class LastRunReader implements CsvClient {
  private final InputStream is;
  private LocalDate result = null;

  public LastRunReader(InputStream is) {
    this.is = is;
  }

  @Override
  public InputStream getInputStream() {
    return is;
  }

  @Override
  public void readLine(String[] columns) {
    LocalDate runDate = LocalDate.parse(columns[4]);
    if (result == null || runDate.isAfter(result)) {
      result = runDate;
    }
  }

  public LocalDate getResult() {
    return result;
  }
}
