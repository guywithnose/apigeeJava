package org.apigee.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.apigee.ApigeeEntity;
import org.apigee.tests.helpers.ApigeeTestFactory;
import org.apigee.tests.helpers.Elephant;
import org.apigee.tests.helpers.ElephantOverride;
import org.junit.Test;

@SuppressWarnings("static-method")
public class TestApigeeEntity {

  @Test
  public void testSave() {
    Elephant elephant = new Elephant();
    elephant.intValue = 67;
    elephant.stringValue = "testVal";
    elephant.save(ApigeeTestFactory.getService());
    Elephant loaded = ApigeeEntity.getById(ApigeeTestFactory.getService(), elephant.getId(), Elephant.class);
    assertEquals(67, loaded.intValue);
    assertEquals("testVal", loaded.stringValue);
    elephant.intValue = 123;
    elephant.stringValue = "new test Value";
    elephant.save(ApigeeTestFactory.getService());
    loaded = ApigeeEntity.getById(ApigeeTestFactory.getService(), elephant.getId(), Elephant.class);
    elephant.delete(ApigeeTestFactory.getService());
    assertEquals(123, loaded.intValue);
    assertEquals("new test Value", loaded.stringValue);
  }
  
  @Test
  public void testCollectionOverride() {
    ElephantOverride elephant = new ElephantOverride();
    elephant.intValue = 67;
    elephant.stringValue = "testVal";
    elephant.save(ApigeeTestFactory.getService());
    Elephant loaded = ApigeeEntity.getById(ApigeeTestFactory.getService(), elephant.getId(), Elephant.class);
    elephant.delete(ApigeeTestFactory.getService());
    assertEquals("elephant", loaded.type);
  }
  
  @Test
  public void testLoadById()
  {
    Elephant elephant = new Elephant();
    elephant.intValue = 79;
    elephant.stringValue = "pickles are yummy";
    elephant.save(ApigeeTestFactory.getService());
    Elephant elephant2 = new Elephant();
    elephant2.loadById(ApigeeTestFactory.getService(), elephant.getId());
    elephant2.delete(ApigeeTestFactory.getService());
    assertEquals(elephant.toString(), elephant2.toString());
  }

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

  @Test
  public void testInvalidId() {
    assertNull(ApigeeEntity.getById(ApigeeTestFactory.getService(), "badId", Elephant.class));
  }

}
