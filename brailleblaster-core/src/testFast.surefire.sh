#!/bin/bash
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
$DIR/xvfb-run-fixed "$JAVA_HOME/bin/java" "$@"
