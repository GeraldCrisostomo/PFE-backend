package models

import play.api.libs.json.{Json, OFormat}

case class TourneeParDefaut(
                             id_tournee_par_defaut: Long,
                             nom_par_defaut: Option[String]
                           )

case class TourneeParDefautCreate(
                                   nom_par_defaut: Option[String]
                                 )

case class TourneeParDefautUpdate(
                                   new_nom_par_defaut: Option[String]
                                 )

object TourneeParDefaut {
  implicit val format: OFormat[TourneeParDefaut] = Json.format[TourneeParDefaut]
}

object TourneeParDefautCreate {
  implicit val format: OFormat[TourneeParDefautCreate] = Json.format[TourneeParDefautCreate]
}

object TourneeParDefautUpdate {
  implicit val format: OFormat[TourneeParDefautUpdate] = Json.format[TourneeParDefautUpdate]
}
