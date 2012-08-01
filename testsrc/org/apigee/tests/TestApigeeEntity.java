package org.apigee.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.apigee.ApigeeEntity;
import org.apigee.tests.helpers.ApigeeTestFactory;
import org.apigee.tests.helpers.Elephant;
import org.apigee.tests.helpers.ElephantOverride;
import org.junit.Test;

/**
 * The Class TestApigeeEntity.
 */
@SuppressWarnings("static-method")
public class TestApigeeEntity {

  /**
   * Tests the save method.
   */
  @Test
  public void testSave() {
    Elephant elephant = new Elephant();
    elephant.intValue = 67;
    elephant.stringValue = "testVal";
    elephant.save(ApigeeTestFactory.getService());
    Elephant loaded = ApigeeEntity.getById(ApigeeTestFactory.getService(), elephant.getUuid(), Elephant.class);
    assertEquals(67, loaded.intValue);
    assertEquals("testVal", loaded.stringValue);
    elephant.intValue = 123;
    elephant.stringValue = "new test Value";
    elephant.save(ApigeeTestFactory.getService());
    loaded = ApigeeEntity.getById(ApigeeTestFactory.getService(), elephant.getUuid(), Elephant.class);
    elephant.delete(ApigeeTestFactory.getService());
    assertEquals(123, loaded.intValue);
    assertEquals("new test Value", loaded.stringValue);
  }
  
  /**
   * Tests the collectionOverride method.
   */
  @Test
  public void testCollectionOverride() {
    ElephantOverride elephant = new ElephantOverride();
    elephant.intValue = 67;
    elephant.stringValue = "testVal";
    elephant.save(ApigeeTestFactory.getService());
    Elephant loaded = ApigeeEntity.getById(ApigeeTestFactory.getService(), elephant.getUuid(), Elephant.class);
    elephant.delete(ApigeeTestFactory.getService());
    assertEquals("elephant", loaded.type);
  }
  
  /**
   * Tests the loadById method.
   */
  @Test
  public void testLoadById()
  {
    Elephant elephant = new Elephant();
    elephant.intValue = 79;
    elephant.stringValue = "pickles are yummy";
    elephant.save(ApigeeTestFactory.getService());
    Elephant elephant2 = new Elephant();
    elephant2.loadById(ApigeeTestFactory.getService(), elephant.getUuid());
    elephant2.delete(ApigeeTestFactory.getService());
    assertEquals(elephant.toString(), elephant2.toString());
  }

  /**
   * Tests the search method.
   */
  @Test
  public void testSearch() {
    for(Elephant deleteMe : ApigeeEntity.search(ApigeeTestFactory.getService(), "intValue=47", Elephant.class)) {
      deleteMe.delete(ApigeeTestFactory.getService());
    }
    Elephant elephant = new Elephant();
    elephant.intValue = 47;
    elephant.save(ApigeeTestFactory.getService());
    Elephant elephant2 = new Elephant();
    elephant2.intValue = 67;
    elephant.save(ApigeeTestFactory.getService());
    List<Elephant> loaded = ApigeeEntity.search(ApigeeTestFactory.getService(), "intValue=47", Elephant.class);
    elephant.delete(ApigeeTestFactory.getService());
    elephant2.delete(ApigeeTestFactory.getService());
    assertEquals(1, loaded.size());
    assertEquals(47, loaded.get(0).intValue);
  }

  /**
   * Tests the invalidId method.
   */
  @Test
  public void testInvalidId() {
    assertNull(ApigeeEntity.getById(ApigeeTestFactory.getService(), "badId", Elephant.class));
  }

  /**
   * Tests the connection method.
   */
  @Test
  public void testConnection()
  {
    Elephant elephant = new Elephant();
    elephant.intValue = 23;
    elephant.stringValue = "testVal";
    elephant.save(ApigeeTestFactory.getService());
    Elephant elephant2 = new Elephant();
    elephant2.intValue = 12;
    elephant2.stringValue = "testVal";
    elephant2.save(ApigeeTestFactory.getService());
    elephant.connect(ApigeeTestFactory.getService(), "picks", elephant2);
    List<Elephant> result = elephant.getConnection(ApigeeTestFactory.getService(), "picks", Elephant.class);
    assertEquals(1, result.size());
    assertEquals(12, result.get(0).intValue);
    elephant.disconnect(ApigeeTestFactory.getService(), "picks", elephant2);
    assertEquals(0, elephant.getConnection(ApigeeTestFactory.getService(), "picks", Elephant.class).size());
    elephant.delete(ApigeeTestFactory.getService());
    elephant2.delete(ApigeeTestFactory.getService());
  }

}
