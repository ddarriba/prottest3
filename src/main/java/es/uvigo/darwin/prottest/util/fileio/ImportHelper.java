/*
 * ImportHelper.java
 *
 * (c) 2002-2005 BEAST Development Core Team
 *
 * This package may be distributed under the
 * Lesser Gnu Public Licence (LGPL)
 */
package es.uvigo.darwin.prottest.util.fileio;

import es.uvigo.darwin.prottest.util.exception.ImportException;
import java.io.EOFException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;

/**
 * A helper class for phylogenetic file format importers
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 *
 * @version $Id: ImportHelper.java 699 2007-04-28 07:07:49Z matthew_cheung $
 */
public class ImportHelper {
    private long totalCharactersRead = 0;

    // Expected length of input in bytes, or 0 if unknown
    private long expectedInputLength = 0;

    /**
     * ATTENTION: The ImportHelper never closes the reader passed to the constructor.
     * If the reader holds resources (e.g. a FileReader, which holds an open file),
     * then it is the client class' responsibility to close the reader when it has
     * finished using it.
     * @param reader
     */
    public ImportHelper(Reader reader) {
        this.reader = new LineNumberReader(reader);
        this.commentWriter = null;
    }

    public void setExpectedInputLength(long l) {
        this.expectedInputLength = l;
    }

    public ImportHelper(Reader reader, Writer commentWriter) {
        this.reader = new LineNumberReader(reader);
        this.commentWriter = commentWriter;
    }

    /**
     * @return  If the length of the input is known (because a file was
     * passed to the constructor), this reports a value between 0.0 and 1.0
     * indicating the relative read position in the file. Otherwise, this
     * always returns 0.0.
     *
     * This method assumes that all characters in the input are one byte
     * long (to get its estimate, it divides the number of *characters* read
     * by the number of *bytes* in the file). If there is an efficient way
     * to fix this, we should do so :)
     */
    public double getProgress() {
        if (expectedInputLength == 0) {
            return 0.0;
        } else {
            return (double) totalCharactersRead / expectedInputLength;
        }
    }

    public void closeReader() throws IOException {
        reader.close();
    }

    public void setCommentDelimiters(char line) {
        hasComments = true;
        this.lineComment = line;
    }

    public void setCommentDelimiters(char start, char stop) {
        hasComments = true;
        this.startComment = start;
        this.stopComment = stop;
    }

    public void setCommentDelimiters(char start, char stop, char line) {
        hasComments = true;
        this.startComment = start;
        this.stopComment = stop;
        this.lineComment = line;
    }

    public void setCommentDelimiters(char start, char stop, char line, char write, char meta) {
        hasComments = true;
        this.startComment = start;
        this.stopComment = stop;
        this.lineComment = line;
        this.writeComment = write;
        this.metaComment = meta;
    }

    public void setCommentWriter(Writer commentWriter) {
        this.commentWriter = commentWriter;
    }

    public int getLineNumber() {
        return reader.getLineNumber();
    }

    public int getLastDelimiter() {
        return lastDelimiter;
    }

    public char nextCharacter() throws IOException {
        if (lastChar == '\0') {
            lastChar = readCharacter();
        }
        return (char)lastChar;
    }

    public char readCharacter() throws IOException {

        skipSpace();

        char ch = read();

        while (hasComments && (ch == startComment || ch == lineComment)) {
            skipComments(ch);
            skipSpace();
            ch = read();
        }

        return ch;
    }

    public void unreadCharacter(char ch) {
        lastChar = ch;
    }

    public char next() throws IOException {
        if (lastChar == '\0') {
            lastChar = read();
        }
        return (char)lastChar;
    }

    /**
     * All read attempts pass through this function.
     * 
     * @return the next char
     * @throws IOException
     */
    public char read() throws IOException {
        int ch;
        if (lastChar == '\0') {
            // this is the only point where anything is read from the reader
            ch = reader.read();
            if (ch != -1) {
                totalCharactersRead++;
            } else {
                throw new EOFException();
            }
        } else {
            ch = lastChar;
            lastChar = '\0';
        }
        return (char)ch;
    }

    /**
     * Reads a line, skipping over any comments.
     */
    public String readLine() throws IOException {

        StringBuffer line = new StringBuffer();
        char ch = read();
        try {
            while (ch != '\n' && ch != '\r') {
                if (hasComments) {
                    if (ch == lineComment) {
                        skipComments(ch);
                        break;
                    }
                    if (ch == startComment) {
                        skipComments(ch);
                        ch = read();
                    }
                }
                line.append(ch);
                ch = read();
            }

            // accommodate DOS line endings..
            if (ch == '\r') {
                if (next() == '\n') read();
            }
            lastDelimiter = ch;

        } catch (EOFException e) {
            // We catch an EOF and return the line we have so far
//            encounteredEndOfFile();
        }

        return line.toString();
    }

    /**
     * Attempts to read and parse an integer delimited by whitespace.
     */
    public int readInteger() throws IOException, ImportException {
        String token = readToken();
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException nfe) {
            throw new ImportException("Number format error: " + nfe.getMessage());
        }
    }

    /**
     * Attempts to read and parse an integer delimited by whitespace or by
     * any character in delimiters.
     */
    public int readInteger(String delimiters) throws IOException, ImportException {
        String token = readToken(delimiters);
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException nfe) {
            throw new ImportException("Number format error: " + nfe.getMessage());
        }
    }

    /**
     * Attempts to read and parse a double delimited by whitespace.
     */
    public double readDouble() throws IOException, ImportException {
        String token = readToken();
        try {
            return Double.parseDouble(token);
        } catch (NumberFormatException nfe) {
            throw new ImportException("Number format error: " + nfe.getMessage());
        }
    }

    /**
     * Attempts to read and parse a double delimited by whitespace or by
     * any character in delimiters.
     */
    public double readDouble(String delimiters) throws IOException, ImportException {
        String token = readToken(delimiters);
        try {
            return Double.parseDouble(token);
        } catch (NumberFormatException nfe) {
            throw new ImportException("Number format error: " + nfe.getMessage());
        }
    }

    /**
     * Reads a token stopping when any whitespace or a comment is found.
     * If the token begins with a quote char then all characters will be
     * included in token until a matching quote is found (including whitespace or comments).
     */
    public String readToken() throws IOException {
        return readToken("");
    }

    /**
     * Reads a token stopping when any whitespace, a comment or when any character
     * in delimiters is found. If the token begins with a quote char
     * then all characters will be included in token until a matching
     * quote is found (including whitespace or comments).
     */
    public String readToken(String delimiters) throws IOException {
        int space = 0;
        char ch, ch2, quoteChar = '\0';
        boolean done = false, first = true, quoted = false, isSpace;

        nextCharacter();

        StringBuffer token = new StringBuffer();

        while (!done) {
            ch = read();

            try {
                isSpace = Character.isWhitespace(ch);

                if (quoted && ch == quoteChar) { // Found the closing quote
                    ch2 = read();

                    if (ch == ch2) {
                        // A repeated quote character so add this to the token
                        token.append(ch);
                    } else {
                        // otherwise it terminates the token

                        lastDelimiter = ' ';

                        if (hasComments && (ch2 == startComment || ch2 == lineComment)) {
                            skipComments(ch2, startComment!= '\"' && startComment != '\'');
                        } else {
                            unreadCharacter(ch2);
                        }
                        done = true;
                        quoted = false;
                    }
                } else if (first && (ch == '\'' || ch == '"')) {
                    // if the opening character is a quote
                    // read everything up to the closing quote
                    quoted = true;
                    quoteChar = ch;
                    first = false;
                    space = 0;
                } else if (!quoted && (ch == startComment || ch == lineComment) ) {
                    // comment markers don't count if we are quoted
                    skipComments(ch, startComment!= '\"' && startComment != '\'');
                    lastDelimiter = ' ';
                    done = true;
                } else {
                    if (quoted) {
                        token.append(ch);
                    } else if (isSpace) {
                        lastDelimiter = ' ';
                        done = true;
                    } else if (delimiters.indexOf(ch) != -1) {
                        done = true;
                        lastDelimiter = ch;
                    } else {
                        token.append(ch);
                        first = false;
                    }
                }
            } catch (EOFException e) {
                // We catch an EOF and return the token we have so far
                done = true;
            }
        }

        if (Character.isWhitespace((char)lastDelimiter)) {
            ch = nextCharacter();
            while (Character.isWhitespace(ch)) {
                read();
                ch = nextCharacter();
            }

            if (delimiters.indexOf(ch) != -1) {
                lastDelimiter = readCharacter();
            }
        }

        return token.toString();
    }

    /**
     * Skips over any comments. The opening comment delimiter is passed.
     * @param delimiter
     * @throws java.io.IOException
     */
    protected void skipComments(char delimiter) throws IOException {
       skipComments(delimiter, false);
    }

    /**
     * Skips over any comments. The opening comment delimiter is passed.
     * @param delimiter 
     * @param gobbleStrings
     * @throws java.io.IOException
     */
    protected void skipComments(char delimiter, boolean gobbleStrings) throws IOException {

        char ch;
        int n=1;
        boolean write = true;
        StringBuffer meta = null;

        if (lastComment == null) {
            lastComment = new StringBuffer();
        }
        if (nextCharacter() == writeComment) {
            read();
//            if (commentWriter != null) {
//                commentWriter.write(writeComment);
//            }
        } else if (nextCharacter() == metaComment) {
            read();
            meta = new StringBuffer();
            write = false;
        }
        lastMetaComment = null;

        if (delimiter == lineComment) {
            String line = readLine();
            if (write && commentWriter != null) {
                commentWriter.write(line, 0, line.length());
                commentWriter.write('\n');//.newLine();
            } else if (meta != null) {
                meta.append(line);
            }
        } else {
            Character inString = null;
            do {
                ch = read();

                if( ch == '\"' || ch == '\'' ) {
                    if( gobbleStrings ) {
                        if( inString == null ) {
                            inString = ch;
                        } else if( inString == ch ) {
                          inString = null;
                        }
                    }
                }
                if( inString == null )  {
                    if (ch == startComment) {
                        lastComment = new StringBuffer();
                        n++;
                        continue;
                    } else if (ch == stopComment) {
                        if (write && commentWriter != null) {
                            if (lastComment.toString().contains("ID")) {
                                commentWriter.write(lastComment.toString());
                            } 
                            lastComment = new StringBuffer();
                        }
//                        if (write && commentWriter != null) {
//                            commentWriter.write('\n');//.newLine();
//                        }
                        n--;
                        continue;
                    }
                }

                if (write && commentWriter != null) {
                    lastComment.append(ch);
//                    commentWriter.write(ch);
                } else if (meta != null) {
                    meta.append(ch);
                }
            } while (n > 0);

        }

        if (meta != null) {
            lastMetaComment = meta.toString();
        }
    }

    /**
     * Skips to the end of the line. If a comment is found then this is read.
     */
    public void skipToEndOfLine() throws IOException {

        char ch;

        do {
            ch = read();
            if (hasComments) {
                if (ch == lineComment) {
                    skipComments(ch);
                    break;
                }
                if (ch == startComment) {
                    skipComments(ch);
                    ch = read();
                }
            }

        } while (ch != '\n' && ch != '\r');

        if (ch == '\r') {
            if (nextCharacter() == '\n') read();
        }
    }

    /**
     * Skips char any contiguous characters in skip. Will also skip
     * comments.
     */
    public void skipWhile(String skip) throws IOException {

        char ch;

        do {
            ch = read();
        } while ( skip.indexOf(ch) > -1 );

        unreadCharacter(ch);
    }

    /**
     * Skips over any space (plus tabs and returns) in the file. Will also skip
     * comments.
     */
    public void skipSpace() throws IOException {
        skipWhile(" \t\r\n");
    }

    /**
     * Skips over any contiguous characters in skip. Will also skip
     * comments and space.
     */
    public void skipCharacters(String skip) throws IOException {
        skipWhile(skip + " \t\r\n");
    }

    /**
     * Skips over the file until a character from delimiters is found. Returns
     * the delimiter found. Will skip comments and will ignore delimiters within
     * comments.
     */
    public char skipUntil(String skip) throws IOException {
        char ch;

        do {
            ch = readCharacter();
        } while ( skip.indexOf(ch) == -1 );

        return ch;
    }

    public String getLastMetaComment() {
        return lastMetaComment;
    }

    public void clearLastMetaComment() {
        lastMetaComment = null;
    }

    static String safeName(String name) {
        if( ! name.matches("[a-zA-Z0-9_.]+") ) {
            name = "\"" + name + "\"";
        }
        return name;
    }

    /**
     * Convert control (unprintable) characters to something printable
     * @param token
     * @return token printable version
     */
    static String convertControlsChars(String token) {
        if( ! token.matches("[^\\p{Cntrl}]+") ) {
            StringBuilder b = new StringBuilder();
            for( char c : token.toCharArray() ) {
                if( c < 0x20 || c >= 0xfe ) {
                    b.append("#").append(Integer.toHexString(c));
                } else {
                    b.append(c);
                }
            }
            return b.toString();
        }
        return token;
    }

    // Private stuff

    private LineNumberReader reader;
    private Writer commentWriter = null;

    private int lastChar = '\0';
    private int lastDelimiter = '\0';

    private boolean hasComments = false;
    private char startComment = (char)-1;
    private char stopComment = (char)-1;
    private char lineComment = (char)-1;
    private char writeComment = (char)-1;
    private char metaComment = (char)-1;

    private StringBuffer lastComment;
    private String lastMetaComment = null;
}
