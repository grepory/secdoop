#!/usr/bin/python

class Cluster(object):

    def __init__(self):
        self.nodes = []
        pass

class Node(object):

    def __init__(self, hostname):
        self.hostname = hostname
