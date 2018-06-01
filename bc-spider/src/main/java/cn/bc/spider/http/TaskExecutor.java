package cn.bc.spider.http;

import cn.bc.spider.Result;

import java.util.concurrent.*;

/**
 * 异步线程任务执行器
 *
 * @author dragon
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
