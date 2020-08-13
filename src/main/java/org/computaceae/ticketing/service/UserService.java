package org.computaceae.ticketing.service;

import java.util.Map;
import org.computaceae.ticketing.dto.UserDTO;

public interface UserService {

  String getEmail(String username);

  void addMailUser(Map<String, String> users);

  UserDTO getUsers();

}
