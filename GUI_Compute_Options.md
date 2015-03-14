# Compute Options #

![http://bytebucket.org/diegodl/prottest3/wiki/images/gui/options.png](http://bytebucket.org/diegodl/prottest3/wiki/images/gui/options.png)

## Number of processors requested ##

You can choose how many processors (cores) you want to use to optimize the model set. The maximum of processors requested is the number of available cores in the machine. The example above is running in a computer with a Intel Core(TM)2 Duo CPU.

In many cases you would like to use the whole computing resources in the machine, so that is the default value.

## Substitution model matrices ##

The matrices of the candidate models to evaluate. In the [Background](Background.md) section you will find information about the supported matrices.

## Distributions ##

The parameters of the candidate models to evaluate:

**Rate variation**
  * **+I** Include models with a proportion of invariant sites.
  * **+G** Include models with some different categories of change (low, medium, high rate, etc) for each site.
  * **+I+G** Both +I and +G properties.

**Amino acid frequencies**
  * **Empirical** (+F models) Use the amino-acid probabilities seen in the alignment.

Also in the [Background](Background.md) there is more information about distributions.

## Total number of models ##

Displays the total number of candidate models. This is, MxD, where M is the number of matrices and D is the number of distributions.

## Starting topology ##

Ideally, one should optimize the tree topology, its branch lengths and the model parameters (for each model) to assure maximum likelihood is achieved. ProtTest 3 provides 4 different starting topologies, which generally the better the slower:

> | | **Description** |
|:|:----------------|
> | **Fixed BIONJ Tree** | The BIONJ tree for the JTT model is calculated _a priori_, and it will be the starting topology for each candidate model. |
> | **BIONJ Tree** | Each model is optimized using its own BIONJ tree. |
> | **ML Tree** | Each model is optimized calculating its own ML Tree. This option is the one which reaches the maximum likelihood, but it is also the slowest option. |
> | **User Defined Topology** | The user can bring its own topology in [Newick format](http://evolution.genetics.washington.edu/phylip/newicktree.html). |

However, model selection seems to be quite robust to topology as long as this is a reasonable representation of the true phylogeny (Posada and Crandall 2001). Therefore a faster strategy (and the one implemented in the program Modeltest (Posada and Crandall 1998)) is to estimate a “good” tree and make all likelihood calculations for all models in this fixed tree (first option of ProtTest 3). Because it only optimizes branch lengths and model parameters it has the advantage of being the fastest option.