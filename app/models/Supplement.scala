package models

import play.api.libs.json.{Json, OFormat}

case class Supplement(id_tournee: Long,
                      id_article: Long,
                      nb_unites: Long,
                      nb_caisses: Long)

object Supplement {
  implicit val format: OFormat[Supplement] = Json.format[Supplement]
}