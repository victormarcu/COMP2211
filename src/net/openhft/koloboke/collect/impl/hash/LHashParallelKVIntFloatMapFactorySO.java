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
import net.openhft.koloboke.collect.map.IntFloatMap;
import net.openhft.koloboke.collect.map.hash.HashIntFloatMapFactory;

import javax.annotation.Nonnull;
import java.util.Map;


public abstract class LHashParallelKVIntFloatMapFactorySO
        extends IntegerLHashFactory 
        implements HashIntFloatMapFactory {

    LHashParallelKVIntFloatMapFactorySO(HashConfig hashConf, int defaultExpectedSize
            , int lower, int upper) {
        super(hashConf, defaultExpectedSize, lower, upper);
    }

    

    

    


     MutableLHashParallelKVIntFloatMapGO uninitializedMutableMap() {
        return new MutableLHashParallelKVIntFloatMap();
    }
     UpdatableLHashParallelKVIntFloatMapGO uninitializedUpdatableMap() {
        return new UpdatableLHashParallelKVIntFloatMap();
    }
     ImmutableLHashParallelKVIntFloatMapGO uninitializedImmutableMap() {
        return new ImmutableLHashParallelKVIntFloatMap();
    }

    @Override
    @Nonnull
    public  MutableLHashParallelKVIntFloatMapGO newMutableMap(int expectedSize) {
        MutableLHashParallelKVIntFloatMapGO map = uninitializedMutableMap();
        map.init(configWrapper, expectedSize, getFree());
        return map;
    }

    

    @Override
    @Nonnull
    public  UpdatableLHashParallelKVIntFloatMapGO newUpdatableMap(int expectedSize) {
        UpdatableLHashParallelKVIntFloatMapGO map = uninitializedUpdatableMap();
        map.init(configWrapper, expectedSize, getFree());
        return map;
    }

    

    @Override
    @Nonnull
    public  UpdatableLHashParallelKVIntFloatMapGO newUpdatableMap(
            Map<Integer, Float> map) {
        if (map instanceof IntFloatMap) {
            if (map instanceof ParallelKVIntFloatLHash) {
                ParallelKVIntFloatLHash hash = (ParallelKVIntFloatLHash) map;
                if (hash.hashConfig().equals(hashConf)) {
                    UpdatableLHashParallelKVIntFloatMapGO res = uninitializedUpdatableMap();
                    res.copy(hash);
                    return res;
                }
            }
            UpdatableLHashParallelKVIntFloatMapGO res = newUpdatableMap(map.size());
            res.putAll(map);
            return res;
        }
        UpdatableLHashParallelKVIntFloatMapGO res = newUpdatableMap(map.size());
        for (Map.Entry<Integer, Float> entry : map.entrySet()) {
            res.put(entry.getKey(), entry.getValue());
        }
        return res;
    }
}

