package jmetal.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import jmetal.core.Algorithm;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.memetic.Bestof2;
import jmetal.metaheuristics.memetic.UntilBest;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.OPLA;
import jmetal.util.JMException;
import logs.log_log.Level;
import metrics.AllMetrics;
import persistence.AllMetricsPersistenceDependency;
import persistence.DistanceEuclideanPersistence;
import persistence.ExecutionPersistence;
import persistence.ExperimentConfs;
import persistence.MetricsPersistence;
import results.Execution;
import results.Experiment;
import results.FunResults;
import results.InfoResult;
import arquitetura.io.ReaderConfig;
import database.Database;
import database.Result;
import exceptions.MissingConfigurationException;

public class UntilBest_OPLA_FeatMut {

	private static Connection connection;
	private static AllMetricsPersistenceDependency allMetricsPersistenceDependencies;
	private static MetricsPersistence mp;
	private static Result result;

	public static int populationSize;
	public static int maxEvaluations;
	public static double mutationProbability;
	// jcn
	public static double mutationLocalProbability;

	public static double crossoverProbability;
	public String[] testePadroes;
	public List<String> testeOperadores;

	private NSGAIIConfig configs;

	private String experiementId;
	private int numberObjectives;
	public static int numeroFuncoesObjetivo;

	public UntilBest_OPLA_FeatMut(NSGAIIConfig config) {
		this.configs = config;
	}

	public UntilBest_OPLA_FeatMut() {
	}

	public void setConfigs(NSGAIIConfig configs) {
		this.configs = configs;
	}

	public void execute() throws FileNotFoundException, IOException, JMException, ClassNotFoundException {

		intializeDependencies();

		int runsNumber = this.configs.getNumberOfRuns();
		populationSize = this.configs.getPopulationSize();
		maxEvaluations = this.configs.getMaxEvaluation();
		crossoverProbability = this.configs.getCrossoverProbability();
		mutationProbability = this.configs.getMutationProbability();
		//jcn
		mutationLocalProbability = 0.9;

		this.numberObjectives = this.configs.getOplaConfigs().getNumberOfObjectives();

		String context = "OPLA";

		String plas[] = this.configs.getPlas().split(",");
		String xmiFilePath;

		for (String pla : plas) {
			xmiFilePath = pla;
			OPLA problem = null;
			String plaName = getPlaName(pla);

			try {
				problem = new OPLA(xmiFilePath, this.configs);
			} catch (Exception e) {
				
				e.printStackTrace();
				this.configs.getLogger()
						.putLog(String.format("Error when try read architecture %s. %s", xmiFilePath, e.getMessage()));
			}

			Experiment experiement = mp.createExperimentOnDb(plaName, "UntilBest", configs.getDescription());
			ExperimentConfs conf = new ExperimentConfs(experiement.getId(), "UntilBest", configs);
			conf.save();

			Algorithm algorithm;
			SolutionSet todasRuns = new SolutionSet();

			Crossover crossover;
			Mutation mutation;
			// jcn
			Mutation operatorLocal;

			Selection selection;

			HashMap<String, Object> parameters;

			// jcn
			HashMap<String, Object> parametersLocal;

			algorithm = new UntilBest(problem);

			// Algorithm parameters
			algorithm.setInputParameter("populationSize", populationSize);
			algorithm.setInputParameter("maxEvaluations", maxEvaluations);

			// Mutation and Crossover
			parameters = new HashMap<String, Object>();

			parameters.put("probability", crossoverProbability);
			crossover = CrossoverFactory.getCrossoverOperator("PLACrossover", parameters);

			parameters = new HashMap<String, Object>();
			parameters.put("probability", mutationProbability);
			mutation = MutationFactory.getMutationOperator("PLAFeatureMutation", parameters, this.configs);

			// jcn
//			parametersLocal = new HashMap<String, Object>();
//			parametersLocal.put("probability", mutationLocalProbability);
			operatorLocal = MutationFactory.getMutationOperatorPatterns("PLAPatternsMutation", parameters,
					this.configs);

			// Selection Operator
			parameters = null;
			selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters);

			// Add the operators to the algorithm
			algorithm.addOperator("crossover", crossover);
			algorithm.addOperator("mutation", mutation);
			// joao
			algorithm.addOperatorLocal("operatorLocal", operatorLocal);
			algorithm.addOperator("selection", selection);

		

			if (this.configs.isLog())
				logInforamtions(context, pla);

			List<String> selectedObjectiveFunctions = this.configs.getOplaConfigs().getSelectedObjectiveFunctions();
			mp.saveObjectivesNames(selectedObjectiveFunctions, experiement.getId());

			result.setPlaName(plaName);

			long time[] = new long[runsNumber];

			for (int runs = 0; runs < runsNumber; runs++) {
				// jcn
				// System.out.println("nova rodada - oplacore - classe
				// nsgaii_opla_feat_mut");
				// Cria uma execução. Cada execução está ligada a um
				// experiemento.
				Execution execution = new Execution(experiement);
				setDirToSaveOutput(experiement.getId(), execution.getId());

				// Execute the Algorithm
				long initTime = System.currentTimeMillis();

				// jcn
				// System.out.println("iniciou o execuçao do NSGA oplacore -
				// classe nsgaii_opla_feat_mut");

				SolutionSet resultFront = algorithm.execute();
				long estimatedTime = System.currentTimeMillis() - initTime;
				time[runs] = estimatedTime;

				resultFront = problem.removeDominadas(resultFront);
				resultFront = problem.removeRepetidas(resultFront);

				execution.setTime(estimatedTime);

				List<FunResults> funResults = result.getObjectives(resultFront.getSolutionSet(), execution,
						experiement);
				List<InfoResult> infoResults = result.getInformations(resultFront.getSolutionSet(), execution,
						experiement);
				AllMetrics allMetrics = result.getMetrics(funResults, resultFront.getSolutionSet(), execution,
						experiement, selectedObjectiveFunctions);

				resultFront.saveVariablesToFile("VAR_" + runs + "_", funResults, this.configs.getLogger(), true);

				execution.setFuns(funResults);
				execution.setInfos(infoResults);
				execution.setAllMetrics(allMetrics);

				ExecutionPersistence persistence = new ExecutionPersistence(allMetricsPersistenceDependencies);
				try {
					persistence.persist(execution);
					persistence = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}

				// armazena as solucoes de todas runs
				todasRuns = todasRuns.union(resultFront);

				// Util.copyFolder(experiement.getId(), execution.getId());
				// Util.moveAllFilesToExecutionDirectory(experiementId,
				// execution.getId());

				saveHypervolume(experiement.getId(), execution.getId(), resultFront, plaName);

			}

			todasRuns = problem.removeDominadas(todasRuns);
			todasRuns = problem.removeRepetidas(todasRuns);

			this.configs.getLogger().putLog("------ All Runs - Non-dominated solutions --------", Level.INFO);
			List<FunResults> funResults = result.getObjectives(todasRuns.getSolutionSet(), null, experiement);

			todasRuns.saveVariablesToFile("VAR_All_", funResults, this.configs.getLogger(), true);

			mp.saveFunAll(funResults);

			List<InfoResult> infoResults = result.getInformations(todasRuns.getSolutionSet(), null, experiement);
			mp.saveInfoAll(infoResults);

			AllMetrics allMetrics = result.getMetrics(funResults, todasRuns.getSolutionSet(), null, experiement,
					selectedObjectiveFunctions);
			mp.persisteMetrics(allMetrics, this.configs.getOplaConfigs().getSelectedObjectiveFunctions());
			mp = null;

			setDirToSaveOutput(experiement.getId(), null);

			CalculaEd c = new CalculaEd();
			DistanceEuclideanPersistence.save(c.calcula(this.experiementId, this.numberObjectives), this.experiementId);
			infoResults = null;
			funResults = null;

			// Util.moveAllFilesToExecutionDirectory(experiementId, null);
			saveHypervolume(experiement.getId(), null, todasRuns, plaName);
		}

		// Util.moveResourceToExperimentFolder(this.experiementId);

	}

	private void logInforamtions(String context, String pla) {
		configs.getLogger().putLog("\n================ UntilBest ================", Level.INFO);
		configs.getLogger().putLog("Context: " + context, Level.INFO);
		configs.getLogger().putLog("PLA: " + pla, Level.INFO);
		configs.getLogger().putLog("Params:", Level.INFO);
		configs.getLogger().putLog("\tPop -> " + populationSize, Level.INFO);
		configs.getLogger().putLog("\tMaxEva -> " + maxEvaluations, Level.INFO);
		configs.getLogger().putLog("\tCross -> " + crossoverProbability, Level.INFO);
		configs.getLogger().putLog("\tMuta -> " + mutationProbability, Level.INFO);
		configs.getLogger().putLog("\tMutaLocal -> " + mutationProbability, Level.INFO);

		long heapSize = Runtime.getRuntime().totalMemory();
		heapSize = (heapSize / 1024) / 1024;
		configs.getLogger().putLog("Heap Size: " + heapSize + "Mb\n");
	}

	private void intializeDependencies() {
		result = new Result();
		Database.setPathToDB(this.configs.getPathToDb());

		try {
			connection = Database.getConnection();
			allMetricsPersistenceDependencies = new AllMetricsPersistenceDependency(connection);
			mp = new MetricsPersistence(allMetricsPersistenceDependencies);
		} catch (ClassNotFoundException | MissingConfigurationException | SQLException e) {
			e.printStackTrace();
		}

	}

	private static String getPlaName(String pla) {
		int beginIndex = pla.lastIndexOf("/") + 1;
		int endIndex = pla.length() - 4;
		return pla.substring(beginIndex, endIndex);
	}

	private void setDirToSaveOutput(String experimentID, String executionID) {
		this.experiementId = experimentID;
		String dir;
		if (executionID != null) {
			dir = ReaderConfig.getDirExportTarget() + experimentID + System.getProperty("file.separator") + executionID
					+ System.getProperty("file.separator");
		} else {
			dir = ReaderConfig.getDirExportTarget() + experimentID + System.getProperty("file.separator");
		}
		File newDir = new File(dir);
		if (!newDir.exists())
			newDir.mkdirs();
	}

	private void saveHypervolume(String experimentID, String executionID, SolutionSet allSolutions, String plaName) {
		String dir;
		if (executionID != null)
			dir = ReaderConfig.getDirExportTarget() + experimentID + "/" + executionID + "/Hypervolume/";
		else
			dir = ReaderConfig.getDirExportTarget() + experimentID + "/Hypervolume/";

		File newDir = new File(dir);
		if (!newDir.exists())
			newDir.mkdirs();

		allSolutions.printObjectivesToFile(dir + "/hypervolume.txt");
	}

}