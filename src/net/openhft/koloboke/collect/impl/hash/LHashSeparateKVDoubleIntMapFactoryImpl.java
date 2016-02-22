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


public final class LHashSeparateKVDoubleIntMapFactoryImpl
        extends LHashSeparateKVDoubleIntMapFactoryGO {

    

    

    
    
    

    
    
    

    /** For ServiceLoader */
    public LHashSeparateKVDoubleIntMapFactoryImpl() {
        this(HashConfig.getDefault(), 10
            );
    }

    

    

    

    LHashSeparateKVDoubleIntMapFactoryImpl(HashConfig hashConf, int defaultExpectedSize) {
        super(hashConf, defaultExpectedSize);
    }

    @Override
    HashDoubleIntMapFactory thisWith(HashConfig hashConf, int defaultExpectedSize) {
        return new LHashSeparateKVDoubleIntMapFactoryImpl(hashConf, defaultExpectedSize);
    }

    @Override
    HashDoubleIntMapFactory qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize) {
        return new QHashSeparateKVDoubleIntMapFactoryImpl(hashConf, defaultExpectedSize);
    }
    @Override
    HashDoubleIntMapFactory lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize) {
        return new LHashSeparateKVDoubleIntMapFactoryImpl(hashConf, defaultExpectedSize);
    }


    @Override
    @Nonnull
    public HashDoubleIntMapFactory withDefaultValue(int defaultValue) {
        if (defaultValue == 0)
            return this;
        return new WithCustomDefaultValue(getHashConfig(), getDefaultExpectedSize()
        , defaultValue);
    }


    static final class WithCustomDefaultValue
            extends LHashSeparateKVDoubleIntMapFactoryGO {
        private final int defaultValue;

        WithCustomDefaultValue(HashConfig hashConf, int defaultExpectedSize, int defaultValue) {
            super(hashConf, defaultExpectedSize);
            this.defaultValue = defaultValue;
        }

        @Override
        public int getDefaultValue() {
            return defaultValue;
        }

        @Override
         MutableLHashSeparateKVDoubleIntMapGO uninitializedMutableMap() {
            MutableLHashSeparateKVDoubleIntMap.WithCustomDefaultValue map =
                    new MutableLHashSeparateKVDoubleIntMap.WithCustomDefaultValue();
            map.defaultValue = defaultValue;
            return map;
        }
        @Override
         UpdatableLHashSeparateKVDoubleIntMapGO uninitializedUpdatableMap() {
            UpdatableLHashSeparateKVDoubleIntMap.WithCustomDefaultValue map =
                    new UpdatableLHashSeparateKVDoubleIntMap.WithCustomDefaultValue();
            map.defaultValue = defaultValue;
            return map;
        }
        @Override
         ImmutableLHashSeparateKVDoubleIntMapGO uninitializedImmutableMap() {
            ImmutableLHashSeparateKVDoubleIntMap.WithCustomDefaultValue map =
                    new ImmutableLHashSeparateKVDoubleIntMap.WithCustomDefaultValue();
            map.defaultValue = defaultValue;
            return map;
        }


        @Override
        @Nonnull
        public HashDoubleIntMapFactory withDefaultValue(int defaultValue) {
            if (defaultValue == 0)
                return new LHashSeparateKVDoubleIntMapFactoryImpl(getHashConfig(), getDefaultExpectedSize()
        );
            if (defaultValue == this.defaultValue)
                return this;
            return new WithCustomDefaultValue(getHashConfig(), getDefaultExpectedSize()
        , defaultValue);
        }

        @Override
        HashDoubleIntMapFactory thisWith(HashConfig hashConf, int defaultExpectedSize) {
            return new WithCustomDefaultValue(hashConf, defaultExpectedSize, defaultValue);
        }

        @Override
        HashDoubleIntMapFactory qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize) {
            return new QHashSeparateKVDoubleIntMapFactoryImpl.WithCustomDefaultValue(
                    hashConf, defaultExpectedSize, defaultValue);
        }
        @Override
        HashDoubleIntMapFactory lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize) {
            return new LHashSeparateKVDoubleIntMapFactoryImpl.WithCustomDefaultValue(
                    hashConf, defaultExpectedSize, defaultValue);
        }
    }

}

