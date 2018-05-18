//  NSGAII.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.metaheuristics.memetic;

import jmetal.operators.localSearch.LocalSearch;
import jmetal.operators.mutation.DesignPatternAndPLAMutation;
import jmetal.operators.mutation.Mutation;
import jmetal.problems.CapturarValoresFitness;
import jmetal.problems.OPLA;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionCopy;
import jmetal.core.SolutionSet;
import jmetal.experiments.Bestof12_OPLA_FeatMut;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparators.CrowdingComparator;

/**
 * This class implements the NSGA-II algorithm.
 */
public class Bestof12 extends Algorithm {

	private static final long serialVersionUID = 5815971727148859507L;

	/**
	 * Constructor
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public Bestof12(Problem problem) {
		super(problem);

	} // NSGAII

	public void criarArquivo() throws IOException {
//		File f = new File("selecao-bestof12.txt");
//		f.createNewFile();
		File t = new File("tempo-bestof12.txt");
		t.createNewFile();
		File a = new File("aplicacao-bestof12.txt");
		a.createNewFile();
	}

	public void gravarTempo(long valor) throws IOException {
		FileWriter t = new FileWriter("tempo-bestof12.txt", true);
		t.write("\n tempo-bestof12: " + valor);
		t.close();
	}

	public void gravarRegistro(String tipo) throws IOException {
		FileWriter f = new FileWriter("selecao-bestof12.txt", true);
		f.write("\n bestof12: " + tipo);
		f.close();
	}

	public void gravarAplicacao(String input) throws IOException {
		FileWriter t = new FileWriter("aplicacao-bestof12.txt", true);
		t.write("\n " + input);
		t.close();
	}

	public SolutionSet solutionBest(SolutionSet result) {
		double vConventional = 0;
		double vPatterns = 0;
		SolutionSet best = result;
		try {
			for (int k = 0; k < result.get(0).numberOfObjectives(); k++) {
				vConventional += result.get(0).getObjective(k);
				vPatterns += result.get(1).getObjective(k);
			}

			if (vConventional < vPatterns) {
				this.gravarRegistro(" Conventional ");
				result.remove(1);
				best = result;

			} else if (vPatterns < vConventional) {
				this.gravarRegistro(" Patterns ");
				result.remove(0);
				best = result;
			} else {
				this.gravarRegistro(" Equals ");
				result.remove(0);
				best = result;
			}
			return best;
		} catch (Exception e) {
			e.printStackTrace();
			return best;
		}

	}

	/**
	 * Runs the NSGA-II algorithm.
	 * 
	 * @return a <code>SolutionSet</code> that is a set of non dominated
	 *         solutions as a result of the algorithm execution
	 * @throws JMException
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public synchronized SolutionSet execute() throws JMException, ClassNotFoundException {
		int populationSize;
		int maxEvaluations;
		int evaluations;

		QualityIndicator indicators; // QualityIndicator object
		int requiredEvaluations; // Use in the example of use of the
		// indicators object (see below)

		SolutionSet population;
		SolutionSet offspringPopulation;
		SolutionSet union;

		Operator mutationOperator;
		// jcn
		Operator operatorLocal;

		Operator crossoverOperator;
		Operator selectionOperator;

		Distance distance = new Distance();

		// Read the parameters
		populationSize = ((Integer) getInputParameter("populationSize")).intValue();
		maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
		indicators = (QualityIndicator) getInputParameter("indicators");

		// Initialize the variables
		population = new SolutionSet(populationSize);
		evaluations = 0;

		requiredEvaluations = 0;

		// Read the operators
		mutationOperator = operators_.get("mutation");
		crossoverOperator = operators_.get("crossover");
		selectionOperator = operators_.get("selection");
		// joao
		operatorLocal = operatorsLocal_.get("operatorLocal");

		// POPULACAO INICIAL
		try {
			// Create the initial solutionSet
			Solution newSolution;

			// extrair fitness original
			// newSolution = new Solution(problem_);
			// System.out.println("parar");
			// problem_.evaluate(newSolution);
			// JOptionPane.showMessageDialog(null, "fitness original");

			for (int i = 0; i < populationSize; i++) {
				// criar a diversidade na populacao inicial
				newSolution = new Solution(problem_);
				mutationOperator.execute(newSolution);
				// jcn
				operatorLocal.execute(newSolution);
				problem_.evaluate(newSolution);
				if (problem_.isPatterns(newSolution)) {
					this.gravarAplicacao("solução inicial COM patterns");
				} else {
					this.gravarAplicacao("solução inicial SEM patterns");
				}

				evaluations++;
				population.add(newSolution);
			}
			this.gravarAplicacao("FIM DA POPULACAO INICIAL");
		} catch (Exception e) {
			System.err.println(e);
		}

		try {
			this.criarArquivo();
			while (evaluations < maxEvaluations) {
				System.out.println("\n ||||||| \n");
				System.out.println("Evolucao:" + evaluations);
				System.out.println("\n ||||||| \n");

				offspringPopulation = new SolutionSet(populationSize);
				Solution[] parents = new Solution[2];

				// crossover
				for (int i = 0; i < (populationSize / 2); i++) {
					if (evaluations < maxEvaluations) {
						ExecutorService executor = Executors.newFixedThreadPool(12);
						System.out.println("\n --- \n");
						System.out.println("Bestof12 INDIVIDUO: " + i + " evolucao: " + evaluations + " \n");
						System.out.println("\n ---\n ");
						// tempo
						long timeBegin = System.currentTimeMillis();
						// tempo
						parents[0] = (Solution) selectionOperator.execute(population);
						parents[1] = (Solution) selectionOperator.execute(population);

						Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
						//////////
						Solution[] offSpringForLocal0 = new Solution[2];
						Solution[] offSpringForLocal1 = new Solution[2];
						Solution[] offSpringForLocal2 = new Solution[2];
						Solution[] offSpringForLocal3 = new Solution[2];
						Solution[] offSpringForLocal4 = new Solution[2];
						Solution[] offSpringForLocal5 = new Solution[2];
						Solution[] offSpringForLocal6 = new Solution[2];
						Solution[] offSpringForLocal7 = new Solution[2];
						Solution[] offSpringForLocal8 = new Solution[2];
						Solution[] offSpringForLocal9 = new Solution[2];
						Solution[] offSpringForLocal10 = new Solution[2];
						Solution[] offSpringForLocal11 = new Solution[2];

						double[] arraySolutionBest0 = new double[12];
						double[] arraySolutionBest1 = new double[12];

						SolutionSet solutionsLocal0 = new SolutionSet(12);
						SolutionSet solutionsLocal1 = new SolutionSet(12);
						//////////

						problem_.evaluateConstraints(offSpring[0]);
						problem_.evaluateConstraints(offSpring[1]);

						// operador convencional begin

						mutationOperator.execute(offSpring[0]);
						mutationOperator.execute(offSpring[1]);

						problem_.evaluateConstraints(offSpring[0]);
						problem_.evaluateConstraints(offSpring[1]);

						// operador convencional end
						// ------------------------------------------------------

						// atribuicoes ----
						SolutionCopy a0 = new SolutionCopy(offSpring[0]);
						SolutionCopy b0 = new SolutionCopy(offSpring[1]);
						SolutionCopy a1 = new SolutionCopy(offSpring[0]);
						SolutionCopy b1 = new SolutionCopy(offSpring[1]);
						SolutionCopy a2 = new SolutionCopy(offSpring[0]);
						SolutionCopy b2 = new SolutionCopy(offSpring[1]);
						SolutionCopy a3 = new SolutionCopy(offSpring[0]);
						SolutionCopy b3 = new SolutionCopy(offSpring[1]);
						SolutionCopy a4 = new SolutionCopy(offSpring[0]);
						SolutionCopy b4 = new SolutionCopy(offSpring[1]);
						SolutionCopy a5 = new SolutionCopy(offSpring[0]);
						SolutionCopy b5 = new SolutionCopy(offSpring[1]);
						SolutionCopy a6 = new SolutionCopy(offSpring[0]);
						SolutionCopy b6 = new SolutionCopy(offSpring[1]);
						SolutionCopy a7 = new SolutionCopy(offSpring[0]);
						SolutionCopy b7 = new SolutionCopy(offSpring[1]);
						SolutionCopy a8 = new SolutionCopy(offSpring[0]);
						SolutionCopy b8 = new SolutionCopy(offSpring[1]);
						SolutionCopy a9 = new SolutionCopy(offSpring[0]);
						SolutionCopy b9 = new SolutionCopy(offSpring[1]);
						SolutionCopy a10 = new SolutionCopy(offSpring[0]);
						SolutionCopy b10 = new SolutionCopy(offSpring[1]);
						SolutionCopy a11 = new SolutionCopy(offSpring[0]);
						SolutionCopy b11 = new SolutionCopy(offSpring[1]);

						offSpringForLocal0[0] = a0.getModifySolution();
						offSpringForLocal0[1] = b0.getModifySolution();
						offSpringForLocal1[0] = a1.getModifySolution();
						offSpringForLocal1[1] = b1.getModifySolution();
						offSpringForLocal2[0] = a2.getModifySolution();
						offSpringForLocal2[1] = b2.getModifySolution();
						offSpringForLocal3[0] = a3.getModifySolution();
						offSpringForLocal3[1] = b3.getModifySolution();
						offSpringForLocal4[0] = a4.getModifySolution();
						offSpringForLocal4[1] = b4.getModifySolution();
						offSpringForLocal5[0] = a5.getModifySolution();
						offSpringForLocal5[1] = b5.getModifySolution();
						offSpringForLocal6[0] = a6.getModifySolution();
						offSpringForLocal6[1] = b6.getModifySolution();
						offSpringForLocal7[0] = a7.getModifySolution();
						offSpringForLocal7[1] = b7.getModifySolution();
						offSpringForLocal8[0] = a8.getModifySolution();
						offSpringForLocal8[1] = b8.getModifySolution();
						offSpringForLocal9[0] = a9.getModifySolution();
						offSpringForLocal9[1] = b9.getModifySolution();
						offSpringForLocal10[0] = a10.getModifySolution();
						offSpringForLocal10[1] = b10.getModifySolution();
						offSpringForLocal11[0] = a11.getModifySolution();
						offSpringForLocal11[1] = b11.getModifySolution();
						// atribuicoes -----------------------------------

						Local_Search local0 = new Local_Search(offSpringForLocal0, problem_, operatorLocal);
						Local_Search local1 = new Local_Search(offSpringForLocal1, problem_, operatorLocal);
						Local_Search local2 = new Local_Search(offSpringForLocal2, problem_, operatorLocal);
						Local_Search local3 = new Local_Search(offSpringForLocal3, problem_, operatorLocal);
						Local_Search local4 = new Local_Search(offSpringForLocal4, problem_, operatorLocal);
						Local_Search local5 = new Local_Search(offSpringForLocal5, problem_, operatorLocal);
						Local_Search local6 = new Local_Search(offSpringForLocal6, problem_, operatorLocal);
						Local_Search local7 = new Local_Search(offSpringForLocal7, problem_, operatorLocal);
						Local_Search local8 = new Local_Search(offSpringForLocal8, problem_, operatorLocal);
						Local_Search local9 = new Local_Search(offSpringForLocal9, problem_, operatorLocal);
						Local_Search local10 = new Local_Search(offSpringForLocal10, problem_, operatorLocal);
						Local_Search local11 = new Local_Search(offSpringForLocal11, problem_, operatorLocal);

						// -------------------------------------------------------
						// operador padroes begin

						try {
							executor.execute(local0);
							executor.execute(local1);
							executor.execute(local2);
							executor.execute(local3);
							executor.execute(local4);
							executor.execute(local5);
							executor.execute(local6);
							executor.execute(local7);
							executor.execute(local8);
							executor.execute(local9);
							executor.execute(local10);
							executor.execute(local11);

						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("erro na thread");
						}
						executor.shutdown();
						while (!executor.isTerminated()) {
							// System.out.println("processando");
						}

						solutionsLocal0.add(offSpringForLocal0[0]);
						solutionsLocal1.add(offSpringForLocal0[1]);

						solutionsLocal0.add(offSpringForLocal1[0]);
						solutionsLocal1.add(offSpringForLocal1[1]);

						solutionsLocal0.add(offSpringForLocal2[0]);
						solutionsLocal1.add(offSpringForLocal2[1]);

						solutionsLocal0.add(offSpringForLocal3[0]);
						solutionsLocal1.add(offSpringForLocal3[1]);

						solutionsLocal0.add(offSpringForLocal4[0]);
						solutionsLocal1.add(offSpringForLocal4[1]);

						solutionsLocal0.add(offSpringForLocal5[0]);
						solutionsLocal1.add(offSpringForLocal5[1]);

						solutionsLocal0.add(offSpringForLocal6[0]);
						solutionsLocal1.add(offSpringForLocal6[1]);

						solutionsLocal0.add(offSpringForLocal7[0]);
						solutionsLocal1.add(offSpringForLocal7[1]);

						solutionsLocal0.add(offSpringForLocal8[0]);
						solutionsLocal1.add(offSpringForLocal8[1]);

						solutionsLocal0.add(offSpringForLocal9[0]);
						solutionsLocal1.add(offSpringForLocal9[1]);

						solutionsLocal0.add(offSpringForLocal10[0]);
						solutionsLocal1.add(offSpringForLocal10[1]);

						solutionsLocal0.add(offSpringForLocal11[0]);
						solutionsLocal1.add(offSpringForLocal11[1]);

						// operador padroes end

						// --------------------------------------------------------

						for (int k = 0; k < offSpring[0].numberOfObjectives(); k++) {
							arraySolutionBest0[0] += offSpringForLocal0[0].getObjective(k);
							arraySolutionBest1[0] += offSpringForLocal0[1].getObjective(k);
							arraySolutionBest0[1] += offSpringForLocal1[0].getObjective(k);
							arraySolutionBest1[1] += offSpringForLocal1[1].getObjective(k);
							arraySolutionBest0[2] += offSpringForLocal2[0].getObjective(k);
							arraySolutionBest1[2] += offSpringForLocal2[1].getObjective(k);
							arraySolutionBest0[3] += offSpringForLocal3[0].getObjective(k);
							arraySolutionBest1[3] += offSpringForLocal3[1].getObjective(k);
							arraySolutionBest0[4] += offSpringForLocal4[0].getObjective(k);
							arraySolutionBest1[4] += offSpringForLocal4[1].getObjective(k);
							arraySolutionBest0[5] += offSpringForLocal5[0].getObjective(k);
							arraySolutionBest1[5] += offSpringForLocal5[1].getObjective(k);
							arraySolutionBest0[6] += offSpringForLocal6[0].getObjective(k);
							arraySolutionBest1[6] += offSpringForLocal6[1].getObjective(k);
							arraySolutionBest0[7] += offSpringForLocal7[0].getObjective(k);
							arraySolutionBest1[7] += offSpringForLocal7[1].getObjective(k);
							arraySolutionBest0[8] += offSpringForLocal8[0].getObjective(k);
							arraySolutionBest1[8] += offSpringForLocal8[1].getObjective(k);
							arraySolutionBest0[9] += offSpringForLocal9[0].getObjective(k);
							arraySolutionBest1[9] += offSpringForLocal9[1].getObjective(k);
							arraySolutionBest0[10] += offSpringForLocal10[0].getObjective(k);
							arraySolutionBest1[10] += offSpringForLocal10[1].getObjective(k);
							arraySolutionBest0[11] += offSpringForLocal11[0].getObjective(k);
							arraySolutionBest1[11] += offSpringForLocal11[1].getObjective(k);
						}

						int position0 = 0, position1 = 0;

						double min0 = arraySolutionBest0[0];
						double min1 = arraySolutionBest1[0];
						for (int w = 0; w < 12; w++) {
							if (arraySolutionBest0[w] < min0) {
								min0 = arraySolutionBest0[w];
								position0 = w;
							}
							if (arraySolutionBest1[w] < min1) {
								min1 = arraySolutionBest1[w];
								position1 = w;
							}
						}

						if (problem_.isPatterns(solutionsLocal0.get(position0))) {
							this.gravarAplicacao("melhor das 12 COM patterns");
						} else {
							this.gravarAplicacao("melhor das 12 SEM patterns");
						}
						if (problem_.isPatterns(solutionsLocal1.get(position1))) {
							this.gravarAplicacao("melhor das 12 COM patterns");
						} else {
							this.gravarAplicacao("melhor das 12 SEM patterns");
						}

						offspringPopulation.add(solutionsLocal0.get(position0));
						offspringPopulation.add(solutionsLocal1.get(position1));
						evaluations += 2;

						this.gravarTempo(System.currentTimeMillis() - timeBegin);
					}

				}
				this.gravarAplicacao("FIM DA POPULACAO");
				// Create the solutionSet union of solutionSet and offSpring
				union = ((SolutionSet) population).union(offspringPopulation);

				// Ranking the union
				Ranking ranking = new Ranking(union);

				int remain = populationSize;
				int index = 0;
				SolutionSet front = null;
				population.clear();

				// Obtain the next front
				front = ranking.getSubfront(index);

				while ((remain > 0) && (remain >= front.size())) {
					// Assign crowding distance to individuals
					distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
					// Add the individuals of this front
					for (int k = 0; k < front.size(); k++) {
						population.add(front.get(k));
					}

					// Decrement remain
					remain = remain - front.size();

					// Obtain the next front
					index++;
					if (remain > 0) {
						front = ranking.getSubfront(index);
					}
				}

				// Remain is less than front(index).size, insert only the best
				// one
				if (remain > 0) { // front contains individuals to insert
					distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
					front.sort(new CrowdingComparator());
					for (int k = 0; k < remain; k++) {
						population.add(front.get(k));
					}
					remain = 0;
				}

				// This piece of code shows how to use the indicator object into
				// the code
				// of NSGA-II. In particular, it finds the number of evaluations
				// required
				// by the algorithm to obtain a Pareto front with a hypervolume
				// higher
				// than the hypervolume of the true Pareto front.
				if ((indicators != null) && (requiredEvaluations == 0)) {
					double HV = indicators.getHypervolume(population);
					if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
						requiredEvaluations = evaluations;
					}
				}
			}

		} catch (Exception b) {
			System.out.println("Erro no Bestof12");
			b.printStackTrace();
		}

		// Return as output parameter the required evaluations
		setOutputParameter("evaluations", requiredEvaluations);

		// Return the first non-dominated front
		Ranking ranking = new Ranking(population);
		// System.out.println(ranking);
		return ranking.getSubfront(0);
		// return population;
	} // execute
} // NSGA-II