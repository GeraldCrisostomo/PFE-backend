package models

import play.api.libs.json.{Json, OFormat}

/**
 * Représente une crèche avec son identifiant, nom, ville et rue.
 *
 * @param id_creche   L'identifiant unique de la crèche.
 * @param nom         Le nom de la crèche.
 * @param ville       La ville où se situe la crèche.
 * @param rue         La rue où se situe la crèche.
 */
case class Creche(
                   id_creche: Long,
                   nom: String,
                   ville: String,
                   rue: String
                 )

/**
 * Représente une crèche avec des détails supplémentaires, y compris les lignes de commande par défaut associées.
 *
 * @param id_creche           L'identifiant unique de la crèche (Optionnel, peut être absent lors de la création).
 * @param nom                 Le nom de la crèche.
 * @param ville               La ville où se situe la crèche.
 * @param rue                 La rue où se situe la crèche.
 * @param lignes_par_defaut   Les lignes de commande par défaut associées à la crèche avec des détails supplémentaires comme l'article sans pourcentage.
 */
case class CrecheWithDetails(
                              id_creche: Option[Long],
                              nom: String,
                              ville: String,
                              rue: String,
                              lignes_par_defaut: List[LigneCommandeParDefautWithDetails]
                            )

/**
 * Représente les détails nécessaires pour créer une nouvelle crèche.
 *
 * @param nom     Le nom de la nouvelle crèche.
 * @param ville   La ville où se situe la nouvelle crèche.
 * @param rue     La rue où se situe la nouvelle crèche.
 */
case class CrecheCreate(
                         nom: String,
                         ville: String,
                         rue: String
                       )

/**
 * Représente une mise à jour pour une crèche, y compris le nouveau nom, la nouvelle ville,
 * la nouvelle rue et les nouvelles lignes de commande par défaut associées.
 *
 * @param nom                     Le nouveau nom de la crèche.
 * @param ville                   La nouvelle ville où se situe la crèche.
 * @param rue                     La nouvelle rue où se situe la crèche.
 * @param new_lignes_par_defaut   Les nouvelles lignes de commande par défaut associées à la crèche.
 */
case class CrecheUpdate(
                         nom: String,
                         ville: String,
                         rue: String,
                         new_lignes_par_defaut: List[LigneCommandeParDefaut]
                       )

object Creche {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets Creche.
  implicit val format: OFormat[Creche] = Json.format[Creche]
}

object CrecheWithDetails {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CrecheWithDetails.
  implicit val format: OFormat[CrecheWithDetails] = Json.format[CrecheWithDetails]
}

object CrecheCreate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CrecheCreate.
  implicit val format: OFormat[CrecheCreate] = Json.format[CrecheCreate]
}

object CrecheUpdate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets CrecheUpdate.
  implicit val format: OFormat[CrecheUpdate] = Json.format[CrecheUpdate]
}
