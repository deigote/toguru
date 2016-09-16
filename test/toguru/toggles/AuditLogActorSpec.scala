package toguru.toggles

import akka.pattern.ask

import akka.actor.{ActorRef, Props}
import toguru.events.toggles.{GlobalRolloutCreated, ToggleCreated}
import toguru.toggles.AuditLogActor.GetLog

class AuditLogActorSpec extends ActorSpec {

  val events = List(
    ("toggle-1", ToggleCreated("toggle 1", "first toggle", Map("team" -> "Toguru team"))),
    ("toggle-1", GlobalRolloutCreated(20))
  )

  def createActor(events: Seq[(String, ToggleEvent)] = List.empty): ActorRef =
    system.actorOf(Props(new AuditLogActor((_,_) => (), events)))


  "audit log actor" should {
    "return current audit log" in {
      val actor = createActor(events)

      val response = await(actor ? GetLog)

      response mustBe events
    }

    "build audit log from events" in {
      val actor = createActor()

      val id1 = "toggle-1"
      val id2 = "toggle-2"

      actor ! events(1)
      actor ! events(0)

      val response = await(actor ? GetLog)

      response mustBe events
    }
  }
}