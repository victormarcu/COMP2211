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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.*;


public abstract class MutableSeparateKVObjQHashSO<E> extends MutableQHash
        implements SeparateKVObjQHash, QHash {

    Object[] set;

    void copy(SeparateKVObjQHash hash) {
        super.copy(hash);
        set = hash.keys().clone();
    }

    void move(SeparateKVObjQHash hash) {
        super.copy(hash);
        set = hash.keys();
    }

    boolean nullableKeyEquals(@Nullable E a, @Nullable E b) {
        return a == b || (a != null && a.equals(b));
    }

    boolean keyEquals(@Nonnull E a, @Nullable E b) {
        return a.equals(b);
    }

    int nullableKeyHashCode(@Nullable E key) {
        return key != null ? key.hashCode() : 0;
    }

    int keyHashCode(@Nonnull E key) {
        return key.hashCode();
    }


    public boolean contains(@Nullable Object key) {
        return index(key) >= 0;
    }

    int index(@Nullable Object key) {
        if (key != null) {
            // noinspection unchecked
            E k = (E) key;
            // noinspection unchecked
            E[] keys = (E[]) set;
            int capacity, index;
            E cur;
            if ((cur = keys[index = SeparateKVObjKeyMixing.mix(keyHashCode(k)) % (capacity = keys.length)]) == k) {
                // key is present
                return index;
            } else {
                if (cur == FREE) {
                    // key is absent, free slot
                    return -1;
                } else {
                    if (cur != REMOVED) {
                        if (keyEquals(k, cur)) {
                            // key is present
                            return index;
                        } else {
                            if (noRemoved()) {
                                int bIndex = index, fIndex = index, step = 1;
                                while (true) {
                                    if ((bIndex -= step) < 0) bIndex += capacity;
                                    if ((cur = keys[bIndex]) == k) {
                                        // key is present
                                        return bIndex;
                                    } else if (cur == FREE) {
                                        // key is absent, free slot
                                        return -1;
                                    }
                                    else if (keyEquals(k, cur)) {
                                        // key is present
                                        return bIndex;
                                    }
                                    int t;
                                    if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                                    if ((cur = keys[fIndex]) == k) {
                                        // key is present
                                        return fIndex;
                                    } else if (cur == FREE) {
                                        // key is absent, free slot
                                        return -1;
                                    }
                                    else if (keyEquals(k, cur)) {
                                        // key is present
                                        return fIndex;
                                    }
                                    step += 2;
                                }
                            }
                        }
                    }
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = keys[bIndex]) == k) {
                            // key is present
                            return bIndex;
                        } else if (cur == FREE) {
                            // key is absent, free slot
                            return -1;
                        }
                        else if (cur != REMOVED && keyEquals(k, cur)) {
                            // key is present
                            return bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = keys[fIndex]) == k) {
                            // key is present
                            return fIndex;
                        } else if (cur == FREE) {
                            // key is absent, free slot
                            return -1;
                        }
                        else if (cur != REMOVED && keyEquals(k, cur)) {
                            // key is present
                            return fIndex;
                        }
                        step += 2;
                    }
                }
            }
        } else {
            return indexNullKey();
        }
    }

    int indexNullKey() {
        // noinspection unchecked
        E[] keys = (E[]) set;
        int capacity = keys.length;
        int index;
        E cur;
        if ((cur = keys[index = 0]) == null) {
            // key is present
            return index;
        } else {
            if (cur == FREE) {
                // key is absent, free slot
                return -1;
            } else {
                int bIndex = index, fIndex = index, step = 1;
                while (true) {
                    if ((bIndex -= step) < 0) bIndex += capacity;
                    if ((cur = keys[bIndex]) == null) {
                        // key is present
                        return bIndex;
                    } else if (cur == FREE) {
                        // key is absent, free slot
                        return -1;
                    }
                    int t;
                    if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                    if ((cur = keys[fIndex]) == null) {
                        // key is present
                        return fIndex;
                    } else if (cur == FREE) {
                        // key is absent, free slot
                        return -1;
                    }
                    step += 2;
                }
            }
        }
    }


    @Override
    void allocateArrays(int capacity) {
        set = new Object[capacity];
        fillFree();
    }

    @Override
    public void clear() {
        super.clear();
        fillFree();
    }

    private void fillFree() {
        Arrays.fill(set, FREE);
    }

    @Override
    void removeAt(int index) {
        set[index] = REMOVED;
    }
}

