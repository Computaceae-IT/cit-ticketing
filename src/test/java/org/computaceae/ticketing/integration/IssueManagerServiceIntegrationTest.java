package org.computaceae.ticketing.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.computaceae.TestConfig;
import org.computaceae.ticketing.service.IssueManagerService;
import org.eclipse.egit.github.core.Issue;
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
import com.lib.cit.core.client.mail.FakeMailsClient;
import com.lib.cit.core.client.mail.MailsClient;
import com.lib.cit.core.dto.mail.MailHtmlDTO;

@ActiveProfiles("test")
@Import(TestConfig.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class IssueManagerServiceIntegrationTest {

  private static Logger log = LoggerFactory.getLogger(IssueManagerServiceIntegrationTest.class);

  @Autowired
  private FakeMailsClient fakeMailsClient;

  @MockBean
  private MailsClient mockMailsClient;

  @Autowired
  private IssueManagerService issueManagerService;

  private Issue issue;

  @Before
  public void ManageMock() {

    this.issue = new Issue();
    issue.setId(123l);
    issue.setBody("MOCK_BODY");
    issue.setHtmlUrl("MOCK_HTMLURL");
    issue.setTitle("MOCK_TITLE");

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
  public void sendMailWithEmptyValueTest() {
    try {
      Future<Boolean> resultFuture = this.issueManagerService.sendCreationIssueMail(null, null);
      assertFalse(resultFuture.get(5, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    Issue issue = new Issue();

    try {
      Future<Boolean> resultFuture = this.issueManagerService.sendCreationIssueMail(null, issue);
      assertFalse(resultFuture.get(5, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCreationIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(5, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    issue.setId(123l);

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCreationIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(5, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    issue.setBody("MOCK_BODY");

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCreationIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(5, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    issue.setHtmlUrl("MOCK_HTMLURL");

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCreationIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(5, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }
  }

  @Test
  public void sendMailTest() {

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCreationIssueMail("ci@mock.mail", issue);
      assertTrue(resultFuture.get(10, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }



    // /*** EMPTY PROPERTIES ***/
    // TicketDTO ticket = new TicketDTO();
    //
    // try {
    // this.ticketingService.create(ticket);
    // throw new AssertionError("No logicalBusinessException raised for create with null object");
    // } catch (LogicalBusinessException e) {
    // assertTrue(e.toString(), e.getListError() != null);
    // assertTrue(e.toString(), e.getListError().size() == 3);
    // // Title empty
    // assertTrue(e.getListError().get(0).toString(),
    // e.getListError().get(0) instanceof InconsistentEmptyValue);
    // assertTrue(e.getListError().get(0).toString(),
    // e.getListError().get(0).getKey().equals("ticket.title"));
    // // label empty
    // assertTrue(e.getListError().get(1).toString(),
    // e.getListError().get(1) instanceof InconsistentEmptyValue);
    // assertTrue(e.getListError().get(1).toString(),
    // e.getListError().get(1).getKey().equals("ticket.label"));
    // // label url
    // assertTrue(e.getListError().get(2).toString(),
    // e.getListError().get(2) instanceof InconsistentEmptyValue);
    // assertTrue(e.getListError().get(2).toString(),
    // e.getListError().get(2).getKey().equals("ticket.url"));
    //
    // } catch (Exception e) {
    // log.error(e.getMessage(), e);
    // throw new AssertionError(e);
    // }
    //
    // /*** ONLY TITLE ***/
    // ticket.setTitle("MOCK TITLE");
    //
    // try {
    // this.ticketingService.create(ticket);
    // throw new AssertionError("No logicalBusinessException raised for create with null object");
    // } catch (LogicalBusinessException e) {
    // assertTrue(e.toString(), e.getListError() != null);
    // assertTrue(e.toString(), e.getListError().size() == 2);
    //
    // // label empty
    // assertTrue(e.getListError().get(0).toString(),
    // e.getListError().get(0) instanceof InconsistentEmptyValue);
    // assertTrue(e.getListError().get(0).toString(),
    // e.getListError().get(0).getKey().equals("ticket.label"));
    //
    // // label url
    // assertTrue(e.getListError().get(1).toString(),
    // e.getListError().get(1) instanceof InconsistentEmptyValue);
    // assertTrue(e.getListError().get(1).toString(),
    // e.getListError().get(1).getKey().equals("ticket.url"));
    //
    // } catch (Exception e) {
    // log.error(e.getMessage(), e);
    // throw new AssertionError(e);
    // }
  }

}
