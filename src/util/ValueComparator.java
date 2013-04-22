package util;

import item.Patent;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<Patent> {
	Map<Patent, Double> base;

	public ValueComparator(Map<Patent, Double> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(Patent a, Patent b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}
