package org.computaceae.ticketing.service;

import java.util.concurrent.Future;

import org.eclipse.egit.github.core.Issue;

public interface IssueManagerService {

	Future<Boolean> sendCreationIssueMail(String mailTo, Issue issue);

	Future<Boolean> sendUpdateIssueMail(String mailTo, Issue issue);

	Future<Boolean> sendCloseIssueMail(String mailTo, Issue issue);

}
