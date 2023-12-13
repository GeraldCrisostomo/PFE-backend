-- Feeding des tables --------------------------------------------------------------------------------------------------

-- Livreurs
-- Utilisateurs
INSERT INTO pfe.utilisateurs (nom, prenom, identifiant, mot_de_passe, role) VALUES
    ('Admin',   'Istrateur',    'admin',    '$2a$10$mX2DIQFmeOar85dNpBbrY.wmGMQmWXy2Nawf92J98OnUq8i7VKraG', 'admin'),
    ('Livre',   'Heure',        'livreur',  '$2a$10$N4X.CfjUdhGDIuBk0fEHe.EJoxyGaFwX8zhIBMowdcINAelNJu0bK', 'livreur');

-- Articles
INSERT INTO pfe.articles (libelle, taille, pourcentage) VALUES
    ('Langes',              'S',    10),
    ('Langes',              'M',    10),
    ('Langes',              'L',    10),
    ('Inserts',             NULL,   10),
    ('Sacs-poubelle',       NULL,   10),
    ('Gants de toilette',   NULL,   10);

-- Creches
INSERT INTO pfe.creches (nom, ville, rue) VALUES
    ('Rêverie',             'Gouy-Lez-Piéton',  'Rue Francisco Ferrer 19 boite 3'),
    ('Les ptits loups',     'Marcinelle',       'Rue de la Vielle Place 51'),
    ('L arbre à cabane',    'Gosselies',        'Chaussée de Nivelles 212'),
    ('Les lutins',          'Wanfercée-Baulet', 'Rue de Tamines 18'),
    ('Les Tiffins',         'La Hulpe',         'Rue des Combattants, 59'),
    ('Cardinal Mercier',    'Bruxelles',        'Rue Souveraine 48'),
    ('Les Poussins',        'Saint-Gilles',     'Av. Ducpétiaux 16'),
    ('Saint Joseph',        'Ixelles',          'Chaussée de Boisfort 40'),
    ('MMI',                 'Edingen',          'Dorpsstraat 76'),
    ('Royaume',             'Enghien',          'Chau. d''Asse 130'),
    ('Gratty',              'Silly',            'Place communale 17'),
    ('Boulous',             'Petit-Enghien',    'Rue de la Coquiane 61'),
    ('IRSIA',               'Colfontaine',      'Place de Pâturages, 41');



-- Tournees_par_defaut
INSERT INTO pfe.tournees_par_defaut (nom_par_defaut) VALUES
    ('Tournée Charleroi'),
    ('Tournée Mons');

-- Commandes_par_defaut
INSERT INTO pfe.commandes_par_defaut (id_tournee_par_defaut, id_creche, ordre) VALUES
    (1, 1,  1),
    (1, 2,  2),
    (1, 3,  3),
    (1, 4,  4),
    (1, 5,  5),
    (1, 6,  6),
    (1, 7,  7),
    (1, 8,  8),
    (2, 9,  1),
    (2, 10, 2),
    (2, 11, 3),
    (2, 12, 4),
    (2, 13, 5);

-- Lignes_commande_par_defaut
UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 1 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 3, nb_unites = 0
WHERE id_creche = 1 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 1 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 1 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 10
WHERE id_creche = 1 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 2, nb_unites = 0
WHERE id_creche = 1 AND id_article = 6;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 2 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 3, nb_unites = 0
WHERE id_creche = 2 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 2 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 2 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 6
WHERE id_creche = 2 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 2 AND id_article = 6;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 3 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 2, nb_unites = 0
WHERE id_creche = 3 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 3 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 3 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 8
WHERE id_creche = 3 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 3 AND id_article = 6;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 4 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 5, nb_unites = 0
WHERE id_creche = 4 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 2, nb_unites = 0
WHERE id_creche = 4 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 2, nb_unites = 0
WHERE id_creche = 4 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 12
WHERE id_creche = 4 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 4 AND id_article = 6;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 5 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 3, nb_unites = 0
WHERE id_creche = 5 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 2, nb_unites = 0
WHERE id_creche = 5 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 2, nb_unites = 0
WHERE id_creche = 5 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 12
WHERE id_creche = 5 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 5 AND id_article = 6;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 25
WHERE id_creche = 6 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 5, nb_unites = 0
WHERE id_creche = 6 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 6 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 6 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 14
WHERE id_creche = 6 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 6 AND id_article = 6;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 7 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 7 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 25
WHERE id_creche = 7 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 7 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 4
WHERE id_creche = 7 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 7 AND id_article = 6;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 8 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 3, nb_unites = 0
WHERE id_creche = 8 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 25
WHERE id_creche = 8 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 8 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 14
WHERE id_creche = 8 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 8 AND id_article = 6;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 9 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 25
WHERE id_creche = 9 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 25
WHERE id_creche = 9 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 25
WHERE id_creche = 9 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 8
WHERE id_creche = 9 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 9 AND id_article = 6;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 10 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 3, nb_unites = 0
WHERE id_creche = 10 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 25
WHERE id_creche = 10 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 10 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 6
WHERE id_creche = 10 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 10 AND id_article = 6;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 11 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 2, nb_unites = 0
WHERE id_creche = 11 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 11 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 11 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 6
WHERE id_creche = 11 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 11 AND id_article = 6;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 25
WHERE id_creche = 12 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 2, nb_unites = 0
WHERE id_creche = 12 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 25
WHERE id_creche = 12 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 12 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 8
WHERE id_creche = 12 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 12 AND id_article = 6;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 2, nb_unites = 0
WHERE id_creche = 13 AND id_article = 1;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 6, nb_unites = 0
WHERE id_creche = 13 AND id_article = 2;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 13 AND id_article = 3;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 1, nb_unites = 0
WHERE id_creche = 13 AND id_article = 4;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 35
WHERE id_creche = 13 AND id_article = 5;

UPDATE pfe.lignes_commande_par_defaut
SET nb_caisses = 0, nb_unites = 0
WHERE id_creche = 13 AND id_article = 6;

-- Tournees
INSERT INTO pfe.tournees (id_livreur, date, nom, statut) VALUES
    (1, '2023-12-13', 'Tournée Charleroi',  'terminée'),
    (2, '2023-12-13', 'Tournée Mons',       'en attente');

-- Commandes
INSERT INTO pfe.commandes (id_tournee, id_creche, ordre, statut) VALUES
    (1, 1,  1, 'terminée'),
    (1, 2,  2, 'terminée'),
    (1, 3,  3, 'terminée'),
    (1, 4,  4, 'terminée'),
    (1, 5,  5, 'terminée'),
    (1, 6,  6, 'terminée'),
    (1, 7,  7, 'terminée'),
    (1, 8,  8, 'terminée'),
    (2, 9,  1, 'en attente'),
    (2, 10, 2, 'en attente'),
    (2, 11, 3, 'en attente'),
    (2, 12, 4, 'en attente'),
    (2, 13, 5, 'en attente');
