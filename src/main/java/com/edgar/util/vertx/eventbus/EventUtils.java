package com.edgar.util.vertx.eventbus;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Edgar on 2017/6/30.
 *
 * @author Edgar  Date 2017/6/30
 */
public class EventUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventUtils.class);

  private static String toJsonString(JsonObject jsonObject) {
    StringBuilder sb = new StringBuilder();
    for (String field : jsonObject.fieldNames()) {
      sb.append(field)
              .append(":")
              .append(jsonObject.getValue(field))
              .append(";");
    }
    return sb.toString();
  }

  private static void outLog(Logger logger,  Event event, String type) {
    logger.info("======> [{}] [{}] [{}] [{}] [{}]",
                type,
                event.id(),
                type,
                toJsonString(event.header()),
                toJsonString(event.body()));
  }

  private static void inLog(Logger logger, Event event) {
    logger.info("<====== [{}] [{}] [{}]",
                event.id(),
                toJsonString(event.header()),
                toJsonString(event.body()));
  }


}
