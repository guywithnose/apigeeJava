/*
 * File: javaCurl.java Author: Robert Bittle <guywithnose@gmail.com>
 */
package data;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class javaCurl.
 */
public class JavaCurl {

  /**
   * Gets the url.
   * 
   * @param url
   *          the url
   * @return the url
   */
  public static String getUrl(String url) {
    return getUrl(url, "GET", "", new HashMap<String, String>());
  }

  /**
   * Async get url.
   * 
   * @param url
   *          the url
   * @param method
   *          the method
   * @param data
   *          the data
   * @param headers
   *          the headers
   */
  @SuppressWarnings("unused")
  public static void asyncGetUrl(String url, String method, String data, Map<String, String> headers) {
    class aSync implements Runnable {

      private String urlASync;
      private String methodASync;
      private String dataAsync;
      private Map<String, String> headersAsync;

      @Override
      public void run() {
        getUrl(urlASync, methodASync, dataAsync, headersAsync);
      }

      public aSync(String Url, String Method, String Data, Map<String, String> Headers) {
        this.urlASync = Url;
        this.methodASync = Method;
        this.dataAsync = Data;
        this.headersAsync = Headers;
        run();
      }

    }
    // Run as thread
    aSync a = new aSync(url, method, data, headers);
  }

  /**
   * Get Url.
   * 
   * @param url
   *          the url
   * @param method
   *          the method
   * @param data
   *          the data
   * @param Headers
   *          the headers
   * @return the url
   */
  public static String getUrl(String url, String method, String data, Map<String, String> Headers) {
    try {
      URL u = new URL(url);
      HttpURLConnection http = (HttpURLConnection) u.openConnection();
      http.setRequestMethod(method);
      http.setUseCaches(false);
      http.setReadTimeout(0);
      http.setConnectTimeout(0);
      for(String key : Headers.keySet()) {
        http.addRequestProperty(key, Headers.get(key));
      }
      if (data.length() > 0) {
        http.setDoOutput(true);
        http.setDoInput(true);
        DataOutputStream wr = new DataOutputStream(http.getOutputStream());
        wr.writeBytes(data);
        wr.flush();
        wr.close();
      } else {
        http.connect();
      }

      InputStream is;
      StringBuilder stringBuilder;
      try {
        is = http.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        stringBuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          stringBuilder.append(line + "\n");
        }
      } catch (IOException e) {
        is = http.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        stringBuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          stringBuilder.append(line + "\n");
        }
//      System.err.println(u);
//      System.err.println(stringBuilder);
      }

      return stringBuilder.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

}
