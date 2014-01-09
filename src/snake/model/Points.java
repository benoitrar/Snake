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
    
    private final static String[] colNames = {"N�v", "Pont"};

    private static ArrayList<ToplistEntry> toplist = new ArrayList<ToplistEntry>();

    private int actualPoints;
    

    public Points() {
    }
    
    public boolean isHighScore() {
        return actualPoints > toplist.get(toplist.size()-1).getPoints();
    }
    
    public void addHighScore() {
        if (isHighScore()) {
            // Egy ArrayList l�trehoz�sa, mely a megadott nevet t�rolja
            final ArrayList<String> holder = new ArrayList<String>();

            // A ki�r�sok �s a sz�vegmez� l�trehoz�sa
            JLabel nyert1 = new JLabel("A j�t�knak v�ge!");
            JLabel nyert2 = new JLabel("Gratul�lok! Felker�lt�l a toplist�ra. K�rlek add meg a neved (max 10 bet�):");
            final JTextField newnev = new JTextField(10);

            // Ezek hozz�ad�sa a top panelhez
//            top.removeAll();
//            top.add(nyert1);
//            top.add(nyert2);
//            top.add(newnev);

            // A sz�vegmez� tartalm�nak hozz�sad�sa a holderhez
            newnev.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    synchronized (holder) {
                        holder.add(newnev.getText());
                        holder.notify();
                    }
//                    dispose();
                }
            });

            // A top panel hozz�ad�sa az ablakhoz, �s az ablak �jrarajzol�sa
//            add(top, BorderLayout.CENTER);
//            setVisible(true);
//            repaint();

            // V�rakoz�s a sz�vegez� kit�lt�s�ig
            synchronized (holder) {
                while (holder.isEmpty())
                    try {
                        holder.wait();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
            }

            // A lista utols� elem�nek kicser�l�se az �j listaelemmel �s a lista
            // sorbarendez�se
            Points.toplist.remove(9);
            Points.toplist.add(new ToplistEntry(holder.remove(0), actualPoints));
            Collections.sort(Points.toplist);

            // A toplista friss�t�se, �s kirajzol�sa az ablakra
//            refreshToplist();
//            top.removeAll();
//            top.add(scrollPane);
//            repaint();
//            points.writeToplistToFile();
            // Ha az eredm�ny nincs bent a legjobb 10-be
        }
        // Szerializ�l�s
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
    	// A f�jl megnyit�sa
    	try {
    		InputStream file = new FileInputStream("toplista.ser");
    		InputStream buffer = new BufferedInputStream(file);
    		ObjectInput in;
    		in = new ObjectInputStream(buffer);
    
    		// A f�jl tartalm�nak bem�sol�sa a lista ArrayListbe
    		Points.toplist = (ArrayList<ToplistEntry>) in.readObject();
    
    		// A f�jl bez�r�sa
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
    	// A f�jl megnyit�sa
    	try {
    		OutputStream file = new FileOutputStream("toplista.ser");
    
    		OutputStream buffer = new BufferedOutputStream(file);
    		ObjectOutput out;
    		out = new ObjectOutputStream(buffer);
    
    		// A lista ArrayList f�jlba �r�sa
    		out.writeObject(Points.toplist);
    
    		// A f�jl bez�r�sa
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