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
import net.openhft.koloboke.collect.map.hash.HashShortDoubleMapFactory;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.openhft.koloboke.collect.map.hash.HashShortDoubleMap;

import javax.annotation.Nonnull;
import java.util.*;

import static net.openhft.koloboke.collect.impl.Containers.sizeAsInt;
import static net.openhft.koloboke.collect.impl.hash.LHashCapacities.configIsSuitableForMutableLHash;


public abstract class QHashSeparateKVShortDoubleMapFactoryGO
        extends QHashSeparateKVShortDoubleMapFactorySO {

    QHashSeparateKVShortDoubleMapFactoryGO(HashConfig hashConf, int defaultExpectedSize
            , short lower, short upper) {
        super(hashConf, defaultExpectedSize, lower, upper);
    }

    

    abstract HashShortDoubleMapFactory thisWith(HashConfig hashConf, int defaultExpectedSize, short lower, short upper);

    abstract HashShortDoubleMapFactory lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, short lower, short upper);

    abstract HashShortDoubleMapFactory qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, short lower, short upper);

    @Override
    public final HashShortDoubleMapFactory withHashConfig(@Nonnull HashConfig hashConf) {
        if (configIsSuitableForMutableLHash(hashConf))
            return lHashLikeThisWith(hashConf, getDefaultExpectedSize()
            
                    , getLowerKeyDomainBound(), getUpperKeyDomainBound());
        return qHashLikeThisWith(hashConf, getDefaultExpectedSize()
            
                , getLowerKeyDomainBound(), getUpperKeyDomainBound());
    }

    @Override
    public final HashShortDoubleMapFactory withDefaultExpectedSize(int defaultExpectedSize) {
        if (defaultExpectedSize == getDefaultExpectedSize())
            return this;
        return thisWith(getHashConfig(), defaultExpectedSize
                
                , getLowerKeyDomainBound(), getUpperKeyDomainBound());
    }

    final HashShortDoubleMapFactory withDomain(short lower, short upper) {
        if (lower == getLowerKeyDomainBound() && upper == getUpperKeyDomainBound())
            return this;
        return thisWith(getHashConfig(), getDefaultExpectedSize(), lower, upper);
    }

    @Override
    public final HashShortDoubleMapFactory withKeysDomain(short lower, short upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minPossibleKey shouldn't be greater " +
                    "than maxPossibleKey");
        return withDomain(lower, upper);
    }

    @Override
    public final HashShortDoubleMapFactory withKeysDomainComplement(short lower, short upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minImpossibleKey shouldn't be greater " +
                    "than maxImpossibleKey");
        return withDomain((short) (upper + 1), (short) (lower - 1));
    }

    @Override
    public String toString() {
        return "HashShortDoubleMapFactory[" + commonString() + keySpecialString() +
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
        if (obj instanceof HashShortDoubleMapFactory) {
            HashShortDoubleMapFactory factory = (HashShortDoubleMapFactory) obj;
            return commonEquals(factory) && keySpecialEquals(factory) &&
                    // boxing to treat NaNs correctly
                   ((Double) getDefaultValue()).equals(factory.getDefaultValue())
                    ;
        } else {
            return false;
        }
    }

    @Override
    public double getDefaultValue() {
        return 0.0;
    }

    

    

    

    

    

    

    
    

    
    

    private  UpdatableQHashSeparateKVShortDoubleMapGO shrunk(
            UpdatableQHashSeparateKVShortDoubleMapGO map) {
        Predicate<HashContainer> shrinkCondition;
        if ((shrinkCondition = hashConf.getShrinkCondition()) != null) {
            if (shrinkCondition.test(map))
                map.shrink();
        }
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap() {
        return newUpdatableMap(getDefaultExpectedSize());
    }
    @Override
    @Nonnull
    public  MutableQHashSeparateKVShortDoubleMapGO newMutableMap() {
        return newMutableMap(getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Map<Short, Double> map) {
        return shrunk(super.newUpdatableMap(map));
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Map<Short, Double> map1, Map<Short, Double> map2) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        return newUpdatableMap(map1, map2, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Map<Short, Double> map1, Map<Short, Double> map2,
            Map<Short, Double> map3) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        return newUpdatableMap(map1, map2, map3, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Map<Short, Double> map1, Map<Short, Double> map2,
            Map<Short, Double> map3, Map<Short, Double> map4) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        return newUpdatableMap(map1, map2, map3, map4, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Map<Short, Double> map1, Map<Short, Double> map2,
            Map<Short, Double> map3, Map<Short, Double> map4,
            Map<Short, Double> map5) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        expectedSize += (long) map5.size();
        return newUpdatableMap(map1, map2, map3, map4, map5, sizeAsInt(expectedSize));
    }


    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Map<Short, Double> map1, Map<Short, Double> map2,
            int expectedSize) {
        UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Map<Short, Double> map1, Map<Short, Double> map2,
            Map<Short, Double> map3, int expectedSize) {
        UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Map<Short, Double> map1, Map<Short, Double> map2,
            Map<Short, Double> map3, Map<Short, Double> map4,
            int expectedSize) {
        UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Map<Short, Double> map1, Map<Short, Double> map2,
            Map<Short, Double> map3, Map<Short, Double> map4,
            Map<Short, Double> map5, int expectedSize) {
        UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        map.putAll(map5);
        return shrunk(map);
    }


    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Consumer<net.openhft.koloboke.function.ShortDoubleConsumer> entriesSupplier) {
        return newUpdatableMap(entriesSupplier, getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Consumer<net.openhft.koloboke.function.ShortDoubleConsumer> entriesSupplier,
            int expectedSize) {
        final UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(expectedSize);
        entriesSupplier.accept(new net.openhft.koloboke.function.ShortDoubleConsumer() {
             @Override
             public void accept(short k, double v) {
                 map.put(k, v);
             }
         });
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            short[] keys, double[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            short[] keys, double[] values, int expectedSize) {
        UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(expectedSize);
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
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Short[] keys, Double[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Short[] keys, Double[] values, int expectedSize) {
        UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(expectedSize);
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
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Iterable<Short> keys, Iterable<Double> values) {
        int expectedSize = keys instanceof Collection ? ((Collection) keys).size() :
                getDefaultExpectedSize();
        return newUpdatableMap(keys, values, expectedSize);
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMap(
            Iterable<Short> keys, Iterable<Double> values, int expectedSize) {
        UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(expectedSize);
        Iterator<Short> keysIt = keys.iterator();
        Iterator<Double> valuesIt = values.iterator();
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
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMapOf(
            short k1, double v1) {
        UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(1);
        map.put(k1, v1);
        return map;
    }

    @Override
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMapOf(
            short k1, double v1, short k2, double v2) {
        UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(2);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMapOf(
            short k1, double v1, short k2, double v2,
            short k3, double v3) {
        UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(3);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMapOf(
            short k1, double v1, short k2, double v2,
            short k3, double v3, short k4, double v4) {
        UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(4);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVShortDoubleMapGO newUpdatableMapOf(
            short k1, double v1, short k2, double v2,
            short k3, double v3, short k4, double v4,
            short k5, double v5) {
        UpdatableQHashSeparateKVShortDoubleMapGO map = newUpdatableMap(5);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    
    


    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, int expectedSize) {
        MutableQHashSeparateKVShortDoubleMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, Map<Short, Double> map3, int expectedSize) {
        MutableQHashSeparateKVShortDoubleMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, Map<Short, Double> map3,
            Map<Short, Double> map4, int expectedSize) {
        MutableQHashSeparateKVShortDoubleMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, Map<Short, Double> map3,
            Map<Short, Double> map4, Map<Short, Double> map5, int expectedSize) {
        MutableQHashSeparateKVShortDoubleMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5, expectedSize));
        return res;
    }



    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(
            Consumer<net.openhft.koloboke.function.ShortDoubleConsumer> entriesSupplier
            , int expectedSize) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(short[] keys,
            double[] values, int expectedSize) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(
            Short[] keys, Double[] values, int expectedSize) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(Iterable<Short> keys,
            Iterable<Double> values, int expectedSize) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    
    

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(
            Map<Short, Double> map) {
        MutableQHashSeparateKVShortDoubleMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2) {
        MutableQHashSeparateKVShortDoubleMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, Map<Short, Double> map3) {
        MutableQHashSeparateKVShortDoubleMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, Map<Short, Double> map3,
            Map<Short, Double> map4) {
        MutableQHashSeparateKVShortDoubleMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, Map<Short, Double> map3,
            Map<Short, Double> map4, Map<Short, Double> map5) {
        MutableQHashSeparateKVShortDoubleMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5));
        return res;
    }



    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(
            Consumer<net.openhft.koloboke.function.ShortDoubleConsumer> entriesSupplier
            ) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(short[] keys,
            double[] values) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(
            Short[] keys, Double[] values) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMap(Iterable<Short> keys,
            Iterable<Double> values) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }


    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMapOf(short k1, double v1) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMapOf(short k1, double v1,
             short k2, double v2) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMapOf(short k1, double v1,
             short k2, double v2, short k3, double v3) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMapOf(short k1, double v1,
             short k2, double v2, short k3, double v3,
             short k4, double v4) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newMutableMapOf(short k1, double v1,
             short k2, double v2, short k3, double v3,
             short k4, double v4, short k5, double v5) {
        MutableQHashSeparateKVShortDoubleMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
    
    


    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, int expectedSize) {
        ImmutableQHashSeparateKVShortDoubleMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, Map<Short, Double> map3, int expectedSize) {
        ImmutableQHashSeparateKVShortDoubleMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, Map<Short, Double> map3,
            Map<Short, Double> map4, int expectedSize) {
        ImmutableQHashSeparateKVShortDoubleMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, Map<Short, Double> map3,
            Map<Short, Double> map4, Map<Short, Double> map5, int expectedSize) {
        ImmutableQHashSeparateKVShortDoubleMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5, expectedSize));
        return res;
    }



    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(
            Consumer<net.openhft.koloboke.function.ShortDoubleConsumer> entriesSupplier
            , int expectedSize) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(short[] keys,
            double[] values, int expectedSize) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(
            Short[] keys, Double[] values, int expectedSize) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(Iterable<Short> keys,
            Iterable<Double> values, int expectedSize) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    
    

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(
            Map<Short, Double> map) {
        ImmutableQHashSeparateKVShortDoubleMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2) {
        ImmutableQHashSeparateKVShortDoubleMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, Map<Short, Double> map3) {
        ImmutableQHashSeparateKVShortDoubleMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, Map<Short, Double> map3,
            Map<Short, Double> map4) {
        ImmutableQHashSeparateKVShortDoubleMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4));
        return res;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(Map<Short, Double> map1,
            Map<Short, Double> map2, Map<Short, Double> map3,
            Map<Short, Double> map4, Map<Short, Double> map5) {
        ImmutableQHashSeparateKVShortDoubleMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5));
        return res;
    }



    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(
            Consumer<net.openhft.koloboke.function.ShortDoubleConsumer> entriesSupplier
            ) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(short[] keys,
            double[] values) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(
            Short[] keys, Double[] values) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMap(Iterable<Short> keys,
            Iterable<Double> values) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }


    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMapOf(short k1, double v1) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMapOf(short k1, double v1,
             short k2, double v2) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMapOf(short k1, double v1,
             short k2, double v2, short k3, double v3) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMapOf(short k1, double v1,
             short k2, double v2, short k3, double v3,
             short k4, double v4) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    @Nonnull
    public  HashShortDoubleMap newImmutableMapOf(short k1, double v1,
             short k2, double v2, short k3, double v3,
             short k4, double v4, short k5, double v5) {
        ImmutableQHashSeparateKVShortDoubleMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
}

