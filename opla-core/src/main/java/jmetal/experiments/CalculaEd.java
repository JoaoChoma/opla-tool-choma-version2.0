package jmetal.experiments;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.NonDominatedSolutionList;
import exceptions.MissingConfigurationException;

public class CalculaEd {

    private MetricsUtil mu;
    private NumberFormat format = NumberFormat.getInstance();
    
    public CalculaEd() {
	mu = new MetricsUtil();
	format.setMaximumFractionDigits(2);  
	format.setMinimumFractionDigits(2);  
	format.setMaximumIntegerDigits(2);  
	format.setRoundingMode(RoundingMode.HALF_UP);  
    }
    
    /**
     * Calcule distance euclideans given a experiment id.
     * 
     * @param experimentId
     * @param numberObjectives
     * @return {@link HashMap<String, Double>}. Solution Name, Distance Euclidean 
     */
    public HashMap<String, Double> calcula(String experimentId, int numberObjectives) {
	SolutionSet ss = queryNonDominatedSolutinsFromExperiment(experimentId);
	HashMap<String, Double> results = new HashMap<>();
	
	String[] names = new String[ss.size()];
	for(int i=0; i < ss.size(); i++)
	    names[i] = ss.get(i).getSolutionName();
	
	double[][] front = ss.writeObjectivesToMatrix();
	double[] min = mu.getMinimumValues(front, numberObjectives);
	for (int i = 0; i < min.length; i++) {
	    System.out.println("->"+min[i] + ", ");
	}
	for (int i = 0; i < front.length; i++) 
	    results.put(names[i],  arredondar(mu.distance(min, front[i]), 4, 0));

	return results;
    }
    
    private static double arredondar(double valor, int casas, int ceilOrFloor) {
	double arredondado = valor;
	arredondado *= (Math.pow(10, casas));
	if (ceilOrFloor == 0) {
	    arredondado = Math.ceil(arredondado);
	} else {
	    arredondado = Math.floor(arredondado);
	}
	arredondado /= (Math.pow(10, casas));
	return arredondado;
    }


    /**
     * 
     * @param experimentID
     *            - ID do experimento no banco de dados
     * @return
     */
    public SolutionSet queryNonDominatedSolutinsFromExperiment(String experimentID) {
	try {
	    Statement statement = database.Database.getConnection().createStatement();
	    
	    StringBuilder query = new StringBuilder();
	    query.append("SELECT objectives, solution_name FROM objectives WHERE experiement_id=");
	    query.append(experimentID);
	    query.append(" AND execution_ID=''");
	    
	    ResultSet r = statement.executeQuery(query.toString());
	    SolutionSet solutionSet = new NonDominatedSolutionList();

	    while (r.next()) {
		int count = 0;
		String[] line = r.getString("objectives").split("\\|");
		Solution solution = new Solution(line.length);
		solution.setSolutionName(r.getString("solution_name"));

		for (int i = 0; i < line.length; i++) {
		    solution.setObjective(count, Double.parseDouble(line[i]));
		    count++;
		}
		solutionSet.add(solution);
	    }
	    r.close();
	    statement.close();
	    return solutionSet;
	} catch (MissingConfigurationException | ClassNotFoundException | SQLException ex) {
	    Logger.getLogger(CalculaEd.class.getName()).log(Level.ERROR, null, ex);
	}

	return null;
    }

}
