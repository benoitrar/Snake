package snake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import snake.controller.MenuController;
import snake.model.SnakeModel;
import snake.model.ToplistEntry;
import snake.view.Menu;
import snake.view.SnakeView;

public class Snake extends JFrame implements KeyListener, Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int width = 506, height = 380, unit = 10;
	int boardWidth = 50 * unit, boardHeight = 30 * unit;
	int actualPoints, snakeLength, xCoordChange, yCoordChange;
	boolean run, canGoToLeft, canGoToRight, canGoUpwards, canGoDownwards, hasEaten, crashedItself, gameover;
	int[] xPosition = new int[125];
	int[] yPosition = new int[125];
	Point[] points = new Point[125];
	Random random = new Random();

	JButton[] pieces = new JButton[125];
	SnakeView board;
    JPanel pointsPanel, top;
	JPanel[] keret = new JPanel[4];
	JMenuBar menubar;
	JMenu jatek, beallitasok, segitseg;
	JLabel pontkiiras;
	JScrollPane scrollpane;

	ArrayList<ToplistEntry> lista = new ArrayList<ToplistEntry>();
//	{
//	    for(int i=0;i<10;i++) {
//	        lista.add(new ToplistEntry("", 0));
//	    }
//	}
    private final SnakeModel model = new SnakeModel();

	/*
	 * Az értékek alaphelyzetbe állítása és a toplistát tartalmazó fájl
	 * megnyitása
	 */
	public void init() {
		xPosition[0] = 24 * unit;
		yPosition[0] = 14 * unit;
		actualPoints = 0;
		snakeLength = 3;
		xCoordChange = +unit;
		yCoordChange = 0;
		run = false;
		crashedItself = false;
		canGoToLeft = false;
		canGoToRight = true;
		canGoUpwards = true;
		canGoDownwards = true;
		hasEaten = true;
		gameover = false;
		fajlmegnyitas();
	}

	/*
	 * A mozgatás elindításának függvénye.
	 */
	public void start() {
		run = true;
		(new Thread(this)).start();
	}

	/*
	 * A Snake() függvény. Ez a program lelke. Itt történik az ablak
	 * létrehozása, az ablak minden elemények hozzáadása, az értékek
	 * inicializálása, az elsõ snake létrehozása, valamint itt híodik meg a
	 * "mozgató" függvény is
	 */
	public Snake() {
	    
		super("Snake v0.7");
		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Az ablak részeinek létrehozása
		board = new SnakeView();
		pointsPanel = new JPanel();
		top = new JPanel();

		// Értékek inicializálása
		init();

        // A teljes menü megjelenítése az ablakon
		Menu menu = menu();
		new MenuController(menu, board, model).bind();

		// A pálya részeinek részletes beállítása (pozíció, szélesség,
		// magasság, szín) és hozzáadása az ablakhoz
		board.setLayout(null);
		board.setBounds(0, 0, boardWidth, boardHeight);
		board.setBackground(Color.LIGHT_GRAY);
		pointsPanel.setBounds(0, boardHeight, boardWidth, 30);
		pointsPanel.setBackground(Color.GRAY);
		top.setBounds(0, 0, boardWidth, boardHeight);
		top.setBackground(Color.LIGHT_GRAY);

		// Keret megrajzolása és hozzáadása a pályához
		keret[0] = new JPanel();
		keret[0].setBounds(0, 0, boardWidth, unit);
		keret[1] = new JPanel();
		keret[1].setBounds(0, 0, unit, boardHeight);
		keret[2] = new JPanel();
		keret[2].setBounds(0, boardHeight - unit, boardWidth, unit);
		keret[3] = new JPanel();
		keret[3].setBounds(boardWidth - unit, 0, unit, boardHeight);
		board.add(keret[0]);
		board.add(keret[1]);
		board.add(keret[2]);
		board.add(keret[3]);

		// Az elsõ snake létrehozása és kirajzolása
		elsoSnake();

		// A pontszám kíírása a képernyõre
		pontkiiras = new JLabel("Pontszám: " + actualPoints);
		pontkiiras.setForeground(Color.BLACK);
		pointsPanel.add(pontkiiras);

        setJMenuBar(menu);
        add(board, BorderLayout.CENTER);
        add(pointsPanel, BorderLayout.SOUTH);
        setLayout(null);
        
		// Az ablak beállításai
		setResizable(false);
		setLocationRelativeTo(null);
		addKeyListener(this);
		setVisible(true);

		// A mozgatás elindítása
		start();
	}

	/*
	 * Ez a menüt létrehozõ függvény. Létrehozza a menüket, hozzáadja a
	 * funkcióikat, és a képernyõre viszi azokat
	 */
	public Menu menu() {
		return new Menu();
	}

	/*
	 * Az újraindító függvény. Ennek meghívásakor az érték újra alapállapotba
	 * kerülnek, ami eddig az ablakon volt az eltûnik, a mozgatás megáll, a
	 * keret, az elsõ snake és a pontszám újra kirajzoldik, és meghívódik a
	 * mozgató függvény
	 */
	void reset() {
		// Az értékek kezdeti helyzetbe állítása
		init();

		// A pálya lepucolása
		board.removeAll();
		scrollpane.removeAll();

		// Ha az elõzõ játékban meghalt a kígyó, akkor a játék vége kijelzõ
		// törlése az ablakból
		if (gameover == true) {
			remove(top);
		}

		// A keret hozzáadása a pályához
		board.add(keret[0]);
		board.add(keret[1]);
		board.add(keret[2]);
		board.add(keret[3]);

		// Az elsõ kígyó létrehozása, kirajzolása
		elsoSnake();

		// A pálya hozzáadása az ablakhoz, annak újrarajzolása és a pontszám
		// kiírása
		add(board, BorderLayout.CENTER);
		repaint();
		setVisible(true);
		pontkiiras.setText("Pontszám: " + actualPoints);

		// A mozgatás elindítása
		start();
	}

	/*
	 * Az elsõ snake létrehozása és a pályára rajzolása.
	 */
	void elsoSnake() {
		// Minden kockát külön rajzol ki a függvény, ezért a ciklus
		for (int i = 0; i < snakeLength; i++) {
			// Egy "kocka" létrehozása és annak beállításai (helyzet, szín)
			pieces[i] = new JButton();
			pieces[i].setEnabled(false);
			pieces[i].setBounds(xPosition[i], yPosition[i], unit, unit);
			pieces[i].setBackground(Color.BLACK);

			// A kocka megjelenítése a pályán
			board.add(pieces[i]);

			// A következõ elem koordinátáinak a megváltoztatása
			xPosition[i + 1] = xPosition[i] - unit;
			yPosition[i + 1] = yPosition[i];
		}
	}

	/*
	 * Ez a függvény létrehozza az új ételt a pályán random helyen, és
	 * kirajzolja azt
	 */
	void novekszik() {
		// Létrehozza az új ételt, és hozzáadja a pályához
		pieces[snakeLength] = new JButton();
		pieces[snakeLength].setEnabled(false);
		pieces[snakeLength].setBackground(Color.BLACK);
		board.add(pieces[snakeLength]);

		// Randomgenerátorral létrehozza az étel x,y koordinátáit
		int kajax = 20 + (unit * random.nextInt(46));
		int kajay = 20 + (unit * random.nextInt(26));

		// Beállítja a koordinátáit a kajának, és kirajzolja azt a megadott
		// pozícióban
		xPosition[snakeLength] = kajax;
		yPosition[snakeLength] = kajay;
		pieces[snakeLength].setBounds(xPosition[snakeLength], yPosition[snakeLength], unit, unit);

		// Megnöveli a kígyó hosszát jelzõ változót
		snakeLength++;
	}

	/*
	 * A fájlmegnyitó függvény megnyitja a "toplist.ser" nevû fájlt, mely a
	 * toplista szereplõit tartalmazza és ezeket a lista nevû ArrayListben
	 * eltárolja (deszerializálás)
	 */
	@SuppressWarnings("unchecked")
	void fajlmegnyitas() {
		// A fájl megnyitása
		try {
			InputStream file = new FileInputStream("toplista.ser");
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput in;
			in = new ObjectInputStream(buffer);

			// A fájl tartalmának bemásolása a lista ArrayListbe
			lista = (ArrayList<ToplistEntry>) in.readObject();

			// A fájl bezárása
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * A fájlbaíró függvény a "toplista.ser" nevû fájlba beírja a legfrissebb
	 * toplista szereplõit (szerializálás)
	 */
	void fajlbairas() {
		// A fájl megnyitása
		try {
			OutputStream file = new FileOutputStream("toplista.ser");

			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput out;
			out = new ObjectOutputStream(buffer);

			// A lista ArrayList fájlba írása
			out.writeObject(lista);

			// A fájl bezárása
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Ez a függvény a játék végét vizsgálja. Megnézi, hogy a kígyó halála után
	 * felkerül-e a toplistára a játékos az elért eredményével. Ha igen akkor
	 * bekéri a nevét, és frissíti a toplistát. Ha nem akkor egy játék vége
	 * képernyõt rajzol ki. A végén pedig szerializál.
	 */
	void toplistabatesz() {
		// A pálya törlése a képernyõrõl.
		remove(board);

		// Ha az elért eredmény jobb az eddigi legkisebb eredménynél
		if (actualPoints > lista.get(9).getPoints()) {
			// Egy ArrayList létrehozása, mely a megadott nevet tárolja
			final ArrayList<String> holder = new ArrayList<String>();

			// A kiírások és a szövegmezõ létrehozása
			JLabel nyert1 = new JLabel("A játéknak vége!");
			JLabel nyert2 = new JLabel("Gratulálok! Felkerültél a toplistára. Kérlek add meg a neved (max 10 betû):");
			final JTextField newnev = new JTextField(10);

			// Ezek hozzáadása a top panelhez
			top.removeAll();
			top.add(nyert1);
			top.add(nyert2);
			top.add(newnev);

			// A szövegmezõ tartalmának hozzásadása a holderhez
			newnev.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					synchronized (holder) {
						holder.add(newnev.getText());
						holder.notify();
					}
					dispose();
				}
			});

			// A top panel hozzáadása az ablakhoz, és az ablak újrarajzolása
			add(top, BorderLayout.CENTER);
			setVisible(true);
			repaint();

			// Várakozás a szövegezõ kitöltéséig
			synchronized (holder) {
				while (holder.isEmpty())
					try {
						holder.wait();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
			}

			// A lista utolsó elemének kicserélése az új listaelemmel és a lista
			// sorbarendezése
			lista.remove(9);
			lista.add(new ToplistEntry(holder.remove(0), actualPoints));
			Collections.sort(lista);

			// A toplista frissítése, és kirajzolása az ablakra
			toplistafrissites();
			top.removeAll();
			top.add(scrollpane);
			repaint();
			// Ha az eredmény nincs bent a legjobb 10-be
		} else {
			// A kiirások létrehozása és hozzáadása az ablakhoz
			JLabel nemnyert1 = new JLabel("A játéknak vége!");
			JLabel nemnyert2 = new JLabel("Sajnos nem került be az eredményed a legjobb 10-be. Próbálkozz újra (F2).");
			nemnyert1.setForeground(Color.BLACK);
			nemnyert2.setForeground(Color.BLACK);
			top.removeAll();
			top.add(nemnyert1);
			top.add(nemnyert2);

			// A toplista frissítése és a top panel hozzáadása az ablakhoz
			toplistafrissites();
			add(top, BorderLayout.CENTER);
			setVisible(true);
			repaint();
		}
		// Szerializálás
		fajlbairas();
	}

	/*
	 * Ez a függvény a toplistát egy táblázatba rakja
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void toplistafrissites() {
		// A táblázat fejlécének létrehozása
		Vector colnames = new Vector();
		colnames.add("Név");
		colnames.add("Pont");

		// A táblázat létrehozása egy ScrollPane-ben
		DefaultTableModel tablazatmodell = new DefaultTableModel(colnames, 0);
		JTable tablazat = new JTable(tablazatmodell);
		scrollpane = new JScrollPane(tablazat);

		// A táblázat feltöltése a lista elemeivel
		for (ToplistEntry i : lista) {
			String[] row = { i.getUserName(), String.valueOf(i.getPoints()) };
			tablazatmodell.addRow(row);
		}

	}

	/*
	 * A mozgató függvény megváltoztatja a kígyó pozícióját a megadott irányba,
	 * és közben vizsgálja, hogy a kígyó nem ütközött-e falnak vagy magának,
	 * illetve azt, hogy evett-e
	 */
	void mozgat() {
		// Lekéri a kígyó összes elemének pozícióját a pályán
		for (int i = 0; i < snakeLength; i++) {
			points[i] = pieces[i].getLocation();
		}

		// Megváltoztatja az elsõ elemnek a pozícióját a megadott irányba
		xPosition[0] = xPosition[0] + xCoordChange;
		yPosition[0] = yPosition[0] + yCoordChange;
		pieces[0].setBounds(xPosition[0], yPosition[0], unit, unit);

		// Megváltoztatja a többi elem helyzetét az elõtt lévõ elemére
		for (int i = 1; i < snakeLength; i++) {
			pieces[i].setLocation(points[i - 1]);
		}

		// Ellenõrzi, hogy a kígyó nem-e ment önmagába
		for (int i = 1; i < snakeLength - 1; i++) {
			if (points[0].equals(points[i])) {
				crashedItself = true;
			}
		}

		// Ellenõrzi, hogy a kígyó nem-e ment önmagába vagy falnak. Ha igen
		// akkor a játéknak vége procedúra zajlik le, illetve leáll a mozgatás
		if ((xPosition[0] + 10 == boardWidth) || (xPosition[0] == 0) || (yPosition[0] == 0) || (yPosition[0] + 10 == boardHeight) || (crashedItself == true)) {
			run = false;
			gameover = true;
			toplistabatesz();
		}

		// Ellenõrzi, hogy a kígyó nem érte-e el az ételt. Ha igen akkor növeli
		// a pontszámot
		if (xPosition[0] == xPosition[snakeLength - 1] && yPosition[0] == yPosition[snakeLength - 1]) {
			hasEaten = true;
			actualPoints = actualPoints + 5;
			pontkiiras.setText("Pontszám: " + actualPoints);
		}

		// Ha evett, akkor létrehozza az új ételt és növeli a kígyót, különben
		// az étel ott marad ahol volt
		if (hasEaten == true) {
			novekszik();
			hasEaten = false;
		} else {
			pieces[snakeLength - 1].setBounds(xPosition[snakeLength - 1], yPosition[snakeLength - 1], unit, unit);
		}

		// A pálya frissítése
		board.repaint();
		setVisible(true);
	}

	/*
	 * A billentyû lenyomását érzékelõ függvény, mely megfelelõ gomb lenyomására
	 * a megfelelõ mûveletet hajtja végre
	 */
	public void keyPressed(KeyEvent e) {
		if (canGoToLeft == true && e.getKeyCode() == 37) {
			xCoordChange = -unit;
			yCoordChange = 0;
			canGoToRight = false;
			canGoUpwards = true;
			canGoDownwards = true;
		}
		if (canGoUpwards == true && e.getKeyCode() == 38) {
			xCoordChange = 0;
			yCoordChange = -unit;
			canGoDownwards = false;
			canGoToRight = true;
			canGoToLeft = true;
		}
		if (canGoToRight == true && e.getKeyCode() == 39) {
			xCoordChange = +unit;
			yCoordChange = 0;
			canGoToLeft = false;
			canGoUpwards = true;
			canGoDownwards = true;
		}
		if (canGoDownwards == true && e.getKeyCode() == 40) {
			xCoordChange = 0;
			yCoordChange = +unit;
			canGoUpwards = false;
			canGoToRight = true;
			canGoToLeft = true;
		}
		if (e.getKeyCode() == 113) {
			reset();
		}
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

	/*
	 * A run metódus hivja meg megadott idõközönként a mozgató függvényt
	 */
	public void run() {
		while (run) {
			mozgat();
			try {
				Thread.sleep(model.getDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
