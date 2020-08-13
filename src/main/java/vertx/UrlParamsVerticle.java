package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-12 16:39
 * 参数
 */
public class UrlParamsVerticle extends AbstractVerticle {
  Router router ;
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    router = Router.router(vertx);
//经典模式
    router.route("/test").handler(req -> {
     String  page = req.request().getParam("page");
      req.response()
        .putHeader("content-type", "text/plain")
        .end(page);
    });
//rest 风格
      router.route("/test/:page").handler(req -> {
      String  page = req.request().getParam("page");
      req.response()
        .putHeader("content-type", "text/plain")
        .end(page);
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
