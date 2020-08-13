package vertx;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-13 15:06
 * 函数编程
 */
public class FuterPromiseVerticle extends AbstractVerticle {

    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);
    SqlConnection conn ;
    Router router ;
    MySQLConnectOptions connectOptions;
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        router = Router.router(vertx);
        router.route("/test").handler(req -> {
          this.getMysqlConfig().compose(mySQLPool ->this.getCon(mySQLPool).compose(conn->this.getRows(conn)).onSuccess(
            rows -> {
              List<JsonObject> list = new ArrayList<>();
              rows.forEach((r->{
                JsonObject jsonObject = new JsonObject();
                jsonObject.put("name",r.getValue("name"));
                jsonObject.put("password",r.getValue("password"));
                list.add(jsonObject);

              })
              );
              req.response()
                .putHeader("content-type", "application/json")
                .end(list.toString());
            }
          )
            //AsyncResult（捕获到异常的处理结果）
          .onFailure(throwable -> {
            req.response()
              .putHeader("content-type", "application/json")
              //异常直接返回
              .end(throwable.toString());
          })
          );

        });
        vertx.createHttpServer().requestHandler(router).listen(8888,(r->{
          if(r.succeeded()){
            startPromise.complete();
          }else{
            startPromise.fail(r.cause());
          }
        }));

  }

  //获取数据库连接
  private Future<SqlConnection>getCon( MySQLPool client){
      //异步调用（固定写法）
    Promise<SqlConnection> promise =Promise.promise();
    client.getConnection(ar1 -> {
      if (ar1.succeeded()) {
        System.out.println("Connected");
        SqlConnection conn = ar1.result();
        //这一步非常关键是固定写法
        promise.complete(conn);
      } else {
        //向上抛出异常
        promise.fail(ar1.cause());
      }
    });
    //这一步非常关键是固定写法
      return promise.future();
  }

  // 获取到的了解查询数据库

  private Future<RowSet<Row>> getRows(SqlConnection conn){
    Promise<RowSet<Row>> promise =Promise.promise();
    conn
      .query("SELECT * FROM user  ")
      .execute(ar2 -> {
        if (ar2.succeeded()) {
          conn.close();
          promise.complete(ar2.result());
        }else {
          promise.fail(ar2.cause());
        }
      });
    return promise.future();
  }

//获取mysql配置文件
  private Future<MySQLPool>getMysqlConfig(){
    Promise<MySQLPool> promise =Promise.promise();
    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(json -> {
      if(json.succeeded()){
    JsonObject result = json.result();
    connectOptions = new MySQLConnectOptions()
      .setPort(Integer.parseInt(result.getString("port")))
      .setHost(result.getString("host"))
      .setDatabase(result.getString("database"))
      .setUser(result.getString("user"))
      .setPassword(result.getString("password"));
        promise.complete(MySQLPool.pool(vertx,connectOptions, poolOptions));
      } else {
        promise.fail(json.cause());
      }
    });
      return promise.future();
  }

  }
