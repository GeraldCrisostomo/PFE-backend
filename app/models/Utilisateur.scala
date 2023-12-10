package models

import play.api.libs.json.{Json, OFormat}

case class Utilisateur( id_utilisateur: Long,
                        nom: String,
                        prenom: String,
                        identifiant: String,
                        mot_de_passe: String,
                        role: String)
object Utilisateur {
  implicit val format: OFormat[Utilisateur] = Json.format[Utilisateur]
}