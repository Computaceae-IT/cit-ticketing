package org.computaceae.ticketing.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.computaceae.lib.core.dto.ticketing.UserRepresentationDTO;
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


  private final static Map<String, UserRepresentationDTO> USERS_REPRESENTATION = new HashMap<>();


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
   * get email address from username and a instance's name
   * 
   * @param username unique connection id
   * @param instance unique
   * @return a mail address
   */
  @Override
  public String getEmail(String username, String instance) {
    if (StringUtils.isEmpty(username) || StringUtils.isEmpty(instance)) {
      return this.defaultMail;
    }
    StringBuilder sb = new StringBuilder(username);
    sb.append(" <");
    if (USERS_REPRESENTATION.containsKey(instance)
        && USERS_REPRESENTATION.get(instance).getUsers().containsKey(username)) {
      sb.append(USERS_REPRESENTATION.get(instance).getUsers().get(username));
    } else {
      sb.append(this.defaultMail);
    }
    sb.append(">");
    return sb.toString();
  }

  /**
   * store on static variable couple username-email
   * 
   * @param users couple's map username-email stored on UserRepresentationDTO
   */
  @Override
  public void addUserRepresentation(UserRepresentationDTO ur) {
    if (StringUtils
        .isEmpty(Optional.ofNullable(ur).orElse(new UserRepresentationDTO()).getInstance()))
      return;

    List<String> toR = Optional.ofNullable(ur.getManager()).orElse(new HashMap<>()).entrySet()
        .stream().filter(e -> StringUtils.isEmpty(e.getValue()) || StringUtils.isEmpty(e.getKey()))
        .map(Map.Entry::getKey).collect(Collectors.toList());

    toR.forEach(key -> ur.getManager().remove(key));
    
    
    toR = Optional.ofNullable(ur.getUsers()).orElse(new HashMap<>()).entrySet()
        .stream().filter(e -> StringUtils.isEmpty(e.getValue()) || StringUtils.isEmpty(e.getKey()))
        .map(Map.Entry::getKey).collect(Collectors.toList());

    toR.forEach(key -> ur.getUsers().remove(key));

    USERS_REPRESENTATION.put(ur.getInstance(), ur);
  }

  /**
   * get user dto with all stored email
   * 
   * @return a user dto
   */
  @Override
  public Collection<UserRepresentationDTO> getUsersRepresentation() {
    return USERS_REPRESENTATION.values();
  }

}
