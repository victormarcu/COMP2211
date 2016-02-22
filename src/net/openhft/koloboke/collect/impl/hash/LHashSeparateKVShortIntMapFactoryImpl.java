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


public final class LHashSeparateKVShortIntMapFactoryImpl
        extends LHashSeparateKVShortIntMapFactoryGO {

    

    

    
    
    

    
    
    

    /** For ServiceLoader */
    public LHashSeparateKVShortIntMapFactoryImpl() {
        this(HashConfig.getDefault(), 10
            , Short.MIN_VALUE, Short.MAX_VALUE);
    }

    

    

    

    LHashSeparateKVShortIntMapFactoryImpl(HashConfig hashConf, int defaultExpectedSize, short lower, short upper) {
        super(hashConf, defaultExpectedSize, lower, upper);
    }

    @Override
    HashShortIntMapFactory thisWith(HashConfig hashConf, int defaultExpectedSize, short lower, short upper) {
        return new LHashSeparateKVShortIntMapFactoryImpl(hashConf, defaultExpectedSize, lower, upper);
    }

    @Override
    HashShortIntMapFactory qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, short lower, short upper) {
        return new QHashSeparateKVShortIntMapFactoryImpl(hashConf, defaultExpectedSize, lower, upper);
    }
    @Override
    HashShortIntMapFactory lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, short lower, short upper) {
        return new LHashSeparateKVShortIntMapFactoryImpl(hashConf, defaultExpectedSize, lower, upper);
    }


    @Override
    @Nonnull
    public HashShortIntMapFactory withDefaultValue(int defaultValue) {
        if (defaultValue == 0)
            return this;
        return new WithCustomDefaultValue(getHashConfig(), getDefaultExpectedSize()
        
            , getLowerKeyDomainBound(), getUpperKeyDomainBound(), defaultValue);
    }


    static final class WithCustomDefaultValue
            extends LHashSeparateKVShortIntMapFactoryGO {
        private final int defaultValue;

        WithCustomDefaultValue(HashConfig hashConf, int defaultExpectedSize, short lower, short upper, int defaultValue) {
            super(hashConf, defaultExpectedSize, lower, upper);
            this.defaultValue = defaultValue;
        }

        @Override
        public int getDefaultValue() {
            return defaultValue;
        }

        @Override
         MutableLHashSeparateKVShortIntMapGO uninitializedMutableMap() {
            MutableLHashSeparateKVShortIntMap.WithCustomDefaultValue map =
                    new MutableLHashSeparateKVShortIntMap.WithCustomDefaultValue();
            map.defaultValue = defaultValue;
            return map;
        }
        @Override
         UpdatableLHashSeparateKVShortIntMapGO uninitializedUpdatableMap() {
            UpdatableLHashSeparateKVShortIntMap.WithCustomDefaultValue map =
                    new UpdatableLHashSeparateKVShortIntMap.WithCustomDefaultValue();
            map.defaultValue = defaultValue;
            return map;
        }
        @Override
         ImmutableLHashSeparateKVShortIntMapGO uninitializedImmutableMap() {
            ImmutableLHashSeparateKVShortIntMap.WithCustomDefaultValue map =
                    new ImmutableLHashSeparateKVShortIntMap.WithCustomDefaultValue();
            map.defaultValue = defaultValue;
            return map;
        }


        @Override
        @Nonnull
        public HashShortIntMapFactory withDefaultValue(int defaultValue) {
            if (defaultValue == 0)
                return new LHashSeparateKVShortIntMapFactoryImpl(getHashConfig(), getDefaultExpectedSize()
        
            , getLowerKeyDomainBound(), getUpperKeyDomainBound());
            if (defaultValue == this.defaultValue)
                return this;
            return new WithCustomDefaultValue(getHashConfig(), getDefaultExpectedSize()
        
            , getLowerKeyDomainBound(), getUpperKeyDomainBound(), defaultValue);
        }

        @Override
        HashShortIntMapFactory thisWith(HashConfig hashConf, int defaultExpectedSize, short lower, short upper) {
            return new WithCustomDefaultValue(hashConf, defaultExpectedSize, lower, upper, defaultValue);
        }

        @Override
        HashShortIntMapFactory qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, short lower, short upper) {
            return new QHashSeparateKVShortIntMapFactoryImpl.WithCustomDefaultValue(
                    hashConf, defaultExpectedSize, lower, upper, defaultValue);
        }
        @Override
        HashShortIntMapFactory lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, short lower, short upper) {
            return new LHashSeparateKVShortIntMapFactoryImpl.WithCustomDefaultValue(
                    hashConf, defaultExpectedSize, lower, upper, defaultValue);
        }
    }

}

