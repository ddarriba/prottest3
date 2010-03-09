package es.uvigo.darwin.prottest.exe;


/**
 * The Class RAxMLAminoAcidRunEstimator. It optimizes Amino-Acid
 * model parameters using RAxML.
 * 
 * This implementation fits with RAxML 7.0.4.
 */
public class RaxMLAminoAcidRunEstimator {//extends AminoAcidRunEstimator {
//
//	/** Prefix for temporary statistic files. */
//	private static final String STATS_FILE_PREFIX = "RAxML_info.";
//	
//	/** Prefix for temporary tree files. */
//	private static final String TREE_FILE_PREFIX = "RAxML_result.";
//	
//	public Model getModel() {
//		return model;
//	}
//	
//	/**
//	 * Instantiates a new optimizer for amino-acid models
//	 * using RAxML.
//	 * 
//	 * @param options the application options instance
//	 * @param model the amino-acid model to optimize
//	 */
//	public RaxMLAminoAcidRunEstimator(ApplicationOptions options, Model model) {
//		super(options, model);
//
//		try {
//			this.model = (AminoAcidModel) model;
//		} catch ( ClassCastException cce ) {
//			throw new ProtTestInternalException("Wrong model type");
//		}
//
//		if(!optimizeModel())
//			throw new ProtTestInternalException("Optimization error");
//	}
//	
//	/* (non-Javadoc)
//	 * @see es.uvigo.darwin.prottest.exe.RunEstimator#optimizeModel(es.uvigo.darwin.prottest.global.options.ApplicationOptions)
//	 */
//	public boolean runEstimator() 
//		throws OSNotSupportedException {
//		//let's call Phyml with the proper command line
//
//		PrintWriter err = options.getPrinter().getErrorWriter();
//		
//		// substitution model name
//		StringBuffer modelType = new StringBuffer();
//		if (model.isGamma() && model.isInv())
//			modelType.append("PROTGAMMAI");
//		else if (model.isGamma())
//			modelType.append("PROTGAMMA");
//		else if (model.isInv())
//			modelType.append("PROTMIXI");
//		else
//			modelType.append("PROTMIX");
//		modelType.append(model.getMatrix().toUpperCase());
//		if (model.isPlusF())
//			modelType.append("F");
//		
////		String inv = "0.0";
////		if(model.isInv())
////			inv = "e";
//		
//		String rateCathegories  = "1";
////		String alpha = " ";
//		if(model.isGamma()) {
//			rateCathegories  = "" + options.ncat;
////			alpha = "e";
//		}
//		String tr = "BIONJ";
//		if(options.getTreeFile()!= null)
//			tr = options.getWorkTree();
////		String F  = "d";
////		if(model.isPlusF())
////			F = "e";
////		String topo = "lr";
////		if(options.strategyMode == ApplicationGlobals.OPTIMIZE_COMPLETE)
////			topo = "tlr";
//	
//		String[] str;
//		try {
//			//TODO: Change parameters to RAxML
//			Process proc = null;
//			Runtime runtime = Runtime.getRuntime();
////
////			String str[] = new String[22];
//			StringBuffer args = new StringBuffer();
//			if(getRaxmlVersion() != null) {
//				//				raxmlHPC[-MPI|-PTHREADS] -s sequenceFileName
//				//				-n outputFileName
//				//				-m substitutionModel
//				//				[-a weightFileName]
//				//				[-b bootstrapRandomNumberSeed]
//				//				[-c numberOfCategories]
//				//				[-d]
//				//				[-e likelihoodEpsilon]
//				//				[-E excludeFileName]
//				//				[-f a|b|c|d|e|g|h|i|j|m|n|o|p|s|t|w|x]
//				//				[-g groupingFileName]
//				//				[-h]
//				//				[-i initialRearrangementSetting]
//				//				6
//				//				[-j]
//				//				[-k]
//				//				[-l sequenceSimilarityThreshold]
//				//				[-L sequenceSimilarityThreshold]
//				//				[-M]
//				//				[-o outGroupName1[,outGroupName2[,...]]]
//				//				[-p parsimonyRandomSeed]
//				//				[-P proteinModel]
//				//				[-q multipleModelFileName]
//				//				[-r binaryConstraintTree]
//				//				[-t userStartingTree]
//				//				[-T numberOfThreads]
//				//				[-u multiBootstrapSearches]
//				//				[-v]
//				//				[-w workingDirectory]
//				//				[-x rapidBootstrapRandomNumberSeed]
//				//				[-y]
//				//				[-z multipleTreesFile]
//				//				[-#|-N numberOfRuns]
//				
//				// command arg.
//				java.io.File currentDir = new java.io.File("");
//				String commandName  =  
//					currentDir.getAbsolutePath() +"/bin/"+getRaxmlVersion();
////				str[1]  = "";
////				str[2]  = "";
////				str[3]  = "";
//				// input alignment
//				args.append("-s " + options.getWorkAlignment(1));
////				str[4]  = new String("-s" );
////				str[5]  = new String(options.getWorkAlignment());
//				//number of rate categories
//				args.append(" -c " + rateCathegories);
////				str[6]  = new String("-c");
////				str[7]  = new String(rateCathegories); 
//				// substitution model
//				args.append(" -m " + modelType.toString());
////				str[8]  = new String("-m"); //the model
////				str[9]  = new String(modelType.toString());
//				// proportion of invariable sites
////				str[10]  = "";//new String("-v");
////				str[11]  = "";//new String(inv);
//				// value of the gamma shape parameter (if gamma distribution)
////				if(alpha != " ") {
////					str[12]  = new String("-a");
////					str[13] = new String(alpha);
////				} else {
////					str[12] = str[13] = new String(" ");
////				}
//				// topology optimization
////				str[14] = new String("-o");
////				str[15] = new String(topo);
////				str[14] = "-f";
////				str[15] = "e";
//				// amino-acid frequencies
////				str[16] = "";//new String("-f");
////				str[17] = "";//new String(F);
//				// starting tree file
//				if(tr != "BIONJ") {
////					args.append(" -f e");
////					args.append(" -t "+tr);
////					str[18] = new String("-t");
////					str[19] = new String(tr);
//				}
//				// name
//				args.append(" -n "+ getRAxMLFileName(options.getWorkAlignment(1)));
////				str[20] = "-n";
////				str[21] = options.getWorkAlignment();
//				// data type
////				str[20] = new String("-d");
////				str[21] = new String("aa");
//				// bootstrapping
////				str[22] = new String("-b");
////				str[23] = new String("0");
//	
//				String[] argsArray = args.toString().split(" ");
//				str = new String[argsArray.length + 1];
//				str[0] = commandName;
//				for (int i=1; i < str.length; i++)
//					str[i] = argsArray[i-1];
//				System.err.print("Raxml's command-line: ");
//				for(int i=0; i<str.length; i++)  
//					System.err.print(str[i] + " "); 
//				System.err.println("");
//				
//				proc = runtime.exec(str);
//			} else {
//				OSNotSupportedException e =
//					new OSNotSupportedException();
//				throw e;
//			}
//			proc.getOutputStream().close();
//	
//			//open options.log_file
//			FileOutputStream tmp = new FileOutputStream(options.getLogFile(1));
//			PrintWriter      log = new PrintWriter(tmp, true);
//	
//			StreamGobbler errorGobbler  = new StreamGobbler(new InputStreamReader(proc.getErrorStream()), "RAxML-Error", true, log, options.getPrinter().getErrorWriter());
//			StreamGobbler outputGobbler = new StreamGobbler(new InputStreamReader(proc.getInputStream()), "RAxML-Output", true, log, options.getPrinter().getErrorWriter());
//			errorGobbler.start ();
//			outputGobbler.start();
//			int exitVal = proc.waitFor();
//			if(exitVal != 0) {
//				err.println("RAxML's exit value: " + exitVal + " (there was probably some error)");
//				//                 System.err.println("Phyml command-line: " + Utilities.getPath() + "/bin/" + Utilities.getPhymlVersion() + " " + options.phymlAlignment + " 1 i 1 0 " + model.getMatrix() + " " + inv + " " + ncat + " " + alpha + " " + tr + " " + topo + " y " + F);
//				err.print("RAxML's command-line: ");
//				for(int i=0; i<str.length; i++)  
//					err.print(str[i] + " "); 
//				err.println("");
//	
//				err.println("Please, take a look at the RAxML's log below:");
//				String line;
//				try {
//					FileReader input = new FileReader(options.getLogFile(1));
//					BufferedReader br = new BufferedReader(input);
//					while((line = br.readLine())!=null) {
//						err.println(line);
//					}
//				} catch ( IOException e ) { 
//					err.println("Unable to read the options file: " + options.getLogFile(1));
//				}
//			}
//		} catch (InterruptedException e) {
//			throw new ExternalExecutionException("Interrupted execution: " + e.getMessage());
//		} catch (IOException e) {
//			throw new ExternalExecutionException("I/O error: " + e.getMessage());
//		}
//
//		if(!(readStatsFile() && readTreeFile ()))
//			return false;
//	
//		return true;
//	}
//	
//	/**
//	 * Read the temporary statistics file.
//	 * 
//	 * @return true, if successful
//	 */
//	private boolean readStatsFile() {
//		String line;
//		PrintWriter out = options.getPrinter().getOutputWriter();
//		PrintWriter err = options.getPrinter().getErrorWriter();
//		
//		//TODO: Change results parsing
//		try {
//			String statsFile = getRAxMLFilePathName(STATS_FILE_PREFIX, options.getWorkAlignment(1));
//			System.err.println(statsFile);
//			FileReader input = new FileReader(statsFile);
//			BufferedReader br = new BufferedReader(input);
//			while((line = br.readLine())!=null) {
//				if(options.isDebug()) 
//					System.out.println("[DEBUG] RAxML: " + line);
//				if(line.length()>0)
//					if(line.startsWith("Substitution Matrix")) {
//						if(!model.getMatrix().toUpperCase().equals(Utilities.lastToken(line))) {
//							String errorMsg = "Matrix names doesn't match";
//							err.println("RAxML: " + line);
//							err.println("Last token: " + Utilities.lastToken(line));
//							err.println("It should be: " + model.getMatrix());
//							err.println(errorMsg);
//							throw new StatsFileFormatException(errorMsg);
//						} 
//					} else if(line.startsWith("Likelihood   :")) {
//						model.setLk(Double.parseDouble(Utilities.lastToken(line)));
//					} else if(line.startsWith(". Discrete gamma model")) {
//						if(Utilities.lastToken(line).equals("Yes")) {
//							line = br.readLine();
//							if(options.isDebug()) System.out.println("[DEBUG] PHYML: " + line);
//							if(model.numOfTransCategories != Integer.parseInt(Utilities.lastToken(line))) {
//								String errorMsg = "There was some error in the number of transition categories: " 
//									+ model.numOfTransCategories+ " vs " + Integer.parseInt(Utilities.lastToken(line));
//								err.println(errorMsg);
//								
//								throw new StatsFileFormatException(errorMsg);
//								//prottest.setCurrentModel(-2);
//							}
//							line = br.readLine();
//							if(options.isDebug()) out.println("[DEBUG] PHYML: " + line);
//							model.setAlpha(Double.parseDouble(Utilities.lastToken(line)));
//						}
//					} else if(line.startsWith("Inference[0]:")) {
////						model.setInv(Double.parseDouble(Utilities.lastToken(line)));
//						if (line.contains("alpha[0]:")) {
//							// parse Alpha value
//							System.err.println("Parsing alpha");
//						} 
//						if (line.contains("invar[0]:")) {
//							// parse Invariant value
//							System.err.println("Parsing invar");
//						}
//					} else if(line.startsWith(". Time used")) {
//						time = Utilities.lastToken(line);
//					}
//			}
//		} catch ( IOException e ) {
//			e.printStackTrace(System.err);
//			throw new StatsFileFormatException(e.getMessage());
//		}
//		return true; 
//	}
//	
//	/**
//	 * Read the temporary tree file.
//	 * 
//	 * @return true, if successful
//	 * 
//	 * @throws TreeFormatException the tree format exception
//	 */
//	private boolean readTreeFile() 
//		throws TreeFormatException {
//		
//		try {
//			model.setTree(new ReadTree(getRAxMLFilePathName(TREE_FILE_PREFIX, options.getWorkAlignment(1))));
//		} catch (TreeParseException e) {
//			String errorMsg = "ProtTest: wrong tree format, exiting...";
//			throw new TreeFormatException(errorMsg);
//		} catch (IOException e) {
//			String errorMsg = "Error: File not found (IO error), exiting...";
//			throw new TreeFormatException(errorMsg);
//		}
//		return true;
//	}
//	
//	/**
//	 * Delete temporary files.
//	 * 
//	 * @return true, if successful
//	 */
//	protected boolean deleteTemporaryFiles() {
//		File f;
//		f = new File(getRAxMLFilePathName(STATS_FILE_PREFIX, options.getWorkAlignment(1)));
//		f.delete();
//		f = new File(getRAxMLFilePathName(TREE_FILE_PREFIX, options.getWorkAlignment(1)));
//		f.delete();
//		f = new File(options.getLogFile(1));
//		f.delete();
//		return true;
//	}
//	
//	/**
//	 * Gets the RAxML executable name for the current Operating System.
//	 * 
//	 * @return the RAxML executable name
//	 */
//	private String getRaxmlVersion () {
//        String os = System.getProperty("os.name");
//        if(os.startsWith("Mac")) {
//			String oa = System.getProperty("os.arch");
//			if(oa.startsWith("ppc")) {
//				return "raxml-prottest-macppc";
//			} else { 
//				return "raxml-prottest-macintel";
//			}
//        } else if(os.startsWith("Linux")) {
//            return "raxml-prottest-linux";
//        } else if(os.startsWith("Window")) {
//            return "raxml-prottest-windows.exe";
//        } else {
//            return null;
//        }
//    }
//	
//	/**
//	 * Builds the path to the temporary file given by the prefix
//	 * 
//	 * @param prefix the temporary file prefix
//	 * @param alignmentFile the alignment filename, also included in the temporary file
//	 * 
//	 * @return the RAxML temporary filename and path
//	 */
//	private String getRAxMLFilePathName(String prefix, String alignmentFile) {
//		String[] path = alignmentFile.split("/");
//		StringBuffer result = new StringBuffer();
//		for (int i = 0; i < (path.length - 1); i++)
//			result.append(path[i]+"/");
//		result.append(prefix + path[path.length-1]);
//		return prefix + path[path.length-1];
//	}
//	
//	/**
//	 * Gets the RAxML filename. Trunks the path.
//	 * 
//	 * @param alignmentFile the alignment filename
//	 * 
//	 * @return the RAxML filename, excluding path
//	 */
//	private String getRAxMLFileName(String alignmentFile) {
//		String[] path = alignmentFile.split("/");
//		return path[path.length-1];
//	}
//
//	public void run() {
//		
//		if (!this.optimizeModel())
//			throw new ProtTestInternalException("Optimization error");
//		
//	}
//	
}
