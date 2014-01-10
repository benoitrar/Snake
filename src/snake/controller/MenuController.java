package snake.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import snake.model.Points;
import snake.view.Menu;
import snake.view.SnakeView;

public class MenuController implements MenuActions {
    
    private final Menu menu;
    private final SnakeView view;
    private final SnakeModel model;
    private final Points points;

    public MenuController(Menu menu, SnakeView view, SnakeModel model, Points points) {
        this.menu = menu;
        this.view = view;
        this.model = model;
        this.points = points;
    }

    public void bind() {
        bindNewGame();
        bindTopList();
        bindExit();
        
        bindCreator();
        bindNavigation();
    }

    @Override
    public void bindNewGame() {
        menu.addActionListenerToNewGame(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                view.reset();
            }
        });
    }

    @Override
    public void bindTopList() {
        menu.addActionListenerToToplist(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(view, points.getToplistAsTable());
            }
        });
    }
    
    @Override
    public void bindExit() {
        menu.addActionListenerToExit(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
    
    @Override
    public void bindCreator() {
        menu.addActionListenerToCreator(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(view, "Készítõ: Kérlek Refaktorálj\n" + "Programnév: Snake\n" + "Verziószám: v0.7");
            }
        });
    }

    @Override
    public void bindNavigation() {
        menu.addActionListenerToNavigation(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(view, "Irányítás a kurzor segítségével:\n" + "-Fel nyíl: a kígyó felfele mozog\n"
                        + "-Le nyíl: a kígyó lefele mozog\n" + "-Jobbra nyíl: a kígyó jobbra mozog\n" + "-Balra nyíl: a kígyó balra mozog\n");
            }
        });
    }
}
