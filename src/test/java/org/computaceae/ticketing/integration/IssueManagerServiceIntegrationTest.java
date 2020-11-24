package org.computaceae.ticketing.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.computaceae.lib.core.client.mail.FakeMailsClient;
import org.computaceae.lib.core.client.mail.MailsClient;
import org.computaceae.lib.core.dto.mail.MailHtmlDTO;
import org.computaceae.ticketing.TestConfig;
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
  public void sendUpdateMailWithEmptyValueTest() {
    try {
      Future<Boolean> resultFuture = this.issueManagerService.sendUpdateIssueMail(null, null);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    Issue issue = new Issue();

    try {
      Future<Boolean> resultFuture = this.issueManagerService.sendUpdateIssueMail(null, issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendUpdateIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    issue.setId(123l);

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendUpdateIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    issue.setBody("MOCK_BODY");

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendUpdateIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    issue.setHtmlUrl("MOCK_HTMLURL");

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendUpdateIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }
  }

  @Test
  public void sendCreationMailWithEmptyValueTest() {
    try {
      Future<Boolean> resultFuture = this.issueManagerService.sendCreationIssueMail(null, null);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    Issue issue = new Issue();

    try {
      Future<Boolean> resultFuture = this.issueManagerService.sendCreationIssueMail(null, issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCreationIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    issue.setId(123l);

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCreationIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    issue.setBody("MOCK_BODY");

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCreationIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    issue.setHtmlUrl("MOCK_HTMLURL");

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCreationIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }
  }

  @Test
  public void sendCloseMailWithEmptyValueTest() {
    try {
      Future<Boolean> resultFuture = this.issueManagerService.sendCloseIssueMail(null, null);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    Issue issue = new Issue();

    try {
      Future<Boolean> resultFuture = this.issueManagerService.sendCloseIssueMail(null, issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCloseIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    issue.setId(123l);

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCloseIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    issue.setBody("MOCK_BODY");

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCloseIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    issue.setHtmlUrl("MOCK_HTMLURL");

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCloseIssueMail("mail@mail.com", issue);
      assertFalse(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }
  }

  @Test
  public void sendCreationIssueMailTest() {

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCreationIssueMail("ci@mock.mail", issue);
      assertTrue(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

  }

  @Test
  public void sendUpdateIssueMailTest() {

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendUpdateIssueMail("ci@mock.mail", issue);
      assertTrue(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

  }

  @Test
  public void sendCloseIssueMailTest() {

    try {
      Future<Boolean> resultFuture =
          this.issueManagerService.sendCloseIssueMail("ci@mock.mail", issue);
      assertTrue(resultFuture.get(30, TimeUnit.SECONDS));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

  }

}
