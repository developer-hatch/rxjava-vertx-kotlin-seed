import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.rxjava3.core.AbstractVerticle
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  override fun start(startFuture: Promise<Void>) {
    val optionsV = DeploymentOptions().setWorker(true)
    vertx.deployVerticle("HttpServerVerticle", optionsV).subscribe()
    vertx.deployVerticle("MessageStoreVerticle", optionsV).subscribe()
  }
}
