package ryhma;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Ty�kalun nimi-luokka
 * @author miemkoiv
 * @version 8.3.2017
 */
public class TyokalunNimi {
    private int tunnusNro;
    private String nimi ="";
    
    private static int seuraavaNro = 1;
    
    /**
     * Alustetaan ty�kalun nimitiedot
     */
    public TyokalunNimi() {
        //
    }
    
    
    
    /**
     * Ty�kalun nimitietomuodostaja
     * @param nimi annettu nimi
     */
    public TyokalunNimi(String nimi) {
        this.nimi = nimi;
    }
    
    /**
     * Antaa tietokannan luontilausekkeen ty�kalunimitaululle
     * @return ty�kalunimitaulun luontilauseke
     */
    public String annaLuontiLauseke()  {
        return "CREATE TABLE TyokaluNimet (" +
                "nimiID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "tknimi VARCHAR(255)" +
                ")";
    }
    
    /**
     * Antaa lausekkeen ty�kalunimen lis��miseksi
     * @param con tietokantayhteys
     * @return ty�kalunimen lis�yslauseke
     * @throws SQLException Jos lausekkeen luomisessa on ongelmia
     */
    public PreparedStatement annaLisaysLauseke(Connection con)
            throws SQLException {
        PreparedStatement sql = con.prepareStatement(
                "INSERT INTO TyokaluNimet (nimiID, tknimi) VALUES (?, ?)");
        if ( tunnusNro != 0 ) sql.setInt(1, tunnusNro); else sql.setString(1, null);
        sql.setString(2, nimi);
        
        return sql;
    }
    
    
    /**
     * Rekister�i ty�kalun nimen
     * @return ty�kalun nimen tunnusnumero
     * * @example
     * <pre name="test">
     *  TyokalunNimi vasara = new TyokalunNimi();
     *  vasara.getTunnusNro() === 0;
     *  vasara.rekisteroi();
     *  vasara.getTunnusNro() === 1;
     *  vasara.vastaaVasara();
     *  vasara.getNimi() === "vasara";
     *  TyokalunNimi vasara2 = new TyokalunNimi("vasara");
     *  vasara2.getNimi() === "vasara";
     *  vasara2.getTunnusNro() === 0;
     *  vasara2.rekisteroi();
     *  vasara2.getTunnusNro() === 2;
     * </pre> 
     */
    public int rekisteroi() {
        if (tunnusNro != 0) return tunnusNro;
        tunnusNro = seuraavaNro;
        seuraavaNro++;
        return tunnusNro;
    }
    
    /**
     * Palauttaa ty�kalun nimen tunnusnumeron
     * @return ty�kalun nimen tunnusnumero
     */
    public int getTunnusNro() {
        return tunnusNro;
    }
    
    /**
     * Asettaa ty�kalun nimen tunnusnumeron
     * @param nro asetettava ty�kalunimen tunnusnumero
     */
    public void setTunnusNro(int nro) {
        tunnusNro = nro;
        if (seuraavaNro <= tunnusNro) seuraavaNro = tunnusNro + 1;
    }
    
    /**
     * Palauttaa ty�kalun nimen nimitekstin 
     * @return ty�kalun nimiteksti
     */
    public String getNimi() {
        return nimi;
    }
    
      
    /**
     * Tulostaa ty�kalun nimi -olion tiedot
     * @param out tulostettava
     */
    public void tulosta(PrintStream out) {
        out.println(tunnusNro + " " + nimi);  
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
     * Selvitt�� ty�kalun nimen tiedot |-merkill� erotellusta merkkijonosta 
     * @param rivi merkkijono, josta erotellaan ty�kalun nimitiedot
     * <pre name="test">
     *  TyokalunNimi vasara = new TyokalunNimi();
     *  vasara.parse("1     | vasara ");
     *  vasara.getTunnusNro() === 1;
     *  vasara.toString() === "1 | vasara";
     * </pre>
     */
    public void parse(String rivi) {
        StringBuffer sb = new StringBuffer(rivi);
        setTunnusNro(Mjonot.erota(sb,'|',getTunnusNro())); //asettaa tunnusnumeron oikein
        nimi = Mjonot.erota(sb,'|', nimi);  
    }
    
    /**
     * Otetaan tiedot ResultSetist�
     * @param tulokset mist� tiedot otetaan
     * @throws SQLException Jos jokin menee vikaan
     */
    public void parse(ResultSet tulokset) throws SQLException {
        setTunnusNro(tulokset.getInt("nimiID"));
        nimi = tulokset.getString("tknimi");
    }
    
    /**
     * Palauttaa ty�kalun nimitiedot | -erotettuna merkkijonona
     * @return ty�kalun nimitiedot |-merkill� erotettuna
     */
    @Override
    public String toString() {
        return tunnusNro + " | " + nimi;
    }
    
    /**
     * Apumetodi, joka antaa ty�kalulle nimitiedoksi "vasara"
     */
    public void vastaaVasara() {
        nimi = "vasara";
    }
    
    /**
     * Asetetaan ty�kalun nimi
     * @param teksti asetettava ty�kalun nimi
     */
    public void setNimi(String teksti) {
        nimi = teksti;
    }

    /**
     * Palauttaa kunkin kent�n selosteen
     * @param k kent�n numero
     * @return kent�n seloste
     */
    public String getKentta(int k) {
        switch (k) {
        case 0: return "nimiID"; 
        case 1: return "tknimi";
        default: return "";
        }
    }
    
    /**
     * Testip��ohjelma Ty�kalun nimi-luokalle
     * @param args ei k�yt�ss�
     */
    public static void main(String[] args) {
        TyokalunNimi eiRekVasara = new TyokalunNimi("vasara");
        TyokalunNimi vasara = new TyokalunNimi();
        vasara.rekisteroi();
        vasara.vastaaVasara();
        TyokalunNimi ruuvari = new TyokalunNimi("ruuvari");
        ruuvari.rekisteroi();
        
        eiRekVasara.tulosta(System.out);
        vasara.tulosta(System.out);
        ruuvari.tulosta(System.out);
        
    }

}


