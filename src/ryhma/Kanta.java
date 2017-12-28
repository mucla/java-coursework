package ryhma;

import java.sql.*;
import java.util.HashMap;

/**
 * Luokka SQLite-yhteyden muodostamiseksi
 * @author miemkoiv
 * @version 5.7.2017
 */
public class Kanta {
    private static HashMap<String, Kanta> kannat = new HashMap<String, Kanta>(); //Hashmap kannoista
    private String tiedostonNimi ="";
    
    /**
     * Alustetaan kanta
     * @param nimi tiedoston nimi
     */
    private Kanta(String nimi) {
        setTiedostonNimi(nimi);
    }
    
    /**
     * Alustetaan kantayhteys
     * @param nimi kannan nimi
     * @return k‰sitelt‰v‰ kanta
     */
    public static Kanta alustaKanta(String nimi) {
        if (kannat.containsKey(nimi)) return kannat.get(nimi); //jos kanta jo olemassa, haetaan se
        Kanta uusi = new Kanta(nimi); //luodaan uusi kanta
        kannat.put(nimi, uusi); //lis‰t‰‰n kanta HashMappiin, nimi avaimeksi
        return uusi;
    }
    
    /**
     * Asetetaan tietokannan nimi
     * @param nimi asetettava nimi
     */
    public void setTiedostonNimi(String nimi) {
        tiedostonNimi = nimi;
    }
    
    /**
     * Palauttaa tallennustiedoston nimen
     * @return tallennustiedoston nimi
     */
    public String getTiedostonNimi() {
        return tiedostonNimi + ".db";
    }
    
    /**
     * Antaa tietokantayhteyden
     * @return tietokantayhteys
     * @throws SQLException jos yhteyden avaamisessa virhe
     */
    public Connection annaKantaYhteys() throws SQLException {
        String sDriver = "org.sqlite.JDBC";
        try {
            Class.forName(sDriver);
        } catch (ClassNotFoundException e) {
            System.err.println("Virhe luokan " + sDriver + "lataamisessa: " + e.getMessage());
        }
        return DriverManager.getConnection("jdbc:sqlite:" + getTiedostonNimi());
        /*Connection con = DriverManager.getConnection("jdbc:sqlite:" + getTiedostonNimi());
        return con;*/
    }
}
