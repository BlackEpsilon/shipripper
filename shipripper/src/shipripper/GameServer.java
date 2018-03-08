package shipripper;

public class GameServer extends util.Server {

	public GameServer(int port) {
		super(port);// 
	}

	public void processMessage(String pClientIP, int pClientPort, String pMessage)
    {
		//TODO
    }

    /**
    Die Verbindung mit einem Client wurde beendet oder verloren.<br>
    Diese leere Methode kann in einer Unterklasse realisiert werden.
    @param pClientIP IP-Nummer des Clients, mit dem die Verbindung beendet wurde
    @param pClientPort Port-Nummer des Clients, mit dem die Verbindung beendet wurde
    */

}
