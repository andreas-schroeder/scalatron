#!/usr/bin/env bash

dir="$( cd "$( dirname "$0" )" && pwd )"
cd $dir

# See https://github.com/scalatron/scalatron/blob/master/Scalatron/doc/markdown/Scalatron%20Server%20Setup.md
java -jar Scalatron.jar -server -Xmx512M -frameX 1280 -frameY 768