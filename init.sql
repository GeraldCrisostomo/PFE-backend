-- Création du schéma pfe ----------------------------------------------------------------------------------------------
DROP SCHEMA IF EXISTS pfe CASCADE;
CREATE SCHEMA pfe;



-- Création des tables -------------------------------------------------------------------------------------------------

-- Table des articles
CREATE TABLE pfe.articles (
    id_article SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL,
    taille VARCHAR(5) CHECK ((libelle = 'Langes' AND taille IN ('S', 'M', 'L')) OR (libelle <> 'Langes' AND taille IS NULL)),
    pourcentage INTEGER NOT NULL DEFAULT 10 CHECK (pourcentage >= 0)
);

-- Table des creches
CREATE TABLE pfe.creches (
    id_creche SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    ville VARCHAR(50) NOT NULL,
    rue VARCHAR(100) NOT NULL
);

-- Table des utilisateurs
CREATE TABLE pfe.utilisateurs (
    id_utilisateur SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    identifiant VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'livreur',
    CHECK (role = 'livreur' OR role = 'admin')
);

-- Table des tournees

-- Création de la table
CREATE TABLE pfe.tournees (
    id_tournee SERIAL PRIMARY KEY,
    id_livreur INTEGER,
    nom VARCHAR(100),
    date DATE NOT NULL DEFAULT now(),
    statut VARCHAR(20) DEFAULT 'en attente' NOT NULL,
    CHECK (statut = 'en attente' OR statut = 'en cours' OR statut = 'terminée'),
    FOREIGN KEY (id_livreur) REFERENCES pfe.utilisateurs ON DELETE SET NULL
);

-- Supprimer l'index unique partiel'
DROP INDEX IF EXISTS idx_unique_nom_non_null;

-- Création de l'index unique partiel
CREATE UNIQUE INDEX idx_unique_nom_non_null ON pfe.tournees (date, nom) WHERE nom IS NOT NULL;


-- Table des commandes
CREATE TABLE pfe.commandes (
    id_commande SERIAL PRIMARY KEY,
    id_tournee INTEGER NOT NULL,
    id_creche INTEGER NOT NULL,
    ordre INTEGER NOT NULL CHECK (ordre > 0),
    statut VARCHAR(20) NOT NULL DEFAULT 'en attente',
    CHECK (statut = 'en attente' OR statut = 'en cours' OR statut = 'terminée'),
    FOREIGN KEY (id_tournee) REFERENCES pfe.tournees ON DELETE CASCADE,
    FOREIGN KEY (id_creche) REFERENCES pfe.creches ON DELETE CASCADE
);

-- Table des suppléments
CREATE TABLE pfe.supplements(
    id_tournee INTEGER NOT NULL,
    id_article INTEGER NOT NULL,
    PRIMARY KEY (id_tournee, id_article),
    nb_caisses INTEGER NOT NULL DEFAULT 0 CHECK (nb_caisses >= 0),
    nb_unites INTEGER NOT NULL DEFAULT 0 CHECK (nb_unites >= 0),
    FOREIGN KEY (id_tournee) REFERENCES pfe.tournees ON DELETE CASCADE,
    FOREIGN KEY (id_article) REFERENCES pfe.articles ON DELETE CASCADE
);

-- Table des lignes_commande
CREATE TABLE pfe.lignes_commande(
    id_commande INTEGER NOT NULL,
    id_article INTEGER NOT NULL,
    PRIMARY KEY (id_commande, id_article),
    nb_caisses INTEGER NOT NULL DEFAULT 0 CHECK (nb_caisses >= 0),
    nb_unites INTEGER NOT NULL DEFAULT 0 CHECK (nb_unites >= 0),
    FOREIGN KEY (id_commande) REFERENCES pfe.commandes ON DELETE CASCADE,
    FOREIGN KEY (id_article) REFERENCES pfe.articles ON DELETE CASCADE
);

-- Tables par défaut

-- Table tournees_par_defaut
CREATE TABLE pfe.tournees_par_defaut (
    id_tournee_par_defaut SERIAL PRIMARY KEY,
    nom_par_defaut VARCHAR(100)
);

-- Table commandes_par_defaut
CREATE TABLE pfe.commandes_par_defaut (
    id_commande_par_defaut SERIAL PRIMARY KEY,
    id_tournee_par_defaut INTEGER NOT NULL,
    id_creche INTEGER NOT NULL,
    ordre INTEGER NOT NULL CHECK (ordre > 0),
    FOREIGN KEY (id_tournee_par_defaut) REFERENCES pfe.tournees_par_defaut ON DELETE CASCADE,
    FOREIGN KEY (id_creche) REFERENCES pfe.creches ON DELETE CASCADE
);

-- Table des lignes_commande_par_defaut
CREATE TABLE pfe.lignes_commande_par_defaut(
    id_creche INTEGER NOT NULL ,
    id_article INTEGER NOT NULL ,
    PRIMARY KEY (id_creche, id_article),
    nb_caisses INTEGER NOT NULL DEFAULT 0 CHECK (nb_caisses >= 0),
    nb_unites INTEGER NOT NULL DEFAULT 0 CHECK (nb_unites >= 0),
    FOREIGN KEY (id_creche) REFERENCES pfe.creches ON DELETE CASCADE,
    FOREIGN KEY (id_article) REFERENCES pfe.articles ON DELETE CASCADE
);



-- Création des views --------------------------------------------------------------------------------------------------

-- Vue pour avoir le résumé d'une commande

-- Vérifier si la vue existe
SELECT * FROM information_schema.views WHERE table_name = 'ResumesTournees';

-- Supprimer la vue existante si elle existe
DROP VIEW IF EXISTS ResumesTournees;

-- Créer la vue
CREATE VIEW ResumesTournees (id_tournee, id_article, libelle, taille, nb_caisses, nb_unites) AS
SELECT
    t.id_tournee,
    a.id_article,
    a.libelle,
    a.taille,
    COALESCE(SUM(lc.nb_caisses), 0) AS nb_caisses,
    COALESCE(SUM(lc.nb_unites), 0) AS nb_unites
FROM
    pfe.tournees t
CROSS JOIN pfe.articles a
LEFT JOIN pfe.commandes c ON t.id_tournee = c.id_tournee
LEFT JOIN pfe.lignes_commande lc ON c.id_commande = lc.id_commande AND lc.id_article = a.id_article
GROUP BY
    t.id_tournee,
    a.id_article,
    a.libelle,
    a.taille;



-- Création des triggers -----------------------------------------------------------------------------------------------

-- Trigger 1  : add_lignes_par_defaut                           : Ajoute automatiquement les lignes par défaut manquantes à la création d'une creche
-- Trigger 2  : add_lignes_commande                             : Ajoute automatiquement les lignes de commande manquantes à la création d'une commande
-- Trigger 3  : add_supplements                                 : Ajoute automatiquement les supplements à la création d'une tournée
-- Trigger 4  : update_supplements                              : Met à jour les suppléments automatiquement
-- Trigger 5  : update_statut_tournees                          : Met à jour le statut d'une tournée automatiquement
-- Trigger 6  : update_commandes_ordre_before_insert            : Met à jour l'ordre des commandes
-- Trigger 7  : update_commandes_ordre_before_update            : Met à jour l'ordre des commandes
-- Trigger 8  : update_commandes_ordre_before_delete            : Met à jour l'ordre des commandes
-- Trigger 9  : update_commandes_par_defaut_ordre_before_insert : Met à jour l'ordre des commandes par défaut
-- Trigger 10 : update_commandes_par_defaut_ordre_before_update : Met à jour l'ordre des commandes par défaut
-- Trigger 11 : update_commandes_par_defaut_ordre_before_delete : Met à jour l'ordre des commandes par défaut


-- Lignes par défaut

-- Trigger 1 :
-- Création de la procédure pour ajouter des lignes par défaut automatiquement à la création d'une crèche
CREATE OR REPLACE FUNCTION add_lignes_par_defaut()
    RETURNS TRIGGER AS $$
DECLARE
    _id_article integer;
BEGIN
    -- Parcourir les articles
    FOR _id_article IN (
        SELECT a.id_article
        FROM pfe.articles a)
    LOOP
        -- Insérer une nouvelle ligne de commande par défaut
        INSERT INTO pfe.lignes_commande_par_defaut (id_creche, id_article)
        VALUES (NEW.id_creche, _id_article);
    END LOOP;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Attacher le déclencheur à la table creches
CREATE TRIGGER after_insert_creches_trigger
AFTER INSERT ON pfe.creches
FOR EACH ROW
EXECUTE PROCEDURE add_lignes_par_defaut();


-- Lignes de commandes

-- Trigger 2 :
-- Création de la procédure pour ajouter des lignes de commande automatiquement à la création d'une commande
CREATE OR REPLACE FUNCTION add_lignes_commande()
    RETURNS TRIGGER AS $$
DECLARE
    _nb_caisses integer;
    _nb_unites integer;
    _id_article integer;
BEGIN
    -- Parcourir les lignes_commande_par_defaut associées à la creche de la nouvelle commande
    FOR _id_article IN (
        SELECT lcpd.id_article
        FROM pfe.lignes_commande_par_defaut lcpd
        WHERE lcpd.id_creche = NEW.id_creche)
    LOOP
        -- Obtenir les quantités associées à l'article et à la creche
        _nb_caisses := (SELECT lcpd.nb_caisses
        FROM pfe.lignes_commande_par_defaut lcpd
        WHERE lcpd.id_creche = NEW.id_creche AND lcpd.id_article = _id_article);

        _nb_unites := (SELECT lcpd.nb_unites
        FROM pfe.lignes_commande_par_defaut lcpd
        WHERE lcpd.id_creche = NEW.id_creche AND lcpd.id_article = _id_article);

        -- Insérer une nouvelle ligne de commande avec les quantités spécifiées
        INSERT INTO pfe.lignes_commande (id_commande, id_article, nb_caisses, nb_unites)
        VALUES (NEW.id_commande, _id_article, _nb_caisses, _nb_unites);
    END LOOP;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Attacher le déclencheur à la table commandes
CREATE TRIGGER after_insert_commandes_trigger
AFTER INSERT ON pfe.commandes
FOR EACH ROW
WHEN (pg_trigger_depth() = 0)
EXECUTE PROCEDURE add_lignes_commande();


-- Suppléments

-- Trigger 3 :
-- Création de la procédure pour ajouter des suppléments à une tournée automatiquement à la création d'une tournée
CREATE OR REPLACE FUNCTION add_supplements()
    RETURNS TRIGGER AS $$
DECLARE
    _id_article integer;
BEGIN
    -- Parcourir les lignes_commande_par_defaut associées à la creche de la nouvelle commande
    FOR _id_article IN (
        SELECT a.id_article
        FROM pfe.articles a)
    LOOP
        -- Insérer une nouveau supplément
        INSERT INTO pfe.supplements (id_tournee, id_article)
        VALUES (NEW.id_tournee, _id_article);
    END LOOP;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Attacher le déclencheur à la table tournees
CREATE TRIGGER after_insert_tournees_trigger
AFTER INSERT ON pfe.tournees
FOR EACH ROW
EXECUTE PROCEDURE add_supplements();


-- Trigger 4 :
-- Création de la procédure pour mettre à jour le statut d'une tournée
CREATE OR REPLACE FUNCTION update_supplements()
    RETURNS TRIGGER AS $$
DECLARE
    _id_article INTEGER;
    _id_commande INTEGER;
    _id_tournee INTEGER;
    _nb_caisses_total INTEGER;
    _nb_unites_total INTEGER;
    _pourcentage INTEGER;
    _nb_caisses INTEGER;
    _nb_unites INTEGER;
BEGIN
    -- Déterminer l'identifiant de la commande et de l'article en fonction de l'opération (INSERT, UPDATE, DELETE)
    IF (TG_OP = 'DELETE' OR TG_OP = 'UPDATE') THEN
        _id_article = OLD.id_article;
        _id_commande = OLD.id_commande;
    ELSIF (TG_OP = 'INSERT') THEN
        _id_article = NEW.id_article;
        _id_commande = NEW.id_commande;
    END IF;

    -- Obtenir l'id de la tournée
    SELECT c.id_tournee INTO _id_tournee
    FROM pfe.commandes c, pfe.lignes_commande lc
    WHERE lc.id_commande = _id_commande AND lc.id_commande = c.id_commande;

    -- Calcul du nombre de caisses et d'unités total pour l'article de cette tournée
    SELECT sum(lc.nb_caisses), sum(lc.nb_unites) INTO _nb_caisses_total, _nb_unites_total
    FROM pfe.lignes_commande lc, pfe.commandes c
    WHERE lc.id_article = _id_article AND lc.id_commande = c.id_commande AND c.id_tournee = _id_tournee;

    -- Obtenir le pourcentage de supplément de caisses et d'unités pour l'article
    SELECT a.pourcentage INTO _pourcentage
    FROM pfe.articles a
    WHERE a.id_article = _id_article;

    -- Calcul du nombre de caisses et d'unités à ajouter en supplément
    _nb_caisses_total := CAST(_nb_caisses_total AS NUMERIC);
    _pourcentage := CAST(_pourcentage AS NUMERIC);
    SELECT ROUND((_nb_caisses_total * _pourcentage / 100.0), 0) INTO _nb_caisses;
    SELECT ROUND((_nb_unites_total * _pourcentage / 100.0), 0) INTO _nb_unites;

    -- Mettre à jour le supplément de caisses et d'unites de cet article pour cette tournée
    UPDATE pfe.supplements
    SET nb_caisses = _nb_caisses, nb_unites = _nb_unites
    WHERE id_tournee = _id_tournee AND id_article = _id_article;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Attacher le déclencheur à la table lignes_commande
CREATE TRIGGER after_insert_update_delete_lignes_commande_trigger
AFTER INSERT OR UPDATE OR DELETE ON pfe.lignes_commande
FOR EACH ROW
EXECUTE PROCEDURE update_supplements();


-- Tournées

-- Trigger 5 :
-- Création de la procédure pour mettre à jour le statut d'une tournée
CREATE OR REPLACE FUNCTION update_statut_tournees()
    RETURNS TRIGGER AS $$
DECLARE
    _statut_tournee VARCHAR(20);
    _statut_commande VARCHAR(20);
    _id_commande INTEGER;
    _id_tournee INTEGER;
BEGIN
    -- Déterminer l'identifiant de la tournée en fonction de l'opération (INSERT, UPDATE, DELETE)
    IF (TG_OP = 'DELETE' OR TG_OP = 'UPDATE') THEN
        _id_tournee = OLD.id_tournee;
    ELSIF (TG_OP = 'INSERT') THEN
        _id_tournee = NEW.id_tournee;
    END IF;

    -- Parcourir les commandes associées à la tournée
    FOR _id_commande IN (
        SELECT c.id_commande
        FROM pfe.commandes c
        WHERE c.id_tournee = _id_tournee)
    LOOP
        -- Obtenir le statut de la commande
        _statut_commande := (SELECT c.statut
        FROM pfe.commandes c
        WHERE c.id_commande = _id_commande);

        -- Mettre à jour le statut de la tournée en fonction du statut de la commande
        IF (_statut_commande = 'en cours') THEN
            _statut_tournee = 'en cours';
            EXIT;
        ELSIF (_statut_commande = 'en attente') THEN
            IF (_statut_tournee IS NOT NULL AND _statut_tournee = 'terminée') THEN
                _statut_tournee = 'en cours';
                EXIT;
            ELSE
                _statut_tournee = 'en attente';
            END IF;
        ELSIF (_statut_commande = 'terminée') THEN
            IF (_statut_tournee IS NOT NULL AND _statut_tournee = 'en attente') THEN
                _statut_tournee = 'en cours';
                EXIT;
            ELSE
                _statut_tournee = 'terminée';
            END IF;
        END IF;
    END LOOP;

    -- Mettre à jour le statut de la tournée si nécessaire
    IF (_statut_tournee = 'en cours' OR _statut_tournee = 'terminée') THEN
        UPDATE pfe.tournees
        SET statut = _statut_tournee
        WHERE id_tournee = _id_tournee;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Attacher le déclencheur à la table commandes
CREATE TRIGGER after_insert_update_delete_commandes_trigger
AFTER INSERT OR UPDATE OR DELETE ON pfe.commandes
FOR EACH ROW
WHEN (pg_trigger_depth() = 0)
EXECUTE PROCEDURE update_statut_tournees();


-- Ordre commandes

-- Trigger 6 :
-- Création de la procédure pour mettre à jour l'ordre des commandes avant l'insertion
CREATE OR REPLACE FUNCTION update_commandes_ordre_before_insert()
    RETURNS TRIGGER AS $$
DECLARE
    _max_ordre INTEGER;
BEGIN
    -- Mettre à jour l'ordre des commandes avec un ordre plus grand que celui à insérer
    UPDATE pfe.commandes
    SET ordre = ordre + 1
    WHERE id_tournee = OLD.id_tournee AND ordre >= NEW.ordre;

    -- Trouver le plus grand ordre pour la même tournée
    SELECT COALESCE(MAX(ordre), 0) INTO _max_ordre
    FROM pfe.commandes
    WHERE id_tournee = NEW.id_tournee;

    -- Ajuster l'ordre de la nouvelle commande
    IF (NEW.ordre >= _max_ordre) THEN
        NEW.ordre := _max_ordre + 1;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attacher le déclencheur avant l'insertion à la table commandes
CREATE TRIGGER before_insert_commandes_trigger
BEFORE INSERT ON pfe.commandes
FOR EACH ROW
WHEN (pg_trigger_depth() = 0)
EXECUTE PROCEDURE update_commandes_ordre_before_insert();


-- Trigger 7 :
-- Création de la procédure pour mettre à jour l'ordre des commandes avant l'update
CREATE OR REPLACE FUNCTION update_commandes_ordre_before_update()
    RETURNS TRIGGER AS $$
DECLARE
    _max_ordre INTEGER;
BEGIN
    IF (NEW.ordre < OLD.ordre) THEN
        -- Mettre à jour l'ordre des commandes avec un ordre plus grand que celui à insérer mais plus petit que l'ancien
        UPDATE pfe.commandes
        SET ordre = ordre + 1
        WHERE id_tournee = NEW.id_tournee AND ordre >= NEW.ordre AND ordre < OLD.ordre AND id_commande <> OLD.id_commande;
    ELSIF (NEW.ordre > OLD.ordre) THEN
        -- Mettre à jour l'ordre des commandes avec un ordre plus petit que celui à insérer mais plus grand que l'ancien
        UPDATE pfe.commandes
        SET ordre = ordre - 1
        WHERE id_tournee = NEW.id_tournee AND ordre <= NEW.ordre AND ordre > OLD.ordre AND id_commande <> OLD.id_commande;
    END IF;

    -- Trouver le plus grand ordre pour la même tournée
    SELECT MAX(ordre) INTO _max_ordre
    FROM pfe.commandes
    WHERE id_tournee = NEW.id_tournee AND id_commande <> OLD.id_commande;

    -- Ajuster l'ordre de la nouvelle commande
    IF (NEW.ordre > _max_ordre) THEN
        NEW.ordre := _max_ordre + 1;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attacher le déclencheur avant la mise à jour de la table commandes
CREATE TRIGGER before_update_commandes_trigger
BEFORE UPDATE ON pfe.commandes
FOR EACH ROW
WHEN (pg_trigger_depth() = 0)
EXECUTE PROCEDURE update_commandes_ordre_before_update();


-- Trigger 8 :
-- Création de la procédure pour mettre à jour l'ordre des commandes avant la suppression d'une commande
CREATE OR REPLACE FUNCTION update_commandes_ordre_before_delete()
    RETURNS TRIGGER AS $$
BEGIN
    -- Mettre à jour l'ordre des commandes avec un ordre plus grand que celui à supprimer
    UPDATE pfe.commandes
    SET ordre = ordre - 1
    WHERE id_tournee = OLD.id_tournee AND ordre > OLD.ordre AND OLD.id_commande <> commandes.id_commande;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Attacher le déclencheur avant une suppression dans la table commandes
CREATE TRIGGER before_delete_commandes_trigger
BEFORE DELETE ON pfe.commandes
FOR EACH ROW
WHEN (pg_trigger_depth() = 0)
EXECUTE PROCEDURE update_commandes_ordre_before_delete();


-- Trigger 9 :
-- Création de la procédure pour mettre à jour l'ordre des commandes par défaut avant l'insertion
CREATE OR REPLACE FUNCTION update_commandes_par_defaut_ordre_before_insert()
    RETURNS TRIGGER AS $$
DECLARE
    _max_ordre INTEGER;
BEGIN
    -- Mettre à jour l'ordre des commandes par défaut avec un ordre plus grand que celui à insérer
    UPDATE pfe.commandes_par_defaut
    SET ordre = ordre + 1
    WHERE id_tournee_par_defaut = NEW.id_tournee_par_defaut AND ordre >= NEW.ordre;

    -- Trouver le plus grand ordre pour la même tournée par défaut
    SELECT COALESCE(MAX(ordre), 0) INTO _max_ordre
    FROM pfe.commandes_par_defaut
    WHERE id_tournee_par_defaut = NEW.id_tournee_par_defaut;

    -- Ajuster l'ordre de la nouvelle commande par défaut
    IF (NEW.ordre >= _max_ordre) THEN
        NEW.ordre := _max_ordre + 1;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attacher le déclencheur avant l'insertion à la table commandes_par_defaut
CREATE TRIGGER before_insert_commandes_par_defaut_trigger
BEFORE INSERT ON pfe.commandes_par_defaut
FOR EACH ROW
WHEN (pg_trigger_depth() = 0)
EXECUTE PROCEDURE update_commandes_par_defaut_ordre_before_insert();


-- Trigger 10 :
-- Création de la procédure pour mettre à jour l'ordre des commandes par défaut avant l'update
CREATE OR REPLACE FUNCTION update_commandes_par_defaut_ordre_before_update()
    RETURNS TRIGGER AS $$
DECLARE
    _max_ordre INTEGER;
BEGIN
    IF (NEW.ordre < OLD.ordre) THEN
        -- Mettre à jour l'ordre des commandes par défaut avec un ordre plus grand que celui à insérer mais plus petit que l'ancien
        UPDATE pfe.commandes_par_defaut
        SET ordre = ordre + 1
        WHERE id_tournee_par_defaut = NEW.id_tournee_par_defaut AND ordre >= NEW.ordre AND ordre < OLD.ordre AND commandes_par_defaut.id_commande_par_defaut <> OLD.id_commande_par_defaut;
    ELSIF (NEW.ordre > OLD.ordre) THEN
        -- Mettre à jour l'ordre des commandes par défaut avec un ordre plus petit que celui à insérer mais plus grand que l'ancien
        UPDATE pfe.commandes_par_defaut
        SET ordre = ordre - 1
        WHERE id_tournee_par_defaut = NEW.id_tournee_par_defaut AND ordre <= NEW.ordre AND ordre > OLD.ordre AND commandes_par_defaut.id_commande_par_defaut <> OLD.id_commande_par_defaut;
    END IF;

    -- Trouver le plus grand ordre pour la même tournée par défaut
    SELECT MAX(ordre) INTO _max_ordre
    FROM pfe.commandes_par_defaut
    WHERE id_tournee_par_defaut = NEW.id_tournee_par_defaut AND commandes_par_defaut.id_commande_par_defaut <> OLD.id_commande_par_defaut;

    -- Ajuster l'ordre de la nouvelle commande par défaut
    IF (NEW.ordre > _max_ordre) THEN
        NEW.ordre := _max_ordre + 1;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attacher le déclencheur avant la mise à jour de la table commandes_par_defaut
CREATE TRIGGER before_update_commandes_par_defaut_trigger
BEFORE UPDATE ON pfe.commandes_par_defaut
FOR EACH ROW
WHEN (pg_trigger_depth() = 0)
EXECUTE PROCEDURE update_commandes_par_defaut_ordre_before_update();


-- Trigger 11 :
-- Création de la procédure pour mettre à jour l'ordre des commandes par défaut avant la suppression d'une commande
CREATE OR REPLACE FUNCTION update_commandes_par_defaut_ordre_before_delete()
    RETURNS TRIGGER AS $$
BEGIN
    -- Mettre à jour l'ordre des commandes par défaut avec un ordre plus grand que celui à supprimer
    UPDATE pfe.commandes_par_defaut
    SET ordre = ordre - 1
    WHERE id_tournee_par_defaut = OLD.id_tournee_par_defaut AND ordre > OLD.ordre AND OLD.id_commande_par_defaut <> commandes_par_defaut.id_commande_par_defaut;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Attacher le déclencheur avant la suppression dans la table commandes_par_defaut
CREATE TRIGGER before_delete_commandes_par_defaut_trigger
BEFORE DELETE ON pfe.commandes_par_defaut
FOR EACH ROW
WHEN (pg_trigger_depth() = 0)
EXECUTE PROCEDURE update_commandes_par_defaut_ordre_before_delete();
