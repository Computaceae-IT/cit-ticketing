package org.computaceae.ticketing.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <b>UserServiceImpl</b>
 * <p>
 * <strong>Service</strong> that handles all user management
 * </p>
 * 
 * @author SiropOps
 * @version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

  @SuppressWarnings("unused")
  private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

  private final Map<String, String> mailUsers = new HashMap<String, String>();

  private String defaultMail;

  /**
   * UserServiceImpl constructor.
   * <p>
   * The constructor takes default mail
   * </p>
   * 
   * @param defaultMail the default email address
   */

  public UserServiceImpl(@Value("${app.admin.mail}") String defaultMail) {
    this.defaultMail = defaultMail;
  }

  /**
   * get email address from username
   * 
   * @param username unique connection id
   * @return a mail address
   */
  @Override
  public String getEmail(String username) {
    if (StringUtils.isEmpty(username)) {
      return this.defaultMail;
    }
    StringBuilder sb = new StringBuilder(username);
    sb.append(" <");
    if (this.mailUsers.containsKey(username)) {
      sb.append(this.mailUsers.get(username));
    } else {
      sb.append(this.defaultMail);
    }
    sb.append(">");
    return sb.toString();
  }

  /**
   * store on static variable couple username-email
   * 
   * @param users couple's map username-email
   */
  @Override
  public void addMailUser(Map<String, String> users) {
    if (users == null)
      return;
    for (Entry<String, String> entry : users.entrySet()) {
      if (!StringUtils.isEmpty(entry.getValue()) && !StringUtils.isEmpty(entry.getKey())) {
        this.mailUsers.put(entry.getKey(), entry.getValue());
      }
    }

  }


}
