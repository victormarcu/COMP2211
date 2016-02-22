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

import net.openhft.koloboke.function.IntCharPredicate;
import net.openhft.koloboke.function.IntCharConsumer;
import net.openhft.koloboke.collect.map.IntCharMap;

import java.util.Map;


public final class CommonIntCharMapOps {

    public static boolean containsAllEntries(final InternalIntCharMapOps map,
            Map<?, ?> another) {
        if ( map == another )
            throw new IllegalArgumentException();
        if (another instanceof IntCharMap) {
            IntCharMap m2 = (IntCharMap) another;
                if (map.size() < m2.size())
                    return false;
                if (m2 instanceof InternalIntCharMapOps) {
                    //noinspection unchecked
                    return ((InternalIntCharMapOps) m2).allEntriesContainingIn(map);
                }
            return m2.forEachWhile(new
                   IntCharPredicate() {
                @Override
                public boolean test(int a, char b) {
                    return map.containsEntry(a, b);
                }
            });
        }
        for ( Map.Entry<?, ?> e : another.entrySet() ) {
            if ( !map.containsEntry((Integer) e.getKey(),
                    (Character) e.getValue()))
                return false;
        }
        return true;
    }

    public static  void putAll(final InternalIntCharMapOps map,
            Map<? extends Integer, ? extends Character> another) {
        if (map == another)
            throw new IllegalArgumentException();
        long maxPossibleSize = map.sizeAsLong() + Containers.sizeAsLong(another);
        map.ensureCapacity(maxPossibleSize);
        if (another instanceof IntCharMap) {
            if (another instanceof InternalIntCharMapOps) {
                ((InternalIntCharMapOps) another).reversePutAllTo(map);
            } else {
                ((IntCharMap) another).forEach(new IntCharConsumer() {
                    @Override
                    public void accept(int key, char value) {
                        map.justPut(key, value);
                    }
                });
            }
        } else {
            for (Map.Entry<? extends Integer, ? extends Character> e : another.entrySet()) {
                map.justPut(e.getKey(), e.getValue());
            }
        }
    }

    private CommonIntCharMapOps() {}
}

