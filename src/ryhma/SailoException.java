package ryhma;

/**
 * Poikkeusluokka poikkeuksille, jotka aiheutuvat tietorakenteesta
 * @author miemkoiv
 * @version 21.2.2017
 */
public class SailoException extends Exception {
    private static final long serialVersionUID = 1L;
    
    /**
     * N‰ytt‰‰ selitetekstin
     * @param viesti poikkeuksen seliteteksti
     */
    public SailoException(String viesti) {
        super(viesti);
    }
}
