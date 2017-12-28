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
 * Luokka p��n�kym�n k�ytt�liittym�n tapahtumien hoitamiseksi
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
     * Mit� tehd��n kun dialogi tulee n�kyviin
     * T�h�n voisi my�hemmmin lis�t� vaikkapa setFocus
     * joka laittaisi kursorin tiettyyn kohtaan
     */
    @Override
    public void handleShown() {
        ModalController.showModal(ResurssitGUIController.class.getResource("ResurssitGUIView.fxml"),
                "Ty�kalurekisteri", null, "");
        ModalController.showModal(AloitusnakymaController.class.getResource("Aloitusnakyma.fxml"),
                 "Kirjaudu sis��n", null, "");

    }
    
    /**
     * K�sittelee aloitusn�kym�n avaamisen
     */
    @FXML private void handleAvaa() {
        avaa();
    }
    
    /**
     * Asetetaan oletusarvo
     * @param oletus mit� n�ytet��n oletuksena
     */
    @Override
    public void setDefault(String oletus) {
        //
    }
    
    /**
     * palautetaan dialogin "tulos"
     * @return dialogin tulos, t�ss� tapauksessa null
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
     * Tallennuksen k�sittely
     */
    @FXML void handleTallenna() {
        tallenna();
    }
    /**
     * Tulostuksen k�sittely
     * Mieti, ett� toteutanko tulostuksen t�ss� harkkaty�ss�
     * Jos toteutan, kannattaa tehd� se mahdollisimman pian ja lis�t�
     * my�s suunnitelmaan itselle malliksi 
     */
    @FXML void handleTulosta() {
        tulosta();
      // t�m�n n�kym�n kuva puuttui vaiheesta 2
    }
    /**
     * Lopetus-k�skyn k�sittely
     */
    @FXML void handleLopeta() {
        tallenna();
        Platform.exit();
    }
    
    /**
     * Hakuehdon m��ritt�misen k�sittely
     * @throws SailoException  jos kannan kanssa ongelmia
     */
    @FXML void handleHakuehto() throws SailoException {
        hae(0);
    }
    
    /**
     * K�sitell��n omien tietojen tarkastelu ja muokkaus
     * t�ll� hetkell� voi muokata ket� tahansa j�sent� (ei vain omia)
     */
    @FXML void handleOmatTiedot() {
        try {
            Jasen jasen;
            // poista allaoleva kommenteista jos haluat, ett� vain omia tietoja muokataan (oletus: oma j�senolio on aina ensimm�inen, vrt. �mp�ri Astiapeliss�)
            // jasen = OmatTiedotController.kysyJasen(null, ryhma.annaJasen(0).clone()); //Testattu, toimii
            if (jasenKohdalla == null) { Dialogs.showMessageDialog("J�sent� ei valittu!",
                    dlg -> dlg.getDialogPane().setPrefWidth(400)); return; } //jos j�seni� ei ole, ei ole my�sk��n valittua j�sent�
            jasen = OmatTiedotController.kysyJasen(null, jasenKohdalla.clone(), ryhma);
            if (jasen == null) return; //jos muokkaukset peruutetaan, ei tehd� tietojen korvausta
            try {
                ryhma.korvaaTaiLisaa(jasen);
            } catch (SailoException e) {
                System.err.println("Virhe: " + e.getMessage());
            }
            try {
                hae(jasen.getTunnusNro()); //haetaan lista uudelleen jotta muutokset n�kyy
            } catch (SailoException e) {
                System.err.println("Virhe: " +e.getMessage());
            } 
        } catch (CloneNotSupportedException ex) {
            // ei tarvita koska clone()-metodi tehty itse ja tiedet��n toimivaksi
        }
    }
    
    /**
     * K�sitell��n Ryhm�t-n�kym�n tarkastelu
     * huom! Ryhm�t-n�kym� on toiminnaltaan niin monimutkainen,
     * ett� on sallittu j�tt�� pois t�st� harjoitusty�st�
     */
    @FXML void handleRyhmat() {
        ryhmat();
    }
    /**
     * K�sitell��n aputoiminto
     */
    @FXML void handleApua() {
        apua();
    }
    
    @FXML void handleTiedot() {
        ModalController.showModal(ResurssitGUIController.class.getResource("Tiedot.fxml"),
                "Tietoja ohjelmasta", null, "");
    }
    
    /**
     * K�sitell��n uuden j�senen lis��minen
     * Huom. t�m� on testaamista varten eik� j�� lopulliseen harjoitusty�h�n
     */
    @FXML private void handleUusiJasen() {
        uusiJasen();
    }
    
    /**
     * K�sitell��n uuden ty�kalun lis��minen
     * Huom. t�m� on testaamista varten eik� j�� lopulliseen harjoitusty�h�n
     * @throws SailoException jos kannan kanssa ongelmia
     */
    @FXML private void handleUusiTyokalu() throws SailoException {
        uusiTyokalu();
    }
    
    
    // ==================================================
    private Ryhma ryhma;
    private Jasen jasenKohdalla;
    private static String ryhmanNimi = "fuksit17"; //t�ss� harkkaty�ss� k�ytet��n yht� ryhm��, joka on t�m�
    private static Jasen apujasen = new Jasen();
    private static Tyokalu aputyokalu = new Tyokalu(); //apuolio kenttien hakuun
    
    /**
     * Tekee tarvittavat muut alustukset, vaihdetaan Stringgridin tilalle
     * yksi iso tekstikentt�, johon voidaan tulostaa j�senten tiedot.
     * Alustetaan my�s j�senlistan kuuntelija.
     */
    protected void alusta() {
        panelJasen.setFitToHeight(true);

        alustaJasenet(); //alustaa j�senien StringGridin
        alustaTyokalut(); //alustaa ty�kalujen StringGridin kuten Jasenet-gridin yll�
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
     * Alustetaan J�senet-StringGrid
     */
    protected void alustaJasenet() {
        int eka = apujasen.ekaKentta();
        int lkm = apujasen.getKenttia();
        String[] headings = new String[lkm-eka];
        for (int i = 0, k = eka; k<lkm; i++,k++) headings[i] = apujasen.getKentta(k);
        tableJasenet.initTable(headings);
        
        tableJasenet.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //ylim. sarake pois
        tableJasenet.setEditable(false);
        tableJasenet.setPlaceholder(new Label("Ei j�seni�")); 
    }

    /**
     * Alustetaan ty�kalut-StringGrid
     */
    protected void alustaTyokalut() {
        //otsikot ty�kalutaulukkoon:
        int eka = aputyokalu.ekaKentta();
        int lkm = aputyokalu.getKenttia();
        String[] headings = new String[lkm-eka];
        for (int i = 0, k = eka; k<lkm; i++,k++) headings[i] = aputyokalu.getKentta(k);
        tableTyokalut.initTable(headings);
        
        tableTyokalut.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //ylim. sarake pois
        
        tableTyokalut.setPlaceholder(new Label("J�senell� ei ole ty�kaluja"));
        panelTyokalu.setFitToHeight(true);
    }
    
    /**
     * Alustetaan hakuehdoiksi j�senen tietokenttien nimet (lukuunottamatta
     * puhelinta ja s�hk�postia)
     */
    protected void alustaHakuehdot() {
        chooserKentat.clear();
        for (int k=apujasen.ekaKentta();k<4;k++ ) //kentt� 4 = puhelin; jos halutaan kaikki -> k<apujasen.getKenttia()
            chooserKentat.add(apujasen.getKentta(k),apujasen.getKentta(k));
        chooserKentat.add("Ty�kalu","Ty�kalu");
    }

    /**
     * N�ytt�� j�sentaulukosta valitun j�senen tiedot (ty�kalut)
     * @throws SailoException jos kannan kanssa ongelmia
     * @throws IndexOutOfBoundsException jos mahdoton indeksi
     */
    protected void naytaJasen() throws IndexOutOfBoundsException, SailoException {
        jasenKohdalla = tableJasenet.getObject();
        tableJasenet.setOnCellString( (g, jasen, oletusArvo, r, c ) -> jasen.anna(c+apujasen.ekaKentta())); //asetetaan sarakkeisiin j�senten tiedot (ensin c+eka=1, jne)
        tableJasenet.setOnCellValue( (g, jasen, defValue, r, c ) -> jasen.getAvain(c+apujasen.ekaKentta()) ); 
        if (jasenKohdalla == null) return;
        naytaTyokalut(jasenKohdalla);
        //tableJasenet.selectRow();
    }


    /**
     * N�ytt�� j�senen ty�kalut
     * (periaatteena merkkijonon pilkkominen)
     * @param valittuJasen j�sen jonka ty�kalut n�ytet��n
     * @throws SailoException jos kannan kanssa ongelmia
     * @throws IndexOutOfBoundsException jos mahdoton indeksi
     */
    protected void naytaTyokalut(Jasen valittuJasen) throws IndexOutOfBoundsException, SailoException {
        tableTyokalut.clear(); //poistetaan mahd. olemassaolevat tiedot
        
        if (valittuJasen == null) return;
        List<Tyokalu> tyokalut = ryhma.annaTyokalut(valittuJasen);
        if (tyokalut.size() == 0) return; //jos ei ty�kaluja, n�ytet��n placeholder

        for (Tyokalu tyok : tyokalut) {
            naytaTyokalu(tyok);
        }
    }
    
    /*
     * Muokataan ty�kalun tietoja
     * HUOM. t�m� tulee siirtym��n OmatTiedotControlleriin
     *
    protected void muokkaaTyokalua() {
        tableTyokalut.setEditable(true); //vaihda jos muokkausmahdollisuus muuttuu
        int eka = aputyokalu.ekaKentta();
        tableTyokalut.setOnGridLiveEdit((g, tyok, teksti, r, c, edit) -> {
            String virhe;
            //nimi haetaan tunnuksen avulla, joten se k�sitell��n erikseen (nimi on sarakkeessa 0):
            if (c==0) {     
                tableTyokalut.setOnMouseExited(e -> ryhma.setTkNimi(tyok,teksti)); 
                virhe = null;
            }
            //yleisten rivien k�sittely:
            else virhe = tyok.aseta(c+eka,teksti);
            if ( virhe == null ) {
                ryhma.korvaaTaiLisaa(tyok); // jotta saadaan muutos
                edit.setStyle(null);
            } else { //t�ll� hetkell� t�m� ei ole mahdollista, sill� oikeellisuustarkistuksia ei ole
                edit.setStyle("-fx-background-color: red");
                //ilmoittaa virheest� vain v�rimuutoksella, virheviesti� ei n�ytet�
            }
            return teksti;
        });
    }*/
    
    /**
     * Hakee listaan tietyn j�senen ty�kalun
     * @param tyok n�ytett�v� ty�kalu
     * @throws SailoException jos kannan kanssa ongelmia
     * @throws IndexOutOfBoundsException 
     */
    private void naytaTyokalu(Tyokalu tyok) throws IndexOutOfBoundsException, SailoException {
        String[] rivit = new String[tyok.getKenttia()-tyok.ekaKentta()];
        /*for (int i=0, k=tyok.ekaKentta(); k<tyok.getKenttia(); i++, k++)
            rivit[i] = tyok.anna(k);*/
        int tkNro = tyok.getTyokaluNro(); //tyokaluNro
        rivit[0] = ryhma.annaTkNimiTeksti(tkNro); //asetetaan ty�kalun nimi ensimm�iseen sarakkeeseen
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
     * Ryhm�t-ikkunan sijasta n�ytett�v� dialogi
     */
    private void ryhmat() {
        Dialogs.showMessageDialog("T�t� toimintoa ei toteuteta t�ss� harjoitusty�ss�",
                                    dlg -> dlg.getDialogPane().setPrefWidth(400));
    }
    
    /**
     * Luo uuden j�senen Aku Ankka-tiedoilla
     * Huom! T�t� ominaisuutta ei ole harjoitusty�n
     * suunnitelmassa, sill� ohjelmaa ei ole tarkoitettu k�ytett�v�ksi t�h�n.
     * T�m� ominaisuus on luotu j�senen luomisen testaamiseksi.
     */
    private void uusiJasen() { 
          try { 
            Jasen jasen = new Jasen();
            jasen.taytaAkuAnkka();
            jasen.rekisteroi(); //pit�� rekister�id� etuk�teen jotta ty�kalun lis��minen onnistuu
            int tunnus = jasen.getTunnusNro();
            jasen = OmatTiedotController.kysyJasen(null, jasen.clone(),ryhma); //Avataan muokkausdialogi
            if (jasen == null) {
                List<Tyokalu> tyok = ryhma.annaTyokalut(tunnus);
                if (tyok.size()<1) return; //jos muokkaukset peruutetaan ja j�senell� ei ty�kaluja, ei korvata tietoja
                for (int i=0; i<tyok.size();i++) { 
                    ryhma.poistaTyokalu(tyok.get(i)); //poistetaan mahd. j�senelle lis�tyt ty�kalut
                }
                return; //jos muokkaukset peruutetaan, ei tehd� tietojen korvausta       
            }
            ryhma.lisaa(jasen); 
            try {
                hae(jasen.getTunnusNro()); //haetaan lista uudelleen jotta muutokset n�kyy
            } catch (SailoException e) {
                e.printStackTrace();
            } 
        } catch (CloneNotSupportedException ex) {
            // ei tarvita koska clone()-metodi tehty itse ja tiedet��n toimivaksi
        }
    }

    
    /**
     * Haetaan j�senten tiedot listaan
     * @param jasenNro valitun j�senen numero; uutta lis�tt�ess� uuden j�senen nro
     * @throws SailoException jos kannan kanssa ongelmia
     */
    protected void hae(int jasenNro) throws SailoException {
        tableJasenet.clear(); //tyhjennet��n grid
        
        int index = 0;
        int k = chooserKentat.getSelectedIndex() + apujasen.ekaKentta(); //nimi on ensimm�inen j�rkev� hakuehto
        Collection<Jasen> jasenet;
        String ehto = hakuehto.getText();
        if (ehto.indexOf('*')<0) ehto = "%" + ehto + "%"; //jos k�ytt�j� ei laita asteriskeja (indexOf palauttaa -1), laitetaan ne itse ehtoon
        jasenet = ryhma.etsi(ehto,k);
        int i = 0;
        for (Jasen jasen : jasenet) {    
            if (jasen.getTunnusNro() == jasenNro) index = i;
            tableJasenet.add(jasen);
            i++;
        }
        tableJasenet.selectRow(index); // valitsee uuden j�senen
        naytaJasen();   //n�ytt�� valitun j�senen tiedot
    }
    
    /**
     * Luo uuden ty�kalun valitulle j�senelle
     * Huom! T�t� ominaisuutta ei ole harjoitusty�n
     * suunnitelmassa, sill� ohjelmaa ei ole tarkoitettu k�ytett�v�ksi t�h�n.
     * T�t� ei siis v�ltt�m�tt� sis�llytet� lopulliseen harjoitusty�h�n, t�t� k�ytet��n
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
     * Ohjelman avustus, n�ytet��n selaimessa (Harkan TIM-sivu)
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
     * Avaa aloitusn�kym�n kun ohjelma k�ynnistet��n
     */
    public void avaa() {
        lueTiedosto(ryhmanNimi);    //avatessa luetaan jo tiedosto
        ModalController.showModal(AloitusnakymaController.class.getResource("Aloitusnakyma.fxml"),
             "Kirjaudu sis��n", null, "");
            
       
    }
    /**
     * Alustaa j�senen luettavissa olevat tiedot lukemalla valitun nimisest� tiedosto(i)sta
     * samoihin ryhmiin kuuluvien j�senien tiedot
     * (HUOM. t�ss� ht:ssa vain yksi ryhm�)
     * t�m� kai voisi olla void? Ei kai noita nulleja ja virheit� tarvitse palauttaa?
     * @param nimi ryhm�n nimi (=ryhm�n hakemiston nimi)
     * @return null jos onnistuu, virhe tekstin� jos ep�onnistuu 
     */
    protected String lueTiedosto(String nimi) {
        ryhmanNimi = nimi;
        try {
            ryhma.lueTiedostosta(nimi); //luetaan t�m�nnimisen ryhm�n tiedot
            hae(0); //haetaan j�senten tiedot listaan, ensimm�inen j�sen aktiiviseksi
            return null;
        } catch (SailoException e) {
            String virhe = e.getMessage();
            if (virhe != null) Dialogs.showMessageDialog("Tapahtui virhe: " + virhe);
            return virhe;
        }     
    }

    /**
     * @param ryhma ryhm� jota k�sitell��n
     * @throws SailoException jos kannan kanssa ongelmia
     * @throws IndexOutOfBoundsException jos mahdoton indeksi
     */
    public void setRyhma(Ryhma ryhma) throws IndexOutOfBoundsException, SailoException {
        this.ryhma = ryhma;
        naytaJasen();                          
    }

}
