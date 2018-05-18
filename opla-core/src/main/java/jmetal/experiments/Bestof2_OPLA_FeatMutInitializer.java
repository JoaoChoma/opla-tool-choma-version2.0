package jmetal.experiments;

import java.io.IOException;
import java.util.List;

import jmetal.util.JMException;

public class Bestof2_OPLA_FeatMutInitializer implements AlgorithmBase {

	private NSGAIIConfig config;
	private NSGAIIConfig configPublica;

	public Bestof2_OPLA_FeatMutInitializer(NSGAIIConfig config) {
		this.config = config;

	}

	public void NSGAII_OPLA_FeatMutInitializerPublica(NSGAIIConfig configPublica) {
		this.configPublica = configPublica;
		// joao
	}

	@Override
	public void run() {
		//jcn nova classe ...memetic
		Bestof2_OPLA_FeatMut bestof2FeatMut = new Bestof2_OPLA_FeatMut();
		bestof2FeatMut.setConfigs(this.config);
		try {
			bestof2FeatMut.execute();
		} catch (ClassNotFoundException | IOException | JMException e) {
			e.printStackTrace();
		}
	}

}