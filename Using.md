# Using ProtTest 3 #

ProtTest 3 distribution contains a single binary file that can be executed in different flavors in order to satisfy the needs of different types of users:

  * [Graphical Version](Using_GUI_Version.md) (a.k.a. XProtTest): runs in common desktop computers. It is intended for users that are not familiar with the command line, or prefer a simpler way to execute and analyze the results. This is also the preferred mode for execution in a shared memory environment.
  * [Console Version](Using_Console_Version.md): designed for high performance computing environments, such as multicore clusters. Depending on the specific hardware, there might be different execution strategies:
    * **ProtTest 3 for shared memory architectures** is useful in multi-core architectures like a current desktop computer and intra-node computation in clusters.
    * **ProtTest 3 for distributed memory architectures** is the MPJ Express parallel version for all types of clusters.
    * **ProtTest 3 experimental hybrid version** combines both shared and distributed memory computation to increase the parallel efficiency and also increase the scalability.

## When to use each version ##

The shared memory version is intended for small to medium dataset executions in multi-core computers. In order to have a better usability, it is recommended to use the graphical interface (XProtTest3) in this case.

However, if a large dataset will be executed, the best choice is one of the MPJ Express distributed memory versions over a cluster. Because of the coarse-grained parallelism in both shared and distributed versions, the scalability will have an upper limit depending on the set of models used in each execution. Typically the highest reached speed-up will be around 50.

This problem can be avoided reducing the granularity of the parallelism by performing a hybrid execution. This hybrid implementation **is not** a merge of both shared and distributed versions. In general terms, the single tasks are distributed among computing nodes and each task is performed by a set of threads inside each node. Thread affinity is handled to efficiently distribute the workload and get the most of the available resources.

So, if a multi-core cluster is available, the hybrid version of ProtTest 3 will be the best choice if dealing with a large dataset. The scalability of this version is much higher than in the previous approaches. In fact, it is possible to run ProtTest 3 up to 512 times faster if there are enough resources available.

## General Configuration ##

Some execution parameters can be configured directly in the _prottest.properties_ file, located in _$PROTTEST\_HOME_:

| **Parameter Name** | **Values** | **Description** |
|:-------------------|:-----------|:----------------|
| analyzer | phyml | The external application used to estimate model parameters and likelihood scores. Currently, only the PhyML program is supported. |
| parallel\_strategy |  | The strategy followed to compute the model scores in distributed memory systems. |
|  | static | The model set is distributed _a priori_. It works well with a low number of processors. |
|  | dynamic | The model set is distributed on demand. |
|  | dynamic\_improved | Like the previous strategy, but including a distributor thread. If there is no inconvenience in running an extra thread in the root processors, this will be the best option. |
| phyml\_thread\_scheduling | true/false | Declares whether ProtTest 3 will attempt to run Phyml in parallel (using cores within a processor). |
| snapshot\_dir | snapshot/ | The directory where the snapshots from the fault tolerance system will be stored. |
| log\_dir | log/ | The directory where the log files will be stored. |
| log\_level |  | The granularity of the logging messages. |
|  | info | Only general information messages are logged. |
|  | fine |  General debug information is logged. |
|  | finer | More complex debug information is logged. |
|  | finest | All activity is logged. |

## Input Data ##

## **Input Alignments** ##

ProtTest natively admits amino acid sequence alignments in [Phylip format](http://www.bioperl.org/wiki/PHYLIP_multiple_alignment_format) and NEXUS sequential format, but several other alignment formats are also supported through the [Alter](http://sing.ei.uvigo.es/ALTER/api) API:

| | **ALN** |**FASTA** |**GDE** |**MSF** |**NEXUS** |**PHYLIP** | **PIR** |
|:|:--------|:---------|:-------|:-------|:---------|:----------|:--------|
|**Clustal** | X | X | X | X | X | X | X |
|**MAFFT** | X | X |  |  |  |  |  |
|**MUSCLE** | X | X |  | X |  | X |  |
|**PROBCONS** | X | X |  |  |  |  |  |
|**TCoffee** | X | X |  | X |  | X | X |

## **Starting Topologies** (Input Trees) ##

The likelihood is the probability of the data given a hypothesis. The hypothesis in this case is the amino acid replacement model and a phylogenetic tree. However, we are really interested in the models and the tree here becomes a nuisance parameter (and therefore we refer to it as the //base// tree). In any case, to calculate the likelihood score for a given model we should optimize not only the model parameters but also the tree topology and branch lengths of the (base) tree. The base tree is the tree used to calculate the likelihood for a given model. Although ideal, this is the slowest option, and faster approximations consist of using the same (base) topology for every model –branch lengths are always optimized for each model individually. ProtTest 3 implements 4 different strategies in this regard.

  * **Fixed BIONJ JTT**    A BIONJ tree for the JTT model is calculated //a priori//, that will \\become the base topology for each candidate model. The branch lengths are optimized during the calculation of the likelihood score but the topology remains fixed.
  * **BIONJ tree**    A BIONJ tree is calculated for each model and its branch lengths are optimized during the likelihood calculation.
  * **Maximum Likelihood tree**    Each model is optimized upon its own ML tree. This option is \\the one that reaches the maximum likelihood, but it is also the \\slowest option.
  * **User defined topology**    The user can bring its own topology in [Newick format](http://evolution.genetics.washington.edu/phylip/newicktree.html).

We should mention that model selection seems to be quite robust to the base tree used as long as this is a reasonable representation of the true phylogeny (Posada and Crandall 2001).

## Logging ##

ProtTest 3 automatically logs each execution in the **log directory** (see //General Configuration// section). The format of the log data follows the same scheme as the information shown in the application console. If you are running the GUI version of ProtTest, you will be able to specify which information you want to log with the "//Export to Console//" button included in each results window. The granularity of the log can be defined in the configuration file, but the default option should be enough for most users.

## Fault Tolerance ##

As each execution can take a very long time for large and complex datasets, ProtTest 3 implements a checkpoint-based fault tolerance system that offers the possibility of resuming a failed execution. It is not difficult to think about a hardware failure, especially when using many computational resources in the distributed memory version, or of a system stop for mantainance. This fault tolerance system allows to continue an analysis from a stable saved snapshot, putatively saving a lot of time. Snapshots will be stored in the //snapshot directory//, and they are automatically deleted when the application successfully completes an execution.