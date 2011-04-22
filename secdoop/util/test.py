#!/usr/bin/python

from testomator.task import Task, TaskSuite

def pfunc():
    return "pfunc executed"

def exceptfunc():
    raise Exception("foo")

def get_results(**kwargs):
    return kwargs['test'].output

t = Task("test 1", pfunc)
r = Task("get results", get_results, test=t)
t2 = Task("exception test", exceptfunc)
r2 = r
suite = TaskSuite()
suite.tasks = [t, r, t2, r2]

suite.run()
