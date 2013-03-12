package ntu.im.bilab.jacky.master;

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
	  return "SAOTuple [clause=" + clause + ", subject=" + subject
	      + ", predicate=" + predicate + ", object=" + object + "]";
  }
}