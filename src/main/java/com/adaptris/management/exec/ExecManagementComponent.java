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

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.adaptris.core.management.MgmtComponentImpl;
import com.adaptris.core.util.ManagedThreadFactory;
import com.adaptris.util.TimeInterval;

public class ExecManagementComponent extends MgmtComponentImpl {
  private static final TimeInterval SHUTDOWN_TIMEOUT_MS = new TimeInterval(60L, TimeUnit.SECONDS);

  private transient List<ExecConfig> config;
  private transient Set<ExecWrapper> wrappers = new HashSet<>();
  private transient ScheduledExecutorService executor;

  public ExecManagementComponent() {
  }

  @Override
  public void init(Properties p) throws Exception {
    executor = Executors.newScheduledThreadPool(1, new ManagedThreadFactory(getClass().getSimpleName()));
    config = new ExecConfigBuilder(p).build();
  }

  @Override
  public void start() throws Exception {
    for (ExecConfig cfg : config) {
      wrappers.add(new ExecWrapper(cfg, executor).start());
    }
  }

  @Override
  public void stop() throws Exception {
    for (ExecWrapper w : wrappers) {
      w.stop();
    }
  }

  @Override
  public void destroy() throws Exception {
    for (ExecWrapper w : wrappers) {
      w.destroy();
    }
    ManagedThreadFactory.shutdownQuietly(executor, SHUTDOWN_TIMEOUT_MS);
  }

}
