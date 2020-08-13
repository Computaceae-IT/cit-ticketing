package org.computaceae.ticketing.controller;

import java.util.Map;
import org.computaceae.ticketing.dto.UserDTO;
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
import com.lib.cit.core.controller.AbstractController;

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
  public @ResponseBody ResponseEntity<Void> putUsers(@RequestBody Map<String, String> users) {
    this.userService.addMailUser(users);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * put a couple username-email on volatile memory
   * 
   * @param users mutiple couple username-email to added
   * @return a http status; 200 is Ok
   * 
   */
  // TODO
  // @PutMapping("/manager/")
  // public @ResponseBody ResponseEntity<Void> putUsers(@RequestBody Map<String, String> users) {
  // this.userService.addMailUser(users);
  // return new ResponseEntity<>(HttpStatus.OK);
  // }



  /**
   * get DTO with all couple username-email on volatile memory
   * 
   * @return UserDTO object
   * 
   */
  @GetMapping("/")
  public @ResponseBody ResponseEntity<UserDTO> getUsers() {
    return new ResponseEntity<>(this.userService.getUsers(), HttpStatus.OK);
  }
}
