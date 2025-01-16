#!/bin/bash
set -e -x

# DO NOT USE $WORKSPACE , will break builds with relative links

# Make build data file
OUTPUT_FILE=.build_data_bb
echo "user=`whoami`" >> $OUTPUT_FILE
echo "hostname=`hostname`" >> $OUTPUT_FILE
echo "date=`date`" >> $OUTPUT_FILE
echo "id=$CI_COMMIT_SHA" >> $OUTPUT_FILE

PRODUCT=$1
echo "product=$PRODUCT" >> $OUTPUT_FILE

VERSION=$2
echo "version=$VERSION" >> $OUTPUT_FILE
OUTPUT_FILE_REV=$OUTPUT_FILE.rev
BRANCH=`git rev-parse --abbrev-ref HEAD`
echo "Last repository revision on branch $BRANCH" >> $OUTPUT_FILE_REV
git log -1 -b $BRANCH >> $OUTPUT_FILE_REV
mv $OUTPUT_FILE $OUTPUT_FILE_REV src/main/resources

# Update version number in about.properties
cd dist/programData/settings
echo "product=$PRODUCT" > about.properties
echo "version=$VERSION" >> about.properties
echo "name=BrailleBlaster" >> about.properties
echo "date=`date`" >> about.properties
cat about.properties
cd ../../..

# scrape docs from website
cd dist/docs
rm *.htm* || true
wget -e robots=off -nd -r --convert-links -H -D aphassets.blob.core.windows.net,brailleblaster.org,dev.brailleblaster.org --level=inf -A html,jpg,jpeg,png,gif,PNG,JPG https://dev.brailleblaster.org/docs/manual/manualV2_1.html
