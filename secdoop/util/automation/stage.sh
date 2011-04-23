#!/bin/bash
# This should be run on the machine where the files are created

if [ ! -d inputs ]
then
    mkdir inputs
fi
hadoop fs -rmr inputs
hadoop fs -put inputs inputs
