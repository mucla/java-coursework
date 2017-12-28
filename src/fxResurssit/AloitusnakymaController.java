package fxResurssit;

import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Luokka aloitusnäkymän käyttöliittymän tapahtumien hoitamiseksi
 * @author miemkoiv
 * @version 11.2.2017
 *
 */
public class AloitusnakymaController implements ModalControllerInterface<String>{
   
    @FXML private TextField textVastaus;
    
    /**
     * Mitä tehdään kun dialogi tulee näkyviin
     * Tähän voisi myöhemmmin laittaa vaikkapa setFocus
     * joka laittaisi kursorin tiettyyn kohtaan
     */
    @Override
    public void handleShown() {
        // ei tarvitse olla mitään vielä
    }
    
    /**
     * Asetetaan oletusarvo
     * @param oletus mitä näytetään oletuksena
     */
    @Override
    public void setDefault(String oletus) {
        // pitäisikö tähän tulla jotain?
    }
    /**
     * palautetaan dialogin "tulos"
     * @return dialogin tulos, tässä tapauksessa null
     */
    @Override
    public String getResult() {
        return null;
    }
    
    /**
     * Käsitellään tunnuksen luominen
     * huom! Tämä toiminto on toiminnaltaan niin monimutkainen,
     * että on sallittu jättää pois tästä harjoitustyöstä
     */
    @FXML void handleLuoTunnus() {
        luoTunnus();
    }
    
    /**
     * Käsitellään kirjautuminen
     * Huom! Jos olisi mahdollista, tekisin niin, että
     * tämä ikkuna sulkeutuisi siirryttäessä päänäkymään.
     * Tämänhetkinen toteutus: sekä aloitusnäkymä että päänäkymä
     * avataan aluksi, kirjautuessa aloitusnäkymä suljetaan
     */
    @FXML void handleKirjaudu() {
        //ModalController.showModal(ResurssitGUIController.class.getResource("ResurssitGUIView.fxml"),
        //        "Työkalurekisteri", null, "");
        ModalController.closeStage(textVastaus);
       
    }
    //======================================================================
    
    /**
     * Tunnuksen luominen
     */
    private void luoTunnus() {
    Dialogs.showMessageDialog("Tätä toimintoa ei toteuteta tässä harjoitustyössä!",
                                dlg -> dlg.getDialogPane().setPrefWidth(400));
    }
   
}
