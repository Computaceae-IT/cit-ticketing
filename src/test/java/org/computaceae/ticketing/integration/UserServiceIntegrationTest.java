package org.computaceae.ticketing.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import org.computaceae.ticketing.ExtraConfig;
import org.computaceae.ticketing.dto.UserDTO;
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
      this.userService.addMailUser(null);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      this.userService.addMailUser(new HashMap<String, String>());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }


    try {
      this.userService.addMailUser(new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
          put(null, "MOCK");
        }
      });
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      this.userService.addMailUser(new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
          put("MOCK", null);
        }
      });
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    assertTrue(this.userService.getEmail("MOCK") + ":" + this.defaultMail,
        this.userService.getEmail("MOCK").contains(this.defaultMail));

  }

  @Test
  public void addMailUserTest() {


    try {
      this.userService.addMailUser(new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
          put("MOCK1", "MOCK1");
        }
      });
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      this.userService.addMailUser(new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
          put("MOCK2", "MOCK2");
        }
      });
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    assertTrue(this.userService.getEmail("MOCK1"),
        this.userService.getEmail("MOCK1").equals("MOCK1 <MOCK1>"));

    assertTrue(this.userService.getEmail("MOCK2"),
        this.userService.getEmail("MOCK2").equals("MOCK2 <MOCK2>"));

    assertTrue(this.userService.getEmail("MOCK3"),
        this.userService.getEmail("MOCK3").equals("MOCK3 <" + this.defaultMail + ">"));

  }

  @Test
  public void addMailManagerWithEmptyValueTest() {

    try {
      this.userService.addMailManager(null);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      this.userService.addMailManager(new HashMap<String, String>());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }


    try {
      this.userService.addMailManager(new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
          put(null, "MOCK");
        }
      });
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      this.userService.addMailManager(new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
          put("MOCK", null);
        }
      });
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }
    UserDTO users = this.userService.getUsers();

    assertNotNull(users);
    assertNotNull(users.getManagers());
    assertTrue(users.getManagers().isEmpty());

  }

  @Test
  public void addMailManagerTest() {


    try {
      this.userService.addMailManager(new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
          put("MOCK1", "MOCK1@MOCK");
        }
      });
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    try {
      this.userService.addMailManager(new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
          put("MOCK2", "MOCK2@MOCK");
        }
      });
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AssertionError(e);
    }

    UserDTO users = this.userService.getUsers();

    assertNotNull(users);
    assertNotNull(users.getManagers());
    assertTrue(users.getManagers().toString(), users.getManagers().containsKey("MOCK1"));
    assertTrue(users.getManagers().toString(), users.getManagers().containsKey("MOCK2"));
    assertFalse(users.getManagers().toString(), users.getManagers().containsKey("MOCK3"));

    assertTrue(users.getManagers().toString(),
        users.getManagers().get("MOCK1").equals("MOCK1@MOCK"));
    assertTrue(users.getManagers().toString(),
        users.getManagers().get("MOCK2").equals("MOCK2@MOCK"));
  }


}
