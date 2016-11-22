package net.devsupport.framework

object JsAgentContext {
  private[framework] var contextStack: List[JsAgentContext] = Nil
}

trait JsAgentContext {
  def self: JsAgentRef
}