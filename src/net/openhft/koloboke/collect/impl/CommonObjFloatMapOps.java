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

package net.openhft.koloboke.collect.impl;

import net.openhft.koloboke.function.ObjFloatPredicate;
import net.openhft.koloboke.function.ObjFloatConsumer;
import net.openhft.koloboke.collect.map.ObjFloatMap;

import java.util.Map;


public final class CommonObjFloatMapOps {

    public static boolean containsAllEntries(final InternalObjFloatMapOps<?> map,
            Map<?, ?> another) {
        if ( map == another )
            throw new IllegalArgumentException();
        if (another instanceof ObjFloatMap) {
            ObjFloatMap m2 = (ObjFloatMap) another;
            if (
                    m2.keyEquivalence().equals(map.keyEquivalence())
                
            ) {
                if (map.size() < m2.size())
                    return false;
                if (m2 instanceof InternalObjFloatMapOps) {
                    //noinspection unchecked
                    return ((InternalObjFloatMapOps) m2).allEntriesContainingIn(map);
                }
            }
            // noinspection unchecked
            return m2.forEachWhile(new
                   ObjFloatPredicate() {
                @Override
                public boolean test(Object a, float b) {
                    return map.containsEntry(a, b);
                }
            });
        }
        for ( Map.Entry<?, ?> e : another.entrySet() ) {
            if ( !map.containsEntry(e.getKey(),
                    (Float) e.getValue()))
                return false;
        }
        return true;
    }

    public static <K> void putAll(final InternalObjFloatMapOps<K> map,
            Map<? extends K, ? extends Float> another) {
        if (map == another)
            throw new IllegalArgumentException();
        long maxPossibleSize = map.sizeAsLong() + Containers.sizeAsLong(another);
        map.ensureCapacity(maxPossibleSize);
        if (another instanceof ObjFloatMap) {
            if (another instanceof InternalObjFloatMapOps) {
                ((InternalObjFloatMapOps) another).reversePutAllTo(map);
            } else {
                ((ObjFloatMap) another).forEach(new ObjFloatConsumer<K>() {
                    @Override
                    public void accept(K key, float value) {
                        map.justPut(key, value);
                    }
                });
            }
        } else {
            for (Map.Entry<? extends K, ? extends Float> e : another.entrySet()) {
                map.justPut(e.getKey(), e.getValue());
            }
        }
    }

    private CommonObjFloatMapOps() {}
}

