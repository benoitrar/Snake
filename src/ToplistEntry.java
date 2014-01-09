import java.io.Serializable;

class ToplistEntry implements Serializable, Comparable<ToplistEntry> {
    
	private static final long serialVersionUID = 1L;
	
	private final String userName;
	private final int points;
	
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
    public int compareTo(ToplistEntry o) {
        if (points > o.getPoints()) {
            return 1;
        } else if (points < o.getPoints()) {
            return -1;
        } else {
            return 0;
        }
    }
}