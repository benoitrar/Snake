package snake.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import snake.view.Menu;
import snake.view.SnakeView;

public class MenuController implements MenuActions {
    
    private final Menu menu;
    private final SnakeView view;
    private final SnakeModel model;

    public MenuController(Menu menu, SnakeView view, SnakeModel model) {
        this.menu = menu;
        this.view = view;
        this.model = model;
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
                // TODO
                JOptionPane.showMessageDialog(view, "TOPLIST - TODO");
            }
        });
    }
    
    @Override
    public void bindExit() {
        menu.addActionListenerToExit(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO System.exit() is forbidden
                System.exit(0);
            }
        });
    }
    
    @Override
    public void bindCreator() {
        menu.addActionListenerToCreator(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(view, "K�sz�t�: K�rlek Refaktor�lj\n" + "Programn�v: Snake\n" + "Verzi�sz�m: v0.7");
            }
        });
    }

    @Override
    public void bindNavigation() {
        menu.addActionListenerToNavigation(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(view, "Ir�ny�t�s a kurzor seg�ts�g�vel:\n" + "-Fel ny�l: a k�gy� felfele mozog\n"
                        + "-Le ny�l: a k�gy� lefele mozog\n" + "-Jobbra ny�l: a k�gy� jobbra mozog\n" + "-Balra ny�l: a k�gy� balra mozog\n");
            }
        });
    }
}
