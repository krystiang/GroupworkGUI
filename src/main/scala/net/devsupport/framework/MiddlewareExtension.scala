package net.devsupport.framework

import scala.collection.mutable

//import scala.scalajs.js
import org.scalajs.dom.WebSocket
import org.scalajs.dom.raw.Event
import org.scalajs.dom.raw.MessageEvent

import net.devsupport.messaging.Header
import net.devsupport.messaging.format._
import net.devsupport.messaging.serialization.Serializer
import net.devsupport.messaging.serialization.Deserializer
import net.devsupport.messaging.json.NativeJsonRenderer
import net.devsupport.messaging.json.NativeJsonParser
import net.devsupport.middleware.groups.websocket.api._
import net.devsupport.messaging.serialization.util._

object MiddlewareExtension extends MiddlewareConnection with JsAgentSystem {
  import WebsocketGroupsSerialization._

  private val ws: WebSocket = new WebSocket("ws://127.0.0.1:8080") //TODO IP / Stash
  private var open = false
  
  private var stash = List.empty[String]

  ws.onmessage = { msg: MessageEvent =>
    val obj = handleWebsocketMsg(msg.data.toString)
    println("onmsg")
    //appendMsg(msg.data.toString)
  }
  ws.onopen = { e: Event =>
    println("onopen", e)
    for(msg <- stash) {
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
    if(open) ws.send(serialize(msg))
    else stash :+= serialize(msg)

  private def serialize[T: Serializer](msg: T): String = {
    val m = implicitly[Serializer[T]].serialize(msg)
    NativeJsonRenderer.stringify(m)
  }

  private def handleWebsocketMsg(s: String): Unit = {
    NativeJsonParser.parse(s) match {
      case ParsingSuccess(msgObj: MsgObject) =>
        WebsocketGroupsDeserializer.deserialize(msgObj) match {
          case ParsingSuccess(value) => println("handle", value)
          case ParsingError(err) => //println("handle other", msgObj, err) //weiterleiten für andere APIs
        }
      case ParsingSuccess(msgObj: MsgValue) => //weiterleiten für andere APIs
      case ParsingError(err) => //Error kein Json
    }
  }

  private def adjust[A, B](m: mutable.Map[A, B], k: A)(f: B => B) = m(k) = f(m(k))
  var subscriptions = mutable.Map.empty[String, List[Any => Unit]]

  def subscribe(group: String, agent: JsAgentRef, deserializer: Deserializer): Unit = {
    if (!subscriptions.contains(group)) {
      subscriptions += group -> List(agent.!)
      send(Subscribe(group))
    }
    adjust(subscriptions, group)(agent.! _ :: _)
  }

  def unsubscribe(group: String, actor: JsAgentRef): Unit = {
    if (subscriptions.contains(group)) {
      adjust(subscriptions, group)(_.filter(_ != actor))
      send(Unsubscribe(group))
    }
  }

  def publish[T](group: String, msg: T, header: Header, sender: JsAgentRef, serializer: Serializer[T]): Unit = {
    val msgString = serialize(msg)(serializer)
    send(Publish(group, msgString))
  }

  def agentOf(props: JsAgentProps, _name: String): JsAgentRef = {
    val ref = new JsAgentRef {
      def name = _name

      lazy val agent = props.create
      def !(msg: Any): Unit = agent.receive(msg)
    }
    JsAgentContext.contextStack ::= new JsAgentContext {
      val self = ref
    }
    ref.agent //create instance of agent
    ref
  }
}