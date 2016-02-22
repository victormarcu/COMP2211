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
import net.openhft.koloboke.collect.map.FloatObjMap;
import net.openhft.koloboke.collect.map.hash.HashFloatObjMapFactory;

import javax.annotation.Nonnull;
import java.util.Map;


public abstract class LHashSeparateKVFloatObjMapFactorySO<V>
        extends FloatLHashFactory 
        implements HashFloatObjMapFactory<V> {

    LHashSeparateKVFloatObjMapFactorySO(HashConfig hashConf, int defaultExpectedSize
            ) {
        super(hashConf, defaultExpectedSize);
    }

    

    

    


    <V2 extends V> MutableLHashSeparateKVFloatObjMapGO<V2> uninitializedMutableMap() {
        return new MutableLHashSeparateKVFloatObjMap<V2>();
    }
    <V2 extends V> UpdatableLHashSeparateKVFloatObjMapGO<V2> uninitializedUpdatableMap() {
        return new UpdatableLHashSeparateKVFloatObjMap<V2>();
    }
    <V2 extends V> ImmutableLHashSeparateKVFloatObjMapGO<V2> uninitializedImmutableMap() {
        return new ImmutableLHashSeparateKVFloatObjMap<V2>();
    }

    @Override
    @Nonnull
    public <V2 extends V> MutableLHashSeparateKVFloatObjMapGO<V2> newMutableMap(int expectedSize) {
        MutableLHashSeparateKVFloatObjMapGO<V2> map = uninitializedMutableMap();
        map.init(configWrapper, expectedSize);
        return map;
    }

    

    @Override
    @Nonnull
    public <V2 extends V> UpdatableLHashSeparateKVFloatObjMapGO<V2> newUpdatableMap(int expectedSize) {
        UpdatableLHashSeparateKVFloatObjMapGO<V2> map = uninitializedUpdatableMap();
        map.init(configWrapper, expectedSize);
        return map;
    }

    

    @Override
    @Nonnull
    public <V2 extends V> UpdatableLHashSeparateKVFloatObjMapGO<V2> newUpdatableMap(
            Map<Float, ? extends V2> map) {
        if (map instanceof FloatObjMap) {
            if (map instanceof SeparateKVFloatObjLHash) {
                SeparateKVFloatObjLHash hash = (SeparateKVFloatObjLHash) map;
                if (hash.hashConfig().equals(hashConf)) {
                    UpdatableLHashSeparateKVFloatObjMapGO<V2> res = uninitializedUpdatableMap();
                    res.copy(hash);
                    return res;
                }
            }
            UpdatableLHashSeparateKVFloatObjMapGO<V2> res = newUpdatableMap(map.size());
            res.putAll(map);
            return res;
        }
        UpdatableLHashSeparateKVFloatObjMapGO<V2> res = newUpdatableMap(map.size());
        for (Map.Entry<Float, ? extends V2> entry : map.entrySet()) {
            res.put(entry.getKey(), entry.getValue());
        }
        return res;
    }
}

