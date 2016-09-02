#!/usr/bin/env bash

dir="$( cd "$( dirname "$0" )" && pwd )"
cd $dir

# See https://github.com/scalatron/scalatron/blob/master/Scalatron/doc/markdown/Scalatron%20Server%20Setup.md
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar Scalatron.jar -server -Xmx1024M -frameX 1280 -frameY 768
