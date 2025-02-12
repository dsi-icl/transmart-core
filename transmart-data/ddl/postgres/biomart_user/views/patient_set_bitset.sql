--
-- Name: patient_set_bitset; Type: VIEW; Schema: biomart_user; Owner: -
--
CREATE VIEW biomart_user.patient_set_bitset AS
SELECT
  collection.result_instance_id AS result_instance_id,
  (bit_or(patient_num_boundaries.one << (collection.patient_num - patient_num_boundaries.min_patient_num)::INTEGER)) AS patient_set
FROM biomart_user.patient_num_boundaries, i2b2demodata.qt_patient_set_collection collection
GROUP BY collection.result_instance_id;