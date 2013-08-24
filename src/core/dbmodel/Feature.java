package core.dbmodel;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("feature")
@IdName("patent_id")
public class Feature extends Model{

}
