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


public final class LHashSeparateKVFloatObjMapFactoryImpl<V>
        extends LHashSeparateKVFloatObjMapFactoryGO<V> {

    

    

    
    
    

    
    
    

    /** For ServiceLoader */
    public LHashSeparateKVFloatObjMapFactoryImpl() {
        this(HashConfig.getDefault(), 10
            );
    }

    

    

    

    LHashSeparateKVFloatObjMapFactoryImpl(HashConfig hashConf, int defaultExpectedSize) {
        super(hashConf, defaultExpectedSize);
    }

    @Override
    HashFloatObjMapFactory<V> thisWith(HashConfig hashConf, int defaultExpectedSize) {
        return new LHashSeparateKVFloatObjMapFactoryImpl<V>(hashConf, defaultExpectedSize);
    }

    @Override
    HashFloatObjMapFactory<V> qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize) {
        return new QHashSeparateKVFloatObjMapFactoryImpl<V>(hashConf, defaultExpectedSize);
    }
    @Override
    HashFloatObjMapFactory<V> lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize) {
        return new LHashSeparateKVFloatObjMapFactoryImpl<V>(hashConf, defaultExpectedSize);
    }


    @Override
    @Nonnull
    public HashFloatObjMapFactory<V> withValueEquivalence(
            @Nonnull Equivalence<? super V> valueEquivalence) {
        if (valueEquivalence.equals(Equivalence.defaultEquality())) {
            // noinspection unchecked
            return (HashFloatObjMapFactory<V>) this;
        }
        return new WithCustomValueEquivalence<V>(getHashConfig(), getDefaultExpectedSize()
        ,
                (Equivalence<V>) valueEquivalence);
    }


    static final class WithCustomValueEquivalence<V>
            extends LHashSeparateKVFloatObjMapFactoryGO<V> {

        private final Equivalence<V> valueEquivalence;
        WithCustomValueEquivalence(HashConfig hashConf, int defaultExpectedSize,
                Equivalence<V> valueEquivalence) {
            super(hashConf, defaultExpectedSize);
            this.valueEquivalence = valueEquivalence;
        }

        @Override
        @Nonnull
        public Equivalence<V> getValueEquivalence() {
            return valueEquivalence;
        }

        @Override
        <V2 extends V> MutableLHashSeparateKVFloatObjMapGO<V2>
        uninitializedMutableMap() {
            MutableLHashSeparateKVFloatObjMap.WithCustomValueEquivalence<V2> map =
                    new MutableLHashSeparateKVFloatObjMap.WithCustomValueEquivalence<V2>();
            map.valueEquivalence = valueEquivalence;
            return map;
        }
        @Override
        <V2 extends V> UpdatableLHashSeparateKVFloatObjMapGO<V2>
        uninitializedUpdatableMap() {
            UpdatableLHashSeparateKVFloatObjMap.WithCustomValueEquivalence<V2> map =
                    new UpdatableLHashSeparateKVFloatObjMap.WithCustomValueEquivalence<V2>();
            map.valueEquivalence = valueEquivalence;
            return map;
        }
        @Override
        <V2 extends V> ImmutableLHashSeparateKVFloatObjMapGO<V2>
        uninitializedImmutableMap() {
            ImmutableLHashSeparateKVFloatObjMap.WithCustomValueEquivalence<V2> map =
                    new ImmutableLHashSeparateKVFloatObjMap.WithCustomValueEquivalence<V2>();
            map.valueEquivalence = valueEquivalence;
            return map;
        }


        @Override
        @Nonnull
        public HashFloatObjMapFactory<V> withValueEquivalence(
                @Nonnull Equivalence<? super V> valueEquivalence) {
            if (valueEquivalence.equals(Equivalence.defaultEquality()))
                return new LHashSeparateKVFloatObjMapFactoryImpl<V>(getHashConfig(), getDefaultExpectedSize()
        );
            if (valueEquivalence.equals(this.valueEquivalence))
                // noinspection unchecked
                return (HashFloatObjMapFactory<V>) this;
            return new WithCustomValueEquivalence<V>(getHashConfig(), getDefaultExpectedSize()
        ,
                    (Equivalence<V>) valueEquivalence);
        }

        @Override
        HashFloatObjMapFactory<V> thisWith(HashConfig hashConf, int defaultExpectedSize) {
            return new WithCustomValueEquivalence<V>(hashConf, defaultExpectedSize,
                    valueEquivalence);
        }

        @Override
        HashFloatObjMapFactory<V> qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize) {
            return new QHashSeparateKVFloatObjMapFactoryImpl.WithCustomValueEquivalence<V>(
                    hashConf, defaultExpectedSize, valueEquivalence);
        }
        @Override
        HashFloatObjMapFactory<V> lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize) {
            return new LHashSeparateKVFloatObjMapFactoryImpl.WithCustomValueEquivalence<V>(
                    hashConf, defaultExpectedSize, valueEquivalence);
        }
    }

}

