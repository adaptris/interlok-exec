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

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.adaptris.util.TimeInterval;

public class ExecConfig {

  private static final long DEFAULT_INTERVAL = new TimeInterval(10L, TimeUnit.SECONDS).toMilliseconds();

  private String startCommand;
  private String stopCommand;
  private String workingDirectory;
  private String identifier;
  private Long maxStopWaitMillis;
  private Long monitorMillis;
  private Boolean debug;

  public ExecConfig(String identifier) {
    this.identifier = identifier;
  }

  public void setStartCommand(String startCommand) {
    this.startCommand = startCommand;
  }

  public String getStartCommand() {
    return startCommand;
  }

  public boolean hasStartCommand() {
    return StringUtils.isNotBlank(getStartCommand());
  }

  public void setStopCommand(String stopCommand) {
    this.stopCommand = stopCommand;
  }

  public String getStopCommand() {
    return stopCommand;
  }

  public boolean hasStopCommand() {
    return StringUtils.isNotBlank(getStopCommand());
  }

  public String getWorkingDirectory() {
    return StringUtils.defaultIfBlank(workingDirectory, System.getProperty("user.dir"));
  }

  public void setWorkingDirectory(String workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

  public String getIdentifier() {
    return identifier;
  }

  public long maxStopWaitMillis() {
    return maxStopWaitMillis != null ? maxStopWaitMillis.longValue() : DEFAULT_INTERVAL;
  }

  public void setMaxStopWaitMillis(Long maxStopWait) {
    this.maxStopWaitMillis = maxStopWait;
  }

  public void setMaxStopWaitMillis(String maxStopWait) {
    setMaxStopWaitMillis(NumberUtils.toLong(maxStopWait, DEFAULT_INTERVAL));
  }

  public Long getMaxStopWaitMillis() {
    return maxStopWaitMillis;
  }

  public Long getMonitorMillis() {
    return monitorMillis;
  }

  public void setMonitorMillis(Long monitorMillis) {
    this.monitorMillis = monitorMillis;
  }

  public void setMonitorMillis(String monitorMillis) {
    setMonitorMillis(NumberUtils.toLong(monitorMillis, DEFAULT_INTERVAL));
  }

  public long monitorMillis() {
    return monitorMillis != null ? monitorMillis.longValue() : DEFAULT_INTERVAL;
  }

  public Boolean getDebug() {
    return debug;
  }

  public void setDebug(Boolean debug) {
    this.debug = debug;
  }

  public void setDebug(String debug) {
    setDebug(BooleanUtils.toBooleanObject(debug));
  }

  public boolean debug() {
    return BooleanUtils.toBooleanDefaultIfNull(getDebug(), false);
  }

}
