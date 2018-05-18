package br.ufpr.inf.opla.patterns.designpatterns;

import arquitetura.representation.Class;
import arquitetura.representation.Concern;
import arquitetura.representation.Element;
import arquitetura.representation.Interface;
import br.ufpr.inf.opla.patterns.models.Scope;
import br.ufpr.inf.opla.patterns.models.ps.impl.PSMediator;
import br.ufpr.inf.opla.patterns.util.ElementUtil;
import br.ufpr.inf.opla.patterns.util.MediatorUtil;
import br.ufpr.inf.opla.patterns.util.RelationshipUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.CollectionUtils;

public class Mediator extends DesignPattern {

	private static volatile Mediator INSTANCE;

	public static synchronized Mediator getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Mediator();
		}
		return INSTANCE;
	}

	private Mediator() {
		super("Mediator", "Behavioral");
	}

	@Override
	public boolean verifyPS(Scope scope) {
		boolean isPs = false;
		//System.out.println("\n Verificação PS Mediator - foi chamada");
		
		List<Element> elements = scope.getElements();
		HashMap<Concern, List<Element>> groupedElements = ElementUtil.groupElementsByConcern(elements);
		for (Map.Entry<Concern, List<Element>> entry : groupedElements.entrySet()) {
			Concern concern = entry.getKey();
			List<Element> list = entry.getValue();
			if (concern != null) {
				List<Element> chainOfElements = ElementUtil.getChainOfRelatedElementsWithSameConcern(list, concern);
				int count = 0;
				for (int i = 0; i < chainOfElements.size(); i++) {
					Element iElement = chainOfElements.get(i);
					boolean isTypeOfOtherParticipant = false;
					for (int j = 0; j < chainOfElements.size(); j++) {
						if (i == j) {
							continue;
						}
						Element jElement = chainOfElements.get(j);
						if (ElementUtil.isTypeOf(iElement, jElement)) {
							isTypeOfOtherParticipant = true;
							break;
						}
					}
					if (!isTypeOfOtherParticipant) {
						count++;
						if (count > 2) {
							break;
						}
					}
				}
				if (count > 2) {
					PSMediator psMediator = new PSMediator(chainOfElements, concern);
					if (!scope.getPSs().contains(psMediator)) {
						scope.addPS(psMediator);
					} else {
						psMediator = (PSMediator) scope.getPSs().get(scope.getPSs().indexOf(psMediator));
						psMediator.setParticipants(
								new ArrayList<>(CollectionUtils.union(chainOfElements, psMediator.getParticipants())));
					}
					isPs = true;
				}
			}
		}
		//System.out.println("\n PS Mediator: " + isPs);
		return isPs;

	}

	@Override
	public boolean verifyPSPLA(Scope scope) {
		//System.out.println("\n PS Mediator não testa PSPLA");
		return verifyPS(scope);
	}

	@Override
	public boolean apply(Scope scope) {
		boolean applied = false;
		//System.out.println("\n Aplicação do Mediator - foi chamada");
		
		if (!scope.getPSs(this).isEmpty()) {
			//System.out.println("\n Aplicação do Mediator - entrou");
			try {
				PSMediator psMediator = (PSMediator) scope.getPSs(this).get(0);
				List<Element> participants = psMediator.getParticipants();
				final Concern concern = psMediator.getConcern();

				// Identificar ou criar classe EventOfInterest
				Class eventOfInterest = MediatorUtil.getOrCreateEventOfInterestClass();
				participants.remove(eventOfInterest);

				// "" interface Mediator
				Interface mediatorInterface = MediatorUtil.getOrCreateMediatorInterface(participants, concern,
						eventOfInterest);
				participants.remove(mediatorInterface);

				// "" classe Mediator
				Class mediatorClass = MediatorUtil.getOrCreateMediatorClass(participants, concern, mediatorInterface);
				participants.remove(mediatorClass);

				// "" interfaces colleagues
				Interface colleagueInterface = MediatorUtil.getOrCreateColleagueInterface(participants, concern,
						mediatorInterface, eventOfInterest);
				participants.remove(colleagueInterface);

				// Remover relacionamentos
				MediatorUtil.removeRelationships(participants, concern);

				// Implementar interface Colleague
				List<Element> adapterList = new ArrayList<>();
				List<Element> adapteeList = new ArrayList<>();

				ElementUtil.implementInterface(participants, colleagueInterface, adapterList, adapteeList);

				participants.removeAll(adapteeList);
				for (Element adapterClass : adapterList) {
					if (!participants.contains(adapterClass)) {
						participants.add(adapterClass);
					}
				}

				// Usar colleagues
				for (Element element : participants) {
					RelationshipUtil.createNewUsageRelationship("usesColleague", mediatorClass, element);
				}

				// Aplicar Estereótipo
				addStereotype(eventOfInterest);
				addStereotype(mediatorInterface);
				addStereotype(mediatorClass);
				addStereotype(colleagueInterface);
				addStereotype(participants);
				addStereotype(adapteeList);
				applied = true;
			} catch (Exception ex) {
				Logger.getLogger(Mediator.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		//System.out.println("\n Aplicação do Mediator? =" + applied);
		return applied;
	}

}
