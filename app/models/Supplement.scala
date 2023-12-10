package models

import play.api.libs.json.{Json, OFormat}

case class Supplement(id_tournee: Long,
                      id_article: Long,
                      nb_unites: Int,
                      nb_caisses: Int)

object Supplement {
  implicit val format: OFormat[Supplement] = Json.format[Supplement]
}

case class SupplementUpdate(nb_unites: Int,
                           nb_caisses: Int)
object SupplementUpdate{
  implicit val format: OFormat[SupplementUpdate] = Json.format[SupplementUpdate]
}

