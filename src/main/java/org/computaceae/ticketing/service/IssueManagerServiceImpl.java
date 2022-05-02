package org.computaceae.ticketing.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.computaceae.lib.core.client.mail.MailsClient;
import org.computaceae.lib.core.dto.mail.MailHtmlDTO;
import org.computaceae.lib.core.errors.container.CustomError;
import org.computaceae.lib.core.errors.container.value.InconsistentEmptyValue;
import org.computaceae.lib.core.errors.container.value.InconsistentEmptyValue.Type;
import org.computaceae.lib.core.errors.container.value.InconsistentPositiveValue;
import org.computaceae.lib.core.errors.exception.LogicalBusinessException;
import org.eclipse.egit.github.core.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * <b>IssueManagerServiceImpl</b>
 * <p>
 * <strong>Service</strong> that handles all notification by mail send to users
 * </p>
 * 
 * @author SiropOps
 * @version 1.0
 */
@Service
public class IssueManagerServiceImpl implements IssueManagerService {

  private static Logger log = LoggerFactory.getLogger(IssueManagerServiceImpl.class);

  private static final String NEW_ISSUE_MAIL_CONCERN = "Nouveau ticket / New ticket / Neues Ticket";
  private static final Map<String, String> NEW_ISSUE_TEXT_MAP = Stream.of(
      new AbstractMap.SimpleEntry<>("fr",
          "Vous venez d'ouvrir un nouveau ticket de support, merci de cliquer sur le lien ci-dessous pour suivre l'avancement de celui-ci"),
      new AbstractMap.SimpleEntry<>("en",
          "You have just opened a new support ticket, please click on the link below to follow its progress"),
      new AbstractMap.SimpleEntry<>("de",
          "Sie haben gerade ein neues Support-Ticket geöffnet. Klicken Sie auf den folgenden Link, um den Fortschritt zu verfolgen"))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


  private static final String UPDATE_ISSUE_MAIL_CONCERN = "Mise à jour / Update / Aktualisieren";
  private static final Map<String, String> UPDATE_ISSUE_TEXT_MAP = Stream.of(
      new AbstractMap.SimpleEntry<>("fr",
          "Un ticket de support a été mis à jour, merci de cliquer sur le lien ci-dessous pour suivre l'avancement de celui-ci"),
      new AbstractMap.SimpleEntry<>("en",
          "A support ticket has been updated, please click on the link below to follow its progress"),
      new AbstractMap.SimpleEntry<>("de",
          "Ein Support-Ticket wurde aktualisiert. Klicken Sie auf den folgenden Link, um den Fortschritt zu verfolgen"))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

  private static final String CLOSE_ISSUE_MAIL_CONCERN =
      "Le ticket a été traité / The issue has been processed / Das Problem wurde bearbeitet";
  private static final Map<String, String> CLOSE_ISSUE_TEXT_MAP = Stream.of(
      new AbstractMap.SimpleEntry<>("fr",
          "Un ticket de support a été traité, si le problème devrait persister, merci de cliquer sur le lien ci-dessous pour le ré-ouvrir"),
      new AbstractMap.SimpleEntry<>("en",
          "A support ticket has been processed, if the problem should persist please click on the link below to re-open it"),
      new AbstractMap.SimpleEntry<>("de",
          "Ein Support-Ticket wurde verarbeitet. Sollte das Problem weiterhin bestehen, klicken Sie auf den folgenden Link, um es erneut zu öffnen"))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


  private static final String ISSUE_TEMPLATE = "issue.ftlh";


  @Autowired
  private MailsClient mailsClient;

  @Autowired
  FreeMarkerConfigurer freeMarkerConfigurer;

  /**
   * Send a creation mail when a new issue is added
   * 
   * @param mailTo Destination email address
   * @param issue created issue
   * @return a boolean future. if true, the mail is sanded
   * 
   */
  @Async
  @Override
  public Future<Boolean> sendCreationIssueMail(String mailTo, Issue issue) {
    try {
      this.manageErrors(mailTo, issue);

      MailHtmlDTO mail = new MailHtmlDTO();
      mail.setTo(mailTo);
      mail.setConcern(NEW_ISSUE_MAIL_CONCERN);
      mail.setHtml(this.getHtmlBody(issue, NEW_ISSUE_MAIL_CONCERN, NEW_ISSUE_TEXT_MAP));
      this.mailsClient.send(mail);

      log.info("sendCreationIssueMail TO : " + mailTo);
    } catch (LogicalBusinessException e) {
      log.error(e.getListError().toString(), e);
      return new AsyncResult<Boolean>(false);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return new AsyncResult<Boolean>(false);
    }
    return new AsyncResult<Boolean>(true);
  }

  /**
   * Send a notification when a issue is updated
   * 
   * @param mailTo Destination email address
   * @param issue updated issue
   * @return a boolean future. if true, the mail is sanded
   * 
   */
  @Async
  @Override
  public Future<Boolean> sendUpdateIssueMail(String mailTo, Issue issue) {
    try {
      this.manageErrors(mailTo, issue);

      MailHtmlDTO mail = new MailHtmlDTO();
      mail.setTo(mailTo);
      mail.setConcern(UPDATE_ISSUE_MAIL_CONCERN);
      mail.setHtml(this.getHtmlBody(issue, UPDATE_ISSUE_MAIL_CONCERN, UPDATE_ISSUE_TEXT_MAP));
      this.mailsClient.send(mail);

      log.info("sendUpdateIssueMail TO : " + mailTo);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return new AsyncResult<Boolean>(false);
    }

    return new AsyncResult<Boolean>(true);
  }

  /**
   * Send a notification when a issue is closed
   * 
   * @param mailTo Destination email address
   * @param issue closed issue
   * @return a boolean future. if true, the mail is sanded
   * 
   */
  @Async
  @Override
  public Future<Boolean> sendCloseIssueMail(String mailTo, Issue issue) {
    try {
      this.manageErrors(mailTo, issue);

      MailHtmlDTO mail = new MailHtmlDTO();
      mail.setTo(mailTo);
      mail.setConcern(CLOSE_ISSUE_MAIL_CONCERN);
      mail.setHtml(this.getHtmlBody(issue, CLOSE_ISSUE_MAIL_CONCERN, CLOSE_ISSUE_TEXT_MAP));
      this.mailsClient.send(mail);

      log.info("sendCloseIssueMail TO : " + mailTo);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return new AsyncResult<Boolean>(false);
    }

    return new AsyncResult<Boolean>(true);
  }

  private void manageErrors(String mailTo, Issue issue) {
    List<CustomError> errors = new ArrayList<>();
    if (StringUtils.isEmpty(mailTo)) {
      errors.add(new InconsistentEmptyValue(Type.object, "mailTo"));
    }
    if (issue == null) {
      errors.add(new InconsistentEmptyValue(Type.object, "issue"));
    }
    if (issue != null) {
      if (issue.getId() <= 0) {
        errors.add(new InconsistentPositiveValue("issue.id", issue.getId()));
      }
      if (StringUtils.isEmpty(issue.getTitle())) {
        errors.add(new InconsistentEmptyValue(Type.property, "issue.title"));
      }
      if (StringUtils.isEmpty(issue.getBody())) {
        errors.add(new InconsistentEmptyValue(Type.property, "issue.body"));
      }
      if (StringUtils.isEmpty(issue.getHtmlUrl())) {
        errors.add(new InconsistentEmptyValue(Type.property, "issue.htmlUrl"));
      }

    }

    if (!errors.isEmpty()) {
      throw new LogicalBusinessException(errors);
    }
  }

  private String getHtmlBody(Issue issue, String title, Map<String, String> text) {
    Map<String, Object> data = new HashMap<>();
    data.put("mainTitle", title);
    data.put("text", text);

    data.put("id", issue.getId());
    data.put("title", issue.getTitle());

    List<String> contents = new ArrayList<String>();

    for (String content : Arrays.stream(issue.getBody().split("\\r?\\n"))
        .collect(Collectors.toList())) {
      if (!StringUtils.isEmpty(content) && content.length() > 100) {
        content = content.substring(0, 100) + "...";
      }
      contents.add(content);

    }
    data.put("content", contents);
    data.put("link", issue.getHtmlUrl());

    data.put("year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

    Writer writer = new StringWriter();
    try {
      Template template = freeMarkerConfigurer.getConfiguration().getTemplate(ISSUE_TEMPLATE);
      template.process(data, writer);
      writer.flush();
    } catch (IOException | TemplateException e) {
      log.error(e.getMessage(), e);
    }

    return writer.toString();
  }



}
