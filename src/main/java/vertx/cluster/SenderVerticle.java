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

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SenderVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception  {
    VertxOptions options = new VertxOptions();
    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();
        String hostAddress = null;
        try {
          hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
        options.getEventBusOptions().setHost(hostAddress).setClustered(true);
        EventBus eventBus = vertx.eventBus();
        System.out.println("We now have a clustered event bus: " + eventBus);
        JsonObject msg = new JsonObject().put("message_from_sender_verticle", "Hello, Consumer !");
        vertx.setPeriodic(3000, index -> {
          System.out.println("触发发送信息。。。。。");
          eventBus.request("receiver", msg, r -> {
            if (r.succeeded()) {
              JsonObject reply = (JsonObject) r.result().body();
              System.out.println("received reply: " + reply.getValue("reply"));
            }
          });
        });
      } else {

        System.out.println("Failed: " + res.cause());
      }});


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
