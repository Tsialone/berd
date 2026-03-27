-- Insertion des exemples cités
INSERT INTO categorie_depense (libelle, type, created) VALUES 
('entretien_moto', 'MAINTENANCE', CURRENT_TIMESTAMP),
('sakafo', 'ALIMENTATION', CURRENT_TIMESTAMP),
('deplacement', 'TRANSPORT', CURRENT_TIMESTAMP);

-- Autres exemples courants
INSERT INTO categorie_depense (libelle, type, created) VALUES 
('loyer', 'FIXE', CURRENT_TIMESTAMP),
('internet', 'ABONNEMENT', CURRENT_TIMESTAMP),
('loisirs', 'VARIABLE', CURRENT_TIMESTAMP);     


-- Insertion des unités demandées
INSERT INTO unite (libelle) VALUES 
('kpc'),     -- Kilo / Pièce ?
('l'),       -- Litre
('unite');   -- Unité standard

-- Autres unités souvent utiles pour la gestion de dépenses
INSERT INTO unite (libelle) VALUES 
('kg'),      -- Kilogramme
('m'),       -- Mètre
('forfait'); -- Forfait / Global


INSERT INTO categorie_depense_detail (libelle, id_cd, created) VALUES 
('Vidange moteur', 1, CURRENT_TIMESTAMP),
('Plaquettes de frein', 1, CURRENT_TIMESTAMP),
('Achat riz', 2, CURRENT_TIMESTAMP),
('Légumes frais', 2, CURRENT_TIMESTAMP),
('Ticket de bus', 3, CURRENT_TIMESTAMP),
('Frais de parking', 3, CURRENT_TIMESTAMP),
('Facture internet', 4, CURRENT_TIMESTAMP),
('Abonnement Netflix', 4, CURRENT_TIMESTAMP);


INSERT INTO param (libelle, valeur) VALUES 
('expiration_token', '5'); -- Durée de validité des tokens en minutes


INSERT INTO utilisateur (nom , mdp , role , active , email) VALUES ('Tsialone' , 'test' , 'ROLE_USER' , true, 'tsialone1902@gmail.com');
INSERT INTO utilisateur (nom , mdp , role , active , email) VALUES ('Admin' , 'test' , 'ROLE_USER' , true, 'toto@gmail.com');
