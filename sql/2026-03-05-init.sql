CREATE TABLE utilisateur(
   id_utilisateur SERIAL,
   nom VARCHAR(50)  NOT NULL,
   mdp VARCHAR(50)  NOT NULL,
   role VARCHAR(50) ,
   active BOOLEAN NOT NULL,
   validation_token VARCHAR(500),
   reset_token VARCHAR(500),
   email VARCHAR(255)  NOT NULL,
   created_token TIMESTAMP NULL,
   created_reset_token TIMESTAMP NULL,
   PRIMARY KEY(id_utilisateur),
   UNIQUE(nom),
   UNIQUE(email)
);




CREATE TABLE categorie_depense(
   id_cd SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   created TIMESTAMP NOT NULL,
   type VARCHAR(50)  NOT NULL,
   id_utilisateur INTEGER,
   PRIMARY KEY(id_cd),
   FOREIGN KEY(id_utilisateur) REFERENCES utilisateur(id_utilisateur)
);

CREATE TABLE depense(
   id_depense SERIAL,
   created TIMESTAMP NOT NULL,
   est_prevue BOOLEAN NOT NULL,
   description VARCHAR(255) ,
   id_utilisateur INTEGER NOT NULL,
   id_cd INTEGER NOT NULL,
   PRIMARY KEY(id_depense),
   FOREIGN KEY(id_utilisateur) REFERENCES utilisateur(id_utilisateur),
   FOREIGN KEY(id_cd) REFERENCES categorie_depense(id_cd)
);


CREATE TABLE unite(
   id_unite SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_unite)
);

CREATE TABLE categorie_depense_detail(
   id_cdd SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   created TIMESTAMP,
   id_cd INTEGER NOT NULL,
   id_utilisateur INTEGER,
   PRIMARY KEY(id_cdd),
   FOREIGN KEY(id_cd) REFERENCES categorie_depense(id_cd)
   FOREIGN KEY(id_utilisateur) REFERENCES utilisateur(id_utilisateur)
);

CREATE TABLE depense_detail(
   id_depense_detail SERIAL,
   qte NUMERIC(15,2)   NOT NULL,
   pu NUMERIC(15,2)   NOT NULL,
   created TIMESTAMP NOT NULL,
   designation VARCHAR(50) ,
   id_depense INTEGER NOT NULL,
   id_cdd INTEGER NOT NULL,
   id_unite INTEGER NOT NULL,
   PRIMARY KEY(id_depense_detail),
   FOREIGN KEY(id_depense) REFERENCES depense(id_depense),
   FOREIGN KEY(id_cdd) REFERENCES categorie_depense_detail(id_cdd),
   FOREIGN KEY(id_unite) REFERENCES unite(id_unite)
);



CREATE TABLE param(
   id_param SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   valeur VARCHAR(255)  NOT NULL,
   PRIMARY KEY(id_param),
   UNIQUE(libelle)
);





