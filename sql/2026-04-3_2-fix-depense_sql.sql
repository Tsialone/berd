ALTER TABLE caisse ALTER  COLUMN id_caisse_categorie SET   NOT NULL ;
ALTER TABLE caisse DROP CONSTRAINT  caisse_id_caisse_categorie_key  ;
TRUNCATE TABLE caisse_categorie RESTART IDENTITY CASCADE ;
ALTER TABLE caisse_categorie ADD CONSTRAINT caisse_categorie_libelle_key   UNIQUE (libelle) ;

INSERT INTO caisse_categorie (libelle , created) VALUES ('wallet' , NOW ());
INSERT INTO caisse_categorie (libelle , created) VALUES ('mvola' , NOW ());
INSERT INTO caisse_categorie (libelle , created) VALUES ('orange money' , NOW ());
INSERT INTO caisse_categorie (libelle , created) VALUES ('banque' , NOW ());