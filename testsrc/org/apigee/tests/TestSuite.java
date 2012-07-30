/*
 * File: TestSuite.java Author: JSON.org
 */
package org.apigee.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * The Class TestSuite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
{ TestApigeeAuthentication.class, TestApigeeEntity.class, TestApigeeService.class })
public class TestSuite
{
    // Do Nothing
}