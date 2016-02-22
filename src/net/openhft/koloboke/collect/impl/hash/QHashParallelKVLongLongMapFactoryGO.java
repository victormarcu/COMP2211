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
import net.openhft.koloboke.collect.map.hash.HashLongLongMapFactory;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.openhft.koloboke.collect.map.hash.HashLongLongMap;

import javax.annotation.Nonnull;
import java.util.*;

import static net.openhft.koloboke.collect.impl.Containers.sizeAsInt;
import static net.openhft.koloboke.collect.impl.hash.LHashCapacities.configIsSuitableForMutableLHash;


public abstract class QHashParallelKVLongLongMapFactoryGO
        extends QHashParallelKVLongLongMapFactorySO {

    QHashParallelKVLongLongMapFactoryGO(HashConfig hashConf, int defaultExpectedSize
            , long lower, long upper) {
        super(hashConf, defaultExpectedSize, lower, upper);
    }

    

    abstract HashLongLongMapFactory thisWith(HashConfig hashConf, int defaultExpectedSize, long lower, long upper);

    abstract HashLongLongMapFactory lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, long lower, long upper);

    abstract HashLongLongMapFactory qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, long lower, long upper);

    @Override
    public final HashLongLongMapFactory withHashConfig(@Nonnull HashConfig hashConf) {
        if (configIsSuitableForMutableLHash(hashConf))
            return lHashLikeThisWith(hashConf, getDefaultExpectedSize()
            
                    , getLowerKeyDomainBound(), getUpperKeyDomainBound());
        return qHashLikeThisWith(hashConf, getDefaultExpectedSize()
            
                , getLowerKeyDomainBound(), getUpperKeyDomainBound());
    }

    @Override
    public final HashLongLongMapFactory withDefaultExpectedSize(int defaultExpectedSize) {
        if (defaultExpectedSize == getDefaultExpectedSize())
            return this;
        return thisWith(getHashConfig(), defaultExpectedSize
                
                , getLowerKeyDomainBound(), getUpperKeyDomainBound());
    }

    final HashLongLongMapFactory withDomain(long lower, long upper) {
        if (lower == getLowerKeyDomainBound() && upper == getUpperKeyDomainBound())
            return this;
        return thisWith(getHashConfig(), getDefaultExpectedSize(), lower, upper);
    }

    @Override
    public final HashLongLongMapFactory withKeysDomain(long lower, long upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minPossibleKey shouldn't be greater " +
                    "than maxPossibleKey");
        return withDomain(lower, upper);
    }

    @Override
    public final HashLongLongMapFactory withKeysDomainComplement(long lower, long upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minImpossibleKey shouldn't be greater " +
                    "than maxImpossibleKey");
        return withDomain((long) (upper + 1), (long) (lower - 1));
    }

    @Override
    public String toString() {
        return "HashLongLongMapFactory[" + commonString() + keySpecialString() +
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
        if (obj instanceof HashLongLongMapFactory) {
            HashLongLongMapFactory factory = (HashLongLongMapFactory) obj;
            return commonEquals(factory) && keySpecialEquals(factory) &&
                    // boxing to treat NaNs correctly
                   ((Long) getDefaultValue()).equals(factory.getDefaultValue())
                    ;
        } else {
            return false;
        }
    }

    @Override
    public long getDefaultValue() {
        return 0L;
    }

    

    

    

    

    

    

    
    

    
    

    private  UpdatableQHashParallelKVLongLongMapGO shrunk(
            UpdatableQHashParallelKVLongLongMapGO map) {
        Predicate<HashContainer> shrinkCondition;
        if ((shrinkCondition = hashConf.getShrinkCondition()) != null) {
            if (shrinkCondition.test(map))
                map.shrink();
        }
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap() {
        return newUpdatableMap(getDefaultExpectedSize());
    }
    @Override
    @Nonnull
    public  MutableQHashParallelKVLongLongMapGO newMutableMap() {
        return newMutableMap(getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Map<Long, Long> map) {
        return shrunk(super.newUpdatableMap(map));
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Map<Long, Long> map1, Map<Long, Long> map2) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        return newUpdatableMap(map1, map2, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Map<Long, Long> map1, Map<Long, Long> map2,
            Map<Long, Long> map3) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        return newUpdatableMap(map1, map2, map3, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Map<Long, Long> map1, Map<Long, Long> map2,
            Map<Long, Long> map3, Map<Long, Long> map4) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        return newUpdatableMap(map1, map2, map3, map4, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Map<Long, Long> map1, Map<Long, Long> map2,
            Map<Long, Long> map3, Map<Long, Long> map4,
            Map<Long, Long> map5) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        expectedSize += (long) map5.size();
        return newUpdatableMap(map1, map2, map3, map4, map5, sizeAsInt(expectedSize));
    }


    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Map<Long, Long> map1, Map<Long, Long> map2,
            int expectedSize) {
        UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Map<Long, Long> map1, Map<Long, Long> map2,
            Map<Long, Long> map3, int expectedSize) {
        UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Map<Long, Long> map1, Map<Long, Long> map2,
            Map<Long, Long> map3, Map<Long, Long> map4,
            int expectedSize) {
        UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Map<Long, Long> map1, Map<Long, Long> map2,
            Map<Long, Long> map3, Map<Long, Long> map4,
            Map<Long, Long> map5, int expectedSize) {
        UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        map.putAll(map5);
        return shrunk(map);
    }


    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Consumer<net.openhft.koloboke.function.LongLongConsumer> entriesSupplier) {
        return newUpdatableMap(entriesSupplier, getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Consumer<net.openhft.koloboke.function.LongLongConsumer> entriesSupplier,
            int expectedSize) {
        final UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(expectedSize);
        entriesSupplier.accept(new net.openhft.koloboke.function.LongLongConsumer() {
             @Override
             public void accept(long k, long v) {
                 map.put(k, v);
             }
         });
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            long[] keys, long[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            long[] keys, long[] values, int expectedSize) {
        UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(expectedSize);
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
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Long[] keys, Long[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Long[] keys, Long[] values, int expectedSize) {
        UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(expectedSize);
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
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Iterable<Long> keys, Iterable<Long> values) {
        int expectedSize = keys instanceof Collection ? ((Collection) keys).size() :
                getDefaultExpectedSize();
        return newUpdatableMap(keys, values, expectedSize);
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMap(
            Iterable<Long> keys, Iterable<Long> values, int expectedSize) {
        UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(expectedSize);
        Iterator<Long> keysIt = keys.iterator();
        Iterator<Long> valuesIt = values.iterator();
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
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMapOf(
            long k1, long v1) {
        UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(1);
        map.put(k1, v1);
        return map;
    }

    @Override
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMapOf(
            long k1, long v1, long k2, long v2) {
        UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(2);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMapOf(
            long k1, long v1, long k2, long v2,
            long k3, long v3) {
        UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(3);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMapOf(
            long k1, long v1, long k2, long v2,
            long k3, long v3, long k4, long v4) {
        UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(4);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableQHashParallelKVLongLongMapGO newUpdatableMapOf(
            long k1, long v1, long k2, long v2,
            long k3, long v3, long k4, long v4,
            long k5, long v5) {
        UpdatableQHashParallelKVLongLongMapGO map = newUpdatableMap(5);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    
    


    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, int expectedSize) {
        MutableQHashParallelKVLongLongMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, Map<Long, Long> map3, int expectedSize) {
        MutableQHashParallelKVLongLongMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, Map<Long, Long> map3,
            Map<Long, Long> map4, int expectedSize) {
        MutableQHashParallelKVLongLongMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, Map<Long, Long> map3,
            Map<Long, Long> map4, Map<Long, Long> map5, int expectedSize) {
        MutableQHashParallelKVLongLongMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5, expectedSize));
        return res;
    }



    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(
            Consumer<net.openhft.koloboke.function.LongLongConsumer> entriesSupplier
            , int expectedSize) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(long[] keys,
            long[] values, int expectedSize) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(
            Long[] keys, Long[] values, int expectedSize) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(Iterable<Long> keys,
            Iterable<Long> values, int expectedSize) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    
    

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(
            Map<Long, Long> map) {
        MutableQHashParallelKVLongLongMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2) {
        MutableQHashParallelKVLongLongMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, Map<Long, Long> map3) {
        MutableQHashParallelKVLongLongMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, Map<Long, Long> map3,
            Map<Long, Long> map4) {
        MutableQHashParallelKVLongLongMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, Map<Long, Long> map3,
            Map<Long, Long> map4, Map<Long, Long> map5) {
        MutableQHashParallelKVLongLongMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5));
        return res;
    }



    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(
            Consumer<net.openhft.koloboke.function.LongLongConsumer> entriesSupplier
            ) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(long[] keys,
            long[] values) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(
            Long[] keys, Long[] values) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMap(Iterable<Long> keys,
            Iterable<Long> values) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }


    @Override
    @Nonnull
    public  HashLongLongMap newMutableMapOf(long k1, long v1) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMapOf(long k1, long v1,
             long k2, long v2) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMapOf(long k1, long v1,
             long k2, long v2, long k3, long v3) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMapOf(long k1, long v1,
             long k2, long v2, long k3, long v3,
             long k4, long v4) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newMutableMapOf(long k1, long v1,
             long k2, long v2, long k3, long v3,
             long k4, long v4, long k5, long v5) {
        MutableQHashParallelKVLongLongMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
    
    


    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, int expectedSize) {
        ImmutableQHashParallelKVLongLongMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, Map<Long, Long> map3, int expectedSize) {
        ImmutableQHashParallelKVLongLongMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, Map<Long, Long> map3,
            Map<Long, Long> map4, int expectedSize) {
        ImmutableQHashParallelKVLongLongMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, Map<Long, Long> map3,
            Map<Long, Long> map4, Map<Long, Long> map5, int expectedSize) {
        ImmutableQHashParallelKVLongLongMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5, expectedSize));
        return res;
    }



    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(
            Consumer<net.openhft.koloboke.function.LongLongConsumer> entriesSupplier
            , int expectedSize) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(long[] keys,
            long[] values, int expectedSize) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(
            Long[] keys, Long[] values, int expectedSize) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(Iterable<Long> keys,
            Iterable<Long> values, int expectedSize) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    
    

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(
            Map<Long, Long> map) {
        ImmutableQHashParallelKVLongLongMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2) {
        ImmutableQHashParallelKVLongLongMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, Map<Long, Long> map3) {
        ImmutableQHashParallelKVLongLongMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, Map<Long, Long> map3,
            Map<Long, Long> map4) {
        ImmutableQHashParallelKVLongLongMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4));
        return res;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(Map<Long, Long> map1,
            Map<Long, Long> map2, Map<Long, Long> map3,
            Map<Long, Long> map4, Map<Long, Long> map5) {
        ImmutableQHashParallelKVLongLongMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5));
        return res;
    }



    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(
            Consumer<net.openhft.koloboke.function.LongLongConsumer> entriesSupplier
            ) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(long[] keys,
            long[] values) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(
            Long[] keys, Long[] values) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMap(Iterable<Long> keys,
            Iterable<Long> values) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }


    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMapOf(long k1, long v1) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMapOf(long k1, long v1,
             long k2, long v2) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMapOf(long k1, long v1,
             long k2, long v2, long k3, long v3) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMapOf(long k1, long v1,
             long k2, long v2, long k3, long v3,
             long k4, long v4) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    @Nonnull
    public  HashLongLongMap newImmutableMapOf(long k1, long v1,
             long k2, long v2, long k3, long v3,
             long k4, long v4, long k5, long v5) {
        ImmutableQHashParallelKVLongLongMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
}

