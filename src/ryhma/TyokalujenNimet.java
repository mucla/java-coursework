package ryhma;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import static ryhma.Kanta.alustaKanta;

/**
 * Ty�kalujen nimet-luokka
 * @author miemkoiv
 * @version 8.3.2017
 * lis�tty tiedostosta luku ja tiedostoon kirjoitus
 * @version 6.4.2017
 */
public class TyokalujenNimet {
    /*
     * Alustuksia ja puhdistuksia testi� varten
     * @example
     * <pre name="testJAVA">
     * #import java.io.*;
     * #import java.util.*;
     * 
     * private TyokalujenNimet kalunimet;
     * private String tiedNimi;
     * private File ftied;
     * 
     * @Before
     * public void alusta() throws SailoException { 
     *    tiedNimi = "testikelmit";
     *    ftied = new File(tiedNimi+".db");
     *    ftied.delete();
     *    kalunimet = new TyokalujenNimet(tiedNimi);
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
    private static TyokalunNimi aputknimi = new TyokalunNimi();
    private Kanta kanta;
    private int lkm;
    
    /**
     * Tarkistetaan ett� kannassa ty�kalutaulu
     * @param nimi tietokannan nimi
     * @throws SailoException jos tapahtuu virhe
     */
    public TyokalujenNimet(String nimi) throws SailoException {
        kanta = alustaKanta(nimi);
        try ( Connection con = kanta.annaKantaYhteys() ) {
            DatabaseMetaData meta = con.getMetaData();
            try ( ResultSet taulu = meta.getTables(null, null, "TyokaluNimet", null) ) {
                if ( !taulu.next() ) {
                    // Luodaan Tyokalut taulu
                    try ( PreparedStatement sql = con.prepareStatement(aputknimi.annaLuontiLauseke()) ) {
                        sql.execute();
                    }
                }
            }
                
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * Palauttaa ty�kalun tunnusnumeroa vastaavan nimen
     * @param tyokaluNro ty�kalun numero
     * @return ty�kalun nimi
     * @throws SailoException jos ongelmia
     * @example
     * <pre name="test">
     *  TyokalujenNimet tknimet = new TyokalujenNimet();
     *  TyokalunNimi vasara = new TyokalunNimi("vasara");
     *  vasara.getNimi() === "vasara";
     *  vasara.getTunnusNro() === 0;
     *  vasara.rekisteroi();
     *  vasara.getTunnusNro() === 1;
     *  TyokalunNimi vasara2 = new TyokalunNimi();
     *  vasara2.getTunnusNro() === 0;
     *  vasara2.rekisteroi();
     *  vasara2.getTunnusNro() === 2;
     *  tknimet.lisaa(vasara2);
     *  tknimet.getNimi(vasara2.getTunnusNro()) === "";
     * </pre>
     */
    public String getNimi(int tyokaluNro) throws SailoException {
        try ( Connection con = kanta.annaKantaYhteys();
              PreparedStatement sql = con.prepareStatement("SELECT * FROM TyokaluNimet WHERE nimiID = ?")
                ) {
            sql.setInt(1, tyokaluNro);
            try ( ResultSet tulokset = sql.executeQuery() )  {
                while ( tulokset.next() ) {
                    TyokalunNimi tyok = new TyokalunNimi();
                    tyok.parse(tulokset);
                    return tyok.getNimi();
                }
            }
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
        return "(tuntematon)";
    }

    /**
     * Lis�t��n ty�kalun nimi
     * @param nimi lis�tt�v�n ty�kalun nimi
     * @throws SailoException jos ongelmia
     */
    public void lisaa(TyokalunNimi nimi) throws SailoException {
        try ( Connection con = kanta.annaKantaYhteys(); PreparedStatement sql = nimi.annaLisaysLauseke(con) ) {
            sql.executeUpdate();
            lkm++;
            try ( ResultSet rs = sql.getGeneratedKeys() ) {
                nimi.tarkistaId(rs);
             }   
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    
    /**
     * Palauttaa ty�kalunimien lukum��r�n
     * @return ty�kalunimien lukum��r�
     * @example
     * <pre name="test">
     *  TyokalujenNimet tknimet = new TyokalujenNimet();
     *  TyokalunNimi vasara = new TyokalunNimi("vasara");
     *  tknimet.getLkm() === 0;
     *  tknimet.lisaa(vasara);
     *  tknimet.getLkm() === 1;
     */
    public int getLkm() {
        return lkm;
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
     * Palauttaa listan annetun j�senen ty�kalunimist�
     * (Voisi olla my�s "SELECT * FROM TyokalujenNimet 
     * WHERE nimiID=?", mutta silloin tietokantayhteys
     * pit�isi muodostaa useamman kerran jokaisen
     * listan ty�kalun kohdalla)
     * @param tyok lista annetun j�senen ty�kaluista
     * @return valitun j�senen ty�kalujen nimet listana
     * @throws SailoException jos ongelmia
     */
    public List<TyokalunNimi> annaTkNimet(List<Tyokalu> tyok) throws SailoException{
        List<TyokalunNimi> nimilista = new ArrayList<TyokalunNimi>();
            try ( Connection con = kanta.annaKantaYhteys();
                    PreparedStatement sql = con.prepareStatement("SELECT * FROM TyokaluNimet")
                      ) {
                  try ( ResultSet tulokset = sql.executeQuery() )  {
                      while ( tulokset.next() ) {
                          TyokalunNimi tknimi = new TyokalunNimi();
                          tknimi.parse(tulokset);
                          for (Tyokalu kalu : tyok ) {
                              if (kalu.getTyokaluNro() == (tknimi.getTunnusNro()))
                                  nimilista.add(tknimi);
                          }
                          
                      }
                  }
            } catch (SQLException e) {
                throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
            }
        return nimilista;
    }
    
    /**
     * Hakee hakuehdon t�ytt�v�t ty�kalunimet
     * (kutsutaan Ryhma-luokasta)
     * @param hakuehto hakuehto
     * @param k kentt� josta haetaan
     * @return hakuehdon t�ytt�v�t ty�kalut
     * @throws SailoException jos tietokannan kanssa ongelmia
     */
    public Collection<TyokalunNimi> etsi(String hakuehto, int k) throws SailoException {
        String ehto = hakuehto;
        String kentta = aputknimi.getKentta(k);
        if (k < 0) { //jos ei hakuehtoa, haetaan kaikki ty�kalut... 
            kentta = aputknimi.getKentta(0); //ja kaikki tiedot (alkaen id:st�)
            ehto = "";
        }
        try(Connection yht = kanta.annaKantaYhteys();
                PreparedStatement sql = yht.prepareStatement("SELECT * FROM TyokaluNimet WHERE " + kentta + " LIKE ?") ) {
            List<TyokalunNimi> loydetyt = new ArrayList<TyokalunNimi>();
            sql.setString(1, "%" + ehto + "%");
            try (ResultSet tulokset = sql.executeQuery()) {
                while (tulokset.next()) {
                    TyokalunNimi t = new TyokalunNimi();
                    t.parse(tulokset);
                    loydetyt.add(t); //lis�t��n ty�kalun tiedot listaan
                }
            }
            return loydetyt;
        } catch (SQLException e) {
            throw new SailoException("Ongelma tietokannasta hakiessa: " + e.getMessage());
        }
    }
    
    /**
     * Testip��ohjelma Ty�kalujen nimet-luokalle
     * @param args ei k�yt�ss�
     */
    public static void main(String[] args) {
        try {
            new File("kokeilu.db").delete();
            Tyokalut tyokalut = new Tyokalut("kokeilu");
            TyokalujenNimet tknimet = new TyokalujenNimet("kokeilu");

            Tyokalu vasara = new Tyokalu();
            vasara.rekisteroi();
            vasara.vastaaVasara(1);
            Tyokalu vasara2 = new Tyokalu();
            vasara2.rekisteroi();
            vasara2.vastaaVasara(1); //asettaa tyokalunro:lle (t�ss� luokassa tunnusNro) arvon 1
            Tyokalu ruuvari = new Tyokalu(2);
            ruuvari.rekisteroi();
            tyokalut.lisaa(vasara);
            tyokalut.lisaa(vasara2);
            tyokalut.lisaa(ruuvari);

            TyokalunNimi tkNimi = new TyokalunNimi("(tuntematon)");
            tknimet.lisaa(tkNimi);      //nimen id=0

            TyokalunNimi vasaraNimi = new TyokalunNimi("vasara se on");
            vasaraNimi.rekisteroi();    //vasaran id=1
            tknimet.lisaa(vasaraNimi);

            TyokalunNimi ruuvariNimi = new TyokalunNimi("ruuvari");
            ruuvariNimi.rekisteroi();   
            //ruuvarin id=2 (jotta t�m�n saisi n�kyviin, pit�isi Ty�kalu-luokassa olla metodi setTyokaluNro)
            tknimet.lisaa(ruuvariNimi);

            System.out.println("========= Testataan Ty�kalujen Nimet-luokkaa ==========");

            
            System.out.println("Tunnetut ty�kalunimet: \n");
            for (TyokalunNimi tknimi : tknimet.etsi("", -1)) {
                tknimi.tulosta(System.out);
            }
            new File("kokeilu.db").delete();
        } catch (SailoException e) {
            System.err.println("Virhe: " + e.getMessage());
        }
    }
}
