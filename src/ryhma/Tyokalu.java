package ryhma;

import java.io.*;
import java.sql.*;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Työkalu-luokka
 * @author miemkoiv
 * @version 3.3.2017
 *
 */
public class Tyokalu {
    private int tunnusNro;
    private int jasenNro;
    private int tyokaluNro;
    private String lisatiedot = ""; 

    private static int seuraavaNro = 1; //staattinen jotta kaikki oliot pääsee tähän käsiksi
    
    
    /**
    * Alustetaan työkalu
    */
    public Tyokalu() {
        //
    }
    
    /**
     * Antaa tietokannan luontilausekkeen työkalutaululle
     * @return työkalutaulun luontilauseke
     */
    public String annaLuontiLauseke()  {
        return "CREATE TABLE Tyokalut (" +
                "tyokaluID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "jasenID INTEGER, " +
                "nimiID INTEGER, " +
                "lisatiedot VARCHAR(255), " +
                "FOREIGN KEY (jasenID) REFERENCES Jasenet(jasenID), " +
                "FOREIGN KEY (nimiID) REFERENCES TkNimet(nimiID)" +
                ")";
    }
    
    /**
     * Antaa lausekkeen työkalun lisäämiseksi
     * @param con tietokantayhteys
     * @return työkalun lisäyslauseke
     * @throws SQLException Jos lausekkeen luomisessa on ongelmia
     */
    public PreparedStatement annaLisaysLauseke(Connection con)
            throws SQLException {
        PreparedStatement sql = con.prepareStatement(
                "INSERT INTO Tyokalut (tyokaluID, jasenID, nimiID, " +
                "lisatiedot) VALUES (?, ?, ?, ?)");
        if ( tunnusNro != 0 ) sql.setInt(1, tunnusNro); else sql.setString(1, null);
        sql.setInt(2, jasenNro);
        sql.setInt(3, tyokaluNro);
        sql.setString(4, lisatiedot);
        
        return sql;
    }
    
    /**
     * Palauttaa poistolausekkeen työkalun poistamiseksi
     * @param yht tietokantayhteys
     * @throws SQLException jos lausekkeen luonti ei onnistu
     * @return työkalun poistolauseke
     */
    public PreparedStatement annaPoistoLauseke(Connection yht) throws SQLException {
         PreparedStatement sql = yht.prepareStatement("DELETE FROM Tyokalut WHERE " +
                 "tyokaluID LIKE ?");
                 sql.setInt(1,tunnusNro);
                 return sql;
     }
    
    /**
     * Palauttaa päivityslausekkeen työkalun tietojen muokkaamiseksi
     * @param yht tietokantayhteys
     * @throws SQLException jos lausekkeen luonti ei onnistu
     * @return työkalun päivityslauseke
     */
    public PreparedStatement annaPaivitysLauseke(Connection yht) throws SQLException {
         PreparedStatement sql = yht.prepareStatement("UPDATE Tyokalut SET jasenID=?, nimiID=?,lisatiedot=? WHERE " +
                 "tyokaluID =?");
                 sql.setInt(1, jasenNro);
                 sql.setInt(2, tyokaluNro);
                 sql.setString(3, lisatiedot);
                 if (tunnusNro != 0) sql.setInt(4,tunnusNro); else sql.setString(4,null); //tarkistetaan onko työkalu jo rekisteröity ja muutetaan sen jäsenen tietoja; jos ei, lisätään uutena (asetetaan null, jolloin autoincrement antaa numeron)
                 return sql;
     }
    
     /**
     * Alustetaan tietyn jäsenen työkalu  
     * @param jasenNro jäsenen tunnusnumero 
     */
     public Tyokalu(int jasenNro) {
         this.jasenNro = jasenNro;
     }
     
     /**
      * Rekisteröi työkalulle yksilöllisen tunnusnumeron
      * @return työkalulle annettu yksilöllinen tunnusnumero
      * @example
      * <pre name="test">
      *  Tyokalu vasara = new Tyokalu();
      *  vasara.getTunnusNro() === 0;
      *  vasara.rekisteroi();
      *  vasara.getTunnusNro() === 3;
      *  Tyokalu ruuvari = new Tyokalu();
      *  ruuvari.rekisteroi();
      *  ruuvari.getTunnusNro() === 4;
      * </pre> 
      */
     public int rekisteroi() {
         if (tunnusNro != 0) return tunnusNro;
         tunnusNro = seuraavaNro;
         seuraavaNro++;
         return tunnusNro;
     }
     
     /**
      * Tarkistetaan onko id muuttunut lisäyksessä
      * @param rs lisäyslauseen ResultSet
      * @throws SQLException jos tulee jotakin vikaa
      */
     public void tarkistaId(ResultSet rs) throws SQLException {
         if ( !rs.next() ) return;
         int id = rs.getInt(1);
         if ( id == tunnusNro ) return;
         setTunnusNro(id);
     }
     
     /**
      * Selvittää työkalun tiedot |-merkillä erotellusta merkkijonosta 
      * @param rivi merkkijono, josta erotellaan työkalun tiedot
      * <pre name="test">
      *  Tyokalu ruuvari = new Tyokalu();
      *  ruuvari.parse("2  | 1  | 2  | pieni tähtipää ");
      *  ruuvari.getTunnusNro() === 2;
      *  ruuvari.getTyokaluNro() === 2;
      *  ruuvari.toString() === "2 | 1 | 2 | pieni tähtipää";
      * </pre>
      */
     public void parse(String rivi) {
         StringBuffer sb = new StringBuffer(rivi);
         setTunnusNro(Mjonot.erota(sb,'|',getTunnusNro())); //asettaa tunnusnumeron oikein
         jasenNro = Mjonot.erota(sb,'|', jasenNro);
         tyokaluNro = Mjonot.erota(sb,'|',tyokaluNro);
         lisatiedot = Mjonot.erota(sb,'|',lisatiedot);      
     }
     
     /**
      * Otetaan tiedot ResultSetistä
      * @param tulokset mistä tiedot otetaan
      * @throws SQLException Jos jokin menee vikaan
      */
     public void parse(ResultSet tulokset) throws SQLException {
         setTunnusNro(tulokset.getInt("tyokaluID"));
         jasenNro = tulokset.getInt("jasenID");
         tyokaluNro = tulokset.getInt("nimiID");
         lisatiedot = tulokset.getString("lisatiedot");
     }

     
     /**
      * Palauttaa työkalun tiedot | -erotettuna merkkijonona
      * @return työkalun tiedot |-merkillä erotettuna
      */
     @Override
     public String toString() {
         StringBuffer sb = new StringBuffer("");
         String erotin = "";
         for (int k = 0; k<getKenttia();k++) {
             sb.append(erotin);
             sb.append(anna(k));
             erotin = " | ";
         }
         return sb.toString();
     }
    
    /**
     * Palauttaa työkalun tunnusnumeron
     * @return työkalun tunnusnumero
     */
    public int getTunnusNro() {
        return tunnusNro;
    }
    
    /**
     * Asettaa työkalun tunnusnumeron
     * @param nro asetettava työkalun tunnusnumero
     */
    public void setTunnusNro(int nro) {
        tunnusNro = nro;
        if (seuraavaNro <= tunnusNro) seuraavaNro = tunnusNro + 1;
    }
    
    /**
     * Palauttaa työkalun nimen tunnusnumeron
     * @return työkalun nimen tunnusnumero
     */
    public int getTyokaluNro() {
        return tyokaluNro;
    }
    
    /**
     * Asettaa työkalunumeron (eli työkalun nimen tunnusnumeron)
     * @param tkNro asetettava tyokaluNro
     */
    public void setTyokaluNro(int tkNro) {
        tyokaluNro = tkNro;
    }
    
    
    /**
     * Palauttaa työkalun omistavan jäsenen tunnusnumeron
     * @return jäsenen tunnusnumero
     */
    public int getJasenNro() {
        return jasenNro;
    }
    
    /**
     * Palauttaa työkalun lisätiedot
     * @return työkalun lisätiedot
     */
    public String getLisatiedot() {
        return lisatiedot;
    }
    
    /**
     * Palauttaa työkalun tietokenttien lukumäärän
     * @return työkalun tietokenttien lkm
     */
    public int getKenttia() {
        return 4;
    }
    
    /**
     * Palauttaa työkalun tietokentistä ensimmäisen mielenkiintoisen (2, koska id:itä ei haluta näyttää)
     * @return ensimmäisen työkalun tietokentän indeksi
     */
    public int ekaKentta() {
        return 2;
    }
    
    /**
     * Palauttaa kunkin kentän selosteen
     * @param k kentän numero
     * @return kentän seloste
     */
    public String getKentta(int k) {
        switch (k) {
        case 0: return "tyokaluID"; 
        case 1: return "jasenID"; 
        case 2: return "Työkalu"; //nimiID
        case 3: return "Lisätiedot";
        default: return "";
        }
    }
    
    
    /**
     * Palauttaa työkalun tietyn tiedon (esim. nimen)
     * @param k kentän numero
     * @return kyseisen työkalun tiedot kyseisessä kentässä
     */
    public String anna(int k) {
        switch (k) {
        case 0: return ""+getTunnusNro();   // lainausmerkit muuttaa intin Stringiksi
        case 1: return ""+getJasenNro();    // nämä tarvitaan toString-metodia varten
        case 2: return ""+getTyokaluNro();
        case 3: return getLisatiedot();
        default: return "";
        }
    }
    
    /**
     * Asettaa työkalun tietokentän k arvoksi tuodun merkkijonon
     * @param k kentän id
     * @param s asetettava merkkijono
     * @return null jos tekstin asettaminen onnistuu, muuten virheilmoitus
     * <pre name="test">
     *  Tyokalu tyok = new Tyokalu();
     *  tyok.aseta(3, "Rautaa") === null;
     *  tyok.getLisatiedot() === "Rautaa";
     * </pre>
     */
    public String aseta(int k, String s) {
        String jono = s.trim();
        StringBuffer sb = new StringBuffer(jono);
        switch (k) {
        case 0:
            setTunnusNro(Mjonot.erota(sb,'&', getTunnusNro())); //merkki voi olla mikä tahansa
            return null;
        case 1:
            jasenNro = Mjonot.erota(sb,'&', getJasenNro());
            return null;
        case 2:
            setTyokaluNro(Mjonot.erota(sb,'&', getTyokaluNro()));
            return null;
        case 3:
            lisatiedot = jono;
            return null;
        default: return "";
        }

    }

    
    /**
     * Apumetodi, joka lisää annetulle jäsenelle (jäsenid:llä) työkalun vasara
     * (sovitaan, että vasara = tyokalunNimi-luokassa id=1)
     * @param nro viite jäseneen, jolle työkalu kuuluu
     */
    public void vastaaVasara(int nro) {
        jasenNro = nro;
        tyokaluNro = 1;
        lisatiedot = "rautaa";
    }
    
    /**
     * tulostaa työkalun tiedot (ei jäsennumeroa)
     * @param out tulostettava
     */
    public void tulosta(PrintStream out) {
        out.println(String.format("%01d",tunnusNro) + " " + String.format("%01d",tyokaluNro) + " " + lisatiedot);
        }
    
    @Override
    public boolean equals(Object obj) {
        return this.toString().equals(obj.toString());
    }
    

    @Override
    public int hashCode() {
        return tunnusNro;
    }
    
    /**
     * Testipääohjelma Työkalu-luokalle
     * @param args ei käytössä
     */
    public static void main(String[] args) {
        Tyokalu vasara = new Tyokalu();
        vasara.tulosta(System.out);
        vasara.rekisteroi();
        vasara.vastaaVasara(1);
        vasara.tulosta(System.out);
        Tyokalu ruuvari = new Tyokalu(3);
        ruuvari.rekisteroi();
        ruuvari.tulosta(System.out);    
    }

}

