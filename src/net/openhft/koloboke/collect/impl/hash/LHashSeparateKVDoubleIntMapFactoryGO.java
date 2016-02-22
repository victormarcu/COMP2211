/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.koloboke.collect.impl.hash;

import net.openhft.koloboke.collect.*;
import net.openhft.koloboke.collect.hash.*;
import net.openhft.koloboke.collect.impl.*;
import net.openhft.koloboke.collect.map.hash.HashDoubleIntMapFactory;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.openhft.koloboke.collect.map.hash.HashDoubleIntMap;

import javax.annotation.Nonnull;
import java.util.*;

import static net.openhft.koloboke.collect.impl.Containers.sizeAsInt;
import static net.openhft.koloboke.collect.impl.hash.LHashCapacities.configIsSuitableForMutableLHash;


public abstract class LHashSeparateKVDoubleIntMapFactoryGO
        extends LHashSeparateKVDoubleIntMapFactorySO {

    LHashSeparateKVDoubleIntMapFactoryGO(HashConfig hashConf, int defaultExpectedSize
            ) {
        super(hashConf, defaultExpectedSize);
    }

    

    abstract HashDoubleIntMapFactory thisWith(HashConfig hashConf, int defaultExpectedSize);

    abstract HashDoubleIntMapFactory lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize);

    abstract HashDoubleIntMapFactory qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize);

    @Override
    public final HashDoubleIntMapFactory withHashConfig(@Nonnull HashConfig hashConf) {
        if (configIsSuitableForMutableLHash(hashConf))
            return lHashLikeThisWith(hashConf, getDefaultExpectedSize()
            );
        return qHashLikeThisWith(hashConf, getDefaultExpectedSize()
            );
    }

    @Override
    public final HashDoubleIntMapFactory withDefaultExpectedSize(int defaultExpectedSize) {
        if (defaultExpectedSize == getDefaultExpectedSize())
            return this;
        return thisWith(getHashConfig(), defaultExpectedSize
                );
    }


    @Override
    public String toString() {
        return "HashDoubleIntMapFactory[" + commonString() + keySpecialString() +
                ",defaultValue=" + getDefaultValue() +
        "]";
    }

    @Override
    public int hashCode() {
        int hashCode = keySpecialHashCode(commonHashCode());
        hashCode = hashCode * 31 + Primitives.hashCode(getDefaultValue());
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof HashDoubleIntMapFactory) {
            HashDoubleIntMapFactory factory = (HashDoubleIntMapFactory) obj;
            return commonEquals(factory) && keySpecialEquals(factory) &&
                    // boxing to treat NaNs correctly
                   ((Integer) getDefaultValue()).equals(factory.getDefaultValue())
                    ;
        } else {
            return false;
        }
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

    

    

    

    

    

    

    
    

    
    

    private  UpdatableLHashSeparateKVDoubleIntMapGO shrunk(
            UpdatableLHashSeparateKVDoubleIntMapGO map) {
        Predicate<HashContainer> shrinkCondition;
        if ((shrinkCondition = hashConf.getShrinkCondition()) != null) {
            if (shrinkCondition.test(map))
                map.shrink();
        }
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap() {
        return newUpdatableMap(getDefaultExpectedSize());
    }
    @Override
    @Nonnull
    public  MutableLHashSeparateKVDoubleIntMapGO newMutableMap() {
        return newMutableMap(getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Map<Double, Integer> map) {
        return shrunk(super.newUpdatableMap(map));
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Map<Double, Integer> map1, Map<Double, Integer> map2) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        return newUpdatableMap(map1, map2, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Map<Double, Integer> map1, Map<Double, Integer> map2,
            Map<Double, Integer> map3) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        return newUpdatableMap(map1, map2, map3, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Map<Double, Integer> map1, Map<Double, Integer> map2,
            Map<Double, Integer> map3, Map<Double, Integer> map4) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        return newUpdatableMap(map1, map2, map3, map4, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Map<Double, Integer> map1, Map<Double, Integer> map2,
            Map<Double, Integer> map3, Map<Double, Integer> map4,
            Map<Double, Integer> map5) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        expectedSize += (long) map5.size();
        return newUpdatableMap(map1, map2, map3, map4, map5, sizeAsInt(expectedSize));
    }


    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Map<Double, Integer> map1, Map<Double, Integer> map2,
            int expectedSize) {
        UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Map<Double, Integer> map1, Map<Double, Integer> map2,
            Map<Double, Integer> map3, int expectedSize) {
        UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Map<Double, Integer> map1, Map<Double, Integer> map2,
            Map<Double, Integer> map3, Map<Double, Integer> map4,
            int expectedSize) {
        UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Map<Double, Integer> map1, Map<Double, Integer> map2,
            Map<Double, Integer> map3, Map<Double, Integer> map4,
            Map<Double, Integer> map5, int expectedSize) {
        UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        map.putAll(map5);
        return shrunk(map);
    }


    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Consumer<net.openhft.koloboke.function.DoubleIntConsumer> entriesSupplier) {
        return newUpdatableMap(entriesSupplier, getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Consumer<net.openhft.koloboke.function.DoubleIntConsumer> entriesSupplier,
            int expectedSize) {
        final UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(expectedSize);
        entriesSupplier.accept(new net.openhft.koloboke.function.DoubleIntConsumer() {
             @Override
             public void accept(double k, int v) {
                 map.put(k, v);
             }
         });
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            double[] keys, int[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            double[] keys, int[] values, int expectedSize) {
        UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(expectedSize);
        int keysLen = keys.length;
        if (keysLen != values.length)
            throw new IllegalArgumentException("keys and values arrays must have the same size");
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Double[] keys, Integer[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Double[] keys, Integer[] values, int expectedSize) {
        UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(expectedSize);
        int keysLen = keys.length;
        if (keysLen != values.length)
            throw new IllegalArgumentException("keys and values arrays must have the same size");
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Iterable<Double> keys, Iterable<Integer> values) {
        int expectedSize = keys instanceof Collection ? ((Collection) keys).size() :
                getDefaultExpectedSize();
        return newUpdatableMap(keys, values, expectedSize);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMap(
            Iterable<Double> keys, Iterable<Integer> values, int expectedSize) {
        UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(expectedSize);
        Iterator<Double> keysIt = keys.iterator();
        Iterator<Integer> valuesIt = values.iterator();
        try {
            while (keysIt.hasNext()) {
                map.put(keysIt.next(), valuesIt.next());
            }
            return shrunk(map);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException(
                    "keys and values iterables must have the same size", e);
        }
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMapOf(
            double k1, int v1) {
        UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(1);
        map.put(k1, v1);
        return map;
    }

    @Override
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMapOf(
            double k1, int v1, double k2, int v2) {
        UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(2);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMapOf(
            double k1, int v1, double k2, int v2,
            double k3, int v3) {
        UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(3);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMapOf(
            double k1, int v1, double k2, int v2,
            double k3, int v3, double k4, int v4) {
        UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(4);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVDoubleIntMapGO newUpdatableMapOf(
            double k1, int v1, double k2, int v2,
            double k3, int v3, double k4, int v4,
            double k5, int v5) {
        UpdatableLHashSeparateKVDoubleIntMapGO map = newUpdatableMap(5);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    
    


    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, int expectedSize) {
        MutableLHashSeparateKVDoubleIntMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, Map<Double, Integer> map3, int expectedSize) {
        MutableLHashSeparateKVDoubleIntMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, Map<Double, Integer> map3,
            Map<Double, Integer> map4, int expectedSize) {
        MutableLHashSeparateKVDoubleIntMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, Map<Double, Integer> map3,
            Map<Double, Integer> map4, Map<Double, Integer> map5, int expectedSize) {
        MutableLHashSeparateKVDoubleIntMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5, expectedSize));
        return res;
    }



    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(
            Consumer<net.openhft.koloboke.function.DoubleIntConsumer> entriesSupplier
            , int expectedSize) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(double[] keys,
            int[] values, int expectedSize) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(
            Double[] keys, Integer[] values, int expectedSize) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(Iterable<Double> keys,
            Iterable<Integer> values, int expectedSize) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    
    

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(
            Map<Double, Integer> map) {
        MutableLHashSeparateKVDoubleIntMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2) {
        MutableLHashSeparateKVDoubleIntMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, Map<Double, Integer> map3) {
        MutableLHashSeparateKVDoubleIntMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, Map<Double, Integer> map3,
            Map<Double, Integer> map4) {
        MutableLHashSeparateKVDoubleIntMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, Map<Double, Integer> map3,
            Map<Double, Integer> map4, Map<Double, Integer> map5) {
        MutableLHashSeparateKVDoubleIntMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5));
        return res;
    }



    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(
            Consumer<net.openhft.koloboke.function.DoubleIntConsumer> entriesSupplier
            ) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(double[] keys,
            int[] values) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(
            Double[] keys, Integer[] values) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMap(Iterable<Double> keys,
            Iterable<Integer> values) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }


    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMapOf(double k1, int v1) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMapOf(double k1, int v1,
             double k2, int v2) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMapOf(double k1, int v1,
             double k2, int v2, double k3, int v3) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMapOf(double k1, int v1,
             double k2, int v2, double k3, int v3,
             double k4, int v4) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newMutableMapOf(double k1, int v1,
             double k2, int v2, double k3, int v3,
             double k4, int v4, double k5, int v5) {
        MutableLHashSeparateKVDoubleIntMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
    
    


    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, int expectedSize) {
        ImmutableLHashSeparateKVDoubleIntMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, Map<Double, Integer> map3, int expectedSize) {
        ImmutableLHashSeparateKVDoubleIntMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, Map<Double, Integer> map3,
            Map<Double, Integer> map4, int expectedSize) {
        ImmutableLHashSeparateKVDoubleIntMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, Map<Double, Integer> map3,
            Map<Double, Integer> map4, Map<Double, Integer> map5, int expectedSize) {
        ImmutableLHashSeparateKVDoubleIntMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5, expectedSize));
        return res;
    }



    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(
            Consumer<net.openhft.koloboke.function.DoubleIntConsumer> entriesSupplier
            , int expectedSize) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(double[] keys,
            int[] values, int expectedSize) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(
            Double[] keys, Integer[] values, int expectedSize) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(Iterable<Double> keys,
            Iterable<Integer> values, int expectedSize) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    
    

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(
            Map<Double, Integer> map) {
        ImmutableLHashSeparateKVDoubleIntMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2) {
        ImmutableLHashSeparateKVDoubleIntMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, Map<Double, Integer> map3) {
        ImmutableLHashSeparateKVDoubleIntMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, Map<Double, Integer> map3,
            Map<Double, Integer> map4) {
        ImmutableLHashSeparateKVDoubleIntMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4));
        return res;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(Map<Double, Integer> map1,
            Map<Double, Integer> map2, Map<Double, Integer> map3,
            Map<Double, Integer> map4, Map<Double, Integer> map5) {
        ImmutableLHashSeparateKVDoubleIntMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5));
        return res;
    }



    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(
            Consumer<net.openhft.koloboke.function.DoubleIntConsumer> entriesSupplier
            ) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(double[] keys,
            int[] values) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(
            Double[] keys, Integer[] values) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMap(Iterable<Double> keys,
            Iterable<Integer> values) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }


    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMapOf(double k1, int v1) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMapOf(double k1, int v1,
             double k2, int v2) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMapOf(double k1, int v1,
             double k2, int v2, double k3, int v3) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMapOf(double k1, int v1,
             double k2, int v2, double k3, int v3,
             double k4, int v4) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    @Nonnull
    public  HashDoubleIntMap newImmutableMapOf(double k1, int v1,
             double k2, int v2, double k3, int v3,
             double k4, int v4, double k5, int v5) {
        ImmutableLHashSeparateKVDoubleIntMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
}

