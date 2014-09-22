package com.kcd.orange;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TestBlockingQueue
{
	public static void main(String[] args) throws Exception
	{
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(50);

		Thread producer = new Thread(new Producer(queue));
		producer.setName("생산자");
		producer.start();

		for(int i = 0; i < 5; i++)
		{
			Thread consum = new Thread(new Consumer(queue));
			consum.setName("소비자" + (i + 1));
			consum.start();
		}
	}

	public static class Producer implements Runnable
	{
		private BlockingQueue<String> queue;

		public Producer(BlockingQueue<String> queue)
		{
			this.queue = queue;
		}

		public void run()
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

			while(true)
			{
				try
				{
					queue.put(dateFormat.format(new Date()));

					System.out.printf("[%s] : size = %d \n", Thread.currentThread().getName(), queue.size());

					Thread.sleep(200);					
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static class Consumer implements Runnable
	{
		private BlockingQueue<String> queue;

		public Consumer(BlockingQueue<String> queue)
		{
			this.queue = queue;
		}

		public void run()
		{
			while(true)
			{
				try
				{
					System.out.printf("[%s] : %s \n", Thread.currentThread().getName(), queue.take());

					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}