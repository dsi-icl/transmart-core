--
-- Type: TABLE; Owner: I2B2DEMODATA; Name: PATIENT_MAPPING
--
 CREATE TABLE "I2B2DEMODATA"."PATIENT_MAPPING" 
  (	"PATIENT_IDE" VARCHAR2(200 BYTE) NOT NULL ENABLE, 
"PATIENT_IDE_SOURCE" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
"PATIENT_NUM" NUMBER(38,0) NOT NULL ENABLE, 
"PATIENT_IDE_STATUS" VARCHAR2(50 BYTE), 
"UPLOAD_DATE" DATE, 
"UPDATE_DATE" DATE, 
"DOWNLOAD_DATE" DATE, 
"IMPORT_DATE" DATE, 
"SOURCESYSTEM_CD" VARCHAR2(50 BYTE), 
"UPLOAD_ID" NUMBER(38,0), 
 CONSTRAINT "PATIENT_MAPPING_PK" PRIMARY KEY ("PATIENT_IDE", "PATIENT_IDE_SOURCE")
 USING INDEX
 TABLESPACE "TRANSMART"  ENABLE
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" ;

--
-- Type: INDEX; Owner: I2B2DEMODATA; Name: PM_PATNUM_IDX
--
CREATE INDEX "I2B2DEMODATA"."PM_PATNUM_IDX" ON "I2B2DEMODATA"."PATIENT_MAPPING" ("PATIENT_NUM")
TABLESPACE "TRANSMART" ;

--
-- Table documentation
--
COMMENT ON TABLE i2b2demodata.patient_mapping IS 'Table with subject identifiers from different sources.';

COMMENT ON COLUMN patient_mapping.patient_ide IS 'Primary key. Subject identifier associated with a patient.';
COMMENT ON COLUMN patient_mapping.patient_ide_source IS 'Primary key. Source of the subject identifier.';
COMMENT ON COLUMN patient_mapping.patient_num IS 'The id of the patient in TranSMART. Refers to patient_num in patient_dimension.';
