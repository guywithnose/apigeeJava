package org.apigee;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.Jsonable;

import data.JavaCurl;

/**
 * The Class ApigeeAuthentication.
 */
public class ApigeeAuthentication extends Jsonable {

  /** The access token. */
  private String accessToken;
  
  /** The expires. */
  private int expires;

  /** The valid. */
  private boolean valid = false;

  /** The organization. */
  private String organization;

  /** The application. */
  private String application;

  /**
   * Instantiates a new apigee authentication.
   * 
   * @param org
   *          the org
   * @param app
   *          the app
   * @param AccessToken
   *          the access token
   * @param Expires
   *          the expires
   */
  public ApigeeAuthentication(String org, String app, String AccessToken, int Expires) {
    organization = org;
    application = app;
    accessToken = AccessToken;
    expires = Expires;
    valid = testConnection();
  }

  /**
   * Test connection.
   * 
   * @return true, if successful
   */
  private boolean testConnection() {
    ApigeeService service = new ApigeeService(this);
    JSONObject response = service.getUrl("");
    return response.has("entities") && !response.has("error");
  }

  /**
   * Instantiates a new apigee authentication.
   * 
   * @param org
   *          the org
   * @param app
   *          the app
   * @param authentication
   *          the authentication
   * @throws AuthenticationException
   *           the authentication exception
   */
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
  
  /**
   * Checks if is valid.
   * 
   * @return true, if is valid
   */
  public boolean isValid()
  {
    return valid;
  }

  /**
   * Gets the access token.
   * 
   * @return the access token
   */
  public String getAccessToken() {
    return accessToken;
  }

  /**
   * Gets the expires.
   * 
   * @return the expires
   */
  public int getExpires() {
    return expires;
  }

  /**
   * Gets the organization.
   * 
   * @return the organization
   */
  public String getOrganization() {
    return organization;
  }

  /**
   * Gets the application.
   * 
   * @return the application
   */
  public String getApplication() {
    return application;
  }
  
}
