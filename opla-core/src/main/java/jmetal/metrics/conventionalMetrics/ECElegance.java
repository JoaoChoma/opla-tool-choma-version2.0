package jmetal.metrics.conventionalMetrics;

import java.util.ArrayList;
import java.util.List;

import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.relationship.AssociationEnd;
import arquitetura.representation.relationship.AssociationRelationship;
import arquitetura.representation.relationship.DependencyRelationship;
import arquitetura.representation.relationship.Relationship;

public class ECElegance {

    private Double results;

    // External Coupling Elegance metric
    public ECElegance(Architecture architecture) {

	this.results = 0.0;
	double stdDeviationCouples = 0.0;
	double externalCouplesNumbers[] = new double[10000];
	int i = 0;

	Estatistica e = new Estatistica();

	for (Class cls : architecture.getAllClasses()) {
	    // busca os external couples de cada classe
	    externalCouplesNumbers[i] = searchClassDependencies(cls);
	    i++;
	}

	e.setArray(externalCouplesNumbers);
	stdDeviationCouples = e.getDesvioPadrao();

	this.results = stdDeviationCouples;

    }

    // ----------------------------------------------------------------------------------

    private int searchClassDependencies(Class source) {
	int cont = 0;
	List<Relationship> relationships = new ArrayList<Relationship>(source.getRelationships());
	if (relationships != null) {
	    for (Relationship relationship : relationships) {
		if (relationship instanceof DependencyRelationship) {
		    DependencyRelationship dependency = (DependencyRelationship) relationship;
		    if (dependency.getClient().equals(source))
			cont++;
		} else {
		    if (relationship instanceof AssociationRelationship) {
			AssociationRelationship association = (AssociationRelationship) relationship;
			for (AssociationEnd associationEnd : association.getParticipants()) {
			    if (associationEnd.getCLSClass().equals(source))
				cont++;
			}
		    }
		}
	    }
	}
	return cont;
    }

    public Double getResults() {
	return results;
    }

}
