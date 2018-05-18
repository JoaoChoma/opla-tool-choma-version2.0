package jmetal.core;

public class SolutionCopy extends Solution{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Solution modifySolution;
	
	public Solution getModifySolution() {
		return modifySolution;
	}

	public void setModifySolution(Solution modifySolution) {
		this.modifySolution = modifySolution;
	}
	
	public SolutionCopy(Solution input){
		this.setModifySolution(new Solution(input));
	}
	
	
	


}
