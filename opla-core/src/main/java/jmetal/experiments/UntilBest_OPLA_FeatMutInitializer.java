package jmetal.experiments;

import java.io.IOException;
import java.util.List;

import jmetal.util.JMException;

public class UntilBest_OPLA_FeatMutInitializer implements AlgorithmBase {

	private NSGAIIConfig config;
	private NSGAIIConfig configPublica;

	public UntilBest_OPLA_FeatMutInitializer(NSGAIIConfig config) {
		this.config = config;

	}

	public void NSGAII_OPLA_FeatMutInitializerPublica(NSGAIIConfig configPublica) {
		this.configPublica = configPublica;
		// joao
	}

	@Override
	public void run() {
		//jcn nova classe ...memetic
		UntilBest_OPLA_FeatMut untilBestFeatMut = new UntilBest_OPLA_FeatMut();
		untilBestFeatMut.setConfigs(this.config);
		try {
			untilBestFeatMut.execute();
		} catch (ClassNotFoundException | IOException | JMException e) {
			e.printStackTrace();
		}
	}

}