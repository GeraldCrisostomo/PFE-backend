package models

import play.api.libs.json.{Json, OFormat}

case class LigneCommandeParDefaut(
                                   id_creche: Long,
                                   id_article: Long,
                                   nb_caisses: Int,
                                   nb_unites: Int
                                 )
object LigneCommandeParDefaut {
  implicit val format: OFormat[LigneCommandeParDefaut] = Json.format[LigneCommandeParDefaut]
}
