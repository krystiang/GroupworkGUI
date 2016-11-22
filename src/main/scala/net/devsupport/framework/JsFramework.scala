package net.devsupport.framework

import scala.collection.mutable
import scalajs.js
import scalajs.js.JSON
import scalajs.js.annotation.JSExport
import scalajs.js.annotation.JSExportAll

import org.scalajs.dom.WebSocket
import org.scalajs.dom.raw.Event
import org.scalajs.dom.raw.MessageEvent

import net.devsupport.messaging.Envelope
import net.devsupport.messaging.Header
import net.devsupport.messaging.format._
import net.devsupport.messaging.serialization.util._
import net.devsupport.messaging.serialization.Serializer
import net.devsupport.messaging.serialization.Deserializer
import net.devsupport.messaging.json.NativeJsonRenderer
import net.devsupport.messaging.json.NativeJsonParser
import net.devsupport.middleware.groups.websocket.api._

@JSExport
class JsFramework(ip: String) { //ip = "ws://127.0.0.1:8080"
  import WebsocketGroupsSerialization._

  private val ws: WebSocket = new WebSocket(ip) //TODO IP / Stash
  private var open = false

  private var stash = List.empty[String]

  ws.onmessage = { msg: MessageEvent =>
    val obj = handleWebsocketMsg(msg.data.toString)
    //println("onmsg")
  }
  ws.onopen = { e: Event =>
    println("onopen", e)
    for (msg <- stash) {
      println("send from stash", msg)
      ws.send(msg)
    }
    stash = Nil
    open = true
  }
  ws.onclose = { e: Event =>
    println("onclose", e)
    open = false
  }

  private def send[T: Serializer](msg: T): Unit =
    if (open) ws.send(serialize(msg))
    else stash :+= serialize(msg)

  private def serialize[T: Serializer](msg: T): String = {
    val m = implicitly[Serializer[T]].serialize(msg)
    NativeJsonRenderer.stringify(m)
  }

  private def handleWebsocketMsg(s: String): Unit = {
    NativeJsonParser.parse(s) match {
      case ParsingSuccess(msgObj: MsgObject) =>
        WebsocketGroupsDeserializer.deserialize(msgObj) match {
          case ParsingSuccess(value) =>
            value match {
              case Envelope(Publish(group, msg), _) =>
                println("handle", value)
                val json = JSON.parse(msg)
                subscriptions.get(group).map(_(json))
              case _ =>             
            }
          case ParsingError(err) => //println("handle other", msgObj, err) //weiterleiten für andere APIs
        }
      case ParsingSuccess(msgObj: MsgValue) => //weiterleiten für andere APIs
      case ParsingError(err) => //Error kein Json
    }
  }

  @JSExport
  def publish(groupName: String, msg: js.Object): Unit = {
    send(Publish(groupName, JSON.stringify(msg)))
  }
  
  private val subscriptions = mutable.Map.empty[String, js.Function1[js.Any, Unit]]

  @JSExport
  def subscribe(groupName: String, f: js.Function1[js.Any, Unit]): Unit = {
    send(Subscribe(groupName))
    subscriptions += groupName -> f
  }
  
  @JSExport
  def unsubscribe(groupName: String): Unit = {
    send(Unsubscribe(groupName))
    subscriptions -= groupName
  }
}