package org.apigee.tests;

import static org.junit.Assert.*;

import org.apigee.ApigeeService;
import org.apigee.tests.helpers.ApigeeTestFactory;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

@SuppressWarnings("static-method")
public class TestApigeeService {

  @Test
  public void testGetUrl() {
    assertThat(ApigeeTestFactory.getService().getUrl("/devices").toString(),
        Matchers.containsString("/devices"));
  }

  @Test
  public void testPostUrl() throws JSONException {
    JSONObject response = ApigeeTestFactory.getService()
        .postUrl("/devices", new JSONObject("{\"value\":1234}"));
    assertThat(
        response.toString(),
        Matchers.containsString("/devices"));
    String id = response.getJSONArray("entities").getJSONObject(0).getString("uuid");
    ApigeeTestFactory.getService().deleteUrl("/devices/" + id);
  }

  @Test
  public void testHttpConnect() {
    ApigeeService service = ApigeeTestFactory.getService();
    service.setSecure(false);
    assertThat(service.getUrl("/devices").toString(),
        Matchers.containsString("/devices"));
  }

}
