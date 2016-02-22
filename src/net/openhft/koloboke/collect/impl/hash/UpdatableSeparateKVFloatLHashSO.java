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

import java.util.Arrays;


public abstract class UpdatableSeparateKVFloatLHashSO extends UpdatableLHash
        implements SeparateKVFloatLHash, PrimitiveConstants, UnsafeConstants {

    int[] set;

    void copy(SeparateKVFloatLHash hash) {
        super.copy(hash);
        set = hash.keys().clone();
    }

    void move(SeparateKVFloatLHash hash) {
        super.copy(hash);
        set = hash.keys();
    }


    public boolean contains(Object key) {
        return contains(((Float) key).floatValue());
    }

    public boolean contains(float key) {
        return index(Float.floatToIntBits(key)) >= 0;
    }

    public boolean contains(int key) {
        return index(key) >= 0;
    }

    int index(int key) {
        int[] keys = set;
        int capacityMask, index;
        int cur;
        if ((cur = keys[index = SeparateKVFloatKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) == key) {
            // key is present
            return index;
        } else {
            if (cur == FREE_BITS) {
                // key is absent
                return -1;
            } else {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        // key is present
                        return index;
                    } else if (cur == FREE_BITS) {
                        // key is absent
                        return -1;
                    }
                }
            }
        }
    }


    @Override
    void allocateArrays(int capacity) {
        set = new int[capacity];
        Arrays.fill(set, FREE_BITS);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(set, FREE_BITS);
    }

}

