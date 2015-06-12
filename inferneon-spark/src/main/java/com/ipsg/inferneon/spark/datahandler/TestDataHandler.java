package com.ipsg.inferneon.spark.datahandler;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.DoubleFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

class AvgCount implements Serializable {
	
	private int total;
	private int num;

	public int getTotal() {
		return total;
	}

	public int getNum() {
		return num;
	}

	public AvgCount(int total, int num){
		this.total = total;
		this.num = num;
	}

	public double avg(){
		return total / (double) num;
	}
	
	public void setTotal(int total){
		this.total = total;
	}
	
	public void setNum(int num){
		this.num = num;
	}
}

public class TestDataHandler implements Serializable {

	public static void main(String[] args){
		TestDataHandler test = new TestDataHandler();

		//test.testSampleSpark1();
		test.testSampleSpark2();
	}

	private void testSampleSpark2() {

		Function2<AvgCount, Integer, AvgCount> addAndCount = 
				new Function2<AvgCount, Integer, AvgCount>() {

			public AvgCount call(AvgCount a, Integer x)
					throws Exception {
				a.setTotal(a.getTotal() + x);
				a.setNum(a.getNum() + 1);
				return a;
			}
		};
		
		Function2<AvgCount, AvgCount, AvgCount> combine = 
				new Function2<AvgCount, AvgCount, AvgCount>() {

			public AvgCount call(AvgCount a, AvgCount b)
					throws Exception {
				a.setTotal(a.getTotal() + b.getTotal());
				a.setNum(a.getNum() + b.getNum());
				return a;
			}
		};
		
		System.setProperty("hadoop.home.dir", "d:\\winutil\\");

		JavaSparkContext javaSparkContext = new JavaSparkContext("local[2]", "First Spark App");

		JavaRDD<String> origData = javaSparkContext.textFile("Sample1.csv");
		
		JavaRDD<Integer> integerRdd = origData.map(new Function<String, Integer>() {

			public Integer call(String record) throws Exception {
				String[] fields = record.split(",");
				return Integer.parseInt(fields[2].trim());
			}
		});
		

		AvgCount initial = new AvgCount(0, 0);
		
		AvgCount result = integerRdd.aggregate(initial, addAndCount, combine);
		
		System.out.println("Average = " + result.avg());
	}

	private void testSampleSpark1() {

		System.setProperty("hadoop.home.dir", "d:\\winutil\\");

		JavaSparkContext javaSparkContext = new JavaSparkContext("local[2]", "First Spark App");

		JavaRDD<String> origData = javaSparkContext.textFile("Sample1.csv");

		JavaRDD<String[]> data = origData.map(new Function<String, String[]> (){
			public String[] call(String text) throws Exception {
				return text.split(",");
			}			
		});

		long numPurchases = data.count();

		long uniqueUsers = data.map(new Function<String[], String>(){
			public String call(String[] fields) throws Exception {
				return fields[0];
			}	
		}).distinct().count();

		double totalRevenue = data.mapToDouble(new DoubleFunction<String[]>(){

			public double call(String[] fields) throws Exception {
				return Double.parseDouble(fields[2]);
			}
		}).sum();

		List<Tuple2<String, Integer>> pairs = data.mapToPair(new PairFunction<String[], String, Integer>(){

			public Tuple2<String, Integer> call(String[] fields) throws Exception {
				return new Tuple2<String, Integer>(fields[1], 1);
			}

		}).reduceByKey(new Function2<Integer, Integer, Integer>(){
			public Integer call(Integer arg0, Integer arg1) throws Exception {
				return arg0 + arg1;
			}
		}).collect();

		Collections.sort(pairs, new Comparator<Tuple2<String, Integer>>(){

			public int compare(Tuple2<String, Integer> o1,
					Tuple2<String, Integer> o2) {
				return -(o1._2() - o2._2());
			}			
		});

		String mostPopular = pairs.get(0)._1();
		int purchases =  pairs.get(0)._2();

		System.out.println("Total purchases : " + numPurchases);
		System.out.println("Unique users : " + uniqueUsers);
		System.out.println("Total revenue : " + totalRevenue);
		System.out.println(String.format("Most popular product: %s with %d purchases", mostPopular, purchases));
	}
}
