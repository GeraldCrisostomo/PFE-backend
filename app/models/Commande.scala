package models

import play.api.libs.json.{Json, OFormat}


case class Commande(
                     id_commande: Long,
                     id_tournee: Long,
                     id_creche: Long,
                     ordre: Int,
                     statut: String
                   )
case class CommandeCreate(
                           id_creche: Long,
                           ordre: Int
                         )

case class CommandeWithDetails(
                                id_commande: Long,
                                tournee: Tournee,
                                creche: Creche,
                                ordre: Int,
                                statut: String
                              )

case class CommandeWithAllDetails(
                                    id_commande: Long,
                                    tournee: Tournee,
                                    creche: Creche,
                                    ordre: Int,
                                    statut: String,
                                    lignes_commande: List[LigneCommandeWithDeltails]
                                 )

case class CommandeUpdateStatut(
                                 new_Statut: String
                               )

case class CommandeFullUpdate(
                               new_ordre: Int,
                               new_statut: String,
                               new_lignes_commande: List[LigneCommandeUpdate]
                             )


object Commande {
  implicit val format: OFormat[Commande] = Json.format[Commande]
}

object CommandeCreate {
  implicit val format: OFormat[CommandeCreate] = Json.format[CommandeCreate]
}

object CommandeWithDetails {
  implicit val format: OFormat[CommandeWithDetails] = Json.format[CommandeWithDetails]
}

object CommandeWithAllDetails {
  implicit val format: OFormat[CommandeWithAllDetails] = Json.format[CommandeWithAllDetails]
}

object CommandeUpdateStatut {
  implicit val format: OFormat[CommandeUpdateStatut] = Json.format[CommandeUpdateStatut]
}

object CommandeFullUpdate {
  implicit val format: OFormat[CommandeFullUpdate] = Json.format[CommandeFullUpdate]
}
