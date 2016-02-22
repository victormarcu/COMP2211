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
import net.openhft.koloboke.collect.map.hash.HashShortFloatMapFactory;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.openhft.koloboke.collect.map.hash.HashShortFloatMap;

import javax.annotation.Nonnull;
import java.util.*;

import static net.openhft.koloboke.collect.impl.Containers.sizeAsInt;
import static net.openhft.koloboke.collect.impl.hash.LHashCapacities.configIsSuitableForMutableLHash;


public abstract class QHashSeparateKVShortFloatMapFactoryGO
        extends QHashSeparateKVShortFloatMapFactorySO {

    QHashSeparateKVShortFloatMapFactoryGO(HashConfig hashConf, int defaultExpectedSize
            , short lower, short upper) {
        super(hashConf, defaultExpectedSize, lower, upper);
    }

    

    abstract HashShortFloatMapFactory thisWith(HashConfig hashConf, int defaultExpectedSize, short lower, short upper);

    abstract HashShortFloatMapFactory lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, short lower, short upper);

    abstract HashShortFloatMapFactory qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, short lower, short upper);

    @Override
    public final HashShortFloatMapFactory withHashConfig(@Nonnull HashConfig hashConf) {
        if (configIsSuitableForMutableLHash(hashConf))
            return lHashLikeThisWith(hashConf, getDefaultExpectedSize()
            
                    , getLowerKeyDomainBound(), getUpperKeyDomainBound());
        return qHashLikeThisWith(hashConf, getDefaultExpectedSize()
            
                , getLowerKeyDomainBound(), getUpperKeyDomainBound());
    }

    @Override
    public final HashShortFloatMapFactory withDefaultExpectedSize(int defaultExpectedSize) {
        if (defaultExpectedSize == getDefaultExpectedSize())
            return this;
        return thisWith(getHashConfig(), defaultExpectedSize
                
                , getLowerKeyDomainBound(), getUpperKeyDomainBound());
    }

    final HashShortFloatMapFactory withDomain(short lower, short upper) {
        if (lower == getLowerKeyDomainBound() && upper == getUpperKeyDomainBound())
            return this;
        return thisWith(getHashConfig(), getDefaultExpectedSize(), lower, upper);
    }

    @Override
    public final HashShortFloatMapFactory withKeysDomain(short lower, short upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minPossibleKey shouldn't be greater " +
                    "than maxPossibleKey");
        return withDomain(lower, upper);
    }

    @Override
    public final HashShortFloatMapFactory withKeysDomainComplement(short lower, short upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minImpossibleKey shouldn't be greater " +
                    "than maxImpossibleKey");
        return withDomain((short) (upper + 1), (short) (lower - 1));
    }

    @Override
    public String toString() {
        return "HashShortFloatMapFactory[" + commonString() + keySpecialString() +
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
        if (obj instanceof HashShortFloatMapFactory) {
            HashShortFloatMapFactory factory = (HashShortFloatMapFactory) obj;
            return commonEquals(factory) && keySpecialEquals(factory) &&
                    // boxing to treat NaNs correctly
                   ((Float) getDefaultValue()).equals(factory.getDefaultValue())
                    ;
        } else {
            return false;
        }
    }

    @Override
    public float getDefaultValue() {
        return 0.0f;
    }

    

    

    

    

    

    

    
    

    
    

    private  UpdatableQHashSeparateKVShortFloatMapGO shrunk(
            UpdatableQHashSeparateKVShortFloatMapGO map) {
        Predicate<HashContainer> shrinkCondition;
        if ((shrinkCondition = hashConf.getShrinkCondition()) != null) {
            if (shrinkCondition.test(map))
                map.shrink();
        }
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap() {
        return newUpdatableMap(getDefaultExpectedSize());
    }
    @Override
    @Nonnull
    public  MutableQHashSeparateKVShortFloatMapGO newMutableMap() {
        return newMutableMap(getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Map<Short, Float> map) {
        return shrunk(super.newUpdatableMap(map));
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Map<Short, Float> map1, Map<Short, Float> map2) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        return newUpdatableMap(map1, map2, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Map<Short, Float> map1, Map<Short, Float> map2,
            Map<Short, Float> map3) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        return newUpdatableMap(map1, map2, map3, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Map<Short, Float> map1, Map<Short, Float> map2,
            Map<Short, Float> map3, Map<Short, Float> map4) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        return newUpdatableMap(map1, map2, map3, map4, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Map<Short, Float> map1, Map<Short, Float> map2,
            Map<Short, Float> map3, Map<Short, Float> map4,
            Map<Short, Float> map5) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        expectedSize += (long) map5.size();
        return newUpdatableMap(map1, map2, map3, map4, map5, sizeAsInt(expectedSize));
    }


    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Map<Short, Float> map1, Map<Short, Float> map2,
            int expectedSize) {
        UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Map<Short, Float> map1, Map<Short, Float> map2,
            Map<Short, Float> map3, int expectedSize) {
        UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Map<Short, Float> map1, Map<Short, Float> map2,
            Map<Short, Float> map3, Map<Short, Float> map4,
            int expectedSize) {
        UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Map<Short, Float> map1, Map<Short, Float> map2,
            Map<Short, Float> map3, Map<Short, Float> map4,
            Map<Short, Float> map5, int expectedSize) {
        UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        map.putAll(map5);
        return shrunk(map);
    }


    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Consumer<net.openhft.koloboke.function.ShortFloatConsumer> entriesSupplier) {
        return newUpdatableMap(entriesSupplier, getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Consumer<net.openhft.koloboke.function.ShortFloatConsumer> entriesSupplier,
            int expectedSize) {
        final UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(expectedSize);
        entriesSupplier.accept(new net.openhft.koloboke.function.ShortFloatConsumer() {
             @Override
             public void accept(short k, float v) {
                 map.put(k, v);
             }
         });
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            short[] keys, float[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            short[] keys, float[] values, int expectedSize) {
        UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(expectedSize);
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
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Short[] keys, Float[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Short[] keys, Float[] values, int expectedSize) {
        UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(expectedSize);
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
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Iterable<Short> keys, Iterable<Float> values) {
        int expectedSize = keys instanceof Collection ? ((Collection) keys).size() :
                getDefaultExpectedSize();
        return newUpdatableMap(keys, values, expectedSize);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMap(
            Iterable<Short> keys, Iterable<Float> values, int expectedSize) {
        UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(expectedSize);
        Iterator<Short> keysIt = keys.iterator();
        Iterator<Float> valuesIt = values.iterator();
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
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMapOf(
            short k1, float v1) {
        UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(1);
        map.put(k1, v1);
        return map;
    }

    @Override
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMapOf(
            short k1, float v1, short k2, float v2) {
        UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(2);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMapOf(
            short k1, float v1, short k2, float v2,
            short k3, float v3) {
        UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(3);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMapOf(
            short k1, float v1, short k2, float v2,
            short k3, float v3, short k4, float v4) {
        UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(4);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortFloatMapGO newUpdatableMapOf(
            short k1, float v1, short k2, float v2,
            short k3, float v3, short k4, float v4,
            short k5, float v5) {
        UpdatableQHashSeparateKVShortFloatMapGO map = newUpdatableMap(5);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    
    


    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, int expectedSize) {
        MutableQHashSeparateKVShortFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, Map<Short, Float> map3, int expectedSize) {
        MutableQHashSeparateKVShortFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, Map<Short, Float> map3,
            Map<Short, Float> map4, int expectedSize) {
        MutableQHashSeparateKVShortFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, Map<Short, Float> map3,
            Map<Short, Float> map4, Map<Short, Float> map5, int expectedSize) {
        MutableQHashSeparateKVShortFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5, expectedSize));
        return res;
    }



    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(
            Consumer<net.openhft.koloboke.function.ShortFloatConsumer> entriesSupplier
            , int expectedSize) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(short[] keys,
            float[] values, int expectedSize) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(
            Short[] keys, Float[] values, int expectedSize) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(Iterable<Short> keys,
            Iterable<Float> values, int expectedSize) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    
    

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(
            Map<Short, Float> map) {
        MutableQHashSeparateKVShortFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2) {
        MutableQHashSeparateKVShortFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, Map<Short, Float> map3) {
        MutableQHashSeparateKVShortFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, Map<Short, Float> map3,
            Map<Short, Float> map4) {
        MutableQHashSeparateKVShortFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, Map<Short, Float> map3,
            Map<Short, Float> map4, Map<Short, Float> map5) {
        MutableQHashSeparateKVShortFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5));
        return res;
    }



    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(
            Consumer<net.openhft.koloboke.function.ShortFloatConsumer> entriesSupplier
            ) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(short[] keys,
            float[] values) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(
            Short[] keys, Float[] values) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMap(Iterable<Short> keys,
            Iterable<Float> values) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }


    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMapOf(short k1, float v1) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMapOf(short k1, float v1,
             short k2, float v2) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMapOf(short k1, float v1,
             short k2, float v2, short k3, float v3) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMapOf(short k1, float v1,
             short k2, float v2, short k3, float v3,
             short k4, float v4) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newMutableMapOf(short k1, float v1,
             short k2, float v2, short k3, float v3,
             short k4, float v4, short k5, float v5) {
        MutableQHashSeparateKVShortFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
    
    


    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, int expectedSize) {
        ImmutableQHashSeparateKVShortFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, Map<Short, Float> map3, int expectedSize) {
        ImmutableQHashSeparateKVShortFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, Map<Short, Float> map3,
            Map<Short, Float> map4, int expectedSize) {
        ImmutableQHashSeparateKVShortFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, Map<Short, Float> map3,
            Map<Short, Float> map4, Map<Short, Float> map5, int expectedSize) {
        ImmutableQHashSeparateKVShortFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5, expectedSize));
        return res;
    }



    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(
            Consumer<net.openhft.koloboke.function.ShortFloatConsumer> entriesSupplier
            , int expectedSize) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(short[] keys,
            float[] values, int expectedSize) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(
            Short[] keys, Float[] values, int expectedSize) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(Iterable<Short> keys,
            Iterable<Float> values, int expectedSize) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    
    

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(
            Map<Short, Float> map) {
        ImmutableQHashSeparateKVShortFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2) {
        ImmutableQHashSeparateKVShortFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, Map<Short, Float> map3) {
        ImmutableQHashSeparateKVShortFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, Map<Short, Float> map3,
            Map<Short, Float> map4) {
        ImmutableQHashSeparateKVShortFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(Map<Short, Float> map1,
            Map<Short, Float> map2, Map<Short, Float> map3,
            Map<Short, Float> map4, Map<Short, Float> map5) {
        ImmutableQHashSeparateKVShortFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5));
        return res;
    }



    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(
            Consumer<net.openhft.koloboke.function.ShortFloatConsumer> entriesSupplier
            ) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(short[] keys,
            float[] values) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(
            Short[] keys, Float[] values) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMap(Iterable<Short> keys,
            Iterable<Float> values) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }


    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMapOf(short k1, float v1) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMapOf(short k1, float v1,
             short k2, float v2) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMapOf(short k1, float v1,
             short k2, float v2, short k3, float v3) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMapOf(short k1, float v1,
             short k2, float v2, short k3, float v3,
             short k4, float v4) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortFloatMap newImmutableMapOf(short k1, float v1,
             short k2, float v2, short k3, float v3,
             short k4, float v4, short k5, float v5) {
        ImmutableQHashSeparateKVShortFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
}

