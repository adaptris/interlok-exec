/*
 * Copyright 2018 Adaptris Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.adaptris.management.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.adaptris.core.util.PropertyHelper;

public class ExecConfigBuilder {

  public static final String EXEC_START_COMMAND = "exec.%s.start.command";
  public static final String EXEC_WORKING_DIR = "exec.%s.working.dir";
  public static final String EXEC_STOP_COMMAND = "exec.%s.stop.command";
  public static final String EXEC_STOP_WAIT = "exec.%s.stop.wait.ms";
  public static final String EXEC_MONITOR_MS = "exec.%s.process.monitor.ms";
  public static final String EXEC_VERBOSE = "exec.%s.process.debug";
  
  private static final String EXEC_IDENTIFIER_REGEX = "^exec\\.(.*)\\.start\\.command$";

  private transient Pattern pattern = Pattern.compile(EXEC_IDENTIFIER_REGEX);

  private transient Map<String, String> config;
  private transient Set<String> identifiers;

  private enum ConfigItem {

    Start {

      @Override
      void add(String identifier, Map<String, String> config, ExecConfig existing) {
        existing.setStartCommand(config.get(String.format(EXEC_START_COMMAND, identifier)));
      }
      
    },
    Stop {
      @Override
      void add(String identifier, Map<String, String> config, ExecConfig existing) {
        existing.setStopCommand(config.get(String.format(EXEC_STOP_COMMAND, identifier)));
      }
    },
    WorkDir {
      @Override
      void add(String identifier, Map<String, String> config, ExecConfig existing) {
        existing.setWorkingDirectory(config.get(String.format(EXEC_WORKING_DIR, identifier)));
      }
    },
    Monitor {
      @Override
      void add(String identifier, Map<String, String> config, ExecConfig existing) {
        existing.setMonitorMillis(config.get(String.format(EXEC_MONITOR_MS, identifier)));
      }
    },
    Debug {
      @Override
      void add(String identifier, Map<String, String> config, ExecConfig existing) {
        existing.setDebug(config.get(String.format(EXEC_VERBOSE, identifier)));
      }
    },
    MaxStopWait {
      @Override
      void add(String identifier, Map<String, String> config, ExecConfig existing) {
        existing.setMaxStopWaitMillis(config.get(String.format(EXEC_STOP_WAIT, identifier)));
      }
    };
    abstract void add(String identifier, Map<String, String> config, ExecConfig existing);

  }

  public ExecConfigBuilder(Properties config) {
    this(PropertyHelper.asMap(config));
  }

  public ExecConfigBuilder(Map<String, String> config) {
    this.config = config;
    buildIdentifiers();
  }

  private void buildIdentifiers() {
    identifiers = new TreeSet<String>();
    for (String s : config.keySet()) {
      Matcher m = pattern.matcher(s);
      if (m.matches()) {
        identifiers.add(m.group(1));
      }
    }
  }

  public List<ExecConfig> build() {
    List<ExecConfig> result = new ArrayList<>();
    for (String id : identifiers) {
      ExecConfig cfg = new ExecConfig(id);
      for (ConfigItem item : ConfigItem.values()) {
        item.add(id, config, cfg);
      }
      result.add(cfg);
    }
    return result;
  }

}
