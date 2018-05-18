/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpr.inf.opla.patterns.factory;

import br.ufpr.inf.opla.patterns.operator.impl.DesignPatternMutationOperator;
import br.ufpr.inf.opla.patterns.operator.impl.DesignPatternsAndPLAMutationOperator;
import br.ufpr.inf.opla.patterns.operator.impl.PLAMutation;
import br.ufpr.inf.opla.patterns.operator.impl.PLAMutationThenDesignPatternsMutationOperator;
import java.util.HashMap;
import jmetal.operators.mutation.Mutation;

/**
 *
 * @author giovaniguizzo
 */
public class MutationOperatorFactory {

    public static Mutation create(String operator, HashMap<String, Object> parameters) {
        switch (operator) {
            case "DesignPatternsMutationOperator":
            	//System.out.println("classe MutationOperatorFactory - entrou DesignPattersMutationOperator");
                return new DesignPatternMutationOperator(parameters, null, null);
            case "DesignPatternsAndPLAMutationOperator":
            	//System.out.println("classe MutationOperatorFactory - entrou DesignPattersAndPLAMutationOperator");
                return new DesignPatternsAndPLAMutationOperator(parameters, null, null);
            case "PLAMutation":
            	//System.out.println("classe MutationOperatorFactory - entrou PLAMutation");
                return new PLAMutation(parameters);
            case "PLAMutationThenDesignPatternsMutationOperator":
            	//System.out.println("classe MutationOperatorFactory - entrou PLAMutationThenDesignPatternsMutationOperator");
                return new PLAMutationThenDesignPatternsMutationOperator(parameters, null, null);
            default:
                return null;
        }
    }

}
