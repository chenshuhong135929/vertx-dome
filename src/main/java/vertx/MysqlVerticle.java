package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-12 17:16
 * 操作数据库
 */
public class MysqlVerticle extends AbstractVerticle {

  MySQLConnectOptions connectOptions = new MySQLConnectOptions()
    .setPort(3306)
    .setHost("192.168.2.17")
    .setDatabase("db1")
    .setUser("root")
    .setPassword("root");

  // Pool options
  PoolOptions poolOptions = new PoolOptions()
    .setMaxSize(5);

  // Create the client pool
  MySQLPool client;
  SqlConnection conn ;
    Router router ;
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    router = Router.router(vertx);
    client = MySQLPool.pool(vertx,connectOptions, poolOptions);
    client.getConnection(ar1 -> {
      if (ar1.succeeded()) {
          conn = ar1.result();
        System.out.println("数据库连接成功！！！");
      } else {
        System.out.println("Could not connect: " + ar1.cause().getMessage());
      }
    });
    router.route("/test").handler(req -> {
      conn
        .query("SELECT * FROM user  ")
        .execute(ar2 -> {
          if (ar2.succeeded()) {
            conn.close();
            List<JsonObject> list = new ArrayList<>();
            ar2.result().forEach((r->{
              JsonObject jsonObject = new JsonObject();
              jsonObject.put("name",r.getValue("name"));
              jsonObject.put("password",r.getValue("password"));
              list.add(jsonObject);
            }));
            req.response()
              .putHeader("content-type", "application/json")
              .end(list.toString());

          } else {
            conn.close();
          }
        });

    });
    vertx.createHttpServer().requestHandler(router).listen(8888,(r->{
      if(r.succeeded()){
        startPromise.complete();
      }else{
        startPromise.fail(r.cause());
      }
    }));

  }
}
