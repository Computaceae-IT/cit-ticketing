//package org.computaceae.ticketing;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import org.apache.log4j.Logger;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import com.botalista.globals.controller.TicketController;
//import com.botalista.globals.entity.ticket.Ticket;
//import com.botalista.globals.service.TicketService;
//import com.botalista.globals.test.AbstractUnitTest;
//import com.botalista.securtiy.component.AuthenticationFacade;
//
//public class TicketControllerUnitTest extends AbstractUnitTest {
//
//
//  @SuppressWarnings("unused")
//  private static Logger log = Logger.getLogger(TicketControllerUnitTest.class);
//
//  @InjectMocks
//  private TicketController ticketController;
//
//  @Mock
//  private TicketService ticketService;
//  @Mock
//  private AuthenticationFacade authenticationService;
//
//  public TicketControllerUnitTest() {
//    super("ticket");
//  }
//
//  @Override
//  protected void setupBeforeTest() {
//    MockitoAnnotations.initMocks(this);
//    this.mockMvc = MockMvcBuilders.standaloneSetup(ticketController).alwaysDo(log()).build();
//  }
//
//  @Test
//  public void testGetLabels() throws Exception {
//    this.mockMvc.perform(get("/" + this.module + "/labels/").accept("application/json").characterEncoding("UTF-8")).andExpect(status().isOk());
//
//  }
//
//  @Test
//  public void testGetMilestones() throws Exception {
//    this.mockMvc.perform(get("/" + this.module + "/milestones/").accept("application/json").characterEncoding("UTF-8")).andExpect(status().isOk());
//
//  }
//
//
//  @Test
//  public void testGetAssignee() throws Exception {
//    this.mockMvc.perform(get("/" + this.module + "/assignees/").accept("application/json").characterEncoding("UTF-8")).andExpect(status().isOk());
//
//  }
//
//
//  @Test
//  public void testOpenTicket() throws Exception {
//    this.mockMvc.perform(post("/" + this.module + "/open/").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new Ticket()))).andDo(log()).andExpect(status().isOk());
//  }
//
//}
