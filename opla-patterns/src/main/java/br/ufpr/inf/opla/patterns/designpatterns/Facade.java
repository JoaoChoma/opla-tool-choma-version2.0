package br.ufpr.inf.opla.patterns.designpatterns;

import br.ufpr.inf.opla.patterns.models.Scope;

public class Facade extends DesignPattern {

    private static volatile Facade INSTANCE;

    public static synchronized Facade getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Facade();
        }
        return INSTANCE;
    }

    private Facade() {
        super("Facade", "Structural");
    }

    @Override
    public boolean verifyPS(Scope scope) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean verifyPSPLA(Scope scope) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean apply(Scope scope) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
