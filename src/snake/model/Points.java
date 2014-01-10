package snake.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Points {
    
    private static final int STARTING_POINTS = 0;
    private static final String TOPLIST_FILENAME = "toplista.ser";
    private static final String[] colNames = {"Név", "Pont"};

    private static ArrayList<ToplistEntry> toplist = new ArrayList<ToplistEntry>();

    private int actualPoints;
    
    public boolean isHighScore() {
        return actualPoints > toplist.get(toplist.size()-1).getPoints();
    }
    
    public void addHighScore(String userName) {
        if (isHighScore()) {
            toplist.remove(toplist.size()-1);
            toplist.add(new ToplistEntry(userName, actualPoints));
            Collections.sort(Points.toplist);
        }
    }

    public JTable getToplistAsTable() {
        DefaultTableModel tableModel = new DefaultTableModel(colNames, 0);

        for (ToplistEntry entry : Points.toplist) {
            String[] row = { entry.getUserName(), String.valueOf(entry.getPoints()) };
            tableModel.addRow(row);
        }
        return new JTable(tableModel);
    }

    @SuppressWarnings("unchecked")
    public void loadToplistFromFile() {
    	try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(TOPLIST_FILENAME)))) {
    		Points.toplist = (ArrayList<ToplistEntry>) in.readObject();
    	} catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Highscores are not available.");
            loadEmptyHighscores();
        }
    }

    private void loadEmptyHighscores() {
        for(int i=0;i<10;i++) {
            toplist.add(new ToplistEntry("", 0));
        }
    }

    public void writeToplistToFile() {
    	try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(TOPLIST_FILENAME)))) {
    		out.writeObject(Points.toplist);
    	} catch (IOException e) {
    	    JOptionPane.showMessageDialog(null, "Highscores could not be saved.");
    	}
    }

    public void init() {
        actualPoints = STARTING_POINTS;
        loadToplistFromFile();
    }

    public int getActualPoints() {
        return actualPoints;
    }

    public void getPointsForNewFood() {
        actualPoints += 5;
    }
}