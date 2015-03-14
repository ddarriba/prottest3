# `ProtTest 3` #

## A high performance computing application for selection of best-fit models of protein evolution ##

## What can I use `ProtTest` for? – Introduction ##

`ProtTest` is a bioinformatic tool for the selection of best-fit models of aminoacid replacement for the data at hand. `ProtTest` makes this selection by finding the model in the candidate list with the smallest Akaike Information Criterion (AIC), Bayesian Information Criterion (BIC) score or Decision Theory Criterion (DT). At the same time, `ProtTest` obtains model-averaged estimates of different parameters (including a model-averaged phylogenetic tree) and calculates their importance(Posada and Buckley 2004). `ProtTest` differs from its nucleotide analog jModeltest (Darriba et.al 2012) in that it does not include likelihood ratio tests, as not all models included in `ProtTest` are nested.

`ProtTest` is written in Java and uses the program [PhyML](http://code.google.com/p/phyml) (Guindon and Gascuel, 2003) for the maximum likelihood (ML) estimation of phylogenetic trees and model parameters. The current version of `ProtTest` (3.2) includes 15 different rate matrices that result in 120 different models when we consider rate variation among sites (+I: invariable sites; +G: gamma-distributed rates) and the observed amino acid frequencies (+F).

For technical doubts, bug reports or comments, please use the [ProtTest forum](https://groups.google.com/forum/m/#!forum/prottest). Take also a look at the [FAQ](FAQ.md).

**Sections:**
  * [Download](Download_and_install.md)
  * [User Manual](User_Manual.md)
  * [Known Bugs](Known_Bugs.md)
  * [References](References.md)
  * [Frequently Asked Questions](FAQ.md)

**Links:**
  * [Homepage](http://darwin.uvigo.es/software/prottest3)
  * [ProtTest Forum](https://groups.google.com/forum/m/#!forum/prottest)

## Disclaimer ##

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. The ProtTest3 distribution includes Phyml executables. This program is protected by their own license and conditions, and using ProtTest3 implies agreeing with those conditions as well.

## Citation ##

  * **Darriba D, Taboada GL, Doallo R, Posada D. ProtTest 3: fast selection of best-fit models of protein evolution. Bioinformatics, 27:1164-1165, 2011**

In addition, given that ProtTest uses Phyml intensively, we encourage users to cite this program as well when using `ProtTest`:
  * **Guindon S, Gascuel O. 2003. A simple, fast, and accurate algorithm to estimate large phylogenies by maximum likelihood. Syst Biol. 52: 696-704.** [Phyml](Phyml.md)