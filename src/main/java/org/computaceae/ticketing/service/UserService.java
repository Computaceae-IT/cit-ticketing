package org.computaceae.ticketing.service;

import java.util.Map;

public interface UserService {

  String getEmail(String username);

  void addMailUser(Map<String, String> users);

}
