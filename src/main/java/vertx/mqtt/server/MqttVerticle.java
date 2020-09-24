package vertx.mqtt.server;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-18 15:48
 */
public class MqttVerticle  extends AbstractVerticle {

  private Integer port =1883;

  final InternalLogger logger= Log4JLoggerFactory.getInstance(MqttVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    MqttServer mqttServer = MqttServer.create(vertx);
    mqttServer.endpointHandler(endpoint -> {
       logger.debug("MQTT client（客户连接） [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());
      //验证
      if (endpoint.auth() != null) {
        System.out.println("[username = " + endpoint.auth().getUsername() + ", password = " + endpoint.auth().getPassword() + "]");
      }
      if (endpoint.will() != null) {
        logger.debug("[（主题）will topic = " + endpoint.will().getWillTopic() +
          " QoS = " + endpoint.will().getWillQos() + " isRetain = " + endpoint.will().isWillRetain() + "]");
      }
      logger.debug("（保持超时）[keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "]");

      // 接受来自远程客户端的连接 accept connection from the remote client
      endpoint.accept(true);
      endpoint.disconnectHandler(v -> {
        logger.debug("（收到客户端断开连接） Received disconnect from client");
      });
      //处理客户端订阅请求
      endpoint.subscribeHandler(subscribe -> {
        List<MqttQoS> grantedQosLevels = new ArrayList<>();
        for (MqttTopicSubscription s: subscribe.topicSubscriptions()) {
          logger.debug("Subscription for（订阅） " + s.topicName() + " with QoS " + s.qualityOfService());
          grantedQosLevels.add(s.qualityOfService());
        }
        // （确认订阅请求）ack the subscriptions request
        endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);

      });
      //处理客户端取消订阅请求
      endpoint.unsubscribeHandler(unsubscribe -> {
        for (String t: unsubscribe.topics()) {
          logger.debug("（取消订阅）Unsubscription for " + t);
        }
        // （确认订阅请求） ack the subscriptions request
        endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
      });

      //处理客户端发布的消息
      endpoint.publishHandler(message -> {
        logger.debug("(客户端消息) [" + message.payload().toString(Charset.defaultCharset()) + "] with QoS [" + message.qosLevel() + "]");
        if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
          endpoint.publishAcknowledge(message.messageId());
        } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
          endpoint.publishReceived(message.messageId());
        }
        //向客户端发布消息
        endpoint.publish(message.topicName(),
          Buffer.buffer("Hello from the Vert.x MQTT server（来自服务器的回应）"+ message.payload().toString(Charset.defaultCharset())),
          MqttQoS.EXACTLY_ONCE,
          false,
          false);
      }).publishReleaseHandler(messageId -> {
        endpoint.publishComplete(messageId);
      });



      //（指定用于处理QoS 1和2的处理程序）specifing handlers for handling QoS 1 and 2
      endpoint.publishAcknowledgeHandler(messageId -> {
        logger.debug("Received ack for message （收到确认消息）= " +  messageId);
      }).publishReceivedHandler(messageId -> {
        endpoint.publishRelease(messageId);
      }).publishCompletionHandler(messageId -> {
        logger.debug("Received ack for message （收到确认消息）= " +  messageId);
      });

      //被客户通知保持生命
      endpoint.pingHandler(v -> {
        logger.debug("（保持生命）Ping received from client");
      });

    /*  mqttServer.close(v -> {
        logger.debug("（关闭服务器）MQTT server closed");
      });*/

    });


    //启动服务器
    mqttServer.listen(port,s -> {
        if(s.succeeded()){
          logger.debug("deploy verticle :{} success", port);
        }else {
          logger.error("deploy verticle :{} error", s.cause());
        }
      });



  }
}
