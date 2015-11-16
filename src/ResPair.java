
public class ResPair {
	private Clause clause1;
	private Clause clause2;
	
	public ResPair(Clause c1, Clause c2) {
		this.clause1 = c1;
		this.clause2 = c2;
	}

	public Clause getClause1() {
		return clause1;
	}

	public void setClause1(Clause clause1) {
		this.clause1 = clause1;
	}

	public Clause getClause2() {
		return clause2;
	}

	public void setClause2(Clause clause2) {
		this.clause2 = clause2;
	}

}
