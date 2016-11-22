package net.devsupport.framework

import net.devsupport.messaging.Header
import net.devsupport.messaging.serialization.Serializer
import net.devsupport.messaging.serialization.Deserializer

trait MiddlewareConnection {
  def subscribe(group: String, actor: JsAgentRef, deserializer: Deserializer): Unit
  def unsubscribe(group: String, actor: JsAgentRef): Unit
  def publish[T](group: String, msg: T, header: Header, sender: JsAgentRef, serializer: Serializer[T])
}

trait MiddlewareConnectionCreationSupport {
  def createMiddleware: MiddlewareConnection
}

trait JsAgentSystem {
  def agentOf(props: JsAgentProps, name: String): JsAgentRef
}