package br.ufpr.inf.opla.patterns.designpatterns;

import arquitetura.exceptions.ConcernNotFoundException;
import arquitetura.representation.Concern;
import arquitetura.representation.Element;
import arquitetura.representation.Interface;
import arquitetura.representation.relationship.Relationship;
import br.ufpr.inf.opla.patterns.models.AlgorithmFamily;
import br.ufpr.inf.opla.patterns.models.Scope;
import br.ufpr.inf.opla.patterns.models.ps.PS;
import br.ufpr.inf.opla.patterns.models.ps.impl.PSPLAStrategy;
import br.ufpr.inf.opla.patterns.models.ps.impl.PSStrategy;
import br.ufpr.inf.opla.patterns.util.AlgorithmFamilyUtil;
import br.ufpr.inf.opla.patterns.util.ElementUtil;
import br.ufpr.inf.opla.patterns.util.RelationshipUtil;
import br.ufpr.inf.opla.patterns.util.StrategyUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.CollectionUtils;

public class Strategy extends DesignPattern {

    private static volatile Strategy INSTANCE;

    public static synchronized Strategy getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Strategy();
        }
        return INSTANCE;
    }

    private Strategy() {
        super("Strategy", "Behavioral");
    }

    @Override
    public boolean verifyPS(Scope scope) {
        List<AlgorithmFamily> familiesInScope = AlgorithmFamilyUtil.getFamiliesFromScope(scope);

        Collections.sort(familiesInScope);
        //System.out.println("\n Verificação PS Strategy - foi chamada");
        
        boolean isPs = false;
        for (int i = familiesInScope.size() - 1; i >= 0; i--) {
        	
            AlgorithmFamily iFamily = familiesInScope.get(i);
            List<Element> participants = iFamily.getParticipants();
            List<Element> elementsInScope = new ArrayList<>(scope.getElements());
            elementsInScope.removeAll(participants);
            List<Element> contexts = new ArrayList<>();
            for (Element element : elementsInScope) {
                List<Element> usedElements = new ArrayList<>();
                for (Relationship relationship : ElementUtil.getRelationships(element)) {
                    Element usedElement = RelationshipUtil.getUsedElementFromRelationship(relationship);
                    if (usedElement != null && !usedElement.equals(element)) {
                        usedElements.add(usedElement);
                    }
                }
                for (int j = 0; j < usedElements.size(); j++) {
                    if (!participants.contains(usedElements.get(j))) {
                        usedElements.remove(j);
                        j--;
                    }
                }
                if (!usedElements.isEmpty()) {
                    contexts.add(element);
                }
            }
            if (!contexts.isEmpty()) {
                PSStrategy psStrategy = new PSStrategy(contexts, iFamily);
                if (!scope.getPSs(this).contains(psStrategy)) {
                    scope.addPS(psStrategy);
                }
                isPs = true;
            }
        }
        //System.out.println("\n PS Strategy: "+isPs);
        return isPs;
    }

    @Override
    public boolean verifyPSPLA(Scope scope) {
        boolean isPsPla = false;
        if (verifyPS(scope)) {
        	//System.out.println("\n Verificação PSPLA Strategy - foi chamada");
            for (PS ps : scope.getPSs(this)) {
                PSStrategy psStrategy = (PSStrategy) ps;
                List<Element> contexts = psStrategy.getContexts();
                AlgorithmFamily algorithmFamily = psStrategy.getAlgorithmFamily();
                if (StrategyUtil.areTheAlgorithmFamilyAndContextsPartOfAVariability(algorithmFamily, contexts)) {
                    PSPLAStrategy psPlaStrategy = new PSPLAStrategy(contexts, algorithmFamily);
                    if (!scope.getPSsPLA(this).contains(psPlaStrategy)) {
                        scope.addPSPLA(psPlaStrategy);
                        isPsPla = true;
                    }
                }
            }
        }
        //System.out.println("\n PSPLA Strategy: "+isPsPla);
        return isPsPla;
    }

    @Override
    public boolean apply(Scope scope) {
        boolean applied = false;
        //System.out.println("\n Aplicacao do Strategy - foi chamada");
        List<PS> pSs = scope.getPSs(this);
        if (!pSs.isEmpty()) {
        	//System.out.println("\n Aplicacao do Strategy - entrou");
            PSStrategy psStrategy = (PSStrategy) pSs.get(0);
            AlgorithmFamily algorithmFamily = psStrategy.getAlgorithmFamily();
            List<Element> participants = psStrategy.getAlgorithmFamily().getParticipants();

            //Get or create Interface
            Interface strategyInterface = StrategyUtil.getStrategyInterfaceFromAlgorithmFamily(algorithmFamily);
            if (strategyInterface == null) {
                strategyInterface = StrategyUtil.createStrategyInterfaceForAlgorithmFamily(algorithmFamily);
            } else if (participants.contains(strategyInterface)) {
                participants.remove(strategyInterface);
            }

            //Implement
            //TODO - Édipo - Adicionar estereótipos Strategy.
            List<Element> adapterList = new ArrayList<>();
            List<Element> adapteeList = new ArrayList<>();

            ElementUtil.implementInterface(participants, strategyInterface, adapterList, adapteeList);

            participants.removeAll(adapteeList);

            for (Element adapterClass : adapterList) {
                if (!participants.contains(adapterClass)) {
                    participants.add(adapterClass);
                }
            }

            //Concern
            for (Element participant : participants) {
                for (Concern concern : participant.getOwnConcerns()) {
                    if (!strategyInterface.containsConcern(concern)) {
                        try {
                            strategyInterface.addConcern(concern.getName());
                        } catch (ConcernNotFoundException ex) {
                            Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

            //Move context relationships
            List<Element> contexts = psStrategy.getContexts();
            StrategyUtil.moveContextsRelationshipWithSameTypeAndName(contexts, new ArrayList<>(CollectionUtils.union(participants, adapteeList)), strategyInterface);

            //Variabilities, variants and variation points.
            StrategyUtil.moveVariabilitiesFromContextsToTarget(contexts, new ArrayList<>(CollectionUtils.union(participants, adapteeList)), strategyInterface);
            //joao - inserir a atribuição no final do escopo
            //applied = true;

            addStereotype(strategyInterface);
            addStereotype(participants);
            addStereotype(adapteeList);
            applied = true;
        }
        //System.out.println("\n Aplicação do Strategy? =" + applied);
        return applied;
    }
}
