package snake.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
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
    
    public void addHighScore() {
        if (isHighScore()) {
            // Egy ArrayList létrehozása, mely a megadott nevet tárolja
            final ArrayList<String> holder = new ArrayList<String>();

            // A kiírások és a szövegmezõ létrehozása
            JLabel nyert1 = new JLabel("A játéknak vége!");
            JLabel nyert2 = new JLabel("Gratulálok! Felkerültél a toplistára. Kérlek add meg a neved (max 10 betû):");
            final JTextField newnev = new JTextField(10);

            // Ezek hozzáadása a top panelhez
//            top.removeAll();
//            top.add(nyert1);
//            top.add(nyert2);
//            top.add(newnev);

            // A szövegmezõ tartalmának hozzásadása a holderhez
            newnev.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    synchronized (holder) {
                        holder.add(newnev.getText());
                        holder.notify();
                    }
//                    dispose();
                }
            });

            // A top panel hozzáadása az ablakhoz, és az ablak újrarajzolása
//            add(top, BorderLayout.CENTER);
//            setVisible(true);
//            repaint();

            // Várakozás a szövegezõ kitöltéséig
            synchronized (holder) {
                while (holder.isEmpty())
                    try {
                        holder.wait();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
            }

            // A lista utolsó elemének kicserélése az új listaelemmel és a lista
            // sorbarendezése
            Points.toplist.remove(9);
            Points.toplist.add(new ToplistEntry(holder.remove(0), actualPoints));
            Collections.sort(Points.toplist);

            // A toplista frissítése, és kirajzolása az ablakra
//            refreshToplist();
//            top.removeAll();
//            top.add(scrollPane);
//            repaint();
//            points.writeToplistToFile();
            // Ha az eredmény nincs bent a legjobb 10-be
        }
        // Szerializálás
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

    public void init() {
        actualPoints = 0;
        loadToplistFromFile();
    }

    public int getActualPoints() {
        return actualPoints;
    }

    public void getPointsForNewFood() {
        actualPoints += 5;
    }
}