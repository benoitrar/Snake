import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

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
        
        bindDifficult();
        bindNormal();
        bindEasy();
        
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
    public void bindDifficult() {
        menu.addActionListenerToDifficult(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.setVelocityToQuick();
            }
        });
    }
    
    @Override
    public void bindNormal() {
        menu.addActionListenerToNormal(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.setVelocityToNormal();
            }
        });
    }
    
    @Override
    public void bindEasy() {
        menu.addActionListenerToEasy(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.setVelocityToSlow();
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
