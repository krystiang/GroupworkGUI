package net.devsupport.framework

object JsAgentProps {
  def apply(creator: => JsAgent) = new JsAgentProps(_ => creator)
}

class JsAgentProps private (creator: Unit => JsAgent) {
  private[framework] def create: JsAgent = creator()
}