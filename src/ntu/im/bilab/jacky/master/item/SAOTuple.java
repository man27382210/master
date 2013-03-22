package ntu.im.bilab.jacky.master.item;

public class SAOTuple {
	String clause;
	String subject;
	String predicate;
	String object;
	
	public SAOTuple(String clause, String subject, String predicate, String object) {
		this.clause = clause;
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	@Override
	public String toString() {
		return "SAOTuple [S=" + subject + ", A=" + predicate + ", O=" + object
		    + "]";
	}

	public String getClause() {
		return clause;
	}

	public void setClause(String clause) {
		this.clause = clause;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}
}