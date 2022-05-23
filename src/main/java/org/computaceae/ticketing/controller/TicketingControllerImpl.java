package org.computaceae.ticketing.controller;

import java.util.List;
import org.computaceae.lib.core.controller.AbstractController;
import org.computaceae.lib.core.dto.ticketing.TicketDTO;
import org.computaceae.ticketing.service.TicketingService;
import org.eclipse.egit.github.core.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <b>TicketingControllerImpl</b>
 * <p>
 * <strong>Controller</strong> that handles all internum (without authentication) data on ticket
 * exchange
 * </p>
 * 
 * @author SiropOps
 * @version 1.0
 */
@RestController
@RequestMapping("/internum")
public class TicketingControllerImpl extends AbstractController {

  @Autowired
  private TicketingService ticketingService;

  /**
   * Get all available issues on GitHub repository
   * 
   * @return a http status [OK, NOT ACCEPTABLE, METHOD_NOT_ALLOWED] with a list of labels
   * 
   */
  @GetMapping("/labels")
  public ResponseEntity<List<Label>> getLabelsByInternumPass() {
    return new ResponseEntity<>(this.ticketingService.getLabels(), HttpStatus.OK);
  }

  /**
   * Post a new ticket in GitHub repository
   * 
   * @param ticket ticketDTO to createO
   * @return a http status [CREATED, NOT ACCEPTABLE, METHOD_NOT_ALLOWED] with the TicketDTO object
   *         created
   * 
   */
  @PostMapping("/")
  public @ResponseBody ResponseEntity<TicketDTO> post(@RequestBody TicketDTO ticket) {
    return new ResponseEntity<>(this.ticketingService.create(ticket), HttpStatus.CREATED);
  }
}
