package jmetal.metaheuristics.memetic;

import java.io.FileWriter;
import java.io.IOException;

import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;

public class Local_Search_Until implements Runnable {

	public void gravarRegistro(String tipo) throws IOException {
		FileWriter f = new FileWriter("selecao.txt", true);
		f.write("\n untilBest: " + tipo);
		f.close();
	}

	public boolean solutionBest(SolutionSet result) {
		double vConventional = 0;
		double vPatterns = 0;
		try {
			for (int k = 0; k < result.get(0).numberOfObjectives(); k++) {
				vConventional += result.get(0).getObjective(k);
				vPatterns += result.get(1).getObjective(k);
			}
			if (vConventional < vPatterns) {
				this.gravarRegistro("Conventional");
				return false;
			} else if (vPatterns < vConventional) {
				this.gravarRegistro("Patterns");
				return true;
			} else {
				this.gravarRegistro("Equals");
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	Solution offSpringForLocal_local = null;
	Solution offSpring_local = null;
	Problem problem_local_ = null;
	Operator operadorLocal_local = null;
	Solution[] solutionsLocal = null;

	public Local_Search_Until(Solution offSpring, Solution offSpringForLocal, Problem problem_,
			Operator operadorLocal) {
		this.offSpringForLocal_local = offSpringForLocal;
		this.problem_local_ = problem_;
		this.operadorLocal_local = operadorLocal;
		this.offSpring_local = offSpring;
	}

	public synchronized Solution[] getSolutionsLocal() {
		return solutionsLocal;
	}

	public synchronized void setSolutionsLocal(Solution[] solutionsLocalOk) {
		this.solutionsLocal = solutionsLocalOk;
	}

	public void setLocal_Search(Solution[] offSpringForLocal, Problem problem_, Operator operadorLocal) {

	}

	@Override
	public synchronized void run() {
		try {
			int contador0 = 0;
			boolean resultComparation0 = false;

			while (resultComparation0 == false) {
				SolutionSet solutions0 = new SolutionSet(2);

				this.operadorLocal_local.execute(offSpringForLocal_local);
				this.problem_local_.evaluateConstraints(offSpringForLocal_local);
				this.problem_local_.evaluate(offSpringForLocal_local);

				solutions0.add(offSpring_local);
				solutions0.add(offSpringForLocal_local);
				resultComparation0 = this.solutionBest(solutions0);
				solutions0 = null;
				// quantidade de tentativas
				contador0++;
				if (contador0 > 14) {
					offSpringForLocal_local = offSpring_local;
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("erro da thread \n");
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

}
