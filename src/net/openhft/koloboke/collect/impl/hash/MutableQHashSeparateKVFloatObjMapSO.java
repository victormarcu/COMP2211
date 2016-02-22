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
import net.openhft.koloboke.collect.map.hash.HashFloatObjMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.*;


public abstract class MutableQHashSeparateKVFloatObjMapSO<V>
        extends MutableQHashSeparateKVFloatKeyMap
        implements HashFloatObjMap<V>,
        InternalFloatObjMapOps<V>, SeparateKVFloatObjQHash {

    V[] values;

    void copy(SeparateKVFloatObjQHash hash) {
        super.copy(hash);
        // noinspection unchecked
        values = (V[]) hash.valueArray().clone();
    }

    void move(SeparateKVFloatObjQHash hash) {
        super.move(hash);
        // noinspection unchecked
        values = (V[]) hash.valueArray();
    }

    @Override
    @Nonnull
    public Object[] valueArray() {
        return values;
    }

    boolean nullableValueEquals(@Nullable V a, @Nullable V b) {
        return a == b || (a != null && a.equals(b));
    }

    boolean valueEquals(@Nonnull V a, @Nullable V b) {
        return a.equals(b);
    }

    int nullableValueHashCode(@Nullable V value) {
        return value != null ? value.hashCode() : 0;
    }

    int valueHashCode(@Nonnull V value) {
        return value.hashCode();
    }


    int valueIndex(@Nullable Object value) {
        if (value == null)
            return nullValueIndex();
        if (isEmpty())
            return -1;
        V val = (V) value;
        int index = -1;
        int mc = modCount();
        int[] keys = set;
        V[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            if (keys[i] < FREE_BITS) {
                if (valueEquals(val, vals[i])) {
                    index = i;
                    break;
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return index;
    }

    private int nullValueIndex() {
        if (isEmpty())
            return -1;
        int index = -1;
        int mc = modCount();
        int[] keys = set;
        V[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            if (keys[i] < FREE_BITS) {
                if (vals[i] == null) {
                    index = i;
                    break;
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return index;
    }

    @Override
    public boolean containsValue(Object value) {
        return valueIndex(value) >= 0;
    }

    boolean removeValue(@Nullable Object value) {
        int index = valueIndex(value);
        if (index >= 0) {
            removeAt(index);
            return true;
        } else {
            return false;
        }
    }


    int insert(int key, V value) {
        int[] keys = set;
        int capacity, index;
        int cur;
        keyAbsentFreeSlot:
        if ((cur = keys[index = SeparateKVFloatKeyMixing.mix(key) % (capacity = keys.length)]) != FREE_BITS) {
            if (cur == key) {
                // key is present
                return index;
            } else {
                int firstRemoved;
                if (cur <= FREE_BITS) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = keys[bIndex]) == FREE_BITS) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else if (cur == key) {
                                // key is present
                                return bIndex;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = keys[fIndex]) == FREE_BITS) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else if (cur == key) {
                                // key is present
                                return fIndex;
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = keys[bIndex]) == FREE_BITS) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == key) {
                            // key is present
                            return bIndex;
                        } else if (cur > FREE_BITS && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = keys[fIndex]) == FREE_BITS) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == key) {
                            // key is present
                            return fIndex;
                        } else if (cur > FREE_BITS && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 2;
                    }
                }
                // key is absent, removed slot
                incrementModCount();
                keys[firstRemoved] = key;
                values[firstRemoved] = value;
                postRemovedSlotInsertHook();
                return -1;
            }
        }
        // key is absent, free slot
        incrementModCount();
        keys[index] = key;
        values[index] = value;
        postFreeSlotInsertHook();
        return -1;
    }


    @Override
    void allocateArrays(int capacity) {
        super.allocateArrays(capacity);
        // noinspection unchecked
        values = (V[]) new Object[capacity];
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(values, null);
    }
}

