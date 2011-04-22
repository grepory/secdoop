#!/bin/sh


$HADOOP_DIR/bin/start-dfs.sh
sleep 5
$HADOOP_DIR/bin/start-mapred.sh
