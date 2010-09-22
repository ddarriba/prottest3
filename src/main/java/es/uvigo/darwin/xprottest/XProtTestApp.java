/*
 * XProtTestApp.java
 */
package es.uvigo.darwin.xprottest;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import es.uvigo.darwin.prottest.facade.ProtTestFacade;
import es.uvigo.darwin.prottest.facade.ProtTestFacadeThread;
import es.uvigo.darwin.prottest.util.Utilities;
import java.awt.Font;
import java.io.File;
import javax.swing.JFileChooser;

/**
 * The main class of the application.
 */
public class XProtTestApp extends SingleFrameApplication {

    public static final int ERROR_BEHAVIOR_CONTINUE = 0;
    public static final int ERROR_BEHAVIOR_STOP = 1;
    public static final Font FONT_CONSOLE;
    public static final Font FONT_LABEL;
    public static final Font FONT_PANEL_TITLE;
    public static final Font FONT_OPTION_LABEL;
    
    private ProtTestFacade prottestFacade;

    static {
        if (Utilities.isWindows() == false) {
            FONT_CONSOLE = new Font(Font.MONOSPACED, 0, 10);
        } else {
            FONT_CONSOLE = new Font("Lucida Console", 0, 11);
        }
        FONT_LABEL = new Font("Application", 1, 9);
        FONT_OPTION_LABEL = new Font("Application", 0, 10);
        FONT_PANEL_TITLE = new Font("Application", 1, 10);
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        prottestFacade = new ProtTestFacadeThread(
                Runtime.getRuntime().availableProcessors());
//        prottestFacade = new ProtTestFacadeSequential();
        XProtTestView view = new XProtTestView(this, prottestFacade);
        show(view);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of XProtTestApp
     */
    public static XProtTestApp getApplication() {
        return Application.getInstance(XProtTestApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(XProtTestApp.class, args);
    }

    public static JFileChooser createFileChooser(String title) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
        fc.setFileFilter(fc.getAcceptAllFileFilter());
        fc.setCurrentDirectory(new File("."));
        return fc;
    }
}
