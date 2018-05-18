package jmetal.experiments;

import java.io.IOException;

import jmetal.util.JMException;

public class PAES_OPLA_FeatMutInitializer implements AlgorithmBase {

    private PaesConfigs config;

    public PAES_OPLA_FeatMutInitializer(PaesConfigs config) {
	 this.config = config;
    }

    @Override
    public void run() {
	PAES_OPLA_FeatMut paes = new PAES_OPLA_FeatMut(this.config);
	
	try {
	    paes.execute();
	} catch (ClassNotFoundException | IOException | JMException e) {
	    e.printStackTrace();
	}

    }

}