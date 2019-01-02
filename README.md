[![Build Status](https://travis-ci.org/open-rsx/rsb-java?branch=master)](https://travis-ci.org/open-rsx/rsb-java) [![Coverage Report](https://codecov.io/gh/open-rsx/rsb-java/branch/master/graph/badge.svg)](https://codecov.io/gh/open-rsx/rsb-java)

# Introduction

This repository contains the Java implementation of the [Robotics Service Bus](https://github.com/open-rsx) middleware.

**The full documentation for RSB can be found at <https://open-rsx.github.io/rsb-manual/>.**

# Dependencies

* [maven] as a build tool
* A Java compiler

Other dependencies are downloaded dynamically using the [maven] repository infrastructure.

# Building and Installing

## Installation

```shell
mvn install
```

## Running Unit Tests

```shell
mvn verify
```

# Contributing

If you want to contribute to this project, please

* Submit your intended changes as coherent pull requests
* Rebase onto the master branch and squash any fixups and corrections
* Make sure the unit tests pass (See [Running Unit Tests](#running-unit-tests))

# Acknowledgments

The development of this software has been supported as follows:

* This research was funded by the EC 7th Framework Programme (FP7/2007-2013), in the TA2 (grant agreement ICT-2007-214 793) and HUMAVIPS (grant aggrement ICT-2009-247525) projects.
* The development of this software was supported by CoR-Lab, Research Institute for Cognition and Robotics Bielefeld University.
* This work was supported by the Cluster of Excellence Cognitive Interaction Technology ‘CITEC’ (EXC 277) at Bielefeld University, which is funded by the German Research Foundation (DFG).

[maven]: https://maven.apache.org/
