CREATE TABLE caisse_mvt(
   id_cm SERIAL,
   debit DOUBLE PRECISION NOT NULL,
   credit DOUBLE PRECISION NOT NULL,
   created TIMESTAMP NOT NULL,
   id_depense INTEGER,
   id_caisse INTEGER NOT NULL,
   PRIMARY KEY(id_cm),
   FOREIGN KEY(id_depense) REFERENCES depense(id_depense),
   FOREIGN KEY(id_caisse) REFERENCES caisse(id_caisse)
);

INSERT INTO caisse (nom, description, created, solde, id_caisse_categorie, id_utilisateur) 
VALUES ('portefeuille', 'Portefeuille maison', NOW  () , 0.0 , (SELECT id_caisse_categorie FROM caisse_categorie WHERE libelle = 'wallet') , 1);

ALTER TABLE depense ADD COLUMN montant_total  DOUBLE PRECISION  NOT NULL  DEFAULT 0.0;
