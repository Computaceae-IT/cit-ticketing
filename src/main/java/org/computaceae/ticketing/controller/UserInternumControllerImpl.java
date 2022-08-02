package org.computaceae.ticketing.controller;

import java.util.Collection;
import org.computaceae.lib.core.controller.AbstractController;
import org.computaceae.lib.core.dto.ticketing.UserRepresentationDTO;
import org.computaceae.ticketing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <b>UserInternumControllerImpl</b>
 * <p>
 * <strong>Controller</strong> that handles all internum (without authentication) data on users
 * exchange
 * </p>
 * 
 * @author SiropOps
 * @version 1.0
 */
@RestController
@RequestMapping("/internum/user")
public class UserInternumControllerImpl extends AbstractController {

  @Autowired
  private UserService userService;

  /**
   * put a couple username-email on volatile memory
   * 
   * @param users mutiple couple username-email to added
   * @return a http status; 200 is Ok
   * 
   */
  @PutMapping("/")
  public @ResponseBody ResponseEntity<Void> putUsers(@RequestBody UserRepresentationDTO users) {
    this.userService.addUserRepresentation(users);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * get DTO with all couple username-email on volatile memory
   * 
   * @return UserDTO object
   * 
   */
  @GetMapping("/")
  public @ResponseBody ResponseEntity<Collection<UserRepresentationDTO>> getUsers() {
    return new ResponseEntity<>(this.userService.getUsersRepresentation(), HttpStatus.OK);
  }
}
