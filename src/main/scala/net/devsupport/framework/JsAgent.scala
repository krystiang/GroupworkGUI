package net.devsupport.framework

import net.devsupport.messaging.serialization.Serializer
import net.devsupport.messaging.serialization.Deserializer

case class JsAgentInfo(name: String)

trait JsFrameworkAgent extends MiddlewareConnectionCreationSupport {

  private val middleware = createMiddleware
  val context = {
    val contextStack = JsAgentContext.contextStack
    if(contextStack.isEmpty)
      throw new RuntimeException(
          s"You cannot create an instance of [${getClass.getName}] explicitly using the constructor (new)." +
          "You have to use the actorOf factory method.")
    contextStack.head
  }
  implicit final val self = context.self

  def subscribe(group: Group)(implicit deserializer: Deserializer): Unit =
    middleware.subscribe(group.name, self, deserializer)

  def unsubscribe(group: Group): Unit =
    middleware.unsubscribe(group.name, self)

  def preStart: Unit = ()
  def postStop: Unit = ()

  type Receive = PartialFunction[Any, Unit]
  def receive: Receive

  protected def handleNotDeserializableMsg(msg: String, group: Group, error: String): Unit =
    println("can not deserialize message {} from group {} - {}", msg, group, error)
}

trait JsAgent extends JsFrameworkAgent {
  def createMiddleware = MiddlewareExtension
}