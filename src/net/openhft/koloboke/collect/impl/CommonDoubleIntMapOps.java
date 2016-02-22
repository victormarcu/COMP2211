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

import net.openhft.koloboke.function.DoubleIntPredicate;
import net.openhft.koloboke.function.DoubleIntConsumer;
import net.openhft.koloboke.collect.map.DoubleIntMap;

import java.util.Map;


public final class CommonDoubleIntMapOps {

    public static boolean containsAllEntries(final InternalDoubleIntMapOps map,
            Map<?, ?> another) {
        if ( map == another )
            throw new IllegalArgumentException();
        if (another instanceof DoubleIntMap) {
            DoubleIntMap m2 = (DoubleIntMap) another;
                if (map.size() < m2.size())
                    return false;
                if (m2 instanceof InternalDoubleIntMapOps) {
                    //noinspection unchecked
                    return ((InternalDoubleIntMapOps) m2).allEntriesContainingIn(map);
                }
            return m2.forEachWhile(new
                   DoubleIntPredicate() {
                @Override
                public boolean test(double a, int b) {
                    return map.containsEntry(a, b);
                }
            });
        }
        for ( Map.Entry<?, ?> e : another.entrySet() ) {
            if ( !map.containsEntry((Double) e.getKey(),
                    (Integer) e.getValue()))
                return false;
        }
        return true;
    }

    public static  void putAll(final InternalDoubleIntMapOps map,
            Map<? extends Double, ? extends Integer> another) {
        if (map == another)
            throw new IllegalArgumentException();
        long maxPossibleSize = map.sizeAsLong() + Containers.sizeAsLong(another);
        map.ensureCapacity(maxPossibleSize);
        if (another instanceof DoubleIntMap) {
            if (another instanceof InternalDoubleIntMapOps) {
                ((InternalDoubleIntMapOps) another).reversePutAllTo(map);
            } else {
                ((DoubleIntMap) another).forEach(new DoubleIntConsumer() {
                    @Override
                    public void accept(double key, int value) {
                        map.justPut(key, value);
                    }
                });
            }
        } else {
            for (Map.Entry<? extends Double, ? extends Integer> e : another.entrySet()) {
                map.justPut(e.getKey(), e.getValue());
            }
        }
    }

    private CommonDoubleIntMapOps() {}
}

