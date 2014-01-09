import java.io.Serializable;

class Toplist implements Serializable, Comparable<Toplist> {
	
    private static final long serialVersionUID = 1L;
    
	private String userName;
	private int points;

	public Toplist(String userName, int points) {
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
    public int compareTo(Toplist t) {
        if (points > t.getPoints()) {
            return +1;
        } else if (points < t.getPoints()) {
            return -1;
        } else {
            return 0;
        }
    }
}