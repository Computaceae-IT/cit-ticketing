package org.computaceae.ticketing.unit;

import static org.junit.Assert.assertTrue;
import org.computaceae.TestConfig;
import org.computaceae.ticketing.service.TicketingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@RunWith(SpringRunner.class)
public class TicketingControllerUnitTest {

  @SuppressWarnings("unused")
  private static Logger log = LoggerFactory.getLogger(TicketingControllerUnitTest.class);

  protected final ObjectMapper mapper = new ObjectMapper();

  @MockBean
  private TicketingService ticketingService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void TODO() {
    assertTrue(true);
  }


  // @Test
  // public void testInfo() throws Exception {
  // for (String lang : CIT_CONSTANTS.languageAvailable)
  // this.mockMvc.perform(get("/internum/translate?lang=" + lang)).andExpect(status().isOk());
  // }

}
