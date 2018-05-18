package jmetal.metaheuristics.memetic;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;

public class Thread_local extends Thread {
	
	Solution[] offSpringForLocal_local = null;
	Problem problem_local_ = null;
	Operator operadorLocal_local = null;
	
	Solution[] solutionsLocal = null;
	
	public Thread_local(Solution[] offSpringForLocal, Problem problem_, Operator operadorLocal){
		this.offSpringForLocal_local = offSpringForLocal;
		this.problem_local_ = problem_;
		this.operadorLocal_local = operadorLocal;
	}
	
	public synchronized Solution[] getSolutionsLocal (){
		return solutionsLocal;
	}
	
	public synchronized void setSolutionsLocal(Solution[] solutionsLocalOk){
		this.solutionsLocal = solutionsLocalOk;
	}

	@Override
	public synchronized void start(){
		this.run();
	}
	
	@Override
	public synchronized void run() {
		//System.out.println("entrou");
		try {
			this.operadorLocal_local.execute(this.offSpringForLocal_local[0]);
			this.operadorLocal_local.execute(this.offSpringForLocal_local[1]);
			this.problem_local_.evaluateConstraints(this.offSpringForLocal_local[0]);
			this.problem_local_.evaluateConstraints(this.offSpringForLocal_local[1]);
			this.problem_local_.evaluate(this.offSpringForLocal_local[0]);
			this.problem_local_.evaluate(this.offSpringForLocal_local[1]);
			
			this.setSolutionsLocal(this.offSpringForLocal_local);
		} catch (Exception e) {
			this.setSolutionsLocal(this.offSpringForLocal_local);
			
			//System.out.println("erro da thread \n"+e);
		}
		

	}

}
