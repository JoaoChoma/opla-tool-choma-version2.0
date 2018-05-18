package br.ufpr.inf.opla.patterns.util;

import arquitetura.representation.ParameterMethod;

public class ParameterMethodUtil {

    public static ParameterMethod cloneParameter(ParameterMethod parameter) {
        return new ParameterMethod(parameter.getName(), parameter.getType(), parameter.getDirection());
    }
    
    private ParameterMethodUtil() {
    }

}
