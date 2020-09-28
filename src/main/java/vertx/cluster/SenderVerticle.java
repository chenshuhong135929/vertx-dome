package vertx.cluster;

/**
 * @Auther ChenShuHong
 * @Date 2020-09-18 10:51
 */
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import vertx.start.RunApiGateway;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SenderVerticle extends AbstractVerticle {

  @Override
  public void start() throws UnknownHostException {
        EventBus eventBus = vertx.eventBus();
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        JsonObject msg = new JsonObject().put("message_from_sender_verticle", "Hello, Consumer !"+hostAddress);
        vertx.setPeriodic(3000, index -> {
          System.out.println(hostAddress+"--->>>  触发发送信息。。。。。");
          eventBus.request("receiver", msg, r -> {
            if (r.succeeded()) {
              JsonObject reply = (JsonObject) r.result().body();
              System.out.println("received reply: " + reply.getValue("reply"));
            }
          });
        });


 /*   EventBus eventBus = vertx.eventBus();
    JsonObject msg = new JsonObject().put("message_from_sender_verticle", "Hello, Consumer !");
    vertx.setPeriodic(3000, index -> {
      System.out.println("触发发送信息。。。。。");
      eventBus.request("receiver", msg, res -> {
        if (res.succeeded()) {
          JsonObject reply = (JsonObject) res.result().body();
          System.out.println("received reply: " + reply.getValue("reply"));
        }
      });
    });*/
  }
}
