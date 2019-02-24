package com.netflix.dpi.job.reader;

import java.io.InputStream;

/**
 * Eliminates duplicate code by allowing implementors to avoid the details of reading from an
 * InputStream.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public interface CsvClient {
  /**
   * @return the InputStream to read from
   */
  InputStream getInputStream();

  /**
   * Manipulates the data from a comma-separated line
   * 
   * @param columns
   */
  void readLine(String[] columns);
}
