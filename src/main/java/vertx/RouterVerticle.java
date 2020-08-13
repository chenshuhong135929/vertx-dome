package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-12 16:31
 * web
 */
public class RouterVerticle extends AbstractVerticle {
  Router router ;
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    router = Router.router(vertx);

    router.route("/").handler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    });
    vertx.createHttpServer().requestHandler(router).listen(8888,(r->{
      if (r.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(r.cause());
      }
    }));
  }
}
