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

/**
 * Représente les informations nécessaires pour créer un nouvel utilisateur.
 *
 * @param nom              Le nom de l'utilisateur.
 * @param prenom           Le prénom de l'utilisateur.
 * @param identifiant      L'identifiant de connexion de l'utilisateur.
 * @param mot_de_passe     Le mot de passe de l'utilisateur.
 * @param role             Le rôle de l'utilisateur.
 */
case class UtilisateurCreate(
                              nom: String,
                              prenom: String,
                              identifiant: String,
                              mot_de_passe: String,
                              role: String
                            )

/**
 * Représente les nouvelles informations pour mettre à jour un utilisateur.
 *
 * @param nom              Le nom de l'utilisateur.
 * @param prenom           Le prénom de l'utilisateur.
 * @param identifiant      L'identifiant de connexion de l'utilisateur.
 * @param mot_de_passe     Le nouveau mot de passe de l'utilisateur.
 * @param role             Le nouveau rôle de l'utilisateur.
 */
case class UtilisateurUpdate(
                              nom: String,
                              prenom: String,
                              identifiant: String,
                              mot_de_passe: String,
                              role: String
                            )

/**
 * Représente les informations d'identification de l'utilisateur pour la connexion.
 *
 * @param identifiant      L'identifiant de connexion de l'utilisateur.
 * @param mot_de_passe     Le mot de passe de l'utilisateur.
 */
case class ConnexionInfo(
                          identifiant: String,
                          mot_de_passe: String
                        )

object Utilisateur {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets Utilisateur.
  implicit val format: OFormat[Utilisateur] = Json.format[Utilisateur]
}

object UtilisateurCreate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets UtilisateurCreate.
  implicit val utilisateurCreateFormat: OFormat[UtilisateurCreate] = Json.format[UtilisateurCreate]
}

object UtilisateurUpdate {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets UtilisateurUpdate.
  implicit val utilisateurUpdateFormat: OFormat[UtilisateurUpdate] = Json.format[UtilisateurUpdate]
}

object ConnexionInfo {
  // Implémente le format JSON implicite pour la sérialisation/désérialisation des objets ConnexionInfo.
  implicit val connexionInfoFormat: OFormat[ConnexionInfo] = Json.format[ConnexionInfo]
}
