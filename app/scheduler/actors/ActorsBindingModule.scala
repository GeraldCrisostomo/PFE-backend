package scheduler.actors

import com.google.inject.AbstractModule
import play.api.libs.concurrent.PekkoGuiceSupport

/**
 * Module Guice pour la liaison et la configuration des acteurs utilisés dans le planificateur.
 * Ce module lie TourneeManagerActor et le configure en tant qu'acteur avec le nom "tourneeManager-actor".
 */
class ActorsBindingModule extends AbstractModule with PekkoGuiceSupport {
  override def configure(): Unit = {
    bindActor[TourneeManagerActor]("tourneeManager-actor")
  }
}
