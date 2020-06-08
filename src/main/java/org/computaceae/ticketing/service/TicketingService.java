package org.computaceae.ticketing.service;

import java.util.List;
import org.eclipse.egit.github.core.Label;
import com.lib.cit.core.dto.ticketing.TicketDTO;

public interface TicketingService {

  List<Label> getLabels();

  TicketDTO create(TicketDTO ticket);

}
