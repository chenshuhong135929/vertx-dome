package vertx.start;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import vertx.cluster.ReceiverVerticle;
import vertx.cluster.SenderVerticle;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 以编码方式启动群集化MainVerticle，
 *
 * @author lhx
 *
 */
public class RunApiGateway {

  public static void start() throws UnknownHostException {
 /*   // Hazelcast配置类
    Config cfg = new Config();
    // 关闭UDP组播，采用TCP进行集群通信。
    JoinConfig joinConfig = cfg.getNetworkConfig().getJoin();
    joinConfig.getMulticastConfig().setEnabled(false);
    joinConfig.getTcpIpConfig().setEnabled(true);
    joinConfig.getTcpIpConfig().addMember("192.168.1.136,192.168.1.137,192.168.2.17");// 有多个目标节点，就需要写多少地址。

    // 这里指定所用通信的网卡（在本机多个网卡时如不指定会有问题，无论有无多个网卡最好设置一下。）
    cfg.getNetworkConfig().getInterfaces().setEnabled(true);
    cfg.getNetworkConfig().getInterfaces().addInterface("192.168.1.*");
    cfg.getNetworkConfig().getInterfaces().addInterface("192.168.2.*");
    ClusterManager mgr = new HazelcastClusterManager(cfg);
*/
    // 申明集群管理器
    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions options = new VertxOptions().setClusterManager(mgr);
    options.getEventBusOptions().setClustered(true);
    String hostAddress = InetAddress.getLocalHost().getHostAddress();
    System.out.println("ip ------------------------------------------------------------->地址   "+hostAddress);
    options.getEventBusOptions().setHost(hostAddress);//这个一定要设置（在本机有多个网卡的时候，如果不设置，会收不到消息。）

    // 集群化vertx
    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();
        //vertx.deployVerticle(SenderVerticle.class.getName());
         vertx.deployVerticle(ReceiverVerticle.class.getName());
         System.out.println("Api Gateway : cluster succeeded");
      } else {
        res.cause().printStackTrace();
      }
    });


  }

}
