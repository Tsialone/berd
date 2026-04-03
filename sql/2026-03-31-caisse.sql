CREATE TABLE caisse_categorie(
   id_caisse_categorie SERIAL,
   libelle VARCHAR(255)  NOT NULL,
   created TIMESTAMP NOT NULL,
   PRIMARY KEY(id_caisse_categorie)
);


CREATE TABLE caisse(
   id_caisse SERIAL,
   nom VARCHAR(255)  NOT NULL,
   description TEXT,
   created TIMESTAMP NOT NULL,
   solde  DOUBLE PRECISION NOT NULL,
   id_caisse_categorie INTEGER NOT NULL,
   id_utilisateur INTEGER NOT NULL,
   PRIMARY KEY(id_caisse),
   UNIQUE(id_caisse_categorie),
   FOREIGN KEY(id_caisse_categorie) REFERENCES caisse_categorie(id_caisse_categorie),
   FOREIGN KEY(id_utilisateur) REFERENCES utilisateur(id_utilisateur)
);

INSERT INTO caisse_categorie (libelle , created) VALUES ('wallet' , NOW ());
INSERT INTO caisse_categorie (libelle , created) VALUES ('mvola' , NOW ());
INSERT INTO caisse_categorie (libelle , created) VALUES ('orange money' , NOW ());
INSERT INTO caisse_categorie (libelle , created) VALUES ('banque' , NOW ());




