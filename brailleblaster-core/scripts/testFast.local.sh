#!/bin/bash
# run UI tests in multiple JVM's, creating a new display for each one
# will use 75% of cores on machine by default
./mvnw clean test \
-Pci_build \
-DmoreExcludedTests=slowTests,benchmarks \
"$@"
