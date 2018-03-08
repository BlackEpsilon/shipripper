package shipripper;

import java.util.Scanner;

public class Game {
	
	private static GameServer gs;
	private static GameClient gc;
	private static String varName;

	public Game() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		initiateGame(args[0]);
		
		System.out.println("Player Name:");
		Scanner mscanner = new Scanner(System.in);
		String pName = mscanner.nextLine();
		varName = pName;
		mscanner.close();

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
		String pName = scanner.nextLine();
		System.out.println("Server IP:");			
		String serverIP = scanner.nextLine();
		System.out.println("Server Port:");
		String serverPortS = scanner.nextLine();
		int serverPort = Integer.parseInt(serverPortS);
		gc = new GameClient(serverIP, serverPort, pName);
		scanner.close();
	} 
	
	private static void createClient(String pIP, int pPort) {
		gc = new GameClient(pIP, pPort, varName);
	}
	
	public static void createServer(int pPort) {
		gs = new GameServer(pPort);
	}
		
	
		

}
