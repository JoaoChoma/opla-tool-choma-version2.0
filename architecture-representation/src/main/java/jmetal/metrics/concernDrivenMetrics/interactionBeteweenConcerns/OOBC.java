package jmetal.metrics.concernDrivenMetrics.interactionBeteweenConcerns;

import java.util.Collection;
import java.util.HashMap;

import arquitetura.representation.Architecture;
import arquitetura.representation.Concern;
import arquitetura.representation.Interface;
import arquitetura.representation.Method;
import arquitetura.representation.Package;

public class OOBC {

	private final Architecture architecture;
	private final HashMap<Concern, OOBCResult> results = new HashMap<Concern, OOBCResult>();

	public OOBC(Architecture architecture){
		this.architecture = architecture;
		
		for (Package component : architecture.getAllPackages()) {
			inspectInterfacesOperations(component.getImplementedInterfaces());
			inspectInterfacesOperations(component.getRequiredInterfaces());
		}
	}

	private void inspectInterfacesOperations(Collection<Interface> interfaces) {
		for (Interface i : interfaces) {
			for (Method operation : i.getOperations()) {
				inspectConcernsOfOperation(operation);
			}
		}
	}

	private void inspectConcernsOfOperation(Method operation) {
		for (Concern concern : operation.getOwnConcerns()) {
			if (results.containsKey(concern))
				results.get(concern).addInterlacedConcerns(operation);
			else
				results.put(concern, new OOBCResult(concern, operation));
		}
	}

	public Architecture getArchitecture() {
		return architecture;
	}

	public HashMap<Concern, OOBCResult> getResults() {
		return results;
	}
}
