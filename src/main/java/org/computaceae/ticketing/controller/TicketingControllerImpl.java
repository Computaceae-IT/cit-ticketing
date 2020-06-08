package org.computaceae.ticketing.controller;

import java.util.List;
import org.computaceae.ticketing.service.TicketingService;
import org.eclipse.egit.github.core.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.lib.cit.core.controller.AbstractController;
import com.lib.cit.core.dto.ticketing.TicketDTO;

@RestController
@RequestMapping("/")
public class TicketingControllerImpl extends AbstractController {

  @Autowired
  private TicketingService ticketingService;

  @GetMapping("internum/labels")
  public ResponseEntity<List<Label>> getLabelsByInternumPass() {
    return new ResponseEntity<>(this.ticketingService.getLabels(), HttpStatus.OK);
  }

  @PostMapping("internum/")
  public @ResponseBody ResponseEntity<TicketDTO> post(@RequestBody TicketDTO ticket) {
    return new ResponseEntity<>(this.ticketingService.create(ticket), HttpStatus.CREATED);
  }
}
