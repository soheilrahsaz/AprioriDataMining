package com.Test;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
	public static final boolean CACHE_ITEM_DATA_SOURCE = true;

    public static void main(String[] args) throws IOException {
    	String filePath = "E:\\Uni\\term8\\data mining\\Proj1\\Project1 - groceries.csv";
    	double minSupport = 0.005;
		double minConfidence = 0.2;
    	boolean generateRules = false;

    	if(args.length > 0)
		{
			try {

				generateRules = Boolean.parseBoolean(args[0]);

				if(args.length > 1)
				{
					minSupport = Double.parseDouble(args[1]);
				}

				if(args.length > 2)
				{
					minConfidence = Double.parseDouble(args[2]);
				}

				if(args.length > 3)
				{
					filePath = args[3];
				}
			}catch (Exception e)
			{
				System.out.println("Bad Parameter Input");
			}
		}
		System.out.println("Running Apriori from " + filePath + "\nminSupport: " + minSupport + ", minConfidence: " + minConfidence);

//		long start = System.currentTimeMillis();
//		for (int i = 0; i < 20; i++)
//		{
//			DataSource ds = new DataSource(Path.of(filePath));
//			Arules arules = new Arules(minSupport, minConfidence, ds);
//			arules.getFrequentItemSets();
//		}
//		System.out.println((System.currentTimeMillis() - start)/(20 * 1000.0) + "s");

		DataSource ds = new DataSource(Path.of(filePath));
		Arules arules = new Arules(minSupport, minConfidence, ds);
		arules.getFrequentItemSets();
		if(generateRules)
		{
//			arules.printFrequentPatterns(10);
			arules.getArules();
			arules.printRules();
		}

//		for (int i = 0; i < 40; i++)
//		{
//			Arules arules = new Arules(minSupport, minConfidence, ds);
//			arules.getFrequentItemSets();
//			arules.getArules();
//			arules.printStaticResult();
//
//			minConfidence+= 0.01;
//		}

    }
}
