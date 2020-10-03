Overview
===========================

Application manages messages and provide information whether message is a palindrom or not.

## Architecture
[![Screen-Shot-2020-10-02-at-9-42-02-PM.png](https://i.postimg.cc/SKnsLtnY/Screen-Shot-2020-10-02-at-9-42-02-PM.png)](https://postimg.cc/Mc8qq5bz)

## Prerequisite
Following prerequisites are required to build application locally:
1. Maven 3.6.2
2. Java 11 (openjdk 11 2018-09-25)
3. Docker 2.4.0.0 Channel Stable. It must be running, tests are using amazon/dynamodb-local image.

## Build:
command: mvn clean install -U

## Test:
command: mvn test
