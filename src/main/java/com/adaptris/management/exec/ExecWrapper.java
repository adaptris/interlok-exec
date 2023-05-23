package com.adaptris.management.exec;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.ProcessDestroyer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.util.Args;

public class ExecWrapper {
  private transient Logger log = LoggerFactory.getLogger(this.getClass().getName());

  private transient ExecConfig config;
  private transient ScheduledExecutorService executor;
  private transient Set<ScheduledFuture<?>> monitors = Collections.newSetFromMap(new WeakHashMap<ScheduledFuture<?>, Boolean>());
  private transient DestroyingProcessMonitor monitor = new DestroyingProcessMonitor();
  private transient AtomicBoolean started = new AtomicBoolean(false);

  public ExecWrapper(ExecConfig config, ScheduledExecutorService executor) {
    this.config = config;
    this.executor = executor;
  }

  public ExecWrapper start() throws Exception {
    started.set(true);
    if (config.hasStartCommand()) {
      executeStart();
    }
    return this;
  }

  public ExecWrapper stop() throws Exception {
    started.set(false);
    if (config.hasStopCommand()) {
      executeStop();
    }
    return this;
  }

  public ExecWrapper destroy() {
    started.set(false);
    monitor.destroy();
    return this;
  }

  private void executeStart() throws Exception {
    DaemonExecutor executor = configureExecutor(new DaemonExecutor(), config);
    executeInternal(config.getStartCommand(), executor);
  }

  private void executeStop() throws Exception {
    DefaultExecutor executor = configureExecutor(new DefaultExecutor(), config);
    executor.setWatchdog(new ExecuteWatchdog(config.maxStopWaitMillis()));
    executeInternal(config.getStopCommand(), executor);
  }

  private ExecuteResultHandler executeInternal(String cmd, DefaultExecutor exec) throws ExecuteException, IOException {
    log.debug("[{}] Attempting to execute [{}]", config.getIdentifier(), cmd);
    DefaultExecuteResultHandler result = new DefaultExecuteResultHandler();
    CommandLine cmdLine = CommandLine.parse(cmd);
    exec.execute(cmdLine, result);
    return result;
  }

  private <T extends DefaultExecutor> T configureExecutor(T executor, ExecConfig cfg) {
    executor.setProcessDestroyer(monitor);
    executor.setWorkingDirectory(new File(cfg.getWorkingDirectory()));
    return Slf4jTraceLogger.attach(executor, log);
  }

  private void scheduleNextRun(Runnable r) {
    if (started.get()) {
      monitors.add(executor.schedule(r, config.monitorMillis(), TimeUnit.MILLISECONDS));
    } else {
      debugLogging("{} not started, ignoring scheduling attempt", config.getIdentifier());
    }
  }

  private void debugLogging(String msg, Object... args) {
    if (config.debug()) {
      log.trace(msg, args);
    }
  }

  private class ProcessMonitor implements Runnable {
    private Process process;
    private ProcessRestarter restarter;

    public ProcessMonitor(Process p) {
      process = p;
      restarter = new ProcessRestarter(this);
    }

    @Override
    public void run() {
      if (started.get()) {
        debugLogging("[{}] Checking Process", config.getIdentifier());
        if (process.isAlive()) {
          debugLogging("[{}] Process is alive", config.getIdentifier());
          scheduleNextRun(this);
        } else {
          debugLogging("[{}] Process DIED", config.getIdentifier());
          log.warn("Process killed; attempting restart");
          executor.execute(restarter);
        }
      }
    }
  }

  private class ProcessRestarter implements Runnable {
    private ProcessMonitor parent;

    public ProcessRestarter(ProcessMonitor parent) {
      this.parent = parent;
    }

    @Override
    public void run() {
      try {
        if (started.get()) {
          debugLogging("[{}] attempting restart", config.getIdentifier());
          executeStart();
        }
      } catch (Exception e) {
        scheduleNextRun(parent);
      }
    }
  }

  // Crap name, but we implement process destroyer so that we get access to the process from commons-exec
  // Then we start a little monitor job (which then might restart).
  // And finally we destroy all the processes when requested.
  private class DestroyingProcessMonitor implements ProcessDestroyer {

    private Set<Process> processes = Collections.synchronizedSet(new HashSet<Process>());

    @Override
    public boolean add(Process process) {
      // Schedule a monitor
      boolean added = processes.add(Args.notNull(process, "process"));
      ProcessMonitor monitor = new ProcessMonitor(process);
      debugLogging("[{}] adding a new monitor : {}", config.getIdentifier(), monitor);
      scheduleNextRun(monitor);
      return added;
    }

    @Override
    public boolean remove(Process process) {
      boolean removed = processes.remove(Args.notNull(process, "process"));
      return removed;
    }

    @Override
    public int size() {
      return processes.size();
    }

    public void destroy() {
      processes.forEach(e -> {
        if (e.isAlive()) {
          e.destroyForcibly();
        }
      });
      processes.clear();
    }
  }

}
