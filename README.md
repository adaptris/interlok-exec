# interlok-exec [![GitHub tag](https://img.shields.io/github/tag/adaptris/interlok-exec.svg)](https://github.com/adaptris/interlok-exec/tags) [![Build Status](https://travis-ci.org/adaptris/interlok-exec.svg?branch=develop)](https://travis-ci.org/adaptris/interlok-exec) [![codecov](https://codecov.io/gh/adaptris/interlok-exec/branch/develop/graph/badge.svg)](https://codecov.io/gh/adaptris/interlok-exec) ![license](https://img.shields.io/github/license/adaptris/interlok-exec.svg) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/7eafc6b297b6409690e9f9597cdca2a5)](https://www.codacy.com/app/adaptris/interlok-exec)

`musical-goggles` was the suggested project name

## Why ![Interlok Hammer](https://img.shields.io/badge/certified-interlok%20hammer-red.svg)

Sometimes, you just want to start some arbitrary programs when you start interlok...

## How to configure

It's a management component, so everything is configured in bootstrap.properties where your configuration consists of a number of  `exec.<identifier>.XXX=` properties. Easiest to explain with an example; in this example the identifier for grouping purposes is _tomcat_ and _activemq_ which logically groups the config for each executable.

```
managementComponents=jmx:exec:jetty

exec.activemq.working.dir=/home/vagrant/activemq
exec.activemq.start.command=./bin/activemq.sh start
exec.activemq.stop.command=./bin/activemq.sh stop
exec.activemq.process.monitor.ms=10000
exec.activemq.process.debug=true

exec.tomcat.working.dir=/home/vagrant/tomcat
exec.tomcat.start.command=./bin/catalina.sh start
exec.tomcat.process.monitor.ms=10000
exec.tomcat.process.debug=true

```

In this instance there are two executables configured so...

* Upon start we execute `catalina.sh start` and `activemq.sh start` respectively. The working directories for those processes are `/home/vagrant/tomcat` and `/home/vagrant/activemq` respectively.
  * Any output to standard error/output will be redirected at TRACE level to the standard interlok logfile 
* Every 10 seconds, we check the process to see if they are alive
  * Because process.debug is true, then you will get logging in any configured log file at trace level for the process monitoring
  * If the process is dead, than we attempt to restart the executable.
* Upon interlok shutdown, the script `activemq.sh stop` will be executed for the _activemq_ exec group only.
