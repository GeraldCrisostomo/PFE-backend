package services

import javax.inject.Inject
import models.{Creche, CrecheCreate, CrecheUpdate, CrecheWithDetails, LigneCommandeParDefaut}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class CrecheService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.profile.api._

  private class CrecheTable(tag: Tag) extends Table[Creche](tag, Some("pfe"), "creches") {
    def id_creche = column[Long]("id_creche", O.PrimaryKey, O.AutoInc)
    def nom = column[String]("nom")
    def ville = column[String]("ville")
    def rue = column[String]("rue")
    def * = (id_creche, nom, ville, rue) <> ((Creche.apply _).tupled, Creche.unapply)
  }

  private val creches = TableQuery[CrecheTable]

  private class LigneCommandeParDefautTable(tag: Tag) extends Table[LigneCommandeParDefaut](tag, Some("pfe"), "lignes_commande_par_defaut") {
    def id_creche = column[Long]("id_creche")
    def id_article = column[Long]("id_article")
    def nb_caisses = column[Int]("nb_caisses", O.Default(0))
    def nb_unites = column[Int]("nb_unites", O.Default(0))
    def pk = primaryKey("pk_lignes_commande_par_defaut", (id_creche, id_article))
    def * = (id_creche, id_article, nb_caisses, nb_unites) <> ((LigneCommandeParDefaut.apply _).tupled, LigneCommandeParDefaut.unapply)
  }

  private val lignesCommandeParDefaut = TableQuery[LigneCommandeParDefautTable]

  def getAllCreches: Future[List[Creche]] =
    dbConfig.db.run(creches.to[List].result)

  def getCrecheById(id: Long): Future[Option[CrecheWithDetails]] = {
    val crecheQuery = creches
      .filter(_.id_creche === id)
      .result.headOption

    val lignesQuery = getLignesCommandeParDefautByIdCreche(id)

    val result = for {
      crecheOption <- dbConfig.db.run(crecheQuery)
      lignesList <- lignesQuery
    } yield (crecheOption, lignesList)

    result.map {
      case (Some(creche), lignesList) =>
        val lignesParDefaut = lignesList.map { ligne =>
          LigneCommandeParDefaut(
            id_creche = ligne.id_creche,
            id_article = ligne.id_article,
            nb_caisses = ligne.nb_caisses,
            nb_unites = ligne.nb_unites
          )
        }

        Some(
          CrecheWithDetails(
            id_creche = Some(creche.id_creche),
            nom = creche.nom,
            ville = creche.ville,
            rue = creche.rue,
            lignes_par_defaut = lignesParDefaut
          )
        )
      case _ => None
    }
  }


  def getLignesCommandeParDefautByIdCreche(idCreche: Long): Future[List[LigneCommandeParDefaut]] = {
    val query = lignesCommandeParDefaut
      .filter(_.id_creche === idCreche)
      .result

    dbConfig.db.run(query).map(_.toList)
  }


  def createCreche(crecheCreate: CrecheCreate): Future[Long] =
    dbConfig.db.run((creches returning creches.map(_.id_creche)) += Creche(0, crecheCreate.nom, crecheCreate.ville, crecheCreate.rue))

  def updateCreche(id: Long, crecheUpdate: CrecheUpdate): Future[Boolean] = {
    val updateCrecheQuery = creches
      .filter(_.id_creche === id)
      .map(c => (c.nom, c.ville, c.rue))
      .update((crecheUpdate.nom, crecheUpdate.ville, crecheUpdate.rue))

    // Met à jour les lignes_commande_par_defaut existantes avec les nouvelles valeurs
    val updateLignesCommandeParDefautQuery = DBIO.seq(
      crecheUpdate.new_lignes_par_defaut.map { ligne =>
        lignesCommandeParDefaut
          .filter(l => l.id_creche === id && l.id_article === ligne.id_article)
          .map(l => (l.nb_caisses, l.nb_unites))
          .update((ligne.nb_caisses, ligne.nb_unites))
      }: _*
    )

    val transaction = DBIO.seq(
      updateCrecheQuery,
      updateLignesCommandeParDefautQuery
    )

    dbConfig.db.run(transaction).map(_ => true)
  }


  def deleteCreche(id: Long): Future[Boolean] =
    dbConfig.db.run(creches.filter(_.id_creche === id).delete).map(_ > 0)
}
