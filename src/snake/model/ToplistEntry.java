package snake.model;

import java.io.Serializable;

public class ToplistEntry implements Serializable, Comparable<ToplistEntry> {
    
    private static final long serialVersionUID = 1L;
    
    private String userName;
    private int points;

    public ToplistEntry(String userName, int points) {
        this.userName = userName;
        this.points = points;
    }

    public String getUserName() {
        return userName;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public int compareTo(ToplistEntry t) {
        if (points > t.getPoints()) {
            return -1;
        } else if (points < t.getPoints()) {
            return +1;
        } else {
            return 0;
        }
    }
}