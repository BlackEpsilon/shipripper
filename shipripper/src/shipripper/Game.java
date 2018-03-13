package shipripper;

import java.util.Scanner;

public class Game {
	
	public static final int STATE_PLACEMENT = 1;
	public static final int STATE_WAITING = 2;
	public static final int STATE_PLAYING = 3;
	
	private static GameServer gs;
	private static GameClient gc;

	public Game() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
//		initiateGame(args[0]);
		
		
		
		initiateGame("play");
		

	}
	
	private static void initiateGame(String workmode) {
		if (workmode.equals("play")) {
			createClient();
		}
		else if (workmode.equals("host")) {
			createServer(40);
		}
		
		else if (workmode.equals("multi")) {
			createServer(40);
			createClient("localhost", 40);
		}
	}
		
	private static void createClient() {
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Server IP:");			
		String serverIP = scanner.nextLine();
		System.out.println("Server Port:");
		String serverPortS = scanner.nextLine();
		int serverPort = Integer.parseInt(serverPortS);
		createClient(serverIP, serverPort);
		scanner.close();
	} 
	
	private static void createClient(String pIP, int pPort) {
		System.out.println("Player Name:");
		Scanner mscanner = new Scanner(System.in);
		String pName = mscanner.nextLine();
		mscanner.close();
		gc = new GameClient(pIP, pPort, pName);
	}
	
	public static void createServer(int pPort) {
		gs = new GameServer(pPort);
	}
		
	
		

}
