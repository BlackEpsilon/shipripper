package shipripper;

import java.util.Scanner;

public class GameClient extends util.Client{
	
	private String name;

	public GameClient(String serverIP, int serverPort, String pName) {
		super(serverIP, serverPort);
		name = pName;
	}
	
	/*public GameClient() {
		System.out.println("Server IP:");
		Scanner scanner = new Scanner(System.in);
		String serverIP = scanner.nextLine();
		System.out.println("Server Port:");
		String serverPortS = scanner.nextLine();
		int serverPort = Integer.parseInt(serverPortS);
		
		super(serverIP, serverPort);
	}*/

	@Override
	public void processMessage(String pMessage) {
		// TODO Auto-generated method stub
		
	}

}
