package vertx;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-13 11:44
 * 静态资源访问
 */
public class StaticVerticle  extends AbstractVerticle {
 final InternalLogger logger= Log4JLoggerFactory.getInstance(StaticVerticle.class);
  Router router ;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    router = Router.router(vertx);
    //启动static静态资源
    router.route("/static/*").handler(StaticHandler.create());
    //自定义static静态资源
    //router.route("/*").handler(StaticHandler.create());
    router.route("/").handler(req -> {
      logger.error("进来了");
      ThymeleafTemplateEngine thymeleafTemplateEngine = ThymeleafTemplateEngine.create(vertx);
      JsonObject jsonObject = new JsonObject();
      jsonObject.put("name","Hello word");
      thymeleafTemplateEngine.render(jsonObject,"templates/index.html",(r->{
        if(r.succeeded()){
          req.response()
            .putHeader("content-type", "text/html")
            .end(r.result());
        }else {
        }
      }));
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
