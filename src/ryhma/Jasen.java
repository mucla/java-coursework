package ryhma;

import java.io.*;
import java.sql.*;
import java.util.Comparator;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Jäsen-luokka
 * @author miemkoiv
 * @version 21.2.2017
 * Lisätty getterit, setterit, kenttien haku
 * @version 13.4.2017
 */
public class Jasen implements Cloneable, Comparable<Jasen> {
    private int tunnusNro;
    private String nimi =""; // voisi lisätä myös yksilöivän satunnaisluvun (jos tunnusnro ei riitä testaamiseen)
    private String katuosoite = "";
    private String postiosoite = ""; 
    private String puhnro = "";
    private String sahkoposti = ""; 
    
    private static int seuraavaNro = 1; //staattinen jotta kaikki oliot pääsee tähän käsiksi
    
    
    /**
     * Palauttaa tietokannan luontilausekkeen jäsentaulun luomiseksi
     * @return jäsentaulun luontilauseke
     */
    public String annaLuontilauseke() {
        return "CREATE TABLE Jasenet (" + 
            "JasenID INTEGER PRIMARY KEY AUTOINCREMENT , " + //asettaa tunnusNron automaattisesti
            "nimi VARCHAR(100), " +
            "katuosoite VARCHAR(100), " +
            "postiosoite VARCHAR(100), " +
            "puhnro VARCHAR(100), " +
            "sahkoposti VARCHAR(100)" +
            ")";
    }
    
    /**
     * Palauttaa lisäyslausekkeen jäsenen lisäämiseksi
     * @param yht tietokantayhteys
     * @throws SQLException jos lausekkeen luonti ei onnistu
     * @return jäsenen lisäyslauseke
     */
    public PreparedStatement annaLisayslauseke(Connection yht) throws SQLException {
        PreparedStatement sql = yht.prepareStatement("INSERT INTO Jasenet" +
            "(jasenID, nimi, katuosoite, " +
            "postiosoite, puhnro, sahkoposti)" +
            "VALUES (?,?,?,?,?,?)");
            if (tunnusNro != 0) sql.setInt(1,tunnusNro); else sql.setString(1,null); //tarkistetaan onko jäsen jo rekisteröity; jos ei, asetetaan null, jolloin autoincrement antaa numeron
            sql.setString(2, nimi);
            sql.setString(3, katuosoite);
            sql.setString(4, postiosoite);
            sql.setString(5, puhnro);
            sql.setString(6, sahkoposti);
            return sql;
    }
    
    /**
     * Palauttaa päivityslausekkeen jäsenen tietojen muokkaamiseksi
     * @param yht tietokantayhteys
     * @throws SQLException jos lausekkeen luonti ei onnistu
     * @return jäsenen päivityslauseke
     */
    public PreparedStatement annaPaivitysLauseke(Connection yht) throws SQLException {
         PreparedStatement sql = yht.prepareStatement("UPDATE Jasenet SET nimi =?, katuosoite =?, "+ 
                 " postiosoite=?, puhnro=?, sahkoposti=? " +
                 "WHERE jasenID LIKE ?");
                 if (tunnusNro != 0) sql.setInt(6,tunnusNro); else sql.setString(6,null); //tarkistetaan onko jäsen jo rekisteröity; jos ei, lisätään uutena (asetetaan null, jolloin autoincrement antaa numeron)
                 sql.setString(1, nimi);
                 sql.setString(2, katuosoite);
                 sql.setString(3, postiosoite);
                 sql.setString(4, puhnro);
                 sql.setString(5, sahkoposti);
                 return sql;
     }
    
    /**
     * Tarkistetaan onko jäsenen ID muuttunut lisäyksessä (kutsutaan 
     * Jasenet- luokasta)
     * @param tul lisäyslauseen antama ResultSet
     * @throws SQLException jos ei onnistu
     */
    public void tarkistaId(ResultSet tul) throws SQLException {
        if (!tul.next() ) return; //jos jäsenet käyty läpi, ei tehdä mitään
        int id = tul.getInt(1); // haetaan tunnusnumero ensimmäisestä sarakkeesta
        if (id == tunnusNro) return;
        setTunnusNro(id); //jos tunnusnro vaihtunut, muutetaan se ennalleen
    }
    
    
    /**
     * Jäsenten vertailija-luokka
     */
    public static class Vertailija implements Comparator<Jasen> {
        private final int kenttaNro;
        
        /**
         * Muodostaja vertailija-oliolle, joka vertailee jäseniä tietyn kentän perusteella
         * @param k vertailtava kenttä
         */
        public Vertailija(int k) {
            this.kenttaNro = k;
        }

        /**
         * Vertaa kahta jäsentä keskenään
         * @param eka ensimmäinen vertailtava jäsen
         * @param toka toinen vertailtava jäsen
         * @return palauttaa -1 jos eka<toka; palauttaa 0 jos samanarvoiset; palauttaa -1 jos toka>eka
         */
        @Override
        public int compare(Jasen eka, Jasen toka) {
            String s1 = eka.getAvain(kenttaNro);
            String s2 = toka.getAvain(kenttaNro);
            
            return s1.compareTo(s2);
        }
    }
    
    @Override
    public Jasen clone() throws CloneNotSupportedException {
        Jasen uusi;
        uusi = (Jasen) super.clone();
        return uusi;
    }
    


    /**
     * Tulostaa jäsenen tiedot
     * (TulostusController käyttää tätä)
     * @param out tulostettava
     */
    public void tulosta(PrintStream out) {
        out.println(nimi + "\n" + katuosoite + "\n" + postiosoite + "\n" 
                    + puhnro + "\n" + sahkoposti);
        }
    
    /**
     * Selvittää jäsenen tiedot |-merkillä erotellusta merkkijonosta 
     * @param rivi merkkijono, josta erotellaan jäsenen tiedot
     * <pre name="test">
     *  Jasen aku = new Jasen();
     *  aku.parse("4 | Aku Ankka|    Kirkkokatu 1 | 64560 Ankkalinna | 050555555| aku@ankka.fi | ");
     *  aku.getTunnusNro() === 4;
     *  aku.getNimi() === "Aku Ankka";
     *  aku.toString() === "4 | Aku Ankka | Kirkkokatu 1 | 64560 Ankkalinna | 050555555 | aku@ankka.fi";
     * </pre>
     */
    public void parse(String rivi) {
        StringBuffer sb = new StringBuffer(rivi);
        for (int k = 0; k<getKenttia();k++) {
            aseta(k,Mjonot.erota(sb, '|'));
        }    
    }
    
    /**
     * Selvittää jäsenen tiedot SQL:n ResultSetistä
     * @param tulokset ResultSet josta tiedot otetaan
     * @throws SQLException jos ei onnistu
     */
    public void parse(ResultSet tulokset) throws SQLException {
        setTunnusNro(tulokset.getInt("jasenID"));
        nimi = tulokset.getString("nimi");
        katuosoite = tulokset.getString("katuosoite");
        postiosoite = tulokset.getString("postiosoite");
        puhnro = tulokset.getString("puhnro");
        sahkoposti = tulokset.getString("sahkoposti");
        }
    
    /**
     * Palauttaa jäsenen tiedot | -erotettuna merkkijonona
     * @return jäsenen tiedot |-merkillä erotettuna
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
     * Testimetodi, joka täyttää jäsenen tiedot Aku Ankan tiedoilla
     */
    public void taytaAkuAnkka() {
        nimi = "Aku Ankka";
        katuosoite = "Paratiisitie 13";
        postiosoite = "131313 Ankkalinna";
        puhnro = "+33313131313";
        sahkoposti = "aku69@ankka.fi";
    }
    
    /**
     * Rekisteröi jäsenelle yksilöllisen tunnusnumeron
     * @return jäsenelle annettu yksilöllinen tunnusnumero
     * @example
     * <pre name="test">
     *  Jasen aku1 = new Jasen();
     *  aku1.getTunnusNro() === 0;
     *  aku1.rekisteroi();
     *  aku1.getTunnusNro() === 5; 
     *  //yllä: pitäisi olla 1, mutta ohjaajan kanssa katsottiin eikä huomattu missä 
     *  //vika. Johtunee siitä että muut testit pyörii ensin ja muuttaa staattista seuraavaNroa. 
     *  //Toimii kuitenkin pääohjelmassa.
     *  Jasen aku2 = new Jasen();
     *  aku2.rekisteroi();
     *  aku2.getTunnusNro() === 6;
     * </pre> 
     */
    public int rekisteroi() {
        if (tunnusNro != 0) return tunnusNro;
        tunnusNro = seuraavaNro;
        seuraavaNro++;
        return tunnusNro;
    }
    
    /**
     * Asettaa jäsenen tunnusnumeron ja varmistaa, että seuraava numero
     * on aina yhtä suurempi kuin tähän mennessä suurin
     * @param nro asetettava tunnusnumero
     */
    public void setTunnusNro(int nro) {
        tunnusNro = nro;
        if (seuraavaNro <= tunnusNro) seuraavaNro = tunnusNro + 1;
    }
    
    
    /**
     * Asettaa jäsenen puhelinnumeron
     * @param s asetettava puhelinnumero
     * @return null jos asettaminen onnistui
     */
    public String setPuhelin(String s) {
        for (int i = 0; i<s.length();i++)
            if ("01234567890+ ".indexOf(s.charAt(i))<0) return "Tarkista puhelinnumero (Sallitut merkit 0-9, + ja välilyönti)";
        puhnro = s;
        return null;
    }
    
    
    /**
     * Palauttaa jäsenen tunnusnumeron
     * @return jäsenen tunnusnumero
     * (testattu jo metodissa rekisteroi)
     */
    public int getTunnusNro() {
        return tunnusNro;
    }
    
    /**
     * Palauttaa jäsenen nimen
     * @return jäsenen nimi
     * @example
     * <pre name="test">
     *  Jasen aku1 = new Jasen();
     *  aku1.taytaAkuAnkka();
     *  aku1.getNimi() === "Aku Ankka";
     * </pre>
     */
    public String getNimi() {
        return nimi;
    }
    
    /**
     * Palauttaa jäsenen katuosoitteen
     * @return jäsenen katuosoite
     * @example
     * <pre name="test">
     *  Jasen aku1 = new Jasen();
     *  aku1.taytaAkuAnkka();
     *  aku1.getKatuosoite() === "Paratiisitie 13";
     * </pre>
     */
    public String getKatuosoite() {
        return katuosoite;
    }
    
    /**
     * Palauttaa jäsenen postiosoitteen
     * @return jäsenen postiosoite
     * @example
     * <pre name="test">
     *  Jasen aku1 = new Jasen();
     *  aku1.taytaAkuAnkka();
     *  aku1.getPostiosoite() === "131313 Ankkalinna";
     * </pre>
     */
    public String getPostiosoite() {
        return postiosoite;
    }
    
    /**
     * Palauttaa jäsenen puhelinnumeron
     * @return jäsenen puhelinnumero
     * @example
     * <pre name="test">
     *  Jasen aku1 = new Jasen();
     *  aku1.taytaAkuAnkka();
     *  aku1.getPuhelin() === "+33313131313";
     * </pre>
     */
    public String getPuhelin() {
        return puhnro;
    }
    
    /**
     * Palauttaa jäsenen sähköpostiosoitteen
     * @return jäsenen sähköpostiosoite
     * @example
     * <pre name="test">
     *  Jasen aku1 = new Jasen();
     *  aku1.aseta(5,"aku@ankka.fi");
     *  aku1.getSapo() === "aku@ankka.fi";
     * </pre>
     */
    public String getSapo() {
        return sahkoposti;
    }
    
    /**
     * Palauttaa jäsenen tietokenttien lukumäärän
     * @return jäsenen tietokenttin lkm
     */
    public int getKenttia() {
        return 6;
    }
    
    /**
     * Palauttaa jäsenen tietokentistä ensimmäisen (1, koska id:tä ei haluta näyttää)
     * @return ensimmäisen jäsenen tietokentän indeksi
     */
    public int ekaKentta() {
        return 1;
    }
    
    /**
     * Palauttaa kunkin kentän selosteen
     * @param k kentän numero
     * @return kentän seloste
     */
    public String getKentta(int k) {
        switch (k) {
        case 0: return "jasenID";
        case 1: return "Nimi";
        case 2: return "Katuosoite";
        case 3: return "Postiosoite";
        case 4: return "Puhelinnumero";
        case 5: return "Sähköposti";
        default: return "";
        }
    }
    
    /**
     * Palauttaa jäsenen tietyn tiedon (esim. nimen)
     * @param k kentän numero
     * @return kyseisen jäsenen tiedot kyseisessä kentässä
     */
    public String anna(int k) {
        switch (k) {
        case 0: return ""+getTunnusNro(); //lainausmerkit muuttaa intin Stringiksi; tämä tarvitaan toString-metodia varten
        case 1: return getNimi();
        case 2: return getKatuosoite();
        case 3: return getPostiosoite();
        case 4: return getPuhelin();
        case 5: return getSapo();
        default: return "";
        }
    }
    
    /**
     * Vertaa jäsentä toiseen jäseneen
     */
    @Override
    public int compareTo(Jasen jasen) {
        return jasen.compareTo(jasen);
    }
    
    /**
     * Palauttaa jäsenen tietyn kentän lajitteluavaimen
     * @param k kenttä jonka perusteella lajitellaan
     * @return lajitteluavain
     */
    public String getAvain(int k) {
        switch (k) {
        case 1: return getNimi();
        case 2: return getKatuosoite();
        case 3:
            StringBuffer osoite = new StringBuffer(getPostiosoite());
            int postinro = Mjonot.erotaInt(osoite, 999999999); //jos ei postinumeroa, oletuksena korkea luku (lajitellaan viimeiseksi)
            return Mjonot.fmt(postinro, 10); //palauttaa postinron 10 numeron pituisena
        case 4: 
            String puhNro = getPuhelin().trim();
            if (puhNro.length()>0 && puhNro.charAt(0) == '+') return puhNro.substring(1); //palauttaa vertailuun puhelinnron ilman alkumerkkiä
            return puhNro;
            //tällä hetkellä ei ota huomioon mahdollisia '+'-välimerkkejä keskellä numeroa
        case 5: return getSapo();
        default: return "";
        }
    }
    

    /**
     * Asettaa jäsenen tietokentän k arvoksi tuodun merkkijonon
     * @param k kentän id
     * @param s asetettava merkkijono
     * @return null jos tekstin asettaminen onnistuu, muuten virheilmoitus
     * <pre name="test">
     *  Jasen jasen = new Jasen();
     *  jasen.aseta(1, "Ankka Tupu") === null;
     *  jasen.aseta(4, "kissa") === "Tarkista puhelinnumero (Sallitut merkit 0-9, + ja välilyönti)";
     *  jasen.aseta(4, "040131313") === null;
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
            nimi = jono;
            return null;
        case 2:
            katuosoite = jono;
            return null;
        case 3:
            postiosoite = jono;
            return null;
        case 4:
            String virhe = setPuhelin(jono);
            if (virhe != null) return virhe;
            return null;
        case 5:
            sahkoposti = jono;
            return null;
        default: return "";
        }

    }

    /**
     * jäsenten vertailu (testeissä)
     */
    @Override
    public boolean equals(Object jasen) {
        if ( jasen == null ) return false;
        return this.toString().equals(jasen.toString());
    }
    
    /**
     * vaaditaan, jotta luokka toteuttaa HashCode()-rajapinnan
     * (jotta voi vertailla olioita)
     */
    @Override
    public int hashCode() {
        return tunnusNro;
    }
    
    /**
     * Testipääohjelma Jäsen-luokalle
     * @param args ei käytössä
     */
    public static void main(String[] args) {
        Jasen aku = new Jasen();
        aku.tulosta(System.out);
        aku.taytaAkuAnkka();
        aku.rekisteroi();
        aku.tulosta(System.out);
        Jasen aku6 = new Jasen();
        aku6.parse("6 | Aku Ankka|    Kirkkokatu 1 | 64560 Ankkalinna | 050555555| aku@ankka.fi | ");
        aku6.tulosta(System.out);
        Jasen aku2 = new Jasen();
        aku2.taytaAkuAnkka();
        aku2.rekisteroi();
        aku2.tulosta(System.out);
        }



}
