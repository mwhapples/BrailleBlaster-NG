#!/bin/sh
if [ ! -z "$JAVA_HOME" ]; then
	cmd="$JAVA_HOME/bin/java"
else
	if which java; then
		cmd="java"
	else
		echo "java not found on \$PATH"
		exit 1
	fi
fi

$cmd -jar brailleblaster.jar
