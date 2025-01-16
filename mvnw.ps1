<#
.SYNOPSIS
Apache Maven Wrapper startup PowerShell script

.DESCRIPTION
This script serves as a PowerShell equivalent to the mvnw.cmd file provided for Apache Maven.

.NOTES
Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.

.LINK
http://www.apache.org/licenses/LICENSE-2.0
#>

# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# Set title of command window
$Host.UI.RawUI.WindowTitle = $MyInvocation.MyCommand.Name

# Set error code variable
$Error_Code = 0

# Check for JAVA_HOME environment variable
if (-not $env:JAVA_HOME) {
    Write-Error "Error: JAVA_HOME not found in your environment."
    Write-Error "Please set the JAVA_HOME variable in your environment to match the location of your Java installation."
    exit 1
}

# Validate JAVA_HOME directory
if (-not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    Write-Error "Error: JAVA_HOME is set to an invalid directory."
    Write-Error "JAVA_HOME = '$env:JAVA_HOME'"
    Write-Error "Please set the JAVA_HOME variable in your environment to match the location of your Java installation."
    exit 1
}

# Find the project base directory
$MAVEN_PROJECTBASEDIR = $PSScriptRoot
if (-not $MAVEN_PROJECTBASEDIR) {
    $EXEC_DIR = (Get-Location).Path
    $WDIR = $EXEC_DIR
    while (-not (Test-Path "$WDIR\.mvn")) {
        Set-Location ..
        if ($WDIR -eq (Get-Location).Path) {
            break
        }
        $WDIR = (Get-Location).Path
    }
    $MAVEN_PROJECTBASEDIR = $WDIR
}

# Read additional configuration from jvm.config if exists
if (Test-Path "$MAVEN_PROJECTBASEDIR\.mvn\jvm.config") {
    $JVM_CONFIG_MAVEN_PROPS = Get-Content -Raw "$MAVEN_PROJECTBASEDIR\.mvn\jvm.config"
}

# Detect maven-wrapper.jar location
$WRAPPER_JAR = "$MAVEN_PROJECTBASEDIR\.mvn\wrapper\maven-wrapper.jar"

# Download maven-wrapper.jar if not found
if (-not (Test-Path $WRAPPER_JAR)) {
    $WRAPPER_URL = "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.1.1/maven-wrapper-3.1.1.jar"
    Write-Host "Downloading $WRAPPER_URL"
    $webClient = New-Object System.Net.WebClient
    $webClient.DownloadFile($WRAPPER_URL, $WRAPPER_JAR)
}

# Define Java executable and Maven wrapper launcher
$MAVEN_JAVA_EXE = "$env:JAVA_HOME\bin\java.exe"
$WRAPPER_LAUNCHER = "org.apache.maven.wrapper.MavenWrapperMain"

# Execute Maven
& $MAVEN_JAVA_EXE $JVM_CONFIG_MAVEN_PROPS $env:MAVEN_OPTS $env:MAVEN_DEBUG_OPTS -classpath $WRAPPER_JAR "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" $WRAPPER_LAUNCHER $env:MAVEN_CONFIG $args

# Set error code
$Error_Code = $LASTEXITCODE

# Pause the script if MAVEN_BATCH_PAUSE is set to 'on'
if ($env:MAVEN_BATCH_PAUSE -eq "on") {
    Read-Host "Press Enter to continue..."
}

# Exit script with appropriate error code
exit $Error_Code
