import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

public class ResolutionTheoremProver {
	
	private static ArrayList<Clause> knowledgeBase;
	private static int numberOfClauses = 0;
	private static int maxQueueSize = 0;

	public static void main(String[] args) {
		knowledgeBase = new ArrayList<Clause>();
		System.out.println("Enter the name of the knowledge base file:");
		Scanner in = new Scanner(System.in);		
		String fileName = in.nextLine();
		in.close();
		extractKnowledgeBase(fileName);
		ResolutionPairComparator rpc = new ResolutionPairComparator();
		PriorityQueue<ResPair> candidates = new PriorityQueue<ResPair>(100, rpc);
		for (int i = 0; i < knowledgeBase.size(); i++) {
			for (int j = i + 1; j < knowledgeBase.size(); j++) {
				Clause clauseA = knowledgeBase.get(i);
				Clause clauseB = knowledgeBase.get(j);
				if(areResolvable(clauseA, clauseB)) {					
					ResPair respair = new ResPair(clauseA, clauseB);
					candidates.add(respair);
					if(candidates.size() > maxQueueSize) {
						maxQueueSize = candidates.size();
					}
				}
			}
		}
		int iteration = 1;
		while(!candidates.isEmpty()) {
			ResPair poppedResPair = candidates.poll();
			System.out.println("Iteration " + iteration + ", queue size " + (candidates.size() + 1) + ", resolution on " + poppedResPair.getClause1().getId() + " and " + poppedResPair.getClause2().getId());
			ArrayList<String> propositions = getResolvablePropositions(poppedResPair);
			for (String p : propositions) {
				System.out.println("Resolving " + poppedResPair.getClause1().getLiterals() + " and " + poppedResPair.getClause2().getLiterals());
				Clause resolventClause = resolve(poppedResPair.getClause1(), poppedResPair.getClause2(), p);
				resolventClause.setGeneratedFromClause1(poppedResPair.getClause1().getId());
				resolventClause.setGeneratedFromClause2(poppedResPair.getClause2().getId());
				System.out.println(resolventClause.getId() + ": " + resolventClause.getLiterals() + " generated from " + poppedResPair.getClause1().getId() + " and " + poppedResPair.getClause2().getId());
				if(resolventClause.getLiterals().size() == 0) {
					System.out.println("Success!");
					printProofTree(resolventClause, 0);
					System.out.println("Number of iterations = " + iteration);	
					System.out.println("Maximum Queue Size = " + maxQueueSize);
					return;
				} else {
					if(!isPresentInKnowledgeBase(resolventClause)) {
						for (Clause clause : knowledgeBase) {
							if(areResolvable(clause, resolventClause)) {
								ResPair newResPair = new ResPair(clause, resolventClause);
								candidates.add(newResPair);
								if(candidates.size() > maxQueueSize) {
									maxQueueSize = candidates.size();
								}
							}
						}
						knowledgeBase.add(resolventClause);						
					}
				}
			}
			iteration++;
		}
		System.out.println("No solution found");
	}

	private static void printProofTree(Clause resolventClause, int depth) {
		for(int i = 0; i < depth; i++) {
			System.out.print(" ");
		}
		if(resolventClause.getGeneratedFromClause1() == -1) {
			System.out.println(resolventClause.getId() + ": " + resolventClause.getLiterals() + " (input)");
		} else {
			System.out.println(resolventClause.getId() + ": " + resolventClause.getLiterals() + " (" + resolventClause.getGeneratedFromClause1() + "," + resolventClause.getGeneratedFromClause2() + ")");
			printProofTree(getClauseByID(resolventClause.getGeneratedFromClause1()), depth + 1);
			printProofTree(getClauseByID(resolventClause.getGeneratedFromClause2()), depth + 1);
		}				
	}

	private static Clause getClauseByID(int id) {		
		for (Clause clause : knowledgeBase) {
			if(clause.getId() == id) {
				return clause;	
			}				
		}
		return null;
	}

	private static boolean isPresentInKnowledgeBase(Clause resolventClause) {
		for (Clause clause : knowledgeBase) {
			if(clause.getLiterals().size() == resolventClause.getLiterals().size()) {
				if(clause.getLiterals().equals(resolventClause.getLiterals())) {
					return true;
				}
			}
		}
		return false;
	}

	private static Clause resolve(Clause clause1, Clause clause2, String proposition) {
		ArrayList<String> newClauseList = new ArrayList<String>();
		newClauseList.addAll(clause1.getLiterals());
		newClauseList.addAll(clause2.getLiterals());
		newClauseList.remove(proposition);
		newClauseList.remove("-" + proposition);
		newClauseList = removeDuplicates(newClauseList);
		Collections.sort(newClauseList);
		Clause clause = new Clause(newClauseList, numberOfClauses);
		numberOfClauses++;
		return clause;
	}

	private static ArrayList<String> removeDuplicates(ArrayList<String> newClauseList) {
		ArrayList<String> al = newClauseList;
		Set<String> hs = new HashSet<>();
		hs.addAll(al);
		al.clear();
		al.addAll(hs);
		return al;
	}

	private static ArrayList<String> getResolvablePropositions(ResPair resPair) {
		ArrayList<String> propositions = new ArrayList<String>();
		for (String literal : resPair.getClause1().getLiterals()) {
			String oppositeLiteral = literal.startsWith("-") ? literal.substring(1) : "-" + literal;							
			if(resPair.getClause2().getLiterals().contains(oppositeLiteral)) {
				if(literal.startsWith("-")) {
					propositions.add(oppositeLiteral);
				}else {
					propositions.add(literal);
				}
			}
		}
		return propositions;
	}

	private static boolean areResolvable(Clause clauseA, Clause clauseB) {
		for (String literal : clauseA.getLiterals()) {
			String oppositeLiteral = literal.startsWith("-") ? literal.substring(1) : "-" + literal;							
			if(clauseB.getLiterals().contains(oppositeLiteral)) {
				return true;
			}
		}
		return false;
	}

	private static void extractKnowledgeBase(String fileName) {
		File knowledgeBaseFile = new File(fileName);
		try {
			Scanner in = new Scanner(knowledgeBaseFile);
			while(in.hasNext()) {
				String clauseText = in.nextLine();
				if(clauseText.startsWith("#") || clauseText.isEmpty()) {					
					continue;
				}
				Clause clause = new Clause(clauseText, numberOfClauses);
				knowledgeBase.add(clause);
				numberOfClauses++;
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Place the input file in the folder which contains src and bin if running from eclipse");
			System.out.println("Place the input file in the bin folder if running from command line");
			System.exit(-1);
		}
		
	}
}

class ResolutionPairComparator implements Comparator<ResPair>
{
	@Override
	public int compare(ResPair r1, ResPair r2) {
		if (r1.getClause1().getLiterals().size() + r1.getClause2().getLiterals().size() < 
				r2.getClause1().getLiterals().size() + r2.getClause2().getLiterals().size()) {
			return -1;
		}
		if (r1.getClause1().getLiterals().size() + r1.getClause2().getLiterals().size() >
				r2.getClause1().getLiterals().size() + r2.getClause2().getLiterals().size()) {
			return 1;
		}
		return 0;
	}
}
