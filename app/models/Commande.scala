package models

import play.api.libs.json.{Json, OFormat}

/**
 * Représente une commande avec son identifiant, l'identifiant de la tournée associée, l'identifiant de la crèche,
 * l'ordre et le statut.
 *
 * @param id_commande   L'identifiant unique de la commande.
 * @param id_tournee    L'identifiant de la tournée associée à la commande.
 * @param id_creche     L'identifiant de la crèche associée à la commande.
 * @param ordre         L'ordre de la commande.
 * @param statut        Le statut de la commande.
 */
case class Commande(
                     id_commande: Long,
                     id_tournee: Long,
                     id_creche: Long,
                     ordre: Int,
                     statut: String
                   )

/**
 * Représente les détails nécessaires pour créer une nouvelle commande.
 *
 * @param id_creche   L'identifiant de la crèche associée à la nouvelle commande.
 * @param ordre       L'ordre de la nouvelle commande.
 */
case class CommandeCreate(
                           id_creche: Long,
                           ordre: Int
                         )

/**
 * Représente une commande avec des détails supplémentaires, y compris la tournée et la crèche associées.
 *
 * @param id_commande   L'identifiant unique de la commande.
 * @param tournee        La tournée associée à la commande.
 * @param creche         La crèche associée à la commande.
 * @param ordre          L'ordre de la commande.
 * @param statut         Le statut de la commande.
 */
case class CommandeWithDetails(
                                id_commande: Long,
                                tournee: Tournee,
                                creche: Creche,
                                ordre: Int,
                                statut: String
                              )

/**
 * Représente une commande avec tous les détails, y compris les lignes de commande détaillées.
 *
 * @param id_commande       L'identifiant unique de la commande.
 * @param tournee            La tournée associée à la commande.
 * @param creche             La crèche associée à la commande.
 * @param ordre              L'ordre de la commande.
 * @param statut             Le statut de la commande.
 * @param lignes_commande    Les lignes de commande détaillées associées à la commande.
 */
case class CommandeWithAllDetails(
                                   id_commande: Long,
                                   tournee: Tournee,
                                   creche: Creche,
                                   ordre: Int,
                                   statut: String,
                                   lignes_commande: List[LigneCommandeWithDetails]
                                 )

/**
 * Représente une mise à jour de statut pour une commande.
 *
 * @param new_Statut   Le nouveau statut à appliquer à la commande.
 */
case class CommandeUpdateStatut(
                                 new_Statut: String
                               )

/**
 * Représente une mise à jour complète pour une commande, y compris le nouvel ordre, le nouveau statut
 * et les mises à jour des lignes de commande.
 *
 * @param new_ordre             Le nouvel ordre à appliquer à la commande.
 * @param new_statut            Le nouveau statut à appliquer à la commande.
 * @param new_lignes_commande   Les mises à jour des lignes de commande à appliquer.
 */
case class CommandeFullUpdate(
                               new_ordre: Int,
                               new_statut: String,
                               new_lignes_commande: List[LigneCommandeUpdate]
                             )

object Commande {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets Commande.
  implicit val format: OFormat[Commande] = Json.format[Commande]
}

object CommandeCreate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CommandeCreate.
  implicit val format: OFormat[CommandeCreate] = Json.format[CommandeCreate]
}

object CommandeWithDetails {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CommandeWithDetails.
  implicit val format: OFormat[CommandeWithDetails] = Json.format[CommandeWithDetails]
}

object CommandeWithAllDetails {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CommandeWithAllDetails.
  implicit val format: OFormat[CommandeWithAllDetails] = Json.format[CommandeWithAllDetails]
}

object CommandeUpdateStatut {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CommandeUpdateStatut.
  implicit val format: OFormat[CommandeUpdateStatut] = Json.format[CommandeUpdateStatut]
}

object CommandeFullUpdate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CommandeFullUpdate.
  implicit val format: OFormat[CommandeFullUpdate] = Json.format[CommandeFullUpdate]
}
