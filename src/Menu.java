import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu extends JMenuBar {

    private final JMenu jatek = new JMenu("Játék");
    private final JMenuItem newGame = new JMenuItem("Új Játék (F2)");
    private final JMenuItem toplist = new JMenuItem("Toplista");
    private final JMenuItem kilepes = new JMenuItem("Kilépés (ALT+F4)");
    
    private final JMenu beallitasok = new JMenu("Beállítások");
    private final JMenuItem nehez = new JMenuItem("Nehéz");
    private final JMenuItem normal = new JMenuItem("Normál");
    private final JMenuItem konnyu = new JMenuItem("Könnyû");
    
    private final JMenu segitseg = new JMenu("Segítség");
    private final JMenuItem keszito = new JMenuItem("Készítõ");
    private final JMenuItem iranyitas = new JMenuItem("Irányítás");

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
        kilepes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Ezek hozzáadása a Játék menüponthoz
        jatek.add(newGame);
        jatek.addSeparator();
        jatek.add(toplist);
        jatek.addSeparator();
        jatek.add(kilepes);
        this.add(jatek);

        // A sebesség változtatásának hozzárendelése
        nehez.addActionListener(new ActionListener() {
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
        konnyu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                //sebesseg = 90;
            }
        });

        // Ezek hozzáadása a Beállítások menüponthoz
        beallitasok.add(nehez);
        beallitasok.addSeparator();
        beallitasok.add(normal);
        beallitasok.addSeparator();
        beallitasok.add(konnyu);
        this.add(beallitasok);

        // A segítségek funkcióinak megvalósítása
        keszito.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                //JOptionPane.showMessageDialog(jatekter, "Készítõ: Kérlek Refaktorálj\n" + "Programnév: Snake\n" + "Verziószám: v0.7");
            }
        });
        iranyitas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                //JOptionPane.showMessageDialog(jatekter, "Irányítás a kurzor segítségével:\n" + "-Fel nyíl: a kígyó felfele mozog\n"
                  //      + "-Le nyíl: a kígyó lefele mozog\n" + "-Jobbra nyíl: a kígyó jobbra mozog\n" + "-Balra nyíl: a kígyó balra mozog\n");
            }
        });

        // Ezek hozzáadása a Segítség menüponthoz
        segitseg.add(keszito);
        segitseg.addSeparator();
        segitseg.add(iranyitas);
        this.add(segitseg);
    }

    public JMenuItem getNewGame() {
        return newGame;
    }
    
}
