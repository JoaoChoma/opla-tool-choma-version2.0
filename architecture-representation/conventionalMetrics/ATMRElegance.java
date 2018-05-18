package br.uem.din.metrics.conventionalMetrics;

import br.uem.din.architectureEvolution.representation.Class;

//Attributes to methods ratio elegance metric
public class ATMRElegance {

	private Architecture architecture;
	private double results;
	
		
	public ATMRElegance(Architecture architecture){
	
	this.architecture = architecture;
	this.results = 0.0;
	double stdDeviationRatios = 0.0;
	double ratiosAttributesMethods[]= new double[10000];
    int i = 0;
        
	//Instancia a classe utilit�ria

    Estatistica e = new Estatistica();
     
    for (Class cls: architecture.getClasses()){
			//seta valores dos arrays
			if (cls.getMethods().size()>0){
				ratiosAttributesMethods[i]= (double) cls.getAttributes().size()/cls.getMethods().size();
				i++;
			}
			else {
				ratiosAttributesMethods[i] = 0.0;
				i++;
			}
			
	}
	e.setArray(ratiosAttributesMethods);
	stdDeviationRatios = e.getDesvioPadrao();
		
	this.results = stdDeviationRatios; 
}
	
	public double getResults() {
		return results;
	}
	
}
