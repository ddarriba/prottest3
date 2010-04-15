#!/bin/sh
# Usage:
#   sh testConsensus.sh FILENAME THRESHOLD
#

java -cp prottest-hpc.jar es.uvigo.darwin.prottest.consensus.Consensus $1 $2
