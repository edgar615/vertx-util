package com.edgar.util.vertx;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Edgar on 2016/9/13.
 *
 * @author Edgar  Date 2016/9/13
 */
public class JsonUtils {

  public static JsonObject getJsonFromFile(String jsonFile) {
    JsonObject conf;
    if (jsonFile != null) {
      try (Scanner scanner = new Scanner(new File(jsonFile)).useDelimiter("\\A")) {
        String sconf = scanner.next();
        try {
          conf = new JsonObject(sconf);
        } catch (DecodeException e) {
//                    log.error("Configuration file " + sconf + " does not contain a valid JSON
// object");
          return null;
        }
      } catch (FileNotFoundException e) {
        try {
          conf = new JsonObject(jsonFile);
        } catch (DecodeException e2) {
          // The configuration is not printed for security purpose, it can contain sensitive data.
//                    log.error("The -conf option does not point to an existing file or is not a
// valid JSON object");
          e2.printStackTrace();
          return null;
        }
      }
    } else {
      conf = null;
    }
    return conf;
  }

  /**
   * 从一个JSON对象中获取int
   *
   * @param jsonObject JSON对象
   * @param key        键值
   * @return int类型的值，如果value不能转换为int，抛出异常
   */
  static Integer getInteger(JsonObject jsonObject, String key) {
    Object value = jsonObject.getValue(key);
    if (value == null) {
      return null;
    }
    if (value instanceof Integer) {
      return (Integer) value;
    }
    if (value instanceof String) {
      if (value.toString().equalsIgnoreCase("")) {
        return 0;
      }
      return Integer.parseInt(value.toString());
    }
    throw new ClassCastException(value.getClass() + " cannot be cast to java.lang.Integer");
  }

  /**
   * 从一个JSON对象中获取int
   *
   * @param jsonObject JSON对象
   * @param key        键值
   * @param def        默认值
   * @return int类型的值，如果value不能转换为int，抛出异常，如果没有该值，返回默认值
   */
  static Integer getInteger(JsonObject jsonObject, String key, int def) {
    Integer value = getInteger(jsonObject, key);

    if (value == null) {
      return def;
    }
    return value;
  }

  /**
   * 从一个JSON对象中获取bool
   *
   * @param jsonObject JSON对象
   * @param key        键值
   * @return bool类型的值，如果value不能转换为bool，抛出异常
   */
  static Boolean getBoolean(JsonObject jsonObject, String key) {
    Object value = jsonObject.getValue(key);
    if (value == null) {
      return null;
    }
    if (value instanceof Boolean) {
      return (Boolean) value;
    }
    if (value instanceof String) {
      return Boolean.parseBoolean(value.toString());
    }
    throw new ClassCastException(value.getClass() + " cannot be cast to java.lang.Boolean");
  }

  /**
   * 从一个JSON对象中获取bool
   *
   * @param jsonObject JSON对象
   * @param key        键值
   * @param def        默认值
   * @return bool类型的值，如果value不能转换为bool，抛出异常，如果没有该值，返回默认值
   */
  static Boolean getBoolean(JsonObject jsonObject, String key, Boolean def) {
    Boolean value = getBoolean(jsonObject, key);

    if (value == null) {
      return def;
    }
    return value;
  }

  /**
   * 将一个jsonobject转换为map对象．里面嵌套的JsonObject也会被转换为Map，里面嵌套的JsonArray也会被转换为List
   * @param jsonObject  json对象
   * @return map
   */
  static Map<String, Object> toMap(JsonObject jsonObject) {
    Map<String, Object> map = new HashMap<>();
    jsonObject.getMap().forEach((key, value) -> {
      map.put(key, check(value));
    });
    return map;
  }

  /**
   * 将一个JsonArray转换为map对象．里面嵌套的JsonObject也会被转换为Map，里面嵌套的JsonArray也会被转换为List
   * @param jsonArray json数组
   * @return
   */
  static List<Object> toList(JsonArray jsonArray) {
    List<Object> list = new ArrayList<>();
    jsonArray.getList().forEach(value -> {
      list.add(check(value));
    });
    return list;
  }

  static Object check(Object val) {
    if (val == null) {
      // OK
    } else if (val instanceof Number && !(val instanceof BigDecimal)) {
      // OK
    } else if (val instanceof Boolean) {
      // OK
    } else if (val instanceof String) {
      // OK
    } else if (val instanceof Character) {
      // OK
    } else if (val instanceof CharSequence) {
      val = val.toString();
    } else if (val instanceof Map) {
      val = new HashMap<>((Map) val);
    } else if (val instanceof JsonObject) {
      val = toMap(((JsonObject) val));
    } else if (val instanceof List) {
      val = new ArrayList<>((List) val);
    } else if (val instanceof JsonArray) {
      val = toList((JsonArray) val);
    } else {
      throw new IllegalStateException("Illegal type in Event Content: " + val.getClass());
    }
    return val;
  }
}
