package com.github.edgar615.util.vertx.cache;
import io.vertx.core.json.JsonObject;

class CacheOptionsConverter {

  static void fromJson(JsonObject json, CacheOptions obj) {
    if (json.getValue("expireAfterWrite") instanceof Number) {
      obj.setExpireAfterWrite(((Number) json.getValue("expireAfterWrite")).longValue());
    }
    if (json.getValue("expireAfterAccess") instanceof Number) {
      obj.setExpireAfterAccess(((Number) json.getValue("expireAfterAccess")).longValue());
    }
    if (json.getValue("maximumSize") instanceof Number) {
      obj.setMaximumSize(((Number) json.getValue("maximumSize")).longValue());
    }
  }

  static void toJson(CacheOptions obj, JsonObject json) {
    if (obj.getExpireAfterWrite() != null) {
      json.put("expireAfterWrite", obj.getExpireAfterWrite());
    }
    if (obj.getExpireAfterAccess() != null) {
      json.put("expireAfterAccess", obj.getExpireAfterAccess());
    }
    if (obj.getMaximumSize() != null) {
      json.put("maximumSize", obj.getMaximumSize());
    }
  }
}