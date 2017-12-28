package fxResurssit;

import fi.jyu.mit.fxgui.*;
import javafx.application.*;
import javafx.fxml.*;
import ryhma.Jasen;
import ryhma.Ryhma;
import ryhma.Tyokalu;
import ryhma.SailoException;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Luokka päänäkymän käyttöliittymän tapahtumien hoitamiseksi
 * @author miemkoiv
 * @version 11.2.2017
 * @version 7.3.2017
 * @version 21.4.2017
 */
public class ResurssitGUIController implements ModalControllerInterface<String>, Initializable{
    
    @FXML private ScrollPane panelJasen;
    @FXML private StringGrid<Jasen> tableJasenet;
    @FXML private ScrollPane panelTyokalu;
    @FXML private StringGrid<Tyokalu> tableTyokalut;
    @FXML private ComboBoxChooser<String> chooserKentat;
    @FXML private TextField hakuehto;
    
    /**
     * Mitä tehdään kun dialogi tulee näkyviin
     * Tähän voisi myöhemmmin lisätä vaikkapa setFocus
     * joka laittaisi kursorin tiettyyn kohtaan
     */
    @Override
    public void handleShown() {
        ModalController.showModal(ResurssitGUIController.class.getResource("ResurssitGUIView.fxml"),
                "Työkalurekisteri", null, "");
        ModalController.showModal(AloitusnakymaController.class.getResource("Aloitusnakyma.fxml"),
                 "Kirjaudu sisään", null, "");

    }
    
    /**
     * Käsittelee aloitusnäkymän avaamisen
     */
    @FXML private void handleAvaa() {
        avaa();
    }
    
    /**
     * Asetetaan oletusarvo
     * @param oletus mitä näytetään oletuksena
     */
    @Override
    public void setDefault(String oletus) {
        //
    }
    
    /**
     * palautetaan dialogin "tulos"
     * @return dialogin tulos, tässä tapauksessa null
     */
    @Override
    public String getResult() {
        return null;
    }
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        alusta();
    }
	

    /**
     * Tallennuksen käsittely
     */
    @FXML void handleTallenna() {
        tallenna();
    }
    /**
     * Tulostuksen käsittely
     * Mieti, että toteutanko tulostuksen tässä harkkatyössä
     * Jos toteutan, kannattaa tehdä se mahdollisimman pian ja lisätä
     * myös suunnitelmaan itselle malliksi 
     */
    @FXML void handleTulosta() {
        tulosta();
      // tämän näkymän kuva puuttui vaiheesta 2
    }
    /**
     * Lopetus-käskyn käsittely
     */
    @FXML void handleLopeta() {
        tallenna();
        Platform.exit();
    }
    
    /**
     * Hakuehdon määrittämisen käsittely
     * @throws SailoException  jos kannan kanssa ongelmia
     */
    @FXML void handleHakuehto() throws SailoException {
        hae(0);
    }
    
    /**
     * Käsitellään omien tietojen tarkastelu ja muokkaus
     * tällä hetkellä voi muokata ketä tahansa jäsentä (ei vain omia)
     */
    @FXML void handleOmatTiedot() {
        try {
            Jasen jasen;
            // poista allaoleva kommenteista jos haluat, että vain omia tietoja muokataan (oletus: oma jäsenolio on aina ensimmäinen, vrt. ämpäri Astiapelissä)
            // jasen = OmatTiedotController.kysyJasen(null, ryhma.annaJasen(0).clone()); //Testattu, toimii
            if (jasenKohdalla == null) { Dialogs.showMessageDialog("Jäsentä ei valittu!",
                    dlg -> dlg.getDialogPane().setPrefWidth(400)); return; } //jos jäseniä ei ole, ei ole myöskään valittua jäsentä
            jasen = OmatTiedotController.kysyJasen(null, jasenKohdalla.clone(), ryhma);
            if (jasen == null) return; //jos muokkaukset peruutetaan, ei tehdä tietojen korvausta
            try {
                ryhma.korvaaTaiLisaa(jasen);
            } catch (SailoException e) {
                System.err.println("Virhe: " + e.getMessage());
            }
            try {
                hae(jasen.getTunnusNro()); //haetaan lista uudelleen jotta muutokset näkyy
            } catch (SailoException e) {
                System.err.println("Virhe: " +e.getMessage());
            } 
        } catch (CloneNotSupportedException ex) {
            // ei tarvita koska clone()-metodi tehty itse ja tiedetään toimivaksi
        }
    }
    
    /**
     * Käsitellään Ryhmät-näkymän tarkastelu
     * huom! Ryhmät-näkymä on toiminnaltaan niin monimutkainen,
     * että on sallittu jättää pois tästä harjoitustyöstä
     */
    @FXML void handleRyhmat() {
        ryhmat();
    }
    /**
     * Käsitellään aputoiminto
     */
    @FXML void handleApua() {
        apua();
    }
    
    @FXML void handleTiedot() {
        ModalController.showModal(ResurssitGUIController.class.getResource("Tiedot.fxml"),
                "Tietoja ohjelmasta", null, "");
    }
    
    /**
     * Käsitellään uuden jäsenen lisääminen
     * Huom. tämä on testaamista varten eikä jää lopulliseen harjoitustyöhön
     */
    @FXML private void handleUusiJasen() {
        uusiJasen();
    }
    
    /**
     * Käsitellään uuden työkalun lisääminen
     * Huom. tämä on testaamista varten eikä jää lopulliseen harjoitustyöhön
     * @throws SailoException jos kannan kanssa ongelmia
     */
    @FXML private void handleUusiTyokalu() throws SailoException {
        uusiTyokalu();
    }
    
    
    // ==================================================
    private Ryhma ryhma;
    private Jasen jasenKohdalla;
    private static String ryhmanNimi = "fuksit17"; //tässä harkkatyössä käytetään yhtä ryhmää, joka on tämä
    private static Jasen apujasen = new Jasen();
    private static Tyokalu aputyokalu = new Tyokalu(); //apuolio kenttien hakuun
    
    /**
     * Tekee tarvittavat muut alustukset, vaihdetaan Stringgridin tilalle
     * yksi iso tekstikenttä, johon voidaan tulostaa jäsenten tiedot.
     * Alustetaan myös jäsenlistan kuuntelija.
     */
    protected void alusta() {
        panelJasen.setFitToHeight(true);

        alustaJasenet(); //alustaa jäsenien StringGridin
        alustaTyokalut(); //alustaa työkalujen StringGridin kuten Jasenet-gridin yllä
        alustaHakuehdot(); //alustaa hakuehdot

        tableJasenet.clear();
        tableTyokalut.clear();
        tableJasenet.setOnMouseClicked( e -> {
            try {
                naytaJasen();
            } catch (IndexOutOfBoundsException e1) {
                e1.printStackTrace();
            } catch (SailoException e1) {
                e1.printStackTrace();
            }
        }); //klikkauksenkuuntelija tableJasenet-gridille

    }
    
    /**
     * Alustetaan Jäsenet-StringGrid
     */
    protected void alustaJasenet() {
        int eka = apujasen.ekaKentta();
        int lkm = apujasen.getKenttia();
        String[] headings = new String[lkm-eka];
        for (int i = 0, k = eka; k<lkm; i++,k++) headings[i] = apujasen.getKentta(k);
        tableJasenet.initTable(headings);
        
        tableJasenet.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //ylim. sarake pois
        tableJasenet.setEditable(false);
        tableJasenet.setPlaceholder(new Label("Ei jäseniä")); 
    }

    /**
     * Alustetaan työkalut-StringGrid
     */
    protected void alustaTyokalut() {
        //otsikot työkalutaulukkoon:
        int eka = aputyokalu.ekaKentta();
        int lkm = aputyokalu.getKenttia();
        String[] headings = new String[lkm-eka];
        for (int i = 0, k = eka; k<lkm; i++,k++) headings[i] = aputyokalu.getKentta(k);
        tableTyokalut.initTable(headings);
        
        tableTyokalut.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //ylim. sarake pois
        
        tableTyokalut.setPlaceholder(new Label("Jäsenellä ei ole työkaluja"));
        panelTyokalu.setFitToHeight(true);
    }
    
    /**
     * Alustetaan hakuehdoiksi jäsenen tietokenttien nimet (lukuunottamatta
     * puhelinta ja sähköpostia)
     */
    protected void alustaHakuehdot() {
        chooserKentat.clear();
        for (int k=apujasen.ekaKentta();k<4;k++ ) //kenttä 4 = puhelin; jos halutaan kaikki -> k<apujasen.getKenttia()
            chooserKentat.add(apujasen.getKentta(k),apujasen.getKentta(k));
        chooserKentat.add("Työkalu","Työkalu");
    }

    /**
     * Näyttää jäsentaulukosta valitun jäsenen tiedot (työkalut)
     * @throws SailoException jos kannan kanssa ongelmia
     * @throws IndexOutOfBoundsException jos mahdoton indeksi
     */
    protected void naytaJasen() throws IndexOutOfBoundsException, SailoException {
        jasenKohdalla = tableJasenet.getObject();
        tableJasenet.setOnCellString( (g, jasen, oletusArvo, r, c ) -> jasen.anna(c+apujasen.ekaKentta())); //asetetaan sarakkeisiin jäsenten tiedot (ensin c+eka=1, jne)
        tableJasenet.setOnCellValue( (g, jasen, defValue, r, c ) -> jasen.getAvain(c+apujasen.ekaKentta()) ); 
        if (jasenKohdalla == null) return;
        naytaTyokalut(jasenKohdalla);
        //tableJasenet.selectRow();
    }


    /**
     * Näyttää jäsenen työkalut
     * (periaatteena merkkijonon pilkkominen)
     * @param valittuJasen jäsen jonka työkalut näytetään
     * @throws SailoException jos kannan kanssa ongelmia
     * @throws IndexOutOfBoundsException jos mahdoton indeksi
     */
    protected void naytaTyokalut(Jasen valittuJasen) throws IndexOutOfBoundsException, SailoException {
        tableTyokalut.clear(); //poistetaan mahd. olemassaolevat tiedot
        
        if (valittuJasen == null) return;
        List<Tyokalu> tyokalut = ryhma.annaTyokalut(valittuJasen);
        if (tyokalut.size() == 0) return; //jos ei työkaluja, näytetään placeholder

        for (Tyokalu tyok : tyokalut) {
            naytaTyokalu(tyok);
        }
    }
    
    /*
     * Muokataan työkalun tietoja
     * HUOM. tämä tulee siirtymään OmatTiedotControlleriin
     *
    protected void muokkaaTyokalua() {
        tableTyokalut.setEditable(true); //vaihda jos muokkausmahdollisuus muuttuu
        int eka = aputyokalu.ekaKentta();
        tableTyokalut.setOnGridLiveEdit((g, tyok, teksti, r, c, edit) -> {
            String virhe;
            //nimi haetaan tunnuksen avulla, joten se käsitellään erikseen (nimi on sarakkeessa 0):
            if (c==0) {     
                tableTyokalut.setOnMouseExited(e -> ryhma.setTkNimi(tyok,teksti)); 
                virhe = null;
            }
            //yleisten rivien käsittely:
            else virhe = tyok.aseta(c+eka,teksti);
            if ( virhe == null ) {
                ryhma.korvaaTaiLisaa(tyok); // jotta saadaan muutos
                edit.setStyle(null);
            } else { //tällä hetkellä tämä ei ole mahdollista, sillä oikeellisuustarkistuksia ei ole
                edit.setStyle("-fx-background-color: red");
                //ilmoittaa virheestä vain värimuutoksella, virheviestiä ei näytetä
            }
            return teksti;
        });
    }*/
    
    /**
     * Hakee listaan tietyn jäsenen työkalun
     * @param tyok näytettävä työkalu
     * @throws SailoException jos kannan kanssa ongelmia
     * @throws IndexOutOfBoundsException 
     */
    private void naytaTyokalu(Tyokalu tyok) throws IndexOutOfBoundsException, SailoException {
        String[] rivit = new String[tyok.getKenttia()-tyok.ekaKentta()];
        /*for (int i=0, k=tyok.ekaKentta(); k<tyok.getKenttia(); i++, k++)
            rivit[i] = tyok.anna(k);*/
        int tkNro = tyok.getTyokaluNro(); //tyokaluNro
        rivit[0] = ryhma.annaTkNimiTeksti(tkNro); //asetetaan työkalun nimi ensimmäiseen sarakkeeseen
        for (int i= 1, k=tyok.ekaKentta()+1; k<tyok.getKenttia(); k++, i++)
            rivit[i] = tyok.anna(k);
        tableTyokalut.add(tyok,rivit);    
    }
    
    /**
     * Tallennus
     */
    private void tallenna() {
       try {
           ryhma.tallenna();
       } catch (SailoException ex) {
           Dialogs.showMessageDialog("Ongelma tallennettaessa: " + ex.getMessage(),
                   dlg -> dlg.getDialogPane().setPrefWidth(400));
       }
    }
    
    /**
     * Tulostus
     */
    private void tulosta() {
        TulostusController.tulosta(jasenKohdalla); 
    }
    
    /**
     * Ryhmät-ikkunan sijasta näytettävä dialogi
     */
    private void ryhmat() {
        Dialogs.showMessageDialog("Tätä toimintoa ei toteuteta tässä harjoitustyössä",
                                    dlg -> dlg.getDialogPane().setPrefWidth(400));
    }
    
    /**
     * Luo uuden jäsenen Aku Ankka-tiedoilla
     * Huom! Tätä ominaisuutta ei ole harjoitustyön
     * suunnitelmassa, sillä ohjelmaa ei ole tarkoitettu käytettäväksi tähän.
     * Tämä ominaisuus on luotu jäsenen luomisen testaamiseksi.
     */
    private void uusiJasen() { 
          try { 
            Jasen jasen = new Jasen();
            jasen.taytaAkuAnkka();
            jasen.rekisteroi(); //pitää rekisteröidä etukäteen jotta työkalun lisääminen onnistuu
            int tunnus = jasen.getTunnusNro();
            jasen = OmatTiedotController.kysyJasen(null, jasen.clone(),ryhma); //Avataan muokkausdialogi
            if (jasen == null) {
                List<Tyokalu> tyok = ryhma.annaTyokalut(tunnus);
                if (tyok.size()<1) return; //jos muokkaukset peruutetaan ja jäsenellä ei työkaluja, ei korvata tietoja
                for (int i=0; i<tyok.size();i++) { 
                    ryhma.poistaTyokalu(tyok.get(i)); //poistetaan mahd. jäsenelle lisätyt työkalut
                }
                return; //jos muokkaukset peruutetaan, ei tehdä tietojen korvausta       
            }
            ryhma.lisaa(jasen); 
            try {
                hae(jasen.getTunnusNro()); //haetaan lista uudelleen jotta muutokset näkyy
            } catch (SailoException e) {
                e.printStackTrace();
            } 
        } catch (CloneNotSupportedException ex) {
            // ei tarvita koska clone()-metodi tehty itse ja tiedetään toimivaksi
        }
    }

    
    /**
     * Haetaan jäsenten tiedot listaan
     * @param jasenNro valitun jäsenen numero; uutta lisättäessä uuden jäsenen nro
     * @throws SailoException jos kannan kanssa ongelmia
     */
    protected void hae(int jasenNro) throws SailoException {
        tableJasenet.clear(); //tyhjennetään grid
        
        int index = 0;
        int k = chooserKentat.getSelectedIndex() + apujasen.ekaKentta(); //nimi on ensimmäinen järkevä hakuehto
        Collection<Jasen> jasenet;
        String ehto = hakuehto.getText();
        if (ehto.indexOf('*')<0) ehto = "%" + ehto + "%"; //jos käyttäjä ei laita asteriskeja (indexOf palauttaa -1), laitetaan ne itse ehtoon
        jasenet = ryhma.etsi(ehto,k);
        int i = 0;
        for (Jasen jasen : jasenet) {    
            if (jasen.getTunnusNro() == jasenNro) index = i;
            tableJasenet.add(jasen);
            i++;
        }
        tableJasenet.selectRow(index); // valitsee uuden jäsenen
        naytaJasen();   //näyttää valitun jäsenen tiedot
    }
    
    /**
     * Luo uuden työkalun valitulle jäsenelle
     * Huom! Tätä ominaisuutta ei ole harjoitustyön
     * suunnitelmassa, sillä ohjelmaa ei ole tarkoitettu käytettäväksi tähän.
     * Tätä ei siis välttämättä sisällytetä lopulliseen harjoitustyöhön, tätä käytetään
     * vaiheessa 5 ja 6 testaamiseen
     * @throws SailoException jos kannan kanssa ongelmia
     */
    protected void uusiTyokalu() throws SailoException {
        if (jasenKohdalla == null) return;
        int valittuJasenNro = jasenKohdalla.getTunnusNro();
        ryhma.uusiTyokalu(valittuJasenNro);
        hae(valittuJasenNro);
        //Poikkeusta ei tarvitse Arraylistin kohdalla ottaa kiinni
        
    }
    
    /**
     * Ohjelman avustus, näytetään selaimessa (Harkan TIM-sivu)
     */
    private void apua() {
        Desktop desktop = Desktop.getDesktop();
        try {
            URI uri = new URI("https://tim.jyu.fi/view/kurssit/tie/ohj2/2017k/ht/miemkoiv");
            desktop.browse(uri);
        } 
        catch (URISyntaxException e) {
            return;
        } 
        catch (IOException e) {
            return;
        } 
    }
   
    /**
     * Avaa aloitusnäkymän kun ohjelma käynnistetään
     */
    public void avaa() {
        lueTiedosto(ryhmanNimi);    //avatessa luetaan jo tiedosto
        ModalController.showModal(AloitusnakymaController.class.getResource("Aloitusnakyma.fxml"),
             "Kirjaudu sisään", null, "");
            
       
    }
    /**
     * Alustaa jäsenen luettavissa olevat tiedot lukemalla valitun nimisestä tiedosto(i)sta
     * samoihin ryhmiin kuuluvien jäsenien tiedot
     * (HUOM. tässä ht:ssa vain yksi ryhmä)
     * tämä kai voisi olla void? Ei kai noita nulleja ja virheitä tarvitse palauttaa?
     * @param nimi ryhmän nimi (=ryhmän hakemiston nimi)
     * @return null jos onnistuu, virhe tekstinä jos epäonnistuu 
     */
    protected String lueTiedosto(String nimi) {
        ryhmanNimi = nimi;
        try {
            ryhma.lueTiedostosta(nimi); //luetaan tämännimisen ryhmän tiedot
            hae(0); //haetaan jäsenten tiedot listaan, ensimmäinen jäsen aktiiviseksi
            return null;
        } catch (SailoException e) {
            String virhe = e.getMessage();
            if (virhe != null) Dialogs.showMessageDialog("Tapahtui virhe: " + virhe);
            return virhe;
        }     
    }

    /**
     * @param ryhma ryhmä jota käsitellään
     * @throws SailoException jos kannan kanssa ongelmia
     * @throws IndexOutOfBoundsException jos mahdoton indeksi
     */
    public void setRyhma(Ryhma ryhma) throws IndexOutOfBoundsException, SailoException {
        this.ryhma = ryhma;
        naytaJasen();                          
    }

}
