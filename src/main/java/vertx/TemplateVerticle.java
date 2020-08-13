package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

/**
 * @Auther ChenShuHong
 * @Date 2020-08-13 10:14
 * html解析 template
 */
public class TemplateVerticle extends AbstractVerticle {

  Router router ;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    router = Router.router(vertx);

    router.route("/").handler(req -> {
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
