/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.darwin.prottest.util.fileio;

/*
 * NexusImporter.java
 *
 * (c) 2002-2005 JEBL development team
 *
 * This package may be distributed under the
 * Lesser Gnu Public Licence (LGPL)
 */
import es.uvigo.darwin.prottest.util.attributable.Attributable;
import es.uvigo.darwin.prottest.taxa.Taxon;
import es.uvigo.darwin.prottest.tree.TreeUtils;
import es.uvigo.darwin.prottest.util.exception.ImportException;
import pal.tree.SimpleTree;
import pal.tree.Tree;

import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pal.misc.Identifier;
import pal.tree.Node;
import pal.tree.NodeFactory;

/**
 * Class for importing NEXUS file format
 *
 * @version $Id: NexusImporter.java 723 2007-06-11 05:40:44Z matt_kearse $
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 */
public class NexusImporter {

    public enum NexusBlock {

        UNKNOWN,
        TAXA,
        CHARACTERS,
        DATA,
        UNALIGNED,
        DISTANCES,
        TREES
    }
    
    private boolean compactTrees = false;
    private Writer commentWriter;
    private String nexusId;
    
    public String getNexusId() {
        return nexusId;
    }
    
    // NEXUS specific ImportException classes
    public static class MissingBlockException extends ImportException {

        public MissingBlockException() {
            super();
        }

        public MissingBlockException(String message) {
            super(message);
        }
    }

    /**
     * Constructor
     */
    public NexusImporter(Reader reader, long expectedLength) {
        helper = new ImportHelper(reader);
        helper.setExpectedInputLength(expectedLength);
        initHelper();
    }

    /**
     * Constructor
     */
    public NexusImporter(Reader reader) {
        this(reader, 0);
    }

    public NexusImporter(Reader reader, boolean compactTrees, long expectedInputLength) {
        this(reader, expectedInputLength);
        this.compactTrees = compactTrees;
    }

    private void initHelper() {
        // ! defines a comment to be written out to a log file
        // & defines a meta comment
        helper.setCommentDelimiters('[', ']', '\0', '!', '&');
        commentWriter = new StringWriter();
        helper.setCommentWriter(commentWriter);
    }

//    public long findId() {
//        
//    }
    
    /**
     * This function returns an integer to specify what the
     * next block in the file is. The internal variable nextBlock is also set to this
     * value. This should be overridden to provide support for other blocks. Once
     * the block is read in, nextBlock is automatically set to UNKNOWN_BLOCK by
     * findEndBlock.
     */
    public NexusBlock findNextBlock() throws IOException {
        findToken("BEGIN", true);
        nextBlockName = helper.readToken(";").toUpperCase();
        return findBlockName(nextBlockName);
    }

    /**
     * This function returns an enum class to specify what the
     * block given by blockName is.
     */
    private NexusBlock findBlockName(String blockName) {
        try {
            nextBlock = NexusBlock.valueOf(blockName);
        } catch (IllegalArgumentException e) {
            // handle unknown blocks. java 1.5 throws an exception in valueOf
            nextBlock = null;
        }

        if (nextBlock == null) {
            nextBlock = NexusBlock.UNKNOWN;
        }

        return nextBlock;
    }

    public String getNextBlockName() {
        return nextBlockName;
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public Iterator<Tree> iterator() {
        return new Iterator<Tree>() {

            public boolean hasNext() {
                boolean hasNext = false;
                try {
                    hasNext = hasTree();
                } catch (IOException e) {
                // deal with errors by stopping the iteration
                } catch (ImportException e) {
                // deal with errors by stopping the iteration
                }
                return hasNext;
            }

            public Tree next() {
                Tree tree = null;
                try {
                    tree = importNextTree();
                } catch (IOException e) {
                // deal with errors by stopping the iteration
                } catch (ImportException e) {
                // deal with errors by stopping the iteration
                }
                if (tree == null) {
                    throw new NoSuchElementException("No more trees in this file");
                }
                return tree;
            }

            public void remove() {
                throw new UnsupportedOperationException("operation is not supported by this Iterator");
            }
        };
    }

    /**
     * Parses a 'TREES' block.
     */
    public List<Tree> parseTreesBlock(List<Taxon> taxonList) throws ImportException, IOException {
        return readTreesBlock(taxonList);
    }

    // **************************************************************
    // TreeImporter IMPLEMENTATION
    // **************************************************************
    private boolean isReadingTreesBlock = false;
    private List<Taxon> treeTaxonList = null;
    private Map<String, Taxon> translationList = Collections.emptyMap();
    private Tree nextTree = null;
    private String[] lastToken = new String[1];

    /**
     * return whether another tree is available.
     */
    public boolean hasTree() throws IOException, ImportException {
        if (!isReadingTreesBlock) {
            isReadingTreesBlock = startReadingTrees();
            translationList = readTranslationList(treeTaxonList, lastToken);
        }

        if (!isReadingTreesBlock) {
            return false;
        }

        if (nextTree == null) {
            nextTree = readNextTree(lastToken);
        }

        return (nextTree != null);
    }

    /**
     * import the next tree.
     * return the tree or null if no more trees are available
     */
    public Tree importNextTree() throws IOException, ImportException {
        // call hasTree to do the hard work...
        if (!hasTree()) {
            isReadingTreesBlock = false;
            return null;
        }

        Tree tree = nextTree;
        nextTree = null;

        return tree;
    }

    public List<Tree> importTrees() throws IOException, ImportException {
        isReadingTreesBlock = false;
        if (!startReadingTrees()) {
            throw new MissingBlockException("TREES block is missing");
        }
        List<Tree> treesBlock = readTreesBlock(treeTaxonList);
        helper.closeReader();
        nexusId = commentWriter.toString();
        commentWriter.close();
        return treesBlock;
    }

    public boolean startReadingTrees() throws IOException, ImportException {
        treeTaxonList = null;

        while (true) {
            try {
                NexusBlock block = findNextBlock();
                switch (block) {
//					case TAXA: treeTaxonList = readTaxaBlock(); break;
                    case TREES:
                        return true;
                    // Ignore the block..
                    default:
                        break;
                }
            } catch (EOFException ex) {
                break;
            }
        }

        return false;
    }

    // **************************************************************
    // DistanceMatrixImporter IMPLEMENTATION
    // **************************************************************

    // **************************************************************
    // PRIVATE Methods
    // **************************************************************
    /**
     * Finds the end of the current block.
     */
    private void findToken(String query, boolean ignoreCase) throws IOException {
        String token;
        boolean found = false;

        do {
            token = helper.readToken();

            if ((ignoreCase && token.equalsIgnoreCase(query)) || token.equals(query)) {
                found = true;
            }
        } while (!found);
    }

    /**
     * Finds the end of the current block.
     */
    public void findEndBlock() throws IOException {
        try {
            String token;

            do {
                token = helper.readToken(";");
            } while (!token.equalsIgnoreCase("END") && !token.equalsIgnoreCase("ENDBLOCK"));
        } catch (EOFException e) {
        // Doesn't matter if the End is missing
        }

        nextBlock = NexusBlock.UNKNOWN;
    }

    /**
     * Reads a 'TREES' block.
     */
    private List<Tree> readTreesBlock(List<Taxon> taxonList) throws ImportException, IOException {
        List<Tree> trees = new ArrayList<Tree>();
        double cumWeight = 0.0;
        
        String[] localLastToken = new String[1];
        translationList = readTranslationList(taxonList, localLastToken);

        while (true) {

            SimpleTree tree = readNextTree(localLastToken);

            if (tree == null) {
                break;
            }

            cumWeight += (Double) tree.getAttribute(tree.getRoot(), TreeUtils.TREE_WEIGHT_ATTRIBUTE);
            trees.add(tree);
            
        }

        if (trees.size() == 0) {
            throw new ImportException.BadFormatException("No trees defined in TREES block");
        }
        
        if (cumWeight > 1.0) {
            // normalization is required
            for (Tree tree : trees) {
                double treeWeight = (Double) tree.getAttribute(tree.getRoot(), TreeUtils.TREE_WEIGHT_ATTRIBUTE);
                treeWeight /= cumWeight;
                tree.setAttribute(tree.getRoot(), TreeUtils.TREE_WEIGHT_ATTRIBUTE, treeWeight);
            }
        }

        nextBlock = NexusBlock.UNKNOWN;

        return trees;
    }

    private Map<String, Taxon> readTranslationList(List<Taxon> taxonList, String[] lastToken) throws ImportException, IOException {
        Map<String, Taxon> localTranslationList = new HashMap<String, Taxon>();

        String token = helper.readToken(";");

        if (token.equalsIgnoreCase("TRANSLATE")) {

            do {
                String token2 = helper.readToken(",;");

                if (helper.getLastDelimiter() == ',' || helper.getLastDelimiter() == ';') {
                    if (token2.length() == 0 && (char) helper.getLastDelimiter() == ';') {
                        //assume an extra comma at end of list
                        break;

                    }
                    throw new ImportException.BadFormatException("Missing taxon label in TRANSLATE command of TREES block");
                }

                String token3 = helper.readToken(",;");

                if (helper.getLastDelimiter() != ',' && helper.getLastDelimiter() != ';') {
                    throw new ImportException.BadFormatException("Expecting ',' or ';' after taxon label in TRANSLATE command of TREES block");
                }

                Taxon taxon = Taxon.getTaxon(token3);

                if (taxonList != null && !taxonList.contains(taxon)) {
                    // taxon not found in taxon list...
                    // ...perhaps it is a numerical taxon reference?
                    throw new ImportException.UnknownTaxonException(token3);
                }
                localTranslationList.put(token2, taxon);

            } while (helper.getLastDelimiter() != ';');

            token = helper.readToken(";");

        } else if (taxonList != null) {
            for (Taxon taxon : taxonList) {
                localTranslationList.put(taxon.getName(), taxon);
            }
        }

        lastToken[0] = token;

        return localTranslationList;
    }

    private SimpleTree readNextTree(String[] lastToken) throws ImportException, IOException {
        try {
            SimpleTree tree = null;
            String token = lastToken[0];
            double weight;
            String treeName;

            boolean isUnrooted = token.equalsIgnoreCase("UTREE");
            if (isUnrooted || token.equalsIgnoreCase("TREE")) {

                if (helper.nextCharacter() == '*') {
                    // Star is used to specify a default tree - ignore it
                    helper.readCharacter();
                }

                final String meta = helper.getLastMetaComment();
                if (meta != null) {
                    // Look for the unrooted meta comment [&U]
                    if (meta.equalsIgnoreCase("U")) {
                        isUnrooted = true;
                    }
                    helper.clearLastMetaComment();
                }

                treeName = helper.readToken("=;");

                if (helper.getLastDelimiter() != '=') {
                    throw new ImportException.BadFormatException("Missing label for tree'" + treeName + "' or missing '=' in TREE command of TREES block");
                }

                try {

                    if (helper.nextCharacter() != '(') {
                        throw new ImportException.BadFormatException("Missing tree definition in TREE command of TREES block");
                    }

                    // Save tree comment and attach it later
                    final String comment = helper.getLastMetaComment();
                    helper.clearLastMetaComment();

                    Node internalNode = readInternalNode();
                    tree = new SimpleTree(internalNode);
                                        
                    int last = helper.getLastDelimiter();
                    if (last == ':') {
                        // root length - discard for now
						/*double rootLength = */ helper.readDouble(";");
                        last = helper.getLastDelimiter();
                    }

                    if (last != ';') {
                        throw new ImportException.BadFormatException("Expecting ';' after tree, '" + treeName + "', TREE command of TREES block");
                    }

                    weight = 1.0;
                    if (comment != null) {
                        // if '[W number]' (MrBayes), set weight attribute
                        // ignore any other comment
                        if (comment.matches("^W\\s+[\\+\\-]?[\\d\\.]+")) {
                            weight = new Double(comment.substring(2));
                        } 
                    }
                    tree.setAttribute(internalNode, TreeUtils.TREE_WEIGHT_ATTRIBUTE, weight);
                    tree.setAttribute(internalNode, TreeUtils.TREE_NAME_ATTRIBUTE, treeName);

                } catch (EOFException e) {
                    // If we reach EOF we may as well return what we have?
                    return tree;
                }

                token = helper.readToken(";");
            } else if (token.equalsIgnoreCase("ENDBLOCK") || token.equalsIgnoreCase("END")) {
                return null;
            } else {
                throw new ImportException.BadFormatException("Unknown command '" + token + "' in TREES block");
            }

            //added this to escape readNextTree loop correctly -- AJD
            lastToken[0] = token;

            return tree;

        } catch (EOFException e) {
            return null;
        }
    }

    /**
     * Reads a branch in. This could be a node or a tip (calls readNode or readTip
     * accordingly). It then reads the branch length and SimpleNode that will
     * point at the new node or tip.
     */
    private Node readBranch() throws IOException, ImportException {
        Node branch;

        helper.clearLastMetaComment();
        if (helper.nextCharacter() == '(') {
            // is an internal node
            branch = readInternalNode();

        } else {
            // is an external node
            branch = readExternalNode();
        }

        if (helper.getLastDelimiter() == ':') {
            final double length = helper.readDouble(",():;");
            branch.setBranchLength(length);
        }

        return branch;
    }

    /**
     * Reads a node in. This could be a polytomy. Calls readBranch on each branch
     * in the node.
     * @param tree
     * @return
     */
    private Node readInternalNode() throws IOException, ImportException {
        List<Node> children = new ArrayList<Node>();

        // read the opening '('
        helper.readCharacter();

        // read the first child
        children.add(readBranch());

        if (helper.getLastDelimiter() != ',') {
        //throw new ImportException.BadFormatException("Missing ',' in tree");
        }
        // MK: previously, an internal node must have at least 2 children.
        // MK: We we now allow trees with a single child so that we can create proper taxonomy
        // MK: trees with only a single child at a taxonomy level.

        // read subsequent children

        while (helper.getLastDelimiter() == ',') {
            children.add(readBranch());
        }

        // should have had a closing ')'
        if (helper.getLastDelimiter() != ')') {
            throw new ImportException.BadFormatException("Missing closing ')' in tree");
        }

        Node node = NodeFactory.createNode(children.toArray(new Node[0]));

        // find the next delimiter
        String token = helper.readToken(":(),;").trim();

        // if there is a token before the branch length, treat it as a node label
        // and store it as an attribute of the node...
        if (token.length() > 0) {
            node.setIdentifier(new Identifier((String) parseValue(token)));
        }

        return node;
    }

    /**
     * Reads an external node in.
     */
    private Node readExternalNode() throws ImportException, IOException {
        String label = helper.readToken(":(),;");

        Taxon taxon;
        try {
            taxon = Taxon.getTaxon(label);
        } catch (IllegalArgumentException e) {
            throw new ImportException.UnknownTaxonException(e.getMessage());
        }

        if (translationList.size() > 0) {
            taxon = translationList.get(label);

            if (taxon == null) {
                // taxon not found in taxon list...
                throw new ImportException.UnknownTaxonException("Taxon in tree, '" + label + "' is unknown");
            }
        }

        try {
            final Node node = NodeFactory.createNode(new Identifier(taxon.getName()));

            return node;
        } catch (IllegalArgumentException e) {
            throw new ImportException.DuplicateTaxaException(e.getMessage());
        }
    }

    static void parseMetaCommentPairs(String meta, Attributable item) throws ImportException.BadFormatException {
        // This regex should match key=value pairs, separated by commas
        // This can match the following types of meta comment pairs:
        // value=number, value="string", value={item1, item2, item3}
        // (label must be quoted if it contains spaces (i.e. "my label"=label)

        Pattern pattern = Pattern.compile("(\"[^\"]*\"+|[^,=\\s]+)\\s*(=\\s*(\\{[^=}]*\\}|\"[^\"]*\"+|[^,]+))?");
        Matcher matcher = pattern.matcher(meta);

        while (matcher.find()) {
            String label = matcher.group(1);
            if (label.charAt(0) == '\"') {
                label = label.substring(1, label.length() - 1);
            }
            if (label == null || label.trim().length() == 0) {
                throw new ImportException.BadFormatException("Badly formatted attribute: '" + matcher.group() + "'");
            }
            final String value = matcher.group(2);
            if (value != null && value.trim().length() > 0) {
                // there is a specified value so try to parse it
                item.setAttribute(label, parseValue(value.substring(1)));
            } else {
                item.setAttribute(label, Boolean.TRUE);
            }
        }
    }

    /**
     * This method takes a string and tries to decode it returning the object
     * that best fits the data. It will recognize command delimited lists enclosed
     * in {..} and call parseValue() on each element. It will also recognize Boolean,
     * Integer and Double. If the value starts with a # then it will attempt to decode
     * the following integer as an RGB colour - see Color.decode(). If nothing else fits
     * then the value will be returned as a string but trimmed of leading and trailing
     * white space.
     * @param value the string
     * @return the object
     */
    static Object parseValue(String value) {

        value = value.trim();

        if (value.startsWith("{")) {
            // the value is a list so recursively parse the elements
            // and return an array
            String[] elements = value.substring(1, value.length() - 1).split(",");
            Object[] values = new Object[elements.length];
            for (int i = 0; i < elements.length; i++) {
                values[i] = parseValue(elements[i]);
            }
            return values;
        }

        if (value.startsWith("#")) {
            // I am not sure whether this is a good idea but
            // I am going to assume that a # denotes an RGB colour
            try {
                return Color.decode(value.substring(1));
            } catch (NumberFormatException nfe1) {
            // not a colour
            }
        }

        // A string qouted by the nexus exporter and such
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.subSequence(1, value.length() - 1);
        }

        if (value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("FALSE")) {
            return Boolean.valueOf(value);
        }

        // Attempt to format the value as an integer
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe1) {
        // not an integer
        }

        // Attempt to format the value as a double
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException nfe2) {
        // not a double
        }

        // return the trimmed string
        return value;
    }

    // private stuff
    private NexusBlock nextBlock = null;
    private String nextBlockName = null;
    protected final ImportHelper helper;
}
