# User Manual #

ProtTest 3 is written in Java and takes advantage of the [www.cebl.auckland.ac.nz/pal-project/ PAL] library (Drummond and Strimmer 2001) for manipulating trees and alignments, and of the [Phyml](http://atgc.lirmm.fr/phyml/) program (Guindon and Gascuel 2003) for the computation of likelihoods and parameter estimation. ProtTest 3.0-beta includes:

  * Up to 120 candidate models of protein evolution:
    * **WAG** (Whelan and Goldman, 2001), **Dayhoff** (Dayhoff et al., 1978), **JTT** (Jones et al., 1992), **mtREV** (Adachi and Hasegawa, 1996), **MtMam** (Cao et al., 1998), **MtArt** (Abascal et al., 2007), **VT** (Muller and Vingron 2000), **RtREV** (Dimmic et al., 2002), **CpREV** (Adachi and Waddell, 2000), **Blosum62** (Henikoff and Henikoff, 1992), **LG** (Le and Gascuel, 2008), **DCMut**, **HIVw**/**HIVb** (Nickle et al. 2007) and **FLU** matrices.
    * +I, +G, +I+G and +F parameters.
  * 4 starting topologies:
    * Fixed BIONJ tree (estimated under the JTT model).
    * BIONJ tree for each model.
    * Maximum-likelihood tree for each model.
    * User tree
  * 4 Information Criteria:
    * Akaike Information Criterion (AIC).
    * Bayesian Information Criterion (BIC).
    * Corrected Akaike Information Criterion (AICc).
    * Decision Theory Criterion (DT).
  * Multimodel inference:
    * Model averaged estimates.
    * Model-averaged phylogeny (including branch lengths).
  * Model parameter estimates:
    * Parameter importance.
  * 4 modes:
    * Graphical desktop computer version (XProtTest).
    * Shared memory version for desktop computers or multi-core nodes.
    * Distributed memory version for cluster computing.
    * Hybrid shared-distributed memory version for multi-core clusters.
  * Fault-tolerance system (important for large dataset executions).
  * Automatic logging of the application activity.

**Sections:**
  * [Download and Installation](Download_and_install.md)
  * [Using ProtTest](Using.md)
    * [GUI-Based Version](Using_GUI_Version.md)
    * [Command Console Version](Using_Console_Version.md)
  * [Background](Background.md)
  * [References](References.md)