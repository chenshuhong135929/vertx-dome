package vertx.mqtt.clien;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-18 16:05
 */
public class MqttClienVerticle  {

  private String url ="192.168.2.17";
  private Integer port =1883;

  public static void main(String[] args) {
    new MqttClienVerticle().startClien();
  }
  final InternalLogger logger= Log4JLoggerFactory.getInstance(MqttClienVerticle.class);
  public void startClien(){
    Vertx vertx = Vertx.vertx();
    MqttClientOptions mqttClientOptions = new MqttClientOptions();
    mqttClientOptions.setPassword("123456");
    mqttClientOptions.setUsername("123456");

    MqttClient client = MqttClient.create(vertx,mqttClientOptions);

    client.connect(port, url, s -> {
       if(s.succeeded()){
         logger.debug("connection verticle :{} success", url+port);
         mqttPublishCompletionHandler(client);
         mqttSubscribeCompletionHandler(client);
       }else {
         logger.error("connection verticle :{} error", s.cause());
       }
    });
    client.pingResponseHandler(s -> {
      //The handler will be called time to time by default
      logger.debug("（收到心跳消息 ）We have just received PINGRESP packet");
    });

  }

  /**
   * 订阅（订阅功能要在服务器启动成功后执行）
   * @param client
   * @param topic
   * @param qos
   */
  public void  mqttSubscribe(MqttClient client,String topic,Integer qos){
    client.subscribe(topic,qos);
  }

  /**
   * 收到通知
   * @param client
   */
  public void  mqttPublishCompletionHandler(MqttClient client){
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
  public void  mqttPublish(MqttClient client,String topic,String data,MqttQoS qos){
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
  public void  mqttSubscribeCompletionHandler(MqttClient client){
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
  public void  mqttUnsubscribeCompletionHandler(MqttClient client){
    client
      .unsubscribeCompletionHandler(id -> {
        logger.debug("刚收到的UNSUBACK数据包的ID为 " + id);
      })
      .subscribe("temp", 1)
      .unsubscribe("temp");
  }




}
