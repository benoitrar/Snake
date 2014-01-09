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
	 * Az �rt�kek alaphelyzetbe �ll�t�sa �s a toplist�t tartalmaz� f�jl
	 * megnyit�sa
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

        new MenuController(menu, board, model, points).bind();

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
		pointsLabel = new JLabel("Pontsz�m: " + points.getActualPoints());
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
		pointsLabel.setText("Pontsz�m: " + points.getActualPoints());

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
			JButton newPiece = createNewSnakePiece();
			Position lastPos = positions.get(i);
			newPiece.setBounds(lastPos.getX(), lastPos.getY(), unit, unit);

			// A kocka megjelen�t�se a p�ly�n
			board.add(newPiece);

			// A k�vetkez� elem koordin�t�inak a megv�ltoztat�sa
			positions.add(new Position(lastPos.getX() - unit, lastPos.getY()));
		}
	}

	/*
	 * Ez a f�ggv�ny l�trehozza az �j �telt a p�ly�n random helyen, �s
	 * kirajzolja azt
	 */
	void novekszik() {
		// L�trehozza az �j �telt, �s hozz�adja a p�ly�hoz
		JButton newPiece = createNewSnakePiece();

		// Randomgener�torral l�trehozza az �tel x,y koordin�t�it
		int kajax = 20 + (unit * random.nextInt(46));
		int kajay = 20 + (unit * random.nextInt(26));

		// Be�ll�tja a koordin�t�it a kaj�nak, �s kirajzolja azt a megadott
		// poz�ci�ban
        positions.add(new Position(kajax, kajay));
		newPiece.setBounds(kajax, kajay, unit, unit);
        board.add(newPiece);

		// Megn�veli a k�gy� hossz�t jelz� v�ltoz�t
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
	 * A mozgat� f�ggv�ny megv�ltoztatja a k�gy� poz�ci�j�t a megadott ir�nyba,
	 * �s k�zben vizsg�lja, hogy a k�gy� nem �tk�z�tt-e falnak vagy mag�nak,
	 * illetve azt, hogy evett-e
	 */
	void mozgat() {
		// Lek�ri a k�gy� �sszes elem�nek poz�ci�j�t a p�ly�n
		/*for (int i = 0; i < snakeLength; i++) {
			points[i] = pieces.get(i).getLocation();
		}*/

		// Megv�ltoztatja az els� elemnek a poz�ci�j�t a megadott ir�nyba
		Position firstPos = positions.get(0);
		int newX = firstPos.getX() + xCoordChange;
	    int newY = firstPos.getY() + yCoordChange;
        positions.add(0, new Position(newX , newY));
        JButton oldTail = pieces.remove(pieces.size()-2);
		oldTail.setBounds(newX, newY, unit, unit);
		pieces.add(0, oldTail);

		// Megv�ltoztatja a t�bbi elem helyzet�t az el�tt l�v� elem�re
		/*for (int i = 1; i < snakeLength; i++) {
			pieces.get(i).setLocation(points[i - 1]);
		}*/

		// Ellen�rzi, hogy a k�gy� nem-e ment �nmag�ba
		// TODO refactor!
		for (int i = 1; i < snakeLength - 1; i++) {
			if (pieces.get(0).getLocation().equals(pieces.get(i).getLocation())) {
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
			handleGameEnd();
		}

		// Ellen�rzi, hogy a k�gy� nem �rte-e el az �telt. Ha igen akkor n�veli
		// a pontsz�mot
		Position tail = positions.get(positions.size()-2);
		if (x == tail.getX() && y == tail.getY()) {
			hasEaten = true;
			points.getPointsForNewFood();
			pointsLabel.setText("Pontsz�m: " + points.getActualPoints());
		}

		// Ha evett, akkor l�trehozza az �j �telt �s n�veli a k�gy�t, k�l�nben
		// az �tel ott marad ahol volt
		if (hasEaten == true) {
			novekszik();
			hasEaten = false;
		} else {
			pieces.get(snakeLength - 1).setBounds(tail.getX(), tail.getY(), unit, unit);
		}

		// A p�lya friss�t�se
		//board.repaint();
		setVisible(true);
	}

	private void handleGameEnd() {
	    remove(board);
	    repaint();
	    if(!points.isHighScore()) {
	        JLabel gameEnd = new JLabel("A j�t�knak v�ge!");
            JLabel noWin = new JLabel("Sajnos nem ker�lt be az eredm�nyed a legjobb 10-be. Pr�b�lkozz �jra (F2).");
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
