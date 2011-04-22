/************************************************************************************************************/
/* EncryptMapper.java by James Majors                                                                       */
/* This is the Mapper function for the Encryption application.  The input will be split up and equally      */
/* distributed across the available nodes in the cluster.  The encryption process will be done for each     */
/* input split and paired with a key value that will help maintain the sequence of the file.  The output    */
/* will be sent to an intermediate file that will contain the key/value pairs.                              */
/************************************************************************************************************/
//package org.apache.hadoop.examples;

import java.io.*;
import java.util.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class EncryptMapper extends MapReduceBase
  implements Mapper<LongWritable, Text, LongWritable, Text> {
  public void map(LongWritable key, Text value,
      OutputCollector<LongWritable, Text> output, Reporter reporter)
      throws IOException {

    //Transfer the values from the input splits to a byte array
    byte[] buffer = new byte[value.getLength()];
    buffer = value.getBytes();
    
    //Iterate through the byte array and encrypt the bytes
    for(int i=0; i < buffer.length; i++){
      buffer[i] = (byte)((buffer[i]) - (byte)1);
    }

    //Replace the value from the input splits with the resulting byte array
    value.set(buffer,0,value.getLength());

    //Output the results to an intermediate file (<LongWritable,Text>)
    output.collect(key, value);
  }
}

