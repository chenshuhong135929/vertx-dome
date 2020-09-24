package vertx.cluster;

/**
 * @Auther ChenShuHong
 * @Date 2020-09-18 10:50
 */
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.impl.clustered.ClusteredEventBus;
import io.vertx.core.eventbus.impl.clustered.ClusteredMessage;
import io.vertx.core.json.JsonObject;

import java.net.InetAddress;

public class ReceiverVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    VertxOptions options = new VertxOptions();

    // 本机局域网Ip
    String hostAddress = InetAddress.getLocalHost().getHostAddress();
    options.getEventBusOptions().setHost(hostAddress).setClustered(true);

    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();
        EventBus eventBus = vertx.eventBus();
        System.out.println("We now have a clustered event bus: " + eventBus);
        MessageConsumer<JsonObject> consumer = eventBus.consumer("receiver");
        consumer.handler(message -> {
          JsonObject jsonMessage = message.body();
          System.out.println("收到信息。。。"+jsonMessage.getValue("message_from_sender_verticle"));
          JsonObject jsonReply = new JsonObject().put("reply", "666 !");
          message.reply(jsonReply);
        });
      } else {

        System.out.println("Failed: " + res.cause());
      }});


   /* EventBus eventBus = vertx.eventBus();

    MessageConsumer<JsonObject> consumer = eventBus.consumer("receiver");
    consumer.handler(message -> {
      JsonObject jsonMessage = message.body();
      System.out.println("收到信息。。。"+jsonMessage.getValue("message_from_sender_verticle"));
      JsonObject jsonReply = new JsonObject().put("reply", "666 !");
      message.reply(jsonReply);
    });*/
  }
}
