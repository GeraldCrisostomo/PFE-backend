package models

import play.api.libs.json.{Json, OFormat}
import java.time.LocalDate

/**
 * Représente une tournée avec son identifiant, la date de la tournée, l'identifiant du livreur
 * (optionnel), le nom du livreur (optionnel), le statut de la tournée.
 *
 * @param id_tournee   L'identifiant unique de la tournée.
 * @param date         La date de la tournée.
 * @param id_livreur   L'identifiant unique du livreur associé à la tournée (optionnel).
 * @param nom          Le nom du livreur associé à la tournée (optionnel).
 * @param statut       Le statut de la tournée.
 */
case class Tournee(
                    id_tournee: Long,
                    date: LocalDate,
                    id_livreur: Option[Long],
                    nom: Option[String],
                    statut: String
                  )

/**
 * Représente une tournée avec des détails supplémentaires sur le livreur.
 *
 * @param id_tournee     L'identifiant unique de la tournée.
 * @param date           La date de la tournée.
 * @param id_livreur     L'identifiant unique du livreur associé à la tournée (optionnel).
 * @param nom_livreur    Le nom du livreur associé à la tournée (optionnel).
 * @param prenom_livreur Le prénom du livreur associé à la tournée (optionnel).
 * @param nom            Le nom de la tournée.
 * @param statut         Le statut de la tournée.
 */
case class TourneeAvecLivreur(
                               id_tournee: Long,
                               date: LocalDate,
                               id_livreur: Option[Long],
                               nom_livreur: Option[String],
                               prenom_livreur: Option[String],
                               nom: Option[String],
                               statut: String
                             )

/**
 * Représente les détails nécessaires pour créer une nouvelle tournée.
 *
 * @param date   La date de la nouvelle tournée.
 */
case class TourneeCreation(
                            date: LocalDate
                          )

/**
 * Représente les détails nécessaires pour créer une nouvelle tournée ainsi que son nom.
 *
 * @param date   La date de la nouvelle tournée.
 * @param nom    Le nom de la nouvelle tournée
 */
case class TourneeCreationComplete(
                                    date: LocalDate,
                                    nom: Option[String]
                                  )

/**
 * Représente une mise à jour pour une tournée, y compris le nouveau nom et le nouveau statut.
 *
 * @param nom    Le nouveau nom de la tournée.
 * @param statut Le nouveau statut de la tournée.
 */
case class TourneeUpdate(
                          nom: Option[String],
                          statut: String
                        )

/**
 * Représente une mise à jour de la date d'une tournée
 *
 * @param new_date La nouvelle date de la tournée.
 */
case class TourneeUpdateDate(
                              new_date: LocalDate
                            )

/**
 * Représente une modification du livreur associé à une tournée.
 *
 * @param id_livreur L'identifiant unique du nouveau livreur associé à la tournée (optionnel).
 */
case class LivreurModification(
                                id_livreur: Option[Long]
                              )

/**
 * Représente un résumé de tournée avec l'identifiant de la tournée, l'identifiant de l'article,
 * le libellé de l'article, la taille de l'article (optionnel), le nombre de caisses et le nombre d'unités.
 *
 * @param id_tournee  L'identifiant unique de la tournée.
 * @param id_article  L'identifiant unique de l'article associé au résumé de tournée.
 * @param libelle     Le libellé de l'article associé au résumé de tournée.
 * @param taille      La taille de l'article associé au résumé de tournée (optionnel).
 * @param nb_caisses  Le nombre de caisses dans le résumé de tournée.
 * @param nb_unites   Le nombre d'unités dans le résumé de tournée.
 */
case class ResumeTournee(
                          id_tournee: Long,
                          id_article: Long,
                          libelle: String,
                          taille: Option[String],
                          nb_caisses: Int,
                          nb_unites: Int
                        )

object Tournee {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets Tournee.
  implicit val format: OFormat[Tournee] = Json.format[Tournee]
}

object TourneeAvecLivreur {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets TourneeAvecLivreur.
  implicit val format: OFormat[TourneeAvecLivreur] = Json.format[TourneeAvecLivreur]
}

object TourneeCreation {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets TourneeCreation.
  implicit val format: OFormat[TourneeCreation] = Json.format[TourneeCreation]
}

object TourneeCreationComplete {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets TourneeCreationComplete.
  implicit val format: OFormat[TourneeCreationComplete] = Json.format[TourneeCreationComplete]
}

object TourneeUpdate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets TourneeUpdate.
  implicit val format: OFormat[TourneeUpdate] = Json.format[TourneeUpdate]
}

object TourneeUpdateDate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets TourneeUpdateDate.
  implicit val format: OFormat[TourneeUpdateDate] = Json.format[TourneeUpdateDate]
}

object LivreurModification {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets LivreurModification.
  implicit val format: OFormat[LivreurModification] = Json.format[LivreurModification]
}

object ResumeTournee {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets ResumeTournee.
  implicit val format: OFormat[ResumeTournee] = Json.format[ResumeTournee]
}
