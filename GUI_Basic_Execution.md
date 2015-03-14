# Basic Execution #

1. Go to **File / Load alignment** and load the input file (aligned protein sequences). See the [Using](Using.md) section for details about alignment formats.

2. Go to **Analysis / Compute likelihood scores** and indicated the set of candidate models you want to compare, marking the desired replacement matrices, estimation of amino acid frequencies (+F), proportion of invariables sites (+I) or rate variation among sites (+G. Here you should also indicate the starting topology  (Starting Topology / User Defined) for the likelihood optimizations. By default all models are included and a ML tree will be calculated for each model (note this is the slowest but most thorough option). To start the analysis click the **Compute** button. See the [Compute options](GUI_Compute_Options.md) section for details.

![http://bytebucket.org/diegodl/prottest3/wiki/images/gui/options.png](http://bytebucket.org/diegodl/prottest3/wiki/images/gui/options.png)

3. Wait for the analysis to finish. This can take some time depending on the size and complexity of the data. A window will appear during the analysis indicating the progress.

![http://bytebucket.org/diegodl/prottest3/wiki/images/gui/running-linux.png](http://bytebucket.org/diegodl/prottest3/wiki/images/gui/running-linux.png)

4. Once the likelihood calculations are finished, the model selection results will become accesible under the **Selection** menu. See the [Selection Results](GUI_Selection_Results.md) section for details.

![http://bytebucket.org/diegodl/prottest3/wiki/images/gui/results.png](http://bytebucket.org/diegodl/prottest3/wiki/images/gui/results.png)

5. Optimized trees for each model are available in **Analysis / Show trees**. See the [Model Trees](GUI_Model_Trees.md) section for details.

![http://bytebucket.org/diegodl/prottest3/wiki/images/gui/showtree.png](http://bytebucket.org/diegodl/prottest3/wiki/images/gui/showtree.png)

6. A model-averaged or multimodel phylogenetic tree can be built in **Analysis / Phylogenetic averaging**. See the [Phylogenetic Averaging](GUI_Phylogenetic_Averaging.md) section for details.

![http://bytebucket.org/diegodl/prottest3/wiki/images/gui/consensus.png](http://bytebucket.org/diegodl/prottest3/wiki/images/gui/consensus.png)