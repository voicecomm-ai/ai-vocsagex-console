package cn.voicecomm.ai.voicesagex.console.util.constant;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

/**
 * @author jiwh
 * @date 2023/4/19 9:38
 */
@NoArgsConstructor(access = PRIVATE)
public final class MessageTopicConstant {

  public static final String ANDROID_MESSAGE_TOPIC = "android-message-topic-";

  public static final String FLUTTER_MESSAGE_TOPIC = "flutter-message-topic-";

  public static final String ANDROID_USER_MESSAGE_TOPIC = "android-user-message-topic-";

  public static final String FLUTTER_USER_MESSAGE_TOPIC = "flutter/user/message/topic/";

  public static final String MESSAGE_CENTER_TOPIC = "backend-voicesagex-console-message-center-topic";
}
