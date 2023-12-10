import play.api.libs.json._
import slick.jdbc.PostgresProfile.api._

// models/Commande.scala
case class Commande(id: Option[Int], id_tournee: Int, ordre: Int, statut: String)

object Commande {
  implicit val commandeFormat: OFormat[Commande] = Json.format[Commande]
  implicit val commandeColumnType: BaseColumnType[Commande] = MappedColumnType.base[Commande, Int](_.id.getOrElse(0), Commande(_))
}

// models/LigneCommande.scala
case class LigneCommande(id_article: Int, nb_caisses: Int, nb_unites: Int)

object LigneCommande {
  implicit val ligneCommandeFormat: OFormat[LigneCommande] = Json.format[LigneCommande]
  implicit val ligneCommandeColumnType: BaseColumnType[LigneCommande] = MappedColumnType.base[LigneCommande, String](
    lc => Json.toJson(lc).toString(),
    json => Json.fromJson[LigneCommande](Json.parse(json)).get
  )
}

// models/StatutEnum.scala
case class StatutEnum(statut: String)

object StatutEnum {
  implicit val statutEnumFormat: OFormat[StatutEnum] = Json.format[StatutEnum]
}

// models/CommandeCreate.scala
case class CommandeCreate(ordre: Int, lignes_commande: Seq[LigneCommandeCreate])

object CommandeCreate {
  implicit val commandeCreateFormat: OFormat[CommandeCreate] = Json.format[CommandeCreate]
}

// models/LigneCommandeCreate.scala
case class LigneCommandeCreate(id_article: Int, nb_caisses: Int, nb_unites: Int)

object LigneCommandeCreate {
  implicit val ligneCommandeCreateFormat: OFormat[LigneCommandeCreate] = Json.format[LigneCommandeCreate]
}

// models/CommandeWithDetails.scala
case class CommandeWithDetails(id_commande: Int, id_tournee: Int, ordre: Int, statut: String, lignes_commande: Seq[LigneCommande])

object CommandeWithDetails {
  implicit val commandeWithDetailsFormat: OFormat[CommandeWithDetails] = Json.format[CommandeWithDetails]
}

// models/CommandeUpdate.scala
case class CommandeUpdate(new_ordre: Int)

object CommandeUpdate {
  implicit val commandeUpdateFormat: OFormat[CommandeUpdate] = Json.format[CommandeUpdate]
}

// models/CommandeFullUpdate.scala
case class CommandeFullUpdate(new_ordre: Int, new_lignes_commande: Seq[LigneCommandeUpdate])

object CommandeFullUpdate {
  implicit val commandeFullUpdateFormat: OFormat[CommandeFullUpdate] = Json.format[CommandeFullUpdate]
}

// models/LigneCommandeUpdate.scala
case class LigneCommandeUpdate(id_article: Int, new_nb_caisses: Int, new_nb_unites: Int)

object LigneCommandeUpdate {
  implicit val ligneCommandeUpdateFormat: OFormat[LigneCommandeUpdate] = Json.format[LigneCommandeUpdate]
}
