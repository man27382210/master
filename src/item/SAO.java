package item;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("sao_tuples")
public class SAO extends Model {

	@Override
	public String toString() {
		return "s=" + getString("subject") + ",a=" + getString("predicate") + ",o=" + getString("object");
	}

}