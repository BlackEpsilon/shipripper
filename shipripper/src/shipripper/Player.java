package shipripper;

import util.InvalidCoordinateException;

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
	public static final int HIT_ALREADY_HIT = 4;
	
	private static final int[] standartRemainingShips = {0,0,4,3,2,1};
	
	public String ausgabe() {
		String temp = "";
		for(int i=0; i<field.length; i++) {
			for(int k=0; k<field[i].length; k++) {
				if(field[k][i] == WATER)temp+="O";
				else if(field[k][i] == WATER_HIT)temp+="X";
				else if(field[k][i] == SHIP)temp+="+";
				else if(field[k][i] == SHIP_HIT)temp+="%";
				else if(field[k][i] == SHIP_SUNKEN)temp+="#";
				
			}
			temp+="\n";
		}
		return temp;
	}
	
	public String ausgabeFuerGegner() {
		String temp = "";
		for(int i=0; i<field.length; i++) {
			for(int k=0; k<field[i].length; k++) {
				if(field[k][i] == WATER)temp+="O";
				else if(field[k][i] == WATER_HIT)temp+="X";
				else if(field[k][i] == SHIP)temp+="O";
				else if(field[k][i] == SHIP_HIT)temp+="%";
				else if(field[k][i] == SHIP_SUNKEN)temp+="#";
				
			}
			temp+="\n";
		}
		return temp;
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
				set(i,k,WATER);
			}
		}
		
		remainingShips = new int[standartRemainingShips.length];
		for(int i = 0; i < remainingShips.length; i++) {
			remainingShips[i] = standartRemainingShips[i];
		}
	}
	
	/**
	 * Schießt auf das angegebene Feld
	 * @param tile: zu beschießende Feld
	 * @return 	-1: Falsches Feld
	 * 			-2: Ungültige koordinaten
	 * 			andere: Konstanten HIT_*
	 */
	public int hit(String tile){
		int[] temp;
		try{
			temp = toCoordinates(tile);
		}catch(InvalidCoordinateException e){
			return -2;
		}
		int x = temp[0];
		int y = temp[1];
		
		switch(field[x][y]){
			case WATER_HIT: return HIT_ALREADY_HIT;
			case SHIP_HIT: return HIT_ALREADY_HIT;
			case SHIP_SUNKEN: return HIT_ALREADY_HIT;
			case WATER: 
				set(x,y,WATER_HIT);
				return HIT_WATER;
			case SHIP:
				set(x,y,SHIP_HIT);
				if(!shipIntactAt(x, y)){
					sinkShipAt(x, y);
					if(allShipsDestroyed())return HIT_LOOSE;
					return HIT_SUNKEN;
				}
				return HIT_SHIP;
		}
		return -1;
	}
	
	/**
	 * Prueft, ob alle Schiffe zerstör wurden
	 * @return
	 */
	private boolean allShipsDestroyed(){
		for(int i = 0; i < remainingShips.length; i++) {
			if(remainingShips[i] != standartRemainingShips[i])return false;
		}
		return true;
	}
	
	/**
	 * Versenkt das Schiff an den gegebenen Koordinaten und fügt es wieder den remaining ships hinzu (Alle verbundenen SHIP_HIt werden zu SHIP_SUNKEN)
	 * @param x
	 * @param y
	 */
	private void sinkShipAt(int x, int y){
		try{
			if(field[x][y] != SHIP_HIT)return;
		}catch(ArrayIndexOutOfBoundsException e){
			return;
		}
		
		set(x,y,SHIP_SUNKEN);
		//Länge des schiffes Zählen
		int length = 1;
		
		length += sinkShipAt(x-1,y,0);
		length += sinkShipAt(x+1,y,1);
		length += sinkShipAt(x,y-1,2);
		length += sinkShipAt(x,y+1,3);
		
		remainingShips[length]++;
	}
	
	private int sinkShipAt(int x, int y, int dir){
		try{
			if(field[x][y] != SHIP_HIT)return 0;
		}catch(ArrayIndexOutOfBoundsException e){
			return 0;
		}
		
		set(x,y,SHIP_SUNKEN);field[x][y] = SHIP_SUNKEN;
		
		//länge des Schiffes Zählen
		if(dir == 0)return sinkShipAt(x-1,y,0)+1;
		else if(dir == 1)return sinkShipAt(x+1,y,1)+1;
		else if(dir == 2)return sinkShipAt(x,y-1,2)+1;
		else if(dir == 3)return sinkShipAt(x,y+1,3)+1;
		
		return 0;
	}
	
	/**
	 * Prüft, ob ein Schiff gesunken ist
	 * @param x: X-Koordinate
	 * @param y: Y-Koordinate
	 * @return boolean: gesunken? (true/false)
	 */
	private boolean shipIntactAt(int x, int y){
		try{
			if(field[x][y] == SHIP)return true;
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
		
		if(shipIntactAt(x-1,y,0))return true;
		else if(shipIntactAt(x+1,y,1))return true;
		else if(shipIntactAt(x,y-1,2))return true;
		else if(shipIntactAt(x,y+1,3))return true;
		
		return false;
	}
	
	/**
	 * Rekursive Hilfsmethode für shipIntactAt(x,y)
	 * @param x
	 * @param y
	 * @param dir: Richtung, in die gesucht wird
	 * @return
	 */
	private boolean shipIntactAt(int x, int y, int dir){
		try{
			if(field[x][y] == SHIP)return true;
			if(field[x][y] == WATER || field[x][y] == WATER_HIT)return false;
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
		
		if(dir == 0)return shipIntactAt(x-1,y,0);
		if(dir == 1)return shipIntactAt(x+1,y,1);
		if(dir == 2)return shipIntactAt(x,y-1,2);
		if(dir == 3)return shipIntactAt(x,y+1,3);
		
		return false;
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
			if(remainingShips[i] > 0)return true;
		}
		return false;
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
	 * 			-6: Kein Schiff dieser Art mehr   
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
	public boolean set(int x, int y, int status) {
		try {
			field[x][y] = status;
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
				default: throw new InvalidCoordinateException();
			}
		}catch(Exception e) {
			throw new InvalidCoordinateException();
		}
		return temp;
	}
	
	public int get(int x, int y){
		return field[x][y];
	}
	
	public String getName(){
		return name;
	}
}
