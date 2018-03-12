/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.github.edgar615.util.vertx.wheel;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link com.github.edgar615.util.vertx.wheel.KeepaliveOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link com.github.edgar615.util.vertx.wheel.KeepaliveOptions} original class using Vert.x codegen.
 */
public class KeepaliveOptionsConverter {

  public static void fromJson(JsonObject json, KeepaliveOptions obj) {
    if (json.getValue("disConnAddress") instanceof String) {
      obj.setDisConnAddress((String)json.getValue("disConnAddress"));
    }
    if (json.getValue("firstConnAddress") instanceof String) {
      obj.setFirstConnAddress((String)json.getValue("firstConnAddress"));
    }
    if (json.getValue("heartbeatAddress") instanceof String) {
      obj.setHeartbeatAddress((String)json.getValue("heartbeatAddress"));
    }
    if (json.getValue("interval") instanceof Number) {
      obj.setInterval(((Number)json.getValue("interval")).intValue());
    }
    if (json.getValue("step") instanceof Number) {
      obj.setStep(((Number)json.getValue("step")).intValue());
    }
  }

  public static void toJson(KeepaliveOptions obj, JsonObject json) {
    if (obj.getDisConnAddress() != null) {
      json.put("disConnAddress", obj.getDisConnAddress());
    }
    if (obj.getFirstConnAddress() != null) {
      json.put("firstConnAddress", obj.getFirstConnAddress());
    }
    if (obj.getHeartbeatAddress() != null) {
      json.put("heartbeatAddress", obj.getHeartbeatAddress());
    }
    json.put("interval", obj.getInterval());
    json.put("step", obj.getStep());
  }
}