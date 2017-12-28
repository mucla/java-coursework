package fxResurssit;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.*;
import fi.jyu.mit.ohj2.Mjonot;
import javafx.fxml.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ryhma.Jasen;
import ryhma.Tyokalu;
import ryhma.Ryhma;
import ryhma.SailoException;

/**
 * Luokka Omat tiedot -ikkunan k�ytt�liittym�n tapahtumien hoitamiseksi
 * @author miemkoiv
 * @version 11.2.2017
 * Mahdollistettu muokkaus j�senen tietoihin
 * @version 11.4.2017
 * Kenttien haku indeksoidusti
 * @version 13.4.2017
 *
 */
public class OmatTiedotController implements ModalControllerInterface<Jasen>, Initializable {
   
    @FXML private GridPane gridJasen;
    @FXML private ScrollPane panelJasen;
    @FXML private StringGrid<Tyokalu> tableTyokalut;
    @FXML private ScrollPane panelTyokalu;
    @FXML private TextField editNimi;
    @FXML private TextField editKatuOsoite;
    @FXML private TextField editPostiOsoite;
    @FXML private TextField editPuhelin;
    @FXML private TextField editSapo;
    @FXML private Label labelVirhe;
    
    /**
     * Mit� tehd��n kun dialogi tulee n�kyviin
     * T�h�n voisi my�hemmmin laittaa vaikkapa setFocus
     * joka laittaisi kursorin tiettyyn kohtaan
     */
    @Override
    public void handleShown() {
        // ei tarvitse olla mit��n, oletuksena n�ytet��n OmatTiedot.fxml
    }
    
    /**
     * Asetetaan oletusarvo
     * @param oletus mit� n�ytet��n oletuksena
     */
    @Override
    public void setDefault(Jasen oletus) {
        jasenKohdalla = oletus;
        naytaJasen(jasenKohdalla);
        try {
            naytaTyokalut(jasenKohdalla);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (SailoException e) {
            e.printStackTrace();
        }
    }

    /**
     * Palautetaan dialogin "tulos"
     * @return dialogin tulos, t�ss� tapauksessa kohdalla oleva j�sen
     */
    @Override
    public Jasen getResult() {
        return jasenKohdalla;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle arg1) {
        alusta();
    }
    
    /**
     * Tallennuksen k�sittely
     */
    @FXML void handleTallenna() {
        String virhe = labelVirhe.getText();
        if (!"".equals(virhe) && virhe != null ) return; //jos virhesy�tt�, ei anneta tallentaa
        ModalController.closeStage(labelVirhe);
    }
    
    /**
     * Peruutuksen k�sittely
     */
    @FXML void handlePeruuta() {      
        jasenKohdalla = null;
        ModalController.closeStage(labelVirhe);
    }
    
    /**
     * Ty�kalun lis��misen k�sittely
     * @throws SailoException jos kannan kanssa ongelmia
     * @throws IndexOutOfBoundsException jos mahdoton indeksi
     */
    @FXML
    void handleLisaa() throws IndexOutOfBoundsException, SailoException {
        lisaa();
    }
    
    /**
     * Ty�kalun poistamisen k�sittely
     * @throws SailoException jos kannan kanssa ongelmia
     * @throws IndexOutOfBoundsException jos mahdoton indeksi
     */
    @FXML
    void handlePoista() throws IndexOutOfBoundsException, SailoException {
        poista();
    }
    
 // ==================================================
    private Jasen jasenKohdalla;
    private static Ryhma oletusRyhma;
    private TextField edits[];
    private static Jasen apujasen = new Jasen(); //apuj�sen kenttien lkm:n kysymiseksi luoKentat-metodissa
    private static Tyokalu aputyokalu = new Tyokalu(); //apuolio kenttien hakuun 
    
    /**
     * Alustaa tiedot valitun j�senen tiedoilla
     * j�senen tietokent�t haetaan tiedoista, mutta ty�kalut n�ytet��n
     * suoraan Gridiin (=ty�kaluille ei t�ll� hetkell� voi lis�t� tietokentti�)
     */
    protected void alusta() {
        edits = luoKentat(gridJasen);
        //naytaJasen(edits, jasenKohdalla);
        for (TextField edit : edits) {
            if (edit != null)
                edit.setOnKeyReleased (e -> kasitteleMuutosJaseneen((TextField)(e.getSource()))); //k�sitell��n muutos kyseiseen kentt��n
        }
        panelJasen.setFitToHeight(true); //asetetaan koko oikeaksi jos kenttien m��r� eroaa alkup. fxml:st�
    }
    
    /**
     * Luo Omat Tiedot-ikkunan GridPaneen j�senen tiedot
     * @param gridJasen j�senen tiedoista koostuva gridpane
     * @return tekstikentt�taulukko j�senen tiedoista
     */
    public static TextField[] luoKentat(GridPane gridJasen) {
        gridJasen.getChildren().clear(); //pyyhit��n ensin tyhj�ksi
        TextField[] edits = new TextField[apujasen.getKenttia()];
        int k = apujasen.ekaKentta();
        for (int i=0; k<apujasen.getKenttia();i++, k++) {
            Label label = new Label(apujasen.getKentta(k));
            gridJasen.add(label, 0, i); //luodaan label ensimm�iseen sarakkeeseen indeksiin i
            TextField edit = new TextField();
            edits[k] = edit;
            edit.setId("e"+k); //annetaan omat tunnukset, muotoa e1,e2,jne...
            gridJasen.add(edit, 1, i);//laitetaan tekstikentt� toiseen sarakkeeseen indeksiin i
        }
        return edits;
    }
    
    /**
     * N�ytt�� j�senen ty�kalut
     * Huom. ei toimi (LoadException)
     * @param valittuJasen j�sen jonka ty�kalut n�ytet��n
     * @throws SailoException jos ongelmia
     * @throws IndexOutOfBoundsException jos indeksi ei taulukon sis�ss�
     */
    protected void naytaTyokalut(Jasen valittuJasen) throws IndexOutOfBoundsException, SailoException {
        //otsikot ty�kalutaulukkoon:
        int eka = aputyokalu.ekaKentta();
        int lkm = aputyokalu.getKenttia();
        String[] headings = new String[lkm-eka];
        for (int i = 0, k = eka; k<lkm; i++,k++) headings[i] = aputyokalu.getKentta(k);
        tableTyokalut.initTable(headings);
        
        tableTyokalut.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //ylim. sarake pois
        tableTyokalut.setEditable(true);
        tableTyokalut.setPlaceholder(new Label("J�senell� ei ole ty�kaluja"));
        panelTyokalu.setFitToHeight(true);
        
        tableTyokalut.clear(); //poistetaan mahd. olemassaolevat tiedot
        if (valittuJasen == null) return;
        
        List<Tyokalu> tyokalut = oletusRyhma.annaTyokalut(valittuJasen);
        if (tyokalut.size() == 0) return; //jos ei ty�kaluja, n�ytet��n placeholder
        for (Tyokalu tyok : tyokalut) {
            naytaTyokalu(tyok);
        }
        muokkaaTyokalua();
    }
    
    
    private void naytaTyokalu(Tyokalu tyok) throws IndexOutOfBoundsException, SailoException {
        String[] rivit = new String[tyok.getKenttia()-tyok.ekaKentta()];
        int tkNro = tyok.getTyokaluNro(); //tyokaluNro
        rivit[0] = oletusRyhma.annaTkNimiTeksti(tkNro); //asetetaan ty�kalun nimi ensimm�iseen sarakkeeseen
        for (int i=1, k=tyok.ekaKentta()+1; k<tyok.getKenttia(); i++, k++)
            rivit[i] = tyok.anna(k);
        tableTyokalut.add(tyok,rivit);    
    }
    
    /**
     * Muokataan ty�kalun tietoja
     */
    protected void muokkaaTyokalua() {
        tableTyokalut.setEditable(true); //vaihda jos muokkausmahdollisuus muuttuu
        int eka = aputyokalu.ekaKentta();
        tableTyokalut.setOnGridLiveEdit((g, tyok, teksti, r, c, edit) -> {  
            //muutokset tehd��n vasta kun poistutaan kent�st�:
            tableTyokalut.setOnMouseExited(e -> {
              //nimi tunnuksen avulla, joten se k�sitell��n erikseen:
                if (c==0) { //nimi on sarakkeessa 0
                    try {
                        oletusRyhma.setTkNimi(tyok,teksti);
                    } catch (SailoException e1) {
                        System.err.println("Virhe: " +e1.getMessage());
                    }

                } 
                //yleisten rivien k�sittely:
                else tyok.aseta(c+eka,teksti);
                //korvataan ty�kalu
                try {
                    oletusRyhma.korvaaTaiLisaa(tyok); // jotta saadaan muutos
                } catch (SailoException e2) {
                    System.err.println("Virhe: " +e2.getMessage());
                } 
                edit.setStyle(null);

            });
            return teksti;
        });
        /*tableTyokalut.setOnGridLiveEdit((g, tyok, teksti, r, c, edit) -> {
            String virhe;
            //nimi haetaan tunnuksen avulla, joten se k�sitell��n erikseen (nimi on sarakkeessa 0):
            if (c==0) {     
                tableTyokalut.setOnMouseExited(e -> {
                    try {
                        oletusRyhma.setTkNimi(tyok,teksti);
                    } catch (SailoException e1) {
                        System.err.println("Virhe: " +e1.getMessage());
                    }
                }); 
                virhe = null;
            }
            //yleisten rivien k�sittely:
            else virhe = tyok.aseta(c+eka,teksti);
            if ( virhe == null ) {
                try {
                    oletusRyhma.korvaaTaiLisaa(tyok); // jotta saadaan muutos
                } catch (SailoException e) {
                    System.err.println("Virhe: " +e.getMessage());
                } 
                edit.setStyle(null);
            } else { //t�ll� hetkell� t�m� ei ole mahdollista, sill� oikeellisuustarkistuksia ei ole
                edit.setStyle("-fx-background-color: red");
                //ilmoittaa virheest� vain v�rimuutoksella, virheviesti� ei n�ytet�
            }
            return teksti;
        });*/
    }
    
    /**
     * K�sittelee j�senen tietoihin tehdyn muutoksen
     * @param edit muutettava kentt�
     */
    protected void kasitteleMuutosJaseneen(TextField edit) {
        if (jasenKohdalla == null) return;
        int k = getKenttaId(edit,apujasen.ekaKentta());
        String s = edit.getText();
        String virhe = null;
        virhe = jasenKohdalla.aseta(k,s);
        if (virhe == null) {
            Dialogs.setToolTipText(edit, "");
            edit.getStyleClass().removeAll("virhe");
            naytaVirhe(virhe);
        } else {
            Dialogs.setToolTipText(edit, virhe);
            edit.getStyleClass().add("virhe");
            naytaVirhe(virhe);
        }
        
    }

    /**
     * Palauttaa k�sitelt�v�n tekstikent�n id-numeron
     * (voisi tehd� my�s pienin muutoksin yleiselle oliolle)
     * @param edit k�sitelt�v� tekstikentt�
     * @param oletusKentta oletuskentt�-id (t�ll� hetkell� 1)
     * @return kent�n id-numero; oletus mik�li ei saada kunnollista arvoa
     */
    public static int getKenttaId(TextField edit, int oletusKentta) {
        String s = edit.getId(); //haetaan kent�n id, muotoa "e1"
        if (s.length() < 1) return oletusKentta;
        return Mjonot.erotaInt(s.substring(1), oletusKentta); //palautetaan id:st� vain numero 
    }

    private void naytaVirhe(String virhe) {
        labelVirhe.setText(virhe);
    }
  
    
    private void naytaJasen(Jasen jasen) {
        if (jasen == null) return;
        naytaJasen(edits,jasen);
    }
    
    /**
     * N�ytt�� valitun j�senen tiedot muokkausdialogissa
     * @param edits j�senen tietokent�t taulukkona
     * @param jasen j�sen jonka tiedot n�ytet��n
     */
    public static void naytaJasen(TextField[] edits, Jasen jasen) {
        if (jasen == null) return;
        for (int k = jasen.ekaKentta(); k<jasen.getKenttia();k++) {
            edits[k].setText(jasen.anna(k));
        } 
    }
    
    /**
     * Lis�� ty�kalun
     * @throws SailoException jos tietokannan kanssa ongelmia
     * @throws IndexOutOfBoundsException 
     */
    private void lisaa() throws IndexOutOfBoundsException, SailoException {
        oletusRyhma.uusiTyokalu(jasenKohdalla.getTunnusNro());
        naytaTyokalut(jasenKohdalla);
    }
    
    /**
     * Poistaa valitun ty�kalun
     * @throws SailoException jos tietokannan kanssa ongelmia
     * @throws IndexOutOfBoundsException 
     */
    private void poista() throws IndexOutOfBoundsException, SailoException {
        Tyokalu tyok = tableTyokalut.getObject();
        if (tyok == null) return;
        oletusRyhma.poistaTyokalu(tyok);
        naytaTyokalut(jasenKohdalla);
    }

    /**
     * Avataan valitun j�senen tietojen muokkausdialogi
     * (huom. jos haluaa tuoda esim. ryhm�n t�h�n, tuo se t�h�n parametrina)
     * @param modalityStage Stage jolle ollaan modaalisia (null = koko sovellus)
     * @param valittuJasen j�sen, jonka tietoja tarkastellaan/muokataan
     * @param ryhma ryhm� jonka j�senen tietoja muokataan
     * @return null jos ei paineta "Tallenna", muuten muutetaan j�senen tiedot
     */
    public static Jasen kysyJasen(Stage modalityStage, Jasen valittuJasen, Ryhma ryhma) {
        oletusRyhma = ryhma;
        return ModalController.<Jasen, OmatTiedotController>showModal(OmatTiedotController.class.getResource("OmatTiedot.fxml"),
                "Omat Tiedot", modalityStage, valittuJasen, null);
        
    }

}
