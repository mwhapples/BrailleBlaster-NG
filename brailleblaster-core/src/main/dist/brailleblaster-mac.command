#!/bin/bash
# Need .command extension as .sh opens this in a text editor
# Mac for some reason starts all .command scripts in the users home directory, so need to go back to where this script is
cd "`dirname $0`"
java -XstartOnFirstThread -splash:programData/images/bb_horizontal_logo.png -jar brailleblaster.jar
