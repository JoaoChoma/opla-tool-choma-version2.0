package jmetal.experiments;

import java.io.IOException;
import java.util.List;

import jmetal.util.JMException;

public class Bestof12_OPLA_FeatMutInitializer implements AlgorithmBase {

	private NSGAIIConfig config;
	private NSGAIIConfig configPublica;

	public Bestof12_OPLA_FeatMutInitializer(NSGAIIConfig config) {
		this.config = config;

	}

	public void NSGAII_OPLA_FeatMutInitializerPublica(NSGAIIConfig configPublica) {
		this.configPublica = configPublica;
		// joao
	}

	@Override
	public void run() {
		//jcn nova classe ...memetic
		Bestof12_OPLA_FeatMut bestof12FeatMut = new Bestof12_OPLA_FeatMut();
		bestof12FeatMut.setConfigs(this.config);
		try {
			bestof12FeatMut.execute();
		} catch (ClassNotFoundException | IOException | JMException e) {
			e.printStackTrace();
		}
	}

}