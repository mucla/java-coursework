package ryhma;

import static ryhma.Kanta.alustaKanta;

import java.io.File;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;

import java.sql.*;

import fi.jyu.mit.ohj2.WildChars;

//import static ryhma.Kanta.alustaKanta;

import java.util.*;

/**
 * Ryhmä-luokka
 * @author miemkoiv
 * @version 22.2.2017
 */
public class Ryhma {
    /*
     * Alustuksia ja puhdistuksia testejä varten
     * @example
     * <pre name="testJAVA">
     * #import java.io.*;
     * #import java.util.*;
     * 
     * private Jasenet jasenet;
     * private String tiedNimi;
     * private File ftied;
     * 
     * @Before
     * public void alusta() throws SailoException { 
     *    tiedNimi = "testiryhma";
     *    ftied = new File(tiedNimi+".db");
     *    ftied.delete();
     *    jasenet = new Jasenet(tiedNimi);
     * }   
     *
     * @After
     * public void siivoa() {
     *    ftied.delete();
     * }   
     * </pre>
     */
    private Jasenet jasenet;  // Yhdistää Ryhma-luokan ja Jasenet-luokan
    private Tyokalut tyokalut;
    private TyokalujenNimet tknimet;
    private Kanta kanta;
    
    /**
     * Palauttaa ryhmän jäsenten lukumäärän
     * Olisiko pitänyt vaihtaa kun siirrytään käyttämään tietokantaa?
     * @return jäsenten lukumäärä
     */
    public int getJasenia() {
        return jasenet.getLkm();
    }
    
    /**
     * Lisää ryhmään uuden jäsenen
     * Huom! Jäseniä ei suunnitelman mukaan lisätä käyttöliittymän 
     * kautta, mutta testin vuoksi mukana
     * @param jasen lisättävä jäsen
     */
    public void lisaa(Jasen jasen){
        try {
            jasenet.lisaa(jasen);
        } catch (SailoException e) {
            System.err.println("Virhe lisätessä jäsentä: " + e.getMessage());
        }
    }    
    
    /**
     * lisää ryhmään työkalun
     * @param tyokalu työkalu joka lisätään
     */
    public void lisaaTyokalu(Tyokalu tyokalu) {
        try {
            tyokalut.lisaa(tyokalu);
        } catch (SailoException e) {
            System.err.println("Virhe lisätessä työkalua: " + e.getMessage());
        }
    }
    
    /**
     * lisää ryhmään työkalun nimen
     * @param tyokalu työkalun nimi joka lisätään
     */
    public void lisaaTkNimi(TyokalunNimi tyokalu) {
        try {
            tknimet.lisaa(tyokalu);
        } catch (SailoException e) {
            System.err.println("Virhe lisätessä työkalunimeä: " + e.getMessage());
        }
    }    
    
    /**
     * Luo uuden työkalun valitulle jäsenelle
     * @param jasenNro valitun jäsenen jäsennumero
     */
    public void uusiTyokalu(int jasenNro) {
        Tyokalu uusi = new Tyokalu();
        //uusi.rekisteroi(); //tietokantaversiossa ei tarvitse rekisteröidä
        uusi.vastaaVasara(jasenNro);
        try {
            tyokalut.lisaa(uusi);
        } catch (SailoException e) {
            System.err.println("Virhe: " + e.getMessage());
        }
    }
    
    /**
     * Poistaa työkalun
     * @param tyok poistettava työkalu
     */
    public void poistaTyokalu(Tyokalu tyok) {
        try {
            tyokalut.poista(tyok);
        } catch (SailoException e) {
            System.err.println("Virhe: " + e.getMessage());
        }
    }
    
    /**
     * Luo uuden jäsenen Aku Ankka-tiedoilla.
     * Huom! Tätä ominaisuutta ei ole harjoitustyön
     * suunnitelmassa, sillä ohjelmaa ei ole tarkoitettu käytettäväksi tähän.
     * Tämä ominaisuus on luotu jäsenen luomisen testaamiseksi
     * @return viite uuteen jäseneen
     */
    public Jasen uusiJasen() {
        Jasen uusi = new Jasen();
        uusi.rekisteroi();
        uusi.taytaAkuAnkka();
        //lisaa(uusi); 
        return uusi; 
    }
    

    
    /**
     * Palauttaa viitteen työkalun nimi -olion nimeen
     * HUOM! Vaihdetaan kun siirrytään käyttämään tietokantaa
     * @param id työkalun tyokaluNro
     * @return viite työkalun nimeen
     * @throws IndexOutOfBoundsException jos id on väärin
     * @throws SailoException jos ongelmia tietokannan kanssa
     */
    public String annaTkNimiTeksti(int id) throws IndexOutOfBoundsException, SailoException {
        return tknimet.getNimi(id);
    }
    
    /**
     * Asettaa työkalulle tyokaluNron (eli viitteen työkalun nimeen)
     * Huom. tietokantaversiossa: työkalunimiä ei koskaan muokata vaan 
     * muutetut nimet rekisteröidään aina uutena tämän metodin avulla.
     * Siksi TyokalunNimi-luokkaan ei tehdä päivityslauseketta.
     *
     * @param tyok työkalu jolle työkaluNro asetetaan
     * @param nimi työkalulle asetettava nimiteksti
     * @throws SailoException jos ongelmia tietokannasta haettaessa
     */
    public void setTkNimi(Tyokalu tyok, String nimi) throws SailoException {
        for (TyokalunNimi tknimi : tknimet.etsi("",-1)) { //käy läpi listaa nimistä
          if ( tknimi.getNimi().equals(nimi)) { //jos nimi löytyy listasta, asetetaan sitä vastaava tunnus tyokalunroksi ja lopetetaan
              tyok.setTyokaluNro(tknimi.getTunnusNro());
              return;
          }
      }
      //jos nimeä ei löydy listasta, lisätään se ja asetetaan työkalulle vastaava tyokaluNro:
      TyokalunNimi uusi = new TyokalunNimi(nimi);
      uusi.rekisteroi();
      lisaaTkNimi(uusi);
      tyok.setTyokaluNro(uusi.getTunnusNro());
    }
    
    
    /**
     * Palauttaa annetun jäsenen työkalut
     * @param jasen valittu jäsen
     * @return valitun jäsenen työkalut
     * <pre name="test">
     *  #import java.util.*;
     *  Ryhma ryhma1 = new Ryhma();
     *  
     *  Jasen aku1 = new Jasen(), aku2 = new Jasen();
     *  aku1.rekisteroi(); aku2.rekisteroi();
     *  
     *  Tyokalu vasara = new Tyokalu(aku1.getTunnusNro());
     *  Tyokalu vasara2 = new Tyokalu(aku1.getTunnusNro());
     *  Tyokalu ruuvari = new Tyokalu(aku2.getTunnusNro()); 
     *  ryhma1.lisaaTyokalu(vasara); ryhma1.lisaaTyokalu(vasara2); 
     *  ryhma1.lisaaTyokalu(ruuvari);
     *  
     *  List<Tyokalu> loytyneet;
     *  loytyneet = ryhma1.annaTyokalut(aku1);
     *  loytyneet.size() === 2;
     *  loytyneet.get(0) == vasara === true;
     *  loytyneet.get(1) == ruuvari === false;
     *  loytyneet.get(1) == vasara2 === true; 
     *  loytyneet = ryhma1.annaTyokalut(aku2);
     *  loytyneet.size() === 1;
     *  loytyneet.get(0) == ruuvari === true;
     *  
     *  vasara.setTyokaluNro(1); vasara2.setTyokaluNro(1);
     *  TyokalunNimi vasaraNimi = new TyokalunNimi("vasara");
     *  vasaraNimi.rekisteroi();
     *  ryhma1.lisaaTkNimi(vasaraNimi);
     *  List<TyokalunNimi> tyoknimet;
     *  tyoknimet = ryhma1.annaTkNimet(aku1);
     *  tyoknimet.size() === 2;
     *  // yllä: jostain syystä palauttaa 0 jos testit lueTiedostosta-metodille aktiivisena
     * </pre> 
     */
    public List<Tyokalu> annaTyokalut(Jasen jasen) {
        List<Tyokalu> tyok = new ArrayList<Tyokalu>();
        try {
            tyok = tyokalut.annaTyokalut(jasen.getTunnusNro());
        } catch (SailoException e) {
            System.err.println("Virhe: " + e.getMessage());
        } return tyok;
    }
    
    /**
     * Hakee listan jäsenen työkaluista (jäsenen tunnusNro:n perusteella)
     * @param tunnusNro jäsenen tunnusnro
     * @return listan työkaluista
     */
    public List<Tyokalu> annaTyokalut(int tunnusNro) {
        List<Tyokalu> tyok = new ArrayList<Tyokalu>();
        try {
            tyok = tyokalut.annaTyokalut(tunnusNro);
        } catch (SailoException e) {
            System.err.println("Virhe: " + e.getMessage());
        } return tyok;
    }
    
    
    /**
     * Palauttaa annetun jäsenen työkalujen nimet listana
     * (tietokantaversiossa tarvitaan vain jäsenen tieto)
     * @param jasen jäsen jonka työkalujen nimet haetaan
     * @return valitun jäsenen työkalujen nimet listana
     * @throws SailoException jos ongelmia
     */
    public List<TyokalunNimi> annaTkNimet(Jasen jasen) throws SailoException {
        List<Tyokalu> tyok = tyokalut.annaTyokalut(jasen.getTunnusNro());
        List<TyokalunNimi> nimilista = tknimet.annaTkNimet(tyok);
        return nimilista;
    }
    
    /**
     * Luetaan tiedostosta ryhmän tiedot
     * Ryhmän nimeä ei tässä harjoitustyössä aseteta käyttöliittymästä
     * vaan se on aina oletus (ResurssiGUIControllerissa asetettu ryhmanNimi)
     * @param nimi ryhmän nimi (oletuksena "fuksit17")
     * @throws SailoException jos virhe luettaessa
     * //<pre name="test">
     * //#THROWS SailoException
     * //#import java.io.File;
     *  //Ryhma tryhma = new Ryhma();
     *  //Jasen aku = new Jasen();
     *  //Jasen aku2 = new Jasen();
     *  //aku.taytaAkuAnkka();
     *  //aku.rekisteroi();
     *  //aku2.taytaAkuAnkka();
     *  //aku2.rekisteroi();
     *  //
     *  //Tyokalu vasara = new Tyokalu(aku.getTunnusNro());
     *  //Tyokalu vasara2 = new Tyokalu(aku.getTunnusNro());
     *  //Tyokalu ruuvari = new Tyokalu(aku2.getTunnusNro()); 
     *  //
     *  //vasara.setTyokaluNro(1); vasara2.setTyokaluNro(1);
     *  //TyokalunNimi vasaraNimi = new TyokalunNimi("vasara");
     *  //vasaraNimi.rekisteroi(); //nyt tyokaluNro 1 vastaa nimeä "vasara"
     *  //
     *  //String kansio = "testiryhma";
     *  //File hak = new File(kansio);
     *  //hak.mkdir();
     *  //File jasenet  = new File("testiryhma.db");
     *  //File tyok = new File("kokeilu/tyokalut.dat");
     *  //File tknimet = new File("kokeilu/tyokalujenNimet.dat");
     *  //jasenet.delete();
     *  //tyok.delete();
     *  //tknimet.delete();
     *  //tryhma.setTiedosto(kansio);
     *  //tryhma.lueTiedostosta(kansio); #THROWS SailoException
     *  //tryhma.lisaa(aku);
     *  //tryhma.lisaa(aku2);
     *  //tryhma.lisaaTyokalu(vasara); tryhma.lisaaTyokalu(vasara2); tryhma.lisaaTyokalu(ruuvari);
     *  //tryhma.lisaaTkNimi(vasaraNimi);
     *  //tryhma.tallenna();
     *  //tryhma = new Ryhma(); //alustetaan ryhmä, luetaan tiedostosta
     *  //tryhma.setTiedosto(kansio);
     *  //tryhma.lueTiedostosta(kansio);
     *  //Jasen tupu = new Jasen();
     *  //tryhma.lisaa(tupu);
     *  //Tyokalu ruuvari2 = new Tyokalu(tupu.getTunnusNro());
     *  //tryhma.lisaaTyokalu(ruuvari2);
     *  //tryhma.tallenna();
     *  //jasenet.delete() === true;
     *  //tyok.delete() === true;
     *  //tknimet.delete() === true;
     *  //File jasbak = new File("testiryhma.db");
     *  //File tyobak = new File(kansio+"/tyokalut.bak");
     *  //File tnimbak = new File(kansio+"/tyokalujenNimet.bak");
     *  //jasbak.delete() === true;
     *  //tyobak.delete() === true;
     *  //tnimbak.delete() === true;
     *  //hak.delete() === true;
     * //</pre>
     */
    public void lueTiedostosta(String nimi) throws SailoException {
        jasenet = new Jasenet(nimi); //tyhjennetään ennen lukemista mahd. olemassaolevat jäsenet
        tyokalut = new Tyokalut(nimi); 
        tknimet = new TyokalujenNimet(nimi);
    }
    


    /**
     * Tallentaa ryhmän tiedot tiedostoon
     * @throws SailoException jos tallennuksessa ilmenee ongelmia
     */
    public void tallenna() throws SailoException {
        return;
    }
    
    /**
     * Korvaa jäsenen tiedot muutetuilla tiedoilla; jos jäsentä ei löydy, lisätään uusi
     * @param jasen korvattava tai lisättävä jäsen
     * @throws SailoException jos muokkaus/lisäys ei onnistu
     */
    public void korvaaTaiLisaa(Jasen jasen) throws SailoException{
        jasenet.korvaaTaiLisaa(jasen);
        
    }
    
    /**
     * Korvaa työkalun tiedot muutetuilla tiedoilla; jos työkalua ei löydy, lisätään uusi
     * Asetetaan samalla uusi työkalunimi, mikäli tarpeen
     * @param tyok korvattava tai lisättävä työkalu
     * @throws SailoException jos muokkaus/lisäys ei onnistu
     */
    public void korvaaTaiLisaa(Tyokalu tyok) throws SailoException {
        tyokalut.korvaaTaiLisaa(tyok);    
    }
    
    /**
     * Haetaan hakuehdon täyttävät jäsenet
     * @param hakuehto hakuehto
     * @param k kenttä josta hakuehdolla haetaan
     * @return jäsenet, jotka täyttävät hakuehdon
     * @throws SailoException jos ongelmia
     */
    public Collection<Jasen> etsi(String hakuehto, int k) throws SailoException {
        Collection<Jasen> loydetyt = new ArrayList<Jasen>();
        if (k==4)  //kenttä 4 = työkalu hakuehtona
            return etsiTyokalu(hakuehto);
        try {
            loydetyt = jasenet.etsi(hakuehto, k);
        } catch (SailoException e) {
            System.err.println("Virhe: " + e);
        } 
        return loydetyt;
    }

    /**
     * Etsii hakuehtoa (=työkalun nimi) vastaavia työkaluja omistavat jäsenet 
     * @param hakuehto hakuehto
     * @return jäsenet, joilla on hakuehtoa vastaavia työkaluja
     * @throws SailoException jos ongelmia
     */
    public Collection<Jasen> etsiTyokalu(String hakuehto) throws SailoException {
        String ehto = ""; //jos ei hakuehtoa, haetaan kaikki jäsenet
        kanta = alustaKanta("fuksit17"); //
        if (hakuehto != null && hakuehto.length() > 0) ehto = "*" + hakuehto.substring(1, hakuehto.length()-1) + "*"; //korvataan SQL-jokerimerkit siten, että Wildchars-luokka tunnistaa
        try ( Connection con = kanta.annaKantaYhteys();
                PreparedStatement sql = con.prepareStatement("SELECT * FROM Jasenet") ) {
              ArrayList<Jasen> loytyneet = new ArrayList<Jasen>();
              try ( ResultSet tul = sql.executeQuery() ) { //tuloslista on nyt lista työkalunimiä
                  while ( tul.next() ) {
                      Jasen j = new Jasen();
                      j.parse(tul);
                      List<TyokalunNimi> nimet = annaTkNimet(j);
                      boolean omistaako = false;
                      for (TyokalunNimi tknimi : nimet) {
                          if (WildChars.onkoSamat(tknimi.getNimi(), ehto)) 
                              omistaako = true;
                      }
                      if (omistaako) {            
                          loytyneet.add(j);
                      }
                  }
            }
            return loytyneet;
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }


    /**
     * Testipääohjelma ryhmälle
     * @param args ei käytössä
     */
    public static void main(String[] args)  {
        try {
            new File("kokeilu.db").delete();
            Ryhma ryhma = new Ryhma();
            ryhma.lueTiedostosta("kokeilu");

            Jasen aku = new Jasen();
            aku.rekisteroi();
            aku.taytaAkuAnkka();
            Jasen aku2 = new Jasen();
            aku2.rekisteroi();
            aku2.taytaAkuAnkka();
            Jasen aku3 = new Jasen();
            aku3.rekisteroi();
            aku3.taytaAkuAnkka();

            
            Tyokalu vasara = new Tyokalu();
            vasara.rekisteroi();
            vasara.vastaaVasara(aku.getTunnusNro());
            Tyokalu vasara2 = new Tyokalu();
            vasara2.vastaaVasara(aku2.getTunnusNro());
            Tyokalu ruuvari = new Tyokalu(2);
            ryhma.lisaaTyokalu(vasara);
            ryhma.lisaaTyokalu(vasara2);
            ryhma.lisaaTyokalu(ruuvari);
            TyokalunNimi tkNimi = new TyokalunNimi("(tuntematon)"); //TyokalunNimi-luokassa tunnusNro = 0 --> tyokaluNro = 0 
            ryhma.lisaaTkNimi(tkNimi);
            TyokalunNimi vasaraNimi = new TyokalunNimi("vasara");   //TyokalunNimi-luokassa tunnusNro = 1
            vasaraNimi.rekisteroi();
            ryhma.lisaaTkNimi(vasaraNimi);
            
            ryhma.lisaa(aku);
            ryhma.lisaa(aku2);
            ryhma.lisaa(aku3);
            //ryhma.lisaa(aku2);
            //ryhma.lisaa(aku);
            //ryhma.lisaa(aku2);
            //ryhma.lisaa(aku);
            //ryhma.lisaa(aku2);
            //ryhma.lisaa(aku);


            System.out.println("======= Ryhmän testi =========");
            int i = 0;

            for (Jasen jasen : ryhma.etsi("", -1)) {
                System.out.println("Jäsen paikassa: " + i);
                jasen.tulosta(System.out);
                System.out.println("Jäsenen työkalut (niminä): ");
                List<Tyokalu> tyokalulista = ryhma.annaTyokalut(jasen);
                List<TyokalunNimi> tyoknimet = ryhma.annaTkNimet(jasen);
                /*for (int k = 0; k<tyoknimet.size(); k++) {
                    */
                for (TyokalunNimi tknimi : tyoknimet) {
                    String nimi = tknimi.getNimi();
                    //
                    System.out.println(nimi);
                    
                }

                System.out.println("Jäsenen työkalujen tiedot: ");
                for (int j = 0; j<tyokalulista.size(); j++) {
                    Tyokalu tyokalu = tyokalulista.get(j);
                    tyokalu.tulosta(System.out);   
                }                           
                i++;
            }
        } catch (SailoException e) {
            System.err.println("Virhe: " + e.getMessage());
        }
        new File("kokeilu.db").delete();
    }

}
