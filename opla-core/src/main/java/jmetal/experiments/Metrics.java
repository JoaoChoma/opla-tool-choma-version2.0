package jmetal.experiments;

public enum Metrics {
	
	CONVENTIONAL("conventional"),
	ELEGANCE("elegance"),
	FEATURE_DRIVEN("featureDriven"),
	PLA_EXTENSIBILIY("PLAExtensibility"),
	ACOMP("acomp"),
	ACLASS("aclass"),
	TAM("tam"),
	COE("coe"),
	EC("ec"),
	DC("dc"),
	
	WOCSCLASS ("wocsclass"),//addYni
	WOCSINTER ("wocsinterface"),//addYni
	CBCS ("cbcs"), //addYni
	SVC ("svc"),//addYni
	SSC ("ssc"),//addYni
	AV ("av"), //addYni
	LCC ("lcc")
	
	;
	
	
	
	private String name;
	
	private Metrics(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}

}
