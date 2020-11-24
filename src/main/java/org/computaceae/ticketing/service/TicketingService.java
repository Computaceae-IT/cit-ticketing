package org.computaceae.ticketing.service;

import java.util.List;
import org.computaceae.lib.core.dto.ticketing.TicketDTO;
import org.eclipse.egit.github.core.Label;

public interface TicketingService {

  List<Label> getLabels();

  TicketDTO create(TicketDTO ticket);

  void getLastUpdatedIssues();

}
