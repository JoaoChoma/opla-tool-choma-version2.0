package arquitetura.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Abstraction;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.AssociationClass;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Realization;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Usage;

import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.exceptions.SMartyProfileNotAppliedToModelExcepetion;
import arquitetura.exceptions.VariationPointElementTypeErrorException;
import arquitetura.flyweights.VariabilityFlyweight;
import arquitetura.flyweights.VariantFlyweight;
import arquitetura.flyweights.VariationPointFlyweight;
import arquitetura.helpers.ModelElementHelper;
import arquitetura.helpers.ModelHelper;
import arquitetura.helpers.ModelHelperFactory;
import arquitetura.helpers.StereotypeHelper;
import arquitetura.io.ReaderConfig;
import arquitetura.representation.Architecture;
import arquitetura.representation.ArchitectureHolder;
import arquitetura.representation.Class;
import arquitetura.representation.Concern;
import arquitetura.representation.ConcernHolder;
import arquitetura.representation.Interface;
import arquitetura.representation.Variability;
import arquitetura.representation.relationship.AssociationClassRelationship;
import arquitetura.representation.relationship.Relationship;

import com.rits.cloning.Cloner;

/**
 * Builder responsável por criar a arquitetura.
 * 
 * @author edipofederle<edipofederle@gmail.com>
 *
 */
public class ArchitectureBuilder {

	private Package model;
	private PackageBuilder packageBuilder;
	private ClassBuilder classBuilder;
	private InterfaceBuilder intefaceBuilder;
	private VariabilityBuilder variabilityBuilder;
	private AssociationRelationshipBuilder associationRelationshipBuilder;
	private AssociationClassRelationshipBuilder associationClassRelationshipBuilder;
	private GeneralizationRelationshipBuilder generalizationRelationshipBuilder;
	private DependencyRelationshipBuilder dependencyRelationshipBuilder;
	private RealizationRelationshipBuilder realizationRelationshipBuilder;
	private AbstractionRelationshipBuilder abstractionRelationshipBuilder;
	private UsageRelationshipBuilder usageRelationshipBuilder;

	private ModelHelper modelHelper;

	/**
	 * 
	 */
	public ArchitectureBuilder() {
		// RelationshipHolder.clearLists();
		ConcernHolder.INSTANCE.clear();

		// Load configure file. Call this method only once
		ReaderConfig.load();

		modelHelper = ModelHelperFactory.getModelHelper();
	}

	/**
	 * Cria a arquitetura. Primeiramente é carregado o model (arquivo .uml),
	 * após isso é instanciado o objeto {@link Architecture}. <br/>
	 * Feito isso, é chamado método "initialize", neste método é crializada a
	 * criação dos Builders. <br/>
	 * 
	 * Em seguida, é carregado os pacotes e suas classes. Também é carregada as
	 * classes que não pertencem a pacotes. <br/>
	 * Após isso são carregadas as variabilidade. <br/>
	 * <br/>
	 * 
	 * InterClassRelationships </br>
	 * 
	 * <li>loadGeneralizations</li>
	 * <li>loadAssociations</li>
	 * <li>loadInterClassDependencies</li>
	 * <li>loadRealizations</li> <br/>
	 *
	 * InterElementRelationships </br>
	 * <li>loadInterElementDependencies</li>
	 * <li>loadAbstractions</li>
	 * 
	 * <br>
	 * <br/>
	 * 
	 * @param xmiFilePath
	 *            - arquivo da arquitetura (.uml)
	 * @return {@link Architecture}
	 * @throws Exception
	 */
	public Architecture create(String xmiFilePath) throws Exception {
		try {
			model = modelHelper.getModel(xmiFilePath);
			VariationPointFlyweight.getInstance().addModel(model);
			VariabilityFlyweight.getInstance().addModel(model);

			Architecture architecture = new Architecture(modelHelper.getName(xmiFilePath));

			initialize(architecture);

			if (ReaderConfig.hasConcernsProfile()) {
				Package concerns = modelHelper.loadConcernProfile();
				EList<Stereotype> concernsAllowed = concerns.getOwnedStereotypes();
				for (Stereotype stereotype : concernsAllowed)
					ConcernHolder.INSTANCE.allowedConcerns().add(new Concern(stereotype.getName()));
			}

			for (arquitetura.representation.Package p : loadPackages())
				architecture.addPackage(p); // Classes que possuem pacotes são
											// carregadas juntamente com seus
											// pacotes

			for (Class klass : loadClasses())
				architecture.addExternalClass(klass); // Classes que nao possuem
														// pacotes
			for (Interface inter : loadInterfaces())
				architecture.addExternalInterface(inter);
			architecture.getAllVariabilities().addAll(loadVariability());
			for (Relationship r : loadInterClassRelationships())
				architecture.addRelationship(r);
			for (Relationship as : loadAssociationClassAssociation())
				architecture.addRelationship(as);

			Cloner cloner = new Cloner();
			architecture.setCloner(cloner);
			ArchitectureHolder.setName(architecture.getName());
			return architecture;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<? extends Relationship> loadAbstractions() {
		List<Abstraction> abstractions = modelHelper.getAllAbstractions(model);
		List<Relationship> relations = new ArrayList<Relationship>();
		List<Package> pacakges = modelHelper.getAllPackages(model);

		for (Package package1 : pacakges) {
			List<Abstraction> abs = modelHelper.getAllAbstractions(package1);
			for (Abstraction abstraction : abs)
				relations.add(abstractionRelationshipBuilder.create(abstraction));
		}

		for (Abstraction abstraction : abstractions)
			relations.add(abstractionRelationshipBuilder.create(abstraction));

		if (relations.isEmpty())
			return Collections.emptyList();
		return relations;
	}

	private List<Relationship> loadInterClassRelationships() {
		List<Relationship> relationships = new ArrayList<Relationship>();
		relationships.addAll(loadGeneralizations());
		relationships.addAll(loadAssociations());
		relationships.addAll(loadDependencies()); // Todo renomear carrega todo
													// tipo de depdenencias(
													// pacote -> classe, class
													// -> pacote)
		relationships.addAll(loadRealizations());
		relationships.addAll(loadUsageInterClass());
		relationships.addAll(loadAbstractions());

		if (relationships.isEmpty())
			return Collections.emptyList();
		return relationships;
	}

	private List<? extends Relationship> loadUsageInterClass() {
		List<Relationship> usageClass = new ArrayList<Relationship>();
		List<Usage> usages = modelHelper.getAllUsage(model);

		List<Package> pacotes = modelHelper.getAllPackages(model);
		for (Package package1 : pacotes)
			for (Usage u : modelHelper.getAllUsage(package1))
				usageClass.add(usageRelationshipBuilder.create(u));

		for (Usage usage : usages)
			usageClass.add(usageRelationshipBuilder.create(usage));

		if (usageClass.isEmpty())
			return Collections.emptyList();
		return usageClass;
	}

	private List<AssociationClassRelationship> loadAssociationClassAssociation() {
		List<AssociationClassRelationship> associationClasses = new ArrayList<AssociationClassRelationship>();
		List<AssociationClass> associationsClass = modelHelper.getAllAssociationsClass(model);

		for (AssociationClass associationClass : associationsClass) {
			associationClasses.add(associationClassRelationshipBuilder.create(associationClass));
		}

		if (associationClasses.isEmpty())
			return Collections.emptyList();
		return associationClasses;
	}

	private List<? extends Relationship> loadRealizations() {
		List<Relationship> relationships = new ArrayList<Relationship>();
		List<Realization> realizations = modelHelper.getAllRealizations(model);

		// Se tivermos uma relação de realization entre um package e uma classe
		// a mesma é carregada como uma dependencia, por isso verificamos aqui
		// se existe algum caso
		// destes dentro de dependencies.
		List<Dependency> depdencies = modelHelper.getAllDependencies(model);

		for (Dependency dependency : depdencies)
			if (dependency instanceof Realization)
				relationships.add(realizationRelationshipBuilder.create((Realization) dependency));

		for (Realization realization : realizations)
			relationships.add(realizationRelationshipBuilder.create(realization));

		if (relationships.isEmpty())
			return Collections.emptyList();
		return relationships;
	}

	private List<? extends Relationship> loadDependencies() {
		List<Relationship> relationships = new ArrayList<Relationship>();
		List<Dependency> dependencies = modelHelper.getAllDependencies(model);

		for (Dependency dependency : dependencies)
			if (!(dependency instanceof Usage) && (!(dependency instanceof Realization)))
				relationships.add(dependencyRelationshipBuilder.create(dependency));

		if (relationships.isEmpty())
			return Collections.emptyList();
		return relationships;
	}

	private List<? extends Relationship> loadGeneralizations() {
		List<Relationship> relationships = new ArrayList<Relationship>();
		List<EList<Generalization>> generalizations = modelHelper.getAllGeneralizations(model);

		for (EList<Generalization> eList : generalizations)
			for (Generalization generalization : eList)
				relationships.add(generalizationRelationshipBuilder.create(generalization));

		if (relationships.isEmpty())
			return Collections.emptyList();
		return relationships;
	}

	private List<Relationship> loadAssociations() {
		List<Relationship> relationships = new ArrayList<Relationship>();
		List<Association> associations = modelHelper.getAllAssociations(model);

		for (Association association : associations)
			relationships.add(associationRelationshipBuilder.create(association));

		if (!relationships.isEmpty())
			return relationships;
		return relationships;
	}

	private List<Variability> loadVariability() throws ModelNotFoundException, ModelIncompleteException,
			SMartyProfileNotAppliedToModelExcepetion, VariationPointElementTypeErrorException {
		List<Variability> variabilities = new ArrayList<Variability>();
		List<org.eclipse.uml2.uml.Class> allClasses = modelHelper.getAllClasses(model);

		for (Classifier classifier : allClasses)
			if (StereotypeHelper.isVariability(classifier))
				variabilityBuilder.create(classifier);

		VariabilityFlyweight.getInstance().createVariants();
		variabilities.addAll(VariabilityFlyweight.getInstance().getVariabilities());

		if (!variabilities.isEmpty())
			return variabilities;
		return Collections.emptyList();
	}

	private List<Class> loadClasses() {
		List<Class> listOfClasses = new ArrayList<Class>();
		List<org.eclipse.uml2.uml.Class> classes = modelHelper.getClasses(model);

		for (NamedElement element : classes)
			if (!ModelElementHelper.isInterface(element))
				listOfClasses.add(classBuilder.create(element));

		if (!listOfClasses.isEmpty())
			return listOfClasses;
		return listOfClasses;
	}

	private List<Interface> loadInterfaces() {
		List<Interface> listOfInterfaces = new ArrayList<Interface>();
		List<org.eclipse.uml2.uml.Class> classes = modelHelper.getClasses(model);

		for (org.eclipse.uml2.uml.Class class1 : classes)
			if (ModelElementHelper.isInterface((NamedElement) class1))
				listOfInterfaces.add(intefaceBuilder.create(class1));

		return listOfInterfaces;
	}

	/**
	 * Retornar todos os pacotes
	 * 
	 * @return {@link Collection<mestrado.arquitetura.representation.Package>}
	 */
	private List<arquitetura.representation.Package> loadPackages() {
		List<arquitetura.representation.Package> packages = new ArrayList<arquitetura.representation.Package>();
		List<Package> packagess = modelHelper.getAllPackages(model);

		for (NamedElement pkg : packagess)
			packages.add(packageBuilder.create(pkg));

		if (!packages.isEmpty())
			return packages;
		return packages;
	}

	/**
	 * Inicializa os elementos da arquitetura. Instanciando as classes builders
	 * juntamente com suas depedências.
	 * 
	 * @param architecture
	 * @throws ModelIncompleteException
	 * @throws ModelNotFoundException
	 */
	private void initialize(Architecture architecture) throws ModelNotFoundException, ModelIncompleteException {
		classBuilder = new ClassBuilder(architecture);
		intefaceBuilder = new InterfaceBuilder(architecture);
		packageBuilder = new PackageBuilder(architecture, classBuilder, intefaceBuilder);
		variabilityBuilder = new VariabilityBuilder(architecture);

		associationRelationshipBuilder = new AssociationRelationshipBuilder(architecture);
		generalizationRelationshipBuilder = new GeneralizationRelationshipBuilder(architecture);
		dependencyRelationshipBuilder = new DependencyRelationshipBuilder(architecture);
		realizationRelationshipBuilder = new RealizationRelationshipBuilder(architecture);
		abstractionRelationshipBuilder = new AbstractionRelationshipBuilder(architecture);
		associationClassRelationshipBuilder = new AssociationClassRelationshipBuilder(architecture);
		usageRelationshipBuilder = new UsageRelationshipBuilder(architecture);

		VariabilityFlyweight.getInstance().resetVariabilities();
		VariationPointFlyweight.getInstance().resertVariationPoints();
		VariantFlyweight.getInstance().resetVariants();

	}

	public Package getModel() {
		return this.model;
	}

}