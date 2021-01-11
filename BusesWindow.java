import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/* 
 *     Plik: BusesWindow.java
 *           
 *          Program pokazuje rozwiazanie problemu przejazdu przez waski most
 *           
 *    Autor: Uladzimir Kaviaka(257276)
 *     Data: 29 grudnia 2020 r.
 */


enum TypeMove {
	NOLIMITS("ruch bez ograniczen"),
	TWODIRECTIONS("ruch w dwie strony, maksimum 2 pojazdy"),
	ONEDIRECTION("ruch jednokierunkowy, maksimum 2 pojazdy"),
	ONEBUS("przez most moze przejechac tylko jeden pojazd");

	String type;

	private TypeMove(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}

public class BusesWindow extends JFrame implements ChangeListener, ActionListener{

	private static final long serialVersionUID = 1L;
	private static final String authorStr = "Autor: Uladzimir Kaviaka\n Data: 29 grudnia 2020";
	private static final String infoStr = "Program przedstawia rozwiazanie problemu\n"
			+ "przejazdu przez waski most, dostepne 3 typy ruchu\n"
			+ "1. ruch bez ograniczen: liczba pojazdow na moscie nie jest ograniczona\n"
			+ "oraz pojazdy nie zatrzymywane przed wejsciem na most\n"
			+ "2. ruch dwukierunkowy: pojazdy moga jechac w dwie strony ale na moscie\n"
			+ "moze sie znajdowac jednoczesnie tylko 2 pojazdy\n"
			+ "3. ruch jednokierunkowy: pojazdy moga przejezdzac przez most tylko w jedna strone\n"
			+ "oraz na moscie moga byc jednoczesnie tylko 2 pojazdy\n"
			+ "4. jeden pojazd: przez most przejezdza tylko 1 pojazd";
	List<Bus> allBuses = new LinkedList<Bus>();
	List<Bus> busesWaiting = new LinkedList<Bus>(); 
	List<Bus> busesOnTheBridge = new LinkedList<Bus>();
	DirectionOfBus oneDirectionMode = DirectionOfBus.RIGHT;
	DirectionOfBus oneDirectionModePrevious;
	static TypeMove moveType = TypeMove.ONEBUS;
	
	private static int TRAFFIC = 2000;
	Font font = new Font("Comic Sans", Font.BOLD, 12);
	JMenuBar menuBar = new JMenuBar();
	JMenu menu = new JMenu("Informacja");
	JMenu exit = new JMenu("Wyjscie");
	JMenuItem infoItem = new JMenuItem("Instrukcja");
	JMenuItem authorItem = new JMenuItem("Autor");
	JMenuItem exitItem = new JMenuItem("Zakoncz");
	JLabel typeLabel = new JLabel("Typ ruchu: ");
	JLabel powerLabel = new JLabel("Natezenie ruchu: ");
	JLabel bridgeLabel = new JLabel("Na moscie: ");
	JLabel queueLabel = new JLabel("W kolejce: ");
	JComboBox<TypeMove> typeBox = new JComboBox<TypeMove>(TypeMove.values());
	JSlider powerSlider = new JSlider(JSlider.HORIZONTAL, 0, 5000, TRAFFIC);
	JTextField bridgeText = new JTextField();
	JTextField queueText = new JTextField();
	JTextArea infoArea = new JTextArea();
	JScrollPane scroll;
	
	public static void main(String[] args) {
		BusesWindow bridge = new BusesWindow();
		while (true) {
			Bus bus = new Bus(bridge);
			new Thread(bus).start();
			try {
				Thread.sleep(5500 - TRAFFIC);
			} catch (InterruptedException e) {
			}
		}
	}

	public BusesWindow() {
		setLayout(new BorderLayout());
		setTitle("Problem przejazdu przez waski most");
		setSize(500, 800);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel(null);
		panel.setBackground(Color.PINK);

		typeLabel.setBounds(10, 10, 100, 20);
		panel.add(typeLabel);

		powerLabel.setBounds(10, 35, 100, 20);
		panel.add(powerLabel);

		bridgeLabel.setBounds(10, 80, 100, 20);
		panel.add(bridgeLabel);

		queueLabel.setBounds(10, 105, 100, 20);
		panel.add(queueLabel);
		
		typeBox.setBounds(140, 10, 330, 20);
		typeBox.setSelectedItem(TypeMove.ONEBUS);
		typeBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {    /* Wybranie typu ruchu */
				moveType = (TypeMove)e.getItem();
			}
		});
		panel.add(typeBox);
		
		authorItem.addActionListener(this);
		infoItem.addActionListener(this);
		exitItem.addActionListener(this);
		
		menu.add(infoItem);
		menu.add(authorItem);
		exit.add(exitItem);
		menuBar.add(menu);
		menuBar.add(exit);
		setJMenuBar(menuBar);
		
		Hashtable table1 = new Hashtable();
		table1.put(new Integer(0), new JLabel("Male"));
		table1.put(new Integer(5000), new JLabel("Duze"));
		powerSlider.setLabelTable(table1);
		powerSlider.setBackground(Color.PINK);
		powerSlider.setPaintLabels(true);
		powerSlider.setBounds(125, 35, 355, 40);
		powerSlider.addChangeListener(this);
		panel.add(powerSlider);
		
		bridgeText.setBounds(140,80,330,20);
		bridgeText.setEditable(false);
		panel.add(bridgeText);
		
		queueText.setBounds(140,105,330,20);
		queueText.setEditable(false);
		panel.add(queueText);
		
		infoArea.setEditable(false);
		infoArea.setLineWrap(true);
		infoArea.setWrapStyleWord(true);
		
		scroll = new JScrollPane(infoArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(10,135,460,590);
		panel.add(scroll);
		
		setContentPane(panel); 
		setVisible(true); 
	}
	void printBridgeInfo(Bus bus, String message) {
		printQueueInfo();
		printBusesOnBridge();
		StringBuilder sb = new StringBuilder();
		sb.append(bus.printCurrentInfo(message));
		sb.append("Sa na moscie: ");
		for (Bus b : busesOnTheBridge)
			sb.append(b.id + "  ");
		sb.append("W kolejce: ");
		for (Bus b : busesWaiting)
			sb.append(b.id + "  ");
		sb.append("\n");
		infoArea.append(sb.toString());
	}
	
	synchronized void getOnTheBridge(Bus bus) {
		switch (moveType) {
		case NOLIMITS:
			busesOnTheBridge.add(bus);
			printBridgeInfo(bus, "AUTOBUS WYJEZDZA NA MOST");
			break;
		case ONEBUS:
			while (!busesOnTheBridge.isEmpty()) {
				busesWaiting.add(bus);
				printBridgeInfo(bus, "AUTOBUS CZEKA NA WJAZD");
				try {
					wait();
				} catch (InterruptedException e) {
				}
				busesWaiting.remove(bus);
			}
			busesOnTheBridge.add(bus);
			printBridgeInfo(bus, "AUTOBUS WYJEZDZA NA MOST");
			break;
		case TWODIRECTIONS:
			while (busesOnTheBridge.size() >= 2) {
				busesWaiting.add(bus);
				printBridgeInfo(bus, "AUTOBUS CZEKA NA WJAZD");
				try {
					wait();
				} catch (InterruptedException e) {
				}
				busesWaiting.remove(bus);
			}
			busesOnTheBridge.add(bus);
			printBridgeInfo(bus, "AUTOBUS WYJEZDZA NA MOST");
			break;
		case ONEDIRECTION:
			if (countBusesDirection(oneDirectionMode) >= 5) /* regulacja zaglodzenia jednego kierunku */
			{
				if (oneDirectionMode == DirectionOfBus.LEFT) {
					oneDirectionMode = DirectionOfBus.RIGHT;
					oneDirectionModePrevious = DirectionOfBus.LEFT;
				} else {
					oneDirectionMode = DirectionOfBus.LEFT;
					oneDirectionModePrevious = DirectionOfBus.RIGHT;
				}
			}
			while (busesOnTheBridge.size() >= 2 || bus.dir == oneDirectionMode || countBusesOnBridge(oneDirectionModePrevious) != 0) {
				busesWaiting.add(bus);
				printBridgeInfo(bus, "AUTOBUS CZEKA NA WJAZD");
				try {
					wait();
				} catch (InterruptedException e) {
				}
				busesWaiting.remove(bus);
			}
			busesOnTheBridge.add(bus);
			printBridgeInfo(bus, "AUTOBUS WYJEZDZA NA MOST");
			break;
		default:
			break;
		}
	}
	
	int countBusesOnBridge(DirectionOfBus d) {
		int count = 0;
		for(Bus b : busesOnTheBridge) {
			if(b.dir == d)
				count++;
		}
		return count;
	}
	int countBusesDirection(DirectionOfBus d) {
		int count = 0;
		for(Bus b : busesWaiting) {
			if(b.dir == d)
				count++;
		}
		return count;
	}
	
	synchronized void getOffTheBridge(Bus bus){
		busesOnTheBridge.remove(bus);
		printBridgeInfo(bus, "AUTOBUS OPUSZCZA MOST");
		notify();
	}
	
	void printQueueInfo() {
		StringBuilder sb = new StringBuilder();
		for(Bus tmp: busesWaiting) {
			sb.append(tmp.id+" ");
		}
		queueText.setText(sb.toString());
	}
	
	void printBusesOnBridge() {
		StringBuilder sb = new StringBuilder();
		for(Bus tmp: busesOnTheBridge) {
			sb.append(tmp.id+" ");
		}
		bridgeText.setText(sb.toString());
	}
	public void stateChanged(ChangeEvent e) {
	    JSlider tmp = (JSlider)e.getSource();
	    if (!tmp.getValueIsAdjusting()) {
	        int value = (int)tmp.getValue();
	        BusesWindow.TRAFFIC = value;
	    }
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object sender = arg0.getSource();
		if(sender==authorItem) {
			JOptionPane.showMessageDialog(this, authorStr, "Okno informacji", JOptionPane.INFORMATION_MESSAGE);
		}
		if(sender==exitItem) {
			System.exit(0);
		}
		if(sender==infoItem) {
			JOptionPane.showMessageDialog(this, infoStr, "Okno informacji", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
}
