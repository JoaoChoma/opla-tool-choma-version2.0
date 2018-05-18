package jmetal.problems;

public class CapturarValoresFitness {
	
	
	static double elegance = 0;
	static double conventional = 0;
	static double featureDriven = 0;
	static double PLAExtensibility = 0;
	static double acomp = 0;
	static double aclass = 0;
	static double tam = 0;
	static double coe = 0;
	static double dc = 0;
	static double ec = 0;
	
	static boolean eleganceB = false;
	static boolean conventionalB = false;
	static boolean featureDrivenB = false;
	static boolean PLAExtensibilityB = false;
	static boolean acompB = false;
	static boolean aclassB = false;
	static boolean tamB = false;
	static boolean coeB = false;
	public static boolean isEleganceB() {
		return eleganceB;
	}

	public static void setEleganceB(boolean eleganceB) {
		CapturarValoresFitness.eleganceB = eleganceB;
	}

	public static boolean isConventionalB() {
		return conventionalB;
	}

	public static void setConventionalB(boolean conventionalB) {
		CapturarValoresFitness.conventionalB = conventionalB;
	}

	public static boolean isFeatureDrivenB() {
		return featureDrivenB;
	}

	public static void setFeatureDrivenB(boolean featureDrivenB) {
		CapturarValoresFitness.featureDrivenB = featureDrivenB;
	}

	public static boolean isPLAExtensibilityB() {
		return PLAExtensibilityB;
	}

	public static void setPLAExtensibilityB(boolean pLAExtensibilityB) {
		PLAExtensibilityB = pLAExtensibilityB;
	}

	public static boolean isAcompB() {
		return acompB;
	}

	public static void setAcompB(boolean acompB) {
		CapturarValoresFitness.acompB = acompB;
	}

	public static boolean isAclassB() {
		return aclassB;
	}

	public static void setAclassB(boolean aclassB) {
		CapturarValoresFitness.aclassB = aclassB;
	}

	public static boolean isTamB() {
		return tamB;
	}

	public static void setTamB(boolean tamB) {
		CapturarValoresFitness.tamB = tamB;
	}

	public static boolean isCoeB() {
		return coeB;
	}

	public static void setCoeB(boolean coeB) {
		CapturarValoresFitness.coeB = coeB;
	}

	public static boolean isDcB() {
		return dcB;
	}

	public static void setDcB(boolean dcB) {
		CapturarValoresFitness.dcB = dcB;
	}

	public static boolean isEcB() {
		return ecB;
	}

	public static void setEcB(boolean ecB) {
		CapturarValoresFitness.ecB = ecB;
	}

	static boolean dcB = false;
	static boolean ecB = false;

	public static double getElegance() {
		return elegance;
	}

	public static void setElegance(double elegance) {
		CapturarValoresFitness.elegance = elegance;
	}

	public static double getConventional() {
		return conventional;
	}

	public static void setConventional(double conventional) {
		CapturarValoresFitness.conventional = conventional;
	}

	public static double getFeatureDriven() {
		return featureDriven;
	}

	public static void setFeatureDriven(double featureDriven) {
		CapturarValoresFitness.featureDriven = featureDriven;
	}

	public static double getPLAExtensibility() {
		return PLAExtensibility;
	}

	public static void setPLAExtensibility(double pLAExtensibility) {
		PLAExtensibility = pLAExtensibility;
	}

	public static double getAcomp() {
		return acomp;
	}

	public static void setAcomp(double acomp) {
		CapturarValoresFitness.acomp = acomp;
	}

	public static double getAclass() {
		return aclass;
	}

	public static void setAclass(double aclass) {
		CapturarValoresFitness.aclass = aclass;
	}

	public static double getTam() {
		return tam;
	}

	public static void setTam(double tam) {
		CapturarValoresFitness.tam = tam;
	}

	public static double getCoe() {
		return coe;
	}

	public static void setCoe(double coe) {
		CapturarValoresFitness.coe = coe;
	}

	public static double getEc() {
		return ec;
	}

	public static void setEc(double ec) {
		CapturarValoresFitness.ec = ec;
	}

	public static double getDc() {
		return dc;
	}

	public static void setDc(double dc) {
		CapturarValoresFitness.dc = dc;
	}

	public void limparDados(){
		this.setAclassB(false);
		this.setAcompB(false);
		this.setCoeB(false);
		this.setConventionalB(false);
		this.setDcB(false);
		this.setEcB(false);
		this.setEleganceB(false);
		this.setFeatureDrivenB(false);
		this.setPLAExtensibilityB(false);
		this.setTamB(false);
	}
	
	public void numeroFuncoes(){
		if(this.isAclassB()==true){
			
		}
	}

}
