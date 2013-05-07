package cn.bc.spider.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import cn.bc.spider.Result;

/**
 * 异步线程任务执行器
 * 
 * @author dragon
 * 
 */
public class TaskExecutor {
	private static ExecutorService executorService = Executors
			.newCachedThreadPool();

	private TaskExecutor() {
	}

	/**
	 * 阻塞等待直至获取运行结果
	 * 
	 * @param callable
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static <V> Result<V> get(Callable<Result<V>> callable) {
		// 创建任务
		FutureTask<Result<V>> task = new FutureTask<Result<V>>(callable);

		// 执行任务
		executorService.execute(task);

		try {
			// 获取任务结果
			return task.get();
		} catch (Exception e) {
			// 封装异常
			return new Result<V>(e);
		}
	}
}
