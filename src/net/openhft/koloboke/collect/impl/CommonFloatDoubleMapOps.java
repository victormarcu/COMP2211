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

import net.openhft.koloboke.function.FloatDoublePredicate;
import net.openhft.koloboke.function.FloatDoubleConsumer;
import net.openhft.koloboke.collect.map.FloatDoubleMap;

import java.util.Map;


public final class CommonFloatDoubleMapOps {

    public static boolean containsAllEntries(final InternalFloatDoubleMapOps map,
            Map<?, ?> another) {
        if ( map == another )
            throw new IllegalArgumentException();
        if (another instanceof FloatDoubleMap) {
            FloatDoubleMap m2 = (FloatDoubleMap) another;
                if (map.size() < m2.size())
                    return false;
                if (m2 instanceof InternalFloatDoubleMapOps) {
                    //noinspection unchecked
                    return ((InternalFloatDoubleMapOps) m2).allEntriesContainingIn(map);
                }
            return m2.forEachWhile(new
                   FloatDoublePredicate() {
                @Override
                public boolean test(float a, double b) {
                    return map.containsEntry(a, b);
                }
            });
        }
        for ( Map.Entry<?, ?> e : another.entrySet() ) {
            if ( !map.containsEntry((Float) e.getKey(),
                    (Double) e.getValue()))
                return false;
        }
        return true;
    }

    public static  void putAll(final InternalFloatDoubleMapOps map,
            Map<? extends Float, ? extends Double> another) {
        if (map == another)
            throw new IllegalArgumentException();
        long maxPossibleSize = map.sizeAsLong() + Containers.sizeAsLong(another);
        map.ensureCapacity(maxPossibleSize);
        if (another instanceof FloatDoubleMap) {
            if (another instanceof InternalFloatDoubleMapOps) {
                ((InternalFloatDoubleMapOps) another).reversePutAllTo(map);
            } else {
                ((FloatDoubleMap) another).forEach(new FloatDoubleConsumer() {
                    @Override
                    public void accept(float key, double value) {
                        map.justPut(key, value);
                    }
                });
            }
        } else {
            for (Map.Entry<? extends Float, ? extends Double> e : another.entrySet()) {
                map.justPut(e.getKey(), e.getValue());
            }
        }
    }

    private CommonFloatDoubleMapOps() {}
}

