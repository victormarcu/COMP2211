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

import net.openhft.koloboke.collect.hash.HashOverflowException;
import net.openhft.koloboke.collect.impl.*;

import java.util.*;
import java.util.concurrent
     .ThreadLocalRandom;


public abstract class UpdatableSeparateKVByteLHashSO extends UpdatableLHash
        implements SeparateKVByteLHash, PrimitiveConstants, UnsafeConstants {

    byte freeValue;

    byte[] set;

    void copy(SeparateKVByteLHash hash) {
        super.copy(hash);
        freeValue = hash.freeValue();

        set = hash.keys().clone();

    }

    void move(SeparateKVByteLHash hash) {
        super.copy(hash);
        freeValue = hash.freeValue();

        set = hash.keys();

    }

    final void init(HashConfigWrapper configWrapper, int size, byte freeValue
            ) {
        this.freeValue = freeValue;
        // calls allocateArrays, fill keys with this.freeValue => assign it before
        super.init(configWrapper, size);
    }


    @Override
    public byte freeValue() {
        return freeValue;
    }

    @Override
    public boolean supportRemoved() {
        return false;
    }

    @Override
    public byte removedValue() {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object key) {
        return contains(((Byte) key).byteValue());
    }

    public boolean contains(byte key) {
        return index(key) >= 0;
    }

    int index(byte key) {
        byte free;
        if (key != (free = freeValue)) {
            byte[] keys = set;
            int capacityMask, index;
            byte cur;
            if ((cur = keys[index = SeparateKVByteKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) == key) {
                // key is present
                return index;
            } else {
                if (cur == free) {
                    // key is absent
                    return -1;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                            // key is present
                            return index;
                        } else if (cur == free) {
                            // key is absent
                            return -1;
                        }
                    }
                }
            }
        } else {
            // key is absent
            return -1;
        }
    }

    private byte findNewFreeOrRemoved() {
        int mc = modCount();
        int size = size();
        if (size >= BYTE_CARDINALITY -
                1) {
            throw new HashOverflowException();
        }
        byte free = this.freeValue;
        
        Random random = ThreadLocalRandom.current();
        byte newFree;
        searchForFree:
        if (size > BYTE_CARDINALITY * 3 / 4) {
            int nf = random.nextInt(BYTE_CARDINALITY) * BYTE_PERMUTATION_STEP;
            for (int i = 0; i < BYTE_CARDINALITY; i++) {
                nf = nf + BYTE_PERMUTATION_STEP;
                newFree = (byte) nf;
                if (newFree != free &&
                        
                        index(newFree) < 0) {
                    break searchForFree;
                }
            }
            if (mc != modCount())
                throw new ConcurrentModificationException();
            throw new AssertionError("Impossible state");
        }
        else  {
            do {
                newFree = (byte) random.nextInt()
                                        ;
            } while (newFree == free ||
                    
                    index(newFree) >= 0);
        }
        return newFree;
    }


    byte changeFree() {
        int mc = modCount();
        byte newFree = findNewFreeOrRemoved();
        incrementModCount();
        mc++;
        ByteArrays.replaceAll(set, freeValue, newFree);
        this.freeValue = newFree;
        if (mc != modCount())
            throw new ConcurrentModificationException();
        return newFree;
    }


    @Override
    void allocateArrays(int capacity) {
        set = new byte[capacity];
        if (freeValue != 0)
            Arrays.fill(set, freeValue);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(set, freeValue);
    }

}

