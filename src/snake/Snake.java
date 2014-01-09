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
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import snake.controller.MenuController;
import snake.model.Position;
import snake.model.SnakeModel;
import snake.model.ToplistEntry;
import snake.view.Menu;
import snake.view.SnakeView;

public class Snake extends JFrame implements KeyListener, Runnable {
    
	private static final long serialVersionUID = 1L;
	
	private static ArrayList<ToplistEntry> toplist = new ArrayList<ToplistEntry>();

	private final int width = 506;
	private final int height = 380;
	private final int unit = 10;
	private final int boardWidth = 50 * unit;
	private final int boardHeight = 30 * unit;
    
	private final SnakeModel model = new SnakeModel();
	private final List<Position> positions = new ArrayList<>();
    private final Point[] points = new Point[125];
    private final Random random = new Random();

    private final JButton[] pieces = new JButton[125];
    private final SnakeView board;
    private final JPanel pointsPanel, top;
    private final JPanel[] frame = new JPanel[4];
    private final JLabel pointsLabel;
    private final JScrollPane scrollPane = new JScrollPane();
	
	private int actualPoints;
	private int snakeLength;
	private int xCoordChange;
	private int yCoordChange;
	
	private boolean run;
	private boolean canGoToLeft;
	private boolean canGoToRight;
	private boolean canGoUpwards;
	private boolean canGoDownwards;
	private boolean hasEaten;
	private boolean crashedItself;
	private boolean gameover;


	/*
	 * Az �rt�kek alaphelyzetbe �ll�t�sa �s a toplist�t tartalmaz� f�jl
	 * megnyit�sa
	 */
	public void init() {
	    int x = 24 * unit;
	    int y = 14 * unit;
	    positions.add(new Position(x, y));
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
	 * A mozgat�s elind�t�s�nak f�ggv�nye.
	 */
	public void start() {
		run = true;
		(new Thread(this)).start();
	}

	/*
	 * A Snake() f�ggv�ny. Ez a program lelke. Itt t�rt�nik az ablak
	 * l�trehoz�sa, az ablak minden elem�nyek hozz�ad�sa, az �rt�kek
	 * inicializ�l�sa, az els� snake l�trehoz�sa, valamint itt h�odik meg a
	 * "mozgat�" f�ggv�ny is
	 */
	public Snake() {
	    
		super("Snake v0.7");
		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Az ablak r�szeinek l�trehoz�sa
		board = new SnakeView();
		pointsPanel = new JPanel();
		top = new JPanel();

		// �rt�kek inicializ�l�sa
		init();

        // A teljes men� megjelen�t�se az ablakon
		Menu menu = menu();
		new MenuController(menu, board, model).bind();

		// A p�lya r�szeinek r�szletes be�ll�t�sa (poz�ci�, sz�less�g,
		// magass�g, sz�n) �s hozz�ad�sa az ablakhoz
		board.setLayout(null);
		board.setBounds(0, 0, boardWidth, boardHeight);
		board.setBackground(Color.LIGHT_GRAY);
		pointsPanel.setBounds(0, boardHeight, boardWidth, 30);
		pointsPanel.setBackground(Color.GRAY);
		top.setBounds(0, 0, boardWidth, boardHeight);
		top.setBackground(Color.LIGHT_GRAY);

		// Keret megrajzol�sa �s hozz�ad�sa a p�ly�hoz
		frame[0] = new JPanel();
		frame[0].setBounds(0, 0, boardWidth, unit);
		frame[1] = new JPanel();
		frame[1].setBounds(0, 0, unit, boardHeight);
		frame[2] = new JPanel();
		frame[2].setBounds(0, boardHeight - unit, boardWidth, unit);
		frame[3] = new JPanel();
		frame[3].setBounds(boardWidth - unit, 0, unit, boardHeight);
		board.add(frame[0]);
		board.add(frame[1]);
		board.add(frame[2]);
		board.add(frame[3]);

		// Az els� snake l�trehoz�sa �s kirajzol�sa
		elsoSnake();

		// A pontsz�m k��r�sa a k�perny�re
		pointsLabel = new JLabel("Pontsz�m: " + actualPoints);
		pointsLabel.setForeground(Color.BLACK);
		pointsPanel.add(pointsLabel);

        setJMenuBar(menu);
        add(board, BorderLayout.CENTER);
        add(pointsPanel, BorderLayout.SOUTH);
        setLayout(null);
        
		// Az ablak be�ll�t�sai
		setResizable(false);
		setLocationRelativeTo(null);
		addKeyListener(this);
		setVisible(true);

		// A mozgat�s elind�t�sa
		start();
	}

	/*
	 * Ez a men�t l�trehoz� f�ggv�ny. L�trehozza a men�ket, hozz�adja a
	 * funkci�ikat, �s a k�perny�re viszi azokat
	 */
	public Menu menu() {
		return new Menu();
	}

	/*
	 * Az �jraind�t� f�ggv�ny. Ennek megh�v�sakor az �rt�k �jra alap�llapotba
	 * ker�lnek, ami eddig az ablakon volt az elt�nik, a mozgat�s meg�ll, a
	 * keret, az els� snake �s a pontsz�m �jra kirajzoldik, �s megh�v�dik a
	 * mozgat� f�ggv�ny
	 */
	void reset() {
		// Az �rt�kek kezdeti helyzetbe �ll�t�sa
		init();

		// A p�lya lepucol�sa
		board.removeAll();
		scrollPane.removeAll();

		// Ha az el�z� j�t�kban meghalt a k�gy�, akkor a j�t�k v�ge kijelz�
		// t�rl�se az ablakb�l
		if (gameover == true) {
			remove(top);
		}

		// A keret hozz�ad�sa a p�ly�hoz
		board.add(frame[0]);
		board.add(frame[1]);
		board.add(frame[2]);
		board.add(frame[3]);

		// Az els� k�gy� l�trehoz�sa, kirajzol�sa
		elsoSnake();

		// A p�lya hozz�ad�sa az ablakhoz, annak �jrarajzol�sa �s a pontsz�m
		// ki�r�sa
		add(board, BorderLayout.CENTER);
		repaint();
		setVisible(true);
		pointsLabel.setText("Pontsz�m: " + actualPoints);

		// A mozgat�s elind�t�sa
		start();
	}

	/*
	 * Az els� snake l�trehoz�sa �s a p�ly�ra rajzol�sa.
	 */
	void elsoSnake() {
		// Minden kock�t k�l�n rajzol ki a f�ggv�ny, ez�rt a ciklus
		for (int i = 0; i < snakeLength; i++) {
			// Egy "kocka" l�trehoz�sa �s annak be�ll�t�sai (helyzet, sz�n)
			pieces[i] = new JButton();
			pieces[i].setEnabled(false);
			Position lastPos = positions.get(i);
			pieces[i].setBounds(lastPos.getX(), lastPos.getY(), unit, unit);
			pieces[i].setBackground(Color.BLACK);

			// A kocka megjelen�t�se a p�ly�n
			board.add(pieces[i]);

			// A k�vetkez� elem koordin�t�inak a megv�ltoztat�sa
			positions.add(new Position(lastPos.getX() - unit, lastPos.getY()));
		}
	}

	/*
	 * Ez a f�ggv�ny l�trehozza az �j �telt a p�ly�n random helyen, �s
	 * kirajzolja azt
	 */
	void novekszik() {
	    System.out.println("Hello");
		// L�trehozza az �j �telt, �s hozz�adja a p�ly�hoz
		pieces[snakeLength] = new JButton();
		pieces[snakeLength].setEnabled(false);
		pieces[snakeLength].setBackground(Color.BLACK);

		// Randomgener�torral l�trehozza az �tel x,y koordin�t�it
		int kajax = 20 + (unit * random.nextInt(46));
		int kajay = 20 + (unit * random.nextInt(26));

		// Be�ll�tja a koordin�t�it a kaj�nak, �s kirajzolja azt a megadott
		// poz�ci�ban
        positions.add(new Position(kajax, kajay));
		pieces[snakeLength].setBounds(kajax, kajay, unit, unit);
        board.add(pieces[snakeLength]);

		// Megn�veli a k�gy� hossz�t jelz� v�ltoz�t
		snakeLength++;
	}

	/*
	 * A f�jlmegnyit� f�ggv�ny megnyitja a "toplist.ser" nev� f�jlt, mely a
	 * toplista szerepl�it tartalmazza �s ezeket a lista nev� ArrayListben
	 * elt�rolja (deszerializ�l�s)
	 */
	@SuppressWarnings("unchecked")
	void fajlmegnyitas() {
		// A f�jl megnyit�sa
		try {
			InputStream file = new FileInputStream("toplista.ser");
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput in;
			in = new ObjectInputStream(buffer);

			// A f�jl tartalm�nak bem�sol�sa a lista ArrayListbe
			toplist = (ArrayList<ToplistEntry>) in.readObject();

			// A f�jl bez�r�sa
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
	 * A f�jlba�r� f�ggv�ny a "toplista.ser" nev� f�jlba be�rja a legfrissebb
	 * toplista szerepl�it (szerializ�l�s)
	 */
	void fajlbairas() {
		// A f�jl megnyit�sa
		try {
			OutputStream file = new FileOutputStream("toplista.ser");

			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput out;
			out = new ObjectOutputStream(buffer);

			// A lista ArrayList f�jlba �r�sa
			out.writeObject(toplist);

			// A f�jl bez�r�sa
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Ez a f�ggv�ny a j�t�k v�g�t vizsg�lja. Megn�zi, hogy a k�gy� hal�la ut�n
	 * felker�l-e a toplist�ra a j�t�kos az el�rt eredm�ny�vel. Ha igen akkor
	 * bek�ri a nev�t, �s friss�ti a toplist�t. Ha nem akkor egy j�t�k v�ge
	 * k�perny�t rajzol ki. A v�g�n pedig szerializ�l.
	 */
	void toplistabatesz() {
		// A p�lya t�rl�se a k�perny�r�l.
		remove(board);

		// Ha az el�rt eredm�ny jobb az eddigi legkisebb eredm�nyn�l
		if (actualPoints > toplist.get(9).getPoints()) {
			// Egy ArrayList l�trehoz�sa, mely a megadott nevet t�rolja
			final ArrayList<String> holder = new ArrayList<String>();

			// A ki�r�sok �s a sz�vegmez� l�trehoz�sa
			JLabel nyert1 = new JLabel("A j�t�knak v�ge!");
			JLabel nyert2 = new JLabel("Gratul�lok! Felker�lt�l a toplist�ra. K�rlek add meg a neved (max 10 bet�):");
			final JTextField newnev = new JTextField(10);

			// Ezek hozz�ad�sa a top panelhez
			top.removeAll();
			top.add(nyert1);
			top.add(nyert2);
			top.add(newnev);

			// A sz�vegmez� tartalm�nak hozz�sad�sa a holderhez
			newnev.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					synchronized (holder) {
						holder.add(newnev.getText());
						holder.notify();
					}
					dispose();
				}
			});

			// A top panel hozz�ad�sa az ablakhoz, �s az ablak �jrarajzol�sa
			add(top, BorderLayout.CENTER);
			setVisible(true);
			repaint();

			// V�rakoz�s a sz�vegez� kit�lt�s�ig
			synchronized (holder) {
				while (holder.isEmpty())
					try {
						holder.wait();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
			}

			// A lista utols� elem�nek kicser�l�se az �j listaelemmel �s a lista
			// sorbarendez�se
			toplist.remove(9);
			toplist.add(new ToplistEntry(holder.remove(0), actualPoints));
			Collections.sort(toplist);

			// A toplista friss�t�se, �s kirajzol�sa az ablakra
			toplistafrissites();
			top.removeAll();
			top.add(scrollPane);
			repaint();
			// Ha az eredm�ny nincs bent a legjobb 10-be
		} else {
			// A kiir�sok l�trehoz�sa �s hozz�ad�sa az ablakhoz
			JLabel nemnyert1 = new JLabel("A j�t�knak v�ge!");
			JLabel nemnyert2 = new JLabel("Sajnos nem ker�lt be az eredm�nyed a legjobb 10-be. Pr�b�lkozz �jra (F2).");
			nemnyert1.setForeground(Color.BLACK);
			nemnyert2.setForeground(Color.BLACK);
			top.removeAll();
			top.add(nemnyert1);
			top.add(nemnyert2);

			// A toplista friss�t�se �s a top panel hozz�ad�sa az ablakhoz
			toplistafrissites();
			add(top, BorderLayout.CENTER);
			setVisible(true);
			repaint();
		}
		// Szerializ�l�s
		fajlbairas();
	}

	/*
	 * Ez a f�ggv�ny a toplist�t egy t�bl�zatba rakja
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void toplistafrissites() {
		// A t�bl�zat fejl�c�nek l�trehoz�sa
		Vector colnames = new Vector();
		colnames.add("N�v");
		colnames.add("Pont");

		// A t�bl�zat l�trehoz�sa egy ScrollPane-ben
		DefaultTableModel tablazatmodell = new DefaultTableModel(colnames, 0);
		JTable tablazat = new JTable(tablazatmodell);
		scrollPane.setViewportView(tablazat);

		// A t�bl�zat felt�lt�se a lista elemeivel
		for (ToplistEntry i : toplist) {
			String[] row = { i.getUserName(), String.valueOf(i.getPoints()) };
			tablazatmodell.addRow(row);
		}

	}

	/*
	 * A mozgat� f�ggv�ny megv�ltoztatja a k�gy� poz�ci�j�t a megadott ir�nyba,
	 * �s k�zben vizsg�lja, hogy a k�gy� nem �tk�z�tt-e falnak vagy mag�nak,
	 * illetve azt, hogy evett-e
	 */
	void mozgat() {
		// Lek�ri a k�gy� �sszes elem�nek poz�ci�j�t a p�ly�n
		for (int i = 0; i < snakeLength; i++) {
			points[i] = pieces[i].getLocation();
		}

		// Megv�ltoztatja az els� elemnek a poz�ci�j�t a megadott ir�nyba
		Position firstPos = positions.remove(0);
		int newX = firstPos.getX() + xCoordChange;
	    int newY = firstPos.getY() + yCoordChange;
        positions.add(0, new Position(newX , newY));
		pieces[0].setBounds(newX, newY, unit, unit);

		// Megv�ltoztatja a t�bbi elem helyzet�t az el�tt l�v� elem�re
		for (int i = 1; i < snakeLength; i++) {
			pieces[i].setLocation(points[i - 1]);
		}

		// Ellen�rzi, hogy a k�gy� nem-e ment �nmag�ba
		for (int i = 1; i < snakeLength - 1; i++) {
			if (points[0].equals(points[i])) {
				crashedItself = true;
			}
		}

		// Ellen�rzi, hogy a k�gy� nem-e ment �nmag�ba vagy falnak. Ha igen
		// akkor a j�t�knak v�ge proced�ra zajlik le, illetve le�ll a mozgat�s
		Position head = positions.get(0);
		int x = head.getX();
		int y = head.getY();
		if ((x + 10 == boardWidth) || (x == 0) || (y == 0) || (y + 10 == boardHeight) || (crashedItself == true)) {
			run = false;
			gameover = true;
			toplistabatesz();
		}

		// Ellen�rzi, hogy a k�gy� nem �rte-e el az �telt. Ha igen akkor n�veli
		// a pontsz�mot
		Position tail = positions.get(positions.size()-2);
		if (x == tail.getX() && y == tail.getY()) {
			hasEaten = true;
			actualPoints = actualPoints + 5;
			pointsLabel.setText("Pontsz�m: " + actualPoints);
		}

		// Ha evett, akkor l�trehozza az �j �telt �s n�veli a k�gy�t, k�l�nben
		// az �tel ott marad ahol volt
		if (hasEaten == true) {
			novekszik();
			hasEaten = false;
		} else {
			pieces[snakeLength - 1].setBounds(tail.getX(), tail.getY(), unit, unit);
		}

		// A p�lya friss�t�se
		//board.repaint();
		setVisible(true);
	}

	/*
	 * A billenty� lenyom�s�t �rz�kel� f�ggv�ny, mely megfelel� gomb lenyom�s�ra
	 * a megfelel� m�veletet hajtja v�gre
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
	 * A run met�dus hivja meg megadott id�k�z�nk�nt a mozgat� f�ggv�nyt
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
