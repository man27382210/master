package item;

import org.javalite.activejdbc.Model;

public class SaoTuple extends Model {

	@Override
	public String toString() {
		return "s=" + getString("subject") + ",a=" + getString("predicate") + ",o=" + getString("object");
	}

}