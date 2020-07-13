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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import com.lib.cit.core.dto.ticketing.TicketDTO;
import com.lib.cit.core.errors.container.value.InconsistentEmptyValue;
import com.lib.cit.core.errors.container.value.InconsistentEmptyValue.Type;
import com.lib.cit.core.errors.exception.LogicalBusinessException;
import com.lib.cit.core.utils.LocalDateUtils;

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
  private final String user = "CJB-Geneve";
  private final String repository = "cit-ticketing";

  private final List<String> officialLabelNames = Arrays.asList("bug", "missing blocking feature",
      "missing feature", "enhancement", "optional enhancement", "question");

  private final List<Label> labels;

  private final Map<String, Label> instanceToLabel = new HashMap<>();

  @Autowired
  private UserService userService;

  @Autowired
  private IssueManagerService issueManagerService;


  /**
   * TicketingServiceImpl constructor.
   * <p>
   * The constructor takes the Github connection token as an argument
   * </p>
   * 
   * @param token GitHub's token
   */

  public TicketingServiceImpl(@Value("${app.token.github}") String token) {
    GitHubClient client = new GitHubClient();
    client.setOAuth2Token(token);
    this.issueService = new IssueService(client);
    this.labelService = new LabelService(client);

    /***** LABEL INITIALIZION ****/
    List<Label> l;
    try {
      l = this.labelService.getLabels(user, repository);
    } catch (IOException e) {
      l = new ArrayList<>();
      log.error(e.getMessage(), e);
    }
    this.labels = l;

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
  @Scheduled(cron = "0 2 0 * * *")
  // At 00:02.
  public void getLastUpdatedIssues() {
    LocalDate now = LocalDate.now();
    LocalDate updatedAt;
    Period period;

    for (Issue issue : this.getAllIssues()) {
      if (issue != null && issue.getUpdatedAt() != null
          && !issue.getUpdatedAt().equals(issue.getCreatedAt())) {
        updatedAt = LocalDateUtils.convertToLocalDate(issue.getUpdatedAt());
        period = Period.between(now, updatedAt);
        if (period.getDays() == 0) {
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
      if (label != null && officialLabelNames.contains(label.getName())) {
        list.add(label);
      }
    }

    Comparator<Label> compareByPriority = new Comparator<Label>() {
      @Override
      public int compare(Label o1, Label o2) {
        if (o1 == null)
          return 1;
        if (o2 == null)
          return -1;
        int o1Index = officialLabelNames.indexOf(o1.getName());
        int o2Index = officialLabelNames.indexOf(o2.getName());
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
      issue = this.issueService.createIssue(user, repository, issue);
      ticket.setHtmlUrl(issue.getHtmlUrl());
      if (!StringUtils.isEmpty(ticket.getError()))
        this.issueService.createComment(user, repository, issue.getNumber(), ticket.getError());

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
        if (module.length() > 4)
          return module.substring(0, 3).toUpperCase();
        else
          return module;
    }
  }


  private String getUsername(Issue issue) {
    if (issue == null || StringUtils.isEmpty(issue.getBody())
        || issue.getBody().split("\\n").length < 2
        || issue.getBody().split("\\n")[1].length() < 11) {
      return null;
    }
    return issue.getBody().split("\\n")[1].substring(11);
  }

  private List<Issue> getAllIssues() {
    try {
      return this.issueService.getIssues(user, repository, null);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return new ArrayList<>();
  }

  private Issue constructIssue(TicketDTO ticket) {

    if (ticket == null) {
      return null;
    }

    Issue issue = new Issue();

    String abbr = null;

    // get the abbreviation from the module
    if (ticket.getModule() == null) {
      if (ticket.getUrl() != null) {
        Pattern p = Pattern.compile("#/[a-z]+/");
        Matcher m = p.matcher(ticket.getUrl());

        if (m.find()) {
          String result = m.group();
          String module = result.substring(2, result.length() - 1);
          abbr = this.getModuleAbbreviation(module);
        } else {
          abbr = this.getModuleAbbreviation("home");
        }
      }
    } else {
      abbr = this.getModuleAbbreviation(ticket.getModule());
    }

    // construct title
    String title = abbr != null ? "[" + abbr + "] " : "";

    title += ticket.getTitle();
    issue.setTitle(title);

    // construct the body of the issue
    String body = "Url : " + ticket.getUrl() + "\n"
        + (ticket.getMsg() != null ? "Error message : " + ticket.getMsg() + "\n" : "")
        + "Username : " + ticket.getAuthor() + "\n\n" + "Description : " + ticket.getDescription();

    issue.setBody(body);

    issue.setLabels(this.getLabels(ticket));

    return issue;
  }

  private List<Label> getLabels(TicketDTO ticket) {
    List<Label> labels = new ArrayList<>();
    if (ticket != null && !StringUtils.isEmpty(ticket.getLabel())) {
      for (Label label : this.labels) {
        if (ticket.getLabel().equals(label.getName()))
          labels.add(label);
      }
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
