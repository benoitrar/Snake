package snake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import snake.controller.MenuController;
import snake.controller.SnakeModel;
import snake.controller.VelocityActions;
import snake.model.Points;
import snake.model.Position;
import snake.view.Menu;
import snake.view.SnakeView;

public class Snake extends JFrame implements KeyListener, Runnable, VelocityActions {
    
	private static final long serialVersionUID = 1L;
	
	private final int width = 506;
	private final int height = 380;
	private final int unit = 10;
	private final int boardWidth = 50 * unit;
	private final int boardHeight = 30 * unit;
    
	private final SnakeModel model = new SnakeModel();
	private final List<Position> positions = new ArrayList<>();
    private final Random random = new Random();

    private final Menu menu = new Menu();
    private final List<JButton> pieces = new ArrayList<>();
    private final SnakeView board;
    private final JPanel pointsPanel;
    private final JPanel top;
    private final JPanel[] frame = new JPanel[4];
    private final JLabel pointsLabel;
    private final JScrollPane scrollPane = new JScrollPane();
	
	private Points points = new Points();

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
	private Snake.Delay delay = Snake.Delay.NORMAL_DELAY;
	
	public static enum Delay {
	    
        LITTLE_DELAY(50), NORMAL_DELAY(70), BIG_DELAY(90);
        
        private final long delay;
        
        private Delay(long delay) {
            this.delay = delay;
        }
        
        public long getDelay() {
            return delay;
        }
    }

	/*
	 * Az értékek alaphelyzetbe állítása és a toplistát tartalmazó fájl
	 * megnyitása
	 */
	public void init() {
	    int x = 24 * unit;
	    int y = 14 * unit;
	    positions.add(new Position(x, y));
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
		points.init();
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

        new MenuController(menu, board, model, points).bind();

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

		// Az elsõ snake létrehozása és kirajzolása
		elsoSnake();

		// A pontszám kíírása a képernyõre
		pointsLabel = new JLabel("Pontszám: " + points.getActualPoints());
		pointsLabel.setForeground(Color.BLACK);
		pointsPanel.add(pointsLabel);

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
		scrollPane.removeAll();

		// Ha az elõzõ játékban meghalt a kígyó, akkor a játék vége kijelzõ
		// törlése az ablakból
		if (gameover == true) {
			remove(top);
		}

		// A keret hozzáadása a pályához
		board.add(frame[0]);
		board.add(frame[1]);
		board.add(frame[2]);
		board.add(frame[3]);

		// Az elsõ kígyó létrehozása, kirajzolása
		elsoSnake();

		// A pálya hozzáadása az ablakhoz, annak újrarajzolása és a pontszám
		// kiírása
		add(board, BorderLayout.CENTER);
		repaint();
		setVisible(true);
		pointsLabel.setText("Pontszám: " + points.getActualPoints());

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
			JButton newPiece = createNewSnakePiece();
			Position lastPos = positions.get(i);
			newPiece.setBounds(lastPos.getX(), lastPos.getY(), unit, unit);

			// A kocka megjelenítése a pályán
			board.add(newPiece);

			// A következõ elem koordinátáinak a megváltoztatása
			positions.add(new Position(lastPos.getX() - unit, lastPos.getY()));
		}
	}

	/*
	 * Ez a függvény létrehozza az új ételt a pályán random helyen, és
	 * kirajzolja azt
	 */
	void novekszik() {
		// Létrehozza az új ételt, és hozzáadja a pályához
		JButton newPiece = createNewSnakePiece();

		// Randomgenerátorral létrehozza az étel x,y koordinátáit
		int kajax = 20 + (unit * random.nextInt(46));
		int kajay = 20 + (unit * random.nextInt(26));

		// Beállítja a koordinátáit a kajának, és kirajzolja azt a megadott
		// pozícióban
        positions.add(new Position(kajax, kajay));
		newPiece.setBounds(kajax, kajay, unit, unit);
        board.add(newPiece);

		// Megnöveli a kígyó hosszát jelzõ változót
		snakeLength++;
	}

    private JButton createNewSnakePiece() {
        JButton newPiece = new JButton();
		newPiece.setEnabled(false);
		newPiece.setBackground(Color.BLACK);
		pieces.add(newPiece);
		return newPiece;
    }

	

	void refreshToplist() {
		scrollPane.setViewportView(points.getToplistAsTable());
	}

	/*
	 * A mozgató függvény megváltoztatja a kígyó pozícióját a megadott irányba,
	 * és közben vizsgálja, hogy a kígyó nem ütközött-e falnak vagy magának,
	 * illetve azt, hogy evett-e
	 */
	void mozgat() {
		// Lekéri a kígyó összes elemének pozícióját a pályán
		/*for (int i = 0; i < snakeLength; i++) {
			points[i] = pieces.get(i).getLocation();
		}*/

		// Megváltoztatja az elsõ elemnek a pozícióját a megadott irányba
		Position firstPos = positions.get(0);
		int newX = firstPos.getX() + xCoordChange;
	    int newY = firstPos.getY() + yCoordChange;
        positions.add(0, new Position(newX , newY));
        JButton oldTail = pieces.remove(pieces.size()-2);
		oldTail.setBounds(newX, newY, unit, unit);
		pieces.add(0, oldTail);

		// Megváltoztatja a többi elem helyzetét az elõtt lévõ elemére
		/*for (int i = 1; i < snakeLength; i++) {
			pieces.get(i).setLocation(points[i - 1]);
		}*/

		// Ellenõrzi, hogy a kígyó nem-e ment önmagába
		// TODO refactor!
		for (int i = 1; i < snakeLength - 1; i++) {
			if (pieces.get(0).getLocation().equals(pieces.get(i).getLocation())) {
				crashedItself = true;
			}
		}

		// Ellenõrzi, hogy a kígyó nem-e ment önmagába vagy falnak. Ha igen
		// akkor a játéknak vége procedúra zajlik le, illetve leáll a mozgatás
		Position head = positions.get(0);
		int x = head.getX();
		int y = head.getY();
		if ((x + 10 == boardWidth) || (x == 0) || (y == 0) || (y + 10 == boardHeight) || (crashedItself == true)) {
			run = false;
			gameover = true;
			handleGameEnd();
		}

		// Ellenõrzi, hogy a kígyó nem érte-e el az ételt. Ha igen akkor növeli
		// a pontszámot
		Position tail = positions.get(positions.size()-2);
		if (x == tail.getX() && y == tail.getY()) {
			hasEaten = true;
			points.getPointsForNewFood();
			pointsLabel.setText("Pontszám: " + points.getActualPoints());
		}

		// Ha evett, akkor létrehozza az új ételt és növeli a kígyót, különben
		// az étel ott marad ahol volt
		if (hasEaten == true) {
			novekszik();
			hasEaten = false;
		} else {
			pieces.get(snakeLength - 1).setBounds(tail.getX(), tail.getY(), unit, unit);
		}

		// A pálya frissítése
		//board.repaint();
		setVisible(true);
	}

	private void handleGameEnd() {
	    remove(board);
	    repaint();
	    if(!points.isHighScore()) {
	        JLabel gameEnd = new JLabel("A játéknak vége!");
            JLabel noWin = new JLabel("Sajnos nem került be az eredményed a legjobb 10-be. Próbálkozz újra (F2).");
            gameEnd.setForeground(Color.BLACK);
            noWin.setForeground(Color.BLACK);
            top.removeAll();
            top.add(gameEnd);
            top.add(noWin);
            add(top, BorderLayout.CENTER);
	    } else {
	        
	    }
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
				Thread.sleep(delay.getDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
    public void bindDifficult() {
        menu.addActionListenerToDifficult(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delay = Snake.Delay.LITTLE_DELAY;
            }
        });
    }
    
    @Override
    public void bindNormal() {
        menu.addActionListenerToNormal(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delay = Snake.Delay.NORMAL_DELAY;
            }
        });
    }
    
    @Override
    public void bindEasy() {
        menu.addActionListenerToEasy(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delay = Snake.Delay.BIG_DELAY;
            }
        });
    }
}
