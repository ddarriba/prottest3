# Background #

## Models of Protein Evolution ##

Basically a model of protein evolution indicates the probability of change from a given amino acid to another over a period of time, given some rate of change. Although mechanistic models exist (Thorne and Goldman, 2003). The models of protein evolution currently used are preferentially based on empirical matrices for computational and data-complexity reasons. These matrices were constructed upon large datasets consisting of many diverse protein families. The most popular matrices are already included in ProtTest:

  * **WAG** (Whelan and Goldman, 2001)
  * **Dayhoff** (Dayhoff et al., 1978)
  * **JTT** (Jones et al., 1992)
  * **mtREV** (Adachi and Hasegawa, 1996)
  * **MtMam** (Cao et al., 1998)
  * **MtArt** (Abascal et al., 2007)
  * **VT** (Muller and Vingron 2000)
  * **RtREV** (Dimmic et al., 2002)
  * **CpREV** (Adachi and Waddell, 2000)
  * **Blosum62** (Henikoff and Henikoff, 1992)
  * **LG** (Le and Gascuel, 2008)
  * **DCMut** (Kosiol and Goldman, 2005)
  * **HIVw**/**HIVb** (Nickle et al. 2007)
  * **FLU** (Dang et al., 2010)

Conservation of protein function and structure imposes constraints on the rate of change for different amino acids positions in the alignment. This rate variation can be taken into account consider that a fraction of positions are invariable (commonly indicated with a “+I” code in the name of the model) (Reeves 1992), or we can consider  different rate categories, and assign each site a probability to belong to each of these categories (usually indicated by a “+G” code) (Yang 1993), or we can include both in the model (+I+G). Also, we can use as equilibrium aminoacid frequencies those observed in the alignment at hand (indicated as “+F”) (Cao et al., 1994).

## Statistics for model selection: Akaike Information Criterion and others ##

For a review of model selection in phylogenetics see Sullivan and Joyce (2005) and Johnson and Omland (2003). Burnham and Anderson (2003) provide a very good description of the AIC framework and its use for model averaging (which they call multimodel inference, and adapted to the phylogenetic context in Posada and Buckley (2004). In brief, the fit of a model of protein evolution (M) to a given data set (D), given a tree (T) and branch lenghts (B) is measured by the likelihood function (L):

![http://bytebucket.org/diegodl/prottest3/wiki/images/lkfunction.png](http://bytebucket.org/diegodl/prottest3/wiki/images/lkfunction.png)

The likelihood measures the fit of a model, but we also need to take into account the number of parameters. Adding parameters to a model implies improving the fit (or at least maintain it), but also a higher the variance for the estimates. Statistical model selection tries to find a good compromise between fit and complexity (number of parameters). The model selection strategies includes in ProtTest 3 include the Akaike Information Criterion (AIC) (and its correctd version AICc), the Bayesian Information Criterion (BIC) and performance-based decision theory (DT).

### AIC ###
The Akaike information criterion (AIC)(Akaike 1974) is an asymptotically unbiased estimator of the Kullback-Leibler information quantity (Kullback and Leibler 1951). We can think of the AIC as the amount of information lost when we use a specific model to approximate the real process of molecular evolution. Therefore, the model with the smallest AIC is preferred. The AIC is computed as:

![http://bytebucket.org/diegodl/prottest3/wiki/images/aicformula.png](http://bytebucket.org/diegodl/prottest3/wiki/images/aicformula.png)

(LnL: log-likelihood; K: number of free parameters). Since AIC is on a relative scale, it is useful to present  the AIC differences (or deltaAIC). For the ith model, the AIC difference is:

![http://bytebucket.org/diegodl/prottest3/wiki/images/deltaaic.png](http://bytebucket.org/diegodl/prottest3/wiki/images/deltaaic.png)

where minAIC is the smallest AIC among all candidate models. The AIC differences are easy to interpret and allow a quick comparison and ranking of candidate models.

### AICc ###
The AIC might not be accurate when the size of the sample is small compared to the number of parameters. For these cases, it is recommended to use a second-order AIC or corrected AIC (AICc in ProtTest; (Sugiura, 1978)), which includes a penalty for cases where the sample size is small:

![http://bytebucket.org/diegodl/prottest3/wiki/images/aiccformula.png](http://bytebucket.org/diegodl/prottest3/wiki/images/aiccformula.png)

where //n// is the size of the sample (see below). If //n// is large with respect to //K//, the second term is negligible, and AICc behaves similar to AIC. The corrected AIC is recommended when relation n/K is small (for example n/K < 40, being K the the number of parameters of the most complex model among the set of candidate models).

### BIC ###
The Bayesian Information Criterion (BIC; (Schwarz 1978)) is another measure of model fit. The BIC is considered a good approximation of the (very computationally demanding) Bayesian methods, and is formulated as:

![http://bytebucket.org/diegodl/prottest3/wiki/images/bicformula.png](http://bytebucket.org/diegodl/prottest3/wiki/images/bicformula.png)


### DT ###

ProtTest also calculates the performance-based decision theory criterion proposed by Minin et al (2003). This approach selects models on the basis of their phylogenetic performance, measured as the expected error on branch lengths estimates weighted by their BIC.

![http://bytebucket.org/diegodl/prottest3/wiki/images/dt1.png](http://bytebucket.org/diegodl/prottest3/wiki/images/dt1.png)

where

![http://bytebucket.org/diegodl/prottest3/wiki/images/dt2.png](http://bytebucket.org/diegodl/prottest3/wiki/images/dt2.png)

and where //t// is the number of taxa.



## Phylogenetics and Sample Size ##

What is the sample size of a protein alignment? This is very unclear. ProtTest 3 offers different criteria to calculate it:

**Alignment length (default).** Number of variable sites.
**Shannon entropy summed over all alignment positions. Shannon Entropy: (Shannon 1948).**

![http://bytebucket.org/diegodl/prottest3/wiki/images/shannon.png](http://bytebucket.org/diegodl/prottest3/wiki/images/shannon.png)

In our case if a position is completely conserved it takes the value of 0. If completely disordered (frequency of every aminoacid equals 1/20) it takes the value of 4.32.
**Number of sequences × length of the alignment × normalized Shannon’s entropy The normalized Shannon’s entropy is calculated by summing the entropies over all positions, dividing this quantity by the number of positions, and dividing the resulting quantity by the maximum possible entropy (4.32).** Number of sequences × length of the alignment.
**User’s provided size.**

## Model weights and the relative importance of parameters ##

The AIC (or AICc, or BIC) differences can be used for calculating the AIC/AICc/BIC weights:

![http://bytebucket.org/diegodl/prottest3/wiki/images/akaikeweights.png](http://bytebucket.org/diegodl/prottest3/wiki/images/akaikeweights.png)

these weights can be interpreted as the probability that a model is the best AIC/AICc/BIC model.

### **Parameter importance** ###

By summing the weights of the models that include a given parameter, for example the gamma distribution, we get the relative importance of such parameter:

![http://bytebucket.org/diegodl/prottest3/wiki/images/pimportance.png](http://bytebucket.org/diegodl/prottest3/wiki/images/pimportance.png)

where

![http://bytebucket.org/diegodl/prottest3/wiki/images/pimportance2.png](http://bytebucket.org/diegodl/prottest3/wiki/images/pimportance2.png)

### **Model-averaged parameter estimates** ###

We can also obtain an averaged estimation of any parameter by summing the different estimates for the models that contain such parameter after multiplying them by the weight of the corresponding model. For example, the model-averaged estimate of alpha for //R// candidate models would be:

![http://bytebucket.org/diegodl/prottest3/wiki/images/mapestimate.png](http://bytebucket.org/diegodl/prottest3/wiki/images/mapestimate.png)

where

![http://bytebucket.org/diegodl/prottest3/wiki/images/mapestimate2.png](http://bytebucket.org/diegodl/prottest3/wiki/images/mapestimate2.png)

and

![http://bytebucket.org/diegodl/prottest3/wiki/images/mapestimate3.png](http://bytebucket.org/diegodl/prottest3/wiki/images/mapestimate3.png)

### Model averaged phylogeny ###
Indeed, the averaged parameter could be the tree itself, so we could construct a model–averaged estimate of the phylogeny. For example, one could estimate a ML tree for all models (or a best subset) and with those one could build a weighted consensus tree using the corresponding Akaike weights (see Posada and Buckley (2004a) for a practical example). ProtTest 3 implements the calculation of model-averaged phylogenies, using different consensus thresholds and estimating averaged branch lengths as the weighted median of the branch lengths estimated for each candidate model.