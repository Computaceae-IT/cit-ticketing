package org.computaceae.ticketing.unit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.computaceae.lib.core.dto.ticketing.TicketDTO;
import org.computaceae.ticketing.ExtraConfig;
import org.computaceae.ticketing.service.TicketingService;
import org.computaceae.ticketing.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test-with-mocked-security")
@Import({ExtraConfig.class})
public class TicketingControllerUnitTest {

  @SuppressWarnings("unused")
  private static Logger log = LoggerFactory.getLogger(TicketingControllerUnitTest.class);

  protected final ObjectMapper mapper = new ObjectMapper();

  @MockBean
  private TicketingService ticketingService;

  @MockBean
  private UserService UserService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void getLabelsByInternumPassTest() throws Exception {
    this.mockMvc.perform(get("/internum/labels")).andExpect(status().isOk());
  }

  @Test
  public void postTest() throws Exception {
    this.mockMvc.perform(post("/internum/").content(mapper.writeValueAsString(new TicketDTO()))
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
  }

}
