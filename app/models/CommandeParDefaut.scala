package models

import play.api.libs.json.{Json, OFormat}

/**
 * Représente une commande par défaut avec son identifiant, l'identifiant de la tournée par défaut associée,
 * l'identifiant de la crèche et l'ordre.
 *
 * @param id_commande_par_defaut   L'identifiant unique de la commande par défaut.
 * @param id_tournee_par_defaut    L'identifiant de la tournée par défaut associée à la commande par défaut.
 * @param id_creche                L'identifiant de la crèche associée à la commande par défaut.
 * @param ordre                    L'ordre de la commande par défaut.
 */
case class CommandeParDefaut(
                              id_commande_par_defaut: Long,
                              id_tournee_par_defaut: Long,
                              id_creche: Long,
                              ordre: Int
                            )

/**
 * Représente une commande par défaut avec la crèche associée.
 *
 * @param id_commande_par_defaut   L'identifiant unique de la commande par défaut.
 * @param id_tournee_par_defaut    L'identifiant de la tournée par défaut associée à la commande par défaut.
 * @param creche                   La crèche associée à la commande par défaut.
 * @param ordre                    L'ordre de la commande par défaut.
 */
case class CommandeParDefautWithCreche(
                                        id_commande_par_defaut: Long,
                                        id_tournee_par_defaut: Long,
                                        creche: Creche,
                                        ordre: Int
                                      )

/**
 * Représente une commande par défaut avec la crèche et la tournée par défaut associées.
 *
 * @param id_commande_par_defaut            L'identifiant unique de la commande par défaut.
 * @param tournee_par_defaut                La tournée par défaut associée à la commande par défaut.
 * @param creche                            La crèche associée à la commande par défaut.
 * @param ordre                             L'ordre de la commande par défaut.
 */
case class CommandeParDefautWithCrecheAndTourneeParDefaut(
                                                           id_commande_par_defaut: Long,
                                                           tournee_par_defaut: TourneeParDefaut,
                                                           creche: Creche,
                                                           ordre: Int
                                                         )

/**
 * Représente les détails nécessaires pour créer une nouvelle commande par défaut.
 *
 * @param id_creche   L'identifiant de la crèche associée à la nouvelle commande par défaut.
 * @param ordre       L'ordre de la nouvelle commande par défaut.
 */
case class CommandeParDefautCreate(
                                    id_creche: Long,
                                    ordre: Int
                                  )

/**
 * Représente une mise à jour d'ordre pour une commande par défaut.
 *
 * @param new_ordre   Le nouvel ordre à appliquer à la commande par défaut.
 */
case class CommandeParDefautUpdate(
                                    new_ordre: Int
                                  )

object CommandeParDefaut {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CommandeParDefaut.
  implicit val format: OFormat[CommandeParDefaut] = Json.format[CommandeParDefaut]
}

object CommandeParDefautWithCreche {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CommandeParDefautWithCreche.
  implicit val format: OFormat[CommandeParDefautWithCreche] = Json.format[CommandeParDefautWithCreche]
}

object CommandeParDefautWithCrecheAndTourneeParDefaut {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CommandeParDefautWithCrecheAndTourneeParDefaut.
  implicit val format: OFormat[CommandeParDefautWithCrecheAndTourneeParDefaut] = Json.format[CommandeParDefautWithCrecheAndTourneeParDefaut]
}

object CommandeParDefautCreate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CommandeParDefautCreate.
  implicit val format: OFormat[CommandeParDefautCreate] = Json.format[CommandeParDefautCreate]
}

object CommandeParDefautUpdate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CommandeParDefautUpdate.
  implicit val format: OFormat[CommandeParDefautUpdate] = Json.format[CommandeParDefautUpdate]
}
