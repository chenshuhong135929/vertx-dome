package vertx.start;

import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import vertx.cluster.ReceiverVerticle;
import vertx.cluster.SenderVerticle;
import vertx.mqtt.server.MqttVerticle;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-19 14:43
 */
public class MainLauncher extends Launcher {

  public static void main(String[] args) {
    //修改为你所要启动的类就可以了
    //   new MainLauncher().dispatch(new String[] { "run", ReceiverVerticle.class.getName() });
    //   new MainLauncher().dispatch(new String[] { "run", SenderVerticle.class.getName() });

    try {
      RunApiGateway.start();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

}
