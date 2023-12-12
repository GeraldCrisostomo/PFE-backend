package models

import play.api.libs.json.{Json, OFormat}

/**
 * Représente un utilisateur avec son identifiant, nom, prénom, identifiant, mot de passe et rôle.
 *
 * @param id_utilisateur   L'identifiant unique de l'utilisateur.
 * @param nom              Le nom de l'utilisateur.
 * @param prenom           Le prénom de l'utilisateur.
 * @param identifiant      L'identifiant de connexion de l'utilisateur.
 * @param mot_de_passe     Le mot de passe de l'utilisateur.
 * @param role             Le rôle de l'utilisateur.
 */
case class Utilisateur(
                        id_utilisateur: Long,
                        nom: String,
                        prenom: String,
                        identifiant: String,
                        mot_de_passe: String,
                        role: String
                      )

object Utilisateur {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets Utilisateur.
  implicit val format: OFormat[Utilisateur] = Json.format[Utilisateur]
}
