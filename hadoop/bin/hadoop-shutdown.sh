#!/bin/sh

$HADOOP_DIR/bin/stop-mapred.sh
sleep 5
$HADOOP_DIR/bin/stop-dfs.sh
