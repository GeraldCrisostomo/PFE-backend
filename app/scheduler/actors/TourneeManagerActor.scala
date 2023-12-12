package scheduler.actors

import models.{TourneeCreationComplete, TourneeUpdateDate}
import org.apache.pekko.actor._
import services.{TourneeParDefautService, TourneeService}

import javax.inject.Inject

/**
 * Acteur responsable de la gestion automatique des tournées.
 *
 * @param tourneeService           Service pour la gestion des tournées.
 * @param tourneeParDefautService  Service pour la gestion des tournées par défaut.
 */
class TourneeManagerActor @Inject()(tourneeService: TourneeService,
                                    tourneeParDefautService: TourneeParDefautService) extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global

  /**
   * Définit le comportement de l'acteur lorsqu'il reçoit un message.
   *
   * @return Rien (Unit).
   */
  def receive: Receive = {
    case "create-tournee" =>
      // Récupérer toutes les tournées par défaut
      val futureTourneesParDefaut = tourneeParDefautService.getTourneesParDefaut
      futureTourneesParDefaut.foreach { tourneesParDefaut =>
        // Récupérer toutes les tournées du jour
        val futureTourneesDuJour = tourneeService.getTourneesByDate(java.time.LocalDate.now())
        futureTourneesDuJour.foreach { tourneesDuJour =>
          // Boucle sur les tournées par défaut
          for (tourneeParDefaut <- tourneesParDefaut) {
            // Vérifier si la tournée par défaut fait partie des tournées du jour
            val tourneeDuJourOption = tourneesDuJour.find(t => t.nom.equals(tourneeParDefaut.nom_par_defaut))

            val nouvelleTournee = TourneeCreationComplete(
              id_tournee_par_defaut = tourneeParDefaut.id_tournee_par_defaut,
              date = java.time.LocalDate.now().plusDays(1),
              nom = tourneeParDefaut.nom_par_defaut
            )
            // Pas de tournée par défaut présente, on en crée une
            if (tourneeDuJourOption.isEmpty){
              tourneeService.createTourneeWithNameAndAddCommandesParDefaut(nouvelleTournee)
            }
            else {
              tourneeDuJourOption.foreach { tourneeDuJour =>
                // Vérifier le statut de la tournée du jour
                if (!tourneeDuJour.statut.equals("terminée")) {
                  // Mettre à jour la date de la tournée du jour à demain
                  tourneeService.updateTourneeDate(
                    tourneeDuJour.id_tournee,
                    TourneeUpdateDate(tourneeDuJour.date.plusDays(1))
                  )
                } else {
                  // Créer une nouvelle tournée avec les informations de la tournée par défaut
                  tourneeService.createTourneeWithNameAndAddCommandesParDefaut(nouvelleTournee)
                }
              }
            }
          }
        }
      }
  }
}
