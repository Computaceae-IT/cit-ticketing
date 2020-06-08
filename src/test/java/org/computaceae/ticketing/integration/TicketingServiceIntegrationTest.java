package org.computaceae.ticketing.integration;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class TicketingServiceIntegrationTest {

  private static Logger log = LoggerFactory.getLogger(TicketingServiceIntegrationTest.class);

  @Test
  public void TODO() {
    assertTrue(true);
  }

}
