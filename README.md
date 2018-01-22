# prottest3

ProtTest is a bioinformatic tool for the selection of best-fit models of aminoacid replacement for the data at hand. ProtTest makes this selection by finding the model in the candidate list with the smallest Akaike Information Criterion (AIC), Bayesian Information Criterion (BIC) score or Decision Theory Criterion (DT). At the same time, ProtTest obtains model-averaged estimates of different parameters (including a model-averaged phylogenetic tree) and calculates their importance(Posada and Buckley 2004). ProtTest differs from its nucleotide analog jModeltest (Posada 2008) in that it does not include likelihood ratio tests, as not all models included in ProtTest are nested.

ProtTest is written in Java and uses the program PhyML (Guindon and Gascuel, 2003) for the maximum likelihood (ML) estimation of phylogenetic trees and model parameters. The current version of ProtTest (3.2) includes 15 different rate matrices that result in 120 different models when we consider rate variation among sites (+I: invariable sites; +G: gamma-distributed rates) and the observed amino acid frequencies (+F).

--------
Citation
--------

    Darriba D, Taboada GL, Doallo R, Posada D. ProtTest 3: fast selection of best-fit models of protein evolution. Bioinformatics, 27:1164-1165, 2011 

In addition, given that ProtTest uses Phyml intensively, we encourage users to cite this program as well when using ProtTest:

    Guindon S, Gascuel O. 2003. A simple, fast, and accurate algorithm to estimate large phylogenies by maximum likelihood. Syst Biol. 52: 696-704. Phyml 

--------
Discussion group
--------

For technical doubts, bug reports or comments, please use the ProtTest forum: http://groups.google.com/group/prottest

--------
Download
--------

New releases of ProtTest 3 can be downloaded from the releases section:
https://github.com/ddarriba/prottest3/releases

--------
News!
--------

08/05/2016 - ProtTest 3.4.2 is out. It includes the following updates:

    Removed dependency on Netbeans libraries
    Check for global binaries on PATH env variable
    Added prottest3 script for running both GUI and console interfaces

25/10/2015 - ProtTest 3.4.1 is out. It includes the following updates:

    Fixed bug with number of categories which might induce an error with certain PhyML versions.
    Removed dependency on WriterOutputStream class.

23/01/2014 - ProtTest 3.4 is out. It includes the following updates:

    Fixed problem while trying to execute ProtTest from outside its home directory.
    Modified log files pattern to prottest3_YYYYMMDDhhmmss_randstring.log
    Added configuration property for disable logging.
    Added configuration property for using a system-wide installed PhyML. 

16/07/2013 - ProtTest 3.3 is out. This includes PhyML real-time logging and removes the sample size selection option.

03/01/2013 - ProtTest 3.2.1 is out. A bug computing alpha and pInv parameters was fixed.
Disclaimer

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA. The ProtTest 3 distribution includes Phyml executables. This program is protected by their own license and conditions, and using ProtTest 3 implies agreeing with those conditions as well. 
