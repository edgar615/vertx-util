/*
 * Copyright (c) 2011-2016 The original author or authors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *      The Eclipse Public License is available at
 *      http://www.eclipse.org/legal/epl-v10.html
 *
 *      The Apache License v2.0 is available at
 *      http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package com.edgar.util.vertx.sharedata;

import io.vertx.core.shareddata.LocalMap;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Wraps a map on top of a local map.
 * <p>
 * 这段代码是从Vert.x的Service Discovery组件中复制，因为有些地方也需要类似的功能，但是又不想引入过多的包
 */
public class LocalMapWrapper<K, V> implements Map<K, V> {

  private final LocalMap<K, V> local;

  public LocalMapWrapper(LocalMap<K, V> local) {
    this.local = local;
  }


  public boolean replaceIfPresent(K key, V oldValue, V newValue) {
    return local.replaceIfPresent(key, oldValue, newValue);
  }

  public void close() {
    local.close();
  }

  public boolean removeIfPresent(K key, V value) {
    return local.removeIfPresent(key, value);
  }

  @Override
  public Collection<V> values() {
    return local.values();
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    Set<Entry<K, V>> entries = new LinkedHashSet<>();
    for (K key : local.keySet()) {
      entries.add(new Entry<K, V>() {
        @Override
        public K getKey() {
          return key;
        }

        @Override
        public V getValue() {
          return local.get(key);
        }

        @Override
        public V setValue(V value) {
          return local.put(key, value);
        }
      });
    }
    return entries;
  }

  @Override
  public int size() {
    return local.size();
  }

  @Override
  public V replace(K key, V value) {
    return local.replace(key, value);
  }

  @Override
  public V putIfAbsent(K key, V value) {
    return local.putIfAbsent(key, value);
  }

  @Override
  public V put(K key, V value) {
    return local.put(key, value);
  }

  @Override
  public V remove(Object key) {
    return local.remove((K) key);
  }


  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
      local.put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public Set<K> keySet() {
    return local.keySet();
  }

  @Override
  public boolean isEmpty() {
    return local.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return local.keySet().contains(key);
  }


  @Override
  public boolean containsValue(Object value) {
    return local.values().contains(value);
  }

  @Override
  public V get(Object key) {
    return local.get((K) key);
  }

  /**
   * Clear all entries in the map
   */
  @Override
  public void clear() {
    local.clear();
  }
}