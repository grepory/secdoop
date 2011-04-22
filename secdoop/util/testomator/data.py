#!/usr/bin/python

import os.path
import os

class DataGenerator(object):

    def __init__(self):
        pass

    def generate(self, path, size):
        self._file = open(path)
    
class SimpleTextDataGenerator(DataGenerator):

    def __init__(self):
        pass

    def generate(self, path, size):
        super(self, SimpleTextDataGenerator).__init__(path, size)


class DataItem(object):

    def __init__(self):
        self._path = ""

    def __init__(self, path):
        self._path = path

    def get_path(self):
        return self._path

    def set_path(self, path):
        if (self._file and self._path != path):
            self._file.close()
        elif (os.path.exists(path)):
            self._path = path

    def get_file(self):
        return self._file

class GeneratedDataItem(DataItem):
    
    def __init__(self, path, size):
        super(self, GeneratedDataItem).__init__(path)
        self.size = size

    def generate(self, delete=false):
        if not delete:
            

class DataBundle(object):

    def __init__(self, path):
        self.items = []
        if (os.path.isdir(path)):
            self._path = path
            for r, d, f in os.walk(path):
                self.items += ['/'.join([r, v]) for v in f]

    def __init__(self, path, 
