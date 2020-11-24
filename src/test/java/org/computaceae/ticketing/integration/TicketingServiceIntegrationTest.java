package org.computaceae.ticketing.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Map;
import org.computaceae.lib.core.client.mail.FakeMailsClient;
import org.computaceae.lib.core.client.mail.MailsClient;
import org.computaceae.lib.core.dto.mail.MailHtmlDTO;
import org.computaceae.lib.core.dto.ticketing.TicketDTO;
import org.computaceae.lib.core.errors.container.value.InconsistentEmptyValue;
import org.computaceae.lib.core.errors.exception.LogicalBusinessException;
import org.computaceae.ticketing.ExtraConfig;
import org.computaceae.ticketing.service.TicketingService;
import org.computaceae.ticketing.service.TicketingServiceImpl;
import org.eclipse.egit.github.core.Label;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@Import(ExtraConfig.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class TicketingServiceIntegrationTest {

  private static Logger log = LoggerFactory.getLogger(TicketingServiceIntegrationTest.class);

  @Autowired
  private FakeMailsClient fakeMailsClient;

  @MockBean
  private MailsClient mockMailsClient;

  @Autowired
  private TicketingService ticketingService;

  @Before
  public void ManageMock() {

    when(mockMailsClient.send(any(MailHtmlDTO.class)))
        .thenAnswer(new Answer<Map<String, Object>>() {
          public Map<String, Object> answer(InvocationOnMock invocation) {
            Object[] args = invocation.getArguments();
            invocation.getMock();
            return fakeMailsClient.send((MailHtmlDTO) args[0]);
          }
        });
  }


  @Test
  public void createWithEmptyValueTest() {
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

    /*** EMPTY PROPERTIES ***/
    TicketDTO ticket = new TicketDTO();

    try {
      this.ticketingService.create(ticket);
      throw new AssertionError("No logicalBusinessException raised for create with null object");
    } catch (LogicalBusinessException e) {
      assertTrue(e.toString(), e.getListError() != null);
      assertTrue(e.toString(), e.getListError().size() == 3);
      // Title empty
      assertTrue(e.getListError().get(0).toString(),
          e.getListError().get(0) instanceof InconsistentEmptyValue);
      assertTrue(e.getListError().get(0).toString(),
          e.getListError().get(0).getKey().equals("ticket.title"));
      // label empty
      assertTrue(e.getListError().get(1).toString(),
          e.getListError().get(1) instanceof InconsistentEmptyValue);
      assertTrue(e.getListError().get(1).toString(),
          e.getListError().get(1).getKey().equals("ticket.label"));
      // label url
      assertTrue(e.getListError().get(2).toString(),
          e.getListError().get(2) instanceof InconsistentEmptyValue);
      assertTrue(e.getListError().get(2).toString(),
          e.getListError().get(2).getKey().equals("ticket.url"));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    /*** ONLY TITLE ***/
    ticket.setTitle("MOCK TITLE");

    try {
      this.ticketingService.create(ticket);
      throw new AssertionError("No logicalBusinessException raised for create with null object");
    } catch (LogicalBusinessException e) {
      assertTrue(e.toString(), e.getListError() != null);
      assertTrue(e.toString(), e.getListError().size() == 2);

      // label empty
      assertTrue(e.getListError().get(0).toString(),
          e.getListError().get(0) instanceof InconsistentEmptyValue);
      assertTrue(e.getListError().get(0).toString(),
          e.getListError().get(0).getKey().equals("ticket.label"));

      // label url
      assertTrue(e.getListError().get(1).toString(),
          e.getListError().get(1) instanceof InconsistentEmptyValue);
      assertTrue(e.getListError().get(1).toString(),
          e.getListError().get(1).getKey().equals("ticket.url"));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }
  }

  @Test
  public void createWithWrongLabelTest() {

    TicketDTO ticket = new TicketDTO();
    ticket.setTitle("MOCK TITLE");
    ticket.setLabel("FAKE_MOCK_LABEL");
    ticket.setUrl("MOCK_URL");

    try {
      this.ticketingService.create(ticket);
      throw new AssertionError("No logicalBusinessException raised for create with null object");
    } catch (LogicalBusinessException e) {
      assertTrue(e.getListError() != null);
      assertTrue(e.toString(), e.getListError().size() == 1);
      // label empty
      assertTrue(e.getListError().get(0).toString(),
          e.getListError().get(0) instanceof InconsistentEmptyValue);
      assertTrue(e.getListError().get(0).toString(),
          e.getListError().get(0).getKey().equals("ticket.label"));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

  }

  @Test
  public void contructorTest() {
    TicketingServiceImpl t;
    try {
      t = new TicketingServiceImpl("", "", "");
      throw new AssertionError("No IllegalArgumentException raised for create with null object");
    } catch (IllegalArgumentException e) {
      assertNotNull(e.getMessage());
      assertTrue(e.getMessage(), e.getMessage().contains("is empty"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      t = new TicketingServiceImpl("MOCK", "", "");
      throw new AssertionError("No IllegalArgumentException raised for create with null object");
    } catch (IllegalArgumentException e) {
      assertNotNull(e.getMessage());
      assertTrue(e.getMessage(), e.getMessage().contains("is empty"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      t = new TicketingServiceImpl("MOCK", "MOCK", "");
      throw new AssertionError("No IllegalArgumentException raised for create with null object");
    } catch (IllegalArgumentException e) {
      assertNotNull(e.getMessage());
      assertTrue(e.getMessage(), e.getMessage().contains("is empty"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    t = new TicketingServiceImpl("MOCK", "MOCK", "MOCK");
    assertNotNull(t.getLabels());
    assertTrue(t.getLabels().toString(), t.getLabels().isEmpty());
  }

  @Test
  public void getLastUpdatedIssuesTest() {
    try {
      this.ticketingService.getLastUpdatedIssues();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }
  }

  @Test
  public void getLabelsTest() {

    List<Label> labels = this.ticketingService.getLabels();

    assertNotNull(labels);
    assertFalse(labels.toString(), labels.isEmpty());
  }


}
