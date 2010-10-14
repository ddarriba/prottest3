/*
Copyright (C) 2009  Diego Darriba

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package es.uvigo.darwin.prottest.exe.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import pal.alignment.Alignment;
import pal.alignment.AlignmentUtils;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import es.uvigo.darwin.prottest.facade.thread.ThreadPoolSynchronizer;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

/**
 * A singleton class to manage the temporary files usage in the
 * application. It should be accessible for every thread that gets
 * part in the execution.
 */
public class TemporaryFileManager {

	/** The Constant BASE_ALIGNMENT_FILE_NAME. */
	private static final String BASE_ALIGNMENT_FILE_NAME = "prottest_alignment_";
	
	/** The Constant BASE_TREE_FILE_NAME. */
	private static final String BASE_TREE_FILE_NAME = "prottest_tree_";
	
	/** The Constant BASE_LOG_FILE_NAME. */
	private static final String BASE_LOG_FILE_NAME = "prottest_";
	
	/** The unique instance. */
	private static TemporaryFileManager instance;
	
	/** The alignment temporary files. */
	private File[] alignmentTempFile;
	
	/** The log temporary files. */
	private File[] logTempFile;
	
	/** The tree temporary file. */
	private File treeTempFile;
	
	/** The thread pool synchronizer. */
	private ThreadPoolSynchronizer synchronizer;
	
	/** The alignment. */
	private Alignment alignment;
	
	/** The tree. */
	private Tree tree;
	
	/**
	 * Gets the alignment filename.
	 * 
	 * @param thread the thread
	 * 
	 * @return the alignment filename
	 */
	public String getAlignmentFilename(Thread thread) {
		int threadId = synchronizer.getThreadId(thread);
		return alignmentTempFile[threadId].getAbsolutePath();
	}
	
	/**
	 * Gets the log filename.
	 * 
	 * @param thread the thread
	 * 
	 * @return the log filename
	 */
	public String getLogFilename(Thread thread) {
		int threadId = synchronizer.getThreadId(thread);
		return logTempFile[threadId].getAbsolutePath();
	}
	
	/**
	 * Gets the tree filename for a thread.
	 * 
	 * @param thread the thread
	 * 
	 * @return the tree filename
	 */
	public String getTreeFilename(Thread thread) {
		if (tree == null)
			return null;
		return treeTempFile.getAbsolutePath();
	}
	
	/**
	 * Checks if is synchronized.
	 * 
	 * @return true, if is synchronized
	 */
	public static boolean isSynchronized() {
		return instance != null;
	}
	
	/**
	 * Sets the input tree. For each manager sync, the input tree should be set just 
	 * once, or all the trees must be the same (i.e., this method can be used to
	 * check consistency between processes) 
	 * 
	 * @param tree the new tree
	 * 
	 * @throws ProtTestInternalException when attempting to set different trees
	 */
	public void setTree(Tree tree) {
		if (this.tree == null) {
			convertTree(tree, treeTempFile);
			this.tree = tree;
		} else {
			if (!tree.equals(this.tree)) {
				throw new ProtTestInternalException("Attempting to set different trees");
			}
		}
			
	}
	
	/**
	 * Gets the input tree.
	 * 
	 * @return the tree
	 */
	public Tree getTree() {
		return tree;
	}
	
	/**
	 * Gets the input alignment.
	 * 
	 * @return the alignment
	 */
	public Alignment getAlignment() {
		return alignment;
	}
	
	/**
	 * Instantiates a new temporary file manager.
	 * 
	 * @param path the path
	 * @param alignment the alignment
	 * @param tree the tree
	 * @param poolSize the pool size
	 */
	private TemporaryFileManager(Alignment alignment, Tree tree, int poolSize) {
		
		alignmentTempFile = new File[poolSize];
		logTempFile = new File[poolSize];
		this.alignment = alignment;
		this.tree = tree;

		try {
		    for (int i = 0; i < poolSize; i++) {
		    	alignmentTempFile[i] = File.createTempFile(BASE_ALIGNMENT_FILE_NAME, null);
		    	alignmentTempFile[i].deleteOnExit();
		    	logTempFile[i] = File.createTempFile(BASE_LOG_FILE_NAME, null);
		    	logTempFile[i].deleteOnExit();
		        convertAlignment(alignment, alignmentTempFile[i]);
	        }
		    treeTempFile = File.createTempFile(BASE_TREE_FILE_NAME, null);
		    treeTempFile.deleteOnExit();
		    if (tree != null)
	        	convertTree(tree, treeTempFile);
		} catch (IOException e) {
			throw new ProtTestInternalException("Cannot create temporary files");
		}
	    ThreadPoolSynchronizer.synchronize(poolSize);
	    synchronizer = ThreadPoolSynchronizer.getInstance();
	}
	
	/**
	 * Gets the single instance of TemporaryFileManager.
	 * 
	 * @return single instance of TemporaryFileManager
	 */
	public static TemporaryFileManager getInstance() {
		if (instance == null) {
			throw new ProtTestInternalException("TemporaryFileManager out of sync");
		}
		return instance;
	}
	
	/**
	 * Synchronizes this manager.
	 * 
	 * @param alignment the input alignment
	 * @param tree the input tree
	 * @param poolSize the pool size
	 */
	public static void synchronize(Alignment alignment, Tree tree, int poolSize) {
		instance = new TemporaryFileManager(alignment, tree, poolSize);
	}
	
	/**
	 * Converts an alignment into a file.
	 * 
	 * @param alignment the input alignment
	 * @param outputFile the output file
	 * 
	 * @return true, if successful
	 */
	private boolean convertAlignment(Alignment alignment, File outputFile) {
		try {
			FileOutputStream fo = new FileOutputStream(outputFile);
			PrintWriter output              = new PrintWriter(fo);
			//AlignmentUtils.printSequential(alignment, output);
			AlignmentUtils.printInterleaved(alignment, output);
			output.flush();
			output.close();
		} catch (IOException e) {
			throw new ProtTestInternalException();
		}
		return true;
	}

	/**
	 * Converts a tree into a file.
	 * 
	 * @param tree the input tree
	 * @param outputFile the output file
	 * 
	 * @return true, if successful
	 */
	private boolean convertTree(Tree tree, File outputFile) {
		if (tree != null) {
			try {
				FileOutputStream fo = new FileOutputStream(outputFile);
				PrintWriter output  = new PrintWriter(fo);
				TreeUtils.printNH(tree, output);
				output.flush();
				output.close();
			} catch (IOException e) {
				throw new ProtTestInternalException();
			}
		}
		return true;
	}
	
}
