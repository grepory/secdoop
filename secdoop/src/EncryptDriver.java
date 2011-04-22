/************************************************************************************************************/
/* EncryptDriver.java by James Majors                                                                       */
/* This is the Driver for the encryption application.  This application supports configurations that can    */
/* be customized by future developers.  This application takes in an input file and outputs an intermediate */
/* file to the distributed file system.  The key class is set to LongWritable, provided by Hadoop, and is   */
/* used to maintain the sequence of the file.  The input values will be of type Text which is also provided */
/* by Hadoop.  There is no Reducer for this application because we do not need to combine or reduce any     */
/* data.                                                                                                    */
/************************************************************************************************************/

//package org.apache.hadoop.examples;

import java.io.*;
import java.util.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;


public class EncryptDriver extends Configured implements Tool {
  @Override
  public int run(String[] args) throws Exception {
    if (args.length != 0) {
      System.err.printf("Usage: %s [generic options] -Dfileconf=<fileconf>\n",
          getClass().getSimpleName());
      ToolRunner.printGenericCommandUsage(System.err);
      return -1;
    }
    JobConf conf = new JobConf(getConf(), getClass());
    conf.setJobName("encrypt");
    
    String fileconf = conf.get("fileconf");
    conf.addResource(new Path(fileconf));

    //For multiple input files, use .addInputPaths and use comma seperation.
    FileInputFormat.addInputPath(conf, new Path(conf.get("inputFile")));
    FileOutputFormat.setOutputPath(conf, new Path(conf.get("encryptedFile")));
    
    //Set output types that are expected in the intermediate files.
    conf.setOutputKeyClass(LongWritable.class);
    conf.setOutputValueClass(BytesWritable.class);
    conf.setNumReduceTasks(0);

    conf.setOutputFormat(SequenceFileOutputFormat.class);

    if (conf.get("algorithm").equals("AES"))
	{
	    conf.setMapperClass(AESEncryptMapper.class);
	    //I have not written the Reducer yet :/
	    //conf.setCombinerClass(DecryptReducer.class);
	    //conf.setReducerClass(DecryptReducer.class);
	}
    else if (conf.get("algorithm").equals("DES"))
	{
	    conf.setMapperClass(DESEncryptMapper.class);
	    //I have not written the Reducer yet :/
	    //conf.setCombinerClass(DecryptReducer.class);
	    //conf.setReducerClass(DecryptReducer.class);
	}
    else if (conf.get("algorithm").equals("DESede"))
	{
	    conf.setMapperClass(DESedeEncryptMapper.class);
	    //I have not written the Reducer yet :/
	    //conf.setCombinerClass(DecryptReducer.class);
	    //conf.setReducerClass(DecryptReducer.class);
	}
    JobClient.runJob(conf);
    return 0;
  }
  public static void main(String[] args) throws Exception {
    int exitCode = ToolRunner.run(new EncryptDriver(), args);
    System.exit(exitCode);
  }
}

