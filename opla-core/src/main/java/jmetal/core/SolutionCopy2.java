package jmetal.core;

public class SolutionCopy2 extends Solution {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		double CD;
		Variable[] DV;
		double DT;
		double F;
		double KD;
		int L;
		int NV;
		double OC;
		int R;
		String SN;
		SolutionType T;
		double[] objective;
		double[] objectiveTemp;
		int variables;
		int objectives;
		double AV;
		int NB;
		//Problem pro = new Problem();
		
	public Solution setCopy(Solution entrada) throws ClassNotFoundException {
		if (entrada != null) {
			CD = entrada.getCrowdingDistance();//
			DV = entrada.getDecisionVariables();//
			DT = entrada.getDistanceToSolutionSet();//
			F = entrada.getFitness();//
			KD = entrada.getKDistance();//
			L = entrada.getLocation();//
			NV = entrada.getNumberOfViolatedConstraint();//
			OC = entrada.getOverallConstraintViolation();//
			R = entrada.getRank();//
			SN = entrada.getSolutionName();//
			T = entrada.getType();//

			for (int j = 0; j < entrada.numberOfObjectives(); j++) {
				objective[j] = entrada.getObjective(j);//
				objectiveTemp[j] = entrada.getObjectiveTemp(j);//
			}
			variables = entrada.numberOfVariables();//
			objectives = entrada.numberOfObjectives();//
			//AV = entrada.getAggregativeValue();//
			//NB = entrada.getNumberOfBits();
			
		}
		
		Solution newSolution = new Solution();
		
		newSolution.setCrowdingDistance(CD);
		newSolution.setDecisionVariables(DV);
		newSolution.setDistanceToSolutionSet(DT);
		newSolution.setFitness(F);
		newSolution.setKDistance(KD);
		newSolution.setLocation(L);
		newSolution.setNumberOfViolatedConstraint(NV);
		newSolution.setOverallConstraintViolation(OC);
		newSolution.setRank(R);
		newSolution.setSolutionName(SN);
		newSolution.setType(T);
		for (int j = 0; j < entrada.numberOfObjectives(); j++) {
			newSolution.setObjective(j, objective[j]);
			newSolution.setObjectiveTemp(j, objectiveTemp[j]);
		}
		Solution.getNewSolution(entrada.problem_);
		return newSolution;
	}

	public Solution getCopy(Solution entrada) throws ClassNotFoundException{
		Solution saida = new Solution();
		saida = this.setCopy(entrada);
		return saida;
		
	}
	
	public Solution copiarSolution(Solution entrada) throws ClassNotFoundException{
		Solution s = new Solution();
		
		Solution.getNewSolution(entrada.problem_);
		
		return entrada;
	}
	
}

/*
 * public Solution atribuir(Solution entrada) { Solution result = new
 * Solution(); result = null; try { if (entrada != null) {
 * result.setCrowdingDistance(entrada.getCrowdingDistance());
 * result.setDecisionVariables(entrada.getDecisionVariables());
 * result.setDistanceToSolutionSet(entrada.getDistanceToSolutionSet());
 * result.setFitness(entrada.getFitness());
 * result.setKDistance(entrada.getKDistance());
 * result.setLocation(entrada.getLocation());
 * result.setNumberOfViolatedConstraint(entrada.getNumberOfViolatedConstraint())
 * ;
 * result.setOverallConstraintViolation(entrada.getOverallConstraintViolation())
 * ; result.setRank(entrada.getRank());
 * result.setSolutionName(entrada.getSolutionName());
 * result.setType(entrada.getType()); for (int j = 0; j <
 * entrada.numberOfObjectives(); j++) { result.setObjective(j,
 * entrada.getObjective(j)); result.setObjectiveTemp(j,
 * entrada.getObjectiveTemp(j)); } } else { JOptionPane.showMessageDialog(null,
 * "Filho passado nulo"); }
 * 
 * return result; } catch (Exception e) { System.err.println(e); } return
 * result; }
 */