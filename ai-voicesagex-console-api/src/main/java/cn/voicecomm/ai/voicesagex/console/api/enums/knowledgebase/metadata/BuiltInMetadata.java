package cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BuiltInMetadata {
  document_name(MetadataType.String),
  uploader(MetadataType.String),
  upload_date(MetadataType.Time),
  last_update_date(MetadataType.Time),
  source(MetadataType.String);

  private final MetadataType type;
}
