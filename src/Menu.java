import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu extends JMenuBar {

    private final JMenu game = new JMenu("J�t�k");
    private final JMenuItem newGame = new JMenuItem("�j J�t�k (F2)");
    private final JMenuItem toplist = new JMenuItem("Toplista");
    private final JMenuItem exit = new JMenuItem("Kil�p�s (ALT+F4)");
    
    private final JMenu settings = new JMenu("Be�ll�t�sok");
    private final JMenuItem difficult = new JMenuItem("Neh�z");
    private final JMenuItem normal = new JMenuItem("Norm�l");
    private final JMenuItem easy = new JMenuItem("K�nny�");
    
    private final JMenu help = new JMenu("Seg�ts�g");
    private final JMenuItem creator = new JMenuItem("K�sz�t�");
    private final JMenuItem navigation = new JMenuItem("Ir�ny�t�s");

    public Menu() {
        composeGameMenuPoint();
        composeSettingsMenuPoint();
        composeHelpMenuPoint();
        
        addMenuPointsToMainMenu();
    }

    private void composeGameMenuPoint() {
        game.add(newGame);
        game.addSeparator();
        game.add(toplist);
        game.addSeparator();
        game.add(exit);
    }

    private void composeSettingsMenuPoint() {
        settings.add(difficult);
        settings.addSeparator();
        settings.add(normal);
        settings.addSeparator();
        settings.add(easy);
    }

    private void composeHelpMenuPoint() {
        help.add(creator);
        help.addSeparator();
        help.add(navigation);
    }

    private void addMenuPointsToMainMenu() {
        this.add(game);
        this.add(settings);
        this.add(help);
    }

    public void addActionListenerToNewGame(ActionListener listener) {
        newGame.addActionListener(listener);
    }
    
}
