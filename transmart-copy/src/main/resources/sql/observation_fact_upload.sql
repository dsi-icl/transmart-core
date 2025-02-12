create temporary table observation_fact_upload (
  id serial primary key,
  encounter_num numeric(38,0) not null,
  patient_num numeric(38,0) not null,
  concept_cd character varying(50) not null,
  provider_id character varying(50) not null,
  start_date timestamp without time zone not null,
  modifier_cd character varying(100) not null,
  instance_num numeric(18,0),
  trial_visit_num numeric(38,0),
  valtype_cd character varying(50),
  tval_char character varying(255),
  nval_num numeric(18,5),
  valueflag_cd character varying(50),
  quantity_num numeric(18,5),
  units_cd character varying(50),
  end_date timestamp without time zone,
  location_cd character varying(50),
  observation_blob text,
  confidence_num numeric(18,5),
  update_date timestamp without time zone,
  download_date timestamp without time zone,
  import_date timestamp without time zone,
  sourcesystem_cd character varying(50),
  upload_id numeric(38,0),
  sample_cd character varying(200)
);
