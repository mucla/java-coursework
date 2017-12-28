package fxResurssit;

import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Luokka aloitusn�kym�n k�ytt�liittym�n tapahtumien hoitamiseksi
 * @author miemkoiv
 * @version 11.2.2017
 *
 */
public class AloitusnakymaController implements ModalControllerInterface<String>{
   
    @FXML private TextField textVastaus;
    
    /**
     * Mit� tehd��n kun dialogi tulee n�kyviin
     * T�h�n voisi my�hemmmin laittaa vaikkapa setFocus
     * joka laittaisi kursorin tiettyyn kohtaan
     */
    @Override
    public void handleShown() {
        // ei tarvitse olla mit��n viel�
    }
    
    /**
     * Asetetaan oletusarvo
     * @param oletus mit� n�ytet��n oletuksena
     */
    @Override
    public void setDefault(String oletus) {
        // pit�isik� t�h�n tulla jotain?
    }
    /**
     * palautetaan dialogin "tulos"
     * @return dialogin tulos, t�ss� tapauksessa null
     */
    @Override
    public String getResult() {
        return null;
    }
    
    /**
     * K�sitell��n tunnuksen luominen
     * huom! T�m� toiminto on toiminnaltaan niin monimutkainen,
     * ett� on sallittu j�tt�� pois t�st� harjoitusty�st�
     */
    @FXML void handleLuoTunnus() {
        luoTunnus();
    }
    
    /**
     * K�sitell��n kirjautuminen
     * Huom! Jos olisi mahdollista, tekisin niin, ett�
     * t�m� ikkuna sulkeutuisi siirrytt�ess� p��n�kym��n.
     * T�m�nhetkinen toteutus: sek� aloitusn�kym� ett� p��n�kym�
     * avataan aluksi, kirjautuessa aloitusn�kym� suljetaan
     */
    @FXML void handleKirjaudu() {
        //ModalController.showModal(ResurssitGUIController.class.getResource("ResurssitGUIView.fxml"),
        //        "Ty�kalurekisteri", null, "");
        ModalController.closeStage(textVastaus);
       
    }
    //======================================================================
    
    /**
     * Tunnuksen luominen
     */
    private void luoTunnus() {
    Dialogs.showMessageDialog("T�t� toimintoa ei toteuteta t�ss� harjoitusty�ss�!",
                                dlg -> dlg.getDialogPane().setPrefWidth(400));
    }
   
}
