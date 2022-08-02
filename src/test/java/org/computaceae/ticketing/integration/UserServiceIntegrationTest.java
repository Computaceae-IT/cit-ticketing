package org.computaceae.ticketing.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Collection;
import org.computaceae.lib.core.dto.ticketing.UserRepresentationDTO;
import org.computaceae.ticketing.ExtraConfig;
import org.computaceae.ticketing.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@Import(ExtraConfig.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class UserServiceIntegrationTest {

  private static Logger log = LoggerFactory.getLogger(UserServiceIntegrationTest.class);

  @Autowired
  private UserService userService;

  @Value("${app.admin.mail}")
  private String defaultMail;

  @Test
  public void addMailUserWithEmptyValueTest() {

    try {
      this.userService.addUserRepresentation(null);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      this.userService.addUserRepresentation(new UserRepresentationDTO());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    UserRepresentationDTO ur = new UserRepresentationDTO();

    try {
      ur.setInstance("INSTANCE");
      this.userService.addUserRepresentation(ur);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    ur.getUsers().put("MOCK", null);

    try {
      this.userService.addUserRepresentation(ur);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    assertTrue(this.userService.getEmail("MOCK", "INSTANCE") + ":" + this.defaultMail,
        this.userService.getEmail("MOCK", "INSTANCE").contains(this.defaultMail));

  }

  @Test
  public void addMailUserTest() {

    UserRepresentationDTO ur = new UserRepresentationDTO();
    ur.setInstance("INSTANCE");
    ur.getUsers().put("MOCK1", "MOCK1");

    try {
      this.userService.addUserRepresentation(ur);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      ur.getUsers().put("MOCK2", "MOCK2");
      this.userService.addUserRepresentation(ur);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    assertTrue(this.userService.getEmail("MOCK1", "INSTANCE"),
        this.userService.getEmail("MOCK1", "INSTANCE").equals("MOCK1 <MOCK1>"));

    assertTrue(this.userService.getEmail("MOCK2", "INSTANCE"),
        this.userService.getEmail("MOCK2", "INSTANCE").equals("MOCK2 <MOCK2>"));

    assertTrue(this.userService.getEmail("MOCK3", "INSTANCE"),
        this.userService.getEmail("MOCK3", "INSTANCE").equals("MOCK3 <" + this.defaultMail + ">"));

  }

  @Test
  public void addMailManagerWithEmptyValueTest() {
    UserRepresentationDTO ur = new UserRepresentationDTO();
    ur.setInstance("INSTANCE");
    ur.getManager().put(null, "MOCK");


    try {
      this.userService.addUserRepresentation(ur);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    ur.getManager().put("MOCK", null);

    try {
      this.userService.addUserRepresentation(ur);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }
    Collection<UserRepresentationDTO> users = this.userService.getUsersRepresentation();

    assertNotNull(users);
    assertTrue(users.stream().filter(u -> "INSTANCE".equals(u.getInstance())).count() == 1);

    ur = users.stream().filter(u -> "INSTANCE".equals(u.getInstance())).findFirst().get();

    assertNotNull(ur.getManager());
    assertTrue(ur.getManager().isEmpty());

  }

  @Test
  public void addMailManagerTest() {

    UserRepresentationDTO ur = new UserRepresentationDTO();
    ur.setInstance("INSTANCE");
    ur.getManager().put("MOCK1", "MOCK1@MOCK");

    try {
      this.userService.addUserRepresentation(ur);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }
    ur.getManager().put("MOCK2", "MOCK2@MOCK");
    try {
      this.userService.addUserRepresentation(ur);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    Collection<UserRepresentationDTO> users = this.userService.getUsersRepresentation();

    assertNotNull(users);
    assertTrue(users.stream().filter(u -> "INSTANCE".equals(u.getInstance())).count() == 1);

    ur = users.stream().filter(u -> "INSTANCE".equals(u.getInstance())).findFirst().get();


    assertNotNull(ur);
    assertNotNull(ur.getManager());
    assertTrue(ur.getManager().toString(), ur.getManager().containsKey("MOCK1"));
    assertTrue(ur.getManager().toString(), ur.getManager().containsKey("MOCK2"));
    assertFalse(ur.getManager().toString(), ur.getManager().containsKey("MOCK3"));

    assertTrue(ur.getManager().toString(), ur.getManager().get("MOCK1").equals("MOCK1@MOCK"));
    assertTrue(ur.getManager().toString(), ur.getManager().get("MOCK2").equals("MOCK2@MOCK"));
  }


}
