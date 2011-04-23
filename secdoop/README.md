SecDoop Readme
==============

### Environment

We use git at Github.com for version control and source distribution.
Begin by cloning the repository:

`git clone git://github.com/grepory/secdoop.git`

Make a note of the path to the secdoop directory that's created. For example,
I keep mine in `$HOME/dev/secdoop`. This directory will be referred to as $CHECKOUT.

In order for Hadoop to work, You must add the following lines to your .bashrc file:

	export HADOOP_DIR=$CHECKOUT
	export HADOOP_COMMON_HOME=$HADOOP_DIR
	PATH=$PATH:$HADOOP_DIR/bin

`HADOOP_COMMON_HOME` is used by the Hadoop scripts.
`HADOOP_DIR` is used by the Secdoop scripts.

### Building SecDoop

To build Secdoop, you can simply run 'ant dist-all' in the trunk/secdoop
folder. This will compile all of the necessary classes needed to test both
Secdoop and the standalone JCE tests.

**Note about AES and the JCE** 
In order to test anything against the Java Cryptographic Extensions (JCE), you
need to install the JAR files from the `jce_policy-6.zip` file in `trunk/`.  Unzip
the file and copy it into your JRE's `lib/security director`. For example, on
Ubuntu 10.\* you can do the following:

	unzip jce_policy-6.zip
	sudo cp jce/*.jar /usr/lib/jvm/java-6-sun/jre/lib/security

### Preparing Hadoop

First thing's first, actually configure hadoop. You'll find some example
configuration files in hadoop/conf. Copy those files without "example" in the
name, and you'll have the files you need for configuration. Change the values
to whatever is appropriate for your cluster.

The testing harness is configured through a tests configuration file, by
default named "tests.conf." In tests.conf you can specify the algorithms you
wish to test and the size of the input files. Currently supported sizes and
algorithms are listed in tests-example.conf.

Then, you need to generate your input sets and configuration files by
calling the generate.py script in the secdoop directory:

	./generator.py

If you would like, take a look at generator.py's usage by running:

	./generator.py -h

Next, format the Hadoop namenode by running the following:

	hdfs namenode -format

After running generator.py and formatting your namenode, you run the stage.sh
script. This will put all of your generated input files into HDFS.

This will create the necessary XML and text files for testing. After they run,
the inputs and configs directories will be populated.

Next, start up Hadoop:

hadoop-startup.sh

**Note:**
In our testing environment, we have a shared home directory via NFS. So,
each of the Hadoop installations is actually shared across the cluster. This
can be problematic, because the hadoop log directory is on NFS. The overhead
from NFS writes was actually enough to notice a roughly 10% performance
decrease. Also, the userlogs directory had to by symbolically linked to a
common local (non-nfs) directory. If your cluster doesn't share a Hadoop
installation, this shouldn't be an issue.

### Running SecDoop Tests

Then you're ready to run tests. From the secdoop directory:

	./test.sh

For information about test.sh command-line options, simply run: 

	./test.sh -h

After testing, shutdown the Hadoop cluster:

	hadoop-shutdown.sh

If at any point you need to refresh your Hadoop cluster (i.e. delete all of
your temporary mapreduce data, remove all of your HDFS local file stores on
cluster nodes, and re-format your namenode), you can use hadoop-clear.sh found
in hadoop/bin. This script should be in your PATH if you made the changes to
your .bashrc, mentioned above, correctly.
