ALTER TABLE caisse ALTER  COLUMN id_caisse_categorie SET   NOT NULL ;
ALTER TABLE caisse DROP CONSTRAINT  caisse_id_caisse_categorie_key  ;