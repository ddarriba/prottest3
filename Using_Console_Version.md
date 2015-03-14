## ProtTest 3 Command Console Version ##

The command console version is designed for  execution in high performance computing environments, such as multicore clusters. There are three possible strategies:
  1. **Shared memory execution** for multi-core machines.
  1. **Message-passing based distributed memory execution** for HPC clusters.
  1. **Hybrid distributed-shared memory execution** for multicore clusters.

For those strategies which imply distributed memory (i.e., strategies 2 and 3), you should have installed the MPJ Express distribution as described in the [Install section](Download_and_install.md).

The general command syntax is as following:
```
#!sh

cd $PROTTEST_HOME
java -jar prottest3.jar -i <alignment_file> -<models_to_evaluate> -<selection_criteria> [other options]
```
for the shared memory version or
```
#!sh

cd $PROTTEST_HOME
./runProtTest-HPC -i <alignment_file> -<models_to_evaluate> -<selection_criteria> [other options]
```
for the distributed and hybrid memory versions.

### ProtTest 3 Parameters ###

The command line options for ProtTest3 slightly differ from previous versions of ProtTest. Indeed, an alignment file is required. Furthermore, it is necessary to explicitly specify which models will be evaluated. On one hand single replacement matrices (JTT, WAG, etc) can be added to the candidate set of models, or every replacement matrix (right now, 15) can be included with the option //-all-matrices//. On the other hand, if you want to evaluate +I and/or +I+G models you should specify the options explained below. Every other option is optional, and those which have a default value is indicated in the right column.

| **Param** | **Value** | **Description** | **Default** |
|:----------|:----------|:----------------|:------------|
| -i | 

<alignment\_file>

 | Input alignment |  |
| -t | 

<tree\_file>

 | Input user defined topology | Fixed BIONJ Tree |
| -o | 

<output\_file>

 | Output file | Standard output |
| -matrix] |  | Evaluates the specified matrix. See supported matrices in [general description](User_Manual.md). If no matrix is specified, the whole set is evaluated |  |
| -I |  | Include models with a proportion of invariable sites |  |
| -G |  | Include models with rate variation among sites |  |
| -IG |  | Include models with both +I and +G properties |  |
| -all-distributions |  | Include +I, +G and +IG models |  |
| -ncat | 

<number\_of\_categories>

 | Indicate the number of rate categories for +G and +I+G models | 4 |
| -F |  | Include models with estimated amino acid frequencies |  |
| -all |  | Displays a table comparing all model selection frameworks | false |
| -S | 

<starting\_tree>

 | Starting topology selection mode | 0 |
|  | 0 | Fixed BIONJ-JTT Tree |  |
|  | 1 | BIONJ Tree for each model |  |
|  | 2 | Maximum Likelihood tree for each model |  |
|  | 3 | User defined topology |  |
| -AIC |  | Sorts models by Akaike Information Criterion |  |
| -BIC |  | Sorts models by Bayesian Information Criterion |  |
| -AICC |  | Sorts models by Corrected Akaike Information Criterion |  |
| -DT |  | Sorts models by Decision Theory Criterion |  |
| -sample | 

<sample\_size\_mode>

 | Sample size for AICc and BIC  | 2 |
|  | 0 | Shannon-entropy Sum |  |
|  | 1 | Average (0-1) Shannon-entropy x NxL |  |
|  | 2 | Total number of characters (alignment length) |  |
|  | 3 | Number of variable characters |  |
|  | 4 | Alignment length x number of taxa (NxL) |  |
|  | 5 | Specified by the user |  |
| -size | 

<user\_sample\_size>

 | Specified sample side if "-sample 5" |  |
| -t1 |  | Display the tree corresponding to the best-fit model in Newick format |  |
| -t2 |  | Display the tree corresponding to the best-fit model in ASCII format  |  |
| -tc | 

<consensus\_threshold>

 | Calculate and display a model-averaged phylogeny tree with the specified consensus threshold |  |
| -threads | 

<number\_of\_threads>

 | Number of threads requested for the run (only if MPJ Express is not used) | 1 |
| -verbose |  | Verbose mode |  |