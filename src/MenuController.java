import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuController {
    
    private final Menu menu;
    private final SnakeView view;

    public MenuController(Menu menu, SnakeView view) {
        this.menu = menu;
        this.view = view;
        
        bind();
    }

    private void bind() {
        bindNewGame();
    }

    private void bindNewGame() {
        menu.getNewGame().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                view.reset();
            }
        });
    }
}
