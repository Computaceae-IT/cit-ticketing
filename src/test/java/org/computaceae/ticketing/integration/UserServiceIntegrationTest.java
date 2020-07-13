//package org.computaceae.ticketing.integration;
//
//import static org.junit.Assert.assertTrue;
//import org.computaceae.TestConfig;
//import org.computaceae.ticketing.service.TicketingService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//import com.lib.cit.core.dto.ticketing.TicketDTO;
//import com.lib.cit.core.errors.container.value.InconsistentEmptyValue;
//import com.lib.cit.core.errors.exception.LogicalBusinessException;
//
//@ActiveProfiles("test")
//@Import(TestConfig.class)
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = WebEnvironment.NONE)
//public class UserServiceIntegrationTest {
//
//  private static Logger log = LoggerFactory.getLogger(UserServiceIntegrationTest.class);
//
//  @Autowired
//  private TicketingService ticketingService;
//
//  @Test
//  public void createWithEmptyValueTest() {
//    try {
//      this.ticketingService.create(null);
//      throw new AssertionError("No logicalBusinessException raised for create with null object");
//    } catch (LogicalBusinessException e) {
//      assertTrue(e.toString(), e.getListError() != null);
//      assertTrue(e.toString(), e.getListError().size() == 1);
//      assertTrue(e.getListError().get(0).toString(),
//          e.getListError().get(0) instanceof InconsistentEmptyValue);
//    } catch (Exception e) {
//      log.error(e.getMessage(), e);
//      throw new AssertionError(e);
//    }
//
//    /*** EMPTY PROPERTIES ***/
//    TicketDTO ticket = new TicketDTO();
//
//    try {
//      this.ticketingService.create(ticket);
//      throw new AssertionError("No logicalBusinessException raised for create with null object");
//    } catch (LogicalBusinessException e) {
//      assertTrue(e.toString(), e.getListError() != null);
//      assertTrue(e.toString(), e.getListError().size() == 3);
//      // Title empty
//      assertTrue(e.getListError().get(0).toString(),
//          e.getListError().get(0) instanceof InconsistentEmptyValue);
//      assertTrue(e.getListError().get(0).toString(),
//          e.getListError().get(0).getKey().equals("ticket.title"));
//      // label empty
//      assertTrue(e.getListError().get(1).toString(),
//          e.getListError().get(1) instanceof InconsistentEmptyValue);
//      assertTrue(e.getListError().get(1).toString(),
//          e.getListError().get(1).getKey().equals("ticket.label"));
//      // label url
//      assertTrue(e.getListError().get(2).toString(),
//          e.getListError().get(2) instanceof InconsistentEmptyValue);
//      assertTrue(e.getListError().get(2).toString(),
//          e.getListError().get(2).getKey().equals("ticket.url"));
//
//    } catch (Exception e) {
//      log.error(e.getMessage(), e);
//      throw new AssertionError(e);
//    }
//
//    /*** ONLY TITLE ***/
//    ticket.setTitle("MOCK TITLE");
//
//    try {
//      this.ticketingService.create(ticket);
//      throw new AssertionError("No logicalBusinessException raised for create with null object");
//    } catch (LogicalBusinessException e) {
//      assertTrue(e.toString(), e.getListError() != null);
//      assertTrue(e.toString(), e.getListError().size() == 2);
//
//      // label empty
//      assertTrue(e.getListError().get(0).toString(),
//          e.getListError().get(0) instanceof InconsistentEmptyValue);
//      assertTrue(e.getListError().get(0).toString(),
//          e.getListError().get(0).getKey().equals("ticket.label"));
//
//      // label url
//      assertTrue(e.getListError().get(1).toString(),
//          e.getListError().get(1) instanceof InconsistentEmptyValue);
//      assertTrue(e.getListError().get(1).toString(),
//          e.getListError().get(1).getKey().equals("ticket.url"));
//
//    } catch (Exception e) {
//      log.error(e.getMessage(), e);
//      throw new AssertionError(e);
//    }
//  }
//
//  @Test
//  public void createWithWrongLabelTest() {
//
//    TicketDTO ticket = new TicketDTO();
//    ticket.setTitle("MOCK TITLE");
//    ticket.setLabel("FAKE_MOCK_LABEL");
//    ticket.setUrl("MOCK_URL");
//
//    try {
//      this.ticketingService.create(ticket);
//      throw new AssertionError("No logicalBusinessException raised for create with null object");
//    } catch (LogicalBusinessException e) {
//      assertTrue(e.getListError() != null);
//      assertTrue(e.toString(), e.getListError().size() == 1);
//      // label empty
//      assertTrue(e.getListError().get(0).toString(),
//          e.getListError().get(0) instanceof InconsistentEmptyValue);
//      assertTrue(e.getListError().get(0).toString(),
//          e.getListError().get(0).getKey().equals("ticket.label"));
//
//    } catch (Exception e) {
//      log.error(e.getMessage(), e);
//      throw new AssertionError(e);
//    }
//
//  }
//
//}
