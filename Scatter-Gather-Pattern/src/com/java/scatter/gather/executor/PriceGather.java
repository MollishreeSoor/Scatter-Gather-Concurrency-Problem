package com.java.scatter.gather.executor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PriceGather {
	ExecutorService threadPool = Executors.newFixedThreadPool(4);
	Future<Integer> taskfuture1 = null;
	Future<Integer> taskfuture2 = null;
	Future<Integer> taskfuture3 = null;

	public static void main(String[] args) {
		PriceGather priceGather = new PriceGather();
		try {
			Set<Integer> prices = priceGather.getPrices(1200);
			System.out.println("Price List");
			for (Integer price : prices) {
				System.out.println(price);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Set<Integer> getPrices(int productId) throws InterruptedException,
			ExecutionException {
		Set<Integer> prices = Collections
				.synchronizedSet(new HashSet<Integer>());
		taskfuture1 = threadPool.submit(new Task(
				"http://localhost:8080/amazon/", productId));
		taskfuture2 = threadPool.submit(new Task(
				"http://localhost:8080/flipkart/", productId));
		taskfuture3 = threadPool.submit(new Task(
				"http://localhost:8080/myntra/", productId));

		threadPool.awaitTermination(5, TimeUnit.SECONDS);
		if (taskfuture1.isDone()) {
			prices.add(taskfuture1.get());
		}
		if (taskfuture2.isDone()) {
			prices.add(taskfuture2.get());
		}
		if (taskfuture3.isDone()) {
			prices.add(taskfuture3.get());
		}

		threadPool.shutdownNow();
		return prices;
	}
}
