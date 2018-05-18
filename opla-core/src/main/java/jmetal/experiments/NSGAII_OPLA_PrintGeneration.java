package jmetal.experiments;

import java.sql.SQLException;
import java.util.List;

import database.Result;
import jmetal.core.SolutionSet;
import jmetal.problems.OPLA;
import metrics.AllMetrics;
import persistence.AllMetricsPersistenceDependency;
import persistence.ExecutionPersistence;
import results.Execution;
import results.Experiment;
import results.FunResults;
import results.InfoResult;

//criado por Joao 
// objetivo salvar cada uma das geracoes alcançadas durante a otimizacao
// se há 100 individuos e 100 geracoes haverá a persistencia de 10000 fronteiras
public class NSGAII_OPLA_PrintGeneration {
	private static AllMetricsPersistenceDependency allMetricsPersistenceDependencies;
	OPLA problem = null;
	Experiment experiement = null;
	Result result = null;
	String selectedObjectiveFunctions = "";
	private NSGAIIConfig configs;
	
	public NSGAII_OPLA_PrintGeneration(){
		
	}
	
	public void printGeneration(SolutionSet resultFront) {

		Execution execution = new Execution(experiement);

		resultFront = problem.removeDominadas(resultFront);
		resultFront = problem.removeRepetidas(resultFront);

		List<FunResults> funResults = result.getObjectives(resultFront.getSolutionSet(), execution, experiement);
		//List<InfoResult> infoResults = result.getInformations(resultFront.getSolutionSet(), execution, experiement);
		//AllMetrics allMetrics = result.getMetrics(funResults, resultFront.getSolutionSet(), execution, experiement,
			//	selectedObjectiveFunctions);

		resultFront.saveVariablesToFile("VAR_", funResults, this.configs.getLogger(), true);

		execution.setFuns(funResults);
		//execution.setInfos(infoResults);
		//execution.setAllMetrics(allMetrics);

		ExecutionPersistence persistence = new ExecutionPersistence(allMetricsPersistenceDependencies);
		try {
			persistence.persist(execution);
			persistence = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
