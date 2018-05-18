package br.ufpr.inf.opla.patterns.designpatterns;

import arquitetura.exceptions.ConcernNotFoundException;
import arquitetura.representation.Concern;
import arquitetura.representation.Element;
import arquitetura.representation.Interface;
import arquitetura.representation.Variant;
import arquitetura.representation.relationship.Relationship;
import br.ufpr.inf.opla.patterns.models.Scope;
import br.ufpr.inf.opla.patterns.repositories.ArchitectureRepository;
import br.ufpr.inf.opla.patterns.util.AdapterUtil;
import br.ufpr.inf.opla.patterns.util.ElementUtil;
import br.ufpr.inf.opla.patterns.util.RelationshipUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.CollectionUtils;

public class Adapter extends DesignPattern {

	private static volatile Adapter INSTANCE;

	public static synchronized Adapter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Adapter();
		}
		return INSTANCE;
	}

	private Adapter() {
		super("Adapter", "Structural");
	}

	@Override
	public boolean verifyPS(Scope scope) {
		return false;
	}

	@Override
	public boolean verifyPSPLA(Scope scope) {
		return false;
	}

	@Override
	public boolean apply(Scope scope) {
		return false;
	}

	public arquitetura.representation.Class applyAdapter(Element target, Element adaptee) {
		arquitetura.representation.Class adapterClass = null;
		if (target != null && adaptee != null
				&& (target instanceof arquitetura.representation.Class || target instanceof Interface)
				&& (adaptee instanceof arquitetura.representation.Class || adaptee instanceof Interface)) {
			/*
			 * if (target instanceof arquitetura.representation.Class) {
			 * System.out.println("arquiteture"); } if (target instanceof
			 * Interface) { System.out.println("interface"); }
			 */
			try {

				adapterClass = AdapterUtil.getAdapterClass(target, adaptee);
				if (adapterClass == null) {
					adapterClass = AdapterUtil.createAdapterClass(adaptee);
				}
				// Implements/Extends and add all methods.
				if (target instanceof arquitetura.representation.Class) {
					ElementUtil.extendClass(adapterClass, (arquitetura.representation.Class) target);
				} else {
					ElementUtil.implementInterface(adapterClass, (Interface) target);
				}

				RelationshipUtil.createNewUsageRelationship("adaptee", adapterClass, adaptee);

				Relationship relationshipToBeExcluded = null;
				if (adaptee.getClass().equals(target.getClass())) {
					for (Relationship relationship : ElementUtil.getRelationships(adaptee)) {
						// erro aqui
						/*
						 * if(target==null){ System.out.println("target e null"
						 * ); }else{ System.out.println("target nao null"); }
						 * if(relationship==null){ System.out.println(
						 * "relationship e null"); }else{ System.out.println(
						 * "relationship nao null"); }
						 */
						// joao
						if (RelationshipUtil.getSuperElement(relationship) == null) {
							continue;
						} else {
							if (target.equals(RelationshipUtil.getSuperElement(relationship))) {

								relationshipToBeExcluded = relationship;
								break;
							}
						}
					}
				} else {
					for (Relationship relationship : ElementUtil.getRelationships(adaptee)) {
						// joao
						if (RelationshipUtil.getImplementedInterface(relationship) == null) {
							continue;
						} else {
							if (target.equals(RelationshipUtil.getImplementedInterface(relationship))) {
								relationshipToBeExcluded = relationship;
								break;
							}
						}
					}
				}

				if (relationshipToBeExcluded != null) {
					ArchitectureRepository.getCurrentArchitecture().removeRelationship(relationshipToBeExcluded);
				}

				// Copy concerns
				for (Concern concern : CollectionUtils.union(target.getOwnConcerns(), adaptee.getOwnConcerns())) {
					try {
						adapterClass.addConcern(concern.getName());
					} catch (ConcernNotFoundException ex) {
						//System.out.println("Erro 01");
						Logger.getLogger(Adapter.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				// Move variants
				Variant variant = adaptee.getVariant();
				if (variant != null) {
					adaptee.setVariant(null);
					adapterClass.setVariant(variant);
					variant.setVariantElement(adapterClass);
				}

				addStereotype(target);
				addStereotype(adaptee);
				addStereotype(adapterClass);
			} catch (Exception ex) {
				//System.out.println("Erro 02");
				Logger.getLogger(Adapter.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return adapterClass;
	}

}
