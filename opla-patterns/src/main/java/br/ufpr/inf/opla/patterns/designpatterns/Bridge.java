package br.ufpr.inf.opla.patterns.designpatterns;

import arquitetura.representation.Concern;
import arquitetura.representation.Element;
import arquitetura.representation.Interface;
import br.ufpr.inf.opla.patterns.comparators.SubElementsComparator;
import br.ufpr.inf.opla.patterns.models.AlgorithmFamily;
import br.ufpr.inf.opla.patterns.models.Scope;
import br.ufpr.inf.opla.patterns.models.ps.PS;
import br.ufpr.inf.opla.patterns.models.ps.impl.PSBridge;
import br.ufpr.inf.opla.patterns.models.ps.impl.PSPLABridge;
import br.ufpr.inf.opla.patterns.models.ps.impl.PSStrategy;
import br.ufpr.inf.opla.patterns.util.BridgeUtil;
import br.ufpr.inf.opla.patterns.util.ElementUtil;
import br.ufpr.inf.opla.patterns.util.StrategyUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Bridge extends DesignPattern {

	private static volatile Bridge INSTANCE;

	public static synchronized Bridge getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Bridge();
		}
		return INSTANCE;
	}

	private Bridge() {
		super("Bridge", "Structural");
	}

	@Override
	public boolean verifyPS(Scope scope) {
		boolean isPS = false;
		//System.out.println("\n Verificação PS Bridge - foi chamada");
		if (Strategy.getInstance().verifyPS(scope)) {
			//System.out.println("\n Verificação PS Bridge - entrou");
			List<PS> psStrategyList = scope.getPSs(Strategy.getInstance());
			for (Iterator<PS> it = psStrategyList.iterator(); it.hasNext();) {
				PSStrategy psStrategy = (PSStrategy) it.next();
				Set<Concern> commonConcerns = ElementUtil.getOwnAndMethodsCommonConcernsOfAtLeastTwoElements(
						psStrategy.getAlgorithmFamily().getParticipants());
				if (!commonConcerns.isEmpty()) {
					PSBridge psBridge = new PSBridge(psStrategy.getContexts(), psStrategy.getAlgorithmFamily(),
							new ArrayList<>(commonConcerns));
					if (!scope.getPSs(this).contains(psBridge)) {
						scope.addPS(psBridge);
					}
					isPS = true;
				}
			}
		}
		//System.out.println("\n PS Bridge: " + isPS);
		return isPS;
	}

	@Override
	public boolean verifyPSPLA(Scope scope) {
		//System.out.println("\n Verificação PSPLA Bridge - foi chamada");
		boolean psPLA = false;
		if (verifyPS(scope)) {
			//System.out.println("\n Verificação PSPLA Bridge - entrou");
			for (PS ps : scope.getPSs(this)) {
				PSBridge psBridge = (PSBridge) ps;
				List<Element> contexts = psBridge.getContexts();
				AlgorithmFamily algorithmFamily = psBridge.getAlgorithmFamily();
				if (StrategyUtil.areTheAlgorithmFamilyAndContextsPartOfAVariability(algorithmFamily, contexts)) {
					PSPLABridge psPlaBridge = new PSPLABridge(contexts, algorithmFamily, psBridge.getCommonConcerns());
					if (!scope.getPSsPLA(this).contains(psPlaBridge)) {
						scope.addPSPLA(psPlaBridge);
						psPLA = true;
					}
				}
			}
		}
		//System.out.println("\n PSPLA Bridge: " + psPLA);
		return psPLA;
	}

	@Override
	public boolean apply(Scope scope) {
		boolean applied = false;
		List<PS> pSs = scope.getPSs(this);
		//System.out.println("\n Aplicacao do bridge - foi chamada");
		
		if (!pSs.isEmpty()) {
			//System.out.println("\n Aplicacao do bridge - entrou");
			
			PSBridge psBridge = (PSBridge) pSs.get(0);
			AlgorithmFamily algorithmFamily = psBridge.getAlgorithmFamily();
			List<Element> participants = algorithmFamily.getParticipants();

			// Get or create Abstraction Classes
			List<Element> abstractionClasses = BridgeUtil.getAbstractionClasses(algorithmFamily);
			if (abstractionClasses.isEmpty()) {
				abstractionClasses = BridgeUtil.createAbstractionClasses(algorithmFamily);
			}
			participants.removeAll(abstractionClasses);

			if (!participants.isEmpty()) {
				// Select the abstract abtraction class
				Collections.sort(abstractionClasses, SubElementsComparator.getDescendingOrderer());
				arquitetura.representation.Class abstractClass = (arquitetura.representation.Class) abstractionClasses
						.get(0);

				// Get or create Implementation Interfaces
				HashMap<Concern, List<Element>> groupedElements = ElementUtil.groupElementsByConcern(participants);
				HashMap<Concern, List<Interface>> potentialImplementationInterfaces = BridgeUtil
						.getImplementationInterfaces(algorithmFamily);
				HashMap<Concern, Interface> implementationInterfaces = new HashMap<>();
				for (Map.Entry<Concern, List<Interface>> entry : potentialImplementationInterfaces.entrySet()) {
					Concern concern = entry.getKey();
					List<Interface> interfaceList = entry.getValue();
					List<Element> elementList = groupedElements.get(concern);

					Interface concernInterface;
					if (interfaceList.isEmpty()) {
						concernInterface = BridgeUtil.createImplementationInterface(concern, elementList);
					} else {
						Collections.sort(interfaceList, SubElementsComparator.getDescendingOrderer());
						concernInterface = interfaceList.get(0);
					}
					elementList.remove(concernInterface);
					participants.remove(concernInterface);
					implementationInterfaces.put(concern, concernInterface);
				}

				List<Element> adapterList = new ArrayList<>();
				List<Element> adapteeList = new ArrayList<>();
				for (Map.Entry<Concern, Interface> entry : implementationInterfaces.entrySet()) {
					Concern concern = entry.getKey();
					Interface concernInterface = entry.getValue();
					List<Element> elementList = groupedElements.get(concern);

					// Aggregate Abstraction <>------> Implementation
					BridgeUtil.aggregateAbstractionWithImplementation(abstractClass, concernInterface);

					List<Element> tempAdapterList = new ArrayList<>();
					List<Element> tempAdapteeList = new ArrayList<>();

					// Implement
					ElementUtil.implementInterface(elementList, concernInterface, tempAdapterList, tempAdapteeList);

					elementList.removeAll(tempAdapteeList);
					elementList.addAll(tempAdapterList);

					adapteeList.addAll(tempAdapteeList);
					adapterList.addAll(tempAdapterList);
				}

				// Move context relationships
				List<Element> contexts = psBridge.getContexts();
				StrategyUtil.moveContextsRelationshipWithSameTypeAndName(contexts, participants, abstractClass);

				// Varaibilities
				StrategyUtil.moveVariabilitiesFromContextsToTarget(contexts, participants, abstractClass);

				participants.removeAll(adapteeList);
				participants.addAll(adapterList);

				addStereotype(abstractionClasses);
				addStereotype(implementationInterfaces.values());
				addStereotype(participants);
				addStereotype(adapteeList);
			}
			applied = true;
		}
		//System.out.println("\n Aplicação do Bridge? =" + applied);
		return applied;
	}

}
