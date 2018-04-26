package com.github.edgar615.util.vertx.eventbus;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * 绑定consumer的简易实现.
 * 参考了ServiceBinder的实现.
 *
 * @author Edgar  Date 2018/4/26
 */
public class ConsumerBinder {

  private final Vertx vertx;

  private String address;

  public ConsumerBinder(Vertx vertx) {
    Objects.requireNonNull(vertx);
    this.vertx = vertx;
  }

  private static Class<?> loadClass(String name, Class origin) {
    try {
      return origin.getClassLoader().loadClass(name);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Cannot find handlerClass: " + name, e);
    }
  }

  private static Constructor getConstructor(Class<?> clazz, Class<?>... types) {
    try {
      return clazz.getDeclaredConstructor(types);
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException("Cannot find constructor on: " + clazz.getName(), e);
    }
  }

  private static Object createInstance(Constructor constructor, Object... args) {
    try {
      return constructor.newInstance(args);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to call constructor on", e);
    }
  }

  public ConsumerBinder setAddress(String address) {
    this.address = address;
    return this;
  }

  public <T> MessageConsumer<T> register(String className, JsonObject config) {
    Objects.requireNonNull(address);
    Class<?> handlerClass = loadClass(className, this.getClass());
    return register(handlerClass, config);
  }

  public <T> MessageConsumer<T> register(Class<?> clazz, JsonObject config) {
    Objects.requireNonNull(address);

    Constructor constructor = getConstructor(clazz, Vertx.class, JsonObject.class);
    Object instance = createInstance(constructor, vertx, config);
    Handler<Message<T>> handler = (Handler) instance;
    // register
    return vertx.eventBus().consumer(address, handler);
  }

}
