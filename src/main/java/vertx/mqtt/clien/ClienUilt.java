package vertx.mqtt.clien;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-19 15:22
 */
public class ClienUilt {

  static  final InternalLogger logger= Log4JLoggerFactory.getInstance(ClienUilt.class);
  /**
   * 订阅（订阅功能要在服务器启动成功后执行）
   * @param client
   * @param topic
   * @param qos
   */
  public static  void  mqttSubscribe(MqttClient client, String topic, Integer qos){
    client.subscribe(topic,qos);
  }

  /**
   * 收到通知
   * @param client
   */
  public static void  mqttPublishCompletionHandler(MqttClient client){
    client.publishCompletionHandler(id -> {
      logger.debug("刚收到的PUBACK或PUBCOMP数据包的ID为 " + id);
    })
      // 响应  (QoS 2)
      .publish("hello", Buffer.buffer(" 响应 2  hello"), MqttQoS.EXACTLY_ONCE, false, false)
      //响应  (QoS is 1)
      .publish("hello", Buffer.buffer("响应 1 hello"), MqttQoS.AT_LEAST_ONCE, false, false)
      // 下面的代码行不会触发，因为QoS值为0
      .publish("hello", Buffer.buffer("0  hello"), MqttQoS.AT_LEAST_ONCE, false, false);
  }


  /**
   * 发布消息（消息发布到主题）
   * @param client
   * @param topic
   * @param data
   * @param qos
   */
  public static void  mqttPublish(MqttClient client,String topic,String data,MqttQoS qos){
    client.publish(topic,
      Buffer.buffer(data),
      qos,
      false,
      false);
  }

  /**
   * 订阅完成
   * @param client
   */
  public static void  mqttSubscribeCompletionHandler(MqttClient client){
    client.subscribeCompletionHandler(mqttSubAckMessage -> {
      logger.debug("订阅完成刚刚收到的SUBACK数据包的ID为 " + mqttSubAckMessage.messageId());
      for (int s : mqttSubAckMessage.grantedQoSLevels()) {
        if (s == 0x80) {
          logger.error("Failure(失败)");
        } else {
          logger.debug("（成功）Success. Maximum QoS is " + s);
        }
      }
    })
      .subscribe("temp", 1)
      .subscribe("temp2", 2);
  }

  /**
   * 退订完成
   * @param client
   */
  public static  void  mqttUnsubscribeCompletionHandler(MqttClient client){
    client
      .unsubscribeCompletionHandler(id -> {
        logger.debug("刚收到的UNSUBACK数据包的ID为 " + id);
      })
      .subscribe("temp", 1)
      .unsubscribe("temp");
  }



}
