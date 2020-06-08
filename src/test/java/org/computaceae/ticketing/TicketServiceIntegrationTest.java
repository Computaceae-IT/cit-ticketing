//
//package org.computaceae.ticketing;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.apache.log4j.Logger;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.botalista.globals.entity.ticket.AssigneeDTO;
//import com.botalista.globals.entity.ticket.LabelDTO;
//import com.botalista.globals.entity.ticket.MilestoneDTO;
//import com.botalista.globals.entity.ticket.Ticket;
//import com.botalista.globals.entity.ticket.TicketDTO;
//import com.botalista.globals.service.EntityConstructorService;
//import com.botalista.globals.service.TicketService;
//import com.botalista.globals.test.AbstractIntegrationTest;
//
//
//public class TicketServiceIntegrationTest extends AbstractIntegrationTest {
//
//  @SuppressWarnings("unused")
//  private static Logger log = Logger.getLogger(TicketServiceIntegrationTest.class);
//
//  @Autowired
//  private TicketService ticketService;
//
//  @Autowired
//  protected EntityConstructorService entityConstructorService;
//
//
//  @Override
//  protected void setupBeforeTest() {
//    this.securityContext.setAuthentication(admin);
//  }
//
//  @Test
//  public void testGetLabels() {
//    List<LabelDTO> l = this.ticketService.getLabels();
//    assertNotNull(l);
//    assertTrue(!l.isEmpty());
//  }
//
//  @Test
//  public void testGetMilestones() {
//    List<MilestoneDTO> l = this.ticketService.getMilestones();
//    assertNotNull(l);
//    assertTrue(!l.isEmpty());
//  }
//
//  @Test
//  public void testGetAssignees() {
//    List<AssigneeDTO> l = this.ticketService.getAssignees();
//    assertNotNull(l);
//    assertTrue(!l.isEmpty());
//  }
//
//  @Test
//  public void testConstructTicketSL() {
//    Ticket ticket = new Ticket();
//    ticket.setTitle("Test Ticket");
//    ticket.setLabel("bug");
//    ticket.setAuthor("cibotalisa");
//    ticket.setUrl("http://localhost:3000/#/");
//    ticket.setModule("home");
//    checkTicketConstruction(ticket, "[S.L] Test Ticket", "bug");
//
//    // test with no module
//    ticket.setModule(null);
//    checkTicketConstruction(ticket, "[S.L] Test Ticket", "bug");
//  }
//
//  @Test
//  public void testListLabeling() {
//    for (LabelDTO l : this.ticketService.getLabels()) {
//      assertFalse("Not contain label : more info needed", "more info needed".equals(l.getName()));
//    }
//  }
//
//  @Test
//  public void testConstructTicketModule() {
//    // test with custom abbreviation
//    Ticket ticket = new Ticket();
//    ticket.setTitle("Test Ticket module");
//    ticket.setLabel("missing feature");
//    ticket.setAuthor("cibotalisa");
//    ticket.setUrl("http://localhost:3000/#/harvest/list/");
//    ticket.setModule("harvest");
//    checkTicketConstruction(ticket, "[REC] Test Ticket module", "missing feature");
//
//    // test with generic abbreviation
//    ticket.setUrl("http://localhost:3000/#/acquisition/list/");
//    ticket.setModule("acquisition");
//    checkTicketConstruction(ticket, "[ACQ] Test Ticket module", "missing feature");
//
//    // test abbreviation with module
//    ticket.setUrl("http://localhost:3000/#/animal/list/");
//    ticket.setModule(null);
//    checkTicketConstruction(ticket, "[ANI] Test Ticket module", "missing feature");
//  }
//
//
//  @Test
//  public void testConstructTicketWithCriticalError() {
//    // test with wrong label
//    Ticket ticket = new Ticket();
//    ticket.setTitle("Test Ticket with critical error");
//    ticket.setLabel("missing feature");
//    ticket.setAuthor("cibotalisa");
//    ticket.setUrl("http://localhost:3000/#/nomenclature/list/");
//    ticket.setModule("nomenclature");
//    ticket.setMsg(
//        "could not execute statement; SQL [n/a]; constraint [check_defaultparent_not_null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement");
//    ticket.setType("com.botalista.errors.container.CriticalError");
//    ticket.setError("Error Details \n\n Status : 405 Méthode Non Autorisée\n\n " + "type : com.botalista.errors.container.CriticalError\n\n method : POST\n\n " + "data :\n\n"
//        + "{\"nodeType\":{\"version\":0,\"key\":\"300\",\"value\":\"classis\",\"id\":300,\"nameNodeType\":\"classis\"},\"nameNode\":\"tutu2\",\"hybride\":false,\"autonym\":false,\"authority\":null,\"source\":null,\"oldID\":null}\n"
//        + "message :\n\n"
//        + "could not execute statement; SQL [n/a]; constraint [check_defaultparent_not_null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement");
//
//    checkTicketConstruction(ticket, "[NOM] (CriticalError) Test Ticket with critical error", "bug");
//
//    // test with right label;
//    ticket.setLabel("bug");
//    checkTicketConstruction(ticket, "[NOM] (CriticalError) Test Ticket with critical error", "bug");
//  }
//
//  private void checkTicketConstruction(Ticket ticket, String titleExpected, String labelExpected) {
//    TicketDTO ticketDTO = this.ticketService.constructTicket(ticket);
//    assertNotNull(ticketDTO);
//    assertNotNull(ticketDTO.getTitle());
//    assertEquals(ticketDTO.getTitle(), titleExpected);
//    assertNotNull(ticketDTO.getLabels());
//    assertTrue(!ticketDTO.getLabels().isEmpty());
//    assertEquals(ticketDTO.getLabels(), Arrays.asList(labelExpected));
//    assertNotNull(ticketDTO.getBody());
//    assertTrue(ticketDTO.getBody().contains("Url : " + ticket.getUrl()));
//    // TODO voir avec Cyril
//    // pourquoi dans ticketcontroller authenticationService.getTokenBasedAuthentication().getName()
//    // retourne cibotalista au lieu de cjb ?
//    // assertTrue(ticketDTO.getBody().contains("Username : cjb"));
//  }
//
//
//}
