package scheduler.tasks

import javax.inject.Inject
import javax.inject.Named

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

import org.apache.pekko.actor.{ActorRef, ActorSystem}

/**
 * Tâche pour planifier la création d'une tournée à un taux fixe en utilisant un acteur.
 *
 * @param actorSystem            Le système d'acteurs.
 * @param tourneeManagerActor    La référence de l'acteur gestionnaire de tournées.
 * @param executionContext       Le contexte d'exécution.
 */
class TourneeManagerTask @Inject()(actorSystem: ActorSystem,
                                   @Named("tourneeManager-actor") tourneeManagerActor: ActorRef)(
                                    implicit executionContext: ExecutionContext
                                  ) {
  // Calcul du délai initial pour commencer à 23h
  private val initialDelay = calculateInitialDelay()

  // Planifie l'exécution de la tâche à un taux fixe
  actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = initialDelay,
    interval = 1.day,
    receiver = tourneeManagerActor,
    message = "create-tournee"
  )

  /**
   * Calcule le délai initial pour que la première exécution de la tâche se produise à 23h.
   *
   * @return Le délai initial en tant que FiniteDuration.
   */
  private def calculateInitialDelay(): FiniteDuration = {
    val now = System.currentTimeMillis()
    // target = 23h
    val target = now - now % (24 * 60 * 60 * 1000) + 23 * 60 * 60 * 1000
    (target - now).milliseconds
  }
}
