package ryhma;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ryhma.Kanta.alustaKanta;

/**
 * Työkalut-luokka 
 * @author miemkoiv
 * @version 3.3.2017
 * lisätty tiedostosta luku ja tiedostoon kirjoitus
 * @version 6.4.2017
 */
public class Tyokalut {
    /*
     * Alustuksia ja puhdistuksia testiä varten
     * @example
     * <pre name="testJAVA">
     * #import java.io.*;
     * #import java.util.*;
     * 
     * private Tyokalut kalut;
     * private String tiedNimi;
     * private File ftied;
     * 
     * @Before
     * public void alusta() throws SailoException { 
     *    tiedNimi = "testikelmit";
     *    ftied = new File(tiedNimi+".db");
     *    ftied.delete();
     *    kalut = new Tyokalut(tiedNimi);
     * }   
     *
     * @After
     * public void siivoa() {
     *    ftied.delete();
     * }   
     * </pre>
     */ 
    
    //private ArrayList<Tyokalu> alkiot = new ArrayList<Tyokalu>();
    //private boolean muutettu = false;
    //private String tiedostonNimi = "tyokalut"; //oletusnimi nykyisen toiminnan kannalta turha
    private static Tyokalu aputyokalu = new Tyokalu();
    private Kanta kanta;
    private int lkm;
    
    /**
     * Tarkistetaan että kannassa työkalutaulu
     * @param nimi tietokannan nimi
     * @throws SailoException jos tapahtuu virhe
     */
    public Tyokalut(String nimi) throws SailoException {
        kanta = alustaKanta(nimi);
        try ( Connection con = kanta.annaKantaYhteys() ) {
            DatabaseMetaData meta = con.getMetaData();
            try ( ResultSet taulu = meta.getTables(null, null, "Tyokalut", null) ) {
                if ( !taulu.next() ) {
                    // Luodaan Tyokalut taulu
                    try ( PreparedStatement sql = con.prepareStatement(aputyokalu.annaLuontiLauseke()) ) {
                        sql.execute();
                    }
                }
            }
                
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * Lisätään työkalu 
     * @param tyokalu lisättävä työkalu
     * @throws SailoException jos ongelmia ilmenee
     */
    public void lisaa(Tyokalu tyokalu) throws SailoException {
        try ( Connection con = kanta.annaKantaYhteys(); PreparedStatement sql = tyokalu.annaLisaysLauseke(con) ) {
            sql.executeUpdate();
            lkm++;
            try ( ResultSet rs = sql.getGeneratedKeys() ) {
                tyokalu.tarkistaId(rs);
             }   
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * Palauttaa työkalujen lukumäärän
     * @return työkalujen lukumäärä
     */
    public int getLkm() {
        return lkm;
    }
    
    

    /**
     * Haetaan tietyn jäsenen omistamat työkalut
     * @param jasenNro jäsen jonka työkalut haetaan
     * @return jäsenen työkalut listana
     * @throws SailoException jos ongelmia
     * @example* <pre name="test">
     * #THROWS SailoException
     *  
     *  Tyokalu vasara21 = new Tyokalu(2); vasara21.vastaaVasara(2); kalut.lisaa(vasara21);
     *  Tyokalu vasara11 = new Tyokalu(1); vasara11.vastaaVasara(1); kalut.lisaa(vasara11);
     *  Tyokalu vasara22 = new Tyokalu(2); vasara22.vastaaVasara(2); kalut.lisaa(vasara22);
     *  Tyokalu vasara12 = new Tyokalu(1); vasara12.vastaaVasara(1); kalut.lisaa(vasara12);
     *  Tyokalu vasara23 = new Tyokalu(2); vasara23.vastaaVasara(2); kalut.lisaa(vasara23);
     *  Tyokalu vasara51 = new Tyokalu(5); vasara51.vastaaVasara(5); kalut.lisaa(vasara51);
     *  
     *  
     *  List<Tyokalu> loytyneet;
     *  loytyneet = kalut.annaTyokalut(3);
     *  loytyneet.size() === 0; 
     *  loytyneet = kalut.annaTyokalut(1);
     *  loytyneet.size() === 2; 
     *  
     *  loytyneet.get(0) === vasara11;
     *  loytyneet.get(1) === vasara12;
     *  
     *  loytyneet = kalut.annaTyokalut(5);
     *  loytyneet.size() === 1; 
     *  loytyneet.get(0) === vasara51;
     * </pre> 
     */
    public List<Tyokalu> annaTyokalut(int jasenNro) throws SailoException {
        List<Tyokalu> loydetyt = new ArrayList<Tyokalu>();
        
        try ( Connection con = kanta.annaKantaYhteys();
              PreparedStatement sql = con.prepareStatement("SELECT * FROM Tyokalut WHERE jasenID =?")
                ) {
            sql.setInt(1, jasenNro);
            try ( ResultSet tulokset = sql.executeQuery() )  {
                while ( tulokset.next() ) {
                    Tyokalu tyok = new Tyokalu();
                    tyok.parse(tulokset);
                    loydetyt.add(tyok);
                }
            }
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
        return loydetyt;
    }
    
    
    /**
     * Tallennetaan muutokset tiedostoon
     * 
     * @throws SailoException jos tapahtuu jokin virhe
     */
    public void tallenna() throws SailoException {
        return;
    }
    
    /**
     * Korvataan työkalun tiedot tai lisätään mikäli kyseessä uusi työkalu
     * @param tyok työkalu, jonka tiedot korvataan tai joka lisätään
     * @throws SailoException jos ongelmia
     */
    public void korvaaTaiLisaa(Tyokalu tyok) throws SailoException {
        try ( Connection con = kanta.annaKantaYhteys(); PreparedStatement sql = tyok.annaPaivitysLauseke(con)) {
            sql.executeUpdate();
            /*try ( ResultSet tul = sql.getGeneratedKeys() ) {
                tyok.tarkistaId(tul);
            }*/
        } catch (SQLException e) {
            throw new SailoException("Ongelma tietokantaa muokattaessa: " + e.getMessage());
        }
    }
    
    /**
     * Poistaa työkalun
     * (testit annaTyoKalut-metodissa)
     * @param tyok poistettava työkalu
     * @throws SailoException jos ongelmia poistamisessa
     */
    public void poista(Tyokalu tyok) throws SailoException {
        try ( Connection con = kanta.annaKantaYhteys(); PreparedStatement sql = tyok.annaPoistoLauseke(con) ) {
            sql.executeUpdate();
            lkm--; //vähennetään lukumäärää
            try ( ResultSet rs = sql.getGeneratedKeys() ) {
                tyok.tarkistaId(rs);
             }   
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannasta poistossa:" + e.getMessage());
        }
    }

    /**
     * Hakee hakuehdon täyttävät työkalut
     * (ei välttämättä käytetä?)
     * @param hakuehto hakuehto
     * @param k kenttä josta haetaan
     * @return hakuehdon täyttävät työkalut
     * @throws SailoException jos tietokannan kanssa ongelmia
     * <pre name="test">
     * #THROWS SailoException
     *  
     *  Tyokalu vasara = new Tyokalu(); vasara.vastaaVasara(1); 
     *  Tyokalu vasara2 = new Tyokalu(); vasara2.vastaaVasara(1); 
     *  kalut.lisaa(vasara);
     *  kalut.lisaa(vasara2);
     *  kalut.lisaa(vasara2);  #THROWS SailoException  // ei saa lisätä sama id:tä uudelleen
     *  Collection<Tyokalu> loydetyt;
     *  loydetyt = kalut.etsi("",-1);
     *  loydetyt.size() === 2;
     *  loydetyt.iterator().next() === vasara;
     *  loydetyt = kalut.etsi(""+vasara.getTyokaluNro(), 1);
     *  loydetyt.size() === 2; //koska nro on sama, löydetään kaksi
     *  Iterator<Tyokalu> i = loydetyt.iterator();
     *  i.next() === vasara;
     *  i.next() === vasara2;
     *  loydetyt = kalut.etsi(""+vasara2.getTunnusNro(), 0);
     *  loydetyt.size() === 1;
     *  loydetyt.iterator().next() === vasara2;
     *  loydetyt = kalut.etsi("",9); #THROWS SailoException
     *  ftied.delete();
     * </pre>
     */
    public Collection<Tyokalu> etsi(String hakuehto, int k) throws SailoException {
        String ehto = hakuehto;
        String kentta = aputyokalu.getKentta(k);
        if (k < 0) { //jos ei hakuehtoa, haetaan kaikki työkalut... 
            kentta = aputyokalu.getKentta(0); //ja kaikki tiedot (alkaen id:stä)
            ehto = "";
        }
        try(Connection yht = kanta.annaKantaYhteys();
                PreparedStatement sql = yht.prepareStatement("SELECT * FROM Tyokalut WHERE " + kentta + " LIKE ?") ) {
            List<Tyokalu> loydetyt = new ArrayList<Tyokalu>();
            sql.setString(1, "%" + ehto + "%");
            try (ResultSet tulokset = sql.executeQuery()) {
                while (tulokset.next()) {
                    Tyokalu t = new Tyokalu();
                    t.parse(tulokset);
                    loydetyt.add(t); //lisätään työkalun tiedot listaan
                }
            }
            return loydetyt;
        } catch (SQLException e) {
            throw new SailoException("Ongelma tietokannasta hakiessa: " + e.getMessage());
        }
    }
    
    /**
     * Testipääohjelma Työkalut-luokalle
     * @param args ei käytössä
     */
    public static void main(String[] args) {
        try {
            new File("kokeilu.db").delete();
            Tyokalut tyokalut = new Tyokalut("kokeilu");
            Tyokalu vasara = new Tyokalu();
            vasara.rekisteroi();
            vasara.vastaaVasara(1); //lisätään vasara jäsenelle 1
            Tyokalu ruuvari = new Tyokalu(1); //lisätään vasara2 jäsenelle 1
            ruuvari.rekisteroi();
            Tyokalu ruuvari2 = new Tyokalu(2); // lisätään ruuvari2 jäsenelle 2
            ruuvari2.rekisteroi();
        
            tyokalut.lisaa(vasara);
            tyokalut.lisaa(ruuvari);
            tyokalut.lisaa(ruuvari2);

            System.out.println("========= Testataan Työkalut-luokkaa ==========");

            for (Tyokalu tyok : tyokalut.etsi("", -1)) {
                System.out.println("Omistaja: Jäsen #" + tyok.getJasenNro() + 
                        "\nTiedot (tunnusNro, nimitunnus, lisätiedot):"); //tulostaa työkalun omistajan, sitten työkalun tiedot
                tyok.tulosta(System.out);
            }
            new File("kokeilu.db").delete();
        } catch (SailoException e) {
            System.err.println("Virhe: " + e.getMessage());
        }
        
        
    }

}
