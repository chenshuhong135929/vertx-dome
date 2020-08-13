package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-12 17:03
 * body数据
 */
public class BodyVerticle extends AbstractVerticle {
  Router router ;
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    //form-data格式
    //请求中的content-type:application/x-www-form-urlencoded
    router.route("/test/form").handler(req -> {
      String  page = req.request().getFormAttribute("page");
      req.response()
        .putHeader("content-type", "text/plain")
        .end(page);
    });
//json 格式
    //请求中的content-type:application/json
    router.route("/test/json").handler(req -> {
      JsonObject page = req.getBodyAsJson();
      req.response()
        .putHeader("content-type", "text/plain")
        .end(page.toString());
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
