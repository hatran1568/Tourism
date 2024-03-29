/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gatourism;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Tran Thi Nguyet Ha
 */
public class ACO {
    public double[][] costMatrix;
    public double[][] pheromoneMatrix;
    public int hotel;

    public ACO(Data data) {
        costMatrix = new double[data.P][data.P];
        pheromoneMatrix = new double[data.P][data.P];
        for (int i = 0; i < data.P; i++) {
            for (int j = 0; j < data.P; j++) {
                if (i != j) {
                    costMatrix[i][j] = data.D[i][j];
                }
                else{
                    costMatrix[i][j]=0;
                }
                if (costMatrix[i][j] == 0) {
                    pheromoneMatrix[i][j] = 0;
                } else {
                    pheromoneMatrix[i][j] = 2 / costMatrix[i][j];
                }
            }
        }
    }

    public ArrayList<Solution> generateAntColony(Data data) throws IOException {
        ArrayList<Solution> arr = new ArrayList<>();
        Solution s = new Solution(data);
        ACO Algorithm = new ACO(data);
        for (int l = 0; l < 200; l++) {
            ArrayList<Solution> ants = new ArrayList<>();
            for (int i = 0; i < 2000; i++) {
                Solution ant = new Solution(data);
                ArrayList<Integer> choosen = new ArrayList<>();
                for (int j = 0; j < data.K; j++) {
                    Random rand = new Random(System.currentTimeMillis());
                    int startLocation = rand.nextInt(data.P);
                    double budget = data.POI[startLocation].getCost();
                    double currentTime = 27000;
                    int currentLocation = startLocation;
                    ArrayList<Integer> oneTrip = new ArrayList<Integer>();
                    while (budget < data.C_max[j] || currentTime < data.T_max[j]) {
                        ArrayList<Integer> canVisited = new ArrayList<Integer>();
                        for (int k = 0; k < data.P; k++) {
                            double timePrediction=0;
                            if(currentLocation==startLocation){
                                timePrediction = currentTime + data.D[startLocation][k]*90 + data.POI[k].getDuration() + data.D[k][startLocation]*90;
                            }
                            
                            else
                            {
                                timePrediction = currentTime + data.D[currentLocation][k]*90 + data.POI[k].getDuration() + data.D[k][startLocation]*90;
                            }
                            if (timePrediction <= data.T_max[j] && choosen.indexOf(k) < 0) {
                                if (currentLocation == 0) {
                                    if (budget + data.S * data.D[startLocation][k] + data.POI[k].getCost() < data.C_max[j]) {
                                        canVisited.add(k);
                                    }
                                } else {
                                    if (budget + data.S * data.D[currentLocation][k] + data.POI[k].getCost() < data.C_max[j]) {
                                        canVisited.add(k);
                                    }
                                }

                            }
                        }
                        if (canVisited.size() == 0) {
                            break;
                        }

                        DistributedRandomNumberGenerator drng = new DistributedRandomNumberGenerator();
                        for (Integer integer : canVisited) {
                            drng.addNumber(integer, Algorithm.pheromoneMatrix[currentLocation][integer]);
                        }
                        int random = drng.getDistributedRandomNumber();
                        oneTrip.add(random);
                        budget += data.S * data.D[currentLocation][random] + data.POI[random].getCost();
                        currentTime += data.D[currentLocation][random]*90 + data.POI[random].getDuration();
                        currentLocation = random;
                        choosen.add(random);

                    }
                    ant.gene.add(oneTrip);

                }

                ants.add(ant);
            }
            Collections.sort(ants, (o1, o2) -> {
                return Double.compare(o1.cal_fitness(), o2.cal_fitness());
            });
            int index=0;
            for (Solution ant : ants) { 
                if(index>20) break;
                index++;
                double pheromone = 0;
//                for (int j = 0; j < data.K; j++) {
//                    for (int k = 0; k < ant.gene.get(j).size() - 1; k++) {
//                        pheromone += Algorithm.pheromoneMatrix[ant.gene.get(j).get(k)][ant.gene.get(j).get(k + 1)];
//                        
//                    }
//                }
              
                for (int j = 0; j < data.K; j++) {
                    for (int k = 0; k < ant.gene.get(j).size() - 1; k++) {
                        Algorithm.pheromoneMatrix[ant.gene.get(j).get(k)][ant.gene.get(j).get(k+1)]
                                += 2/ Algorithm.pheromoneMatrix[ant.gene.get(j).get(k)][ant.gene.get(j).get(k+1)];                       
                    }
                }
                
            }
            Collections.sort(ants, (o1, o2) -> {
                return Double.compare(o1.cal_fitness(), o2.cal_fitness());
            });
            if (arr.size() > 0) {
                if (ants.get(0).cal_fitness() >= arr.get(arr.size() - 1).cal_fitness()) {
                    arr.add(arr.get(arr.size() - 1));
                } else {
                    arr.add(ants.get(0));
                }
            } else if (arr.size()==0){
                arr.add(ants.get(0));
            }
            
        }

        return arr;
    }
//    public ArrayList<ArrayList<Integer>> generateTour(Data data) throws IOException {
//        ArrayList<ArrayList<Integer>> arr = new ArrayList<>();
//        Solution s = new Solution(data);
//        ArrayList<Integer> chosen = new ArrayList<>();
//        Solution ant = new Solution();
//        for (int j = 0; j < data.K; j++) {
//                    double budget = data.hotel[ant.hotel][1];
//                    double currentTime = 27000;
//                    int currentLocation = 0;
//                    ArrayList<Integer> oneTrip = new ArrayList<Integer>();
//                    while (budget < data.C_max[j] || currentTime < data.T_max[j]) {
//                        ArrayList<Integer> canVisit = new ArrayList<Integer>();
//                        for (int k = 0; k < data.P - data.H; k++) {
//                            double timePrediction=0;
//                            if(currentLocation==0){
//                                timePrediction = currentTime + data.D[ant.hotel][k + data.H] + data.POI[k].getDuration() + data.D[k + data.H][ant.hotel];
//                            }
//                            
//                            else
//                            {
//                                timePrediction = currentTime + data.D[currentLocation+data.H][k + data.H]*90 + data.POI[k].getDuration() + data.D[k + data.H][ant.hotel]*90;
//                            }
//                            if (timePrediction <= data.T_max[j] && chosen.indexOf(k) < 0) {
//                                if (currentLocation == 0) {
//                                    if (budget + data.S * data.D[ant.hotel][k + data.H] + data.POI[k].getCost() < data.C_max[j]) {
//                                        canVisit.add(k);
//                                    }
//                                } else {
//                                    if (budget + data.S * data.D[currentLocation + data.H][k + data.H] + data.POI[k].getCost() < data.C_max[j]) {
//                                        canVisit.add(k);
//                                    }
//                                }
//
//                            }
//                        }
//                        if (canVisit.size() == 0) {
//                            break;
//                        }
//                        Random random = new Random();
//                        int nextDest = random.nextInt(42);
//
//                        while (chosen.contains(nextDest)){
//                            nextDest = random.nextInt(42);
//                        }
//                        oneTrip.add(nextDest);
//                        if (currentLocation == 0) {
//                            budget += data.S * data.D[ant.hotel][nextDest + data.H] + data.POI[nextDest].getCost();
//                        } else {
//                            budget += data.S * data.D[currentLocation + data.H][nextDest + data.H] + data.POI[nextDest].getCost();
//                        }
//                        currentTime += data.D[currentLocation][nextDest + data.H]*90 + data.POI[nextDest].getDuration();
//                        currentLocation = nextDest;
//                        chosen.add(nextDest);
//
//                    }
//                    arr.add(oneTrip);
//
//                }
//
//        return arr;
//    }
    
    public static void writeSolution(ArrayList<Solution> solutions) throws IOException{
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");
        
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Fitness");
        
        cell = row.createCell(1);
        cell.setCellValue("Trip");
        
        cell = row.createCell(2);
        cell.setCellValue("Happiness");
        
        cell = row.createCell(3);
        cell.setCellValue("Distance");
        
        cell = row.createCell(4);
        cell.setCellValue("No. dest");
        
        cell = row.createCell(5);
        cell.setCellValue("Waiting time");
        
        int rowCount = 0;
        for (Solution s: solutions){
            row = sheet.createRow(++rowCount);
            cell = row.createCell(0);
            cell.setCellValue(s.cal_fitness());

            cell = row.createCell(1);
            cell.setCellValue(s.gene.toString());

            cell = row.createCell(2);
            cell.setCellValue(s.cal_hapiness_obj());

            cell = row.createCell(3);
            cell.setCellValue(s.cal_distance_obj());

            cell = row.createCell(4);
            cell.setCellValue(s.cal_number_of_destination_obj());

            cell = row.createCell(5);
            cell.setCellValue(s.cal_waiting_time_obj());
            
        }
        try (FileOutputStream outputStream = new FileOutputStream("Result.xlsx")) {
            workbook.write(outputStream);
        }    
    }
}
