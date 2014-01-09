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
	int WIDTH = 506, HEIGHT = 380, egyseg = 10;
	int palyasz = 50 * egyseg, palyam = 30 * egyseg;
	int pontok, hossz, xvalt, yvalt;
	boolean fut, mehetbalra, mehetjobbra, mehetfel, mehetle, evett, magabament, gameover;
	int[] pozx = new int[125];
	int[] pozy = new int[125];
	Point[] p = new Point[125];
	Random r = new Random();

	JButton[] kocka = new JButton[125];
	JFrame frame;
	SnakeView jatekter;
    JPanel pontszam, top;
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
		pozx[0] = 24 * egyseg;
		pozy[0] = 14 * egyseg;
		pontok = 0;
		hossz = 3;
		xvalt = +egyseg;
		yvalt = 0;
		fut = false;
		magabament = false;
		mehetbalra = false;
		mehetjobbra = true;
		mehetfel = true;
		mehetle = true;
		evett = true;
		gameover = false;
		fajlmegnyitas();
	}

	/*
	 * A mozgatás elindításának függvénye.
	 */
	public void start() {
		fut = true;
		(new Thread(this)).start();
	}

	/*
	 * A Snake() függvény. Ez a program lelke. Itt történik az ablak
	 * létrehozása, az ablak minden elemények hozzáadása, az értékek
	 * inicializálása, az elsõ snake létrehozása, valamint itt híodik meg a
	 * "mozgató" függvény is
	 */
	Snake() {
		// Egy WIDTH, HEIGHT méretekkel rendelkezõ abalak létrehozása
		frame = new JFrame("Snake v0.7");
		frame.setSize(WIDTH, HEIGHT);

		// Az ablak részeinek létrehozása
		jatekter = new SnakeView();
		pontszam = new JPanel();
		top = new JPanel();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Értékek inicializálása és a menü létrehozása
		init();

        // A teljes menü megjelenítése az ablakon
		Menu menu = menu();
		new MenuController(menu, jatekter, model).bind();
        frame.setJMenuBar(menu);

		// A pálya részeinek részletes beállítása (pozíció, szélesség,
		// magasság, szín) és hozzáadása az ablakhoz
		frame.add(jatekter, BorderLayout.CENTER);
		frame.add(pontszam, BorderLayout.SOUTH);
		frame.setLayout(null);
		jatekter.setLayout(null);
		jatekter.setBounds(0, 0, palyasz, palyam);
		jatekter.setBackground(Color.LIGHT_GRAY);
		pontszam.setBounds(0, palyam, palyasz, 30);
		pontszam.setBackground(Color.GRAY);
		top.setBounds(0, 0, palyasz, palyam);
		top.setBackground(Color.LIGHT_GRAY);

		// Keret megrajzolása és hozzáadása a pályához
		keret[0] = new JPanel();
		keret[0].setBounds(0, 0, palyasz, egyseg);
		keret[1] = new JPanel();
		keret[1].setBounds(0, 0, egyseg, palyam);
		keret[2] = new JPanel();
		keret[2].setBounds(0, palyam - egyseg, palyasz, egyseg);
		keret[3] = new JPanel();
		keret[3].setBounds(palyasz - egyseg, 0, egyseg, palyam);
		jatekter.add(keret[0]);
		jatekter.add(keret[1]);
		jatekter.add(keret[2]);
		jatekter.add(keret[3]);

		// Az elsõ snake létrehozása és kirajzolása
		elsoSnake();

		// A pontszám kíírása a képernyõre
		pontkiiras = new JLabel("Pontszám: " + pontok);
		pontkiiras.setForeground(Color.BLACK);
		pontszam.add(pontkiiras);

		// Az ablak beállításai
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.addKeyListener(this);

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
		jatekter.removeAll();
		scrollpane.removeAll();

		// Ha az elõzõ játékban meghalt a kígyó, akkor a játék vége kijelzõ
		// törlése az ablakból
		if (gameover == true) {
			frame.remove(top);
		}

		// A keret hozzáadása a pályához
		jatekter.add(keret[0]);
		jatekter.add(keret[1]);
		jatekter.add(keret[2]);
		jatekter.add(keret[3]);

		// Az elsõ kígyó létrehozása, kirajzolása
		elsoSnake();

		// A pálya hozzáadása az ablakhoz, annak újrarajzolása és a pontszám
		// kiírása
		frame.add(jatekter, BorderLayout.CENTER);
		frame.repaint();
		frame.setVisible(true);
		pontkiiras.setText("Pontszám: " + pontok);

		// A mozgatás elindítása
		start();
	}

	/*
	 * Az elsõ snake létrehozása és a pályára rajzolása.
	 */
	void elsoSnake() {
		// Minden kockát külön rajzol ki a függvény, ezért a ciklus
		for (int i = 0; i < hossz; i++) {
			// Egy "kocka" létrehozása és annak beállításai (helyzet, szín)
			kocka[i] = new JButton();
			kocka[i].setEnabled(false);
			kocka[i].setBounds(pozx[i], pozy[i], egyseg, egyseg);
			kocka[i].setBackground(Color.BLACK);

			// A kocka megjelenítése a pályán
			jatekter.add(kocka[i]);

			// A következõ elem koordinátáinak a megváltoztatása
			pozx[i + 1] = pozx[i] - egyseg;
			pozy[i + 1] = pozy[i];
		}
	}

	/*
	 * Ez a függvény létrehozza az új ételt a pályán random helyen, és
	 * kirajzolja azt
	 */
	void novekszik() {
		// Létrehozza az új ételt, és hozzáadja a pályához
		kocka[hossz] = new JButton();
		kocka[hossz].setEnabled(false);
		kocka[hossz].setBackground(Color.BLACK);
		jatekter.add(kocka[hossz]);

		// Randomgenerátorral létrehozza az étel x,y koordinátáit
		int kajax = 20 + (egyseg * r.nextInt(46));
		int kajay = 20 + (egyseg * r.nextInt(26));

		// Beállítja a koordinátáit a kajának, és kirajzolja azt a megadott
		// pozícióban
		pozx[hossz] = kajax;
		pozy[hossz] = kajay;
		kocka[hossz].setBounds(pozx[hossz], pozy[hossz], egyseg, egyseg);

		// Megnöveli a kígyó hosszát jelzõ változót
		hossz++;
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
		frame.remove(jatekter);

		// Ha az elért eredmény jobb az eddigi legkisebb eredménynél
		if (pontok > lista.get(9).getPoints()) {
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
					frame.dispose();
				}
			});

			// A top panel hozzáadása az ablakhoz, és az ablak újrarajzolása
			frame.add(top, BorderLayout.CENTER);
			frame.setVisible(true);
			frame.repaint();

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
			lista.add(new ToplistEntry(holder.remove(0), pontok));
			Collections.sort(lista);

			// A toplista frissítése, és kirajzolása az ablakra
			toplistafrissites();
			top.removeAll();
			top.add(scrollpane);
			frame.repaint();
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
			frame.add(top, BorderLayout.CENTER);
			frame.setVisible(true);
			frame.repaint();
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
		for (int i = 0; i < hossz; i++) {
			p[i] = kocka[i].getLocation();
		}

		// Megváltoztatja az elsõ elemnek a pozícióját a megadott irányba
		pozx[0] = pozx[0] + xvalt;
		pozy[0] = pozy[0] + yvalt;
		kocka[0].setBounds(pozx[0], pozy[0], egyseg, egyseg);

		// Megváltoztatja a többi elem helyzetét az elõtt lévõ elemére
		for (int i = 1; i < hossz; i++) {
			kocka[i].setLocation(p[i - 1]);
		}

		// Ellenõrzi, hogy a kígyó nem-e ment önmagába
		for (int i = 1; i < hossz - 1; i++) {
			if (p[0].equals(p[i])) {
				magabament = true;
			}
		}

		// Ellenõrzi, hogy a kígyó nem-e ment önmagába vagy falnak. Ha igen
		// akkor a játéknak vége procedúra zajlik le, illetve leáll a mozgatás
		if ((pozx[0] + 10 == palyasz) || (pozx[0] == 0) || (pozy[0] == 0) || (pozy[0] + 10 == palyam) || (magabament == true)) {
			fut = false;
			gameover = true;
			toplistabatesz();
		}

		// Ellenõrzi, hogy a kígyó nem érte-e el az ételt. Ha igen akkor növeli
		// a pontszámot
		if (pozx[0] == pozx[hossz - 1] && pozy[0] == pozy[hossz - 1]) {
			evett = true;
			pontok = pontok + 5;
			pontkiiras.setText("Pontszám: " + pontok);
		}

		// Ha evett, akkor létrehozza az új ételt és növeli a kígyót, különben
		// az étel ott marad ahol volt
		if (evett == true) {
			novekszik();
			evett = false;
		} else {
			kocka[hossz - 1].setBounds(pozx[hossz - 1], pozy[hossz - 1], egyseg, egyseg);
		}

		// A pálya frissítése
		jatekter.repaint();
		frame.setVisible(true);
	}

	/*
	 * A billentyû lenyomását érzékelõ függvény, mely megfelelõ gomb lenyomására
	 * a megfelelõ mûveletet hajtja végre
	 */
	public void keyPressed(KeyEvent e) {
		if (mehetbalra == true && e.getKeyCode() == 37) {
			xvalt = -egyseg;
			yvalt = 0;
			mehetjobbra = false;
			mehetfel = true;
			mehetle = true;
		}
		if (mehetfel == true && e.getKeyCode() == 38) {
			xvalt = 0;
			yvalt = -egyseg;
			mehetle = false;
			mehetjobbra = true;
			mehetbalra = true;
		}
		if (mehetjobbra == true && e.getKeyCode() == 39) {
			xvalt = +egyseg;
			yvalt = 0;
			mehetbalra = false;
			mehetfel = true;
			mehetle = true;
		}
		if (mehetle == true && e.getKeyCode() == 40) {
			xvalt = 0;
			yvalt = +egyseg;
			mehetfel = false;
			mehetjobbra = true;
			mehetbalra = true;
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
		while (fut) {
			mozgat();
			try {
				Thread.sleep(model.getDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
