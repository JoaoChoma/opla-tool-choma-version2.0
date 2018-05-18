package br.ufpr.inf.opla.patterns.operator;

import arquitetura.exceptions.ClassNotFound;
import arquitetura.exceptions.ConcernNotFoundException;
import arquitetura.exceptions.NotFoundException;
import arquitetura.exceptions.PackageNotFound;
import arquitetura.representation.Architecture;
import arquitetura.representation.Interface;
import br.ufpr.inf.opla.patterns.operator.impl.DesignPatternsAndPLAMutationOperator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import jmetal.core.Solution;
import jmetal.operators.mutation.Mutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.ufpr.inf.opla.patterns.operator.impl.DesignPatternMutationOperator;

public abstract class AbstractMutationOperator extends Mutation {

	public static final Logger LOGGER = LogManager.getLogger(DesignPatternsAndPLAMutationOperator.class);

	public AbstractMutationOperator(HashMap<String, Object> parameters) {
		super(parameters);
	}

	@Override
	public synchronized Object execute(Object o) throws JMException, CloneNotSupportedException, ClassNotFound, PackageNotFound,
			NotFoundException, ConcernNotFoundException {
		Solution solution = (Solution) o;
		Double probability = (Double) getParameter("probability");
		
		if (probability == null) {
			Configuration.logger_.severe("FeatureMutation.execute: probability not specified");
			java.lang.Class<String> cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		}

		try {
			hookMutation(solution, probability);
			// esta estourando excessão aqui
		} catch (Exception ex) {
			
			java.util.logging.Logger.getLogger(AbstractMutationOperator.class.getName()).log(Level.SEVERE, null, ex);

		}

		return solution;
	}

	protected abstract boolean hookMutation(Solution solution, Double probability) throws Exception;

	protected boolean isValidSolution(Architecture solution) {
		boolean isValid = true;
		List<Interface> allInterfaces = new ArrayList<>(solution.getAllInterfaces());
		if (!allInterfaces.isEmpty()) {
			for (Interface itf : allInterfaces) {
				if ((itf.getImplementors().isEmpty()) && (itf.getDependents().isEmpty())
						&& (!itf.getOperations().isEmpty())) {
					return false;
				}
			}
		}
		return isValid;
	}

}
