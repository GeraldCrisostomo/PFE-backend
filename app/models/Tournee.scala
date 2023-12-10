package models

import play.api.libs.json.{Json, OFormat}
import java.time.LocalDate

case class Tournee(
                    id_tournee: Long,
                    date: LocalDate,
                    id_livreur: Option[Long],
                    nom: Option[String],
                    statut: String
                  )

case class TourneeAvecLivreur(
                               id_tournee: Long,
                               date: LocalDate,
                               id_livreur: Option[Long],
                               nom_livreur: Option[String],
                               prenom_livreur: Option[String],
                               nom: Option[String],
                               statut: String
                             )


case class TourneeCreation(
                            date: LocalDate
                          )

case class TourneeUpdate(
                          nom: Option[String],
                          statut: String
                        )

case class LivreurModification(
                                id_livreur: Option[Long]
                              )

case class ResumeTournee(
                          id_tournee: Long,
                          id_article: Long,
                          libelle: String,
                          taille: Option[String],
                          nb_caisses: Int,
                          nb_unites: Int
                        )

object Tournee {
  implicit val format: OFormat[Tournee] = Json.format[Tournee]
}

object TourneeAvecLivreur {
  implicit val format: OFormat[TourneeAvecLivreur] = Json.format[TourneeAvecLivreur]
}

object TourneeCreation {
  implicit val format: OFormat[TourneeCreation] = Json.format[TourneeCreation]
}

object TourneeUpdate {
  implicit val format: OFormat[TourneeUpdate] = Json.format[TourneeUpdate]
}

object LivreurModification {
  implicit val format: OFormat[LivreurModification] = Json.format[LivreurModification]
}

object ResumeTournee {
  implicit val format: OFormat[ResumeTournee] = Json.format[ResumeTournee]
}