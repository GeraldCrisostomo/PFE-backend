package models

import play.api.libs.json.{Json, OFormat}
import java.time.LocalDate

case class Tournee(id_tournee: Long,
                   date: LocalDate,
                   id_livreur: Option[Long],
                   nom: Option[String],
                   statut: Option[String])
object Tournee {
  implicit val format: OFormat[Tournee] = Json.format[Tournee]
}

case class TourneeAvecLivreur(id_tournee: Long,
                               date: LocalDate,
                               id_livreur: Option[Long],
                               nom_livreur: Option[String],
                               prenom_livreur: Option[String],
                               nom: Option[String],
                               statut: Option[String])
object TourneeAvecLivreur {
  implicit val format: OFormat[TourneeAvecLivreur] = Json.format[TourneeAvecLivreur]
}


case class TourneeCreation(date: LocalDate)
object TourneeCreation {
  implicit val format: OFormat[TourneeCreation] = Json.format[TourneeCreation]
}

case class TourneeUpdate(nom: Option[String],
                         statut: Option[String])
object TourneeUpdate {
  implicit val format: OFormat[TourneeUpdate] = Json.format[TourneeUpdate]
}

case class LivreurModification(id_livreur: Option[Long])
object LivreurModification {
  implicit val format: OFormat[LivreurModification] = Json.format[LivreurModification]
}

case class ResumeTournee(id_tournee: Long,
                                 id_article: Long,
                                 libelle: String,
                                 taille: Option[String],
                                 nb_caisses: Int,
                                 nb_unites: Int)
object ResumeTournee {
  implicit val format: OFormat[ResumeTournee] = Json.format[ResumeTournee]
}