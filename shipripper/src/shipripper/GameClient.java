package shipripper;

import java.util.Scanner;

public class GameClient extends util.Client{
	
	private Player player;
	private int state;
	private Scanner scanner;

	public GameClient(String serverIP, int serverPort, String pName) {
		super(serverIP, serverPort);
		player = new Player(pName);
		state = Game.STATE_WAITING;
		if(!isConnected()) {
			System.out.println("Kein Server unter " + serverIP + ":" + serverPort + " zu erreichen.");
		}
		System.out.println("An Server " + serverIP + ":" + serverPort + " anmelden...");
		send("READY " + pName);
		scanner = new Scanner(System.in);
	}
	
	private void gameLoop() {
		while(true){
			String input = scanner.nextLine();
			String[] in = input.split(" ");
			
			if(in[0].equalsIgnoreCase("GEGNER")) {
				//Gegnerfeld ausgeben
			}else if(in[0].equalsIgnoreCase("EIGEN")) {
				//Eigenes Feld ausgeben
			}else if(in[0].equalsIgnoreCase("PLACE")) {
				//Schiff platzieren
			}else if(in[0].equalsIgnoreCase("SHOOT")) {
				//Schieﬂen
			}else if(in[0].equalsIgnoreCase("QUIT")) {
				//Spiel beenden
			}
		}
	}
	
	@Override
	public void processMessage(String pMessage) {
		// TODO Auto-generated method stub
		
	}

}
