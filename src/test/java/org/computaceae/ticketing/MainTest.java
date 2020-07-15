package org.computaceae.ticketing;

import org.computaceae.CitTicketingApplication;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test-whitout-config")
public class MainTest {

  private static Logger log = LoggerFactory.getLogger(MainTest.class);

  @Test
  public void main() {
    try {
      CitTicketingApplication.main(new String[] {});
    } catch (Exception e) {
      log.warn(e.getMessage());
    }
  }
}
