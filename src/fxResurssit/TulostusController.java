package fxResurssit;

import java.io.PrintStream;

import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import fi.jyu.mit.fxgui.TextAreaOutputStream;
import javafx.fxml.*;
import javafx.print.PrinterJob;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import ryhma.Jasen;

/**
 * Käsittelijäluokka tulostukselle
 * @author miemkoiv
 * @version 20.4.2017
 */
public class TulostusController implements ModalControllerInterface<Jasen>{
    @FXML private TextArea tulostusAlue;
    

    @Override
    public Jasen getResult() {
        // TODO Auto-generated method stub
        return jasenKohdalla;
    }

    @Override
    public void handleShown() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDefault(Jasen oletus) {
        jasenKohdalla = oletus;
        tulostaTiedot(jasenKohdalla);
    }
    
    @FXML
    void handleOK() {
        ModalController.closeStage(tulostusAlue);
    }

    @FXML
    void handleTulosta() {
        tulosta();
    }

//=====================================================
    private Jasen jasenKohdalla;
    //private Ryhma oletusRyhma; //jos vie ryhmän (esim. työkalujen tulostamiseksi)
    
    /**
     * Tulostaa jäsenen tiedot
     * @param jasen jäsen jonka tiedot tulostetaan
     */
    protected void tulostaTiedot(Jasen jasen) {
        try (PrintStream os = TextAreaOutputStream.getTextPrintStream(tulostusAlue)) {
            os.println("Jäsenen tiedot\n===================");
            jasen.tulosta(os);
            /* os.println("Jäsenen työkalut\n===================");
             * List<Tyokalu> tyok = ryhma.annaTyokalut(jasen);
             * for (Tyokalu tk : tyok)
             *      tk.tulosta(os);
             */
        }
    }
    
    /**
     * Hoitaa varsinaisen tulostuksen
     */
    protected void tulosta() {
       PrinterJob job = PrinterJob.createPrinterJob();
       if ( job != null && job.showPrintDialog(null) ) {
           WebEngine webEngine = new WebEngine(); 
           webEngine.loadContent("<pre>" + tulostusAlue.getText() + "</pre>");
           webEngine.print(job);
           job.endJob();
       }
    }
    
    /**
     * Näyttää tulostusalueessa tekstin
     * @param jasen jäsen jonka tiedot tulostetaan
     * @return kontrolleri, jolta voidaan pyytää lisää tietoa
     */
    public static TulostusController tulosta(Jasen jasen) {
        //oletusRyhma = ryhma;
        TulostusController tulostusCtrl = 
        (TulostusController) ModalController.showModeless(TulostusController.class.getResource("Tulostus.fxml"),
                "Tulostus", jasen);
        return tulostusCtrl;
    }

}
