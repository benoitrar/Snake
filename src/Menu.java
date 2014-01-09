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
