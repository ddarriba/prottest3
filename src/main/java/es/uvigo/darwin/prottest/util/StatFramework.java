/*
Copyright (C) 2004  Federico Abascal

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
//
//  StatFramework.java
//  vanilla-xcode
//
//  Created by Federico Abascal on Tue Aug 03 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package es.uvigo.darwin.prottest.util;

import java.util.Hashtable;
import java.util.Iterator;

import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.selection.model.SelectionModel;

public class StatFramework {
        //recibe los delta.
        //ordena los datos y calcula los pesos
        //guarda las cosas en un hash
        //implementa métodos para dar el ranking y el peso a partir del String modelName
        //implementa métodos para dar el modelName y el peso a partir del ranking
    
    private Hashtable<String, Integer> hashSorted;          //clave: nombre modelo, valor: ranking
    private Hashtable<Integer, String> hashSortedReverse;   //clave: raking,        valor: nombre
    private Hashtable<String, Double> hashWeights;          //clave: nombre modelo, valor: peso
    private Hashtable<String, Double> hashDeltas;           //clave: nombre modelo, valor delta
    private String    framework;
    private String    description;
    private double    alphaImp       = 0.0;
    private double    invImp         = 0.0;
    private double    GIImp          = 0.0;
    private double    FImp           = 0.0;
    private double    overallAlpha   = 0.0;
    private double    overallInv     = 0.0;
    private double    overallAlphaGI = 0.0;
    private double    overallInvGI   = 0.0;
    private String[]  names;
    
    public StatFramework (InformationCriterion ic, String framework, String description) {
        this.framework     = framework;
        this.description   = description;
        alphaImp           = ic.getAlphaImportance();//calculateAlphaImp    (ic, false);
        invImp             = ic.getInvImportance(); //calculateInvImp      (ic, false);
        GIImp              = ic.getAlphaInvImportance();//calculateAlphaImp    (ic, true );
        FImp               = ic.getFImportance();//calculateFImp        (ic);
        overallAlpha       = ic.getOverallAlpha();//calculateOverallAlpha(ic, false);
        overallInv         = ic.getOverallInv();//calculateOverallInv  (ic, false);
        overallAlphaGI     = ic.getOverallAlphaInv();//calculateOverallAlpha(ic, true );
        overallInvGI       = ic.getOverallInvAlpha();//calculateOverallInv  (ic, true );
        initHashes         (ic, names);
    }
    
    private void initHashes (InformationCriterion ic, String[] names) {
        hashSorted        = new Hashtable<String, Integer>(); //clave: nombre modelo, valor ranking
        hashSortedReverse = new Hashtable<Integer, String>(); //clave: raking, valor: nombre
        hashWeights       = new Hashtable<String, Double>(); //clave: nombre modelo, valor peso
        hashDeltas        = new Hashtable<String, Double>(); //clave: nombre modelo, valor delta
        Iterator<SelectionModel> selectionModels = ic.allIterator();
        int position = 0;
        while (selectionModels.hasNext()) {
        	SelectionModel model = selectionModels.next();
        	hashSorted.put       (model.getModel().getModelName(),  new Integer(position));
            hashSortedReverse.put(new Integer(position),model.getModel().getModelName());
            hashWeights.put      (model.getModel().getModelName(),  new Double(model.getWeightValue()));
            hashDeltas.put       (model.getModel().getModelName(),  new Double(model.getDeltaValue()));
            position++;
        }
    }
    
    public int getRanking (String modelName) {
        return hashSorted.get(modelName).intValue()+1;
    }
    
    public double getWeight (String modelName) {
        return hashWeights.get(modelName).doubleValue();
    }
    
    public double getDelta (String modelName) {
        return hashDeltas.get(modelName).doubleValue();
    }
    
    public String getModelName (int ranking) {
        return hashSortedReverse.get(new Integer(ranking));
    }
    
    public String getFramework () {
        return framework;
    } 

    public String getDescription () {
        return description;
    }

	public double getAlphaImp() {
		return alphaImp;
	}

	public double getInvImp() {
		return invImp;
	}

	public double getGIImp() {
		return GIImp;
	}

	public double getFImp() {
		return FImp;
	}

	public double getOverallAlpha() {
		return overallAlpha;
	}

	public double getOverallInv() {
		return overallInv;
	}

	public double getOverallAlphaGI() {
		return overallAlphaGI;
	}

	/**
	 * Gets the overall inv gi.
	 * 
	 * @return the overall inv gi
	 */
	public double getOverallInvGI() {
		return overallInvGI;
	}
    
}
