/*
 * XProtTestApp.java
 */

package es.uvigo.darwin.xprottest;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import es.uvigo.darwin.prottest.facade.ProtTestFacade;
import es.uvigo.darwin.prottest.facade.ProtTestFacadeThread;
import java.io.File;
import javax.swing.JFileChooser;

/**
 * The main class of the application.
 */
public class XProtTestApp extends SingleFrameApplication {

    public static final int ERROR_BEHAVIOR_CONTINUE = 0;
    public static final int ERROR_BEHAVIOR_STOP = 1;
    
    private ProtTestFacade prottestFacade;
    
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        prottestFacade = new ProtTestFacadeThread(
                Runtime.getRuntime().availableProcessors()
                );
//        prottestFacade = new ProtTestFacadeSequential();
        XProtTestView view = new XProtTestView(this, prottestFacade);
        show(view);
    }
    
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
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
