<?php
$dependencies = array (
  'search_taxonomy_rels' => 
  array (
    0 => 'search_taxonomy',
    1 => 'search_taxonomy',
  ),
  'search_taxonomy' => 
  array (
    0 => 'search_keyword',
  ),
  'search_role_auth_user' => 
  array (
    0 => 'search_auth_user',
    1 => 'search_role',
  ),
  'search_gene_signature' => 
  array (
    0 => 'search_auth_user',
    1 => 'search_gene_sig_file_schema',
    2 => 'search_auth_user',
  ),
  'plugin_module' => 
  array (
    0 => 'plugin',
  ),
  'saved_faceted_search' => 
  array (
    0 => 'search_auth_user',
  ),
  'search_auth_sec_object_access' => 
  array (
    0 => 'search_auth_principal',
    1 => 'search_sec_access_level',
    2 => 'search_secure_object',
  ),
  'search_auth_group_member' => 
  array (
    0 => 'search_auth_group',
    1 => 'search_auth_principal',
  ),
  'search_keyword_term' => 
  array (
    0 => 'search_keyword',
  ),
  'search_auth_user_sec_access' => 
  array (
    0 => 'search_auth_user',
    1 => 'search_sec_access_level',
    2 => 'search_secure_object',
  ),
  'search_auth_group' => 
  array (
    0 => 'search_auth_principal',
  ),
  'search_auth_user' => 
  array (
    0 => 'search_auth_principal',
  ),
)
;