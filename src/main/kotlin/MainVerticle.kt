import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.rxjava3.core.AbstractVerticle
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  override fun start(startFuture: Promise<Void>) {
    val options = DeploymentOptions().setWorker(true)
    logger.info("Deploying verticles!!!")
    vertx.deployVerticle("HttpServerVerticle", options).subscribe()
    vertx.deployVerticle("MessageStoreVerticle", options).subscribe()
    logger.info("Verticles deployed!!!")
  }
}
