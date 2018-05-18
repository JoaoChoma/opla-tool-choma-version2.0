/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpr.inf.opla.patterns.operator.impl;

import arquitetura.representation.Architecture;
import arquitetura.representation.Patterns;
import br.ufpr.inf.opla.patterns.designpatterns.DesignPattern;
import br.ufpr.inf.opla.patterns.models.Scope;
import br.ufpr.inf.opla.patterns.operator.AbstractMutationOperator;
import static br.ufpr.inf.opla.patterns.operator.AbstractMutationOperator.LOGGER;
import br.ufpr.inf.opla.patterns.repositories.ArchitectureRepository;
import br.ufpr.inf.opla.patterns.strategies.designpatternselection.DesignPatternSelectionStrategy;
import br.ufpr.inf.opla.patterns.strategies.designpatternselection.defaultstrategy.RandomDesignPatternSelection;
import br.ufpr.inf.opla.patterns.strategies.scopeselection.ScopeSelectionStrategy;
import br.ufpr.inf.opla.patterns.strategies.scopeselection.defaultstrategy.RandomScopeSelection;
import java.util.HashMap;
import jmetal.core.Solution;
import jmetal.problems.OPLA;
import jmetal.util.PseudoRandom;
import org.apache.log4j.Priority;

/**
 *
 * @author giovaniguizzo
 */
public class DesignPatternMutationOperator extends AbstractMutationOperator {

	protected final ScopeSelectionStrategy scopeSelectionStrategy;
	protected final DesignPatternSelectionStrategy designPatternSelectionStrategy;

	public DesignPatternMutationOperator(HashMap<String, Object> parameters,
			ScopeSelectionStrategy scopeSelectionStrategy,
			DesignPatternSelectionStrategy designPatternSelectionStrategy) {
		super(parameters);
		this.scopeSelectionStrategy = scopeSelectionStrategy;
		this.designPatternSelectionStrategy = designPatternSelectionStrategy;
	}
	
	
	

	@Override
	protected synchronized boolean hookMutation(Solution solution, Double probability) throws Exception {

		// boolean applied = true;
		boolean applied = false;
		if (solution.getDecisionVariables()[0].getVariableType() == java.lang.Class
				.forName(Architecture.ARCHITECTURE_TYPE)) {
			// System.out.println("Design - primeiro if");
			if (PseudoRandom.randDouble() < probability) {
				// System.out.println("Design - segundo if");
				Architecture arch = ((Architecture) solution.getDecisionVariables()[0]);
				if (scopeSelectionStrategy == null && designPatternSelectionStrategy == null) {
					//System.out.println("Design - caso 01");
					this.mutateArchitecture(arch);
				} else if (scopeSelectionStrategy == null) {
					//System.out.println("Design - caso 02");
					this.mutateArchitecture(arch, designPatternSelectionStrategy);
				} else if (designPatternSelectionStrategy == null) {
					//System.out.println("Design - caso 03");
					this.mutateArchitecture(arch, scopeSelectionStrategy);
				} else {
					//System.out.println("Design - caso 04");
					this.mutateArchitecture(arch, scopeSelectionStrategy, designPatternSelectionStrategy);
				}
				applied = true;

			}
		}
		if (!this.isValidSolution(((Architecture) solution.getDecisionVariables()[0]))) {
			// System.out.println("isValid");
			Architecture clone = ((Architecture) solution.getDecisionVariables()[0]).deepClone();
			solution.getDecisionVariables()[0] = clone;
			OPLA.contDiscardedSolutions_++;
			LOGGER.log(Priority.INFO, "Invalid Solution. Reverting Modifications.");
		}
		//System.out.println("DesignPatternMutationOperator aplied=" + applied);
		return applied;

	}

	public Architecture mutateArchitecture(Architecture architecture) {
		RandomScopeSelection rss = new RandomScopeSelection();
		RandomDesignPatternSelection rdps = new RandomDesignPatternSelection();
		return mutateArchitecture(architecture, rss, rdps);
	}

	public Architecture mutateArchitecture(Architecture architecture, ScopeSelectionStrategy scopeSelectionStartegy) {
		RandomDesignPatternSelection rdps = new RandomDesignPatternSelection();
		return mutateArchitecture(architecture, scopeSelectionStartegy, rdps);
	}

	public Architecture mutateArchitecture(Architecture architecture,
			DesignPatternSelectionStrategy designPatternSelectionStrategy) {
		//System.out.println("Design - caso 02 - arch e patterns");
		RandomScopeSelection rss = new RandomScopeSelection();
		return mutateArchitecture(architecture, rss, designPatternSelectionStrategy);
	}

	public Architecture mutateArchitecture(Architecture architecture, ScopeSelectionStrategy scopeSelectionStartegy,
			DesignPatternSelectionStrategy designPatternSelectionStrategy) {

		ArchitectureRepository.setCurrentArchitecture(architecture);
		DesignPattern designPattern = designPatternSelectionStrategy.selectDesignPattern();
		Scope scope = scopeSelectionStartegy.selectScope(architecture,
				Patterns.valueOf(designPattern.getName().toUpperCase()));
		//System.out.println("\n Algum escopo selecionado = " + designPattern.randomlyVerifyAsPSOrPSPLA(scope));
		if (designPattern.randomlyVerifyAsPSOrPSPLA(scope)) {
			if (designPattern.apply(scope)) {
				//System.out.println("\n DesignPatternMutationOperator - aplicando padrao");
				//jcn
				architecture.setAppliedPatterns(true);
				LOGGER.log(Priority.INFO, "Design Pattern " + designPattern.getName() + " applied to scope "
						+ scope.getElements().toString() + " successfully!");
			}
		}
		//if (architecture != null) {
		//	System.out.println("DesignPatternMutationOperator = aplicou MutateArchitecture");
		//}
		return architecture;
	}

}
