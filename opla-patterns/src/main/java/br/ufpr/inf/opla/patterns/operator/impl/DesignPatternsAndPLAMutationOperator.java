package br.ufpr.inf.opla.patterns.operator.impl;

import br.ufpr.inf.opla.patterns.operator.AbstractMutationOperator;
import br.ufpr.inf.opla.patterns.strategies.designpatternselection.DesignPatternSelectionStrategy;
import br.ufpr.inf.opla.patterns.strategies.scopeselection.ScopeSelectionStrategy;
import java.util.HashMap;
import jmetal.core.Solution;
import jmetal.util.PseudoRandom;

public class DesignPatternsAndPLAMutationOperator extends AbstractMutationOperator {

    private final PLAMutation pLAMutation;
    private final DesignPatternMutationOperator designPatternMutationOperator;

    public DesignPatternsAndPLAMutationOperator(HashMap<String, Object> parameters, ScopeSelectionStrategy scopeSelectionStrategy, DesignPatternSelectionStrategy designPatternSelectionStrategy) {
        super(parameters);
        pLAMutation = new PLAMutation(parameters);
        designPatternMutationOperator = new DesignPatternMutationOperator(parameters, scopeSelectionStrategy, designPatternSelectionStrategy);
    }

    @Override
    protected synchronized boolean hookMutation(Solution solution, Double probability) throws Exception {
    	//System.out.println("DesignPatternsAndPLA... - Entrou no sorteio");
        int random = PseudoRandom.randInt(0, 6);
        if (random == 0) {
        	System.out.println("DesignPatternsAndMutationOperator - para designPatternMutationOperator");
            return designPatternMutationOperator.hookMutation(solution, probability);
        } else {
        	System.out.println("DesignPatternsAndMutationOperator - para PLAMutation");
            return pLAMutation.hookMutation(solution, probability);
        }
    }

}
