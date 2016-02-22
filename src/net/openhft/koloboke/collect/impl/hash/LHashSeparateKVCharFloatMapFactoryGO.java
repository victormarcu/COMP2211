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
import net.openhft.koloboke.collect.map.hash.HashCharFloatMapFactory;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.openhft.koloboke.collect.map.hash.HashCharFloatMap;

import javax.annotation.Nonnull;
import java.util.*;

import static net.openhft.koloboke.collect.impl.Containers.sizeAsInt;
import static net.openhft.koloboke.collect.impl.hash.LHashCapacities.configIsSuitableForMutableLHash;


public abstract class LHashSeparateKVCharFloatMapFactoryGO
        extends LHashSeparateKVCharFloatMapFactorySO {

    LHashSeparateKVCharFloatMapFactoryGO(HashConfig hashConf, int defaultExpectedSize
            , char lower, char upper) {
        super(hashConf, defaultExpectedSize, lower, upper);
    }

    

    abstract HashCharFloatMapFactory thisWith(HashConfig hashConf, int defaultExpectedSize, char lower, char upper);

    abstract HashCharFloatMapFactory lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, char lower, char upper);

    abstract HashCharFloatMapFactory qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, char lower, char upper);

    @Override
    public final HashCharFloatMapFactory withHashConfig(@Nonnull HashConfig hashConf) {
        if (configIsSuitableForMutableLHash(hashConf))
            return lHashLikeThisWith(hashConf, getDefaultExpectedSize()
            
                    , getLowerKeyDomainBound(), getUpperKeyDomainBound());
        return qHashLikeThisWith(hashConf, getDefaultExpectedSize()
            
                , getLowerKeyDomainBound(), getUpperKeyDomainBound());
    }

    @Override
    public final HashCharFloatMapFactory withDefaultExpectedSize(int defaultExpectedSize) {
        if (defaultExpectedSize == getDefaultExpectedSize())
            return this;
        return thisWith(getHashConfig(), defaultExpectedSize
                
                , getLowerKeyDomainBound(), getUpperKeyDomainBound());
    }

    final HashCharFloatMapFactory withDomain(char lower, char upper) {
        if (lower == getLowerKeyDomainBound() && upper == getUpperKeyDomainBound())
            return this;
        return thisWith(getHashConfig(), getDefaultExpectedSize(), lower, upper);
    }

    @Override
    public final HashCharFloatMapFactory withKeysDomain(char lower, char upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minPossibleKey shouldn't be greater " +
                    "than maxPossibleKey");
        return withDomain(lower, upper);
    }

    @Override
    public final HashCharFloatMapFactory withKeysDomainComplement(char lower, char upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minImpossibleKey shouldn't be greater " +
                    "than maxImpossibleKey");
        return withDomain((char) (upper + 1), (char) (lower - 1));
    }

    @Override
    public String toString() {
        return "HashCharFloatMapFactory[" + commonString() + keySpecialString() +
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
        if (obj instanceof HashCharFloatMapFactory) {
            HashCharFloatMapFactory factory = (HashCharFloatMapFactory) obj;
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

    

    

    

    

    

    

    
    

    
    

    private  UpdatableLHashSeparateKVCharFloatMapGO shrunk(
            UpdatableLHashSeparateKVCharFloatMapGO map) {
        Predicate<HashContainer> shrinkCondition;
        if ((shrinkCondition = hashConf.getShrinkCondition()) != null) {
            if (shrinkCondition.test(map))
                map.shrink();
        }
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap() {
        return newUpdatableMap(getDefaultExpectedSize());
    }
    @Override
    @Nonnull
    public  MutableLHashSeparateKVCharFloatMapGO newMutableMap() {
        return newMutableMap(getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Map<Character, Float> map) {
        return shrunk(super.newUpdatableMap(map));
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Map<Character, Float> map1, Map<Character, Float> map2) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        return newUpdatableMap(map1, map2, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Map<Character, Float> map1, Map<Character, Float> map2,
            Map<Character, Float> map3) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        return newUpdatableMap(map1, map2, map3, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Map<Character, Float> map1, Map<Character, Float> map2,
            Map<Character, Float> map3, Map<Character, Float> map4) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        return newUpdatableMap(map1, map2, map3, map4, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Map<Character, Float> map1, Map<Character, Float> map2,
            Map<Character, Float> map3, Map<Character, Float> map4,
            Map<Character, Float> map5) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        expectedSize += (long) map5.size();
        return newUpdatableMap(map1, map2, map3, map4, map5, sizeAsInt(expectedSize));
    }


    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Map<Character, Float> map1, Map<Character, Float> map2,
            int expectedSize) {
        UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Map<Character, Float> map1, Map<Character, Float> map2,
            Map<Character, Float> map3, int expectedSize) {
        UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Map<Character, Float> map1, Map<Character, Float> map2,
            Map<Character, Float> map3, Map<Character, Float> map4,
            int expectedSize) {
        UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Map<Character, Float> map1, Map<Character, Float> map2,
            Map<Character, Float> map3, Map<Character, Float> map4,
            Map<Character, Float> map5, int expectedSize) {
        UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        map.putAll(map5);
        return shrunk(map);
    }


    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Consumer<net.openhft.koloboke.function.CharFloatConsumer> entriesSupplier) {
        return newUpdatableMap(entriesSupplier, getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Consumer<net.openhft.koloboke.function.CharFloatConsumer> entriesSupplier,
            int expectedSize) {
        final UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(expectedSize);
        entriesSupplier.accept(new net.openhft.koloboke.function.CharFloatConsumer() {
             @Override
             public void accept(char k, float v) {
                 map.put(k, v);
             }
         });
        return shrunk(map);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            char[] keys, float[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            char[] keys, float[] values, int expectedSize) {
        UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(expectedSize);
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
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Character[] keys, Float[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Character[] keys, Float[] values, int expectedSize) {
        UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(expectedSize);
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
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Iterable<Character> keys, Iterable<Float> values) {
        int expectedSize = keys instanceof Collection ? ((Collection) keys).size() :
                getDefaultExpectedSize();
        return newUpdatableMap(keys, values, expectedSize);
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMap(
            Iterable<Character> keys, Iterable<Float> values, int expectedSize) {
        UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(expectedSize);
        Iterator<Character> keysIt = keys.iterator();
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
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMapOf(
            char k1, float v1) {
        UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(1);
        map.put(k1, v1);
        return map;
    }

    @Override
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMapOf(
            char k1, float v1, char k2, float v2) {
        UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(2);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMapOf(
            char k1, float v1, char k2, float v2,
            char k3, float v3) {
        UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(3);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMapOf(
            char k1, float v1, char k2, float v2,
            char k3, float v3, char k4, float v4) {
        UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(4);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVCharFloatMapGO newUpdatableMapOf(
            char k1, float v1, char k2, float v2,
            char k3, float v3, char k4, float v4,
            char k5, float v5) {
        UpdatableLHashSeparateKVCharFloatMapGO map = newUpdatableMap(5);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    
    


    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, int expectedSize) {
        MutableLHashSeparateKVCharFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, Map<Character, Float> map3, int expectedSize) {
        MutableLHashSeparateKVCharFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, Map<Character, Float> map3,
            Map<Character, Float> map4, int expectedSize) {
        MutableLHashSeparateKVCharFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, Map<Character, Float> map3,
            Map<Character, Float> map4, Map<Character, Float> map5, int expectedSize) {
        MutableLHashSeparateKVCharFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5, expectedSize));
        return res;
    }



    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(
            Consumer<net.openhft.koloboke.function.CharFloatConsumer> entriesSupplier
            , int expectedSize) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(char[] keys,
            float[] values, int expectedSize) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(
            Character[] keys, Float[] values, int expectedSize) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(Iterable<Character> keys,
            Iterable<Float> values, int expectedSize) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    
    

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(
            Map<Character, Float> map) {
        MutableLHashSeparateKVCharFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2) {
        MutableLHashSeparateKVCharFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, Map<Character, Float> map3) {
        MutableLHashSeparateKVCharFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, Map<Character, Float> map3,
            Map<Character, Float> map4) {
        MutableLHashSeparateKVCharFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, Map<Character, Float> map3,
            Map<Character, Float> map4, Map<Character, Float> map5) {
        MutableLHashSeparateKVCharFloatMapGO res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5));
        return res;
    }



    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(
            Consumer<net.openhft.koloboke.function.CharFloatConsumer> entriesSupplier
            ) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(char[] keys,
            float[] values) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(
            Character[] keys, Float[] values) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMap(Iterable<Character> keys,
            Iterable<Float> values) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }


    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMapOf(char k1, float v1) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMapOf(char k1, float v1,
             char k2, float v2) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMapOf(char k1, float v1,
             char k2, float v2, char k3, float v3) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMapOf(char k1, float v1,
             char k2, float v2, char k3, float v3,
             char k4, float v4) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newMutableMapOf(char k1, float v1,
             char k2, float v2, char k3, float v3,
             char k4, float v4, char k5, float v5) {
        MutableLHashSeparateKVCharFloatMapGO map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
    
    


    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, int expectedSize) {
        ImmutableLHashSeparateKVCharFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, Map<Character, Float> map3, int expectedSize) {
        ImmutableLHashSeparateKVCharFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, Map<Character, Float> map3,
            Map<Character, Float> map4, int expectedSize) {
        ImmutableLHashSeparateKVCharFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, Map<Character, Float> map3,
            Map<Character, Float> map4, Map<Character, Float> map5, int expectedSize) {
        ImmutableLHashSeparateKVCharFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5, expectedSize));
        return res;
    }



    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(
            Consumer<net.openhft.koloboke.function.CharFloatConsumer> entriesSupplier
            , int expectedSize) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(char[] keys,
            float[] values, int expectedSize) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(
            Character[] keys, Float[] values, int expectedSize) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(Iterable<Character> keys,
            Iterable<Float> values, int expectedSize) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    
    

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(
            Map<Character, Float> map) {
        ImmutableLHashSeparateKVCharFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2) {
        ImmutableLHashSeparateKVCharFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, Map<Character, Float> map3) {
        ImmutableLHashSeparateKVCharFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, Map<Character, Float> map3,
            Map<Character, Float> map4) {
        ImmutableLHashSeparateKVCharFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4));
        return res;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(Map<Character, Float> map1,
            Map<Character, Float> map2, Map<Character, Float> map3,
            Map<Character, Float> map4, Map<Character, Float> map5) {
        ImmutableLHashSeparateKVCharFloatMapGO res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5));
        return res;
    }



    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(
            Consumer<net.openhft.koloboke.function.CharFloatConsumer> entriesSupplier
            ) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(char[] keys,
            float[] values) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(
            Character[] keys, Float[] values) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMap(Iterable<Character> keys,
            Iterable<Float> values) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }


    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMapOf(char k1, float v1) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMapOf(char k1, float v1,
             char k2, float v2) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMapOf(char k1, float v1,
             char k2, float v2, char k3, float v3) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMapOf(char k1, float v1,
             char k2, float v2, char k3, float v3,
             char k4, float v4) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    @Nonnull
    public  HashCharFloatMap newImmutableMapOf(char k1, float v1,
             char k2, float v2, char k3, float v3,
             char k4, float v4, char k5, float v5) {
        ImmutableLHashSeparateKVCharFloatMapGO map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
}

