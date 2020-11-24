package org.computaceae.ticketing.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.computaceae.lib.core.dto.ticketing.TicketDTO;
import org.computaceae.lib.core.errors.container.CustomError;
import org.computaceae.lib.core.errors.container.value.InconsistentEmptyValue;
import org.computaceae.lib.core.errors.container.value.InconsistentEmptyValue.Type;
import org.computaceae.lib.core.errors.exception.LogicalBusinessException;
import org.computaceae.lib.core.utils.LocalDateUtils;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <b>TicketingServiceImpl</b>
 * <p>
 * <strong>Service</strong> that handles all ticket management
 * </p>
 * 
 * @author SiropOps
 * @version 1.0
 */
@Service
public class TicketingServiceImpl implements TicketingService {

  private static Logger log = LoggerFactory.getLogger(TicketingServiceImpl.class);

  private IssueService issueService;
  private LabelService labelService;

  private Pattern usernamePattern = Pattern.compile("(Username : .*)");

  final static List<String> OFFICIAL_LABEL_NAMES = Arrays.asList("bug", "missing blocking feature",
      "missing feature", "enhancement", "optional enhancement", "question");

  private final List<Label> labels = new ArrayList<>();

  private final Map<String, Label> instanceToLabel = new HashMap<>();

  @Autowired
  private UserService userService;

  @Autowired
  private IssueManagerService issueManagerService;

  private final String user;
  private final String repository;

  /**
   * TicketingServiceImpl constructor.
   * <p>
   * The constructor takes the Github connection token as an argument
   * </p>
   * 
   * @param token GitHub's token
   * @param user GitHub's user
   * @param repository GitHub's repository
   */
  public TicketingServiceImpl(@Value("${app.github.token}") String token,
      @Value("${app.github.user}") String user,
      @Value("${app.github.repository}") String repository) {
    if (StringUtils.isEmpty(token)) {
      throw new IllegalArgumentException("Token is empty");
    }
    if (StringUtils.isEmpty(user)) {
      throw new IllegalArgumentException("User is empty");
    }
    if (StringUtils.isEmpty(repository)) {
      throw new IllegalArgumentException("Repository is empty");
    }

    GitHubClient client = new GitHubClient();
    client.setOAuth2Token(token);
    this.issueService = new IssueService(client);
    this.labelService = new LabelService(client);
    this.user = user;
    this.repository = repository;

    /***** LABEL INITIALIZION ****/
    try {
      this.labels.addAll(this.labelService.getLabels(this.user, this.repository));
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    /***** INTANCE MAP INITIALIZION ****/
    for (Label label : this.labels) {
      if (label != null && !StringUtils.isEmpty(label.getName())
          && label.getName().endsWith("-instance")) {
        this.instanceToLabel.put(label.getName().substring(0, label.getName().length() - 9), label);
      }
    }
  }

  /**
   * Each night, list of all recently updated issues and send notifation mail
   * 
   * 
   */
  @Override
  @Scheduled(cron = "0 2 0 * * *")
  // At 00:02.
  public synchronized void getLastUpdatedIssues() {
    log.info("Started getLastUpdatedIssues function");
    LocalDate now = LocalDate.now();
    LocalDate updatedAt, closedAt;
    Period period;

    for (Issue issue : this.getAllIssues()) {
      if (issue != null && issue.getClosedAt() != null) {
        closedAt = LocalDateUtils.convertToLocalDate(issue.getClosedAt());
        period = Period.between(now, closedAt);
        log.debug("closed period " + period + "(y:" + period.getYears() + ",m:" + period.getMonths()
            + ",d:" + period.getDays() + ") for issue " + issue.getTitle() + "(" + issue.getId()
            + ")");

        if (period.getYears() == 0 && period.getMonths() == 0 && period.getDays() == -1) {
          this.issueManagerService
              .sendCloseIssueMail(this.userService.getEmail(this.getUsername(issue)), issue);
        }
      } else if (issue != null && issue.getUpdatedAt() != null && issue.getCreatedAt() != null
          && TimeUnit.SECONDS.convert(
              Math.abs(issue.getUpdatedAt().getTime() - issue.getCreatedAt().getTime()),
              TimeUnit.MILLISECONDS) > 30) {
        log.debug("UpdatedAt : " + issue.getUpdatedAt());
        log.debug("CreatedAt : " + issue.getCreatedAt());
        log.debug("Period UpdatedAt-CreatedAt : " + TimeUnit.SECONDS.convert(
            Math.abs(issue.getUpdatedAt().getTime() - issue.getCreatedAt().getTime()),
            TimeUnit.MILLISECONDS));
        updatedAt = LocalDateUtils.convertToLocalDate(issue.getUpdatedAt());
        period = Period.between(now, updatedAt);
        log.debug("update period " + period + "(y:" + period.getYears() + ",m:" + period.getMonths()
            + ",d:" + period.getDays() + ") for issue " + issue.getTitle() + "(" + issue.getId()
            + ")");

        if (period.getYears() == 0 && period.getMonths() == 0 && period.getDays() == -1) {
          this.issueManagerService
              .sendUpdateIssueMail(this.userService.getEmail(this.getUsername(issue)), issue);
        }
      }
    }
  }

  /**
   * Get all available issues on GitHub repository
   * 
   * @return list of labels
   * 
   */
  @Override
  public List<Label> getLabels() {
    List<Label> list = new ArrayList<>();

    for (Label label : this.labels) {
      if (label != null && OFFICIAL_LABEL_NAMES.contains(label.getName())) {
        list.add(label);
      }
    }

    Comparator<Label> compareByPriority = new Comparator<Label>() {
      @Override
      public int compare(Label o1, Label o2) {
        if (o1 == null) {
          return 1;
        }
        if (o2 == null) {
          return -1;
        }
        int o1Index = OFFICIAL_LABEL_NAMES.indexOf(o1.getName());
        int o2Index = OFFICIAL_LABEL_NAMES.indexOf(o2.getName());
        return Integer.compare(o1Index, o2Index);
      }
    };
    Collections.sort(list, compareByPriority);
    return list;
  }

  /**
   * Create a new ticket in GitHub repository
   * 
   * @return TicketDTO the new created object
   * 
   */
  @Override
  public TicketDTO create(TicketDTO ticket) {
    if (ticket == null) {
      throw new LogicalBusinessException(new InconsistentEmptyValue(Type.object, "ticket"));
    }
    Issue issue = this.constructIssue(ticket);
    try {
      issue = this.issueService.createIssue(this.user, this.repository, issue);
      ticket.setHtmlUrl(issue.getHtmlUrl());
      if (!StringUtils.isEmpty(ticket.getError())) {
        this.issueService.createComment(this.user, this.repository, issue.getNumber(),
            ticket.getError());
      }

      this.issueManagerService
          .sendCreationIssueMail(this.userService.getEmail(this.getUsername(issue)), issue);

      return ticket;
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  private String getModuleAbbreviation(String module) {
    switch (module) {
      case "collective":
        return "CLV";
      case "cultivated":
        return "CULT";
      case "is":
        return "IS";
      case "harvest":
        return "REC";
      case "user":
        return "USER";
      case "home":
        return "S.L";
      default:
        if (module.length() > 4) {
          return module.substring(0, 3).toUpperCase();
        } else {
          return module;
        }
    }
  }

  private String getUsername(Issue issue) {
    if (issue == null || StringUtils.isEmpty(issue.getBody())) {
      return null;
    }

    Matcher matcher = this.usernamePattern.matcher(issue.getBody());

    if (matcher.find() && !StringUtils.isEmpty(matcher.group(1))
        && matcher.group(1).length() > 11) {
      log.debug("Username : " + matcher.group(1).substring(11).trim());
      return matcher.group(1).substring(11).trim();
    }
    return null;
  }

  private List<Issue> getAllIssues() {

    List<Issue> list = new ArrayList<>();
    try {
      /**
       * Get all open issues
       */
      list.addAll(this.issueService.getIssues(this.user, this.repository, null));
      /**
       * Get all closed issues
       */
      list.addAll(
          this.issueService.getIssues(this.user, this.repository, new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
              put("state", "closed");
            }
          }));
      return list;
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return list;
  }

  private Issue constructIssue(TicketDTO ticket) {
    if (ticket == null) {
      throw new IllegalArgumentException("ticket is empty");
    }

    List<CustomError> errors = new ArrayList<>();

    if (StringUtils.isEmpty(ticket.getTitle())) {
      errors.add(new InconsistentEmptyValue(Type.property, "ticket.title"));
    }

    if (StringUtils.isEmpty(ticket.getLabel())) {
      errors.add(new InconsistentEmptyValue(Type.property, "ticket.label"));
    }

    if (StringUtils.isEmpty(ticket.getUrl())) {
      errors.add(new InconsistentEmptyValue(Type.property, "ticket.url"));
    }

    if (!errors.isEmpty()) {
      throw new LogicalBusinessException(errors);
    }

    Issue issue = new Issue();

    String abbr = null;

    // get the abbreviation from the module
    if (StringUtils.isEmpty(ticket.getModule())) {
      if (!StringUtils.isEmpty(ticket.getUrl())) {
        Pattern p = Pattern.compile("#/[a-z]+/");
        Matcher m = p.matcher(ticket.getUrl());

        if (m.find()) {
          String result = m.group();
          if (result != null && result.length() > 3) {
            String module = result.substring(2, result.length() - 1);
            abbr = this.getModuleAbbreviation(module);
          }
        } else {
          abbr = this.getModuleAbbreviation("home");
        }
      }
    } else {
      abbr = this.getModuleAbbreviation(ticket.getModule());
    }

    // construct title
    StringBuilder sb = new StringBuilder(!StringUtils.isEmpty(abbr) ? "[" + abbr + "] " : "");

    sb.append(ticket.getTitle());
    issue.setTitle(sb.toString());

    // construct the body of the issue
    String body = "Url : " + ticket.getUrl() + "\n"
        + (ticket.getMsg() != null ? "Error message : " + ticket.getMsg() + "\n" : "")
        + "Username : " + ticket.getAuthor() + "\n\n" + "Description : " + ticket.getDescription();

    issue.setBody(body);

    issue.setLabels(this.getLabels(ticket));

    return issue;
  }

  private List<Label> getLabels(TicketDTO ticket) {
    if (ticket == null) {
      throw new IllegalArgumentException("ticket is empty");
    }
    if (StringUtils.isEmpty(ticket.getLabel())) {
      throw new IllegalArgumentException("ticket.label is empty");
    }
    if (StringUtils.isEmpty(ticket.getUrl())) {
      throw new IllegalArgumentException("ticket.url is empty");
    }

    List<Label> labels = new ArrayList<>();
    for (Label label : this.labels) {
      if (ticket.getLabel().equals(label.getName())) {
        labels.add(label);
      }
    }
    if (labels.isEmpty()) {
      throw new LogicalBusinessException(new InconsistentEmptyValue(Type.property, "ticket.label"));
    }
    boolean findIntance = false;
    for (Entry<String, Label> entry : this.instanceToLabel.entrySet()) {
      if (ticket.getUrl().contains("//" + entry.getKey() + ".")) {
        labels.add(entry.getValue());
        findIntance = true;
      }
    }
    if (!findIntance) {
      labels.add(this.instanceToLabel.get("dev"));
    }
    return labels;
  }

}
