package com.Test;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
	public static final boolean CACHE_ITEM_DATA_SOURCE = true;

    public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
	    DataSource ds = new DataSource(Path.of("E:\\Uni\\term8\\data mining\\Proj1\\Project1 - groceries.csv"));
//	    DataSource ds = new DataSource(Path.of("E:\\Uni\\term8\\data mining\\Proj1\\test.csv"));
	    Arules arules = new Arules(0.005d,0.2d, ds);
//	    Arules arules = new Arules(0.222d,0.2d, ds);

//		for (int i = 0; i < 20; i++)
//		{
//			arules.getFrequentItemSets();
//		}
//		System.out.println((System.currentTimeMillis() - start)/(20 * 1000.0) + "s");

		arules.getFrequentItemSets();
		arules.getArules();
		arules.printResult();
		System.out.println((System.currentTimeMillis() - start)/(1000.0) + "s");

    }
}
