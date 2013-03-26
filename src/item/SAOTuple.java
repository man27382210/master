package item;

import java.io.Serializable;

public class SAOTuple  {
	String subject;
	String predicate;
	String object;
	
	public SAOTuple(String subject, String predicate, String object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	@Override
	public String toString() {
		return "SAOTuple [S=" + subject + ", A=" + predicate + ", O=" + object
		    + "]";
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