package models

import play.api.libs.json.{Json, OFormat}

/**
 * Représente une tournée par défaut avec son identifiant et le nom par défaut.
 *
 * @param id_tournee_par_defaut    L'identifiant unique de la tournée par défaut.
 * @param nom_par_defaut           Le nom par défaut de la tournée.
 */
case class TourneeParDefaut(
                             id_tournee_par_defaut: Long,
                             nom_par_defaut: Option[String]
                           )

/**
 * Représente les détails nécessaires pour créer une nouvelle tournée par défaut.
 *
 * @param nom_par_defaut           Le nom par défaut de la nouvelle tournée.
 */
case class TourneeParDefautCreate(
                                   nom_par_defaut: Option[String]
                                 )

/**
 * Représente une mise à jour pour une tournée par défaut, y compris le nouveau nom par défaut.
 *
 * @param new_nom_par_defaut       Le nouveau nom par défaut de la tournée.
 */
case class TourneeParDefautUpdate(
                                   new_nom_par_defaut: Option[String]
                                 )

object TourneeParDefaut {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets TourneeParDefaut.
  implicit val format: OFormat[TourneeParDefaut] = Json.format[TourneeParDefaut]
}

object TourneeParDefautCreate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets TourneeParDefautCreate.
  implicit val format: OFormat[TourneeParDefautCreate] = Json.format[TourneeParDefautCreate]
}

object TourneeParDefautUpdate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets TourneeParDefautUpdate.
  implicit val format: OFormat[TourneeParDefautUpdate] = Json.format[TourneeParDefautUpdate]
}
