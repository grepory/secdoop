Automation
----------

### Files
*csv.py*: Format test results into a CSV

*generator.py*: Generate input sets for SecDoop testing

*stage.sh*: Generate and push input sets to HDFS

*tests-example.conf*: Example test configuration for test.sh and
generatory.py

*test.sh*: Test automation script


Testomator
----------

### Object Hierarchy
* Experiment
  * Config
    * HadoopConfig
    * TestConfig
* TestRun
  * Test
* HadoopInstance
  * HDFSInstance
* MapReduceTask
* DataBundle
  * DataItem

