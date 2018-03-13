package shipripper;

public class GameServer extends util.Server {

	private Player[] players;	
	private String[] IPs;
	private int[] ports;
	
	public GameServer(int port) {
		super(port);
		players = new Player[2];
		IPs = new String[2];
		ports = new int[2];
	}

	public void processMessage(String pClientIP, int pClientPort, String pMessage)
    {
		String in[] = pMessage.split(" ");
		if(in[0].equals("READY")) {
			if(bothConnected()) {
				send(pClientIP, pClientPort, "-ERR REJECTED FULL");
				closeConnection(pClientIP, pClientPort);
			}else if(getPlayerByName(in[1]) != null) {
				send(pClientIP, pClientPort, "-ERR REJECTED NAME");
				closeConnection(pClientIP, pClientPort);
			}else {
				if(players[0] != null) {
					players[0] = new Player(in[1]);
					IPs[0] = pClientIP;
					ports[0] = pClientPort;
				}else {
					players[1] = new Player(in[1]);
					IPs[1] = pClientIP;
					ports[1] = pClientPort;
				}
				
				if(!bothConnected()) {
					send(pClientIP, pClientPort, "+OK WAITING");
				}else{
					sendToAll("+OK STATE " + Game.STATE_PLACEMENT);
					sendToAll("+OK PLACE");
				}
			}
		}else if(in[0].equals("ENEMY")) {
			Player enemy = getEnemy(getPlayerByIPAndPort(pClientIP, pClientPort));
			send(pClientIP, pClientPort, "+OK ENEMY " + enemy.ausgabeFuerGegner());
		}else if(in[0].equals("PLACE")) {
			Player player = getPlayerByIPAndPort(pClientIP, pClientPort);
			for(int i = 1; i < in.length; i+=2) {
				player.set(Integer.parseInt(in[i]), Integer.parseInt(in[i+1]), Player.SHIP);
			}
		}else if(in[0].equals("SHOOT")) {
			Player player = getPlayerByIPAndPort(pClientIP, pClientPort);
			Player enemy = getEnemy(player);
			String eIP = getIp(enemy);
			int ePort = getPort(enemy);
			int res = enemy.hit(in[1]);
			switch(res) {
				//Für jeden Fall an opfer und täter senden
				case Player.HIT_ALREADY_HIT: 	send(pClientIP, pClientPort, "+OK SHOT_AT ALREADY");
												send(eIP, ePort, "+OK FRIENDLY_HIT " + res + " " + in[1]);
												break;
				case Player.HIT_SHIP: 	send(pClientIP, pClientPort, "+OK SHOT_AT HIT");
										send(eIP, ePort, "+OK FRIENDLY_HIT " + res + " " + in[1]);
										
				case Player.HIT_SUNKEN: send(pClientIP, pClientPort, "+OK SHOT_AT SUNKEN");
										send(eIP, ePort, "+OK FRIENDLY_HIT " + res + " " + in[1]);
										
				case Player.HIT_WATER: 	send(pClientIP, pClientPort, "+OK SHOT_AT WATER");
										send(eIP, ePort, "+OK FRIENDLY_HIT " + res + " " + in[1]);
										
				case Player.HIT_LOOSE:	sendToAll("+OK WIN " + player.getName());
										players[0] = new Player(players[0].getName());
										players[1] = new Player(players[1].getName());
										sendToAll("+OK STATE " + Game.STATE_PLACEMENT);
										sendToAll("+OK PLACE");
										break;
			}
			send(pClientIP, pClientPort, "+OK STATE " + Game.STATE_WAITING);
			send(eIP, ePort, "+OK STATE " + Game.STATE_PLAYING);
			
		}else if(in[0].equals("QUIT")) {
			sendToAll("+OK QUIT " + in[1]);
			sendToAll("+OK STATE " + Game.STATE_WAITING);
			players[0] = new Player(players[0].getName());
			players[1] = new Player(players[1].getName());
			deletePlayer(in[1]);
		}
    }
	
	private void deletePlayer(String name) {
		if(players[0] != null && players[0].getName().equals(name))players[0] = null;
		if(players[1] != null && players[1].getName().equals(name))players[1] = null;
	}
	
	private Player getPlayerByName(String name) {
		if(players[0] != null && players[0].getName().equals(name))return players[0];
		if(players[1] != null && players[1].getName().equals(name))return players[1];
		return null;
	}
	
	private Player getPlayerByIPAndPort(String IP, int port) {
		if(IPs[0].equals(IP) && ports[0] == port)return players[0];
		if(IPs[1].equals(IP) && ports[1] == port)return players[1];
		return null;
	}
	
	private Player getEnemy(Player p) {
		if(p == players[0]) return players[1];
		if(p == players[1]) return players[0];
		return null;
	}
	
	private String getIp(Player p) {
		if(p == players[0]) return IPs[1];
		if(p == players[1]) return IPs[0];
		return null;
	}
	
	private int getPort(Player p) {
		if(p == players[0]) return ports[1];
		if(p == players[1]) return ports[0];
		return -1;
	}
	
	private boolean bothConnected() {
		if(players[0] == null || players[1] == null)return false;
		return true;
	}

}
