package org.apigee.tests;

import static org.junit.Assert.*;

import org.apigee.ApigeeAuthentication;
import org.apigee.AuthenticationException;
import org.apigee.tests.helpers.ApigeeTestFactory;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

@SuppressWarnings("static-method")
public class TestApigeeAuthentication {

  @Test
  public void testAuthenticateAsOrganization() throws JSONException,
      AuthenticationException {
    ApigeeAuthentication auth = new ApigeeAuthentication(
        "apigeejava",
        "sandbox",
        new JSONObject(
            "{\"Organization\":{\"client_id\":\"b3U6KyBRGNivEeGvrRIxOwHVwQ\",\"client_secret\":\"b3U6qzEgQG_XMEt-ow9ejv9xmoqwWR0\"}}"));
    assertTrue(auth.isValid());
  }

  @Test
  public void testAuthenticateAsAdminUser() throws JSONException,
      AuthenticationException {
    ApigeeAuthentication auth = new ApigeeAuthentication(
        "apigeejava",
        "sandbox",
        new JSONObject(
            "{\"AdminUser\":{\"username\":\"apigeejava\",\"password\":\"javaapigee\"}}"));
    assertTrue(auth.isValid());
  }

  @Test
  public void testAuthenticateAsApplication() throws JSONException,
      AuthenticationException {
    ApigeeAuthentication auth = new ApigeeAuthentication(
        "apigeejava",
        "sandbox",
        new JSONObject(
            "{\"Application\":{\"org-name\":\"apigeejava\",\"app-name\":\"sandbox\",\"client_id\":\"YXA6Kz0n9divEeGvrRIxOwHVwQ\",\"client_secret\":\"YXA6VqSONm4xBPJlRkxOGbHZjO1EnGU\"}}"));
    assertTrue(auth.isValid());
  }

  @Test
  public void testAuthenticateAsApplicationUser() throws JSONException,
      AuthenticationException {
    ApigeeAuthentication auth = new ApigeeAuthentication(
        "apigeejava",
        "sandbox",
        new JSONObject(
            "{\"ApplicationUser\":{\"org-name\":\"apigeejava\",\"app-name\":\"sandbox\",\"username\":\"apigeeUser\",\"password\":\"userapigee\"}}"));
    assertTrue(auth.isValid());
  }

  @Test
  @SuppressWarnings("unused")
  public void testFailedAuthenticateAsApplicationUser() throws JSONException {
    try {
      new ApigeeAuthentication(
          "apigeejava",
          "sandbox",
          new JSONObject(
              "{\"ApplicationUser\":{\"org-name\":\"apigeejava2\",\"app-name\":\"sandbox\",\"username\":\"apigeeUser\",\"password\":\"userapigee\"}}"));
      fail("Should have thrown exception.");
    } catch (AuthenticationException e) {
      assertThat(e.getMessage(),
          Matchers.containsString("Could not find application"));
    }
  }

  @Test
  @SuppressWarnings("unused")
  public void testSavedAccessCode() throws JSONException {
    ApigeeAuthentication auth = new ApigeeAuthentication("apigeejava",
        "sandbox", "123", 3600);
    assertFalse(auth.isValid());
    ApigeeAuthentication validAuth = ApigeeTestFactory.getValidAuthentication();
    auth = new ApigeeAuthentication("apigeejava", "sandbox",
        validAuth.getAccessToken(), 3600);
    assertTrue(auth.isValid());
  }

}
