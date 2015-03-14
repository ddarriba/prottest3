# Frequently Asked Questions #

## I got an error when ProtTest 3 attempts to run PhyML under Linux64 ##

In some cases, ProtTest 3 could try to execute phyml-prottest-linux binary instead of phyml-prottest-linux64. In this case, just rename this binary file:
```
#!bash
$ cd $PROTTEST_HOME/bin
$ mv prottest-phyml-linux64 prottest-phyml-linux
```


Furthermore, if you have installed phyml in your HFS (from your APT repositories), you can link the binary file from the prottest "bin/" directory:
```
#!bash
$ cd $PROTTEST_HOME/bin
$ ln -s `which phyml` phyml
```

## I got the following error: "//Failed to load Main-Class manifest attribute from prottest-xyz.jar//" ##

Some old binary distributions of prottest included a wrong manifest file in its jar file. Please download the newest version and substitute it entirely, or just the main jar file and the running scripts if you prefer.

## I'd like to install prottest system-wide in a Unix Based OS (just running on multiple threads) ##

While ProtTest 3 does not implement this feature, we can do a workaround instead, allowing ProtTest 3 to run under an HFS. You have to follow this steps:

  * .**Move your prottest directory to /usr/local/lib/prottest3 and make sure you have read/write permissions.**

  * .**Put the following bash scripts under /usr/local/bin/ (this directory should be in the system PATH)**

## /usr/local/bin/prottest3 ##
```
#!bash
PROTTEST_HOME=/usr/local/lib/prottest3
PREVIOUS_PATH=$PWD
cd $PROTTEST_HOME

args=`getopt i:t: $*`
for i
do
case "$i" in
	-i) shift;INPUT_FILE=`echo $PREVIOUS_PATH/$1`;shift;
           MY_ARGS="$MY_ARGS -i $INPUT_FILE";;
	-t) shift;TREE_FILE=`echo $PREVIOUS_PATH/$1`;shift;
	    MY_ARGS="$MY_ARGS -t $TREE_FILE";;
esac
done

java -jar $PROTTEST_HOME/prottest-3.0.jar $MY_ARGS $*
```

## /usr/local/bin/xprottest3 ##
```
#!bash
export PROTTEST_HOME=/usr/local/lib/prottest3
cd $PROTTEST_HOME
./runXProtTestHPC.sh
```

  * .**Make them executables (chmod +x prottest3 xprottest3)**

## I got this error when running ProtTest 3 ##
```
#!bash
      ./phyml-prottest-linux: /lib/libc.so.6: version `GLIBC_2.7` not found 
```

You can try to update GLIB to the required version (2.7). However, this is not always possible, for example when running ProtTest on a cluster. If you have an old version of PhyML you can try to use it just linking the binary file to $PROTTEST\_HOME/bin/phyml

If this does not work, it is possible that your "old" version of PhyML does not support the --no-memory-check argument, included in lattest PhyML versions. In this case, edit the prottest.properties file and change the no-memory-check" property to "no" (for ProtTest > 3.1).