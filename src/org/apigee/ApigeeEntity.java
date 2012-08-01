package org.apigee;

import java.io.UnsupportedEncodingException;
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
  protected String uuid;
  
  /** The id. */
  protected String name;
  
  /** The type. */
  public String type;
  
  /**
   * Instantiates a new data object.
   */
  public ApigeeEntity()
  {
  }

  /**
   * Instantiates a new data object.
   * 
   * @param Name
   *          the name
   */
  public ApigeeEntity(ApigeeService service, String Name)
  {
    name = Name;
    loadById(service, name);
  }
  
  /**
   * Save.
   * 
   * @param service
   *          the service
   * @return true, if successful
   */
  public boolean save(ApigeeService service) {
    String collectionName = getCollectionName(this.getClass());
    if (name == null) {
      name = getUuid();
    }
    if (type == null) {
      type = collectionName;
    }
    if (exists(service)) {
      return service.putUrl(collectionName + "/" + getIndex(), toJSON()).has("entities");
    }
    JSONObject response = service.postUrl(collectionName, toJSON());
    try {
      uuid = response.getJSONArray("entities").getJSONObject(0)
          .getString("uuid");
      if (name == null) {
        name = uuid;
      }
      return true;
    } catch (JSONException e) {
      return false;
    }
  }
  
  /**
   * Delete.
   * 
   * @param service
   *          the service
   */
  public void delete(ApigeeService service) {
    String collectionName = getCollectionName(this.getClass());
    service.deleteUrl(collectionName + "/" + uuid);
  }

  /**
   * Gets the id.
   * 
   * @return the id
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * Sets the id.
   */
  public void setUuid(String Uuid) {
    uuid = Uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String Name) {
    this.name = Name;
  }

  private String getIndex() {
    if (uuid == null) {
      return name;
    }
    return uuid;
  }

  private boolean exists(ApigeeService service) {
    String collectionName = getCollectionName(this.getClass());
    return service.getUrl(collectionName + "/" + getIndex()).has("entities");
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
      if (name == null) {
        name = uuid;
      }
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
    service.postUrl(collectionName + "/" + uuid + "/" + connectionType + "/" + entityCollectionName + "/" + entity.getUuid());
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
    service.deleteUrl(collectionName + "/" + uuid + "/" + connectionType + "/" + entityCollectionName + "/" + entity.getUuid());
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
    String collectionName = getCollectionName(this.getClass());
    return handlePages(service, collectionName + "/" + uuid + "/"
        + connectionType + "?ql=" + query, classType);
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
      String query, Class<T> type)
  {
    String encodedQuery = "";
    try {
      encodedQuery = URLEncoder.encode(query, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    String collectionName = getCollectionName(type);
    return handlePages(service, collectionName + "?ql="
        + encodedQuery, type);
  }
  
  private static <T extends ApigeeEntity> List<T> handlePages(ApigeeService service, String url, Class<T> type)
  {
    List<T> resultList = new ArrayList<T>();
    try {
      JSONObject response = service.getUrl(url + "&limit=100");
      JSONArray entities = response.getJSONArray("entities");
      for (int i = 0; i < entities.length(); i++) {
        T item = loadFromJson(entities.getJSONObject(i), type);
        resultList.add(item);
      }
      while (response.has("cursor")) {
        response = service.getUrl(url + "&limit=100&cursor="
            + response.getString("cursor"));
        entities = response.getJSONArray("entities");
        for (int i = 0; i < entities.length(); i++) {
          T item = loadFromJson(entities.getJSONObject(i), type);
          resultList.add(item);
        }
      }
    } catch (JSONException e) {
      // Empty Search
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

  /* (non-Javadoc)
   * @see org.json.Jsonable#toString()
   */
  @Override
  public String toString()
  {
    return toJSON().toString();
  }

  /**
   * Get All.
   * 
   * @param type
   *          the type
   * @return the all
   */
  public static String getAllAsJson(ApigeeService service, Class<? extends ApigeeEntity> type)
  {
    JSONArray ja = new JSONArray();
    for(ApigeeEntity entity : search(service, "", type))
    {
      ja.put(entity.toJSON());
    }
    return ja.toString();
  }
  
  /**
   * Instantiate from json.
   * 
   * @param <T>
   *          the generic type
   * @param jo
   *          the jo
   * @param type
   *          the type
   * @return the fb user
   */
  public static <T extends ApigeeEntity> T instantiateFromJson(JSONObject jo,
      Class<T> type)
  {
    T dataObject = null;
    try {
      dataObject = type.getConstructor(String.class).newInstance(jo.getString("name"));
      dataObject.loadFromJson(jo);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dataObject;
  }

}
