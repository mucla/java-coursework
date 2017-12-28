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
 * Luokka Omat tiedot -ikkunan käyttöliittymän tapahtumien hoitamiseksi
 * @author miemkoiv
 * @version 11.2.2017
 * Mahdollistettu muokkaus jäsenen tietoihin
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
     * Mitä tehdään kun dialogi tulee näkyviin
     * Tähän voisi myöhemmmin laittaa vaikkapa setFocus
     * joka laittaisi kursorin tiettyyn kohtaan
     */
    @Override
    public void handleShown() {
        // ei tarvitse olla mitään, oletuksena näytetään OmatTiedot.fxml
    }
    
    /**
     * Asetetaan oletusarvo
     * @param oletus mitä näytetään oletuksena
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
     * @return dialogin tulos, tässä tapauksessa kohdalla oleva jäsen
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
     * Tallennuksen käsittely
     */
    @FXML void handleTallenna() {
        String virhe = labelVirhe.getText();
        if (!"".equals(virhe) && virhe != null ) return; //jos virhesyöttö, ei anneta tallentaa
        ModalController.closeStage(labelVirhe);
    }
    
    /**
     * Peruutuksen käsittely
     */
    @FXML void handlePeruuta() {      
        jasenKohdalla = null;
        ModalController.closeStage(labelVirhe);
    }
    
    /**
     * Työkalun lisäämisen käsittely
     * @throws SailoException jos kannan kanssa ongelmia
     * @throws IndexOutOfBoundsException jos mahdoton indeksi
     */
    @FXML
    void handleLisaa() throws IndexOutOfBoundsException, SailoException {
        lisaa();
    }
    
    /**
     * Työkalun poistamisen käsittely
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
    private static Jasen apujasen = new Jasen(); //apujäsen kenttien lkm:n kysymiseksi luoKentat-metodissa
    private static Tyokalu aputyokalu = new Tyokalu(); //apuolio kenttien hakuun 
    
    /**
     * Alustaa tiedot valitun jäsenen tiedoilla
     * jäsenen tietokentät haetaan tiedoista, mutta työkalut näytetään
     * suoraan Gridiin (=työkaluille ei tällä hetkellä voi lisätä tietokenttiä)
     */
    protected void alusta() {
        edits = luoKentat(gridJasen);
        //naytaJasen(edits, jasenKohdalla);
        for (TextField edit : edits) {
            if (edit != null)
                edit.setOnKeyReleased (e -> kasitteleMuutosJaseneen((TextField)(e.getSource()))); //käsitellään muutos kyseiseen kenttään
        }
        panelJasen.setFitToHeight(true); //asetetaan koko oikeaksi jos kenttien määrä eroaa alkup. fxml:stä
    }
    
    /**
     * Luo Omat Tiedot-ikkunan GridPaneen jäsenen tiedot
     * @param gridJasen jäsenen tiedoista koostuva gridpane
     * @return tekstikenttätaulukko jäsenen tiedoista
     */
    public static TextField[] luoKentat(GridPane gridJasen) {
        gridJasen.getChildren().clear(); //pyyhitään ensin tyhjäksi
        TextField[] edits = new TextField[apujasen.getKenttia()];
        int k = apujasen.ekaKentta();
        for (int i=0; k<apujasen.getKenttia();i++, k++) {
            Label label = new Label(apujasen.getKentta(k));
            gridJasen.add(label, 0, i); //luodaan label ensimmäiseen sarakkeeseen indeksiin i
            TextField edit = new TextField();
            edits[k] = edit;
            edit.setId("e"+k); //annetaan omat tunnukset, muotoa e1,e2,jne...
            gridJasen.add(edit, 1, i);//laitetaan tekstikenttä toiseen sarakkeeseen indeksiin i
        }
        return edits;
    }
    
    /**
     * Näyttää jäsenen työkalut
     * Huom. ei toimi (LoadException)
     * @param valittuJasen jäsen jonka työkalut näytetään
     * @throws SailoException jos ongelmia
     * @throws IndexOutOfBoundsException jos indeksi ei taulukon sisässä
     */
    protected void naytaTyokalut(Jasen valittuJasen) throws IndexOutOfBoundsException, SailoException {
        //otsikot työkalutaulukkoon:
        int eka = aputyokalu.ekaKentta();
        int lkm = aputyokalu.getKenttia();
        String[] headings = new String[lkm-eka];
        for (int i = 0, k = eka; k<lkm; i++,k++) headings[i] = aputyokalu.getKentta(k);
        tableTyokalut.initTable(headings);
        
        tableTyokalut.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //ylim. sarake pois
        tableTyokalut.setEditable(true);
        tableTyokalut.setPlaceholder(new Label("Jäsenellä ei ole työkaluja"));
        panelTyokalu.setFitToHeight(true);
        
        tableTyokalut.clear(); //poistetaan mahd. olemassaolevat tiedot
        if (valittuJasen == null) return;
        
        List<Tyokalu> tyokalut = oletusRyhma.annaTyokalut(valittuJasen);
        if (tyokalut.size() == 0) return; //jos ei työkaluja, näytetään placeholder
        for (Tyokalu tyok : tyokalut) {
            naytaTyokalu(tyok);
        }
        muokkaaTyokalua();
    }
    
    
    private void naytaTyokalu(Tyokalu tyok) throws IndexOutOfBoundsException, SailoException {
        String[] rivit = new String[tyok.getKenttia()-tyok.ekaKentta()];
        int tkNro = tyok.getTyokaluNro(); //tyokaluNro
        rivit[0] = oletusRyhma.annaTkNimiTeksti(tkNro); //asetetaan työkalun nimi ensimmäiseen sarakkeeseen
        for (int i=1, k=tyok.ekaKentta()+1; k<tyok.getKenttia(); i++, k++)
            rivit[i] = tyok.anna(k);
        tableTyokalut.add(tyok,rivit);    
    }
    
    /**
     * Muokataan työkalun tietoja
     */
    protected void muokkaaTyokalua() {
        tableTyokalut.setEditable(true); //vaihda jos muokkausmahdollisuus muuttuu
        int eka = aputyokalu.ekaKentta();
        tableTyokalut.setOnGridLiveEdit((g, tyok, teksti, r, c, edit) -> {  
            //muutokset tehdään vasta kun poistutaan kentästä:
            tableTyokalut.setOnMouseExited(e -> {
              //nimi tunnuksen avulla, joten se käsitellään erikseen:
                if (c==0) { //nimi on sarakkeessa 0
                    try {
                        oletusRyhma.setTkNimi(tyok,teksti);
                    } catch (SailoException e1) {
                        System.err.println("Virhe: " +e1.getMessage());
                    }

                } 
                //yleisten rivien käsittely:
                else tyok.aseta(c+eka,teksti);
                //korvataan työkalu
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
            //nimi haetaan tunnuksen avulla, joten se käsitellään erikseen (nimi on sarakkeessa 0):
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
            //yleisten rivien käsittely:
            else virhe = tyok.aseta(c+eka,teksti);
            if ( virhe == null ) {
                try {
                    oletusRyhma.korvaaTaiLisaa(tyok); // jotta saadaan muutos
                } catch (SailoException e) {
                    System.err.println("Virhe: " +e.getMessage());
                } 
                edit.setStyle(null);
            } else { //tällä hetkellä tämä ei ole mahdollista, sillä oikeellisuustarkistuksia ei ole
                edit.setStyle("-fx-background-color: red");
                //ilmoittaa virheestä vain värimuutoksella, virheviestiä ei näytetä
            }
            return teksti;
        });*/
    }
    
    /**
     * Käsittelee jäsenen tietoihin tehdyn muutoksen
     * @param edit muutettava kenttä
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
     * Palauttaa käsiteltävän tekstikentän id-numeron
     * (voisi tehdä myös pienin muutoksin yleiselle oliolle)
     * @param edit käsiteltävä tekstikenttä
     * @param oletusKentta oletuskenttä-id (tällä hetkellä 1)
     * @return kentän id-numero; oletus mikäli ei saada kunnollista arvoa
     */
    public static int getKenttaId(TextField edit, int oletusKentta) {
        String s = edit.getId(); //haetaan kentän id, muotoa "e1"
        if (s.length() < 1) return oletusKentta;
        return Mjonot.erotaInt(s.substring(1), oletusKentta); //palautetaan id:stä vain numero 
    }

    private void naytaVirhe(String virhe) {
        labelVirhe.setText(virhe);
    }
  
    
    private void naytaJasen(Jasen jasen) {
        if (jasen == null) return;
        naytaJasen(edits,jasen);
    }
    
    /**
     * Näyttää valitun jäsenen tiedot muokkausdialogissa
     * @param edits jäsenen tietokentät taulukkona
     * @param jasen jäsen jonka tiedot näytetään
     */
    public static void naytaJasen(TextField[] edits, Jasen jasen) {
        if (jasen == null) return;
        for (int k = jasen.ekaKentta(); k<jasen.getKenttia();k++) {
            edits[k].setText(jasen.anna(k));
        } 
    }
    
    /**
     * Lisää työkalun
     * @throws SailoException jos tietokannan kanssa ongelmia
     * @throws IndexOutOfBoundsException 
     */
    private void lisaa() throws IndexOutOfBoundsException, SailoException {
        oletusRyhma.uusiTyokalu(jasenKohdalla.getTunnusNro());
        naytaTyokalut(jasenKohdalla);
    }
    
    /**
     * Poistaa valitun työkalun
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
     * Avataan valitun jäsenen tietojen muokkausdialogi
     * (huom. jos haluaa tuoda esim. ryhmän tähän, tuo se tähän parametrina)
     * @param modalityStage Stage jolle ollaan modaalisia (null = koko sovellus)
     * @param valittuJasen jäsen, jonka tietoja tarkastellaan/muokataan
     * @param ryhma ryhmä jonka jäsenen tietoja muokataan
     * @return null jos ei paineta "Tallenna", muuten muutetaan jäsenen tiedot
     */
    public static Jasen kysyJasen(Stage modalityStage, Jasen valittuJasen, Ryhma ryhma) {
        oletusRyhma = ryhma;
        return ModalController.<Jasen, OmatTiedotController>showModal(OmatTiedotController.class.getResource("OmatTiedot.fxml"),
                "Omat Tiedot", modalityStage, valittuJasen, null);
        
    }

}
