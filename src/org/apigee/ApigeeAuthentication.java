package org.apigee;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.Jsonable;

import data.JavaCurl;

public class ApigeeAuthentication extends Jsonable {

  private String accessToken;
  
  private int expires;

  private boolean valid = false;

  private String organization;

  private String application;

  public ApigeeAuthentication(String org, String app, String AccessToken, int Expires) {
    organization = org;
    application = app;
    accessToken = AccessToken;
    expires = Expires;
    valid = testConnection();
  }

  private boolean testConnection() {
    ApigeeService service = new ApigeeService(this);
    JSONObject response = service.getUrl("");
    return response.has("entities") && !response.has("error");
  }

  public ApigeeAuthentication(String org, String app, JSONObject authentication) throws AuthenticationException {
    organization = org;
    application = app;
    String url = "";
    JSONObject response = null;
    try {
      if (authentication.has("Organization")) {
        url = "http://api.usergrid.com/management/token?grant_type=client_credentials&client_id="
            + authentication.getJSONObject("Organization").getString("client_id")
            + "&client_secret="
            + authentication.getJSONObject("Organization").getString("client_secret");
      } else if (authentication.has("AdminUser")) {
        url = "http://api.usergrid.com/management/token?grant_type=password&username="
            + authentication.getJSONObject("AdminUser").getString("username")
            + "&password="
            + authentication.getJSONObject("AdminUser").getString("password");
      } else if (authentication.has("Application")) {
        url = "http://api.usergrid.com/" + authentication.getJSONObject("Application").getString("org-name")
            + "/" + authentication.getJSONObject("Application").getString("app-name")
            + "/token?grant_type=client_credentials&client_id="
            + authentication.getJSONObject("Application").getString("client_id")
            + "&client_secret="
            + authentication.getJSONObject("Application").getString("client_secret");
      } else if (authentication.has("ApplicationUser")) {
        url = "http://api.usergrid.com/" + authentication.getJSONObject("ApplicationUser").getString("org-name")
            + "/" + authentication.getJSONObject("ApplicationUser").getString("app-name")
            + "/token?grant_type=password&username="
            + authentication.getJSONObject("ApplicationUser").getString("username")
            + "&password="
            + authentication.getJSONObject("ApplicationUser").getString("password");
      }
      try {
        response = new JSONObject(JavaCurl.getUrl(url));
        if (response.has("access_token") && response.has("expires_in")) {
          accessToken = response.getString("access_token");
          expires = response.getInt("expires_in");
          valid = true;
          return;
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
      throw new AuthenticationException("Invalid Authentication" + (response != null ? response.toString() : ""));
  }
  
  public boolean isValid()
  {
    return valid;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public int getExpires() {
    return expires;
  }

  public String getOrganization() {
    return organization;
  }

  public String getApplication() {
    return application;
  }
  
}
