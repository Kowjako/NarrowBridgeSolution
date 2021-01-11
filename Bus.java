import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/* 
 *     Plik: Bus.java
 *           
 *          Program do przedstawienia pojedynczego pojazdu
 *           
 *    Autor: Uladzimir Kaviaka(257276)
 *     Data: 29 grudnia 2020 r.
 */

enum DirectionOfBus {
	LEFT, RIGHT;
	String typeMove;
	
	public String toString() {
		switch (this) {
		case LEFT:
			return "LEFT";
		case RIGHT:
			return "RIGHT";
		}
		return "notype";
	}
}

public class Bus implements Runnable {

	public static final int MIN_START_TIME = 1000;
	public static final int MAX_START_TIME = 5000;
	public static final int GO_TO_BRIDGE_TIME = 800;
	public static final int RIDE_OVER_BRIDGE = 2000;
	public static final int GO_TO_PARKING_TIME = 400;
	public static final int END_TIME = 400;
	
	Random r = new Random();
	private static int numOfBuses = 1;
	BusesWindow bridge;
	int id;
	DirectionOfBus dir;
	
	public void sleep(int start, int end) {
		try {
			Thread.sleep(end-start);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Bus(BusesWindow bridge) {
		this.bridge = bridge;
		this.id = numOfBuses++;
		if (r.nextInt(2) == 0)
			dir = DirectionOfBus.LEFT;
		else
			dir = DirectionOfBus.RIGHT;
	}
	
	public String printCurrentInfo(String msg) {
		return "Bus[" + id + "->" + dir + "]: " + msg + "\n";
	}
	
	void startMove() {
		bridge.infoArea.append(printCurrentInfo("Zaczynam ruch"));
		sleep(MIN_START_TIME, MAX_START_TIME);
	}
	void goToBridge() {
		bridge.infoArea.append(printCurrentInfo("Jade w strone mostu"));
		sleep(GO_TO_BRIDGE_TIME);
	}
	
	void rideOverBridge() {
		bridge.infoArea.append(printCurrentInfo("Przejezdzam most"));
		sleep(RIDE_OVER_BRIDGE);
	}
	
	void goToParking() {
		bridge.infoArea.append(printCurrentInfo("Jade do punktu koncowego"));
		sleep(GO_TO_PARKING_TIME);
	}
	
	void finish() {
		bridge.infoArea.append(printCurrentInfo("Koncze swoja prace"));
		sleep(END_TIME);
	}
	@Override
	public void run() {
		bridge.allBuses.add(this);
		startMove();
		goToBridge();
		bridge.getOnTheBridge(this);
		rideOverBridge();
		bridge.getOffTheBridge(this);
		goToParking();
		finish();
		bridge.allBuses.remove(this);
	}

}
