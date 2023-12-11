package models

import play.api.libs.json.{Json, OFormat}

/**
 * Représente une ligne de commande par défaut avec son identifiant de crèche, l'identifiant de l'article,
 * le nombre de caisses et le nombre d'unités.
 *
 * @param id_creche   L'identifiant unique de la crèche associée à la ligne de commande par défaut.
 * @param id_article  L'identifiant unique de l'article associé à la ligne de commande par défaut.
 * @param nb_caisses  Le nombre de caisses dans la ligne de commande par défaut.
 * @param nb_unites   Le nombre d'unités dans la ligne de commande par défaut.
 */
case class LigneCommandeParDefaut(
                                   id_creche: Long,
                                   id_article: Long,
                                   nb_caisses: Int,
                                   nb_unites: Int
                                 )

/**
 * Représente une ligne de commande par défaut avec des détails supplémentaires, y compris l'article sans pourcentage associé.
 *
 * @param id_creche      L'identifiant unique de la crèche associée à la ligne de commande par défaut.
 * @param article        L'article sans pourcentage associé à la ligne de commande par défaut.
 * @param nb_caisses     Le nombre de caisses dans la ligne de commande par défaut.
 * @param nb_unites      Le nombre d'unités dans la ligne de commande par défaut.
 */
case class LigneCommandeParDefautWithDetails(
                                               id_creche: Long,
                                               article: ArticleSansPourcentage,
                                               nb_caisses: Int,
                                               nb_unites: Int
                                            )

object LigneCommandeParDefaut {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets LigneCommandeParDefaut.
  implicit val format: OFormat[LigneCommandeParDefaut] = Json.format[LigneCommandeParDefaut]
}

object LigneCommandeParDefautWithDetails {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets LigneCommandeParDefautWithDetails.
  implicit val format: OFormat[LigneCommandeParDefautWithDetails] = Json.format[LigneCommandeParDefautWithDetails]
}
