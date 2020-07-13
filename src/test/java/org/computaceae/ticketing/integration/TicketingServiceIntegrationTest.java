package org.computaceae.ticketing.integration;

import static org.junit.Assert.assertTrue;
import org.computaceae.TestConfig;
import org.computaceae.ticketing.service.TicketingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import com.lib.cit.core.errors.container.value.InconsistentEmptyValue;
import com.lib.cit.core.errors.exception.LogicalBusinessException;

@ActiveProfiles("test")
@Import(TestConfig.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class TicketingServiceIntegrationTest {

  private static Logger log = LoggerFactory.getLogger(TicketingServiceIntegrationTest.class);

  @Autowired
  private TicketingService ticketingService;

  @Test
  public void createTest() {
    try {
      this.ticketingService.create(null);
      throw new AssertionError("No logicalBusinessException raised for create with null object");
    } catch (LogicalBusinessException e) {
      assertTrue(e.toString(), e.getListError() != null);
      assertTrue(e.toString(), e.getListError().size() == 1);
      assertTrue(e.getListError().get(0).toString(),
          e.getListError().get(0) instanceof InconsistentEmptyValue);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

  }

}
