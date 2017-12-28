package ryhma;

import java.io.*;
import java.sql.*;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Ty�kalu-luokka
 * @author miemkoiv
 * @version 3.3.2017
 *
 */
public class Tyokalu {
    private int tunnusNro;
    private int jasenNro;
    private int tyokaluNro;
    private String lisatiedot = ""; 

    private static int seuraavaNro = 1; //staattinen jotta kaikki oliot p��see t�h�n k�siksi
    
    
    /**
    * Alustetaan ty�kalu
    */
    public Tyokalu() {
        //
    }
    
    /**
     * Antaa tietokannan luontilausekkeen ty�kalutaululle
     * @return ty�kalutaulun luontilauseke
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
     * Antaa lausekkeen ty�kalun lis��miseksi
     * @param con tietokantayhteys
     * @return ty�kalun lis�yslauseke
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
     * Palauttaa poistolausekkeen ty�kalun poistamiseksi
     * @param yht tietokantayhteys
     * @throws SQLException jos lausekkeen luonti ei onnistu
     * @return ty�kalun poistolauseke
     */
    public PreparedStatement annaPoistoLauseke(Connection yht) throws SQLException {
         PreparedStatement sql = yht.prepareStatement("DELETE FROM Tyokalut WHERE " +
                 "tyokaluID LIKE ?");
                 sql.setInt(1,tunnusNro);
                 return sql;
     }
    
    /**
     * Palauttaa p�ivityslausekkeen ty�kalun tietojen muokkaamiseksi
     * @param yht tietokantayhteys
     * @throws SQLException jos lausekkeen luonti ei onnistu
     * @return ty�kalun p�ivityslauseke
     */
    public PreparedStatement annaPaivitysLauseke(Connection yht) throws SQLException {
         PreparedStatement sql = yht.prepareStatement("UPDATE Tyokalut SET jasenID=?, nimiID=?,lisatiedot=? WHERE " +
                 "tyokaluID =?");
                 sql.setInt(1, jasenNro);
                 sql.setInt(2, tyokaluNro);
                 sql.setString(3, lisatiedot);
                 if (tunnusNro != 0) sql.setInt(4,tunnusNro); else sql.setString(4,null); //tarkistetaan onko ty�kalu jo rekister�ity ja muutetaan sen j�senen tietoja; jos ei, lis�t��n uutena (asetetaan null, jolloin autoincrement antaa numeron)
                 return sql;
     }
    
     /**
     * Alustetaan tietyn j�senen ty�kalu  
     * @param jasenNro j�senen tunnusnumero 
     */
     public Tyokalu(int jasenNro) {
         this.jasenNro = jasenNro;
     }
     
     /**
      * Rekister�i ty�kalulle yksil�llisen tunnusnumeron
      * @return ty�kalulle annettu yksil�llinen tunnusnumero
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
      * Tarkistetaan onko id muuttunut lis�yksess�
      * @param rs lis�yslauseen ResultSet
      * @throws SQLException jos tulee jotakin vikaa
      */
     public void tarkistaId(ResultSet rs) throws SQLException {
         if ( !rs.next() ) return;
         int id = rs.getInt(1);
         if ( id == tunnusNro ) return;
         setTunnusNro(id);
     }
     
     /**
      * Selvitt�� ty�kalun tiedot |-merkill� erotellusta merkkijonosta 
      * @param rivi merkkijono, josta erotellaan ty�kalun tiedot
      * <pre name="test">
      *  Tyokalu ruuvari = new Tyokalu();
      *  ruuvari.parse("2  | 1  | 2  | pieni t�htip�� ");
      *  ruuvari.getTunnusNro() === 2;
      *  ruuvari.getTyokaluNro() === 2;
      *  ruuvari.toString() === "2 | 1 | 2 | pieni t�htip��";
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
      * Otetaan tiedot ResultSetist�
      * @param tulokset mist� tiedot otetaan
      * @throws SQLException Jos jokin menee vikaan
      */
     public void parse(ResultSet tulokset) throws SQLException {
         setTunnusNro(tulokset.getInt("tyokaluID"));
         jasenNro = tulokset.getInt("jasenID");
         tyokaluNro = tulokset.getInt("nimiID");
         lisatiedot = tulokset.getString("lisatiedot");
     }

     
     /**
      * Palauttaa ty�kalun tiedot | -erotettuna merkkijonona
      * @return ty�kalun tiedot |-merkill� erotettuna
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
     * Palauttaa ty�kalun tunnusnumeron
     * @return ty�kalun tunnusnumero
     */
    public int getTunnusNro() {
        return tunnusNro;
    }
    
    /**
     * Asettaa ty�kalun tunnusnumeron
     * @param nro asetettava ty�kalun tunnusnumero
     */
    public void setTunnusNro(int nro) {
        tunnusNro = nro;
        if (seuraavaNro <= tunnusNro) seuraavaNro = tunnusNro + 1;
    }
    
    /**
     * Palauttaa ty�kalun nimen tunnusnumeron
     * @return ty�kalun nimen tunnusnumero
     */
    public int getTyokaluNro() {
        return tyokaluNro;
    }
    
    /**
     * Asettaa ty�kalunumeron (eli ty�kalun nimen tunnusnumeron)
     * @param tkNro asetettava tyokaluNro
     */
    public void setTyokaluNro(int tkNro) {
        tyokaluNro = tkNro;
    }
    
    
    /**
     * Palauttaa ty�kalun omistavan j�senen tunnusnumeron
     * @return j�senen tunnusnumero
     */
    public int getJasenNro() {
        return jasenNro;
    }
    
    /**
     * Palauttaa ty�kalun lis�tiedot
     * @return ty�kalun lis�tiedot
     */
    public String getLisatiedot() {
        return lisatiedot;
    }
    
    /**
     * Palauttaa ty�kalun tietokenttien lukum��r�n
     * @return ty�kalun tietokenttien lkm
     */
    public int getKenttia() {
        return 4;
    }
    
    /**
     * Palauttaa ty�kalun tietokentist� ensimm�isen mielenkiintoisen (2, koska id:it� ei haluta n�ytt��)
     * @return ensimm�isen ty�kalun tietokent�n indeksi
     */
    public int ekaKentta() {
        return 2;
    }
    
    /**
     * Palauttaa kunkin kent�n selosteen
     * @param k kent�n numero
     * @return kent�n seloste
     */
    public String getKentta(int k) {
        switch (k) {
        case 0: return "tyokaluID"; 
        case 1: return "jasenID"; 
        case 2: return "Ty�kalu"; //nimiID
        case 3: return "Lis�tiedot";
        default: return "";
        }
    }
    
    
    /**
     * Palauttaa ty�kalun tietyn tiedon (esim. nimen)
     * @param k kent�n numero
     * @return kyseisen ty�kalun tiedot kyseisess� kent�ss�
     */
    public String anna(int k) {
        switch (k) {
        case 0: return ""+getTunnusNro();   // lainausmerkit muuttaa intin Stringiksi
        case 1: return ""+getJasenNro();    // n�m� tarvitaan toString-metodia varten
        case 2: return ""+getTyokaluNro();
        case 3: return getLisatiedot();
        default: return "";
        }
    }
    
    /**
     * Asettaa ty�kalun tietokent�n k arvoksi tuodun merkkijonon
     * @param k kent�n id
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
            setTunnusNro(Mjonot.erota(sb,'&', getTunnusNro())); //merkki voi olla mik� tahansa
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
     * Apumetodi, joka lis�� annetulle j�senelle (j�senid:ll�) ty�kalun vasara
     * (sovitaan, ett� vasara = tyokalunNimi-luokassa id=1)
     * @param nro viite j�seneen, jolle ty�kalu kuuluu
     */
    public void vastaaVasara(int nro) {
        jasenNro = nro;
        tyokaluNro = 1;
        lisatiedot = "rautaa";
    }
    
    /**
     * tulostaa ty�kalun tiedot (ei j�sennumeroa)
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
     * Testip��ohjelma Ty�kalu-luokalle
     * @param args ei k�yt�ss�
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

