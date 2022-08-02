package org.computaceae.ticketing.service;

import java.util.Collection;
import org.computaceae.lib.core.dto.ticketing.UserRepresentationDTO;

public interface UserService {

  String getEmail(String username, String instance);

  void addUserRepresentation(UserRepresentationDTO ur);

  Collection<UserRepresentationDTO> getUsersRepresentation();

}
