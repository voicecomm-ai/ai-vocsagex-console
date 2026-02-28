package cn.voicecomm.ai.voicesagex.console.api.constant.graph;


/**
 * System 错误码枚举类
 */
public interface ErrorConstants {

  // 图空间模块
//    ErrorCode SPACE_NAME_DUPLICATE = new ErrorCode(500, "图空间名称不能重复");
  ErrorCode SPACE_NAME_DUPLICATE = new ErrorCode(500, "error.space_name_duplicate");
  ErrorCode SPACE_NAME_FILE_DUPLICATE = new ErrorCode(500, "error.space_name_file_duplicate");
  ErrorCode DEFAULT_SPACE = new ErrorCode(500, "error.default_space_missing");
  ErrorCode TEMPALTE_NAME_DUPLICATE = new ErrorCode(500, "error.template_name_duplicate");
  ErrorCode SPACE_NAME_CREATE = new ErrorCode(500, "error.space_name_create");
  ErrorCode CHECK_SPACE_ERROR = new ErrorCode(500, "error.check_space_error");
  ErrorCode CHECK_ROBIT_ERROR = new ErrorCode(500, "error.check_robot_error");
  ErrorCode CHECK_SITE_ERROR = new ErrorCode(500, "error.check_site_error");
  ErrorCode SPACE_NAME_CREATE1 = new ErrorCode(500, "error.space_name_create1");
  ErrorCode SPACE_TEMPLATE = new ErrorCode(500, "error.space_template");
  ErrorCode SPACE_TEMPLATE_ERROR = new ErrorCode(500, "error.space_template_error");
  ErrorCode SPACE_MYSQL_ERROR = new ErrorCode(500, "error.space_mysql_error");
  ErrorCode SPACE_ROBOT_ERROR = new ErrorCode(500, "error.space_robot_error");
  ErrorCode SPACE_READ_WRITE_ERROR = new ErrorCode(500, "error.space_read_write_error");
  ErrorCode SPACE_NEBULA_ERROR = new ErrorCode(500, "error.space_nebula_error");
  ErrorCode NOT_SUFFICIENT_FUNDS = new ErrorCode(500, "error.not_sufficient_funds");
  ErrorCode SPACE_NAME_CHECK = new ErrorCode(500, "error.space_name_check");
  ErrorCode SPACE_JOB_CHECK = new ErrorCode(500, "error.space_job_check");
  ErrorCode SPACE_JOB_FLUSH = new ErrorCode(500, "error.space_job_flush");
  ErrorCode SPACE_INDEX_TAG_REBUILD = new ErrorCode(500, "error.space_index_tag_rebuild");
  ErrorCode SPACE_INDEX_EDGE_REBUILD = new ErrorCode(500, "error.space_index_edge_rebuild");
  ErrorCode SPACE_JOB_INFO = new ErrorCode(500, "error.space_job_info");
  ErrorCode SPACE_NAME_ALL = new ErrorCode(500, "error.space_name_all");
  ErrorCode SPACE_NAME_USE = new ErrorCode(500, "error.space_name_use");
  ErrorCode SPACE_NAME_DROP = new ErrorCode(500, "error.space_name_drop");
  ErrorCode SPACE_NAME_TARGET = new ErrorCode(500, "error.space_name_target");
  ErrorCode UPVOTE_FAIL = new ErrorCode(500, "error.upvote_fail");
  ErrorCode Q_AND_A = new ErrorCode(500, "error.q_and_a");
  ErrorCode ROOBOT_STATIC_ERROR = new ErrorCode(500, "error.roobot_static_error");
  ErrorCode ROOBOT_MODEL_ERROR = new ErrorCode(500, "error.roobot_model_error");


  // 点模块
  ErrorCode TAG_NAME_CREATE = new ErrorCode(500, "error.tag_name_create");
  ErrorCode TAG_NAME_ALTER = new ErrorCode(500, "error.tag_name_alter");
  ErrorCode TTLCOL_FILED = new ErrorCode(500, "ttlCol 字段不存在");
  ErrorCode TAG_NAME_CHECK = new ErrorCode(500, "error.tag_name_check");
  ErrorCode GRAPH_PATTERN = new ErrorCode(500, "error.graph_pattern");
  ErrorCode TAG_EDGE_EXISTS = new ErrorCode(500, "error.tag_edge_exists");
  ErrorCode DROP_TAG = new ErrorCode(500, "error.drop_tag");
  ErrorCode DROP_TAG_PROPERTY = new ErrorCode(500, "error.drop_tag_property");
  ErrorCode UPDATE_TAG_PROPERTY = new ErrorCode(500, "error.update_tag_property");
  ErrorCode DROP_TAG_EDGE_TTL = new ErrorCode(500, "error.drop_tag_edge_ttl");
  ErrorCode GET_ALL_TAGEDGE = new ErrorCode(500, "error.get_all_tagedge");
  ErrorCode TAG_EDGE_IS_EXISTS = new ErrorCode(500, "error.tag_edge_is_exists");
  ErrorCode SAME_NAME_EDGE_IS_EXISTS = new ErrorCode(500, "error.same.name.edge.exists");
  ErrorCode SAME_TTL = new ErrorCode(500, "error.same_ttl_invalid");
  ErrorCode SAME_NAME_PROPERTIES_IS_EXISTS = new ErrorCode(500,
      "error.same_name_properties_exists");
  ErrorCode SAME_NAME_PROPERTIES = new ErrorCode(500, "error.same_name_properties_invalid");
  ErrorCode SAME_NAME = new ErrorCode(500, "error.name_default_exists");
  ErrorCode UPDATE_TAG_EDGE_PROPERTY = new ErrorCode(500,
      "error.update_tag_edge_property_restricted");
  ErrorCode UPDATE_TAG_TTL_PROPERTY = new ErrorCode(500, "error.update_tag_ttl_property_locked");
  ErrorCode UPDATE_TAG_EDGE_INT_PROPERTY = new ErrorCode(500,
      "error.update_tag_int_length_restriction");
  ErrorCode UPDATE_TAG_EDGE_INT = new ErrorCode(500, "error.update_tag_type_change_forbidden");
  ErrorCode UPDATE_TAG_EDGE_DOUBLE_PROPERTY = new ErrorCode(500, "error.update_tag_double_upgrade");
  ErrorCode UPDATE_TAG_EDGE_STRING_PROPERTY = new ErrorCode(500, "error.update_tag_string_upgrade");
  ErrorCode DELTE_PRPPERTY_ERROR = new ErrorCode(500, "error.delete_property_value_exists");
  ErrorCode UPLOWD_FILE = new ErrorCode(500, "error.uplowd_file");
  ErrorCode UPLOWD_FILE_XLSX = new ErrorCode(500, "error.uplowd_file_type");
  ErrorCode JOB_EXISTS = new ErrorCode(500, "error.job_id_not_exists");
  ErrorCode GET_FILE = new ErrorCode(500, "error.file_retrieve_failed");
  ErrorCode GET_FILE_ADDRESS = new ErrorCode(500, "error.file_path_not_found");
  ErrorCode DROP_ALL_DATA = new ErrorCode(500, "error.drop_all_data_restricted");
  ErrorCode DROP_ALL_EDGE_DATA = new ErrorCode(500, "error.drop_all_edge_data_restricted");


  // 实体模块
  ErrorCode VERTEX_NAME_DELETE = new ErrorCode(500, "error.entity_delete_failed");
  ErrorCode RELATION_NAME_DELETE = new ErrorCode(500, "error.relation_delete_failed");
  ErrorCode SAVE_VERTEX = new ErrorCode(500, "error.entity_save_failed");
  ErrorCode LOCK_GET_ERROR = new ErrorCode(500, "error.lock_acquire_interrupted");
  ErrorCode ENTITY_NAME = new ErrorCode(500, "error.entity_name_invalid");
  ErrorCode PERPROTY_EXPIRED = new ErrorCode(500, "error.property_expired_conflict");
  ErrorCode SUBJECT_PERPROTY_EXPIRED = new ErrorCode(500,
      "error.subject_property_expired_conflict");
  ErrorCode OBJECT_PERPROTY_EXPIRED = new ErrorCode(500, "error.object_property_expired_conflict");
  ErrorCode EXCEL_PERPROTY_EXPIRED = new ErrorCode(500, "error.excel_property_expired_conflict");
  ErrorCode PERPROTY_EXPIRED_UPDATE = new ErrorCode(500, "error.property_expired_update_conflict");
  ErrorCode ENTITY_SAVE_NAME = new ErrorCode(500, "error.entity_save_name_conflict");
  ErrorCode SCREEN_TAG_ERROR = new ErrorCode(500, "error.screen_tag_failed");
  ErrorCode UPDATE_VERTEX = new ErrorCode(500, "error.entity_update_failed");
  ErrorCode ENTITY_RELATEION = new ErrorCode(500, "error.entity_create_failed");
  ErrorCode EXISTS_RELATEION = new ErrorCode(500, "error.relationship_exists");
  ErrorCode SAVE_RELATEION = new ErrorCode(500, "error.relationship_save_failed");
  ErrorCode GET_ENTITY = new ErrorCode(500, "error.entity_retrieve_failed");
  ErrorCode GET_EDGE = new ErrorCode(500, "error.relationship_retrieve_failed");
  ErrorCode EXPORT_GET_EDGE = new ErrorCode(500, "error.relationship_export_failed");
  ErrorCode GET_ENTITY_ALL = new ErrorCode(500, "error.entity_all_retrieve_failed");
  ErrorCode GET_ENTITY_NODE_INFO = new ErrorCode(500, "error.entity_node_info_failed");
  ErrorCode GET_EXTEND_NODE_INFO = new ErrorCode(500, "error.entity_extension_check_failed");
  ErrorCode GET_ENTITY_NUMBER = new ErrorCode(500, "error.entity_count_failed");
  ErrorCode GET_SPACE_INFO_ALL = new ErrorCode(500, "error.space_node_info_failed");
  ErrorCode GET_RELATION_ALL = new ErrorCode(500, "error.relationship_all_retrieve_failed");
  ErrorCode GET_RELATION_NUMBER = new ErrorCode(500, "error.relationship_count_failed");
  ErrorCode UPDATE_ENTITY_ALL = new ErrorCode(500, "error.entity_update_all_failed");
  ErrorCode GET_ENTITY_PROPERTY_ALL = new ErrorCode(500,
      "error.entity_property_all_retrieve_failed");


  // 导入导出模块
  ErrorCode EXPORT_ERROP = new ErrorCode(500, "error.export_entity_failed");
  ErrorCode EXPORT_ERROP_TEMPTE = new ErrorCode(500, "error.export_template_invalid");
  ErrorCode IMPORT_ERROP_TEMPTE = new ErrorCode(500, "error.import_file_empty");
  ErrorCode IMPORT_ERROP_HEADER = new ErrorCode(500, "error.import_header.missing");
  ErrorCode IMPORT_ERROP_HEADER_MAP = new ErrorCode(500, "error.import_header.invalid");
  ErrorCode IMPORT_ERROP_DATA = new ErrorCode(500, "error.import_data_empty");
  ErrorCode IMPORT_ERROP_DATA_MAX = new ErrorCode(500, "error.import_data_max");


  // 图谱可视化模块
  ErrorCode GET_RAND_VERTEX = new ErrorCode(500, "error.random_vertex_failed");
  ErrorCode GET_LIKE_VERTEX = new ErrorCode(500, "error.fuzzy_vertex_query");
  ErrorCode GET_VERTEX = new ErrorCode(500, "error.vertex_retrieve_failed");
  ErrorCode GET_SUBGRAPH = new ErrorCode(500, "error.subgraph_query_failed");
  ErrorCode QUEREY_PATH = new ErrorCode(500, "error.path_query_failed");
  ErrorCode GET_VERTEX_TAG = new ErrorCode(500, "error.vertex_tag_failed");


  // 知识融合模块
  ErrorCode GET_ALL_NGEDGE = new ErrorCode(500, "error.ngedge_retrieve_failed");
  ErrorCode AFFIRMFUSION = new ErrorCode(500, "error.knowledge_fusion_failed");


  // 知识抽取模块
  ErrorCode INSERT_EXTRACTION_JOB = new ErrorCode(500, "error.job_create_failed");
  ErrorCode INSERT_EXTRACTION_JOB_SAME = new ErrorCode(500, "error.job_name_duplicate");
  ErrorCode UPDATE_EXTRACTION_JOB = new ErrorCode(500, "error.job_update_failed");
  ErrorCode DELETE_EXTRACTION_JOB = new ErrorCode(500, "error.job_delete_failed");
  ErrorCode GET_EXTRACTION_JOB = new ErrorCode(500, "error.job_retrieve_failed");
  ErrorCode GET_DOCUMENT_INFO = new ErrorCode(500, "error.document_info_failed");
  ErrorCode MOVE_FILE_ERROR = new ErrorCode(500, "error.file_move_failed");
  ErrorCode INSERT_DOCUMENT_ERROR = new ErrorCode(500, "error.document_create_failed");
  ErrorCode DELETE_DOCUMENT_ERROR = new ErrorCode(500, "error.document_delete_failed");
  ErrorCode UPDATE_DOCUMENT_ERROR = new ErrorCode(500, "error.parse_line_up");
  ErrorCode PARSE_LINE_UP = new ErrorCode(500, "error.document_update_failed");
  ErrorCode INVOKE_DOCUMENT = new ErrorCode(500, "error.remote_invoke_failed");
  ErrorCode PROCESS_UPLOAD_FILE = new ErrorCode(500, "error.process_upload_file_error");
  ErrorCode EXTRACT_INVOKE_DOCUMENT = new ErrorCode(500, "error.extraction_invoke_failed");
  ErrorCode CALLBACK_INVOKE_DOCUMENT_FAIL = new ErrorCode(500, "error.callback_invoke_failed");
  ErrorCode EXTRACT_DOCUMENT = new ErrorCode(500, "error.document_extract_failed");
  ErrorCode EXTRACT_DOCUMENT_ERROR = new ErrorCode(500, "error.callback_execution_failed");
  ErrorCode VERIFICATION_DOCUMENT_ERROR = new ErrorCode(500, "error.verification_failed");
  ErrorCode VERIFICATION_DOCUMENT_INFO_ERROR = new ErrorCode(500, "error.verification_list_failed");
  ErrorCode DROP_VERIFICATION_STATUS = new ErrorCode(500, "error.verification_status_failed");
  ErrorCode GET_VERIFICATION_INFO = new ErrorCode(500, "error.verification_info_failed");
  ErrorCode BATCH_VERIFICATION_INFO = new ErrorCode(500, "error.batch_verification_failed");
  ErrorCode BATCH_VERIFICATION_DELETE_INFO = new ErrorCode(500,
      "error.batch_verification_delete_failed");
  ErrorCode GET_SUBJECT_OBJECT_TYPE = new ErrorCode(500, "error.subject_object_type_failed");
  ErrorCode GET_EDGE_PROPERTY_INFO = new ErrorCode(500, "error.edge_property_failed");
  ErrorCode ADD_KNOWLEDGE_INFO = new ErrorCode(500, "error.knowledge_info_add_failed");
  ErrorCode UPDATE_KNOWLEDGE_INFO = new ErrorCode(500, "error.knowledge_info_update_failed");
  ErrorCode UPDATE_KNOWLEDGE_ERROR = new ErrorCode(500, "error.knowledge_type_mismatch");
  ErrorCode UPDATE_EDGE_ERROR = new ErrorCode(500, "error.edge_type_mismatch");
  ErrorCode INPUT_ERROR_RANGE = new ErrorCode(500, "error.attribute_range_invalid");
  ErrorCode VERIFY_DATA_GRAPH = new ErrorCode(500, "error.data_verification_failed");
  ErrorCode VERIFY_DATA_TOTAL = new ErrorCode(500, "error.statistics_retrieve_failed");
  ErrorCode VERIFY_DATA_TOTAL_CHUNK = new ErrorCode(500, "error.chunk_retrieve_failed");
  ErrorCode UPDATE_FILE_SIZE = new ErrorCode(500, "error.file_size_limit");
  ErrorCode FILE_NAME_EMPTY = new ErrorCode(500, "error.filename_empty");
  ErrorCode FILE_NAME_EXISTS = new ErrorCode(500, "error.filename_exists");
  ErrorCode FILE_NAME_FORMAT = new ErrorCode(500, "error.file_format_invalid");
  ErrorCode FILE_ONE_SIZE = new ErrorCode(500, "error.file_size_single");
  ErrorCode FILE_UPLOAD_FILE = new ErrorCode(500, "error.file_upload_failed");
  ErrorCode GET_DOCUMENT_CONFIG = new ErrorCode(500, "error.get_file_config_failed");
  ErrorCode GET_DOCUMENT_PREVIEW = new ErrorCode(500, "error.get_file_preview_failed");
  ErrorCode JOB_STATUS = new ErrorCode(500, "获取解析任务状态失败");
  ErrorCode EXTRACT_LINEUP = new ErrorCode(500, "error.queue_count_failed");
  ErrorCode PARSE_LINEUP_ERROR = new ErrorCode(500, "error.parse_lineup_error");
  ErrorCode DOCUMENT_CONFIG = new ErrorCode(500, "error.file_config_failed");


  // 向量数据库模块
  ErrorCode CREATE_COLLECTION_INFO = new ErrorCode(500, "error.vector_collection_failed");
  ErrorCode VECTOR_CALLBACK_INFO = new ErrorCode(500, "error.vector_callback_failed");
  ErrorCode VECTOR_UPDATE_ERROR_INFO = new ErrorCode(500, "error.vector_update_failed");
  ErrorCode VECTOR_DELETE_ERROR_INFO = new ErrorCode(500, "error.vector_delete_failed");
  ErrorCode VECTOR_DELETE_ERROR = new ErrorCode(500, "error.vector_entity_failed");
  ErrorCode GET_VECTOR = new ErrorCode(500, "error.vector_multi_hop_failed");
  ErrorCode CREATE_TAG_EDGE = new ErrorCode(500, "error.vector_tag_edge_failed");


  // 问答模块
  ErrorCode SAME_ROBOT_NAME = new ErrorCode(500, "error.robot_name_duplicate");
  ErrorCode SAVE_ROBOT_NAME = new ErrorCode(500, "error.robot_create_failed");
  ErrorCode SAVE_ROBOT_ERROR_NAME = new ErrorCode(500, "error.robot_concurrency_failed");
  ErrorCode UPDATE_ROBOT_NAME = new ErrorCode(500, "error.robot_update_failed");
  ErrorCode NEW_SESSION = new ErrorCode(500, "error.session_create_failed");
  ErrorCode SESSION_GET_ERROR = new ErrorCode(500, "error.session_timeout");
  ErrorCode SESSION_LIMIT = new ErrorCode(500, "error.session_limit");
  ErrorCode SESSION_SHOW_LIMIT = new ErrorCode(500, "error.session_share_limit");
  ErrorCode SEARCH_LIMIT = new ErrorCode(500, "error.search_concurrency_limit");
  ErrorCode UPDATE_VERTOR_ERROR = new ErrorCode(500, "error.vector_state_update_failed");
  ErrorCode GET_ROBOT_INFO = new ErrorCode(500, "error.robot_list_failed");
  ErrorCode DROP_ROBOT_INFO = new ErrorCode(500, "error.robot_drop_failed");
  ErrorCode HISTORY_ROBOT_INFO = new ErrorCode(500, "error.robot_history_retrieve_failed");
  ErrorCode DROP_HISTORY_ROBOT_INFO = new ErrorCode(500, "error.robot_history_drop_failed");
  ErrorCode REQUEST_ROBOT_INFO = new ErrorCode(500, "error.robot_request_failed");
  ErrorCode REQUEST_ROBOT_INFO_ERROR = new ErrorCode(500, "error.robot_key_invalid");
  ErrorCode RELEASE_ROBOT_INFO = new ErrorCode(500, "error.robot_release_failed");
  ErrorCode SESSION_EXITS_QUESTION = new ErrorCode(500, "error.session_expired");
  ErrorCode REQUEST_ROBOT_SAVE_HISTORY = new ErrorCode(500, "error.robot_save_history_failed");
  ErrorCode REQUEST_ROBOT_SAVE_HISTORY_ERROR = new ErrorCode(500,
      "error.robot_save_history_disconnect");
  ErrorCode RESPONSE_ROBOT_INFO = new ErrorCode(500, "error.robot_response_failed");

  //语义搜索

  ErrorCode SEMANTIC_SEARCH_ERROR = new ErrorCode(500, "error.semantic_search_failed");
  ErrorCode SEMANTIC_SEARCH_ERROR_HISTORY = new ErrorCode(500, "error.semantic_history_failed");
  ErrorCode SEMANTIC_SEARCH_PROCESS_ERROR = new ErrorCode(500, "error.semantic_process_failed");
  ErrorCode SEMANTIC_SEARCH_PROCESS_DOCUMEN_ERROR = new ErrorCode(500,
      "error.semantic_document_failed");
  ErrorCode NO_GRAPH_SPACE = new ErrorCode(500, "error.graph_space_not_found");
  ErrorCode QUESTION_NONE = new ErrorCode(500, "error.question_none");
  ErrorCode NO_GRAPH_SITE = new ErrorCode(3001, "error.site_not_found");

  // 登录模块

  ErrorCode VERIFICATION_CODE_NULL = new ErrorCode(500, "error.verification_code_null");
  ErrorCode ROBOT_ERROR = new ErrorCode(3002, "error.robot_not_found");
  ErrorCode INVALID_TIME_ERROR = new ErrorCode(3002, "error.invalid_time_error");
  ErrorCode START_END_TIME_ERROR = new ErrorCode(3002, "error.start_end_time_error");


  // 用户模块
  ErrorCode ADD_USER = new ErrorCode(500, "error.user_add_failed");
  ErrorCode UPDATE_USER = new ErrorCode(500, "error.user_update_failed");
  ErrorCode UPDATE_NAME_USER = new ErrorCode(500, "error.user_update_name_failed");
  ErrorCode USER_INFO_LIST = new ErrorCode(500, "error.user_list_failed");
  ErrorCode UPDATE_PHONE_USER = new ErrorCode(500, "error.user_update_phone_failed=");
  ErrorCode UPDATE_PASSWORD_USER = new ErrorCode(500, "error.user_update_password_failed");
  ErrorCode UPDATE_PASSWORD = new ErrorCode(500, "error.user_password_invalid");
  ErrorCode USER_DETAIL_INFO = new ErrorCode(500, "error.user_detail_failed");
  ErrorCode SYSTEM_INFO = new ErrorCode(500, "error.system_info_failed");
  ErrorCode DELETE_USER = new ErrorCode(500, "error.user_delete_failed");
  ErrorCode DISABLE_USER = new ErrorCode(500, "error.user_disable_failed");
  ErrorCode ENABLE_USER = new ErrorCode(500, "error.user_enable_failed");
  ErrorCode UNLOCK_USER = new ErrorCode(500, "error.user_unlock_failed");
  ErrorCode LOCK_USER = new ErrorCode(500, "error.user_lock_failed");
  ErrorCode RESET_PASSWORD_USER = new ErrorCode(500, "error.user_reset_password_failed");
  ErrorCode SHARE_ERROR = new ErrorCode(500, "error.share_link_failed");
  ErrorCode DELETE_USER_INFO = new ErrorCode(500, "error.user_info_delete_failed");


  // 角色模块
  ErrorCode ADD_ROLE_NAME = new ErrorCode(500, "error.role_add_failed");
  ErrorCode UPDATE_ROLE_NAME = new ErrorCode(500, "error.role_update_failed");
  ErrorCode DELETE_ROLE_NAME = new ErrorCode(500, "error.role_delete_failed");
  ErrorCode DETAIL_ROLE_NAME = new ErrorCode(500, "error.role_detail_failed");
  ErrorCode LIST_ROLE_NAME = new ErrorCode(500, "error.role_list_failed");
  ErrorCode LIST_ROLE_DROP = new ErrorCode(500, "error.role_dropdown_list_failed");


  // 部门模块
  ErrorCode ADD_DEPARTMENT_INFO = new ErrorCode(500, "error.department_add_failed");
  ErrorCode UPDATE_DEPARTMENT_INFO = new ErrorCode(500, "error.department_update_failed");
  ErrorCode DELETE_DEPARTMENT_INFO = new ErrorCode(500, "error.department_delete_failed");
  ErrorCode MENU_DEPARTMENT_INFO = new ErrorCode(500, "error.department_menu_failed");
  ErrorCode DEPARTMENT_TREE_INFO = new ErrorCode(500, "error.department_tree_failed");
  ErrorCode DEPARTMENT_DETAIL_INFO = new ErrorCode(500, "error.department_detail_failed");


  // 认证模块
  ErrorCode AUTH_ERROR = new ErrorCode(500, "error.auth_exception");
  ErrorCode LOGOUT_ERROR = new ErrorCode(500, "error.logout_exception");
  ErrorCode USERINOF_ERROR = new ErrorCode(500, "error.user_info_failed");


  // 资源管理
  ErrorCode RESOURCE_LIST_ERROR = new ErrorCode(500, "error.resource_list_failed");
  ErrorCode RESOURCE_INFO_ERROR = new ErrorCode(500, "error.resource_info_failed");
  ErrorCode RESOURCE_UPDATE_ERROR = new ErrorCode(500, "error.resource_update_failed");
  ErrorCode RESOURCE_COMPANY_LIST_ERROR = new ErrorCode(500, "error.resource_company_list_failed");
  ErrorCode RESOURCE_LIST_INFO_ERROR = new ErrorCode(500, "error.resource_list_info_failed");
  ErrorCode RESOURCE_SPACE_LIST_ERROR = new ErrorCode(500, "error.resource_space_detail_failed");
  ErrorCode RESOURCE_QUESTION_ERROR = new ErrorCode(500,
      "error.resource_question_concurrency_failed");
  ErrorCode RESOURCE_SEARCH_ERROR = new ErrorCode(500, "error.resource_search_concurrency_failed");
  ErrorCode RESOURCE_UPDATE_CONCURRENCY_ERROR = new ErrorCode(500,
      "error.resource_concurrency_update_failed");
  ErrorCode RESOURCE_UPDATE_CONCURRENCY = new ErrorCode(500,
      "error.resource_concurrency_insufficient");
  ErrorCode SEARCH_UPDATE_CONCURRENCY_ERROR = new ErrorCode(500,
      "error.search_concurrency_update_failed");
  ErrorCode RESOURCE_SITE_CREATE = new ErrorCode(500, "error.resource_site_create_failed");
  ErrorCode RESOURCE_SITE_DELETE = new ErrorCode(500, "error.resource_site_delete_failed");
  ErrorCode RESOURCE_SITE_INFO_ERROR = new ErrorCode(500, "error.resource_site_info_failed");
  ErrorCode RESOURCE_SITE_LIST_ERROR = new ErrorCode(500, "error.resource_site_list_failed");
  ErrorCode RESOURCE_DATA_LIMIT_ERROR = new ErrorCode(500, "error.resource_data_limit");
  ErrorCode RESOURCE_DATA_ERROR = new ErrorCode(500, "error.resource_data_exceed");
  ErrorCode RESOURCE_DATA_IMPORT_ERROR = new ErrorCode(500, "error.resource_data_import_failed");
  ErrorCode SPACE_LIMIT_ERROR = new ErrorCode(500, "error.space_quota_exceeded");
  ErrorCode SPACE_DATA_ERROR = new ErrorCode(500, "error.space_data_full");
  ErrorCode SPACE_QUESTION_ERROR = new ErrorCode(500, "error.space_question_limit");
  ErrorCode SPACE_SEARCH_ERROR = new ErrorCode(500, "error.space_search_limit");
  ErrorCode SITE_ERROR = new ErrorCode(500, "error.site_search_limit");


  // 消息中心
  ErrorCode MESSAGE_LIST_ERROR = new ErrorCode(500, "error.message_list_failed");
  ErrorCode UPLOAD_BATCH_FILE = new ErrorCode(500, "批量上传文件失败！");
  ErrorCode MESSAGE_DELETE_ERROR = new ErrorCode(500, "error.message_delete_failed");
  ErrorCode MESSAGE_READ_ERROR = new ErrorCode(500, "error.message_read_failed");


  // 导出图空间
  ErrorCode EXPORT_SPACE_DATA = new ErrorCode(500, "error.export_space_data_failed");
  ErrorCode EXPORT_ZIP_DATA = new ErrorCode(500, "error.export_zip_data_failed");
  ErrorCode EXPORT_MYSQL_DATA = new ErrorCode(500, "error.export_mysql_data_failed");
  ErrorCode EXPORT_NO_MODEL_DATA = new ErrorCode(500, "error.export_no_model_data");
  ErrorCode EXPORT_SPACE_INFO_DATA = new ErrorCode(500, "error.export_space_info_failed");
  ErrorCode EXPORT_IMPORT_DATA = new ErrorCode(500, "error.export_import_data_failed");
  ErrorCode EXPORT_SEARCH_SETTINGS = new ErrorCode(500, "error.export_search_settings");
  ErrorCode EXPORT_RERANK_ERROR = new ErrorCode(500, "error.export_rerank_error");
  ErrorCode EXPORT_ZIP_ERROR_DATA = new ErrorCode(500, "error.export_zip_error.data");
  ErrorCode EXPORT_FILE_DATA = new ErrorCode(500, "error.export_file_data=");
  ErrorCode EXPORT_ERROR_FIEL_DATA = new ErrorCode(500, "error.export_invalid_file");
  ErrorCode RECALL_ERROR_DATA = new ErrorCode(500, "error.export_invalid_file");
  ErrorCode EXPORT_DECOMPRESSION_DATA = new ErrorCode(500, "error.export_decompression_failed");
  ErrorCode EXPORT_FILE_NUMBER_DATA = new ErrorCode(500, "error.export_file_count_failed");

  // 同义词

  ErrorCode SYNONYMS_DELETE_ERROR = new ErrorCode(500, "error.synonyms_delete_failed=");
  ErrorCode SYNONYMS_LIST_ERROR = new ErrorCode(500, "error.synonyms_list_failed");
  ErrorCode SYNONYMS_EXISTS_ERROR = new ErrorCode(500, "error.synonyms_exists");
  ErrorCode SYNONYMS_EXISTS_WORD_ERROR = new ErrorCode(500, "error.synonyms_exists_word");
  ErrorCode SYNONYMS_CHECK_ERROR = new ErrorCode(500, "error.synonyms_check_failed");
  ErrorCode SYNONYMS_ADD_ERROR = new ErrorCode(500, "error.synonyms_add_failed");
  ErrorCode SYNONYMS_WOLD_CHECK_ERROR = new ErrorCode(500, "error.synonyms_word_check_failed");
  ErrorCode CREATE_DIR_ERROR = new ErrorCode(500, "error.create_dir.failed");


  // 发版检测
  ErrorCode SERVICES_STATUS = new ErrorCode(500, "error.service.status");
  ErrorCode SERVICES_LIST_ERROR = new ErrorCode(500, "error.release.list");
  ErrorCode SERVICES_NUMBER_ERROR = new ErrorCode(500, "error.release.number");
  ErrorCode SERVICES_NOW_ERROR = new ErrorCode(500, "error.now.number");
  ErrorCode SERVICES_ADD_ERROR = new ErrorCode(500, "error.release.add");
  ErrorCode SERVICES_DELETE_ERROR = new ErrorCode(500, "error.release.delete");
  ErrorCode SERVICES_RELEASE_ERROR = new ErrorCode(500, "error.release.update");
  ErrorCode SERVICES_REPETITION_ERROR = new ErrorCode(500, "error.release.repetition");
}
