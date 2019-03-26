import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;


public class Traintest {
	
	
	
	public static LinkedHashMap buildFeatureMatrix() throws IOException{
		
		
		
		// all query ids
		int[] qid = {85, 59, 56, 71, 64, 62, 93, 99, 58, 77, 54, 87, 94, 100, 89, 61, 95, 68, 57, 97, 98, 60, 80, 63, 91};
		// query ids to train the model
		int[] trainQid = {85, 59, 56, 71, 64, 62, 93, 99, 58, 77, 54, 87, 94, 100, 89, 61, 95, 68, 57, 97};
		// query ids to test or predict the model
		int[] testQid = {98, 60, 80, 63, 91};
		//System.out.println("Length of the array: "+qid.length);
		
		//load the actual queryIds to a tempHashMap
		LinkedHashMap<Integer, Integer> tempQIDHash = new LinkedHashMap<Integer, Integer>();
		
		for(int i = 0; i < 20; i++){
			
			tempQIDHash.put(qid[i], 0);
		}
		
		for(int i = 20 ; i < 25; i++){
			
			tempQIDHash.put(qid[i], 1);
		}
		//System.out.println("Length of the qid hash: "+tempQIDHash.size());
		
		
		//structure: <QID-DocID, List<Double>>
		LinkedHashMap<String, List<Double>> featureMatrix = new LinkedHashMap<String, List<Double>>();
		
		
		
		// add relevance as label for FeatureMatrix <QID-DOCID, <lable>>
		String filepath = "C:/Users/Naveen/Desktop/MLTrainer/qrels.adhoc.51-100.AP89.txt";
		//String filepath = "C:/Users/Naveen/Desktop/MLTrainer/TestQrels.txt";
		Path path = Paths.get(filepath);
		Scanner scanner = new Scanner(path);
		
		while(scanner.hasNextLine()){
			String lineItem[] = scanner.nextLine().split(" ");
			if(tempQIDHash.containsKey(Integer.parseInt(lineItem[0]))){
				List<Double> insertLabel = new ArrayList<Double>();
				insertLabel.add((double) Integer.parseInt(lineItem[3]));
				featureMatrix.put(lineItem[0]+"-"+lineItem[2], insertLabel);
			}
			
		}
		scanner.close();
		
		
		// add okapi Score for the FeatureMatrix <QID-DOCID, <lable, okapi>> -----------------------------
		//String filepath1 = "C:/Users/Naveen/Desktop/MLTrainer/TestOkapi.txt";
		String filepath1 = "C:/Users/Naveen/Desktop/MLTrainer/Okapi.txt";
		Path path1 = Paths.get(filepath1);
		Scanner scanner1 = new Scanner(path1);
		while(scanner1.hasNextLine()){
			String lineItem[] = scanner1.nextLine().split(" ");
			String keyQD = lineItem[0]+"-"+lineItem[2];
			//System.out.println(keyQD);
			if(featureMatrix.containsKey(keyQD)){
				List<Double> featureValueUpdate = featureMatrix.get(keyQD);
				featureValueUpdate.add(Double.parseDouble(lineItem[4]));
			}
		}
		scanner1.close();
		
		// if the doc does not contain any score then assign zero.
		for (Entry<String, List<Double>> entry : featureMatrix.entrySet()){
			
			List<Double> update = entry.getValue();
			String keyQD = entry.getKey();
			int length = update.size();
			if(length == 1){
				update.add(0.0);
			}
	      }
		
		
		// add BM25 Score for the FeatureMatrix <QID-DOCID, <lable, okapi, BM25>> -----------------------------
		//String filepath1 = "C:/Users/Naveen/Desktop/MLTrainer/TestOkapi.txt";
		String filepath2 = "C:/Users/Naveen/Desktop/MLTrainer/BM25.txt";
		Path path2 = Paths.get(filepath2);
		Scanner scanner2 = new Scanner(path2);
		while(scanner2.hasNextLine()){
			String lineItem[] = scanner2.nextLine().split(" ");
			String keyQD = lineItem[0]+"-"+lineItem[2];
			//System.out.println(keyQD);
			if(featureMatrix.containsKey(keyQD)){
				List<Double> featureValueUpdate = featureMatrix.get(keyQD);
				featureValueUpdate.add(Double.parseDouble(lineItem[4]));
			}
		}
		scanner2.close();
		
		// if the doc does not contain any score then assign zero.
		for (Entry<String, List<Double>> entry : featureMatrix.entrySet()){
			
			List<Double> update = entry.getValue();
			String keyQD = entry.getKey();
			int length = update.size();
			if(length == 2){
				update.add(0.0);
			}
	      }
	     
		
		
		// add TFIDF Score for the FeatureMatrix <QID-DOCID, <lable, okapi, BM25, TFIDF>> -----------------------------
		//String filepath1 = "C:/Users/Naveen/Desktop/MLTrainer/TestOkapi.txt";
		String filepath3 = "C:/Users/Naveen/Desktop/MLTrainer/TFIDF.txt";
		Path path3 = Paths.get(filepath3);
		Scanner scanner3 = new Scanner(path3);
		while(scanner3.hasNextLine()){
			String lineItem[] = scanner3.nextLine().split(" ");
			String keyQD = lineItem[0]+"-"+lineItem[2];
			//System.out.println(keyQD);
			if(featureMatrix.containsKey(keyQD)){
				List<Double> featureValueUpdate = featureMatrix.get(keyQD);
				featureValueUpdate.add(Double.parseDouble(lineItem[4]));
			}
		}
		scanner3.close();
		
		// if the doc does not contain any score then assign zero.
		for (Entry<String, List<Double>> entry : featureMatrix.entrySet()){
			
			List<Double> update = entry.getValue();
			String keyQD = entry.getKey();
			int length = update.size();
			if(length == 3){
				update.add(0.0);
			}
	      }
		
		
		// add TFIDF Score for the FeatureMatrix <QID-DOCID, <lable, okapi, BM25, TFIDF, UnigramLS>> -----------------------------
		//String filepath1 = "C:/Users/Naveen/Desktop/MLTrainer/TestOkapi.txt";
		String filepath4 = "C:/Users/Naveen/Desktop/MLTrainer/UnigramLS.txt";
		Path path4 = Paths.get(filepath4);
		Scanner scanner4 = new Scanner(path4);
		while(scanner4.hasNextLine()){
			String lineItem[] = scanner4.nextLine().split(" ");
			String keyQD = lineItem[0]+"-"+lineItem[2];
			//System.out.println(keyQD);
			if(featureMatrix.containsKey(keyQD)){
				List<Double> featureValueUpdate = featureMatrix.get(keyQD);
				featureValueUpdate.add(Double.parseDouble(lineItem[4]));
			}
		}
		scanner4.close();
		
		// if the doc does not contain any score then assign zero.
		for (Entry<String, List<Double>> entry : featureMatrix.entrySet()){
			
			List<Double> update = entry.getValue();
			String keyQD = entry.getKey();
			int length = update.size();
			if(length == 4){
				update.add(0.0);
			}
	      }
		
		
		
		// add TFIDF Score for the FeatureMatrix <QID-DOCID, <lable, okapi, BM25, TFIDF, UnigramLS, UnigramJMS>> -----------------------------
		//String filepath1 = "C:/Users/Naveen/Desktop/MLTrainer/TestOkapi.txt";
		String filepath5 = "C:/Users/Naveen/Desktop/MLTrainer/UnigramJMS.txt";
		Path path5 = Paths.get(filepath5);
		Scanner scanner5 = new Scanner(path5);
		while(scanner5.hasNextLine()){
			String lineItem[] = scanner5.nextLine().split(" ");
			String keyQD = lineItem[0]+"-"+lineItem[2];
			//System.out.println(keyQD);
			if(featureMatrix.containsKey(keyQD)){
				List<Double> featureValueUpdate = featureMatrix.get(keyQD);
				featureValueUpdate.add(Double.parseDouble(lineItem[4]));
			}
		}
		scanner5.close();
		
		// if the doc does not contain any score then assign zero.
		for (Entry<String, List<Double>> entry : featureMatrix.entrySet()){
			
			List<Double> update = entry.getValue();
			String keyQD = entry.getKey();
			int length = update.size();
			if(length == 5){
				update.add(0.0);
			}
	      }
		
		
		
		
		
		System.out.println("Count of the FeatureMatrix: "+featureMatrix.size());	
		System.out.println("value of 63 0 AP891103-0230:"+ featureMatrix.get("63-AP891103-0230"));
		
		return featureMatrix;
		
	}

	public static void printFeatureMatrix(LinkedHashMap<String, List<Double>> token, String filename) throws FileNotFoundException{
		
		PrintWriter writer = new PrintWriter("C:/Users/Naveen/Desktop/MLTrainer/"+filename);
		for (Entry<String, List<Double>> entry : token.entrySet())
	      {	  		
	  		writer.println(entry.getKey()+":"+entry.getValue());
	      }
	      writer.close();
		
//		System.out.println("Writing the fearure Matrix.......................");
//		for (String name: token.keySet()){
//            String key =name.toString();
//            String value = token.get(name).toString();  
//            System.out.println(key + " " + value);  
//		}
	}
	
	
	public static void test(){
		List<Double> tester = new ArrayList<Double>();
		tester.add(13.4565);
		tester.add(14.4546);
		tester.add(12.4663);
		Feature[][] first = new Feature[100][3];
		
		for(int i = 0; i < 100; i++){
			for(int j = 0; j < 3; j++){
				first[i][j] = new FeatureNode(j+1, tester.indexOf(j));
			}
		}
		
		
		//{new FeatureNode(1,tester.indexOf(0) ), new FeatureNode(2,tester.indexOf(1) ), new FeatureNode(3,tester.indexOf(2))};
		
		
		Feature[][] trainingData = {};
	}
	
	
	// sort the hashmap and put into linked hashmap to maintain the order.
	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order)
    {

        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Double>>()
        {
            public int compare(Entry<String, Double> o1,
                    Entry<String, Double> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        int rank = 1;
        // Maintaining insertion order with the help of LinkedList
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Entry<String, Double> entry : list)
        {
        	//if(rank<=1000){
        	
        		sortedMap.put(entry.getKey() +" "+ String.valueOf(rank), entry.getValue());
        		rank++;
        	//}
        	//else break;
        }
           
        //Map<string, Double> updatedRank = new LinkedHashMap<String, Double>();

        return sortedMap;
    }
	
	
	public static void printMap(Map<String, Double> map) throws FileNotFoundException, UnsupportedEncodingException
	  {
			
			PrintWriter writer = new PrintWriter("C:/Perl64/bin/regressionScore.txt", "UTF-8");
	      for (Entry<String, Double> entry : map.entrySet())
	      {
	  		
	  		writer.println(entry.getKey()+" "+entry.getValue()+" Exp");
	  		
	  		
	          //System.out.println("Key : " + entry.getKey() + " Value : "+ entry.getValue());
	      }
	      writer.close();
	  }
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		
			
		//Divide the feature Matrix to train and test HashMaps.
		// query ids to train the model
		int[] trainQid = {85, 59, 56, 71, 64, 62, 93, 99, 58, 77, 54, 87, 94, 100, 89, 91, 95, 68, 57, 97};
		// query ids to test or predict the model
		int[] testQid = {98, 60, 80, 63, 61};
		
		//load the actual queryIds to a tempHashMap
		LinkedHashMap<Integer, Integer> testQID = new LinkedHashMap<Integer, Integer>();
		
		
		Map<String, Double> rankedOutput = new LinkedHashMap<String, Double>();

		for(int i = 0; i < testQid.length; i++){
			
			testQID.put(testQid[i], 0);
		}
		
		//System.out.println(testQid.length);
		//System.out.println(testQID.size());
		
		LinkedHashMap<String, List<Double>> featureTrainMatrix = buildFeatureMatrix();
		
		LinkedHashMap<String, List<Double>> featureTestMatrix = new LinkedHashMap<String, List<Double>>();
		
		
		for(Iterator<Map.Entry<String, List<Double>>> it = featureTrainMatrix.entrySet().iterator(); it.hasNext(); ) {
		      Map.Entry<String, List<Double>> entry = it.next();
		      String[] keyQD = entry.getKey().split("-");
		      if(testQID.containsKey(Integer.parseInt(keyQD[0]))) {
		    	  
		    	  List<Double> insertIntoTest = entry.getValue();
		    	  
		    	  featureTestMatrix.put(keyQD[0]+"-"+keyQD[1]+"-"+keyQD[2], insertIntoTest);
		        it.remove();
		      }
		    }
		
		
		//printFeatureMatrix(featureTrainMatrix,"featureTrainMatrix.txt");
		//printFeatureMatrix(featureTestMatrix,"featureTestMatrix.txt");
		//System.out.println("Done");
		
		//Length of Test Matrix = 724
		int lengthOfTest = featureTestMatrix.size();
		int lengthOfTrain = featureTrainMatrix.size();
		System.out.println(featureTestMatrix.size());
		//Length of Train Matrix = 6734
		System.out.println(featureTrainMatrix.size());		
		//Count of the FeatureMatrix: 7458
		
		
		
		Problem problem = new Problem();
		
						
		Feature[][] value = new Feature[lengthOfTrain][5];
		
		int rcount = 0;
		for(Iterator<Map.Entry<String, List<Double>>> itl = featureTrainMatrix.entrySet().iterator(); itl.hasNext(); ) {
			Map.Entry<String, List<Double>> entry = itl.next();
			List<Double> testList = entry.getValue();
			for(int i = 0; i < testList.size()-1 ;  i++){
				value[rcount][i] = new FeatureNode(i+1, testList.get(i+1));
				//value[rcount][i] = testList.get(i+1);
				//value[rcount][i] = i+1;
			}
			rcount++;
		}
		
		// build the y value for the model--------------------------------------------------------------------
		double[] yvalue = new double[lengthOfTrain];
		int ycount =0;
		for(Iterator<Map.Entry<String, List<Double>>> it = featureTrainMatrix.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, List<Double>> entry = it.next();
			List<Double> testList = entry.getValue();
			yvalue[ycount] = testList.get(0);
			ycount++;
		}
		
		//-----------------------------------------------------------------------------------------------------------
		
		System.out.println("Successfully build yvalue and value matrix");

        

		
		problem.l = lengthOfTrain; 	// number of training examples
		problem.n = 5;		// number of features.
		problem.x = value; 		// feature nodes.
		problem.y = yvalue; 		// target values.
		
//		SolverType solver = SolverType.L1R_LR;
//		SolverType solver = SolverType.L2R_LR;
		SolverType solver = SolverType.L2R_L2LOSS_SVR; //0.1726
//		SolverType solver = SolverType.L2R_L1LOSS_SVR_DUAL; //0.16 
//		SolverType solver = SolverType.L2R_L2LOSS_SVR_DUAL; //0.16
//		SolverType solver = SolverType.MCSVM_CS; //0.14
//		SolverType solver = SolverType.L2R_L2LOSS_SVC; //0.14
		
		double C = 1.0;
		double eps = 0.001;
		
		Parameter parameter = new Parameter(solver, C, eps);
		Model model = Linear.train(problem, parameter);
		File modelFile = new File("C:/Users/Naveen/Desktop/MLTrainer/model");
		model.save(modelFile);
		System.out.println("file written");
		
		
		
		// test the model
		
		LinkedHashMap<String, Double> score = new LinkedHashMap<String, Double>();
		
		
//		for (Entry<Integer, Integer> entry1 : testQID.entrySet()){	  		
//	  		int qid = entry1.getKey();

			for(Iterator<Map.Entry<String, List<Double>>> itt = featureTrainMatrix.entrySet().iterator(); itt.hasNext(); ) {
				Map.Entry<String, List<Double>> entry = itt.next();
				String[] keyQD = entry.getKey().split("-");
				//if(qid == Integer.parseInt(keyQD[0])){
					
					List<Double> testList = entry.getValue();
					Feature[] instance = {new FeatureNode(1, testList.get(1)), new FeatureNode(2, testList.get(2)), new FeatureNode(3, testList.get(3))
					,new FeatureNode(4, testList.get(4)), new FeatureNode(5, testList.get(5))};
					
					double prediction = Linear.predict(model, instance);
					System.out.println(prediction);
					
					String insert = keyQD[0]+" Q0 "+keyQD[1]+"-"+keyQD[2];
					score.put(insert, prediction);
				//}
		
			}
			
			Map<String, Double> sortedMapDsc = sortByComparator(score, false);
			 for(Map.Entry<String, Double> entry2 : sortedMapDsc.entrySet()){			 
					 rankedOutput.put(entry2.getKey(), entry2.getValue());
					 
			//}
		
		}
		
		
		
		 printMap(rankedOutput);
			
	}

}


//
//
//for(int j=0; j<yvalue.length; j++){
//	System.out.println(yvalue[j]);
//}

// --------------------------------------------------------------------------------------------------

//LinkedHashMap<String, List<Double>> testMatrix = new LinkedHashMap<String, List<Double>>();
//
//List<Double> one = new ArrayList<Double>();
//one.add(0.0);
//one.add(12.3333);
//one.add(13.4444);
//one.add(14.5756);
//
//List<Double> two = new ArrayList<Double>();
//two.add(1.0);
//two.add(12.3333);
//two.add(13.4444);
//two.add(34.3455);
//
//List<Double> three = new ArrayList<Double>();
//three.add((Double)1.0);
//three.add(12.3333);
//three.add(13.4444);
//three.add(36.3425);
//
//testMatrix.put("one",one);
//testMatrix.put("two",two);
//testMatrix.put("three",three);
//
//
//
//double[][] value = new double[3][3];
//
//int rcount = 0;
//for(Iterator<Map.Entry<String, List<Double>>> itl = testMatrix.entrySet().iterator(); itl.hasNext(); ) {
//	Map.Entry<String, List<Double>> entry = itl.next();
//	List<Double> testList = entry.getValue();
//	for(int i = 0; i < testList.size()-1 ;  i++){
//		//value[rcount][i] = new FeatureNode(i+1, testList.get(i+1));
//		//value[rcount][i] = testList.get(i+1);
//		value[rcount][i] = i+1;
//	}
//	rcount++;
//}
//
//for(int k =0; k< 3; k++){
//	for(int l = 0; l< 3; l++){
//		System.out.println(value[k][l]);
//	}
//}
//
////Load the lable values to yvalue.
//double[] yvalue = new double[3];
//int i =0;
//for(Iterator<Map.Entry<String, List<Double>>> it = testMatrix.entrySet().iterator(); it.hasNext(); ) {
//	Map.Entry<String, List<Double>> entry = it.next();
//	List<Double> testList = entry.getValue();
//	yvalue[i] = testList.get(0);
//	i++;
//}
//
//
//for(int j=0; j<yvalue.length; j++){
//	System.out.println(yvalue[j]);
//}