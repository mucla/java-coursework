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
 * K�sittelij�luokka tulostukselle
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
    //private Ryhma oletusRyhma; //jos vie ryhm�n (esim. ty�kalujen tulostamiseksi)
    
    /**
     * Tulostaa j�senen tiedot
     * @param jasen j�sen jonka tiedot tulostetaan
     */
    protected void tulostaTiedot(Jasen jasen) {
        try (PrintStream os = TextAreaOutputStream.getTextPrintStream(tulostusAlue)) {
            os.println("J�senen tiedot\n===================");
            jasen.tulosta(os);
            /* os.println("J�senen ty�kalut\n===================");
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
     * N�ytt�� tulostusalueessa tekstin
     * @param jasen j�sen jonka tiedot tulostetaan
     * @return kontrolleri, jolta voidaan pyyt�� lis�� tietoa
     */
    public static TulostusController tulosta(Jasen jasen) {
        //oletusRyhma = ryhma;
        TulostusController tulostusCtrl = 
        (TulostusController) ModalController.showModeless(TulostusController.class.getResource("Tulostus.fxml"),
                "Tulostus", jasen);
        return tulostusCtrl;
    }

}
