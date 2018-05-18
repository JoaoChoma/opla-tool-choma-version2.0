package jmetal.metrics.conventionalMetrics;

import arquitetura.representation.Architecture;
import arquitetura.representation.Class;

//Numbers among classes elegance metric

public class NACElegance {

    private Architecture architecture;
    private Double results;

    public NACElegance(Architecture architecture) {

	this.architecture = architecture;
	this.results = 0.0;
	double stdDeviationAttributes = 0.0;
	double stdDeviationMethods = 0.0;
	double arrayAttributesNumbers[] = new double[10000];
	double arrayMethodsNumbers[] = new double[10000];
	int i = 0;
	int j = 0;


	Estatistica e = new Estatistica();

	for (Class cls : this.architecture.getAllClasses()) {
	    // seta valores dos arrays
	    arrayAttributesNumbers[i] = cls.getAllAttributes().size();
	    i++;
	    arrayMethodsNumbers[j] = cls.getAllMethods().size();
	    j++;
	}
	e.setArray(arrayAttributesNumbers);
	stdDeviationAttributes = e.getDesvioPadrao();

	e.setArray(arrayMethodsNumbers);
	stdDeviationMethods = e.getDesvioPadrao();

	this.results = (stdDeviationAttributes + stdDeviationMethods) / 2;
    }

    public Double getResults() {
	return results;
    }

}
