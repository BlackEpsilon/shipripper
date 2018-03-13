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
			return;
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
				send("ENEMY");
			}else if(in[0].equalsIgnoreCase("EIGEN")) {
				System.out.println("Dein Feld:");
				System.out.println(player.ausgabe());
			}else if(in[0].equalsIgnoreCase("PLACE")) {
				if(state != Game.STATE_PLACEMENT){
					System.out.println("Nicht in platzierungsphase!");
					continue;
				}
				int res = player.place(in[1], in[2]);
				switch(res){
				case -6: System.out.println("Du hast bereits alle Schiffe dieser Größe platziert."); continue;
				case -5: System.out.println("Das Schiff verlässt das Spielfeld."); continue;
				case -4: System.out.println("Das Schiff Kollidiert mit einem anderen."); continue;
				case -3: System.out.println("Das Schiff steht schräg."); continue;
				case -2: System.out.println("Mindestens eine der Koordinaten ist ungültig."); continue;
				case 1: System.out.println("Schiff platziert."); continue;
				case 2: System.out.println("Alle Schiffe platziert.");
						sendShips();
						continue;
				}
			}else if(in[0].equalsIgnoreCase("SHOOT")) {
				if(state != Game.STATE_PLAYING){
					System.out.println("Du bist nicht an der Reihe!");
					continue;
				}
				send("SHOOT " + in[1]);
			}else if(in[0].equalsIgnoreCase("QUIT")) {
				send("QUIT " + player.getName());
				close();
				return;
			}
		}
	}
	
	private void sendShips(){
		String msg = "PLACE";
		for(int i=0; i<10; i++){
			for(int k=0; k<10; k++){
				if(player.get(i, k)==Player.SHIP)msg += " " + i + " " + k;
			}
		}
		send(msg);
	}
	
	@Override
	public void processMessage(String pMessage) {
		String[] in = pMessage.split(" ");
		if(in[1].equals("STATE")){
			state = Integer.parseInt(in[2]);
		}else if(in[1].equals("SHOT_AT")){
			if(in[2].equals("WATER")){
				System.out.println("Du hast daneben geschossen.");
			}else if(in[2].equals("HIT")){
				System.out.println("Du hast ein Schiff getroffen.");
			}else if(in[2].equals("SUNKEN")){
				System.out.println("Du hast ein Schiff versenkt.");
			}else if(in[2].equals("ALREADY")){
				System.out.println("Du retard schießt 2 mal auf das selbe Feld!");
			}
		}else if(in[1].equals("WIN")){
			System.out.println(in[2] + " hat gewonnen!");
		}else if(in[1].equals("FRIENDLY_HIT")){
			int res = player.hit(in[2]);
			switch(res){
				case Player.HIT_ALREADY_HIT: System.out.println("Dein Gegner hat kein Kurzzeitgedächtinss!"); return;
				case Player.HIT_SHIP: System.out.println("Eines deiner Schiffe wurde getroffen!"); return;
				case Player.HIT_SUNKEN: System.out.println("Eines deiner Schiffe wurde versenkt!"); return;
				case Player.HIT_WATER: System.out.println("Dein Gegner hat das Wasser getroffen"); return;
			}
			player.hit(in[3]);
		}else if(in[1].equals("WAITING")){
			System.out.println("Warte auf mitspieler.");
		}else if(in[1].equals("PLACE")){
			System.out.println("Platziere deine Schiffe");
		}else if(in[1].equals("BEGINN")){
			System.out.println(in[2] + " beginnt!");
		}else if(in[1].equals("REJECTED")){
			if(in[2].equals("NAME")) {
				System.out.println("Suche dir einen anderen Namen!");
			}else if(in[2].equals("FULL")) {
				System.out.println("Der Server ist voll!");
			}
			close();
		}else if(in[1].equals("ENEMY")){
			System.out.println("Gegnerisches Feld:");
			System.out.println(pMessage.substring(10));
		}else if(in[1].equals("QUIT")){
			System.out.println(in[2] + " gibt auf!");
		}
	}

}
