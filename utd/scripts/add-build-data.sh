#!/bin/bash

#
# Add build metadata during Jenkins build
#

set -e -x

# By default sets the project name based on the directory. In this case, workspace
echo "rootProject.name = 'utd'" > settings.gradle

# No built-in way to get version number
cat >>build.gradle <<'EOF'
task overlyComplicatedVersionFlag {
def output = new File('.gradle_project_version')
println "Writing version ${version} to file " + output.absolutePath
output.text = "${version}"
}
EOF

./gradlew overlyComplicatedVersionFlag
VERSION=`cat .gradle_project_version`
if [ -z "$VERSION" ]; then
	echo "Failed, version is empty"
	exit 404
fi;
VERSION=`basename $VERSION -SNAPSHOT`.${BUILD_NUMBER}-SNAPSHOT;

#Make build data file
OUTPUT_FILE=.build_data_utd
echo "user=`whoami`" >> $OUTPUT_FILE
echo "hostname=`hostname`" >> $OUTPUT_FILE
echo "date=`date`" >> $OUTPUT_FILE
echo "id=$BUILD_TAG" >> $OUTPUT_FILE
echo "version=$VERSION" >> $OUTPUT_FILE
OUTPUT_FILE_REV=$OUTPUT_FILE.rev
BRANCH=`git rev-parse --abbrev-ref HEAD`
echo "Last repository revision on branch $BRANCH" >> $OUTPUT_FILE_REV
git log -1 -b $BRANCH >> $OUTPUT_FILE_REV

mv $OUTPUT_FILE $OUTPUT_FILE_REV src/main/resources