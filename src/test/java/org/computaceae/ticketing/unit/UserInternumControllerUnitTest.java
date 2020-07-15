package org.computaceae.ticketing.unit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.HashMap;
import org.computaceae.TestConfig;
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
@ActiveProfiles("test")
@Import(TestConfig.class)
@RunWith(SpringRunner.class)
public class UserInternumControllerUnitTest {

  @SuppressWarnings("unused")
  private static Logger log = LoggerFactory.getLogger(UserInternumControllerUnitTest.class);

  protected final ObjectMapper mapper = new ObjectMapper();

  @MockBean
  private TicketingService ticketingService;

  @MockBean
  private UserService UserService;

  @Autowired
  private MockMvc mockMvc;


  @Test
  @SuppressWarnings("serial")
  public void putUsersTest() throws Exception {

    this.mockMvc.perform(
        put("/internum/user/").content(mapper.writeValueAsString(new HashMap<String, String>() {
          {
            put("MOCK_USER1", "MOCK_USER1@MOCK.COM");
            put("MOCK_USER2", "MOCK_USER2@MOCK.COM");
            put("MOCK_USER3", "MOCK_USER3@MOCK.COM");
          }
        })).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
  }

}
