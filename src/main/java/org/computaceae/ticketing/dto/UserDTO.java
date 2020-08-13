package org.computaceae.ticketing.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Map<String, String> users = new HashMap<String, String>();
  private Map<String, String> managers = new HashMap<String, String>();

  public Map<String, String> getUsers() {
    return users;
  }

  public void setUsers(Map<String, String> users) {
    this.users = users;
  }

  public Map<String, String> getManagers() {
    return managers;
  }

  public void setManagers(Map<String, String> managers) {
    this.managers = managers;
  }

  @Override
  public String toString() {
    return "UserDTO [users=" + users + ", managers=" + managers + "]";
  }

}
