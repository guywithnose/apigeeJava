package org.apigee.tests.helpers;

import org.apigee.ApigeeAuthentication;
import org.apigee.ApigeeService;
import org.json.JSONObject;

/**
 * A factory for creating ApigeeTest objects.
 */
public class ApigeeTestFactory {

  /** The authentication. */
  public static ApigeeAuthentication authentication;

  /** The service. */
  public static ApigeeService service;

  /**
   * Gets the valid authentication.
   * 
   * @return the valid authentication
   */
  public static ApigeeAuthentication getValidAuthentication() {
    if (authentication == null) {
      try {
        return new ApigeeAuthentication(
            "apigeejava",
            "sandbox",
            new JSONObject(
                "{\"Organization\":{\"client_id\":\"b3U6KyBRGNivEeGvrRIxOwHVwQ\",\"client_secret\":\"b3U6qzEgQG_XMEt-ow9ejv9xmoqwWR0\"}}"));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return authentication;
  }

  /**
   * Gets the service.
   * 
   * @return the service
   */
  public static ApigeeService getService() {
    if (service == null) {
      service = new ApigeeService(getValidAuthentication());
    }
    return service;
  }

}
