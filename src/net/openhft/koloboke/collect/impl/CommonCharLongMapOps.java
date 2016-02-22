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

import net.openhft.koloboke.function.CharLongPredicate;
import net.openhft.koloboke.function.CharLongConsumer;
import net.openhft.koloboke.collect.map.CharLongMap;

import java.util.Map;


public final class CommonCharLongMapOps {

    public static boolean containsAllEntries(final InternalCharLongMapOps map,
            Map<?, ?> another) {
        if ( map == another )
            throw new IllegalArgumentException();
        if (another instanceof CharLongMap) {
            CharLongMap m2 = (CharLongMap) another;
                if (map.size() < m2.size())
                    return false;
                if (m2 instanceof InternalCharLongMapOps) {
                    //noinspection unchecked
                    return ((InternalCharLongMapOps) m2).allEntriesContainingIn(map);
                }
            return m2.forEachWhile(new
                   CharLongPredicate() {
                @Override
                public boolean test(char a, long b) {
                    return map.containsEntry(a, b);
                }
            });
        }
        for ( Map.Entry<?, ?> e : another.entrySet() ) {
            if ( !map.containsEntry((Character) e.getKey(),
                    (Long) e.getValue()))
                return false;
        }
        return true;
    }

    public static  void putAll(final InternalCharLongMapOps map,
            Map<? extends Character, ? extends Long> another) {
        if (map == another)
            throw new IllegalArgumentException();
        long maxPossibleSize = map.sizeAsLong() + Containers.sizeAsLong(another);
        map.ensureCapacity(maxPossibleSize);
        if (another instanceof CharLongMap) {
            if (another instanceof InternalCharLongMapOps) {
                ((InternalCharLongMapOps) another).reversePutAllTo(map);
            } else {
                ((CharLongMap) another).forEach(new CharLongConsumer() {
                    @Override
                    public void accept(char key, long value) {
                        map.justPut(key, value);
                    }
                });
            }
        } else {
            for (Map.Entry<? extends Character, ? extends Long> e : another.entrySet()) {
                map.justPut(e.getKey(), e.getValue());
            }
        }
    }

    private CommonCharLongMapOps() {}
}

