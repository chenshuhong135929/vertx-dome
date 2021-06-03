package vertx.start;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import vertx.mqtt.clien.ClienUilt;

import java.nio.charset.Charset;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-19 15:10
 */
public class MainClientDome extends AbstractVerticle {
  /**
   * http://localhost:8888/mqttSubscribe?topic=ddd&qos=2
   * http://localhost:8888/mqttPublish?topic=ddd&qos=2&data=HOLLEO
   */

  final InternalLogger logger= Log4JLoggerFactory.getInstance(MainClientDome.class);
  private String url ="192.168.2.17";
  private Integer port =1883;
  Router router;
  Vertx clienvertx;
  MqttClient client;
  @Override
  public void start(Promise<Void> startPromise) {
    router = Router.router(vertx);

    //启动客户端
    startClien();
    HttpServer httpServer = vertx.createHttpServer().requestHandler(router);
    httpServer.listen(8888, r -> {
      if (r.succeeded()) {
        System.out.println("启动成功！！！");
      }
    });
    router.route("/mqttSubscribe").handler(r -> {
      String topic = r.request().getParam("topic");
      String qos = r.request().getParam("qos");
      ClienUilt.mqttSubscribe(client,topic,Integer.parseInt(qos));
      r.response()
        .putHeader("content-type", "text/plain")
        .end("订阅成功");
    });
    router.route("/mqttPublish").handler(r -> {
      String topic = r.request().getParam("topic");
      String qos = r.request().getParam("qos");
      String data = r.request().getParam("data");

      ClienUilt.mqttPublish(client,topic,data, MqttQoS.valueOf(Integer.parseInt(qos)));
      r.response()
        .putHeader("content-type", "text/plain")
        .end("发送消息成功");
    });
  }


  public void startClien() {

    clienvertx = Vertx.vertx();
    MqttClientOptions mqttClientOptions = new MqttClientOptions();
    mqttClientOptions.setPassword("123456");
    mqttClientOptions.setUsername("123456");
    client = MqttClient.create(clienvertx, mqttClientOptions);
    client.connect(port, url, s -> {
      if (s.succeeded()) {
        logger.debug("connection verticle :{} success", url + port);
        ClienUilt.mqttPublishCompletionHandler(client);

        client.publishHandler(message->{
          logger.debug(message.topicName()+"(服务端消息) [" + message.payload().toString(Charset.defaultCharset()) + "]   QoS [" + message.qosLevel() + "]");

        });
      } else {
        logger.error("connection verticle :{} error", s.cause());
      }
    });
    client.pingResponseHandler(s -> {
      //The handler will be called time to time by default
      logger.debug("（收到心跳消息 ）We have just received PINGRESP packet");
    });

  }
}
