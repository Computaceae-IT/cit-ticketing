package org.computaceae.ticketing.service;

import java.io.IOException;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.lib.cit.core.dto.ticketing.TicketDTO;

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

  // TODO injecter les credential
  public TicketingServiceImpl() {
    GitHubClient client = new GitHubClient();
    client.setOAuth2Token("9fed390144911651a9b453cb4b35f1b00c634a38");
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

  @Override
  public TicketDTO create(TicketDTO ticket) {
    System.out.println(ticket);
    Issue issue = this.constructIssue(ticket);
    try {
      issue = this.issueService.createIssue(user, repository, issue);
      ticket.setHtmlUrl(issue.getHtmlUrl());
      if (!StringUtils.isEmpty(ticket.getError()))
        this.issueService.createComment(user, repository, issue.getNumber(), ticket.getError());

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


  public Issue constructIssue(TicketDTO ticket) {

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
    // String errorType = this.extractErrorType(ticket);
    String title = abbr != null ? "[" + abbr + "] " : "";
    // title += "CriticalError".equals(errorType) ? "(" + errorType + ") " : "";
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
