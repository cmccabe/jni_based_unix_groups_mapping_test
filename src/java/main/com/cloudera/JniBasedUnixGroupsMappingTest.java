/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudera;

import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Thread;
import java.lang.System;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Hex;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.server.datanode.SimulatedFSDataset;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.JniBasedUnixGroupsMapping;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class JniBasedUnixGroupsMappingTest { //extends Configured {
  private static void usage(int retval) {
    System.err.println(
        "JniBasedUnixGroupsMappingTest: tests concurrent use of JniBasedUnixGroupsMapping in Hadoop.\n" +
        "\n" +
        "Java system properties to set:\n" +
        "test.nthreads [nthreads]           Number of threads to use. (default 100)\n" +
        "test.username [user name]          User name to use.\n" +
        "test.seconds [num seconds]         Number of seconds to run the test (default 10)\n" +
    );
    System.exit(retval);
  }

  static int getIntOrDie(String key) {
    String val = System.getProperty(key);
    if (val == null) {
      System.err.println("You must set the integer property " + key + "\n\n");
      usage(1);
    }
    return Integer.parseInt(val);
  }

  static int getIntWithDefault(String key, int defaultVal) {
    String val = System.getProperty(key);
    if (val == null) {
      return defaultVal;
    }
    return Integer.parseInt(val);
  }

  static String getStringOrDie(String key) {
    String val = System.getProperty(key);
    if (val == null) {
      System.err.println("You must set the string property " + key + "\n\n");
      usage(1);
    }
    return val;
  }

  static String getStringWithDefault(String key, String defaultVal) {
    String val = System.getProperty(key);
    if (val == null) {
      return defaultVal;
    }
    return val;
  }

  static boolean getBoolean(String key, boolean defaultVal) {
    String value = System.getProperty(key);
    if (value == null) {
      return defaultVal;
    }
    if ("true".equals(value)) {
      return true;
    }
    if ("false".equals(value)) {
      return false;
    }
    throw new RuntimeException("can't understand " + value +
                               "; expected true or false.");
  }

  static private class Options {
    public final int nthreads;
    public final String username;
    public final int seconds;

    public Options() {
      this.nthreads = getIntWithDefault("test.nthreads", 100);
      this.username = getStringOrDie("test.username");
      this.seconds = getIntWithDefault("test.seconds", 10);
    }
  };

  private static Options options;

  static private class TestThread extends Thread {
    @Override
    public void run() {
      long start = System.currentTimeMillis();
      JniBasedUnixGroupsMapping mapping = new JniBasedUnixGroupsMapping();
      while (true) {
        long now = System.currentTimeMillis();
        if (now - start > (options.seconds * 1000)) {
          break;
        }
        try {
          mapping.getGroups(options.username);
        }
        catch (IOException e) {
          throw new RuntimeException("getGroups failed", e);
        }
      }
    }
  }

  public static void main(String[] args) throws Exception {
    options = new Options();
    TestThread threads[] = new TestThread[options.nthreads];
    for (int i = 0; i < options.nthreads; i++) {
      threads[i] = new TestThread();
    }
    for (int i = 0; i < options.nthreads; i++) {
      threads[i].start();
    }
    for (int i = 0; i < options.nthreads; i++) {
      threads[i].join();
    }
    System.out.println("done.");
  }
}
