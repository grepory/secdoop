#!/usr/bin/python

import unittest
from subprocess import Popen
from ..hadoop import MapReduceInstance, HADOOP_DIR
from os.path import exists

class MapRedInstanceTest(unittest.TestCase):

    def setUp(self):
        self.mapred = MapReduceInstance()
        jtpid = self.mapred._jtpid
    
    def tearDown(self):
        args=["%s/bin/stop-mapred.sh" % HADOOP_DIR]
        p = Popen(args)
        p.wait()

    def test_startup(self):
        self.mapred.start()
        self.assertTrue(self.mapred.running)

    def test_shutdown(self):
        self.mapred.stop()
        self.assertFalse(self.mapred.running)

    def test_delete_tmp_data(self):
        if self.mapred.running:
            self.mapred.stop()
        self.mapred.delete_tmp_data()
        # XXX: Fix this test once Config is done.
        self.assertFalse(exists('/tmp/hadoop-grep/mapred'))
