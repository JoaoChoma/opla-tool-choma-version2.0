package jmetal.problems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.encodings.solutionType.ArchitectureSolutionType;
import jmetal.experiments.ExperimentCommomConfigs;
import jmetal.metrics.PLAMetrics.extensibility.ExtensPLA;
import jmetal.metrics.concernDrivenMetrics.concernCohesion.LCC;
import jmetal.metrics.concernDrivenMetrics.concernCohesion.LCCClass;
import jmetal.metrics.concernDrivenMetrics.concernCohesion.LCCClassComponentResult;
import jmetal.metrics.concernDrivenMetrics.concernCohesion.LCCComponentResult;
import jmetal.metrics.concernDrivenMetrics.concernDiffusion.CDAC;
import jmetal.metrics.concernDrivenMetrics.concernDiffusion.CDACResult;
import jmetal.metrics.concernDrivenMetrics.concernDiffusion.CDAClass;
import jmetal.metrics.concernDrivenMetrics.concernDiffusion.CDAClassResult;
import jmetal.metrics.concernDrivenMetrics.concernDiffusion.CDAI;
import jmetal.metrics.concernDrivenMetrics.concernDiffusion.CDAIResult;
import jmetal.metrics.concernDrivenMetrics.concernDiffusion.CDAO;
import jmetal.metrics.concernDrivenMetrics.concernDiffusion.CDAOResult;
import jmetal.metrics.concernDrivenMetrics.interactionBeteweenConcerns.CIBC;
import jmetal.metrics.concernDrivenMetrics.interactionBeteweenConcerns.CIBCResult;
import jmetal.metrics.concernDrivenMetrics.interactionBeteweenConcerns.CIBClass;
import jmetal.metrics.concernDrivenMetrics.interactionBeteweenConcerns.CIBClassResult;
import jmetal.metrics.concernDrivenMetrics.interactionBeteweenConcerns.IIBC;
import jmetal.metrics.concernDrivenMetrics.interactionBeteweenConcerns.IIBCResult;
import jmetal.metrics.concernDrivenMetrics.interactionBeteweenConcerns.OOBC;
import jmetal.metrics.concernDrivenMetrics.interactionBeteweenConcerns.OOBCResult;
import jmetal.metrics.conventionalMetrics.ATMRElegance;
import jmetal.metrics.conventionalMetrics.ClassDependencyIn;
import jmetal.metrics.conventionalMetrics.ClassDependencyOut;
import jmetal.metrics.conventionalMetrics.DependencyIn;
import jmetal.metrics.conventionalMetrics.DependencyOut;
import jmetal.metrics.conventionalMetrics.ECElegance;
import jmetal.metrics.conventionalMetrics.MeanDepComponents;
import jmetal.metrics.conventionalMetrics.MeanNumOpsByInterface;
import jmetal.metrics.conventionalMetrics.NACElegance;
import jmetal.metrics.conventionalMetrics.RelationalCohesion;
import jmetal.metrics.newPlasMetrics.AV;
import jmetal.metrics.newPlasMetrics.CBCS;
import jmetal.metrics.newPlasMetrics.SSC;
import jmetal.metrics.newPlasMetrics.SVC;
import jmetal.metrics.newPlasMetrics.WocsClass;
import jmetal.metrics.newPlasMetrics.WocsInterface;
import jmetal.util.JMException;
import arquitetura.builders.ArchitectureBuilder;
import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Interface;
import arquitetura.representation.Package;
import arquitetura.representation.relationship.AbstractionRelationship;
import arquitetura.representation.relationship.AssociationEnd;
import arquitetura.representation.relationship.AssociationRelationship;
import arquitetura.representation.relationship.DependencyRelationship;
import arquitetura.representation.relationship.GeneralizationRelationship;
import arquitetura.representation.relationship.Relationship;

//criado por Thelma em agosto/2012
public class OPLA extends Problem {

	private static final long serialVersionUID = 884633138619836573L;

	// variaveis para controlar os contadores de componentes e interfaces
	public static int contComp_ = 0;
	public static int contInt_ = 0;
	public static int contClass_ = 0;
	public static int contDiscardedSolutions_ = 0;

	public Architecture architecture_;
	private List<String> selectedMetrics; // Vai vir da GUI
	private ExperimentCommomConfigs configs;

	public OPLA(String xmiFilePath, ExperimentCommomConfigs oplaConfig) throws Exception {
		this.configs = oplaConfig;
		numberOfVariables_ = 1;
		numberOfObjectives_ = oplaConfig.getOplaConfigs().getNumberOfObjectives();
		numberOfConstraints_ = 0;
		problemName_ = "OPLA";
		solutionType_ = new ArchitectureSolutionType(this);
		variableType_ = new java.lang.Class[numberOfVariables_];
		length_ = new int[numberOfVariables_];
		variableType_[0] = java.lang.Class.forName(Architecture.ARCHITECTURE_TYPE);
		architecture_ = new ArchitectureBuilder().create(xmiFilePath);

		selectedMetrics = oplaConfig.getOplaConfigs().getSelectedObjectiveFunctions();
	}

	@Override
	public void evaluate(Solution solution) {
		
		
		
		
		
		List<jmetal.experiments.Fitness> fitnesses = new ArrayList<jmetal.experiments.Fitness>();
//		CapturarValoresFitness capturar = new CapturarValoresFitness();
		//System.out.println(this.selectedMetrics);
		for (int i = 0; i < this.selectedMetrics.size(); i++) {
			String metric = this.selectedMetrics.get(i);

			switch (metric) {
			case "elegance":
				jmetal.experiments.Fitness e = new jmetal.experiments.Fitness(
						evaluateElegance((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(e);
				//System.out.println("valor do elegance: " + e.getValue());
				break;

			case "conventional":
				jmetal.experiments.Fitness c = new jmetal.experiments.Fitness(
						evaluateMACFitness((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(c);
				//System.out.println("valor do conventional: " + c.getValue());
				break;

			case "featureDriven":
				jmetal.experiments.Fitness f = new jmetal.experiments.Fitness(
						evaluateMSIFitness((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(f);
				//System.out.println("valor do featureDriven: " + f.getValue());
				break;

			case "PLAExtensibility":
				jmetal.experiments.Fitness p = new jmetal.experiments.Fitness(
						evaluatePLAExtensibility((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(p);
				//System.out.println("valor do PLAExtensibility: " + p.getValue());
				break;

			case "acomp":
				jmetal.experiments.Fitness a = new jmetal.experiments.Fitness(
						evaluateACOMP((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(a);
				//System.out.println("valor do acomp: " + a.getValue());
				break;

			case "aclass":
				jmetal.experiments.Fitness ac = new jmetal.experiments.Fitness(
						evaluateACLASS((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(ac);
				//System.out.println("valor do aclass: " + ac.getValue());
				break;

			case "tam":
				jmetal.experiments.Fitness t = new jmetal.experiments.Fitness(
						evaluateTAM((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(t);
				//System.out.println("valor do tam: " + t.getValue());
				break;

			case "coe":
				jmetal.experiments.Fitness co = new jmetal.experiments.Fitness(
						evaluateCohesionFitness((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(co);
				//System.out.println("valor do coe: " + co.getValue());
				break;

			case "dc":
				jmetal.experiments.Fitness dc = new jmetal.experiments.Fitness(
						evaluateDC((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(dc);
				//System.out.println("valor do dc: " + dc.getValue());
				break;

			case "ec":
				jmetal.experiments.Fitness ec = new jmetal.experiments.Fitness(
						evaluateEC((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(ec);
				//System.out.println("valor do ec: " + ec.getValue());
				break;
			//addYni
			case "wocsclass":
				jmetal.experiments.Fitness wocsc = new jmetal.experiments.Fitness(
						evaluateWocsC((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(wocsc);
				//System.out.println("valor do wocsclass: " + wocsc.getValue());
				break;	
			
			case "wocsinterface":
				jmetal.experiments.Fitness wocsi = new jmetal.experiments.Fitness(
						evaluateWocsI((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(wocsi);
				//System.out.println("valor do wocsinterface: " + wocsi.getValue());
				break;	
				
			case "cbcs":
				jmetal.experiments.Fitness cbcs = new jmetal.experiments.Fitness(
						evaluateCbcs((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(cbcs);
				//System.out.println("valor do cbcs: " + cbcs.getValue());
				break;	
				
			case "svc":
				jmetal.experiments.Fitness svc = new jmetal.experiments.Fitness(
						evaluateSvc((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(svc);
				//System.out.println("valor do svc: " + svc.getValue());
				break;	
				
			case "ssc":
				jmetal.experiments.Fitness ssc = new jmetal.experiments.Fitness(
						evaluateSsc((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(ssc);
				//System.out.println("valor do ssc: " + ssc.getValue());
				break;	
				
			case "av":
				jmetal.experiments.Fitness av = new jmetal.experiments.Fitness(
						evaluateAv((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(av);
				//System.out.println("valor do av: " + av.getValue());
				break;		
			//addYni	
				
			case "lcc":
				jmetal.experiments.Fitness lcc = new jmetal.experiments.Fitness(
						evaluateLCC((Architecture) solution.getDecisionVariables()[0]));
				fitnesses.add(lcc);
				//System.out.println("valor do lcc: " + lcc.getValue());
				break;
				
			/*
			 * switch (metric) { case "elegance": fitnesses.add(new
			 * jmetal.experiments.Fitness(evaluateElegance((Architecture)
			 * solution .getDecisionVariables()[0]))); break;
			 * 
			 * 
			 * case "conventional": fitnesses.add(new
			 * jmetal.experiments.Fitness(evaluateMACFitness((Architecture)
			 * solution .getDecisionVariables()[0]))); break;
			 * 
			 * case "featureDriven": fitnesses.add(new
			 * jmetal.experiments.Fitness(evaluateMSIFitness((Architecture)
			 * solution .getDecisionVariables()[0]))); break;
			 * 
			 * case "PLAExtensibility": fitnesses.add(new
			 * jmetal.experiments.Fitness(evaluatePLAExtensibility((
			 * Architecture) solution .getDecisionVariables()[0]))); break;
			 * 
			 * //implementado por marcelo case "acomp": fitnesses.add(new
			 * jmetal.experiments.Fitness(evaluateACOMP((Architecture) solution
			 * .getDecisionVariables()[0]))); break;
			 * 
			 * case "aclass": fitnesses.add(new
			 * jmetal.experiments.Fitness(evaluateACLASS((Architecture) solution
			 * .getDecisionVariables()[0]))); break;
			 * 
			 * case "tam": fitnesses.add(new
			 * jmetal.experiments.Fitness(evaluateTAM((Architecture) solution
			 * .getDecisionVariables()[0]))); break;
			 * 
			 * case "coe": fitnesses.add(new
			 * jmetal.experiments.Fitness(evaluateCOE((Architecture) solution
			 * .getDecisionVariables()[0]))); break; case "dc":
			 * fitnesses.add(new
			 * jmetal.experiments.Fitness(evaluateDC((Architecture) solution
			 * .getDecisionVariables()[0]))); break;
			 * 
			 * case "ec": fitnesses.add(new
			 * jmetal.experiments.Fitness(evaluateEC((Architecture) solution
			 * .getDecisionVariables()[0])));
			 */
			default:
			}
		}
		
		for (int i = 0; i < fitnesses.size(); i++) {

			solution.setObjective(i, fitnesses.get(i).getValue());

		}
		
	}

	private double evaluateDepIN(Architecture architecture) {
		ClassDependencyIn depIn = new ClassDependencyIn(architecture);
		return depIn.getResults();
	}

	private double evaluateElegance(Architecture architecture) {
		double EleganceFitness = 0.0;
		ECElegance EC = new ECElegance(architecture);
		ATMRElegance ATMR = new ATMRElegance(architecture);
		NACElegance NAC = new NACElegance(architecture);
		EleganceFitness = EC.getResults() + ATMR.getResults() + NAC.getResults();

		return EleganceFitness;
	}

	private double evaluateMSIFitness(Architecture architecture) {
		double sumCIBC = 0.0;
		double sumIIBC = 0.0;
		double sumOOBC = 0.0;
		double sumCDAC = 0.0;
		double sumCDAI = 0.0;
		double sumCDAO = 0.0;
		double sumLCC = 0.0;
		double MSIFitness = 0.0;
		double sumCDAClass = 0.0;
		double sumCIBClass = 0.0;
		double sumLCCClass = 0.0;

		sumLCC = evaluateLCC(architecture);

		sumLCCClass = evaluateLCCClass(architecture);

		CIBC cibc = new CIBC(architecture);
		for (CIBCResult c : cibc.getResults().values()) {
			sumCIBC += c.getInterlacedConcerns().size();
		}

		CIBClass cibclass = new CIBClass(architecture);
		for (CIBClassResult c : cibclass.getResults().values()) {
			sumCIBClass += c.getInterlacedConcerns().size();
		}

		IIBC iibc = new IIBC(architecture);
		for (IIBCResult c : iibc.getResults().values()) {
			sumIIBC += c.getInterlacedConcerns().size();
		}

		OOBC oobc = new OOBC(architecture);
		for (OOBCResult c : oobc.getResults().values()) {
			sumOOBC += c.getInterlacedConcerns().size();
		}

		CDAC cdac = new CDAC(architecture);
		for (CDACResult c : cdac.getResults()) {
			sumCDAC += c.getElements().size();
		}

		CDAClass cdaclass = new CDAClass(architecture);
		for (CDAClassResult c : cdaclass.getResults()) {
			sumCDAClass += c.getElements().size();
		}

		CDAI cdai = new CDAI(architecture);
		for (CDAIResult c : cdai.getResults()) {
			sumCDAI += c.getElements().size();
		}

		CDAO cdao = new CDAO(architecture);
		for (CDAOResult c : cdao.getResults()) {
			sumCDAO += c.getElements().size();
		}

		MSIFitness = sumLCC + sumLCCClass + sumCDAC + sumCDAClass + sumCDAI + sumCDAO + sumCIBC + sumCIBClass + sumIIBC
				+ sumOOBC;
		return MSIFitness;
	}

	private double evaluateLCC(Architecture architecture) {
		double sumLCC = 0.0;
		LCC result = new LCC(architecture);

		for (LCCComponentResult component : result.getResults()) {
			sumLCC += component.numberOfConcerns();

		}
		return sumLCC;
	}

	// ----------------------------------------------------------------------------------
	private double evaluateMACFitness(Architecture architecture) {
		double MACFitness = 0.0;
		double meanNumOps = 0.0;
		double meanDepComps = 0.0;
		double sumCohesion = 0.0;
		double sumClassesDepIn = 0.0;
		double sumClassesDepOut = 0.0;
		double sumDepIn = 0.0;
		double sumDepOut = 0.0;
		double iCohesion = 0.0;

		MeanNumOpsByInterface numOps = new MeanNumOpsByInterface(architecture);
		meanNumOps = numOps.getResults();

		MeanDepComponents depComps = new MeanDepComponents(architecture);
		meanDepComps = depComps.getResults();

		ClassDependencyOut classesDepOut = new ClassDependencyOut(architecture);
		sumClassesDepOut = classesDepOut.getResults();

		ClassDependencyIn classesDepIn = new ClassDependencyIn(architecture);
		sumClassesDepIn = classesDepIn.getResults();

		DependencyOut DepOut = new DependencyOut(architecture);
		sumDepOut = DepOut.getResults();

		DependencyIn DepIn = new DependencyIn(architecture);
		sumDepIn = DepIn.getResults();

		RelationalCohesion cohesion = new RelationalCohesion(architecture);
		sumCohesion = cohesion.getResults();
		if (sumCohesion == 0) {
			iCohesion = 1.0;
		} else
			iCohesion = 1 / sumCohesion;

		// System.out.println("MeanNumOps: "+meanNumOps);
		// System.out.println("meanDepComps: "+meanDepComps);
		// System.out.println("sumClassesDepIn: "+sumClassesDepIn);
		// System.out.println("sumClassesDepOut: "+sumClassesDepOut);
		// System.out.println("sumDepIn: "+sumDepIn);
		// System.out.println("sumDepOut: "+sumDepOut);
		// System.out.println("sumCohesion: "+iCohesion);
		//

		// MACFitness = meanNumOps + meanDepComps + sumClassesDepIn +
		// sumClassesDepOut + sumDepIn + sumDepOut + (1 / sumCohesion);
		// Design Outset
		MACFitness = sumClassesDepIn + sumClassesDepOut + sumDepIn + sumDepOut + iCohesion;

		return MACFitness;
	}

	// ---------------------------------------------------------------------------------
	private double evaluateCohesionFitness(Architecture architecture) {
		double sumCohesion = 0.0;
		double Cohesion = 0.0;

		RelationalCohesion cohesion = new RelationalCohesion(architecture);
		sumCohesion = cohesion.getResults();
		Cohesion = (1 / sumCohesion);
		return Cohesion;
	}

	// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
	public SolutionSet removeDominadas(SolutionSet result) {
		boolean dominador, dominado;
		double valor1 = 0;
		double valor2 = 0;

		for (int i = 0; i < (result.size() - 1); i++) {
			for (int j = (i + 1); j < result.size(); j++) {
				dominador = true;
				dominado = true;

				for (int k = 0; k < result.get(i).numberOfObjectives(); k++) {
					valor1 = result.get(i).getObjective(k);
					valor2 = result.get(j).getObjective(k);

					if (valor1 > valor2 || dominador == false) {
						dominador = false;
					} else if (valor1 <= valor2) {
						dominador = true;
					}

					if (valor2 > valor1 || dominado == false) {
						dominado = false;
					} else if (valor2 < valor1) {
						dominado = true;
					}
				}

				if (dominador) {
					// System.out.println("--------------------------------------------");
					// System.out.println("Solucao [" + i +
					// "] domina a Solucao [" + j + "]");
					// System.out.println("[" + i + "] " +
					// result.get(i).toString());
					// System.out.println("[" + j + "] " +
					// result.get(j).toString());

					result.remove(j);
					this.configs.getLogger().putLog("removido Dominada");
					j = j - 1;
				} else if (dominado) {
					// System.out.println("--------------------------------------------");
					// System.out.println("Solucao [" + j +
					// "] domina a Solucao [" + i + "]");
					// System.out.println("[" + i + "] " +
					// result.get(i).toString());
					// System.out.println("[" + j + "] " +
					// result.get(j).toString());

					result.remove(i);
					this.configs.getLogger().putLog("removido Dominada");
					j = i;
				}
			}
		}

		return result;
	}

	// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
	public SolutionSet removeRepetidas(SolutionSet result) {
		String solucao;

		for (int i = 0; i < result.size() - 1; i++) {
			solucao = result.get(i).getDecisionVariables()[0].toString();
			for (int j = i + 1; j < result.size(); j++) {
				if (solucao.equals(result.get(j).getDecisionVariables()[0].toString())) {
					result.remove(j);
					this.configs.getLogger().putLog("removido Repedita");
				}
			}
		}

		return result;
	}

	// m��todo para verificar se algum dos relacionamentos recebidos ���
	// generaliza������o
	private boolean searchForGeneralizations(Class cls) { // ok
		Collection<Relationship> Relationships = cls.getRelationships();
		for (Relationship relationship : Relationships) {
			if (relationship instanceof GeneralizationRelationship) {
				GeneralizationRelationship generalization = (GeneralizationRelationship) relationship;
				if (generalization.getChild().equals(cls) || generalization.getParent().equals(cls))
					return true;
			}
		}
		return false;
	}

	private double evaluateMSIFitnessDesignOutset(Architecture architecture) {
		double sumCIBC = 0.0;
		double sumIIBC = 0.0;
		double sumOOBC = 0.0;
		double sumCDAC = 0.0;
		double sumCDAI = 0.0;
		double sumCDAO = 0.0;
		double sumLCC = 0.0;
		double MSIFitness = 0.0;
		double sumCDAClass = 0.0;
		double sumCIBClass = 0.0;
		double sumLCCClass = 0.0;

		sumLCC = evaluateLCC(architecture);

		sumLCCClass = evaluateLCCClass(architecture);

		CIBC cibc = new CIBC(architecture);
		for (CIBCResult c : cibc.getResults().values()) {
			sumCIBC += c.getInterlacedConcerns().size();
		}

		CIBClass cibclass = new CIBClass(architecture);
		for (CIBClassResult c : cibclass.getResults().values()) {
			sumCIBClass += c.getInterlacedConcerns().size();
		}

		IIBC iibc = new IIBC(architecture);
		for (IIBCResult c : iibc.getResults().values()) {
			sumIIBC += c.getInterlacedConcerns().size();
		}

		OOBC oobc = new OOBC(architecture);
		for (OOBCResult c : oobc.getResults().values()) {
			sumOOBC += c.getInterlacedConcerns().size();
		}

		CDAC cdac = new CDAC(architecture);
		for (CDACResult c : cdac.getResults()) {
			sumCDAC += c.getElements().size();
		}

		CDAClass cdaclass = new CDAClass(architecture);
		for (CDAClassResult c : cdaclass.getResults()) {
			sumCDAClass += c.getElements().size();
		}

		CDAI cdai = new CDAI(architecture);
		for (CDAIResult c : cdai.getResults()) {
			sumCDAI += c.getElements().size();
		}

		CDAO cdao = new CDAO(architecture);
		for (CDAOResult c : cdao.getResults()) {
			sumCDAO += c.getElements().size();
		}

		MSIFitness = sumLCC + sumLCCClass + sumCDAC + sumCDAClass + sumCDAI + sumCDAO + sumCIBC + sumCIBClass + sumIIBC
				+ sumOOBC;
		return MSIFitness;
	}

	private double evaluateLCCClass(Architecture architecture) {
		double sumLCCClass = 0.0;
		LCCClass result = new LCCClass(architecture);

		for (LCCClassComponentResult cls : result.getResults()) {
			sumLCCClass += cls.numberOfConcerns();

		}
		return sumLCCClass;
	}

	private float evaluatePLAExtensibility(Architecture architecture) {
		float ExtensibilityFitness = 0;
		float Extensibility;
		ExtensPLA PLAExtens = new ExtensPLA(architecture);
		ExtensibilityFitness = PLAExtens.getValue();
		if (ExtensibilityFitness == 0)
			Extensibility = 1000;
		else
			Extensibility = 1 / ExtensibilityFitness;
		return (Extensibility);
	}

	private void removeComponentRelationships(Package comp, Architecture architecture) {
		//
		Relationship[] allInterElementRelationships = architecture.getRelationshipHolder().getAllRelationships()
				.toArray(new Relationship[0]);
		for (Relationship relationship : allInterElementRelationships) {
			if (relationship instanceof AbstractionRelationship) {
				AbstractionRelationship abstraction = (AbstractionRelationship) relationship;
				if (abstraction.getClient().equals(comp)) {
					architecture.removeRelationship(relationship);
				}
			}
			if (relationship instanceof DependencyRelationship) {
				DependencyRelationship dependency = (DependencyRelationship) relationship;
				if (dependency.getClient().equals(comp)) {
					architecture.removeRelationship(relationship);
				}
			}
		}
	}

	private void removeClassRelationships(Class cls, Architecture architecture) {
		List<Relationship> relationshipsCls = new ArrayList<Relationship>(cls.getRelationships());
		if (!relationshipsCls.isEmpty()) {
			Iterator<Relationship> iteratorRelationships = relationshipsCls.iterator();
			while (iteratorRelationships.hasNext()) {
				Relationship relationship = iteratorRelationships.next();

				if (relationship instanceof DependencyRelationship) {
					DependencyRelationship dependency = (DependencyRelationship) relationship;
					if (dependency.getClient().equals(cls) || dependency.getSupplier().equals(cls))
						architecture.removeRelationship(dependency);
				}

				if (relationship instanceof AssociationRelationship) {
					AssociationRelationship association = (AssociationRelationship) relationship;
					for (AssociationEnd associationEnd : association.getParticipants()) {
						if (associationEnd.getCLSClass().equals(cls)) {
							architecture.removeRelationship(association);
							break;
						}
					}
				}

				if (relationship instanceof GeneralizationRelationship) {
					GeneralizationRelationship generalization = (GeneralizationRelationship) relationship;
					if ((generalization.getChild().equals(cls)) || (generalization.getParent().equals(cls))) {
						architecture.removeRelationship(generalization);

					}
				}

			}
		}
	}

	public void evaluateConstraints(Solution solution) throws JMException {
		List<Package> allComponents = new ArrayList<Package>(
				((Architecture) solution.getDecisionVariables()[0]).getAllPackages());
		for (Package comp : allComponents) {
			List<Class> allClasses = new ArrayList<Class>(comp.getAllClasses());
			if (!(allClasses.isEmpty())) {
				Iterator<Class> iteratorClasses = allClasses.iterator();

				while (iteratorClasses.hasNext()) {
					Class cls = iteratorClasses.next();
					if ((cls.getAllAttributes().isEmpty()) && (cls.getAllMethods().isEmpty())
							&& (cls.getImplementedInterfaces().isEmpty()) && !(searchForGeneralizations(cls))
							&& (cls.getVariantType() == null)) {
						comp.removeClass(cls);
						this.removeClassRelationships(cls, (Architecture) solution.getDecisionVariables()[0]);
					}
				}
			}

			List<Interface> allItfsComp = new ArrayList<Interface>(comp.getImplementedInterfaces());
			if (!(allItfsComp.isEmpty())) {
				Iterator<Interface> iteratorInterfaces = allItfsComp.iterator();
				while (iteratorInterfaces.hasNext()) {
					Interface itf = iteratorInterfaces.next();
					boolean ultimaInterface = false;
					if (comp.getImplementedInterfaces().size() == 1)
						ultimaInterface = true;
					if (itf.getOperations().isEmpty() && !ultimaInterface) {
						((Architecture) solution.getDecisionVariables()[0]).removeInterface(itf);
						// this.removeInterfaceRelationships(itf, (Architecture)
						// solution.getDecisionVariables()[0]);
					}
					if (itf.getOperations().isEmpty() && ultimaInterface && comp.getAllClasses().size() < 1) {
						((Architecture) solution.getDecisionVariables()[0]).removeInterface(itf);
					}
				}
			}
			allItfsComp.clear();

			Iterator<Interface> iteratorInterfaces = ((Architecture) solution.getDecisionVariables()[0])
					.getAllInterfaces().iterator();
			while (iteratorInterfaces.hasNext()) {
				Interface itf = iteratorInterfaces.next();
				boolean ultimaInterface = false;
				if (comp.getImplementedInterfaces().size() == 1)
					ultimaInterface = true;
				if (itf.getOperations().isEmpty() && !ultimaInterface) {
					((Architecture) solution.getDecisionVariables()[0]).removeInterface(itf);
					// this.removeInterfaceRelationships(itf, (Architecture)
					// solution.getDecisionVariables()[0]);
				}
				if (itf.getOperations().isEmpty() && ultimaInterface && comp.getAllClasses().size() < 1) {
					((Architecture) solution.getDecisionVariables()[0]).removeInterface(itf);
				}
			}

			if (comp.getAllClasses().isEmpty() && comp.getImplementedInterfaces().isEmpty()
					&& comp.getAllInterfaces().isEmpty()) {
				this.removeComponentRelationships(comp, (Architecture) solution.getDecisionVariables()[0]);
				((Architecture) solution.getDecisionVariables()[0]).removePackage(comp);
			}
		}
		allComponents.clear();

	}
	
	//addYni
		public float evaluateWocsC(Architecture architecture) {
			float wocscFitness = 0;
			WocsClass wocsc = new WocsClass(architecture);
			wocscFitness = wocsc.getResults();
			return wocscFitness;
		}
		
		public float evaluateWocsI(Architecture architecture) {
			float wocsiFitness = 0;
			WocsInterface wocsi = new WocsInterface(architecture);
			wocsiFitness = wocsi.getResults();
			return wocsiFitness;
		}
		
		public float evaluateCbcs(Architecture architecture) {
			float cbcsFitness = 0;
			CBCS cbcs = new CBCS(architecture);
			cbcsFitness = cbcs.getResults();
			return cbcsFitness;
		}
		
		
		public float evaluateSvc(Architecture architecture) {
			float svcFitness = 0;
			SVC svc = new SVC(architecture);
			svcFitness = svc.getResults();
			return svcFitness;
		}
		
		public float evaluateSsc(Architecture architecture) {
			float sscFitness = 0;
			SSC ssc = new SSC(architecture);
			sscFitness = ssc.getResults();
			return sscFitness;
		}
		
		public float evaluateAv(Architecture architecture) {
			float avFitness = 0;
			AV av = new AV(architecture);
			avFitness = av.getResults();
			return avFitness;
		}
		
		
		
		
	//------------------------------------------
	

	// implementado por marcelo
	public double evaluateACOMP(Architecture architecture) {
		double acompFitness = 0.0;
		DependencyIn depIN = new DependencyIn(architecture);
		DependencyOut depOUT = new DependencyOut(architecture);
		acompFitness = depIN.getResults() + depOUT.getResults();
		return acompFitness;
	}

	public double evaluateACLASS(Architecture architecture) {
		double aclassFitness = 0.0;
		ClassDependencyIn CDepIN = new ClassDependencyIn(architecture);
		ClassDependencyOut CDepOUT = new ClassDependencyOut(architecture);
		aclassFitness = CDepIN.getResults() + CDepOUT.getResults();
		return aclassFitness;
	}

	public double evaluateTAM(Architecture architecture) {
		double tamFitness = 0.0;
		MeanNumOpsByInterface NumOps = new MeanNumOpsByInterface(architecture);

		tamFitness = NumOps.getResults();
		return tamFitness;
	}

	
//	public double evaluateCOE(Architecture architecture) {
//		double coeFitness = 0.0;
//		double sumLCC = 0.0;
//RelationalCohesion rc = new RelationalCohesion(architecture);
//		LCC lcc = new LCC(architecture);
//		for (LCCComponentResult c : lcc.getResults()) {
//			sumLCC += c.numberOfConcerns();
//		}
//		coeFitness = sumLCC;
//				//rc.getResults(); //+ sumLCC;
//		//return sumLCC;
//		return coeFitness;
//	}

	public double evaluateDC(Architecture architecture) {
		double dcFitness = 0.0;
		double sumCDAC = 0.0;
		double sumCDAI = 0.0;
		double sumCDAO = 0.0;

		CDAI cdai = new CDAI(architecture);
		for (CDAIResult c : cdai.getResults()) {
			sumCDAI += c.getElements().size();
		}

		CDAO cdao = new CDAO(architecture);
		for (CDAOResult c : cdao.getResults()) {
			sumCDAO += c.getElements().size();
		}

		CDAC cdac = new CDAC(architecture);
		for (CDACResult c : cdac.getResults()) {
			sumCDAC += c.getElements().size();
		}

		dcFitness = sumCDAI + sumCDAO + sumCDAC;
		return dcFitness;
	}

	public double evaluateEC(Architecture architecture) {
		double ecFitness = 0.0;
		double sumCIBC = 0.0;
		double sumIIBC = 0.0;
		double sumOOBC = 0.0;

		CIBC cibc = new CIBC(architecture);
		for (CIBCResult c : cibc.getResults().values()) {
			sumCIBC += c.getInterlacedConcerns().size();
		}

		IIBC iibc = new IIBC(architecture);
		for (IIBCResult c : iibc.getResults().values()) {
			sumIIBC += c.getInterlacedConcerns().size();
		}

		OOBC oobc = new OOBC(architecture);
		for (OOBCResult c : oobc.getResults().values()) {
			sumOOBC += c.getInterlacedConcerns().size();
		}

		ecFitness = sumCIBC + sumIIBC + sumOOBC;
		return ecFitness;
	}

	@Override
	public boolean isPatterns(Solution solution) throws JMException {
		// TODO Auto-generated method stub
		Architecture a = (Architecture) solution.getDecisionVariables()[0];
		return a.isAppliedPatterns();
	}
}
