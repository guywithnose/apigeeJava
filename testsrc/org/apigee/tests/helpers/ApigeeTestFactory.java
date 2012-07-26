package org.apigee.tests.helpers;

import org.apigee.ApigeeAuthentication;
import org.apigee.ApigeeService;
import org.json.JSONObject;

public class ApigeeTestFactory {

  public static ApigeeAuthentication authentication;

  public static ApigeeService service;

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

  public static ApigeeService getService() {
    if (service == null) {
      service = new ApigeeService(getValidAuthentication());
    }
    return service;
  }

}
