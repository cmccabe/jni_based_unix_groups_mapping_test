jni_based_unix_groups_mapping_test
==================================

This is a regression test for HADOOP-9439.  It invokes JniBasedUnixGroupsMapping from many different threads, over and over, in hopes of triggering any latent bugs.

You must install the Hadoop-2 jars in order to use this.

USAGE
=====

Build
-----

```bash
git clone https://github.com/cmccabe/jni_based_unix_groups_mapping_test.git
cd jni_based_unix_groups_mapping_test/
mvn package
ls -l target/jni_based_unix_groups_mapping_test-1.0.jar
```

Usage
-----

```bash
~> hadoop jar target/jni_based_unix_groups_mapping_test-1.0.jar
You must set the string property test.username

JniBasedUnixGroupsMappingTest: tests concurrent use of JniBasedUnixGroupsMapping in Hadoop.

Java system properties to set:
test.nthreads [nthreads]           Number of threads to use. (default 100)
test.username [user name]          User name to use.
test.seconds [num seconds]         Number of seconds to run the test (default 10)
```

Example
-------

```bash
~> export HADOOP_CLIENT_OPTS="-Dtest.username=cmccabe -Dtest.seconds=60 -Dtest.nthreads=200"
~> hadoop jar target/jni_based_unix_groups_mapping_test-1.0.jar
done.
~>
```

Colin Patrick McCabe
