/*
 * File:         ApigeeService.java
 * Author:       Robert Bittle <guywithnose@gmail.com>
 */
package org.apigee;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import data.JavaCurl;

/**
 * The Class ApigeeService.
 */
public class ApigeeService {

  /** The host. */
  private String host;

  /** The is secure. */
  private Boolean isSecure = true;

  /** The authentication. */
  private ApigeeAuthentication authentication;

  /** The authentication header. */
  private Map<String, String> authenticationHeader = new HashMap<String, String>();

  /**
   * Instantiates a new apigee service.
   * 
   * @param auth
   *          the auth
   */
  public ApigeeService(ApigeeAuthentication auth) {
    this("api.usergrid.com", auth);
  }

  /**
   * Instantiates a new apigee service.
   * 
   * @param Host
   *          the host
   * @param auth
   *          the auth
   */
  private ApigeeService(String Host, ApigeeAuthentication auth) {
    host = Host;
    authentication = auth;
    authenticationHeader.put("Authorization",
        "Bearer " + authentication.getAccessToken());
  }

  /**
   * Gets the url.
   * 
   * @param url
   *          the url
   * @return the url
   */
  public JSONObject getUrl(String url) {
    String response = JavaCurl.getUrl(
        getProtocol() + host + "/" + authentication.getOrganization() + "/"
            + authentication.getApplication() + "/" + url, "GET", "",
        authenticationHeader);
    return convertResponse(response);
  }

  /**
   * Post url.
   * 
   * @param url
   *          the url
   * @param data
   *          the data
   * @return the jSON object
   */
  public JSONObject postUrl(String url, JSONObject data) {
    String response = JavaCurl.getUrl(
        getProtocol() + host + "/" + authentication.getOrganization() + "/"
            + authentication.getApplication() + "/" + url, "POST",
        data.toString(), authenticationHeader);
    return convertResponse(response);
  }

  /**
   * Put url.
   * 
   * @param url
   *          the url
   * @param data
   *          the data
   * @return the jSON object
   */
  public JSONObject putUrl(String url, JSONObject data) {
    Map<String, String> putHeader = new HashMap<String, String>(
        authenticationHeader);
    putHeader.put("Content-Type", "application/json; charset=utf-8");
    String response = JavaCurl.getUrl(
        getProtocol() + host + "/" + authentication.getOrganization() + "/"
            + authentication.getApplication() + "/" + url, "PUT",
        data.toString(), putHeader);
    return convertResponse(response);
  }

  /**
   * Delete url.
   * 
   * @param url
   *          the url
   * @return the jSON object
   */
  public JSONObject deleteUrl(String url) {
    String response = JavaCurl.getUrl(
        getProtocol() + host + "/" + authentication.getOrganization() + "/"
            + authentication.getApplication() + "/" + url, "DELETE", "",
        authenticationHeader);
    return convertResponse(response);
  }

  /**
   * Convert response.
   * 
   * @param response
   *          the response
   * @return the jSON object
   */
  private static JSONObject convertResponse(String response) {
    try {
      return new JSONObject(response);
    } catch (JSONException e) {
      System.out.println("Non-JSON response" + response);
      return new JSONObject();
    }
  }

  /**
   * Gets the protocol.
   * 
   * @return the protocol
   */
  private String getProtocol() {
    if (isSecure) {
      return "https://";
    }
    return "http://";
  }

  /**
   * Sets the secure.
   * 
   * @param secure
   *          the new secure
   */
  public void setSecure(boolean secure) {
    isSecure = secure;
  }

}
