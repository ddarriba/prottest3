# Download and Installation #

`ProtTest` works in Mac OSX, Windows, and Linux, and requires a version of the Java Runtime Environment equal or posterior to 1.6 (read section “Installing java” if you don’t have it). ProTest3 binaries are available from [Google Drive ProtTest 3 Folder](https://drive.google.com/folderview?id=0ByrkKOPtF_n_dVBBbDVBOEM5b1U). Download the package and decompress it in any directory. A README file and some examples are included.

To decompress the distribution under Linux or OS X, just type

```
#!sh

cd $PROTTEST_DOWNLOAD_DIR
tar zvxf prottest-3.x-[...].tar.gz
```

Where $PROTTEST\_DOWNLOAD\_DIR is the directory where `ProtTest` compressed file is located. Under Windows there are several tools for decompress, but we recommend GNU Zip:

  * [http://www.gzip.org/](http://www.gzip.org/)

## Installing Java ##

First of all, make sure that you have a Java Virtual Machine (JVM) properly installed in your system. The `ProtTest` 3 graphical interface requires a JRE equal or newer to 6.0, and this version it is also recommended for the console version. To test your JVM:
  * Go to [http://www.java.com/en/download/help/testvm.jsp](http://www.java.com/en/download/help/testvm.jsp)
  * Or in a terminal window, type “java –version”. The JVM is also included in:
    * Java Runtime Environment (JRE)
    * Java 2 Platform Standard Edition (J2SE)
  * More information on obtaining the JVM in:
    * [http://openjdk.java.net/](http://openjdk.java.net/)
    * [http://www.java.com/en/download/](http://www.java.com/en/download/)

## Installing MPJ Express ##

For the distributed memory version (i.e., for execution over HPC clusters), is necessary to install the [MPJ Express](MPJ_Express.md) library, included in the `ProtTest` distribution. In the [MPJ Express project home page](http://mpj-express.org) you can find more information. MPJ Express is a Java implementation of the message-passing standard interface [MPI](http://www.mcs.anl.gov/research/projects/mpi/).

First, you should check that the MPJ Express library is not already installed  in your machine. However, there is no problem in running the daemons included in the `ProtTest` 3 distribution, unless there are other daemons listening in the same port. In that case, you should configure your distribution to use another port. By default, the MPJ Express daemon listens on port 10,000.

Typically, the distributed version of `ProtTest` 3 will be executed under Unix-based operating systems. Follow the general instructions to configure and execute the MPJ Express daemons with the default settings:
1. Decompress the mpj distribution //$PROTTEST\_HOME/mpj.tar.gz//.
```
#!sh
cd $PROTTEST_HOME
tar zvxf mpj.tar.gz
```

2. Set the environment variable for the MPJ home and export to PATH.
```
#!sh
export MPJ_HOME=$PROTTEST_HOME/mpj
export PATH=$MPJ_HOME/bin:$PATH
```
You can also add these lines to ~/.bashrc or ~/.profile to automatically set these variables at the console startup.

3. The //$PROTTEST\_HOME/machines// file contains the set of computing nodes where the the mpj processes will be executed. By default it points to the localhost machine, so you should change it if you want to run a parallel execution over a cluster machine, just identifying on each line the computing nodes (e.g. see //filecluster8.conf.template//).

4. Start the MPJ Express daemons:
```
#!sh
$ mpjboot machines
```
The application "mpjboot" should be in the execution path (it is located at //$MPJ\_HOME/bin//). A ssh service must be running in the machines listed in the machines file. Moreover, as indicated above, the port 10000 should be free. For more details, please refer to the MPJ Express documentation.

For custom configurations and more information, please check the official user guides:
  * [MPJ Express user guide for Linux/OS X](http://mpj-express.org/docs/guides/linuxguide.pdf)
  * [MPJ Express user guide for Windows](http://mpj-express.org/docs/guides/windowsguide.pdf)

## First Execution ##

Once the JVM is installed, `ProtTest` 3 can be started using the different execution scripts for the different flavors of the application provided, like //runXProtTest.sh// (Unix-based OS) or //runXProtTest.bat// (Windows) for the desktop version.