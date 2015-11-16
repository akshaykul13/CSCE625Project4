import java.util.ArrayList;

public class Clause {
	private int id;
	private ArrayList<String> literals;
	private int generatedFromClause1 = -1;
	private int generatedFromClause2 = -1;
	
	public Clause(String clauseText, int id) {
		this.id = id;
		String[] literalsArray = clauseText.split(" ");
		literals = new ArrayList<String>();
		for (String literal : literalsArray) {
			literals.add(literal.trim());
		}
	}

	public Clause(ArrayList<String> newClauseList, int id) {
		this.id = id;
		this.literals = newClauseList;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<String> getLiterals() {
		return literals;
	}

	public void setLiterals(ArrayList<String> literals) {
		this.literals = literals;
	}

	public int getGeneratedFromClause1() {
		return generatedFromClause1;
	}

	public void setGeneratedFromClause1(int generatedFromClause1) {
		this.generatedFromClause1 = generatedFromClause1;
	}

	public int getGeneratedFromClause2() {
		return generatedFromClause2;
	}

	public void setGeneratedFromClause2(int generatedFromClause2) {
		this.generatedFromClause2 = generatedFromClause2;
	}

}
