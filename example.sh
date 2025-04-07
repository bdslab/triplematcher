#!/bin/bash

source config.conf

if [[ $# -ne 3 ]]; then
    echo "Usage: $0 primo.jar script.py secondo.jar"
    exit 1
fi

JAR1="$1"
PYTHON_SCRIPT="$2"
JAR2="$3"

if [[ ! -f "$JAR1" ]]; then
    echo "Error: $JAR1 not found!"
    exit 1
fi

if [[ ! -f "$PYTHON_SCRIPT" ]]; then
    echo "Error: $PYTHON_SCRIPT not found!"
    exit 1
fi

if [[ ! -f "$JAR2" ]]; then
    echo "Error: $JAR2 not found!"
    exit 1
fi

java -jar $JAR1 $MATCHER_INPUT $MATCHER_OUTPUT -n $MATCHER_PATTERN_SEQ -b $MATCHER_PATTERN_BOND -t $MATCHER_TOLERANCE_SEQ -ml $MATCHER_ML -bt $MATCHER_TOLERANCE_BOND -pt $MATCHER_TOLERANCE_NP -ct $MATCHER_TOLERANCE_CONS_BOND

python3 $PYTHON_SCRIPT $PY_ADD_KEY_MATCH $PY_ADD_KEY_XLSX $PY_ADD_KEY_OUT

java -jar $JAR2 $FILTER_INPUT $FILTER_OUTPUT -t $FILTER_TOLERANCE -p $FILTER_PDB