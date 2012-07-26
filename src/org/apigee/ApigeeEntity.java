package org.apigee;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.Jsonable;

abstract public class ApigeeEntity extends Jsonable {

  protected String id;
  
  public String type;
  
  public boolean save(ApigeeService service) {
    String collectionName = getCollectionName(this.getClass());
    if (type == null) {
      type = collectionName;
    }
    if (id == null) {
      JSONObject response = service.postUrl(collectionName, toJSON());
      try {
        id = response.getJSONArray("entities").getJSONObject(0)
            .getString("uuid");
        return true;
      } catch (JSONException e) {
        return false;
      }
    }
    return service.putUrl(collectionName + "/" + id, toJSON()).has("entities");
  }
  public void delete(ApigeeService service) {
    String collectionName = getCollectionName(this.getClass());
    service.deleteUrl(collectionName + "/" + id);
  }

  public String getId() {
    return id;
  }
  
  public static <T extends ApigeeEntity> T getById(ApigeeService service,
      String id, Class<T> type) {
    String collectionName = getCollectionName(type);
    JSONObject response = service.getUrl(collectionName + "/" + id);
    try {
      T item = loadFromJson(response.getJSONArray("entities").getJSONObject(0), type);
      return item;
    } catch (JSONException e) {
      return null;
    }
  }
  
  public boolean loadById(ApigeeService service, String ID) {
    String collectionName = getCollectionName(this.getClass());
    JSONObject response = service.getUrl(collectionName + "/" + ID);
    try {
      loadFromJson(response.getJSONArray("entities").getJSONObject(0));
      id = ID;
      return true;
    } catch (JSONException e) {
      return false;
    }
  }

  public static <T extends ApigeeEntity> List<T> search(ApigeeService service,
      String query, Class<T> type) {
    List<T> resultList = new ArrayList<T>();
    try {
      String encodedQuery = URLEncoder.encode(query, "UTF-8");
      String collectionName = getCollectionName(type);
      JSONObject response = service.getUrl(collectionName + "?ql=" + encodedQuery);
      JSONArray entities = response.getJSONArray("entities");
      for (int i = 0; i < entities.length(); i++) {
        T item = loadFromJson(entities.getJSONObject(i), type);
        resultList.add(item);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resultList;
  }
  
  protected static <T extends ApigeeEntity> String getCollectionName(Class<T> type)
  {
    try {
      return (String)type.getMethod("getCollectionName").invoke(null);
    } catch (Exception e) {
      if ("ElephantOverride".equals(type.getSimpleName())) {
        e.printStackTrace();
      }
      // If the class does not override collection name then use class name
      return type.getSimpleName().toLowerCase();
    }
  }

}
