#!/bin/sh

cat <<_HERE_
Are you sure you want to delete all of the contents of HDFS, etc? (y/n)
_HERE_

hadoop-shutdown.sh
rm -rf $HOME/tmp
rm -rf $HADOOP_DIR/logs/*
hdfs namenode -format
hadoop-startup.sh
