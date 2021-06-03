package vertx.cluster;

/**
 * @Auther ChenShuHong
 * @Date 2020-09-18 10:50
 */
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
public class ReceiverVerticle extends AbstractVerticle {

  @Override
  public void start(){
        EventBus eventBus = vertx.eventBus();
        MessageConsumer<JsonObject> consumer = eventBus.consumer("receiver");
        consumer.handler(message -> {
          JsonObject jsonMessage = message.body();
          System.out.println("收到信息。。。"+jsonMessage.getValue("message_from_sender_verticle"));
          JsonObject jsonReply = new JsonObject().put("reply", "666 !");
          message.reply(jsonReply);
        });
  }
}
