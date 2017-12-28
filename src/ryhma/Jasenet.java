package ryhma;


import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


import static ryhma.Kanta.alustaKanta;

/**
 * J‰senet-luokka
 * @author miemkoiv
 * @version 21.2.2017
 * Lis‰tty j‰sentaulukon koon kasvatus
 * @version 16.3.2017
 * Lis‰tty tiedostosta luku
 * @version 24.3.2017
 * Lis‰tty tiedostoon kirjoittaminen
 * @version 5.4.2017
 */
public class Jasenet {
    /*
     * Alustuksia ja puhdistuksia testej‰ varten
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
    
    private Kanta kanta;
    private static Jasen apujasen = new Jasen();
    private int lkm;

    /**
     * Tarkistetaan, ett‰ kannassa on j‰senten tarvitsema taulu
     * @param nimi tietokannan nimi
     * @throws SailoException jos tapahtuu virhe
     */
    public Jasenet(String nimi) throws SailoException {
        kanta = alustaKanta(nimi);
        try (Connection con = kanta.annaKantaYhteys() ) {
            DatabaseMetaData meta = con.getMetaData(); //metadatasta saadaan tiedot
            try (ResultSet taulu = meta.getTables(null, null, "Jasenet", null)) {
                if (!taulu.next() ) {
                    //jos taulua ei ole, luodaan se
                    try (PreparedStatement sql = con.prepareStatement(apujasen.annaLuontilauseke())) {
                        sql.execute();
                    }
                }
            }
        } catch (SQLException e) {
            throw new SailoException("Ongelma tietokannan kanssa: " + e.getMessage());
        }
    }

    /**
     * Tallennetaan muutokset tiedostoon
     * (ei k‰ytˆss‰ tietokannassa)
     */
    public void tallenna() {
        return;
    }

    /**
     * Lis‰t‰‰n j‰sen
     * @param jasen j‰sen joka lis‰t‰‰n
     * @throws SailoException jos ongelma tietokannan kanssa
     * @example
     * <pre name="test">
     * #THROWS SailoException 
     * 
     * Collection<Jasen> loytyneet = jasenet.etsi("", 1);
     * loytyneet.size() === 0;
     * 
     * Jasen aku1 = new Jasen(), aku2 = new Jasen();
     * jasenet.lisaa(aku1); 
     * jasenet.lisaa(aku2); 
     *  
     * loytyneet = jasenet.etsi("", 1);
     * loytyneet.size() === 2;
     * 
     * jasenet.lisaa(aku1); #THROWS SailoException //ei voi lis‰t‰ samaa j‰sent‰ uudelleen
     * Jasen aku3 = new Jasen(); Jasen aku4 = new Jasen(); Jasen aku5 = new Jasen();
     * jasenet.lisaa(aku3); 
     * jasenet.lisaa(aku4); 
     * jasenet.lisaa(aku5); 

     * loytyneet = jasenet.etsi("", 1);
     * loytyneet.size() === 5;
     * Iterator<Jasen> i = loytyneet.iterator();
     * i.next() === aku1;
     * i.next() === aku2;
     * i.next() === aku3;
     * </pre>
     */
    public void lisaa(Jasen jasen) throws SailoException {
        try ( Connection con = kanta.annaKantaYhteys(); PreparedStatement sql = jasen.annaLisayslauseke(con)) {
            sql.executeUpdate();
            lkm++;
            try ( ResultSet tul = sql.getGeneratedKeys() ) {
                jasen.tarkistaId(tul);
            }
        } catch (SQLException e) {
            throw new SailoException("Ongelma tietokantaan lis‰‰misess‰: " + e.getMessage());
        }
    }
    
    /**
     * Palauttaa j‰senten lukum‰‰r‰n
     * @return j‰senten lukum‰‰r‰
     */
    public int getLkm() {
        return lkm;
    }
    
    
    /**
     * Korvataan j‰senen tiedot tai lis‰t‰‰n mik‰li kyseess‰ uusi j‰sen
     * @param jasen j‰sen, jonka tiedot korvataan tai joka lis‰t‰‰n
     * @throws SailoException jos ongelmia
     */
    public void korvaaTaiLisaa(Jasen jasen) throws SailoException {
        try ( Connection con = kanta.annaKantaYhteys(); PreparedStatement sql = jasen.annaPaivitysLauseke(con)) {
            sql.executeUpdate();
        } catch (SQLException e) {
            throw new SailoException("Ongelma tietokantaa muokattaessa: " + e.getMessage());
        }
        /*int id = jasen.getTunnusNro();
        try {
            for (Jasen jasenKohdalla : etsi("",-1)) { //kentt‰ -1 == lista kaikista j‰senist‰
                if (jasenKohdalla.getTunnusNro() == id) {
                    jasenKohdalla = jasen;
                    //muutettu = true;
                    return;
                }
                lisaa(jasen); // jos j‰sent‰ ei lˆytynyt, lis‰t‰‰n uutena j‰senen‰
                              // Huom. t‰t‰ ei pit‰isi tapahtua l‰hes koskaan, se on vain varalla
            }
        } catch (SailoException e) {
            System.err.println("Virhe: " + e.getMessage());
        }
        */
    }
    

    /**
     * Hakee hakuehdon t‰ytt‰v‰t j‰senet
     * @param hakuehto hakuehto
     * @param k kentt‰ josta haetaan
     * @return hakuehdon t‰ytt‰v‰t j‰senet
     * @throws SailoException jos tietokannan kanssa ongelmia
     * <pre name="test">
     * #THROWS SailoException
     *  
     *  Jasen aku1 = new Jasen(); aku1.taytaAkuAnkka(); 
     *  Jasen aku2 = new Jasen(); aku2.taytaAkuAnkka(); 
     *  jasenet.lisaa(aku1);
     *  jasenet.lisaa(aku2);
     *  jasenet.lisaa(aku2);  #THROWS SailoException  // ei saa lis‰t‰ sama id:t‰ uudelleen
     *  Collection<Jasen> loydetyt;
     *  loydetyt = jasenet.etsi("Ankka",1);
     *  loydetyt.size() === 2;
     *  loydetyt.iterator().next() === aku1;
     *  loydetyt = jasenet.etsi(aku1.getNimi(), 1);
     *  loydetyt.size() === 2; //koska nimi on sama, lˆydet‰‰n kaksi
     *  Iterator<Jasen> i = loydetyt.iterator();
     *  i.next() === aku1;
     *  i.next() === aku2;
     *  loydetyt = jasenet.etsi(""+aku2.getTunnusNro(), 0);
     *  loydetyt.size() === 1;
     *  loydetyt.iterator().next() === aku2;
     *  loydetyt = jasenet.etsi("",9); #THROWS SailoException
     *  ftied.delete();
     * </pre>
     */
    public Collection<Jasen> etsi(String hakuehto, int k) throws SailoException {
        String ehto = hakuehto;
        String kentta = apujasen.getKentta(k);
        if (k < 0) { //jos ei hakuehtoa, haetaan kaikki j‰senet... 
            kentta = apujasen.getKentta(0); //ja kaikki tiedot (alkaen id:st‰)
            ehto = "";
        }
        try(Connection yht = kanta.annaKantaYhteys();
                PreparedStatement sql = yht.prepareStatement("SELECT * FROM Jasenet WHERE " + kentta + " LIKE ?") ) {
            List<Jasen> loydetyt = new ArrayList<Jasen>();
            sql.setString(1, "%" + ehto + "%");
            try (ResultSet tulokset = sql.executeQuery()) {
                while (tulokset.next()) {
                    Jasen j = new Jasen();
                    j.parse(tulokset);
                    loydetyt.add(j); //lis‰t‰‰n j‰senen tiedot listaan
                }
            }
            Collections.sort(loydetyt, new Jasen.Vertailija(k)); //luodaan vertailija-olio sis‰luokaksi j‰senelle
            return loydetyt;
        } catch (SQLException e) {
            throw new SailoException("Ongelma tietokannasta hakiessa: " + e.getMessage());
        }
    }
    
    /**
     * Testip‰‰ohjelma J‰senet-luokalle
     * @param args ei k‰ytˆss‰
     */
    public static void main(String[] args) {
        try {
            new File("kokeilu.db").delete();
            Jasenet jasenet = new Jasenet("kokeilu");

            Jasen aku = new Jasen();
            Jasen aku2 = new Jasen();
            Jasen aku3 = new Jasen();
            Jasen aku4 = new Jasen();
            Jasen aku5 = new Jasen();
            Jasen aku6 = new Jasen();
            aku.rekisteroi();
            aku.taytaAkuAnkka();
            aku2.rekisteroi();
            aku2.taytaAkuAnkka();
            aku3.rekisteroi();
            aku3.taytaAkuAnkka();
            aku4.rekisteroi();
            aku4.taytaAkuAnkka();
            aku5.rekisteroi();
            aku5.taytaAkuAnkka();
            aku6.rekisteroi();
            aku6.taytaAkuAnkka();

            jasenet.lisaa(aku);
            jasenet.lisaa(aku2);
            jasenet.lisaa(aku3);
            jasenet.lisaa(aku4);
            jasenet.lisaa(aku5);
            jasenet.lisaa(aku6);

            System.out.println("======= testi =========");
            int i = 0;
            for (Jasen jasenKohdalla : jasenet.etsi("", -1)) {
                System.out.println("J‰sen paikassa: " + i);
                jasenKohdalla.tulosta(System.out);
                i++;
            }
            System.out.println("J‰seni‰ yhteens‰: " + jasenet.getLkm());
            new File("kokeilu.db").delete();
        } catch (SailoException e) {
            System.err.println("Virhe: " +e.getMessage());
        }

    }

}
