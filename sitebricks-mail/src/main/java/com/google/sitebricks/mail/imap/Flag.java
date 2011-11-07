package com.google.sitebricks.mail.imap;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
public enum Flag {
  SEEN,
  RECENT,
  DELETED,
  DRAFT,
  FLAGGED,
  ANSWERED,
  FORWARDED,
  ;

  private static BiMap<String, Flag> flagMap = HashBiMap.create(10);
  static {
    flagMap.put("\\seen", SEEN);
    flagMap.put("\\recent", RECENT);
    flagMap.put("\\deleted", DELETED);
    flagMap.put("\\draft", DRAFT);
    flagMap.put("\\flagged", FLAGGED);
    flagMap.put("\\answered", ANSWERED);
    flagMap.put("$forwarded", FORWARDED);
  }

  public static Flag parse(String flag) {
    return flagMap.get(flag.toLowerCase());
  }

  public static String toImap(Flag f) {
    return flagMap.inverse().get(f);
  }
}
