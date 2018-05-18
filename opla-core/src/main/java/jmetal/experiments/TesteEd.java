package jmetal.experiments;

import java.util.HashMap;
import java.util.Map;

import database.Database;

public class TesteEd {

    public static void main(String[] args) {
	Database.setPathToDB("/Users/elf/oplatool/db/oplatool.db");
	CalculaEd ed = new CalculaEd();
//paes bet 4744218659
//nsga bet 5568111744	
	HashMap<String, Double> eds = ed.calcula("2777147138", 3);
	
	for (Map.Entry<String, Double> entry : eds.entrySet()) {
	    System.out.println(arredondar(entry.getValue(), 4, 0));
	}

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
    

}
