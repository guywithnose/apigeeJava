package org.apigee;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.Jsonable;

/**
 * The Class ApigeeEntity.
 */
abstract public class ApigeeEntity extends Jsonable {

  /** The id. */
  protected String id;
  
  /** The type. */
  public String type;
  
  /**
   * Save.
   * 
   * @param service
   *          the service
   * @return true, if successful
   */
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
  
  /**
   * Delete.
   * 
   * @param service
   *          the service
   */
  public void delete(ApigeeService service) {
    String collectionName = getCollectionName(this.getClass());
    service.deleteUrl(collectionName + "/" + id);
  }

  /**
   * Gets the id.
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }
  
  /**
   * Gets the by id.
   * 
   * @param <T>
   *          the generic type
   * @param service
   *          the service
   * @param id
   *          the id
   * @param type
   *          the type
   * @return the by id
   */
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
  
  /**
   * Load by id.
   * 
   * @param service
   *          the service
   * @param ID
   *          the id
   * @return true, if successful
   */
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
  
  /**
   * Connect.
   * 
   * @param service
   *          the service
   * @param connectionType
   *          the connection type
   * @param entity
   *          the entity
   */
  public void connect(ApigeeService service, String connectionType, ApigeeEntity entity)
  {
    String collectionName = getCollectionName(this.getClass());
    String entityCollectionName = getCollectionName(entity.getClass());
    service.postUrl(collectionName + "/" + id + "/" + connectionType + "/" + entityCollectionName + "/" + entity.getId());
  }
  
  /**
   * Disconnect.
   * 
   * @param service
   *          the service
   * @param connectionType
   *          the connection type
   * @param entity
   *          the entity
   */
  public void disconnect(ApigeeService service, String connectionType, ApigeeEntity entity)
  {
    String collectionName = getCollectionName(this.getClass());
    String entityCollectionName = getCollectionName(entity.getClass());
    service.deleteUrl(collectionName + "/" + id + "/" + connectionType + "/" + entityCollectionName + "/" + entity.getId());
  }
  
  /**
   * Gets the connection.
   * 
   * @param <T>
   *          the generic type
   * @param service
   *          the service
   * @param connectionType
   *          the connection type
   * @param classType
   *          the type
   * @return the connection
   */
  public <T extends ApigeeEntity> List<T> getConnection(ApigeeService service, String connectionType, Class<T> classType)
  {
    return getConnection(service, connectionType, classType, "");
  }
  
  /**
   * Gets the connection.
   * 
   * @param <T>
   *          the generic type
   * @param service
   *          the service
   * @param connectionType
   *          the connection type
   * @param classType
   *          the type
   * @param query
   *          the query
   * @return the connection
   */
  public <T extends ApigeeEntity> List<T> getConnection(ApigeeService service, String connectionType, Class<T> classType, String query)
  {
    List<T> resultList = new ArrayList<T>();
    try {
      String collectionName = getCollectionName(this.getClass());
      JSONObject response = service.getUrl(collectionName + "/" + id + "/" + connectionType + "?ql=" + query);
      JSONArray entities = response.getJSONArray("entities");
      for (int i = 0; i < entities.length(); i++) {
        T item = loadFromJson(entities.getJSONObject(i), classType);
        resultList.add(item);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resultList;
  }

  /**
   * Search.
   * 
   * @param <T>
   *          the generic type
   * @param service
   *          the service
   * @param query
   *          the query
   * @param type
   *          the type
   * @return the list
   */
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
  
  /**
   * Gets the collection name.
   * 
   * @param <T>
   *          the generic type
   * @param type
   *          the type
   * @return the collection name
   */
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
