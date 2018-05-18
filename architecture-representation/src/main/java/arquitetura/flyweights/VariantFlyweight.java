package arquitetura.flyweights;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Stereotype;

import arquitetura.helpers.StereotypeHelper;
import arquitetura.helpers.XmiHelper;
import arquitetura.representation.Architecture;
import arquitetura.representation.Element;
import arquitetura.representation.Variability;
import arquitetura.representation.Variant;
import arquitetura.representation.VariantType;

public class VariantFlyweight  extends XmiHelper{
	
	private Architecture architecture;
	
	private static final VariantFlyweight INSTANCE = new VariantFlyweight();
	private HashMap<String, Variant> variants = new HashMap<String, Variant>();
	
	
	private VariantFlyweight(){}
	
	public static VariantFlyweight getInstance(){
		return INSTANCE;
	}
	
	public Variant getOrCreateVariant(Classifier klass) {
		
		Variant variant = variants.get(klass.getName());
		if(variant == null){
			Stereotype variantType = StereotypeHelper.getVariantType(klass);
			VariantType type = VariantType.getByName(variantType.getName());
			
			if ((type != null)){
				Element variantElement;
				try {
					variantElement = architecture.findElementById(getXmiId(klass));
					
					String rootVp = StereotypeHelper.getValueOfAttribute(klass, variantType, "rootVP");
					VariabilityFlyweight.getInstance().getVariabilities();
					variant = Variant.createVariant().withName(klass.getName())
							   .withVariantType(variantType.getName())
							   .andRootVp(rootVp)
							   .build();
					variant.setVariantElement(variantElement);
					variantElement.setVariant(variant);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				String[] variabilitiesForVariant = StereotypeHelper.getValueOfAttribute(klass, variantType, "variabilities").split(",");
				for (String variability : variabilitiesForVariant) {
					Variability variabilityVariant = VariabilityFlyweight.getInstance().getVariability(variability.trim());
					if(variabilityVariant != null){
						if(!variabilityVariant.getVariants().contains(variant)){
							variabilityVariant.getVariants().add(variant);
							variant.getVariabilities().add(variabilityVariant);
						}
					}
				}
				variants.put(variant.getName(), variant);
			}
			
		}
		
		return variant;
	}

	public Variant getVariant(String name){
		return variants.get(name);
	}
	

	public List<Variant> getVariants() {
		return new ArrayList<Variant>(variants.values()); 
	}
	

	public void setArchitecture(Architecture architecture) {
		this.architecture = architecture;
	}

	public void resetVariants() {
		this.variants.clear();
	}

}
