#!/usr/bin/python

import sys
import traceback

RED='\033[1m\033[31m'
GREEN='\033[1m\033[32m'
CLEAR='\033[0m'

class TaskSuite(object):
    """Defines a collection of Tasks and exposes an interface
    to run them.
    """

    def __init__(self):
        self.tasks = []
    
    def run(self):
        """Run the suite of Tasks.
        Output will be collected and printed to the screen
        as each Task is executed.
        """
        ok = 0
        for task in self.tasks:
            task.run()
            if task.success:
                print "%sOK%s: %s" % (GREEN, CLEAR, task.name)
                ok += 1
            else:
                print "\n%sFAILED%s: %s\n%s" % (RED, CLEAR, task.name, task.output)
        print "\n%s%s%s OK, %s%s%s FAILED" % (GREEN, ok, CLEAR, RED, len(self.tasks) - ok, CLEAR)

class Task(object):
    """Tasks are initialized with a name and a pointer to a function
    that actually does the work. All you need to do is create a 
    Task object for each unit of work in your TaskSuite.  Of course,
    functions can be re-used.

    func should return output if you need to do any kind of processing.
    Failed tests should throw exceptions.
    """
    
    def __init__(self, name, func, **kwargs):
        self.name = name
        self._func = func
        self.success = False
        self._args = kwargs

    def run(self):
        """Run the defined test.
        """
        try:
            self.output = self._func(**self._args)
            self.success = True
        except:
            self.output = "Caught Exception\n%s" % traceback.format_exc()
            sys.exc_clear()
        return self.output
