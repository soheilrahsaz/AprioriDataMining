package com.Test;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
	    DataSource ds = new DataSource(Path.of("E:\\Uni\\term8\\data mining\\Proj1\\Project1 - groceries.csv"));
//	    DataSource ds = new DataSource(Path.of("E:\\Uni\\term8\\data mining\\Proj1\\test.csv"));
	    Arules arules = new Arules(0.005d,0.2d, ds);
//	    Arules arules = new Arules(0.222d,0.2d, ds);

		long start = System.currentTimeMillis();
		for (int i = 0; i < 10; i++)
		{
			arules.getFrequentItemSets();
		}
		System.out.println(System.currentTimeMillis() - start);
    }
}
