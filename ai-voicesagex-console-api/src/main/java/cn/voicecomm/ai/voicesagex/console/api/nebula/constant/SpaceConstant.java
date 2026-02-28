package cn.voicecomm.ai.voicesagex.console.api.nebula.constant;

/**
 * 图数据库常量
 *
 * @author ryc
 * @date 2025/8/21
 */
public class SpaceConstant {

  public static final Integer PARTITION_NUM = 10;
  public static final Integer LIMIT_NUM = 20;
  public static final Integer SPACE_NUM = 5;
  public static final Integer REPLICA_FACTOR = 1;
  public static final Integer UP = 10000;
  public static final Integer EXCEL_NUMBER = 65535;
  public static final Integer ERROR = -1;
  public static final String ERROR_FILE = "error_file/";
  public static final String FILE = "/file";
  public static final Integer THIRTEEN = 13;
  public static final int INDEX = 0;
  public static final int TWELVE = 12;
  public static final int MAX = 5000;
  public static final int MAX_QUESTION = 2000;
  public static final int THOUSAND = 1000;
  public static final int TWO = 2;
  public static final int THREE = 3;
  public static final int FOUR = 4;
  public static final int FIVE = 5;
  public static final int SECOND = 3600;
  public static final int WAIT = 2000;
  public static final int BATCH_SIZE = 2000;
  public static final int ENGDE = 1;
  public static final String SPACE_NAME = "Name";
  public static final String SHOW_SPACE_QL = "SHOW SPACES";
  public static final String regex = "[。，,\\!?；]";
  public static final String TAG_SPLIT = ",";
  public static final String TAG_SPLIT_CHINESE = "，";
  public static final String TAG_SPLIT_INDEX = "、";
  public static final String TAG_SPACE = " ";
  public static final String COLON = " ";
  public static final String SPACE_FIX_NAME = "SPACE";
  public static final String SPACE_NAME_FIX = "SPACE_";
  public static final String TAG_NOT_NULL = "NOT NULL ";
  public static final String TAG_NULL = "NULL ";
  public static final String TAG_EDGE_DEFAULT = "DEFAULT ";
  public static final String FIX_TAG_NAME = "( NAME STRING";
  public static final String PROPERTY_NAME = "v.";
  public static final String PROPERTY_NAME_EDGE = "properties(e).";
  public static final String FIX_TAG_NAME_SUX = ")";
  public static final String FIX_TAG_NAME_SUX_FUSION = " ) ";
  public static final String FIX_TAG_NAME_SUX_FUSION_CHINESE = "） ";
  public static final String FIX_TAG_NAME_START = "(";
  public static final String FIX_TAG_NAME_START_CHINESE = "（";
  public static final String BRACKET = "()";
  public static final String FIX_TAG_NAME_FUSION = " ( ";
  public static final String FIX_INDEX_NAME = "_INDEX";
  public static final String FIX_STRING = "FIXED_STRING";
  public static final String STRING = "STRING";
  public static final String FLOAT = "FLOAT";
  public static final String DOUBLE = "DOUBLE";
  public static final String DATE = "DATE";
  public static final String TIME = "TIME";
  public static final String DATETIME = "DATETIME";
  public static final String TIMESTAMP = "TIMESTAMP";
  public static final String BOOL = "BOOL";
  public static final String SPILE = "-";
  public static final String DIRECTION = "->";
  public static final String MANUALLY_ADD = "手动添加";
  public static final String SEPARATOR = "/";
  public static final String PATTERN = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]*$";
  public static final String PATTERN_EN = "^[\\u4e00-\\u9fa5a-zA-Z0-9_ ]*$";
  public static final String PATTERN_STRING = "[a-zA-Z0-9_\\u4e00-\\u9fa5\\-()（）“”\"'【】\\[\\]/.、——‘’ ·,]{1,50}";
  public static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{12,18}$";
  public static final String NAME_PATTERN = "^[a-zA-Z0-9\\u4e00-\\u9fa5 ]*$";
  public static final String NAME_PATTERN_EN = "^[a-zA-Z0-9\\u4e00-\\u9fa5 ]*$";
  public static final String OPT = "/opt";
  public static final String PREFIX = "/file";
  public static final String TMP = "/tmp/";
  public static final String TARGET = "/target/";
  public static final String ZIP = "zip/";
  public static final String STR = "-";
  public static final String ENTITY_NAME = "实体名称 (必填)";
  public static final String RELATION_NAME = "主体类型 (必填)";
  public static final String RELATION_VALUE = "主体类型 (必填)";
  public static final String ENTITY_NAME_YES = "实体名称必填";
  public static final String ENTITY_RELATION_YES = "主体值/客体值不能为空";
  public static final String TYPE_RELATION_YES = "主体类型/客体类型不能为空";
  public static final String ENTITY_NAME_CHECK = "实体名填写不符合规范";
  public static final String ENTITY_NAME_CHECK_ERROR = "主/客体属性过期时间冲突，新增失败";
  public static final String ENTITY_SUBJECT_CHECK = "主体值填写不符合规范";
  public static final String ENTITY_OBJECT_CHECK = "客体值填写不符合规范";
  public static final String ENTITY_EXCEL_CHECK = "导入数据属性与本体属性顺序不一致";
  public static final String ENTITY_RELATION_CHECK = "导入数据属性与关系属性顺序不一致";
  public static final String SUBJECT_NAME = "主体类型 (必填)";
  public static final String SUBJECT_VALUE = "主体值 (必填)";
  public static final String OBJECT_NAME = "客体类型 (必填)";
  public static final String OBJECT_VALUE = "客体值 (必填)";
  public static final String YES = "必填";
  public static final String TYPE_ERROR = "类型填写错误";
  public static final String TTL_TYPE_ERROR = "与属性过期时间冲突，新增失败";
  public static final String NAME = "NAME";
  public static final String PRE = "pre";
  public static final String NEXT = "next";
  public static final String E = "e";
  public static final String RANK_NUMBER = "rank";
  public static final String ID = "id";
  public static final String ALL = "全部";
  public static final String VALUE = " = ";
  public static final String CHECK = "必填/";
  public static final String PROPERRY_CHACK = "属性值 ";
  public static final String APP_NAME = "spring.application.name";
  public static final String EXCEL = "excel";
  public static final String EXCEL_ERROR = "错误说明";
  public static final String TYPE = "Type";
  public static final String TAG_NAME = "Name";
  public static final String COUNT = "Count";
  public static final String JOB = "Job Id(TaskId)";
  public static final String STATUS = "Status";
  public static final String STOP_TIME = "Stop Time";
  public static final String VERTICES = "vertices";
  public static final String EDGES = "edges";
  public static final String FINISHED = "FINISHED";
  public static final String NULL = "NULL";
  public static final String SRC = "src";
  public static final String DST = "dst";
  public static final String TAGLIST = "tagList";
  public static final String EDGENAME = "edgeName";
  public static final String ENTITYNAME = "objectName";
  public static final String SUBJECTNAME = "subjectName";
  public static final String IMPORT_ENTITY = "没有查询到对应的本体";
  public static final String RELATION_ENTITY = "没有查询到对应的本体";
  public static final String Tag_ERROR = "主体类型不存在";
  public static final String EDGE_ERROR = "关系类型不存在";
  public static final String EDGE_ERROR_SAME = "主体名称和客体名称不能相同";
  public static final String SAME_RELATION = "重复关系数据，请勿重复导入";
  public static final String SET_RELATION = "-";
  public static final String ALL_RELATION = "\\";
  public static final String AS = " AS ";
  public static final String PRO = "pro";
  public static final String T = "t";
  public static final String T1 = "t1";
  public static final String PERCENT = "%";
  public static final String PERCENT_REPLACE = "\\%";
  public static final String UNDERLINE = "_";
  public static final String UNDERLINE_TWO = "__";
  public static final String SPACE_NAME_DEFAULT = "--";
  public static final String UNDERLINE_LIKE = "\\_";
  public static final String UNDERLINE_LIKE_TWO = "\\__";
  public static final String UNDERLINE_LIKE_QUERY = "\\";
  public static final String EDGENAMEINFO = "edgeName";
  public static final String PROPERTY = "property";
  public static final String NAME_TAG = "\'";
  public static final String QUOTATIONMARK = "`";
  public static final String DOUBLE_QUOTATION_MARKS = "\"";
  public static final String TRUE_STRING = "true";
  public static final String FALSE_STRING = "false";
  public static final String PDF = ".pdf";
  public static final String PDF_INDEX = "pdf";
  public static final String RANGE = "属性值 ";
  public static final String RANGE_INDEX = " 超过规定范围";
  public static final String VERTEX = ".";
  public static final String DIAGONAL = "/";
  public static final String ZHIPI = "zhipu/";
  public static final String TEMPLATE = "template/";
  public static final String DEC = "dec/";
  public static final String CUT_OUT = "^\\d+-";
  public static final String DOCUMENT_METADATA = "documentMetadata";
  public static final String CHUNK_METADATA = "chunkMetadata";
  public static final String TASK_ID = "task_ids";
  public static final String REGEX = "(\\w+)\\((\\w+)\\)";
  public static final String DATA = "data";
  public static final String WUHANG = "开目";
  public static final String DATA1 = "官网";
  public static final String DATA2 = "官网为";
  public static final String DONE = "done";
  public static final String RESPONSE = "response";
  public static final String STATE = "state";
  public static final String DOCUMENT_STATE = "document";
  public static final String TRIPLES = "triples";
  public static final String COLLECTION = "collection";
  public static final String VECTOR = "Vector";
  public static final String VECTOR_TAG = "tag";
  public static final String VECTOR_RELATION = "edge";
  public static final String EQUAL_SIGN = "=";
  public static final String HTTP = "https://";
  public static final String TEXT = "text";
  public static final String IDS = "ids";
  public static final String RANGEVERTOR = "(?<=\\),)(?=\"[^\"]+\":\\()";
  public static final String EDGE = "edge";
  public static final String DOUBLE_QUOTATION_MARKS_INDEX = "";
  public static final String EXTRACTION = "extraction/";
  public static final String TAGEDGE = "tagEdge/";
  public static final String UPLOAD = "tmpFile/";
  public static final String TAGCACHE = "tags_";
  public static final String EDGECACHE = "edges_";
  public static final String SEMANTIC_ENTITY = "entity";
  public static final String SEMANTIC_RELATION = "relation";
  public static final String SEMANTIC_CHUNK = "chunk";
  public static final String DATA_DUPLICATION = "模版重复数据";
  public static final String SAME = "已经存在相同关系,不允许新增！";
  public static final String EXPLAIN = "说明";
  public static final String COLON_CHINESE = "：";
  public static final String THINK = "<think>";
  public static final String THINK_END = "</think>";
  public static final String ENTITY_PROMPT =
      "你是专门进行实体抽取的专家。请从input中抽取出符合schema定义的实体，不存在的实体类型返回空列表。请按照JSON字符串的格式回答。"
          + "{{\"schema\": {ner_schema}, \"input\": \"{input}\"}}";

  public static final String RELATION_PROMPT =
      "你是专门进行关系抽取的专家。请从input中抽取出符合schema定义的关系三元组，不存在的关系返回空列表。请按照JSON字符串的格式回答。"
          + "{{\"schema\": {re_schema}, \"input\": \"{input}\"}}";


  public static final String SINGLE_QUOTES = "'";
  public static final String SINGLE_QUOTES_CHANGE = "\\'";
  public static final String ENTITY_EXPLAIN_ONE = "1.实体名称：必填，30个字符以内。仅支持输入包括英文字母（区分大小写）、数字、中文等，不能包含除下划线（_）、中英文的括号、中英文的双引号、中英文的横线以外的特殊字符；";
  public static final String ENTITY_EXPLAIN_TWO = "2.属性根据类型填写相应的值；";
  public static final String ENTITY_EXPLAIN_THREE = "3.表头不可变动；";


  public static final String RELATION_EXPLAIN_ONE = "1.主体类型/客体类型：根据下拉选择，必填项；";
  public static final String RELATION_EXPLAIN_TWO = "2.实体名称：必填，30个字符以内，仅支持输入包括英文字母（区分大小写）、数字、中文等，不能包含除下划线（_）、中英文的括号、中英文的双引号、中英文的横线以外的特殊字符；";
  public static final String RELATION_EXPLAIN_THREE = "3.客体名称：必填，30个字符以内，仅支持输入包括英文字母（区分大小写）、数字、中文等，不能包含除下划线（_）、中英文的括号、中英文的双引号、中英文的横线以外的特殊字符；";
  public static final String RELATION_EXPLAIN_FORE = "4.属性根据类型填写相应的值；";
  public static final String RELATION_EXPLAIN_FIVE = "5.表头不可变动；";


  // Mysql
  public static final String USER_NAME = "admin";
  public static final int USER_ID = 10001;


  // 文档图谱
  public static final String DOCUMENT = "文档";
  public static final String FRAGMENT = "片段";
  public static final String OWNING_DOCUMENT = "所属文档";
  public static final String FIRST_STAGE = "第一段";
  public static final String NEXT_PARAGRAPH = "下一段";
  public static final String PAGE_CONTENT = "页面内容";
  public static final String PAGE_NUMBER = "页码";
  public static final String SEGMENT_NUMBERING = "片段编号";
  public static final String TOTAL_PAGE = "总页码";
  public static final String FILE_PATH = "文件路径";
  public static final String FORMAT = "格式";
  public static final String NUMBER_OF_FRAGMENTS = "片段数量";
  public static final String TXT = "文本";
  public static final String OTHER_FILE = "otherFile";
  public static final String MESSAGE = "声通智能小助手正在思考中...";
  public static final String MESSAGE_ENGLISH = "ShengTong smart assistant is thinking...";
  public static final String MESSAGE_AR = "المساعد الذكي الآلي يفكر";


  public static final String DOCUMENT_EN = "document";
  public static final String FRAGMENT_EN = "fragment";
  public static final String OWNING_DOCUMENT_EN = "owning document";
  public static final String FIRST_STAGE_EN = "first stage";
  public static final String NEXT_PARAGRAPH_EN = "next stage";
  public static final String PAGE_CONTENT_EN = "page content";
  public static final String PAGE_NUMBER_EN = "page number";
  public static final String SEGMENT_NUMBERING_EN = "segment number";
  public static final String TOTAL_PAGE_EN = "total page number";
  public static final String FILE_PATH_EN = "file path";
  public static final String FORMAT_EN = "format";
  public static final String NUMBER_OF_FRAGMENTS_EN = "number of fragments";
  public static final String TXT_EN = "text";


  public static final String RANK = "@";
  public static final String RANK_NAME = "rank";
}
