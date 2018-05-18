package jmetal.metaheuristics.nsgaII;

import java.io.FileWriter;
import java.io.IOException;

import jmetal.core.SolutionSet;

public class PrintGenerations implements Runnable{
	public PrintGenerations(){	
	}
	
	SolutionSet offSpring_ = null;
	int evaluations_ = 0;
	public PrintGenerations(SolutionSet offspringPopulation, int evaluations){
		this.evaluations_ = evaluations;
		this.offSpring_ = offspringPopulation;
		
		
	}

	@Override
	public synchronized void run() {
		offSpring_.saveGenerationsToFile("Generations_"+evaluations_, true);
		double fitnessFunctions;
		String fitnessValues = null;
		StringBuilder str = new StringBuilder(fitnessValues);
		
		for (int k = 0; k < offSpring_.size(); k++) {
			fitnessFunctions = 0;
			fitnessValues = null;
			
			for(int w=0; w < offSpring_.get(0).numberOfObjectives(); w++){
				fitnessFunctions = offSpring_.get(k).getObjective(k);
				str.append(" , "+String.valueOf(fitnessFunctions));
			}
			
			FileWriter r;
			try {
				r = new FileWriter("generations-UntilBest.txt", true);
				r.write("\n "+ fitnessValues);
				r.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("erro na escrita");
			}
			
		}
		
	}
	
}
