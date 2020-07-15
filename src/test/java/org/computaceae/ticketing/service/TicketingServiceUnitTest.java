package org.computaceae.ticketing.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.computaceae.TestConfig;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
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
import com.lib.cit.core.dto.ticketing.TicketDTO;

@ActiveProfiles("test")
@Import(TestConfig.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class TicketingServiceUnitTest {

  private static Logger log = LoggerFactory.getLogger(TicketingServiceUnitTest.class);

  @Autowired
  private FakeMailsClient fakeMailsClient;

  @MockBean
  private MailsClient mockMailsClient;

  @Mock
  private IssueService mockIssueService;

  @Mock
  private LabelService mockLabelService;

  @Autowired
  private TicketingService ticketingService;

  private final TicketDTO ticket = new TicketDTO();
  private final List<Label> labels = new ArrayList<>();
  private final Issue issue = new Issue();

  @Before
  public void manageMock() {

    try {

      FieldSetter.setField(ticketingService,
          ticketingService.getClass().getDeclaredField("issueService"), mockIssueService);
      FieldSetter.setField(ticketingService,
          ticketingService.getClass().getDeclaredField("labelService"), mockLabelService);

    } catch (NoSuchFieldException | SecurityException e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    when(mockMailsClient.send(any(MailHtmlDTO.class)))
        .thenAnswer(new Answer<Map<String, Object>>() {
          public Map<String, Object> answer(InvocationOnMock invocation) {
            Object[] args = invocation.getArguments();
            invocation.getMock();
            return fakeMailsClient.send((MailHtmlDTO) args[0]);
          }
        });


    ticket.setTitle("MOCK TITLE");
    ticket.setLabel(TicketingServiceImpl.OFFICIAL_LABEL_NAMES.get(1));
    ticket.setUrl("MOCK_URL");

    issue.setId(Integer.MAX_VALUE);
    issue.setTitle(ticket.getTitle());
    issue.setHtmlUrl("MOCK_HTML_URL");
    issue.setBody("MOCK_BODY");
    issue.setUpdatedAt(new Date());


    TicketingServiceImpl.OFFICIAL_LABEL_NAMES.forEach(name -> {
      Label l = new Label();
      l.setName(name);
      labels.add(l);
    });

    try {
      given(mockLabelService.getLabels(anyString(), anyString())).willReturn(labels);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    List<Issue> issues = new ArrayList<>();
    issues.add(this.issue);
    try {
      given(mockIssueService.createIssue(anyString(), anyString(), any(Issue.class)))
          .willReturn(this.issue);

      given(mockIssueService.getIssues(anyString(), anyString(), any())).willReturn(issues);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

  }


  @Test
  public void createTest() {

    try {
      this.ticketingService.create(this.ticket);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

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

}
