package vertx.mqtt.concurrent;

import sun.nio.ch.ThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-21 10:04
 */
public class FutureTest {

  //创建一个线程池
  private ExecutorService executor = Executors.newSingleThreadExecutor();

  public Future<Integer> calculate(Integer input) {
    return executor.submit(() -> {
      Thread.sleep(1000);
      return input * input;
    });
  }


}


