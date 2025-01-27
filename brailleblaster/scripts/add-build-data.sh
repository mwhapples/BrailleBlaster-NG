#!/usr/bin/env bash
set -e -x

# DO NOT USE $WORKSPACE , will break builds with relative links
BB_DIR=$4

# Make build data file
OUTPUT_FILE=.build_data_bb
echo "user=`whoami`" >> $OUTPUT_FILE
echo "hostname=`hostname`" >> $OUTPUT_FILE
echo "date=`date`" >> $OUTPUT_FILE
echo "id=$3" >> $OUTPUT_FILE

PRODUCT=$1
echo "product=$PRODUCT" >> $OUTPUT_FILE

VERSION=$2
echo "version=$VERSION" >> $OUTPUT_FILE
OUTPUT_FILE_REV=$OUTPUT_FILE.rev
BRANCH=`git rev-parse --abbrev-ref HEAD`
echo "Last repository revision on branch $BRANCH" >> $OUTPUT_FILE_REV
git log -1 -b $BRANCH >> $OUTPUT_FILE_REV
mv $OUTPUT_FILE $OUTPUT_FILE_REV $BB_DIR/src/main/resources

# Update version number in about.properties
cd $BB_DIR/dist/programData/settings
echo "product=$PRODUCT" > about.properties
echo "version=$VERSION" >> about.properties
echo "name=BrailleBlaster" >> about.properties
echo "date=`date`" >> about.properties
cat about.properties

