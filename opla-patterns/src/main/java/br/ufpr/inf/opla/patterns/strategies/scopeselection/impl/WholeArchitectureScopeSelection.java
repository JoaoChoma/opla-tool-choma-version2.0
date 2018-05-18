package br.ufpr.inf.opla.patterns.strategies.scopeselection.impl;

import arquitetura.representation.Architecture;
import arquitetura.representation.Patterns;
import br.ufpr.inf.opla.patterns.models.Scope;
import br.ufpr.inf.opla.patterns.strategies.scopeselection.ScopeSelectionStrategy;

public class WholeArchitectureScopeSelection implements ScopeSelectionStrategy{

    @Override
    public Scope selectScope(Architecture architecture, Patterns pattern) {
    	//System.out.println("classe WheloArchScope - entrou na classe");
        Scope scope = new Scope();
        scope.getElements().addAll(architecture.getElements());
        return scope;
    }

}
