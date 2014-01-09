import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu extends JMenuBar {

    private final JMenu jatek = new JMenu("J�t�k");
    private final JMenuItem newGame = new JMenuItem("�j J�t�k (F2)");
    private final JMenuItem toplist = new JMenuItem("Toplista");
    private final JMenuItem kilepes = new JMenuItem("Kil�p�s (ALT+F4)");
    
    private final JMenu beallitasok = new JMenu("Be�ll�t�sok");
    private final JMenuItem nehez = new JMenuItem("Neh�z");
    private final JMenuItem normal = new JMenuItem("Norm�l");
    private final JMenuItem konnyu = new JMenuItem("K�nny�");
    
    private final JMenu segitseg = new JMenu("Seg�ts�g");
    private final JMenuItem keszito = new JMenuItem("K�sz�t�");
    private final JMenuItem iranyitas = new JMenuItem("Ir�ny�t�s");

    public Menu() {
        // Az �j J�t�k, a Toplista �s a Kil�p�s funkci�k hozz�rendel�se
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

        // Ezek hozz�ad�sa a J�t�k men�ponthoz
        jatek.add(newGame);
        jatek.addSeparator();
        jatek.add(toplist);
        jatek.addSeparator();
        jatek.add(kilepes);
        this.add(jatek);

        // A sebess�g v�ltoztat�s�nak hozz�rendel�se
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

        // Ezek hozz�ad�sa a Be�ll�t�sok men�ponthoz
        beallitasok.add(nehez);
        beallitasok.addSeparator();
        beallitasok.add(normal);
        beallitasok.addSeparator();
        beallitasok.add(konnyu);
        this.add(beallitasok);

        // A seg�ts�gek funkci�inak megval�s�t�sa
        keszito.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                //JOptionPane.showMessageDialog(jatekter, "K�sz�t�: K�rlek Refaktor�lj\n" + "Programn�v: Snake\n" + "Verzi�sz�m: v0.7");
            }
        });
        iranyitas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
                //JOptionPane.showMessageDialog(jatekter, "Ir�ny�t�s a kurzor seg�ts�g�vel:\n" + "-Fel ny�l: a k�gy� felfele mozog\n"
                  //      + "-Le ny�l: a k�gy� lefele mozog\n" + "-Jobbra ny�l: a k�gy� jobbra mozog\n" + "-Balra ny�l: a k�gy� balra mozog\n");
            }
        });

        // Ezek hozz�ad�sa a Seg�ts�g men�ponthoz
        segitseg.add(keszito);
        segitseg.addSeparator();
        segitseg.add(iranyitas);
        this.add(segitseg);
    }

    public JMenuItem getNewGame() {
        return newGame;
    }
    
}
