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
import javax.swing.JTextField;

import snake.controller.MenuController;
import snake.controller.SnakeModel;
import snake.controller.VelocityActions;
import snake.model.Points;
import snake.model.Position;
import snake.view.Menu;
import snake.view.SnakeView;

public class Snake extends JFrame implements KeyListener, Runnable, VelocityActions {
    
	private static final long serialVersionUID = 1L;
	
	private static final JLabel gameEnd = new JLabel("A játéknak vége!");
	private static final JLabel win = new JLabel("Gratulálok! Felkerültél a toplistára. Kérlek add meg a neved (max 10 betû):");
	private static final JLabel noWin = new JLabel("Sajnos nem került be az eredményed a legjobb 10-be. Próbálkozz újra (F2).");
	
	private final int width = 506;
	private final int height = 380;
	private final int unit = 10;
	private final int boardWidth = 50 * unit;
	private final int boardHeight = 30 * unit;
	private final Position firstPosition = new Position(boardWidth/2 - unit, boardHeight/2 - unit);
    
	private final SnakeModel model = new SnakeModel();
	private final Points points = new Points();
	private final List<Position> positions = new ArrayList<>();
    private final Random random = new Random();

    private final Menu menu = new Menu();
    private final List<JButton> pieces = new ArrayList<>();
    private final SnakeView board = new SnakeView();
    private final JPanel pointsPanel = new JPanel();
    private final JPanel top = new JPanel();
    private final JPanel[] frame = new JPanel[4];
    private final JLabel pointsLabel = new JLabel("Pontszám: 0");
    private final JScrollPane scrollPane = new JScrollPane();
    private final JTextField winnersName = new WinnersName(10);
	
    private int snakeLength;
	private int xCoordChange;
	private int yCoordChange;
	
	private boolean run;
	private boolean canGoToLeft;
	private boolean canGoToRight;
	private boolean canGoUpwards;
	private boolean canGoDownwards;
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

	public void init() {
	    positions.add(firstPosition);
		snakeLength = 3;
		xCoordChange = +unit;
		yCoordChange = 0;
		run = false;
		canGoToLeft = false;
		canGoToRight = true;
		canGoUpwards = true;
		canGoDownwards = true;
		gameover = false;
		points.init();
	}

	public void start() {
		run = true;
		(new Thread(this)).start();
	}

	public Snake() {
	    
		super("Snake v0.7");
		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		init();

        new MenuController(menu, board, model, points).bind();

		board.setLayout(null);
		board.setBounds(0, 0, boardWidth, boardHeight);
		board.setBackground(Color.LIGHT_GRAY);
		pointsPanel.setBounds(0, boardHeight, boardWidth, 30);
		pointsPanel.setBackground(Color.GRAY);
		top.setBounds(0, 0, boardWidth, boardHeight);
		top.setBackground(Color.LIGHT_GRAY);

		createFrame();
		addFrameToBoard();

		createSnakeAndFood();

		pointsLabel.setForeground(Color.BLACK);
		pointsPanel.add(pointsLabel);

        setJMenuBar(menu);
        add(board, BorderLayout.CENTER);
        add(pointsPanel, BorderLayout.SOUTH);
        setLayout(null);
        
		setResizable(false);
		setLocationRelativeTo(null);
		addKeyListener(this);
		setVisible(true);

		start();
	}

    private void createSnakeAndFood() {
        firstSnake();
		createNewFood();
    }

    private void createFrame() {
        frame[0] = new JPanel();
		frame[0].setBounds(0, 0, boardWidth, unit);
		frame[1] = new JPanel();
		frame[1].setBounds(0, 0, unit, boardHeight);
		frame[2] = new JPanel();
		frame[2].setBounds(0, boardHeight - unit, boardWidth, unit);
		frame[3] = new JPanel();
		frame[3].setBounds(boardWidth - unit, 0, unit, boardHeight);
    }

    private void addFrameToBoard() {
        board.add(frame[0]);
		board.add(frame[1]);
		board.add(frame[2]);
		board.add(frame[3]);
    }

	void reset() {
		init();

		board.removeAll();
		scrollPane.removeAll();

		if (gameover == true) {
			remove(top);
		}

		addFrameToBoard();

		createSnakeAndFood();

		add(board, BorderLayout.CENTER);
		repaint();
		setVisible(true);
		pointsLabel.setText("Pontszám: " + points.getActualPoints());

		start();
	}

	void firstSnake() {
		for (int i = 0; i < snakeLength; i++) {
			JButton newPiece = createNewSnakePiece();
			Position lastPos = positions.get(i);
			newPiece.setBounds(lastPos.getX(), lastPos.getY(), unit, unit);

			board.add(newPiece);

			positions.add(new Position(lastPos.getX() - unit, lastPos.getY()));
		}
	}

	void createNewFood() {
		JButton newPiece = createNewSnakePiece();

		int kajax = 20 + (unit * random.nextInt(46));
		int kajay = 20 + (unit * random.nextInt(26));

		positions.add(new Position(kajax, kajay));
		newPiece.setBounds(kajax, kajay, unit, unit);
        board.add(newPiece);

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

	private void move() {
		removeTailAndAddNewHead();

		Position head = positions.get(0);
		int x = head.getX();
		int y = head.getY();
		if ((x + 10 == boardWidth) || (x == 0) || (y == 0) || (y + 10 == boardHeight) || hasCrashed()) {
			run = false;
			gameover = true;
			handleGameEnd();
		}

		Position tail = positions.get(positions.size()-2);
		if (hasEaten(x, y, tail)) {
		    createNewFood();
			points.getPointsForNewFood();
			pointsLabel.setText("Pontszám: " + points.getActualPoints());
		} else {
		    pieces.get(snakeLength - 1).setBounds(tail.getX(), tail.getY(), unit, unit);		    
		}

		setVisible(true);
	}

    private boolean hasEaten(int x, int y, Position tail) {
        return x == tail.getX() && y == tail.getY();
    }

    private boolean hasCrashed() {
        for (int i = 1; i < snakeLength - 1; i++) {
			if (pieces.get(0).getLocation().equals(pieces.get(i).getLocation())) {
				return true;
			}
		}
        return false;
    }

    private void removeTailAndAddNewHead() {
        Position firstPos = positions.get(0);
		int newX = firstPos.getX() + xCoordChange;
	    int newY = firstPos.getY() + yCoordChange;
        positions.add(0, new Position(newX , newY));
        JButton oldTail = pieces.remove(pieces.size()-2);
		oldTail.setBounds(newX, newY, unit, unit);
		pieces.add(0, oldTail);
    }

	private void handleGameEnd() {
	    remove(board);
	    top.removeAll();
	    top.add(gameEnd);
	    repaint();
	    if(!points.isHighScore()) {
            top.add(noWin);
	    } else {
            top.add(win);
            top.add(winnersName);
	    }
	    add(top, BorderLayout.CENTER);
    }
    
    private class WinnersName extends JTextField {
        public WinnersName(int length) {
            super(length);
            
            addKeyListener(new KeyListener() {
                public void keyTyped(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {}
                
                @Override
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyChar() == KeyEvent.VK_ENTER) {
                        points.addHighScore(WinnersName.this.getText());
                        refreshToplist();
                        top.removeAll();
                        top.add(scrollPane);
                        Snake.this.revalidate();
                        points.writeToplistToFile();
                    }
                }
            });
        }
    }

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

	public void run() {
		while (run) {
			move();
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
