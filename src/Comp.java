import java.util.Comparator;

public class Comp implements Comparator<Toplist> {
	/*
	 * A Comparator belsõ függvénye, melynek segítségével sorba rendezi a
	 * Toplistát
	 */
	public int compare(Toplist pont1, Toplist pont2) {
		int p1 = pont1.getPoints();
		int p2 = pont2.getPoints();

		if (p1 > p2) {
			return -1;
		} else if (p1 < p2) {
			return +1;
		} else {
			return 0;
		}
	}

}
