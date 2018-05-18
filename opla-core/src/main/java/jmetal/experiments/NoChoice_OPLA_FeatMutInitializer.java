package jmetal.experiments;

import java.io.IOException;
import java.util.List;

import jmetal.util.JMException;

public class NoChoice_OPLA_FeatMutInitializer implements AlgorithmBase {

	private NSGAIIConfig config;
	private NSGAIIConfig configPublica;

	public NoChoice_OPLA_FeatMutInitializer(NSGAIIConfig config) {
		this.config = config;

	}

	public void NSGAII_OPLA_FeatMutInitializerPublica(NSGAIIConfig configPublica) {
		this.configPublica = configPublica;
		// joao
	}

	@Override
	public void run() {
		//jcn nova classe ...memetic
		NoChoice_OPLA_FeatMut noChoiceFeatMut = new NoChoice_OPLA_FeatMut();
		noChoiceFeatMut.setConfigs(this.config);
		try {
			noChoiceFeatMut.execute();
		} catch (ClassNotFoundException | IOException | JMException e) {
			e.printStackTrace();
		}
	}

}