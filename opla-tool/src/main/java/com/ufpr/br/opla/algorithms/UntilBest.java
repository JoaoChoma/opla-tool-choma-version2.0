/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ufpr.br.opla.algorithms;

import arquitetura.io.ReaderConfig;
import com.ufpr.br.opla.configuration.UserHome;
import com.ufpr.br.opla.configuration.VolatileConfs;
import com.ufpr.br.opla.utils.MutationOperatorsSelected;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import jmetal.experiments.FeatureMutationOperators;
import jmetal.experiments.NSGAIIConfig;
import jmetal.experiments.NSGAII_OPLA_FeatMutInitializer;
import jmetal.experiments.OPLAConfigs;
import jmetal.experiments.UntilBest_OPLA_FeatMutInitializer;
import logs.log_log.Logger;

/**
 *
 * @author elf
 */
public class UntilBest {
	
	public void execute(JComboBox comboAlgorithms, JCheckBox checkMutation, JTextField fieldMutationProb,
			JTextArea fieldArchitectureInput, JTextField fieldNumberOfRuns, JTextField fieldPopulationSize,
			JTextField fieldMaxEvaluations, JCheckBox checkCrossover, JTextField fieldCrossoverProbability,
			String executionDescription) {	
		try {

			ReaderConfig.setPathToConfigurationFile(UserHome.getPathToConfigFile());
			ReaderConfig.load();

			NSGAIIConfig configs = new NSGAIIConfig();

			configs.setLogger(Logger.getLogger());
			configs.activeLogs();
			configs.setDescription(executionDescription);

			// Se mutação estiver marcada, pega os operadores selecionados
			// ,e seta a probabilidade de mutacao
			if (checkMutation.isSelected()) {
				List<String> mutationsOperators = MutationOperatorsSelected.getSelectedMutationOperators();
				configs.setMutationOperators(mutationsOperators);
				configs.setMutationProbability(Double.parseDouble(fieldMutationProb.getText()));
				// configPublica.setMutationProbability(Double.parseDouble(fieldMutationProb.getText()));
			}
			
			configs.setPlas(fieldArchitectureInput.getText());
			configs.setNumberOfRuns(Integer.parseInt(fieldNumberOfRuns.getText()));
			configs.setPopulationSize(Integer.parseInt(fieldPopulationSize.getText()));
			configs.setMaxEvaluations(Integer.parseInt(fieldMaxEvaluations.getText()));

			// Se crossover estiver marcado, configura probabilidade
			// Caso contrario desativa
			if (checkCrossover.isSelected()) {
				configs.setCrossoverProbability(Double.parseDouble(fieldCrossoverProbability.getText()));
			} else {
				configs.disableCrossover();
			}

			// OPA-Patterns Configurations
			if (MutationOperatorsSelected.getSelectedMutationOperators()
					.contains(FeatureMutationOperators.DESIGN_PATTERNS.getOperatorName())) {
				// joao
				System.out.println("Instanciando o campo do Patterns - oplatool classe nsgaii");
				String[] array = new String[MutationOperatorsSelected.getSelectedPatternsToApply().size()];
				configs.setPatterns(MutationOperatorsSelected.getSelectedPatternsToApply().toArray(array));
				configs.setDesignPatternStrategy(VolatileConfs.getScopePatterns());

			}

			List<String> operadores = configs.getMutationOperators();

			for (int i = 0; i < operadores.size(); i++) {
				if (operadores.get(i) == "DesignPatterns") {
					operadores.remove(i);
				}
			}
			configs.setMutationOperators(operadores);
			// operadores convencionais ok
			// operadores padroes ok
			// String[] padroes = configs.getPatterns();

			// Configura onde o db esta localizado
			configs.setPathToDb(UserHome.getPathToDb());

			// Instancia a classe de configuracao da OPLA.java
			OPLAConfigs oplaConfig = new OPLAConfigs();

			// Funcoes Objetivo
			oplaConfig.setSelectedObjectiveFunctions(VolatileConfs.getObjectiveFunctionSelected());

			// Add as confs de OPLA na classe de configuracoes gerais.
			configs.setOplaConfigs(oplaConfig);

			

			// Utiliza a classe Initializer do NSGAII passando as configs.
			UntilBest_OPLA_FeatMutInitializer untilBest = new UntilBest_OPLA_FeatMutInitializer(configs);

			// Executa
			System.out.println("Execução UntilBest");
			untilBest.run();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}