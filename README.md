# Cagey Config

[![Build Status](https://travis-ci.org/xp-cagey/cagey-config.svg?branch=master)](https://travis-ci.org/xp-cagey/cagey-config) [![codecov](https://codecov.io/gh/xp-cagey/cagey-config/branch/master/graph/badge.svg)](https://codecov.io/gh/xp-cagey/cagey-config) [![Maven Central](https://img.shields.io/maven-central/v/com.xpcagey/cagey-config.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.xpcagey%22%20AND%20a%3A%22cagey-config%22)

This is an abstraction layer for configuration that allows programs to subscribe to value changes and be notified when they occur, permitting live tuning of application logic and automatic stitching of configuration values from multiple sources.  It does for configuration what SLF4J has done for logging.

Each application must declare a set of preferences for configuration sources, with each source able to contribute to the parameterization of the next. Configuration sources are each injected by a runtime module that should be placed into the application classpath before running the system.  Failure to load a module will not cause the process to fail, but an exception will be thrown to report failures; it is up to the application to decide whether this
failure should be considered fatal.
 
An implementation of the system for static declaration of default values is provided in the core package.      