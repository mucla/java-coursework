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
 * Ryhm�-luokka
 * @author miemkoiv
 * @version 22.2.2017
 */
public class Ryhma {
    /*
     * Alustuksia ja puhdistuksia testej� varten
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
    private Jasenet jasenet;  // Yhdist�� Ryhma-luokan ja Jasenet-luokan
    private Tyokalut tyokalut;
    private TyokalujenNimet tknimet;
    private Kanta kanta;
    
    /**
     * Palauttaa ryhm�n j�senten lukum��r�n
     * Olisiko pit�nyt vaihtaa kun siirryt��n k�ytt�m��n tietokantaa?
     * @return j�senten lukum��r�
     */
    public int getJasenia() {
        return jasenet.getLkm();
    }
    
    /**
     * Lis�� ryhm��n uuden j�senen
     * Huom! J�seni� ei suunnitelman mukaan lis�t� k�ytt�liittym�n 
     * kautta, mutta testin vuoksi mukana
     * @param jasen lis�tt�v� j�sen
     */
    public void lisaa(Jasen jasen){
        try {
            jasenet.lisaa(jasen);
        } catch (SailoException e) {
            System.err.println("Virhe lis�tess� j�sent�: " + e.getMessage());
        }
    }    
    
    /**
     * lis�� ryhm��n ty�kalun
     * @param tyokalu ty�kalu joka lis�t��n
     */
    public void lisaaTyokalu(Tyokalu tyokalu) {
        try {
            tyokalut.lisaa(tyokalu);
        } catch (SailoException e) {
            System.err.println("Virhe lis�tess� ty�kalua: " + e.getMessage());
        }
    }
    
    /**
     * lis�� ryhm��n ty�kalun nimen
     * @param tyokalu ty�kalun nimi joka lis�t��n
     */
    public void lisaaTkNimi(TyokalunNimi tyokalu) {
        try {
            tknimet.lisaa(tyokalu);
        } catch (SailoException e) {
            System.err.println("Virhe lis�tess� ty�kalunime�: " + e.getMessage());
        }
    }    
    
    /**
     * Luo uuden ty�kalun valitulle j�senelle
     * @param jasenNro valitun j�senen j�sennumero
     */
    public void uusiTyokalu(int jasenNro) {
        Tyokalu uusi = new Tyokalu();
        //uusi.rekisteroi(); //tietokantaversiossa ei tarvitse rekister�id�
        uusi.vastaaVasara(jasenNro);
        try {
            tyokalut.lisaa(uusi);
        } catch (SailoException e) {
            System.err.println("Virhe: " + e.getMessage());
        }
    }
    
    /**
     * Poistaa ty�kalun
     * @param tyok poistettava ty�kalu
     */
    public void poistaTyokalu(Tyokalu tyok) {
        try {
            tyokalut.poista(tyok);
        } catch (SailoException e) {
            System.err.println("Virhe: " + e.getMessage());
        }
    }
    
    /**
     * Luo uuden j�senen Aku Ankka-tiedoilla.
     * Huom! T�t� ominaisuutta ei ole harjoitusty�n
     * suunnitelmassa, sill� ohjelmaa ei ole tarkoitettu k�ytett�v�ksi t�h�n.
     * T�m� ominaisuus on luotu j�senen luomisen testaamiseksi
     * @return viite uuteen j�seneen
     */
    public Jasen uusiJasen() {
        Jasen uusi = new Jasen();
        uusi.rekisteroi();
        uusi.taytaAkuAnkka();
        //lisaa(uusi); 
        return uusi; 
    }
    

    
    /**
     * Palauttaa viitteen ty�kalun nimi -olion nimeen
     * HUOM! Vaihdetaan kun siirryt��n k�ytt�m��n tietokantaa
     * @param id ty�kalun tyokaluNro
     * @return viite ty�kalun nimeen
     * @throws IndexOutOfBoundsException jos id on v��rin
     * @throws SailoException jos ongelmia tietokannan kanssa
     */
    public String annaTkNimiTeksti(int id) throws IndexOutOfBoundsException, SailoException {
        return tknimet.getNimi(id);
    }
    
    /**
     * Asettaa ty�kalulle tyokaluNron (eli viitteen ty�kalun nimeen)
     * Huom. tietokantaversiossa: ty�kalunimi� ei koskaan muokata vaan 
     * muutetut nimet rekister�id��n aina uutena t�m�n metodin avulla.
     * Siksi TyokalunNimi-luokkaan ei tehd� p�ivityslauseketta.
     *
     * @param tyok ty�kalu jolle ty�kaluNro asetetaan
     * @param nimi ty�kalulle asetettava nimiteksti
     * @throws SailoException jos ongelmia tietokannasta haettaessa
     */
    public void setTkNimi(Tyokalu tyok, String nimi) throws SailoException {
        for (TyokalunNimi tknimi : tknimet.etsi("",-1)) { //k�y l�pi listaa nimist�
          if ( tknimi.getNimi().equals(nimi)) { //jos nimi l�ytyy listasta, asetetaan sit� vastaava tunnus tyokalunroksi ja lopetetaan
              tyok.setTyokaluNro(tknimi.getTunnusNro());
              return;
          }
      }
      //jos nime� ei l�ydy listasta, lis�t��n se ja asetetaan ty�kalulle vastaava tyokaluNro:
      TyokalunNimi uusi = new TyokalunNimi(nimi);
      uusi.rekisteroi();
      lisaaTkNimi(uusi);
      tyok.setTyokaluNro(uusi.getTunnusNro());
    }
    
    
    /**
     * Palauttaa annetun j�senen ty�kalut
     * @param jasen valittu j�sen
     * @return valitun j�senen ty�kalut
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
     *  // yll�: jostain syyst� palauttaa 0 jos testit lueTiedostosta-metodille aktiivisena
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
     * Hakee listan j�senen ty�kaluista (j�senen tunnusNro:n perusteella)
     * @param tunnusNro j�senen tunnusnro
     * @return listan ty�kaluista
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
     * Palauttaa annetun j�senen ty�kalujen nimet listana
     * (tietokantaversiossa tarvitaan vain j�senen tieto)
     * @param jasen j�sen jonka ty�kalujen nimet haetaan
     * @return valitun j�senen ty�kalujen nimet listana
     * @throws SailoException jos ongelmia
     */
    public List<TyokalunNimi> annaTkNimet(Jasen jasen) throws SailoException {
        List<Tyokalu> tyok = tyokalut.annaTyokalut(jasen.getTunnusNro());
        List<TyokalunNimi> nimilista = tknimet.annaTkNimet(tyok);
        return nimilista;
    }
    
    /**
     * Luetaan tiedostosta ryhm�n tiedot
     * Ryhm�n nime� ei t�ss� harjoitusty�ss� aseteta k�ytt�liittym�st�
     * vaan se on aina oletus (ResurssiGUIControllerissa asetettu ryhmanNimi)
     * @param nimi ryhm�n nimi (oletuksena "fuksit17")
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
     *  //vasaraNimi.rekisteroi(); //nyt tyokaluNro 1 vastaa nime� "vasara"
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
     *  //tryhma = new Ryhma(); //alustetaan ryhm�, luetaan tiedostosta
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
        jasenet = new Jasenet(nimi); //tyhjennet��n ennen lukemista mahd. olemassaolevat j�senet
        tyokalut = new Tyokalut(nimi); 
        tknimet = new TyokalujenNimet(nimi);
    }
    


    /**
     * Tallentaa ryhm�n tiedot tiedostoon
     * @throws SailoException jos tallennuksessa ilmenee ongelmia
     */
    public void tallenna() throws SailoException {
        return;
    }
    
    /**
     * Korvaa j�senen tiedot muutetuilla tiedoilla; jos j�sent� ei l�ydy, lis�t��n uusi
     * @param jasen korvattava tai lis�tt�v� j�sen
     * @throws SailoException jos muokkaus/lis�ys ei onnistu
     */
    public void korvaaTaiLisaa(Jasen jasen) throws SailoException{
        jasenet.korvaaTaiLisaa(jasen);
        
    }
    
    /**
     * Korvaa ty�kalun tiedot muutetuilla tiedoilla; jos ty�kalua ei l�ydy, lis�t��n uusi
     * Asetetaan samalla uusi ty�kalunimi, mik�li tarpeen
     * @param tyok korvattava tai lis�tt�v� ty�kalu
     * @throws SailoException jos muokkaus/lis�ys ei onnistu
     */
    public void korvaaTaiLisaa(Tyokalu tyok) throws SailoException {
        tyokalut.korvaaTaiLisaa(tyok);    
    }
    
    /**
     * Haetaan hakuehdon t�ytt�v�t j�senet
     * @param hakuehto hakuehto
     * @param k kentt� josta hakuehdolla haetaan
     * @return j�senet, jotka t�ytt�v�t hakuehdon
     * @throws SailoException jos ongelmia
     */
    public Collection<Jasen> etsi(String hakuehto, int k) throws SailoException {
        Collection<Jasen> loydetyt = new ArrayList<Jasen>();
        if (k==4)  //kentt� 4 = ty�kalu hakuehtona
            return etsiTyokalu(hakuehto);
        try {
            loydetyt = jasenet.etsi(hakuehto, k);
        } catch (SailoException e) {
            System.err.println("Virhe: " + e);
        } 
        return loydetyt;
    }

    /**
     * Etsii hakuehtoa (=ty�kalun nimi) vastaavia ty�kaluja omistavat j�senet 
     * @param hakuehto hakuehto
     * @return j�senet, joilla on hakuehtoa vastaavia ty�kaluja
     * @throws SailoException jos ongelmia
     */
    public Collection<Jasen> etsiTyokalu(String hakuehto) throws SailoException {
        String ehto = ""; //jos ei hakuehtoa, haetaan kaikki j�senet
        kanta = alustaKanta("fuksit17"); //
        if (hakuehto != null && hakuehto.length() > 0) ehto = "*" + hakuehto.substring(1, hakuehto.length()-1) + "*"; //korvataan SQL-jokerimerkit siten, ett� Wildchars-luokka tunnistaa
        try ( Connection con = kanta.annaKantaYhteys();
                PreparedStatement sql = con.prepareStatement("SELECT * FROM Jasenet") ) {
              ArrayList<Jasen> loytyneet = new ArrayList<Jasen>();
              try ( ResultSet tul = sql.executeQuery() ) { //tuloslista on nyt lista ty�kalunimi�
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
     * Testip��ohjelma ryhm�lle
     * @param args ei k�yt�ss�
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


            System.out.println("======= Ryhm�n testi =========");
            int i = 0;

            for (Jasen jasen : ryhma.etsi("", -1)) {
                System.out.println("J�sen paikassa: " + i);
                jasen.tulosta(System.out);
                System.out.println("J�senen ty�kalut (nimin�): ");
                List<Tyokalu> tyokalulista = ryhma.annaTyokalut(jasen);
                List<TyokalunNimi> tyoknimet = ryhma.annaTkNimet(jasen);
                /*for (int k = 0; k<tyoknimet.size(); k++) {
                    */
                for (TyokalunNimi tknimi : tyoknimet) {
                    String nimi = tknimi.getNimi();
                    //
                    System.out.println(nimi);
                    
                }

                System.out.println("J�senen ty�kalujen tiedot: ");
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
