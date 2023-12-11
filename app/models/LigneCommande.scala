package models

import play.api.libs.json.{Json, OFormat}

/**
 * Représente une ligne de commande avec son identifiant, l'identifiant de la commande associée,
 * l'identifiant de l'article, le nombre de caisses et le nombre d'unités.
 *
 * @param id_commande   L'identifiant unique de la commande associée à la ligne de commande.
 * @param id_article     L'identifiant unique de l'article associé à la ligne de commande.
 * @param nb_caisses     Le nombre de caisses dans la ligne de commande.
 * @param nb_unites      Le nombre d'unités dans la ligne de commande.
 */
case class LigneCommande(
                          id_commande: Long,
                          id_article: Long,
                          nb_caisses: Int,
                          nb_unites: Int
                        )

/**
 * Représente une ligne de commande avec des détails supplémentaires, y compris l'article sans pourcentage associé.
 *
 * @param id_commande   L'identifiant unique de la commande associée à la ligne de commande.
 * @param article        L'article sans pourcentage associé à la ligne de commande.
 * @param nb_caisses     Le nombre de caisses dans la ligne de commande.
 * @param nb_unites      Le nombre d'unités dans la ligne de commande.
 */
case class LigneCommandeWithDetails(
                                      id_commande: Long,
                                      article: ArticleSansPourcentage,
                                      nb_caisses: Int,
                                      nb_unites: Int
                                    )

/**
 * Représente une mise à jour pour une ligne de commande, y compris le nouvel identifiant d'article,
 * le nouveau nombre de caisses et le nouveau nombre d'unités.
 *
 * @param id_commande       L'identifiant unique de la commande associée à la ligne de commande.
 * @param id_article         Le nouvel identifiant d'article associé à la ligne de commande.
 * @param new_nb_caisses     Le nouveau nombre de caisses dans la ligne de commande.
 * @param new_nb_unites      Le nouveau nombre d'unités dans la ligne de commande.
 */
case class LigneCommandeUpdate(
                                id_commande: Long,
                                id_article: Long,
                                new_nb_caisses: Int,
                                new_nb_unites: Int
                              )

object LigneCommande {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets LigneCommande.
  implicit val format: OFormat[LigneCommande] = Json.format[LigneCommande]
}

object LigneCommandeWithDeltails {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets LigneCommandeWithDeltails.
  implicit val format: OFormat[LigneCommandeWithDetails] = Json.format[LigneCommandeWithDetails]
}

object LigneCommandeUpdate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets LigneCommandeUpdate.
  implicit val format: OFormat[LigneCommandeUpdate] = Json.format[LigneCommandeUpdate]
}
