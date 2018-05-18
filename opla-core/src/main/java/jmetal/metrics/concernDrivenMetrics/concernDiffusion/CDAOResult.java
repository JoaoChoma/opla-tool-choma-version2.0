package jmetal.metrics.concernDrivenMetrics.concernDiffusion;
import java.util.ArrayList;
import java.util.Collection;

import arquitetura.representation.Architecture;
import arquitetura.representation.Concern;
import arquitetura.representation.Interface;
import arquitetura.representation.Method;
import arquitetura.representation.Package;


public class CDAOResult extends ConcernDiffusionResult<Method> {

	public CDAOResult(Concern concern, Architecture architecture) {
		super(concern, architecture);
	}

	@Override
	protected void loadElements(Architecture architecture) {
		for (Package component : architecture.getAllPackages()) {
			if (componentContainsConcern(component))
				getElements().addAll(getAllOperationsOfAllInterfaces(component));
			else
				inspectInterfaces(component);
		}
	}

	private Collection<Method> getAllOperationsOfAllInterfaces(Package component) {
		ArrayList<Method> operations = new ArrayList<Method>();  
	
		for (Interface i : component.getImplementedInterfaces()) 
			operations.addAll(i.getOperations());
		
		return operations;
	}
	
	private void inspectInterfaces(Package component) {
		for (Interface i : component.getImplementedInterfaces()) {
			if (interfaceContainsConcern(i))
				getElements().addAll(i.getOperations());
			else
				inspectOperations(i);
		}
	}
	
	private void inspectOperations(Interface i) {
		for (Method operation : i.getOperations()) {
			if (operation.containsConcern(getConcern()))
				getElements().add(operation);
		}
	}
}
