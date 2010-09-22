//
//  Utilities.java
//  vanilla-xcode
//
//  Created by Federico Abascal on Wed Sep 01 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package es.uvigo.darwin.prottest.util;

import java.net.URL;
import java.util.StringTokenizer;

// TODO: Auto-generated Javadoc
/**
 * The Class Utilities.
 */
public class Utilities {
	
    /**
     * Instantiates a new utilities.
     */
    public Utilities() {    }
    
    /**
     * Last token.
     * 
     * @param str the str
     * 
     * @return the string
     */
    public static String lastToken (String str) {
        StringTokenizer st = new StringTokenizer(str);
        String token = "";
        while (st.hasMoreTokens()) {
            token = st.nextToken();
        }
        return token;    
    }
    
    /**
     * Checks if is windows.
     * 
     * @return true, if is windows
     */
    public static boolean isWindows () {
        if(System.getProperty("os.name").startsWith("Window"))
            return true;
        return false;
    }
    
    /**
     * Gets the average.
     * 
     * @param meassures the meassures
     * 
     * @return the average
     */
    public static long getAverage(long[] meassures) {
    	double avg = 0;
    	for (long value : meassures)
    		avg += value;
    	avg /= meassures.length;
    	return Math.round(avg);
    }
    
    /**
     * Gets the max.
     * 
     * @param meassures the meassures
     * 
     * @return the max
     */
    public static long getMax(long[] meassures) {
    	long max = Long.MIN_VALUE;
    	for (long value : meassures) {
    		if (value > max)
    			max = value;
    	}
    	return max;	
    }
    
    /**
     * Gets the min.
     * 
     * @param meassures the meassures
     * 
     * @return the min
     */
    public static long getMin(long[] meassures) {
    	long min = Long.MAX_VALUE;
    	for (long value : meassures) {
    		if (value < min)
    			min = value;
    	}
    	return min;	
    }
    
    /**
     * Arrange runtime.
     * 
     * @param runTime the run time
     * 
     * @return the string
     */
    public static String arrangeRuntime(long runTime) {
		long seconds = (long) Math.round(runTime/1000.0);
		int  hours   = (int ) (seconds/(60.0*60.0));
		int  rest1   = (int ) (seconds%(60.0*60.0));
		int  minutes = (int ) (rest1/60.0);
		seconds      = (int ) (seconds - (hours*60*60 + minutes*60));
        String h = "" + hours;
        String m = "" + minutes;
        String s = "" + seconds;
        if(minutes < 10)
            m = "0"+m;
        if(seconds < 10)
            s = "0"+s;
		return h + "h:" + m + ":" + s + "";
    }
    
    /**
     * Calculate runtime.
     * 
     * @param startTime the start time
     * @param endTime the end time
     * 
     * @return the string
     */
    public static String calculateRuntime(long startTime, long endTime) {
        return arrangeRuntime(endTime - startTime);
    }

    /**
     * Gets the path.
     * 
     * @return the path
     */
    public static String getPath () {
        return (new Utilities()).internalGetPath(false);
    }
    
    /**
     * Gets the uRL path.
     * 
     * @return the uRL path
     */
    public static String getURLPath () {
        return (new Utilities()).internalGetPath(true);
    }
    
    /**
     * Internal get path.
     * 
     * @param withFile the with file
     * 
     * @return the string
     */
    private String internalGetPath (boolean withFile) {
        //ClassLoader loader = this.getClass().getClassLoader();
        //URL tmp = loader.getResource("./");
        String j   = null;
        URL    tmp = null;
        try {
            //tmp = XProtTest.class.getResource("");
            tmp = getClass().getResource("");
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            return null;
        }
        if(tmp == null) {
            System.err.println("***************************************************************");
            System.err.println("** ERROR: Cannot find ProtTest's path, unable to run phyml!! **");
            System.err.println("***************************************************************");
            //prottest.setValue(-1);
            return null;
        } else {
            j = tmp.getPath();
            j = replace(j, "%20", " ");
            //j = tmp.getPath().replaceAll("%20", " ");
			//System.err.println("j: " + j);
            if(!withFile) {
                if(isWindows())
                    j = replace(j, "file:/", "");
                else
                    j = replace(j, "file:" , "");
            }
            //j = Pattern.compile("file:").matcher(j).replaceFirst("");
			//j = j.replaceFirst("file:", "");
			//System.err.println("after replaceFirst:  " + j);
            //j = Pattern.compile("!.*$").matcher(j).replaceFirst("");
            int last2 = j.lastIndexOf("!");
            j = j.substring(0, last2);
			//j = j.replaceFirst("!.*$", "");
			//System.err.println("after replaceFirst2: " + j);
			int last =  j.lastIndexOf("/");
			//j = j.replaceFirst("/.*?.jar$", "");
			j = j.substring(0, last);
			//System.err.println("after replaceFirst3: " + j);
        }
        return j;
    }
    
    /**
     * Quote it.
     * 
     * @param orig the orig
     * 
     * @return the string
     */
    public static String quoteIt (String orig) {
        //quote spaces (or other characters) of filenames:
        String result = replace(orig, " "  , "\\ ");
        //orig = replace(orig, "\\\\ ", "\\ ");
        return result;
    }

    //I took this method from SkeetUtil ( http://www.yoda.arachsys.com/java/skeetutil/ )
    /**
     * Replace.
     * 
     * @param orig the orig
     * @param from the from
     * @param to the to
     * 
     * @return the string
     */
    public static String replace (String orig, String from, String to) {
        int fromLength = from.length();
        
        if (fromLength==0)
            throw new IllegalArgumentException 
            ("String to be replaced must not be empty");
        
        int start = orig.indexOf (from);
        if (start==-1)
            return orig;
        
        boolean greaterLength = (to.length() >= fromLength);
        
        StringBuffer buffer;
        // If the "to" parameter is longer than (or
        // as long as) "from", the final length will
        // be at least as large
        if (greaterLength) {
            if (from.equals (to))
                return orig;
            buffer = new StringBuffer(orig.length());
        }
        else {
            buffer = new StringBuffer();
        }
        
        char [] origChars = orig.toCharArray();
        
        int copyFrom=0;
        while (start != -1) {
            buffer.append (origChars, copyFrom, start-copyFrom);
            buffer.append (to);
            copyFrom=start+fromLength;
            start = orig.indexOf (from, copyFrom);
        }
        buffer.append (origChars, copyFrom, origChars.length-copyFrom);
        
        return buffer.toString();
    }
    
    public static double round(double value, int decimals) {
        return (Math.round(value * Math.pow(10, decimals)))/Math.pow(10, decimals);
    }
}
