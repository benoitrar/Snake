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

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Points {
    
    public int actualPoints;
    public static ArrayList<ToplistEntry> toplist = new ArrayList<ToplistEntry>();
    
    private final static String[] colNames = {"Név", "Pont"};

    public Points() {
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
    public
    void loadToplistFromFile() {
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
}