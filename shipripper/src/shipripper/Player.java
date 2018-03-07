package shipripper;

public class Player {

	private String name;
	private int[][] field;
	private int[] remainingShips;
	
	public static final int WATER = 0;
	public static final int WATER_HIT = 1;
	public static final int SHIP = 2;
	public static final int SHIP_HIT = 3;
	public static final int SHIP_SUNKEN = 4;

	public static final int HIT_WATER = 0;
	public static final int HIT_SHIP = 1;
	public static final int HIT_SUNKEN = 2;
	public static final int HIT_LOOSE = 3;
	
	public void ausgabe() {
		for(int i=0; i<field.length; i++) {
			for(int k=0; k<field[i].length; k++) {
				if(field[k][i] == WATER)System.out.print("O");
				else System.out.print("X");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		Player p = new Player("");
		System.out.println(p.place("A1", "E1"));
		System.out.println(p.place("F3", "F6"));
		p.ausgabe();
	}
	
	
	/**
	 * Konstruktor
	 * @param name: Name des Spielers
	 */
	public Player(String name) {
		this.name = name;
		
		field = new int[10][10];
		for(int i=0; i<field.length; i++) {
			for(int k=0; k<field[i].length; k++) {
				field[i][k] = WATER;
			}
		}

		remainingShips = new int[6];
		remainingShips[0] = 0;
		remainingShips[1] = 0;
		remainingShips[2] = 4;
		remainingShips[3] = 3;
		remainingShips[4] = 2;
		remainingShips[5] = 1;
	}
	
	/**
	 * Platziert ein Schiff auf
	 * @param start String: start des Schiffes
	 * @param end String: ende des Schiffes
	 * @return 	1: Schiff platziert
	 * 			2: letztes Schiff platziert
	 * 			andere: siehe checkPlacement()
	 */
	public int place(String start, String end) {
		int res = checkPlacement(start, end);
		if(res < 0)return res;
		
		int[] startCoordinate = new int[2];
		int[] endCoordinate = new int[2];
		int length;
		try {
			startCoordinate = toCoordinates(start);
			endCoordinate = toCoordinates(end);
		}catch(InvalidCoordinateException e) {
			return -2;
		}
		
		if(startCoordinate[0] != endCoordinate[0] || startCoordinate[1] == endCoordinate[1]) {
			int y = startCoordinate[1];
			int x;
			if(startCoordinate[0] < endCoordinate[0]) {
				x = startCoordinate[0];
			}else {
				x = endCoordinate[0];
			}
			length = Math.abs(startCoordinate[0]-endCoordinate[0]);
			
			
			//Platzieren
			for(int i = 0; i <= length; i++) {
				set(x+i,y,SHIP);
			}
			
		}else if(startCoordinate[1] != endCoordinate[1] || startCoordinate[0] == endCoordinate[0]) {
			int x = startCoordinate[0];
			int y;
			if(startCoordinate[1] < endCoordinate[1]) {
				y = startCoordinate[1];
			}else {
				y = endCoordinate[1];
			}
			length = Math.abs(startCoordinate[1]-endCoordinate[1]);
			
			//Platzieren
			for(int i = 0; i <= length; i++) {
				set(x,y+i,SHIP);
			}
		}
		//Schiff abziehen
		remainingShips[res]--;
		if(!shipsRemaining())return 2;
		return 1;
	}
	
	private boolean shipsRemaining() {
		for(int i = 0; i < remainingShips.length; i++) {
			if(remainingShips[i] < 0)return false;
		}
		return true;
	}
	
	/**
	 * Überprüft, ob ein Schiff mit gegebenen Start und Endpunkt platziert werden kann
	 * @param start: String: Startpunkt
	 * @param end: String: Endpunkt
	 * @return 	 0: Schiff ist platzierbar mit länge (<=) plazierbar
	 * 			-2: Koordinaten in falscher Form
	 * 			-3: Das Schiff steht schräg
	 * 			-4: Das Schiff Kollidiert mit einem anderen
	 * 			-5: Das Schiff ragt aus dem Spielfeld heraus
	 * 			-6: Kein Schiff dieser Art mehr platzierbar
	 */
	private int checkPlacement(String start, String end) {
		int[] startCoordinate = new int[2];
		int[] endCoordinate = new int[2];
		int length;
		try {
			startCoordinate = toCoordinates(start);
			endCoordinate = toCoordinates(end);
		}catch(InvalidCoordinateException e) {
			return -2;
		}
		
		if(startCoordinate[0] != endCoordinate[0] || startCoordinate[1] == endCoordinate[1]) {
			int y = startCoordinate[1];
			int x;
			if(startCoordinate[0] < endCoordinate[0]) {
				x = startCoordinate[0];
			}else {
				x = endCoordinate[0];
			}
			length = Math.abs(startCoordinate[0]-endCoordinate[0]);
			
			//Innerhalb der Grenzen?
			if(x<0 || x+length>field.length || y<0 || y>field.length)return -5;
			
			//Kollidiert?
			for(int i = 0; i <= length; i++) {
				if(!neighboursOnlyWater(x+i,y))return -4;
			}
			
		}else if(startCoordinate[1] != endCoordinate[1] || startCoordinate[0] == endCoordinate[0]) {
			int x = startCoordinate[0];
			int y;
			if(startCoordinate[1] < endCoordinate[1]) {
				y = startCoordinate[1];
			}else {
				y = endCoordinate[1];
			}
			length = Math.abs(startCoordinate[1]-endCoordinate[1]);
			
			//Innerhalb der Grenzen?
			if(x<0 || x>field.length || y<0 || y+length>field.length)return -5;
			
			//Kollidiert?
			for(int i = 0; i <= length; i++) {
				if(!neighboursOnlyWater(x,y+i))return -4;
			}
			
		}else {
			return -3;
		}
		
		
		length++;
		if(length>=remainingShips.length)return -6;
		if(remainingShips[length] <= 0)return -6;
		return length;
	}
	
	/**
	 * Hilfsmethode für checkPlacement
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean neighboursOnlyWater(int x, int y) {
		try {if(field[x][y] != WATER)return false;}catch(Exception e) {}
		try {if(field[x+1][y] != WATER)return false;}catch(Exception e) {}
		try {if(field[x-1][y] != WATER)return false;}catch(Exception e) {}
		try {if(field[x][y+1] != WATER)return false;}catch(Exception e) {}
		try {if(field[x][y-1] != WATER)return false;}catch(Exception e) {}
		return true;
	}
	
	
	/**
	 * Setzt zustand eines Feldes
	 * @param x/y: Koordinaten des Feldes (X/Y)
	 * @param status: Zustand, in den das Feld versezt wird
	 */
	private boolean set(int x, int y, int status) {
		try {
			field[x][y] = status;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Setzt zustand eines Feldes
	 * @param tile: Koordinaten des Feldes (0:X 1:Y)
	 * @param status: Zustand, in den das Feld versezt wird
	 */
	private boolean set(int[] coordinates, int status) {
		try {
			field[coordinates[0]][coordinates[1]] = status;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Setzt zustand eines Feldes
	 * @param tile: String, bezeichnung des Feldes
	 * @param status: Zustand, in den das Feld versezt wird
	 */
	private boolean set(String tile, int status) {
		int[] coordinates = new int[2];
		try {
			coordinates = toCoordinates(tile);
			field[coordinates[0]][coordinates[1]] = status;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Wandelt den String zb: "A1" in Koordinaten des 2D-Arrays um
	 * @param tile: String der Position
	 * @return X, Y des 2D arrays
	 * @throws InvalidCoordinateException 
	 */
	private static int[] toCoordinates(String tile) throws InvalidCoordinateException {
		int[] temp = new int[2];
		try {
			temp[0] = Integer.parseInt(tile.substring(1))-1;
			char y = tile.charAt(0);
			switch(y) {
				case 'A': temp[1]=0; break;
				case 'B': temp[1]=1; break;
				case 'C': temp[1]=2; break;
				case 'D': temp[1]=3; break;
				case 'E': temp[1]=4; break;
				case 'F': temp[1]=5; break;
				case 'G': temp[1]=6; break;
				case 'H': temp[1]=7; break;
				case 'I': temp[1]=8; break;
				case 'J': temp[1]=9; break;
			}
		}catch(Exception e) {
			throw new InvalidCoordinateException();
		}
		return temp;
	}
	
	public String getName(){
		return name;
	}
}
