package jmetal.metrics.conventionalMetrics;
import java.util.ArrayList;
import java.util.List;

import arquitetura.representation.Architecture;
import arquitetura.representation.Package;
import arquitetura.representation.relationship.AbstractionRelationship;
import arquitetura.representation.relationship.DependencyRelationship;
import arquitetura.representation.relationship.GeneralizationRelationship;
import arquitetura.representation.relationship.RealizationRelationship;
import arquitetura.representation.relationship.Relationship;
import arquitetura.representation.relationship.UsageRelationship;

public class DependencyOut {

	private int results;
	
  public DependencyOut(Architecture architecture) {

		this.results = 0;
		int depOut = 0;
		
	for (Package component : architecture.getAllPackages()) {
		List<Relationship> relationships = new ArrayList<Relationship>(architecture.getRelationshipHolder().getAllRelationships());

		 for (Relationship relationship : relationships) {
		    	if (relationship instanceof AbstractionRelationship) {
		    		AbstractionRelationship abstraction = (AbstractionRelationship) relationship;
		    		if (abstraction.getClient().getNamespace().contains(component.getName())) depOut++;
			    	
			    }else if (relationship instanceof DependencyRelationship) {
			    	DependencyRelationship dependency = (DependencyRelationship) relationship;
		    		if (dependency.getClient().getNamespace().contains(component.getName())) depOut++;
					
				}else if (relationship instanceof UsageRelationship) {
					UsageRelationship usage = (UsageRelationship) relationship;
					if (usage.getClient().getNamespace().contains(component.getName())) depOut++;
					
				}else if (relationship instanceof RealizationRelationship) {
					RealizationRelationship realization = (RealizationRelationship) relationship;
					if (realization.getClient().getNamespace().contains(component.getName())) depOut++;
					
				}
		    }

		this.results += depOut; // somatorio de DepOut da arquitetura como um todo
		depOut= 0;
	}
  }

	public int getResults() {
		return results;
	}

}
