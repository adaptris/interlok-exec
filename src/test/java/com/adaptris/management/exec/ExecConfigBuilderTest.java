package com.adaptris.management.exec;

import static com.adaptris.core.stubs.ObjectUtils.filterGetterWithNoSetter;
import static com.adaptris.core.stubs.ObjectUtils.getPrimitiveGetters;
import static com.adaptris.core.stubs.ObjectUtils.invokeGetter;
import static com.adaptris.management.exec.ExecConfigBuilder.EXEC_MONITOR_MS;
import static com.adaptris.management.exec.ExecConfigBuilder.EXEC_START_COMMAND;
import static com.adaptris.management.exec.ExecConfigBuilder.EXEC_STOP_COMMAND;
import static com.adaptris.management.exec.ExecConfigBuilder.EXEC_STOP_WAIT;
import static com.adaptris.management.exec.ExecConfigBuilder.EXEC_VERBOSE;
import static com.adaptris.management.exec.ExecConfigBuilder.EXEC_WORKING_DIR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.BooleanUtils;
import org.junit.Test;

import com.adaptris.util.GuidGenerator;

public class ExecConfigBuilderTest {

  private static final GuidGenerator guid = new GuidGenerator();

  private static final String[] CONFIG_PROPERTIES = {
      EXEC_START_COMMAND,EXEC_WORKING_DIR,EXEC_STOP_COMMAND,EXEC_STOP_WAIT,EXEC_MONITOR_MS,EXEC_VERBOSE
  };

  private static final ValueGenerator[] GENERATORS = {
      () -> {
        return guid.getUUID();
      },
      () -> {
        return guid.getUUID();
      },
      () -> {
        return guid.getUUID();
      },
      () -> {
        return String.valueOf(ThreadLocalRandom.current().nextLong());
      },
      () -> {
        return String.valueOf(ThreadLocalRandom.current().nextLong());
      },
      () -> {
        return BooleanUtils.toBooleanObject(ThreadLocalRandom.current().nextInt(2)).toString();
      }
  };

  private static final String[] IDENTIFIERS =
  {
      "hello", "goodbye"
  };

  @Test
  public void testBuild() throws Exception {
    ExecConfigBuilder builder = new ExecConfigBuilder(createProperties());
    List<ExecConfig> configs = builder.build();
    assertEquals(2, configs.size());
    for (ExecConfig c : configs) {
      assertGettersNotNull(c);
    }
  }

  private void assertGettersNotNull(ExecConfig c) throws Exception {
    String[] methods = filterGetterWithNoSetter(c.getClass(), getPrimitiveGetters(c.getClass()));
    System.out.println(c.getIdentifier());
    for (String m : methods) {
      System.out.println(m + " " + invokeGetter(c, m));
      assertNotNull(invokeGetter(c, m));
    }
  }
  private Properties createProperties() {
    Properties result = new Properties();
    for (String id : IDENTIFIERS) {
      for (int i = 0; i < CONFIG_PROPERTIES.length; i++) {
        System.out.println(String.format(CONFIG_PROPERTIES[i], id) + "=" + GENERATORS[i].generate());
        result.setProperty(String.format(CONFIG_PROPERTIES[i], id), GENERATORS[i].generate());
      }
    }
    return result;
  }

  @FunctionalInterface
  private static interface ValueGenerator {
    String generate();
  }
}
