package com.adaptris.management.exec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ExecConfigTest {
  @Rule
  public TestName testName = new TestName();

  @Test
  public void testHasStartCommand() {
    ExecConfig c = create();
    assertNull(c.getStartCommand());
    assertFalse(c.hasStartCommand());
    c.setStartCommand("xxx");
    assertNotNull(c.getStartCommand());
    assertTrue(c.hasStartCommand());
    assertEquals("xxx", c.getStartCommand());
  }

  @Test
  public void testHasStopCommand() {
    ExecConfig c = create();
    assertNull(c.getStopCommand());
    assertFalse(c.hasStopCommand());
    c.setStopCommand("xxx");
    assertNotNull(c.getStopCommand());
    assertTrue(c.hasStopCommand());
    assertEquals("xxx", c.getStopCommand());
  }

  @Test
  public void testGetWorkingDirectory() {
    ExecConfig c = create();
    assertNotNull(c.getWorkingDirectory());
    assertEquals(System.getProperty("user.dir"), c.getWorkingDirectory());
    c.setWorkingDirectory("xxx");
    assertEquals("xxx", c.getWorkingDirectory());

  }

  @Test
  public void testMaxStopWaitMillis() {
    ExecConfig c = create();
    assertTrue(c.maxStopWaitMillis() > 0);
    assertNull(c.getMaxStopWaitMillis());
    c.setMaxStopWaitMillis("1120");
    assertEquals(1120L, c.maxStopWaitMillis());
  }

  @Test
  public void testMonitorMillis() {
    ExecConfig c = create();
    assertTrue(c.monitorMillis() > 0);
    assertNull(c.getMonitorMillis());
    c.setMonitorMillis("1120");
    assertEquals(1120L, c.monitorMillis());
  }

  @Test
  public void testDebug() {
    ExecConfig c = create();
    assertFalse(c.debug());
    assertNull(c.getDebug());
    c.setDebug("true");
    assertTrue(c.debug());
    assertEquals(Boolean.TRUE, c.getDebug());
  }

  private ExecConfig create() {
    ExecConfig c = new ExecConfig(testName.getMethodName());
    assertNotNull(c.getIdentifier());
    assertEquals(testName.getMethodName(), c.getIdentifier());
    return c;
  }
}
