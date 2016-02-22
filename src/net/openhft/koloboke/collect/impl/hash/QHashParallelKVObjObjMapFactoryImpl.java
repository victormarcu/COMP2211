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
import net.openhft.koloboke.collect.map.hash.*;

import javax.annotation.Nonnull;


public final class QHashParallelKVObjObjMapFactoryImpl<K, V>
        extends QHashParallelKVObjObjMapFactoryGO<K, V> {

    

    

    
    
    

    
    
    

    /** For ServiceLoader */
    public QHashParallelKVObjObjMapFactoryImpl() {
        this(HashConfig.getDefault(), 10
            , false
            );
    }

    

    

    

    QHashParallelKVObjObjMapFactoryImpl(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
        super(hashConf, defaultExpectedSize, isNullKeyAllowed
            );
    }

    @Override
    HashObjObjMapFactory<K, V> thisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
        return new QHashParallelKVObjObjMapFactoryImpl<K, V>(hashConf, defaultExpectedSize, isNullKeyAllowed
            );
    }

    @Override
    HashObjObjMapFactory<K, V> qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
        return new QHashParallelKVObjObjMapFactoryImpl<K, V>(hashConf, defaultExpectedSize, isNullKeyAllowed
            );
    }
    @Override
    HashObjObjMapFactory<K, V> lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
        return new LHashParallelKVObjObjMapFactoryImpl<K, V>(hashConf, defaultExpectedSize, isNullKeyAllowed
            );
    }

    @Override
    @Nonnull
    public HashObjObjMapFactory<K, V> withKeyEquivalence(
            @Nonnull Equivalence<? super K> keyEquivalence) {
        if (keyEquivalence.equals(Equivalence.defaultEquality())) {
            // noinspection unchecked
            return (HashObjObjMapFactory<K, V>) this;
        }
        return new WithCustomKeyEquivalence<K, V>(getHashConfig(), getDefaultExpectedSize()
        , isNullKeyAllowed(),
                (Equivalence<K>) keyEquivalence);
    }

    @Override
    @Nonnull
    public HashObjObjMapFactory<K, V> withValueEquivalence(
            @Nonnull Equivalence<? super V> valueEquivalence) {
        if (valueEquivalence.equals(Equivalence.defaultEquality())) {
            // noinspection unchecked
            return (HashObjObjMapFactory<K, V>) this;
        }
        return new WithCustomValueEquivalence<K, V>(getHashConfig(), getDefaultExpectedSize()
        , isNullKeyAllowed(),
                (Equivalence<V>) valueEquivalence);
    }

    static class WithCustomKeyEquivalence<K, V>
            extends QHashParallelKVObjObjMapFactoryGO<K, V> {

        private final Equivalence<K> keyEquivalence;

        WithCustomKeyEquivalence(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            , Equivalence<K> keyEquivalence) {
            super(hashConf, defaultExpectedSize, isNullKeyAllowed
            );
            this.keyEquivalence = keyEquivalence;
        }

        @Override
        @Nonnull
        public Equivalence<K> getKeyEquivalence() {
            return keyEquivalence;
        }

        @Override
        <K2 extends K, V2 extends V> MutableQHashParallelKVObjObjMapGO<K2, V2>
        uninitializedMutableMap() {
            MutableQHashParallelKVObjObjMap.WithCustomKeyEquivalence<K2, V2> map =
                    new MutableQHashParallelKVObjObjMap.WithCustomKeyEquivalence<K2, V2>();
            map.keyEquivalence = keyEquivalence;
            return map;
        }
        @Override
        <K2 extends K, V2 extends V> UpdatableQHashParallelKVObjObjMapGO<K2, V2>
        uninitializedUpdatableMap() {
            UpdatableQHashParallelKVObjObjMap.WithCustomKeyEquivalence<K2, V2> map =
                    new UpdatableQHashParallelKVObjObjMap.WithCustomKeyEquivalence<K2, V2>();
            map.keyEquivalence = keyEquivalence;
            return map;
        }
        @Override
        <K2 extends K, V2 extends V> ImmutableQHashParallelKVObjObjMapGO<K2, V2>
        uninitializedImmutableMap() {
            ImmutableQHashParallelKVObjObjMap.WithCustomKeyEquivalence<K2, V2> map =
                    new ImmutableQHashParallelKVObjObjMap.WithCustomKeyEquivalence<K2, V2>();
            map.keyEquivalence = keyEquivalence;
            return map;
        }

        @Override
        @Nonnull
        public HashObjObjMapFactory<K, V> withKeyEquivalence(
                @Nonnull Equivalence<? super K> keyEquivalence) {
            if (keyEquivalence.equals(Equivalence.defaultEquality()))
                return new QHashParallelKVObjObjMapFactoryImpl<K, V>(getHashConfig(), getDefaultExpectedSize()
        , isNullKeyAllowed());
            if (keyEquivalence.equals(this.keyEquivalence)) {
                // noinspection unchecked
                return (HashObjObjMapFactory<K, V>) this;
            }
            return new WithCustomKeyEquivalence<K, V>(getHashConfig(), getDefaultExpectedSize()
        , isNullKeyAllowed(),
                    (Equivalence<K>) keyEquivalence);
        }

        @Override
        @Nonnull
        public HashObjObjMapFactory<K, V> withValueEquivalence(
                @Nonnull Equivalence<? super V> valueEquivalence) {
            if (valueEquivalence.equals(Equivalence.defaultEquality())) {
                // noinspection unchecked
                return (HashObjObjMapFactory<K, V>) this;
            }
            return new WithCustomEquivalences<K, V>(getHashConfig(), getDefaultExpectedSize()
        , isNullKeyAllowed(),
                    keyEquivalence, (Equivalence<V>) valueEquivalence);
        }

        @Override
        HashObjObjMapFactory<K, V> thisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
            return new WithCustomKeyEquivalence<K, V>(hashConf, defaultExpectedSize, isNullKeyAllowed
            , keyEquivalence);
        }

        @Override
        HashObjObjMapFactory<K, V> qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
            return new QHashParallelKVObjObjMapFactoryImpl.WithCustomKeyEquivalence<K, V>(
                    hashConf, defaultExpectedSize, isNullKeyAllowed
            , keyEquivalence);
        }
        @Override
        HashObjObjMapFactory<K, V> lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
            return new LHashParallelKVObjObjMapFactoryImpl.WithCustomKeyEquivalence<K, V>(
                    hashConf, defaultExpectedSize, isNullKeyAllowed
            , keyEquivalence);
        }
    }

    static final class WithCustomValueEquivalence<K, V>
            extends QHashParallelKVObjObjMapFactoryGO<K, V> {

        private final Equivalence<V> valueEquivalence;
        WithCustomValueEquivalence(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ,
                Equivalence<V> valueEquivalence) {
            super(hashConf, defaultExpectedSize, isNullKeyAllowed
            );
            this.valueEquivalence = valueEquivalence;
        }

        @Override
        @Nonnull
        public Equivalence<V> getValueEquivalence() {
            return valueEquivalence;
        }

        @Override
        <K2 extends K, V2 extends V> MutableQHashParallelKVObjObjMapGO<K2, V2>
        uninitializedMutableMap() {
            MutableQHashParallelKVObjObjMap.WithCustomValueEquivalence<K2, V2> map =
                    new MutableQHashParallelKVObjObjMap.WithCustomValueEquivalence<K2, V2>();
            map.valueEquivalence = valueEquivalence;
            return map;
        }
        @Override
        <K2 extends K, V2 extends V> UpdatableQHashParallelKVObjObjMapGO<K2, V2>
        uninitializedUpdatableMap() {
            UpdatableQHashParallelKVObjObjMap.WithCustomValueEquivalence<K2, V2> map =
                    new UpdatableQHashParallelKVObjObjMap.WithCustomValueEquivalence<K2, V2>();
            map.valueEquivalence = valueEquivalence;
            return map;
        }
        @Override
        <K2 extends K, V2 extends V> ImmutableQHashParallelKVObjObjMapGO<K2, V2>
        uninitializedImmutableMap() {
            ImmutableQHashParallelKVObjObjMap.WithCustomValueEquivalence<K2, V2> map =
                    new ImmutableQHashParallelKVObjObjMap.WithCustomValueEquivalence<K2, V2>();
            map.valueEquivalence = valueEquivalence;
            return map;
        }

        @Override
        @Nonnull
        public HashObjObjMapFactory<K, V> withKeyEquivalence(
                @Nonnull Equivalence<? super K> keyEquivalence) {
            if (keyEquivalence.equals(Equivalence.defaultEquality())) {
                // noinspection unchecked
                return (HashObjObjMapFactory<K, V>) this;
            }
            return new WithCustomEquivalences<K, V>(getHashConfig(), getDefaultExpectedSize()
        , isNullKeyAllowed(),
                    (Equivalence<K>) keyEquivalence, valueEquivalence);
        }

        @Override
        @Nonnull
        public HashObjObjMapFactory<K, V> withValueEquivalence(
                @Nonnull Equivalence<? super V> valueEquivalence) {
            if (valueEquivalence.equals(Equivalence.defaultEquality()))
                return new QHashParallelKVObjObjMapFactoryImpl<K, V>(getHashConfig(), getDefaultExpectedSize()
        , isNullKeyAllowed());
            if (valueEquivalence.equals(this.valueEquivalence))
                // noinspection unchecked
                return (HashObjObjMapFactory<K, V>) this;
            return new WithCustomValueEquivalence<K, V>(getHashConfig(), getDefaultExpectedSize()
        , isNullKeyAllowed(),
                    (Equivalence<V>) valueEquivalence);
        }

        @Override
        HashObjObjMapFactory<K, V> thisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
            return new WithCustomValueEquivalence<K, V>(hashConf, defaultExpectedSize, isNullKeyAllowed
            ,
                    valueEquivalence);
        }

        @Override
        HashObjObjMapFactory<K, V> qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
            return new QHashParallelKVObjObjMapFactoryImpl.WithCustomValueEquivalence<K, V>(
                    hashConf, defaultExpectedSize, isNullKeyAllowed
            , valueEquivalence);
        }
        @Override
        HashObjObjMapFactory<K, V> lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
            return new LHashParallelKVObjObjMapFactoryImpl.WithCustomValueEquivalence<K, V>(
                    hashConf, defaultExpectedSize, isNullKeyAllowed
            , valueEquivalence);
        }
    }

    static final class WithCustomEquivalences<K, V>
            extends QHashParallelKVObjObjMapFactoryGO<K, V> {
        private final Equivalence<K> keyEquivalence;
        private final Equivalence<V> valueEquivalence;

        WithCustomEquivalences(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ,
                Equivalence<K> keyEquivalence, Equivalence<V> valueEquivalence) {
            super(hashConf, defaultExpectedSize, isNullKeyAllowed
            );
            this.keyEquivalence = keyEquivalence;
            this.valueEquivalence = valueEquivalence;
        }

        @Override
        @Nonnull
        public Equivalence<K> getKeyEquivalence() {
            return keyEquivalence;
        }

        @Override
        @Nonnull
        public Equivalence<V> getValueEquivalence() {
            return valueEquivalence;
        }

        @Override
        <K2 extends K, V2 extends V> MutableQHashParallelKVObjObjMapGO<K2, V2>
        uninitializedMutableMap() {
            MutableQHashParallelKVObjObjMap.WithCustomEquivalences<K2, V2> map =
                    new MutableQHashParallelKVObjObjMap.WithCustomEquivalences<K2, V2>();
            map.keyEquivalence = keyEquivalence;
            map.valueEquivalence = valueEquivalence;
            return map;
        }
        @Override
        <K2 extends K, V2 extends V> UpdatableQHashParallelKVObjObjMapGO<K2, V2>
        uninitializedUpdatableMap() {
            UpdatableQHashParallelKVObjObjMap.WithCustomEquivalences<K2, V2> map =
                    new UpdatableQHashParallelKVObjObjMap.WithCustomEquivalences<K2, V2>();
            map.keyEquivalence = keyEquivalence;
            map.valueEquivalence = valueEquivalence;
            return map;
        }
        @Override
        <K2 extends K, V2 extends V> ImmutableQHashParallelKVObjObjMapGO<K2, V2>
        uninitializedImmutableMap() {
            ImmutableQHashParallelKVObjObjMap.WithCustomEquivalences<K2, V2> map =
                    new ImmutableQHashParallelKVObjObjMap.WithCustomEquivalences<K2, V2>();
            map.keyEquivalence = keyEquivalence;
            map.valueEquivalence = valueEquivalence;
            return map;
        }

        @Override
        @Nonnull
        public HashObjObjMapFactory<K, V> withKeyEquivalence(
                @Nonnull Equivalence<? super K> keyEquivalence) {
            if (keyEquivalence.equals(Equivalence.defaultEquality()))
                return new WithCustomValueEquivalence<K, V>(getHashConfig(), getDefaultExpectedSize()
        , isNullKeyAllowed(),
                        valueEquivalence);
            if (keyEquivalence.equals(this.keyEquivalence)) {
                // noinspection unchecked
                return (HashObjObjMapFactory<K, V>) this;
            }
            return new WithCustomEquivalences<K, V>(getHashConfig(), getDefaultExpectedSize()
        , isNullKeyAllowed(),
                    (Equivalence<K>) keyEquivalence, valueEquivalence);
        }

        @Override
        @Nonnull
        public HashObjObjMapFactory<K, V> withValueEquivalence(
                @Nonnull Equivalence<? super V> valueEquivalence) {
            if (valueEquivalence.equals(Equivalence.defaultEquality()))
                return new WithCustomKeyEquivalence<K, V>(getHashConfig(), getDefaultExpectedSize()
        , isNullKeyAllowed(), keyEquivalence);
            if (valueEquivalence.equals(this.valueEquivalence)) {
                // noinspection unchecked
                return (HashObjObjMapFactory<K, V>) this;
            }
            return new WithCustomEquivalences<K, V>(getHashConfig(), getDefaultExpectedSize()
        , isNullKeyAllowed(),
                    keyEquivalence, (Equivalence<V>) valueEquivalence);
        }

        @Override
        HashObjObjMapFactory<K, V> thisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
            return new WithCustomEquivalences<K, V>(hashConf, defaultExpectedSize, isNullKeyAllowed
            ,
                    keyEquivalence, valueEquivalence);
        }

        @Override
        HashObjObjMapFactory<K, V> qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
            return new QHashParallelKVObjObjMapFactoryImpl.WithCustomEquivalences<K, V>(
                    hashConf, defaultExpectedSize, isNullKeyAllowed
            , keyEquivalence, valueEquivalence);
        }
        @Override
        HashObjObjMapFactory<K, V> lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            ) {
            return new LHashParallelKVObjObjMapFactoryImpl.WithCustomEquivalences<K, V>(
                    hashConf, defaultExpectedSize, isNullKeyAllowed
            , keyEquivalence, valueEquivalence);
        }
    }
}

