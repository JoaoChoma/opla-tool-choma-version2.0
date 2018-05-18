//  MutationFactory.java
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

package jmetal.operators.mutation;

import java.util.HashMap;

import jmetal.experiments.ExperimentCommomConfigs;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import br.ufpr.inf.opla.patterns.operator.impl.DesignPatternMutationOperator;
import br.ufpr.inf.opla.patterns.strategies.designpatternselection.impl.CustomDesignPatternSelection;

public class MutationFactory {

	private static final String DESIGN_PATTERNS = "DesignPatterns";

	/**
	 * Gets a mutation operator through its name.
	 * 
	 * @param name
	 *            of the operator
	 * @params configs
	 * @param configs.getMutationOperators()
	 * @return the operator
	 * @throws JMException
	 */
	

	public static Mutation getMutationOperator(String name, HashMap<String, Object> parameters,
			ExperimentCommomConfigs configs) throws JMException {
		
		if (isOnlyDesignPattern(configs)) {
			//jcn - so patterns
			return new DesignPatternMutationOperator(parameters, configs.getDesignPatternStrategy(),
					new CustomDesignPatternSelection(configs.getPatterns()));
			
		} else if (isDesignPatternAndPlaFeatureMutation(configs)) {
			//jcn - chama uma classe no pacote patterns
			DesignPatternMutationOperator dpm = new DesignPatternMutationOperator(parameters,
					configs.getDesignPatternStrategy(), new CustomDesignPatternSelection(configs.getPatterns()));

			configs.excludeDesignPatternsFromMutationOperatorList();
			
			PLAFeatureMutation pf = new PLAFeatureMutation(parameters, configs.getMutationOperators());
			
			//jcn - chama a classe DesignPatternAndPLAMutation
			return new DesignPatternAndPLAMutation(parameters, dpm, pf);
		
		} else if (isOnlyPLAFeatureMutation(configs)) {
			//jcn - so operadores convencionais
			return new PLAFeatureMutation(parameters, configs.getMutationOperators());
		
		} else {
			Configuration.logger_.severe("Operator '" + name + "' not found ");
			Class<String> cls = java.lang.String.class;
			String name2 = cls.getName();
			throw new JMException("Exception in " + name2 + ".getMutationOperator()");
		}
	}

	// jcn - novo
	public static Mutation getMutationOperatorPatterns(String name, HashMap<String, Object> parameters,
			ExperimentCommomConfigs configs) throws JMException {
		
		return new DesignPatternMutationOperator(parameters, configs.getDesignPatternStrategy(),
				new CustomDesignPatternSelection(configs.getPatterns()));

	}

	private static boolean isOnlyPLAFeatureMutation(ExperimentCommomConfigs configs) {
		return !configs.getMutationOperators().contains(DESIGN_PATTERNS) && configs.getMutationOperators().size() >= 1;
	}

	private static boolean isDesignPatternAndPlaFeatureMutation(ExperimentCommomConfigs configs) {
		return configs.getMutationOperators().contains(DESIGN_PATTERNS) && configs.getMutationOperators().size() > 1;
	}

	private static boolean isOnlyDesignPattern(ExperimentCommomConfigs configs) {
		return configs.getMutationOperators().contains(DESIGN_PATTERNS) && configs.getMutationOperators().size() == 1;
	}
}
