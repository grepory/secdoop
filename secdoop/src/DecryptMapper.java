/************************************************************************************************************/
/* DecryptMapper.java by James Majors                                                                       */
/* This is the Mapper function for the Decryption application.  The input will be split up and equally      */
/* distributed across the available nodes in the cluster.  The decryption process will be done for each     */
/* input split and paired with a key value that will help maintain the sequence of the file.  The output    */
/* will be sent to an intermediate file that will contain the key/value pairs.  The Reducer will use these  */
/* intermediate results to sort the key values in order to restore the original sequence of the file.       */
/************************************************************************************************************/

//package org.apache.hadoop.examples;

import java.io.*;
import java.util.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class DecryptMapper extends MapReduceBase
  implements Mapper<Text, Text, Text, Text> {
  public void map(Text key, Text value,
      OutputCollector<Text, Text> output, Reporter reporter)
      throws IOException {
    
    //Transfer the values from the input splits to a byte array
    byte[] buffer = new byte[value.getLength()];
    buffer = value.getBytes();
    
    //Iterate through the byte array and decrypt the bytes
    for(int i=0; i < buffer.length; i++){
      buffer[i] = (byte)((buffer[i]) + (byte)1);
    }

    //Replace the value from the input splits with the resulting byte array
    value.set(buffer,0,value.getLength());

    //Output the results to an intermediate file (<Text,Text>)
    output.collect(key, value);
  }
}

