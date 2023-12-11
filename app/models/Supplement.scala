package models

import play.api.libs.json.{Json, OFormat}

/**
 * Représente un supplément avec son identifiant de tournée, l'identifiant de l'article,
 * le nombre d'unités et le nombre de caisses.
 *
 * @param id_tournee  L'identifiant unique de la tournée associée au supplément.
 * @param id_article  L'identifiant unique de l'article associé au supplément.
 * @param nb_unites   Le nombre d'unités dans le supplément.
 * @param nb_caisses  Le nombre de caisses dans le supplément.
 */
case class Supplement(
                       id_tournee: Long,
                       id_article: Long,
                       nb_unites: Int,
                       nb_caisses: Int
                     )

/**
 * Représente une mise à jour pour un supplément, y compris le nouveau nombre d'unités et le nouveau nombre de caisses.
 *
 * @param nb_unites   Le nouveau nombre d'unités dans le supplément.
 * @param nb_caisses  Le nouveau nombre de caisses dans le supplément.
 */
case class SupplementUpdate(
                             nb_unites: Int,
                             nb_caisses: Int
                           )

object Supplement {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets Supplement.
  implicit val format: OFormat[Supplement] = Json.format[Supplement]
}

object SupplementUpdate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets SupplementUpdate.
  implicit val format: OFormat[SupplementUpdate] = Json.format[SupplementUpdate]
}
