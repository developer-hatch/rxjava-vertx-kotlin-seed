import io.reactivex.rxjava3.kotlin.subscribeBy
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.rxjava3.core.AbstractVerticle
import io.vertx.rxjava3.core.eventbus.Message
import io.vertx.rxjava3.ext.mongo.MongoClient
import org.slf4j.LoggerFactory
import java.time.Instant

private const val MONGO_COLLECTION = "messages"

class MessageStoreVerticle : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(MessageStoreVerticle::class.java)

  private lateinit var client: MongoClient

  override fun start() {
    val host = config().getString("mongo-host")
    val port = config().getString("mongo-port").toInt()
    val dbname = config().getString("mongo-db-name")

    logger.info("Starting the message store with MongoDB host=$host, port=$port")

    client = MongoClient.createShared(vertx, json {
      obj(
        "host" to host,
        "port" to port,
        "db_name" to dbname,
        "useObjectId" to true
      )
    })

    vertx.eventBus().consumer("messages.store", this::store)
    vertx.eventBus().consumer("messages.get-all", this::all)

    logger.info("Connected to MongoDB")
  }

  private fun store(cmd: Message<JsonObject>) {
    val now = Instant.now().epochSecond
    val documentWithTimestamp = cmd.body().copy().put("timestamp", System.currentTimeMillis())
    client.rxInsert(MONGO_COLLECTION, documentWithTimestamp)
      .subscribeBy(
        onSuccess = { id ->
          logger.info("Stored: id=${id} - ${cmd.body().encode()}")
          cmd.reply(json { obj("id" to id) })
          vertx.eventBus().publish("events.new-message", json {
            obj(
              "id" to id,
              "author" to cmd.body().getString("author"),
              "content" to cmd.body().getString("content"),
              "timestamp" to now
            )
          })
        },
        onError = { err ->
          logger.error("Could not store ${cmd.body().encode()}", err.cause)
          cmd.fail(1, err.message)
        })
  }

  private fun all(cmd: Message<JsonObject>) {
    client.rxFind(MONGO_COLLECTION, JsonObject())
      .map { jsonObjects ->
        jsonObjects.map { obj ->
          json {
            obj(
              "id" to obj.getString("_id"),
              "author" to obj.getString("author", "???"),
              "content" to obj.getString("content", ""),
              "timestamp" to obj.getLong("timestamp", System.currentTimeMillis())
            )
          }
        }
      }
      .map { json { obj("messages" to it) } }
      .subscribeBy(
        onSuccess = { messages ->
          logger.info("Retrieved ${messages.getJsonArray("messages").size()} messages")
          cmd.reply(messages)
        },
        onError = { err ->
          logger.error("Could not retrieve messages", err)
          cmd.fail(2, err.message)
        })
  }
}
