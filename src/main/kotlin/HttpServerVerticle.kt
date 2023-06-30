import io.reactivex.rxjava3.kotlin.subscribeBy
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.rxjava3.core.AbstractVerticle
import io.vertx.rxjava3.ext.web.Router
import io.vertx.rxjava3.ext.web.RoutingContext
import io.vertx.rxjava3.ext.web.handler.BodyHandler
import io.vertx.rxjava3.ext.web.handler.StaticHandler
import io.vertx.rxjava3.ext.web.handler.sockjs.SockJSHandler
import org.slf4j.LoggerFactory

class HttpServerVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(HttpServerVerticle::class.java)

  override fun start(startFuture: Promise<Void>) {
    val port = config().getString("http-port", "8080").toInt()
    logger.info("Trying to start a HTTP server on port $port")

    val router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.get("/api/messages").handler(this::getAllMessages)
    router.post("/api/messages").handler(this::postNewMessage)

    val sockJSHandler = SockJSHandler.create(vertx, SockJSHandlerOptions())
    val options = SockJSBridgeOptions()
    options.setInboundPermitteds(listOf(PermittedOptions()))
    options.setOutboundPermitteds(listOf(PermittedOptions()))
    sockJSHandler.bridge(options)
    router.route("/eventbus/*").handler(sockJSHandler)

    router.route().handler(StaticHandler.create().setCachingEnabled(false))

    vertx.createHttpServer().requestHandler { router.get() }.rxListen(port).subscribeBy(onSuccess = {
      logger.info("HTTP server running on port $port")
      startFuture.complete()
    }, onError = {
      logger.error("HTTP server could not be started on port $port", it)
      startFuture.fail(it)
    })
  }

  private val emptyJson = json { obj() }

  private fun getAllMessages(context: RoutingContext) {
    vertx.eventBus().rxRequest<JsonObject>("messages.get-all", emptyJson).subscribeBy(onSuccess = { reply ->
      context.response().setStatusCode(200).putHeader("Content-Type", "application/json").end(reply.body().encode())
    }, onError = { context.fail(500) })
  }

  private fun postNewMessage(context: RoutingContext) {
    val payload = context.body().asJsonObject()

    if (!payload.containsKey("author") || !payload.containsKey("content")) {
      context.fail(400)
      return
    }

    vertx.eventBus().rxRequest<JsonObject>("messages.store", payload).subscribeBy(onSuccess = { reply ->
      context.response().setStatusCode(201).putHeader("Content-Type", "application/json").end(reply.body().encode())
    }, onError = { context.fail(500) })
  }
}
