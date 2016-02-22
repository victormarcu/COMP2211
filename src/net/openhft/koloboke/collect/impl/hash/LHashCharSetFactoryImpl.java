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

import net.openhft.koloboke.collect.hash.*;
import net.openhft.koloboke.collect.set.hash.HashCharSetFactory;


public final class LHashCharSetFactoryImpl extends LHashCharSetFactoryGO {

    /** For ServiceLoader */
    public LHashCharSetFactoryImpl() {
        this(HashConfig.getDefault(), 10
                , Character.MIN_VALUE, Character.MAX_VALUE);
    }

    

    

    public LHashCharSetFactoryImpl(HashConfig hashConf, int defaultExpectedSize
        , char lower, char upper) {
        super(hashConf, defaultExpectedSize
        , lower, upper);
    }

    @Override
    HashCharSetFactory thisWith(HashConfig hashConf, int defaultExpectedSize
        , char lower, char upper) {
        return new LHashCharSetFactoryImpl(hashConf, defaultExpectedSize
        , lower, upper);
    }

    @Override
    HashCharSetFactory qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize
        , char lower, char upper) {
        return new QHashCharSetFactoryImpl(hashConf, defaultExpectedSize
        , lower, upper);
    }
    @Override
    HashCharSetFactory lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize
        , char lower, char upper) {
        return new LHashCharSetFactoryImpl(hashConf, defaultExpectedSize
        , lower, upper);
    }

    HashCharSetFactory withDomain(char lower, char upper) {
        if (lower == getLowerKeyDomainBound() && upper == getUpperKeyDomainBound())
            return this;
        return new LHashCharSetFactoryImpl(getHashConfig(), getDefaultExpectedSize(), lower, upper);
    }

    @Override
    public HashCharSetFactory withKeysDomain(char lower, char upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minPossibleKey shouldn't be greater " +
                    "than maxPossibleKey");
        return withDomain(lower, upper);
    }

    @Override
    public HashCharSetFactory withKeysDomainComplement(char lower, char upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minImpossibleKey shouldn't be greater " +
                    "than maxImpossibleKey");
        return withDomain((char) (upper + 1), (char) (lower - 1));
    }
}

