package net.devsupport.framework

import net.devsupport.messaging.serialization.Serializer
import net.devsupport.messaging.EmptyHeader

case class Group(name: String) {
  def ![T](msg: T)(implicit sender: JsAgentRef, serializer: Serializer[T]): Unit =
    MiddlewareExtension.publish(name, msg, EmptyHeader, sender, serializer) //TODO Header
}