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


public final class LHashSeparateKVIntCharMapFactoryImpl
        extends LHashSeparateKVIntCharMapFactoryGO {

    

    

    
    
    

    
    
    

    /** For ServiceLoader */
    public LHashSeparateKVIntCharMapFactoryImpl() {
        this(HashConfig.getDefault(), 10
            , Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    

    

    

    LHashSeparateKVIntCharMapFactoryImpl(HashConfig hashConf, int defaultExpectedSize, int lower, int upper) {
        super(hashConf, defaultExpectedSize, lower, upper);
    }

    @Override
    HashIntCharMapFactory thisWith(HashConfig hashConf, int defaultExpectedSize, int lower, int upper) {
        return new LHashSeparateKVIntCharMapFactoryImpl(hashConf, defaultExpectedSize, lower, upper);
    }

    @Override
    HashIntCharMapFactory qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, int lower, int upper) {
        return new QHashSeparateKVIntCharMapFactoryImpl(hashConf, defaultExpectedSize, lower, upper);
    }
    @Override
    HashIntCharMapFactory lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, int lower, int upper) {
        return new LHashSeparateKVIntCharMapFactoryImpl(hashConf, defaultExpectedSize, lower, upper);
    }


    @Override
    @Nonnull
    public HashIntCharMapFactory withDefaultValue(char defaultValue) {
        if (defaultValue == (char) 0)
            return this;
        return new WithCustomDefaultValue(getHashConfig(), getDefaultExpectedSize()
        
            , getLowerKeyDomainBound(), getUpperKeyDomainBound(), defaultValue);
    }


    static final class WithCustomDefaultValue
            extends LHashSeparateKVIntCharMapFactoryGO {
        private final char defaultValue;

        WithCustomDefaultValue(HashConfig hashConf, int defaultExpectedSize, int lower, int upper, char defaultValue) {
            super(hashConf, defaultExpectedSize, lower, upper);
            this.defaultValue = defaultValue;
        }

        @Override
        public char getDefaultValue() {
            return defaultValue;
        }

        @Override
         MutableLHashSeparateKVIntCharMapGO uninitializedMutableMap() {
            MutableLHashSeparateKVIntCharMap.WithCustomDefaultValue map =
                    new MutableLHashSeparateKVIntCharMap.WithCustomDefaultValue();
            map.defaultValue = defaultValue;
            return map;
        }
        @Override
         UpdatableLHashSeparateKVIntCharMapGO uninitializedUpdatableMap() {
            UpdatableLHashSeparateKVIntCharMap.WithCustomDefaultValue map =
                    new UpdatableLHashSeparateKVIntCharMap.WithCustomDefaultValue();
            map.defaultValue = defaultValue;
            return map;
        }
        @Override
         ImmutableLHashSeparateKVIntCharMapGO uninitializedImmutableMap() {
            ImmutableLHashSeparateKVIntCharMap.WithCustomDefaultValue map =
                    new ImmutableLHashSeparateKVIntCharMap.WithCustomDefaultValue();
            map.defaultValue = defaultValue;
            return map;
        }


        @Override
        @Nonnull
        public HashIntCharMapFactory withDefaultValue(char defaultValue) {
            if (defaultValue == (char) 0)
                return new LHashSeparateKVIntCharMapFactoryImpl(getHashConfig(), getDefaultExpectedSize()
        
            , getLowerKeyDomainBound(), getUpperKeyDomainBound());
            if (defaultValue == this.defaultValue)
                return this;
            return new WithCustomDefaultValue(getHashConfig(), getDefaultExpectedSize()
        
            , getLowerKeyDomainBound(), getUpperKeyDomainBound(), defaultValue);
        }

        @Override
        HashIntCharMapFactory thisWith(HashConfig hashConf, int defaultExpectedSize, int lower, int upper) {
            return new WithCustomDefaultValue(hashConf, defaultExpectedSize, lower, upper, defaultValue);
        }

        @Override
        HashIntCharMapFactory qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, int lower, int upper) {
            return new QHashSeparateKVIntCharMapFactoryImpl.WithCustomDefaultValue(
                    hashConf, defaultExpectedSize, lower, upper, defaultValue);
        }
        @Override
        HashIntCharMapFactory lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, int lower, int upper) {
            return new LHashSeparateKVIntCharMapFactoryImpl.WithCustomDefaultValue(
                    hashConf, defaultExpectedSize, lower, upper, defaultValue);
        }
    }

}

