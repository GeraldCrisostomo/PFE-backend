package models

import play.api.libs.json.{Json, OFormat}

case class Creche(
                    id_creche: Long,
                    nom: String,
                    ville: String,
                    rue: String,
                  )

case class CrecheWithDetails(
                              id_creche: Option[Long],
                              nom: String,
                              ville: String,
                              rue: String,
                              lignes_par_defaut: List[LigneCommandeParDefaut]
                            )

case class CrecheCreate(
                         nom: String,
                         ville: String,
                         rue: String
                       )

case class CrecheUpdate(
                         nom: String,
                         ville: String,
                         rue: String,
                         new_lignes_par_defaut: List[LigneCommandeParDefaut]
                       )

object Creche {
  implicit val format: OFormat[Creche] = Json.format[Creche]
}

object CrecheWithDetails {
  implicit val format: OFormat[CrecheWithDetails] = Json.format[CrecheWithDetails]
}

object CrecheCreate {
  implicit val format: OFormat[CrecheCreate] = Json.format[CrecheCreate]
}

object CrecheUpdate {
  implicit val format: OFormat[CrecheUpdate] = Json.format[CrecheUpdate]
}
