import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions

import io.vertx.core.*
import io.vertx.core.json.JsonObject

class MainVerticle : AbstractVerticle() {
  override fun start(promise: Promise<Void>) {
    try {
      val bootConfig: Config = ConfigFactory.load()
      val configList = bootConfig
        .getConfig(VERTX_BOOT_VERTICLES_PATH)
        .root()
        .keys
        .map { key: String -> bootConfig.getConfig("$VERTX_BOOT_VERTICLES_PATH.$key") }

      val futures = configList
        .map { config -> deployVerticle(config) }
      Future.all(futures).onComplete { ar ->
        if (ar.succeeded()) {
          promise.complete()
        } else {
          promise.fail(ar.cause())
        }
      }
    } catch (t: Throwable) {
      promise.fail(t)
    }
  }

  private fun deployVerticle(config: Config): Future<String?> {
    val promise = Promise.promise<String?>()
    try {
      val name: String = config.getString("name")
      val options = DeploymentOptions()
        .setInstances(getInstances(config))
        .setConfig(getConfig(config))
        .setHa(getHa(config))
        .setMaxWorkerExecuteTime(getMaxWorkerExecuteTime(config))
        .setWorker(getWorker(config))
        .setWorkerPoolName(getWorkerPoolName(config))
        .setWorkerPoolSize(getWorkerPoolSize(config))
      vertx.deployVerticle(name, options) { ar: AsyncResult<String?> ->
        if (ar.succeeded()) {
          promise.complete(ar.result())
        } else {
          promise.fail(ar.cause())
        }
      }
    } catch (t: Throwable) {
      promise.fail(t)
    }
    return promise.future()
  }

  private fun getWorkerPoolSize(config: Config): Int {
    return if (config.hasPath(WORKER_POOLSIZE_KEY)) {
      config.getInt(WORKER_POOLSIZE_KEY)
    } else 1
  }

  private fun getWorkerPoolName(config: Config): String? {
    return if (config.hasPath(WORKER_POOLNAME_KEY)) {
      config.getString(WORKER_POOLNAME_KEY)
    } else null
  }

  private fun getWorker(config: Config): Boolean {
    return if (config.hasPath(WORKER_KEY)) {
      config.getBoolean(WORKER_KEY)
    } else false
  }

  private fun getMaxWorkerExecuteTime(config: Config): Long {
    return if (config.hasPath(MAXWORKER_EXECTIME_KEY)) {
      config.getLong(MAXWORKER_EXECTIME_KEY)
    } else Long.MAX_VALUE
  }

  private fun getHa(config: Config): Boolean {
    return if (config.hasPath(HA_KEY)) {
      config.getBoolean(HA_KEY)
    } else false
  }

  private fun getConfig(config: Config): JsonObject {
    return if (config.hasPath(CONF_KEY)) {
      JsonObject(config.getValue(CONF_KEY).render(ConfigRenderOptions.concise()))
    } else JsonObject()
  }

  private fun getInstances(config: Config): Int {
    return if (config.hasPath(INSTANCES_KEY)) {
      config.getInt(INSTANCES_KEY)
    } else 1
  }

  companion object {
    private const val VERTX_BOOT_VERTICLES_PATH = "vertx-boot.verticles"
    private const val CONF_KEY = "configuration"
    private const val INSTANCES_KEY = "instances"
    private const val HA_KEY = "high-availability"
    private const val MAXWORKER_EXECTIME_KEY = "max-worker-execution-time"
    private const val WORKER_KEY = "worker"
    private const val WORKER_POOLNAME_KEY = "worker-pool-name"
    private const val WORKER_POOLSIZE_KEY = "worker-pool-size"
  }
}
