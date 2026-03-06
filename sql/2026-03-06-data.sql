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


INSERT INTO categorie_depense_detail (libelle, created) VALUES 
('Vidange moteur', CURRENT_TIMESTAMP),
('Plaquettes de frein', CURRENT_TIMESTAMP),
('Achat riz', CURRENT_TIMESTAMP),
('Légumes frais', CURRENT_TIMESTAMP),
('Ticket de bus', CURRENT_TIMESTAMP),
('Frais de parking', CURRENT_TIMESTAMP),
('Facture internet', CURRENT_TIMESTAMP),
('Abonnement Netflix', CURRENT_TIMESTAMP);
