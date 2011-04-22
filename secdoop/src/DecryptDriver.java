/************************************************************************************************************/
/* DecryptDriver.java by James Majors                                                                       */
/* This is the Driver for the decryption application.  This application supports configurations that can    */
/* be customized by future developers.  This application takes in an input file that consists of key/value  */
/* pairs.  The output key class of type Text, provided by Hadoop, and is used to maintain the sequence of   */
/* the file.  The input values will be of type Text as well.  The Mapper will decrypt the input values and  */
/* return the results to an intermediate file. The Reducer for this application sorts the intermediate      */
/* results using keys, to return the sequence of the original file.                                         */
/************************************************************************************************************/


//package org.apache.hadoop.examples;

import java.io.*;
import java.util.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;


public class DecryptDriver extends Configured implements Tool {
  @Override
  public int run(String[] args) throws Exception {
    if (args.length != 0) {
      System.err.printf("Usage: %s [generic options] -Dfileconf=<fileconf>\n",
          getClass().getSimpleName());
      ToolRunner.printGenericCommandUsage(System.err);
      return -1;
    }
    JobConf conf = new JobConf(getConf(), getClass());
    conf.setJobName("decrypt");

    String fileconf = conf.get("fileconf");
    conf.addResource(new Path(fileconf));

    //For multiple input files, use .addInputPaths and use comma seperation.
    FileInputFormat.addInputPath(conf, new Path(conf.get("encryptedFile")));
    FileOutputFormat.setOutputPath(conf, new Path(conf.get("decryptedFile")));
    
    //Set the output classes expected for the intermediate files
    conf.setOutputKeyClass(LongWritable.class);
    conf.setOutputValueClass(Text.class);

    //The input is expected to be a key value pair that is produced from the Encryption application
    conf.setInputFormat(SequenceFileInputFormat.class);
    conf.setOutputFormat(TextOutputFormat.class);
    conf.setNumReduceTasks(0);

    if (conf.get("algorithm").equals("AES"))
	{
	    conf.setMapperClass(AESDecryptMapper.class);
	    //I have not written the Reducer yet :/
	    //conf.setCombinerClass(DecryptReducer.class);
	    //conf.setReducerClass(DecryptReducer.class);
	}
    else if (conf.get("algorithm").equals("DES"))
	{
	    conf.setMapperClass(DESDecryptMapper.class);
	    //I have not written the Reducer yet :/
	    //conf.setCombinerClass(DecryptReducer.class);
	    //conf.setReducerClass(DecryptReducer.class);
	}
    else if (conf.get("algorithm").equals("DESede"))
	{
	    conf.setMapperClass(DESedeDecryptMapper.class);
	    //I have not written the Reducer yet :/
	    //conf.setCombinerClass(DecryptReducer.class);
	    //conf.setReducerClass(DecryptReducer.class);
	}
    
    JobClient.runJob(conf);
    return 0;
  }
  public static void main(String[] args) throws Exception {
    int exitCode = ToolRunner.run(new DecryptDriver(), args);
    System.exit(exitCode);
  }
}

