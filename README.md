jni_based_unix_groups_mapping_test
==================================

This is a regression test for HADOOP-9439.  It invokes JniBasedUnixGroupsMapping from many different threads, over and over, in hopes of triggering any latent bugs.

You must install the Hadoop-2 jars in order to use this.

Colin Patrick McCabe
