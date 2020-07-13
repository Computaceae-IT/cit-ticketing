package org.computaceae.ticketing.service;

import org.eclipse.egit.github.core.Issue;

public interface IssueManagerService {

  void sendCreationIssueMail(String mailTo, Issue issue);

  void sendUpdateIssueMail(String mailTo, Issue issue);

}
