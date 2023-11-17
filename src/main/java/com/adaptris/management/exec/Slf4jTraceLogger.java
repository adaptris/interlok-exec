package com.adaptris.management.exec;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.StreamPumper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.util.ManagedThreadFactory;

/**
 * {@code LogOutputStream} implementation that logs to slf4j at {@code TRACE} level.
 *
 *
 */
public class Slf4jTraceLogger extends LogOutputStream {

  private transient Logger log = LoggerFactory.getLogger(this.getClass());
  private static final ManagedThreadFactory MTF = new ManagedThreadFactory();

  public Slf4jTraceLogger(Logger log) {
    this.log = log;
  }

  @Override
  protected void processLine(String line, int logLevel) {
    log.trace(line);
  }

  public static <T extends DefaultExecutor> T attach(T executor, Logger log) {
    Slf4jTraceLogger logger = new Slf4jTraceLogger(log); // lgtm [java/output-resource-leak]
    PumpStreamHandler pump = new ManagedPumpStreamHandler(logger);
    executor.setStreamHandler(pump);
    return executor;
  }

  static class ManagedPumpStreamHandler extends PumpStreamHandler {

    public ManagedPumpStreamHandler(final OutputStream outAndErr) {
      super(outAndErr, outAndErr);
    }

    @Override
    protected Thread createPump(final InputStream is, final OutputStream os, final boolean closeWhenExhausted) {
      String name = Thread.currentThread().getName();
      final Thread result = MTF.newThread(new StreamPumper(is, os, closeWhenExhausted));
      result.setName(name);
      result.setDaemon(true);
      return result;
    }
  }

}
