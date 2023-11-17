package com.adaptris.management.exec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class ExecConfigTest {

  private TestInfo testInfo;

  @BeforeEach
  public void beforeTests(TestInfo info) {
    testInfo = info;
  }

  @Test
  public void testHasStartCommand() {
    ExecConfig execConfig = create();
    assertNull(execConfig.getStartCommand());
    assertFalse(execConfig.hasStartCommand());
    execConfig.setStartCommand("xxx");
    assertNotNull(execConfig.getStartCommand());
    assertTrue(execConfig.hasStartCommand());
    assertEquals("xxx", execConfig.getStartCommand());
  }

  @Test
  public void testHasStopCommand() {
    ExecConfig execConfig = create();
    assertNull(execConfig.getStopCommand());
    assertFalse(execConfig.hasStopCommand());
    execConfig.setStopCommand("xxx");
    assertNotNull(execConfig.getStopCommand());
    assertTrue(execConfig.hasStopCommand());
    assertEquals("xxx", execConfig.getStopCommand());
  }

  @Test
  public void testGetWorkingDirectory() {
    ExecConfig execConfig = create();
    assertNotNull(execConfig.getWorkingDirectory());
    assertEquals(System.getProperty("user.dir"), execConfig.getWorkingDirectory());
    execConfig.setWorkingDirectory("xxx");
    assertEquals("xxx", execConfig.getWorkingDirectory());

  }

  @Test
  public void testMaxStopWaitMillis() {
    ExecConfig execConfig = create();
    assertTrue(execConfig.maxStopWaitMillis() > 0);
    assertNull(execConfig.getMaxStopWaitMillis());
    execConfig.setMaxStopWaitMillis("1120");
    assertEquals(1120L, execConfig.maxStopWaitMillis());
  }

  @Test
  public void testMonitorMillis() {
    ExecConfig execConfig = create();
    assertTrue(execConfig.monitorMillis() > 0);
    assertNull(execConfig.getMonitorMillis());
    execConfig.setMonitorMillis("1120");
    assertEquals(1120L, execConfig.monitorMillis());
  }

  @Test
  public void testDebug() {
    ExecConfig execConfig = create();
    assertFalse(execConfig.debug());
    assertNull(execConfig.getDebug());
    execConfig.setDebug("true");
    assertTrue(execConfig.debug());
    assertEquals(Boolean.TRUE, execConfig.getDebug());
  }

  private ExecConfig create() {
    ExecConfig execConfig = new ExecConfig(testInfo.getTestMethod().get().getName());
    assertNotNull(execConfig.getIdentifier());
    assertEquals(testInfo.getTestMethod().get().getName(), execConfig.getIdentifier());
    return execConfig;
  }

}
