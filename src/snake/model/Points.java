package snake.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Points {
    
    private final static String[] colNames = {"Név", "Pont"};

    private static ArrayList<ToplistEntry> toplist = new ArrayList<ToplistEntry>();

    private int actualPoints;
    

    public Points() {
    }
    
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
    	// A fájl megnyitása
    	try {
    		InputStream file = new FileInputStream("toplista.ser");
    		InputStream buffer = new BufferedInputStream(file);
    		ObjectInput in;
    		in = new ObjectInputStream(buffer);
    
    		// A fájl tartalmának bemásolása a lista ArrayListbe
    		Points.toplist = (ArrayList<ToplistEntry>) in.readObject();
    
    		// A fájl bezárása
    		in.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    public void writeToplistToFile() {
    	// A fájl megnyitása
    	try {
    		OutputStream file = new FileOutputStream("toplista.ser");
    
    		OutputStream buffer = new BufferedOutputStream(file);
    		ObjectOutput out;
    		out = new ObjectOutputStream(buffer);
    
    		// A lista ArrayList fájlba írása
    		out.writeObject(Points.toplist);
    
    		// A fájl bezárása
    		out.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    public void init() {
        actualPoints = -2;
        loadToplistFromFile();
    }

    public int getActualPoints() {
        return actualPoints;
    }

    public void getPointsForNewFood() {
        actualPoints += 5;
    }
    
    private void loadToplistWithEmptyRecords() {
        for(int i=0;i<10;i++) {
            toplist.add(new ToplistEntry("", -1));
        }
    }
}