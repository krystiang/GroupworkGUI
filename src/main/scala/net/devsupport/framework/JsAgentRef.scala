package net.devsupport.framework

trait JsAgentRef {
  def name: String
  def !(msg: Any): Unit
}