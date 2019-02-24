package com.netflix.dpi.job.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains common code for reading from a comma-separated InputStream using the CsvClient
 * interface.
 * 
 * @author John Amos (johnamos@stanfordalumni.org)
 *
 */
public class CsvReader {
  private static final Logger LOGGER = Logger.getLogger(CsvReader.class.getName());

  private CsvReader() {
  }

  /**
   * @param is
   * @return
   */
  public static void readStream(CsvClient client) {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
      readLines(in, client);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "", e);
    }
  }

  /**
   * @param in
   * @param client
   * @throws IOException
   */
  private static void readLines(BufferedReader in, CsvClient client) throws IOException {
    String line = in.readLine();
    while (line != null) {
      try {
        client.readLine(line.split(","));
      } catch (Exception e) {
        LOGGER.log(Level.FINE, "skipping line \"{0}\" due to \"{1} - {2}\"",
            new Object[] { line, e.getClass().getSimpleName(), e.getMessage() });
      }
      line = in.readLine();
    }
  }
}
