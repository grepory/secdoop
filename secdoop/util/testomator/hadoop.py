#!/usr/bin/python

from subprocess import Popen, PIPE
from config import ConfigFactory, Config
import cluster
import os
import os.path
import sys

class MapReduceInstance(object):

    def __init__(self, hadoop_dir, cluster=None):
        self.hadoop_dir = hadoop_dir
        # need mapred-site.xml
        self.config = ConfigFactory.get_config( \
                "%s/conf/mapred-site.xml" % hadoop_dir)
        self.cluster = cluster
        self.running = False
        self._jtpid = None
        self._ttpid = None

    def start(self):
        """Start the MapReduce cluster.
        
        """
        if not self.running:
            args = ["%s/bin/start-mapred.sh" % self.hadoop_dir]
            p = Popen(args)
            p.wait()
            args = ["jps",]
            p = Popen(args, stdout=PIPE, stderr=PIPE)
            (stdoutdata, stderrdata) = p.communicate()
            outlines = stdoutdata.rstrip('\n').split('\n')
            for line in outlines:
                (pid, job) = line.split()
                if job == "JobTracker":
                    self._jtpid = pid
                elif job == "TaskTracker":
                    self._ttpid = pid
            if self._jtpid and self._ttpid:
                self.running = True
            else:
                print "ERROR: MapReduce failed to start.\n \
                        %s JobTracker\n%s TaskTracker" \
                        % (self._jtpid, self._ttpid)
        else:
            print "ERROR: Attempting to start MapReduce while it's running."

    def stop(self):
        """Stop the MapReduce cluster
        
        """
        args = ["%s/bin/stop-mapred.sh" % self.hadoop_dir]
        p = Popen(args, stdout=PIPE)
        p.wait()
        p = Popen(['jps',], stdout=PIPE)
        stdoutdata = p.communicate()
        outlines = stdoutdata.rstrip('\n').split('\n')
        for line in outlines:
            pid, job = line.split()
            if job == 'TaskTracker' or job == 'JobTracker':
                print "ERROR: Failed to shutdown MapReduce"
                sys.exit(1)

    def delete_tmp_data(self):
        """Deletes all MapReduce temporary data on all machines in 
        the cluster.
        
        """
        # XXX: Fix this once Config is finished.
        # This is pretty dangerous. We need to look into a way to make
        # it a little more safe.
        args = ["rm", "-rf", self.config.get("mapreduce.cluster.local.dir")]
        #args = ["rm", "-rf", "/tmp/hadoop-grep/mapred"]
        p = Popen(args, stdout=PIPE)

class HDFSInstance(object):

    def __init__(self, hadoop_dir, cluster=None):
        self.hadoop_dir = hadoop_dir
        self.cluster = cluster
        pass

    def in_safemode():
        """Returns True if the HDFS NameNode is currently in SafeMode.
        
        """
        pass

    def start(self):
        """Start the HDFS cluster.
        
        """
        pass

    def stop(self):
        """Stop the HDFS cluster.
        
        """
        pass
    
    def delete_tmp_data(self):
        """Deletes all of the temporary HDFS data on all of the nodes
        of the HDFS cluster.
        
        """
        # dfs.namenode.name.dir
        pass
    
    def format(self):
        """Formats the HDFS cluster's NameNode.
        
        """
        pass


    def put(self, localsrc, dst):
        """Put a file into HDFS. 
        hadoop fs -put <localsrc> <dst>
        
        """
        pass

    def get(self, src, localdst):
        """Get a file from HDFS.
        hadoop fs -get <src> [<localdst>]
        
        """
        pass

    def rm(self, path):
        """Remove a file from HDFS.
        hadoop fs -rm <path>
        
        """
        pass

    def rmr(self, path):
        """Recursively remove a directory in HDFS.
        hadoop fs -rmr <path>
        
        """
        pass


class HadoopInstance(object):

    def __init__(self, cluster=None):
        self.cluster = cluster
        self.mapred = MapReduceInstance(cluster)
        self.hdfs = HDFSInstance(cluster)

    def __init__(self, mapred, hdfs, cluster=None):
        self.cluster = cluster
        self.mapred = mapred
        self.hdfs = hdfs

    def clean(self):
        """Shutdown the cluster, delete all of the temporary data, format
        the namenode, start the cluster back up again.
        
        """
        self.dfs.stop()
        self.mapred.stop()
        self.mapred.delete_tmp_data()
        self.hdfs.delete_tmp_data()
        self.delete_tmp_data()

    def delete_tmp_data(self):
        """Delete all temporary Hadoop data.
        
        """
        # hadoop.tmp.dir
