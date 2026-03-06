CREATE TABLE categorie_depense(
   id_cd SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   created TIMESTAMP NOT NULL,
   type VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_cd)
);

CREATE TABLE categorie_depense_detail(
   id_cdd SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   created TIMESTAMP,
   PRIMARY KEY(id_cdd)
);

CREATE TABLE unite(
   id_unite SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_unite)
);

CREATE TABLE depense(
   id_depense SERIAL,
   created TIMESTAMP NOT NULL,
   est_prevue BOOLEAN NOT NULL,
   description VARCHAR(255) ,
   id_cd INTEGER NOT NULL,
   PRIMARY KEY(id_depense),
   FOREIGN KEY(id_cd) REFERENCES categorie_depense(id_cd)
);

CREATE TABLE depense_detail(
   id_depense_detail SERIAL,
   qte NUMERIC(15,2) NOT NULL,
   pu NUMERIC(15,2) NOT NULL,
   created TIMESTAMP NOT NULL,
   id_unite INTEGER NOT NULL,
   id_cdd INTEGER NOT NULL,
   id_depense INTEGER NOT NULL,
   designation VARCHAR (50),
   PRIMARY KEY(id_depense_detail),
   FOREIGN KEY(id_unite) REFERENCES unite(id_unite),
   FOREIGN KEY(id_cdd) REFERENCES categorie_depense_detail(id_cdd),
   FOREIGN KEY(id_depense) REFERENCES depense(id_depense)
);
