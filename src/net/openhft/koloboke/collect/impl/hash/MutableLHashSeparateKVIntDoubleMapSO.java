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
import net.openhft.koloboke.collect.map.hash.HashIntDoubleMap;
import javax.annotation.Nonnull;


public abstract class MutableLHashSeparateKVIntDoubleMapSO
        extends MutableLHashSeparateKVIntKeyMap
        implements HashIntDoubleMap, InternalIntDoubleMapOps, SeparateKVIntDoubleLHash {

    long[] values;

    void copy(SeparateKVIntDoubleLHash hash) {
        super.copy(hash);
        values = hash.valueArray().clone();
    }

    void move(SeparateKVIntDoubleLHash hash) {
        super.move(hash);
        values = hash.valueArray();
    }

    @Override
    @Nonnull
    public long[] valueArray() {
        return values;
    }

    
    int valueIndex(long value) {
        if (isEmpty())
            return -1;
        int index = -1;
        int mc = modCount();
        int free = freeValue;
        int[] keys = set;
        long[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            if (keys[i] != free) {
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

    
    boolean containsValue(long value) {
        return valueIndex(value) >= 0;
    }

    boolean removeValue(long value) {
        int index = valueIndex(value);
        if (index >= 0) {
            removeAt(index);
            return true;
        } else {
            return false;
        }
    }
    
    int valueIndex(double value) {
        if (isEmpty())
            return -1;
        long val = Double.doubleToLongBits(value);
        int index = -1;
        int mc = modCount();
        int free = freeValue;
        int[] keys = set;
        long[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            if (keys[i] != free) {
                if (val == vals[i]) {
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
    boolean containsValue(double value) {
        return valueIndex(value) >= 0;
    }

    boolean removeValue(double value) {
        int index = valueIndex(value);
        if (index >= 0) {
            removeAt(index);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(((Double) value).doubleValue());
    }

    int insert(int key, long value) {
        int free;
        if (key == (free = freeValue)) {
            free = changeFree();
        }
        int[] keys = set;
        int capacityMask, index;
        int cur;
        keyAbsent:
        if ((cur = keys[index = SeparateKVIntKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) != free) {
            if (cur == key) {
                // key is present
                return index;
            } else {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == free) {
                        break keyAbsent;
                    } else if (cur == key) {
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
    }


    @Override
    void allocateArrays(int capacity) {
        super.allocateArrays(capacity);
        values = new long[capacity];
    }
}

