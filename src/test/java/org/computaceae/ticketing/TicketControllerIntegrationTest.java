//package org.computaceae.ticketing;
//
//import com.botalista.globals.test.AbstractIntegrationTest;
//import org.apache.log4j.Logger;
//import org.junit.Test;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.test.web.servlet.ResultMatcher;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class TicketControllerIntegrationTest extends AbstractIntegrationTest {
//
//  private static Logger log = Logger.getLogger(TicketControllerIntegrationTest.class);
//
//  @Autowired
//  private FilterChainProxy springSecurityFilterChain;
//
//
//  protected String baseURL;
//  protected String module;
//
//
//  public TicketControllerIntegrationTest() {
//    this.module = "ticket";
//    this.baseURL = "/" + this.module + "/";
//  }
//
//  @Override
//  protected void setupBeforeTest() {
//    MockitoAnnotations.initMocks(this);
//    this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(this.springSecurityFilterChain).build();
//  }
//
//  private void testGetSecurityAccess(String path, int status) throws Exception {
//
//    ResultMatcher expected = status().isOk();
//
//    if (status == 401)
//      expected = status().isUnauthorized();
//
//    this.mockMvc.perform(get(this.baseURL + path).contentType(MediaType.APPLICATION_JSON)).andDo(log()).andExpect(expected);
//
//  }
//
//  private void testPostSecurityAccess(String objToJson, String path) throws Exception {
//
//    this.mockMvc.perform(post(this.baseURL + path).contentType(MediaType.APPLICATION_JSON).content(objToJson)).andDo(log()).andExpect(status().isUnauthorized());
//
//  }
//
//  @Test
//  public void testGetLabels() throws Exception {
//    this.testGetSecurityAccess("labels/", 401);
//  }
//
//  @Test
//  public void testGetMilestones() throws Exception {
//    this.testGetSecurityAccess("milestones/", 401);
//  }
//
//  @Test
//  public void testGetAssignees() throws Exception {
//    this.testGetSecurityAccess("assignees/", 401);
//  }
//
//  @Test
//  public void testOpenTicket() throws Exception {
//    this.testPostSecurityAccess(this.mapper.writeValueAsString(null), "open/");
//  }
//
//}
