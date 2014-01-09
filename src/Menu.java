import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu extends JMenuBar {

    private final JMenu game = new JMenu("Játék");
    private final JMenuItem newGame = new JMenuItem("Új Játék (F2)");
    private final JMenuItem toplist = new JMenuItem("Toplista");
    private final JMenuItem exit = new JMenuItem("Kilépés (ALT+F4)");
    
    private final JMenu settings = new JMenu("Beállítások");
    private final JMenuItem difficult = new JMenuItem("Nehéz");
    private final JMenuItem normal = new JMenuItem("Normál");
    private final JMenuItem easy = new JMenuItem("Könnyû");
    
    private final JMenu help = new JMenu("Segítség");
    private final JMenuItem creator = new JMenuItem("Készítõ");
    private final JMenuItem navigation = new JMenuItem("Irányítás");

    public Menu() {
        // Az Új Játék, a Toplista és a Kilépés funkciók hozzárendelése
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                //reset();
            }
        });
        toplist.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                //JOptionPane.showMessageDialog(jatekter, scrollpane);
            }
        });
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Ezek hozzáadása a Játék menüponthoz
        game.add(newGame);
        game.addSeparator();
        game.add(toplist);
        game.addSeparator();
        game.add(exit);
        this.add(game);

        // A sebesség változtatásának hozzárendelése
        difficult.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                //sebesseg = 50;
            }
        });
        normal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                //sebesseg = 70;
            }
        });
        easy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                //sebesseg = 90;
            }
        });

        // Ezek hozzáadása a Beállítások menüponthoz
        settings.add(difficult);
        settings.addSeparator();
        settings.add(normal);
        settings.addSeparator();
        settings.add(easy);
        this.add(settings);

        // A segítségek funkcióinak megvalósítása
        creator.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                //JOptionPane.showMessageDialog(jatekter, "Készítõ: Kérlek Refaktorálj\n" + "Programnév: Snake\n" + "Verziószám: v0.7");
            }
        });
        navigation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                //JOptionPane.showMessageDialog(jatekter, "Irányítás a kurzor segítségével:\n" + "-Fel nyíl: a kígyó felfele mozog\n"
                  //      + "-Le nyíl: a kígyó lefele mozog\n" + "-Jobbra nyíl: a kígyó jobbra mozog\n" + "-Balra nyíl: a kígyó balra mozog\n");
            }
        });

        // Ezek hozzáadása a Segítség menüponthoz
        help.add(creator);
        help.addSeparator();
        help.add(navigation);
        this.add(help);
    }

    public JMenuItem getNewGame() {
        return newGame;
    }
    
}
