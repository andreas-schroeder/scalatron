#!/usr/bin/env bash

dir="$( cd "$( dirname "$0" )" && pwd )"

cd $dir && cd ..
sbt clean dist