
package jmetal.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.OPLA;
import jmetal.util.JMException;


public class NSGAII_OPLA_FeatMut {
 
 public static int populationSize_; 
 public static int maxEvaluations_;
 public static double mutationProbability_;
 public static double crossoverProbability_; 
 
//--  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
 public static void main(String[] args) throws FileNotFoundException, IOException, JMException, ClassNotFoundException {

	    int runsNumber = 30; //30;
        populationSize_ = 100; //100; 
        maxEvaluations_ = 30000; //300 geraçõeshttp://loggr.net/
         
         crossoverProbability_ = 0.0; 
         mutationProbability_ = 0.9;
         String context = "OPLA";
         //Thelma - Dez2013 linha adicionada para identificar o algoritmo no nome do arquivo do hypervolume
         String moea = "NSGAII-M";
         
         //File directory = new File("resultado/nsgaii/" + context);
         File directory = new File("experiment/OPLA/NSGA-II/FeatureMutation" + "/");
         if (!directory.exists()) {
             if (!directory.mkdirs()) {
             	System.out.println("N�o foi poss�vel criar o diret�rio do resultado");
             	System.exit(0);
             }
         }


         String plas[] = new String[]{
        		"/Users/elf/mestrado/sourcesMestrado/arquitetura/src/test/java/resources/agmfinal/agm.uml" };
         String xmiFilePath;
          		
         for (String pla : plas) {
               xmiFilePath = pla;
         
     	OPLA problem = null;
			try {
				problem = new OPLA(xmiFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
         Algorithm algorithm; 
         SolutionSet todasRuns = new SolutionSet();
         // Thelma - Dez2013 - adicao da linha abaixo
         SolutionSet allSolutions = new SolutionSet();
         
         Crossover crossover;
         Mutation mutation;
         Selection selection;

         HashMap  parameters ; // Operator parameters

                   
         algorithm = new NSGAII(problem) ;
                     
         // Algorithm parameters
         algorithm.setInputParameter("populationSize",populationSize_);
         algorithm.setInputParameter("maxEvaluations",maxEvaluations_);

         // Mutation and Crossover
         parameters = new HashMap() ;
         parameters.put("probability", crossoverProbability_) ;
         crossover = CrossoverFactory.getCrossoverOperator("PLACrossover", parameters);                   

         parameters = new HashMap() ;
         parameters.put("probability", mutationProbability_) ;
         mutation = MutationFactory.getMutationOperator("PLAFeatureMutation", parameters);                        

         // Selection Operator 
         parameters = null ;
         selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;     

         // Add the operators to the algorithm
         algorithm.addOperator("crossover",crossover);
         algorithm.addOperator("mutation",mutation);
         algorithm.addOperator("selection",selection);
         
        
                   
         System.out.println("\n================ NSGAII ================");
         System.out.println("Context: " + context);
         System.out.println("PLA: " + pla);
         System.out.println("Params:");
         System.out.println("\tPop -> " + populationSize_);
         System.out.println("\tMaxEva -> "+maxEvaluations_);
         System.out.println("\tCross -> "+crossoverProbability_);
         System.out.println("\tMuta -> "+mutationProbability_);
         
                     
         long heapSize = Runtime.getRuntime().totalMemory();
         heapSize = (heapSize / 1024) / 1024;
         System.out.println("Heap Size: " + heapSize + "Mb\n");

 		 String PLAName = getPlaName(pla);
         long time[] = new long[runsNumber];
         
         for (int runs = 0; runs < runsNumber; runs++) {
         	
         	// Execute the Algorithm
             
         	 long initTime = System.currentTimeMillis();
             SolutionSet resultFront = algorithm.execute();
             long estimatedTime = System.currentTimeMillis() - initTime;
             //System.out.println("Iruns: " + runs + "\tTotal time: " + estimatedTime);
             time[runs] = estimatedTime;
             
             resultFront = problem.removeDominadas(resultFront);
             resultFront = problem.removeRepetidas(resultFront);

             resultFront.printObjectivesToFile(directory + "/FUN_" + PLAName + "_" + runs + ".txt");
             //resultFront.printVariablesToFile(directory + "/VAR_" + runs);
             resultFront.printInformationToFile(directory + "/INFO_" + PLAName + "_" + runs + ".txt");
            // resultFront.saveVariablesToFile(directory + "/VAR_" + runs + "_");
             resultFront.saveVariablesToFile("VAR_" + runs + "_");
             
             //armazena as solucoes de todas runs
             todasRuns = todasRuns.union(resultFront);
             
           //Thelma - Dez2013
             allSolutions = allSolutions.union(resultFront);
             resultFront.printMetricsToFile(directory + "/Metrics_" + PLAName + "_" + runs + ".txt");


         }
         //Thelma - Dez2013 - duas proximas linhas
         String NameOfPLA = getPlaName(pla);
         allSolutions.printObjectivesToFile(directory + "/Hypervolume/" + NameOfPLA + "/"  + NameOfPLA + "_HV_" + moea + ".txt");
                
         todasRuns.printTimeToFile(directory + "/TIME_" + PLAName, runsNumber, time, pla);
         
         todasRuns = problem.removeDominadas(todasRuns);
         todasRuns = problem.removeRepetidas(todasRuns);
        
         System.out.println("------    All Runs - Non-dominated solutions --------");          
         todasRuns.printObjectivesToFile(directory + "/FUN_All_" + PLAName + ".txt");
         //todasRuns.printVariablesToFile(directory + "/VAR_All");
         todasRuns.printInformationToFile(directory + "/INFO_All_" + PLAName + ".txt");
         //todasRuns.saveVariablesToFile(directory + "/VAR_All_");
         todasRuns.saveVariablesToFile("VAR_All_");
         
         //Thelma - Dez2013
         todasRuns.printMetricsToFile(directory + "/Metrics_All_" + PLAName + ".txt");
         todasRuns.printAllMetricsToFile(directory + "/FUN_Metrics_All_" + PLAName + ".txt");
         
         }
 }

 	private static String getPlaName(String pla) {
 		int beginIndex = pla.lastIndexOf("/") + 1;
		int endIndex = pla.length() - 4;
		return pla.substring(beginIndex, endIndex);
 	}
 

}