package es.uvigo.darwin.xprottest.util;

import java.io.IOException;
import javax.swing.JTextArea;
import java.io.Writer;

/**
 * Simple way to "print" to a JTextArea; just say
 * PrintWriter out = new PrintWriter(new TextAreaAppender(myTextArea));
 * Then out.println() et all will all appear in the TextArea.
 */
public final class TextAreaAppender extends Writer {

	private final JTextArea textArea;

	public TextAreaAppender(final JTextArea textArea) {
		this.textArea = textArea;
	}

    @Override
    public void flush(){ }
    
    @Override
    public void close(){ }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        textArea.append(new String(cbuf, off, len));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
