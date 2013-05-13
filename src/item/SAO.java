package item;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("sao_tuples")
public class SAO extends Model {

  @Override
  public String toString() {
    return "(S) " + getString("subject") + " (A) "  + getString("predicate") + " (O) " + getString("object");
  }
  
  
}