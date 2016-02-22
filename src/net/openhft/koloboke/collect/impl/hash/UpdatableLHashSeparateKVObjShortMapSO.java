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

import net.openhft.koloboke.collect.impl.*;
import net.openhft.koloboke.collect.map.hash.HashObjShortMap;
import javax.annotation.Nonnull;


public abstract class UpdatableLHashSeparateKVObjShortMapSO<K>
        extends UpdatableLHashSeparateKVObjKeyMap<K>
        implements HashObjShortMap<K>, InternalObjShortMapOps<K>, SeparateKVObjShortLHash {

    short[] values;

    void copy(SeparateKVObjShortLHash hash) {
        super.copy(hash);
        values = hash.valueArray().clone();
    }

    void move(SeparateKVObjShortLHash hash) {
        super.move(hash);
        values = hash.valueArray();
    }

    @Override
    @Nonnull
    public short[] valueArray() {
        return values;
    }

    
    int valueIndex(short value) {
        if (isEmpty())
            return -1;
        int index = -1;
        int mc = modCount();
        Object[] keys = set;
        short[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            if (keys[i] != FREE) {
                if (value == vals[i]) {
                    index = i;
                    break;
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return index;
    }

    @Override public
    boolean containsValue(short value) {
        return valueIndex(value) >= 0;
    }

    boolean removeValue(short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(((Short) value).shortValue());
    }

    int insert(K key, short value) {
        if (key != null) {
            // noinspection unchecked
            K[] keys = (K[]) set;
            int capacityMask, index;
            K cur;
            keyAbsent:
            if ((cur = keys[index = SeparateKVObjKeyMixing.mix(keyHashCode(key)) & (capacityMask = keys.length - 1)]) != FREE) {
                if (cur == key || keyEquals(key, cur)) {
                    // key is present
                    return index;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == FREE) {
                            break keyAbsent;
                        } else if (cur == key || (keyEquals(key, cur))) {
                            // key is present
                            return index;
                        }
                    }
                }
            }
            // key is absent
            incrementModCount();
            keys[index] = key;
            values[index] = value;
            postInsertHook();
            return -1;
        } else {
            return insertNullKey(value);
        }
    }

    int insertNullKey(short value) {
        // noinspection unchecked
        K[] keys = (K[]) set;
        int capacityMask, index;
        K cur;
        keyAbsent:
        if ((cur = keys[index = 0]) != FREE) {
            if (cur == null) {
                // key is present
                return index;
            } else {
                capacityMask = keys.length - 1;
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == FREE) {
                        break keyAbsent;
                    } else if (cur == null) {
                        // key is present
                        return index;
                    }
                }
            }
        }
        // key is absent
        incrementModCount();
        keys[index] = null;
        values[index] = value;
        postInsertHook();
        return -1;
    }

    @Override
    void allocateArrays(int capacity) {
        super.allocateArrays(capacity);
        values = new short[capacity];
    }
}

