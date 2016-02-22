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

import net.openhft.koloboke.collect.*;
import net.openhft.koloboke.collect.hash.HashConfig;
import net.openhft.koloboke.collect.impl.*;
import net.openhft.koloboke.collect.map.*;
import net.openhft.koloboke.collect.map.hash.*;
import net.openhft.koloboke.collect.set.*;
import net.openhft.koloboke.collect.set.hash.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.LongPredicate;
import net.openhft.koloboke.function.LongLongConsumer;
import net.openhft.koloboke.function.LongLongPredicate;
import net.openhft.koloboke.function.LongLongToLongFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;

import javax.annotation.Nonnull;
import java.util.*;


public class MutableQHashParallelKVLongLongMapGO
        extends MutableQHashParallelKVLongLongMapSO {

    
    final void copy(ParallelKVLongLongQHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    
    final void move(ParallelKVLongLongQHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }


    @Override
    public long defaultValue() {
        return 0L;
    }

    @Override
    public boolean containsEntry(long key, long value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return table[index + 1] == value;
        } else {
            // key is absent
            return false;
        }
    }


    @Override
    public Long get(Object key) {
        int index = index((Long) key);
        if (index >= 0) {
            // key is present
            return table[index + 1];
        } else {
            // key is absent
            return null;
        }
    }

    

    @Override
    public long get(long key) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return table[index + 1];
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Long getOrDefault(Object key, Long defaultValue) {
        int index = index((Long) key);
        if (index >= 0) {
            // key is present
            return table[index + 1];
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public long getOrDefault(long key, long defaultValue) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return table[index + 1];
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public void forEach(BiConsumer<? super Long, ? super Long> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        long free = freeValue;
        long removed = removedValue;
        long[] tab = table;
        if (noRemoved()) {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    action.accept(key, tab[i + 1]);
                }
            }
        } else {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    action.accept(key, tab[i + 1]);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void forEach(LongLongConsumer action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        long free = freeValue;
        long removed = removedValue;
        long[] tab = table;
        if (noRemoved()) {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    action.accept(key, tab[i + 1]);
                }
            }
        } else {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    action.accept(key, tab[i + 1]);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public boolean forEachWhile(LongLongPredicate predicate) {
        if (predicate == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return true;
        boolean terminated = false;
        int mc = modCount();
        long free = freeValue;
        long removed = removedValue;
        long[] tab = table;
        if (noRemoved()) {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    if (!predicate.test(key, tab[i + 1])) {
                        terminated = true;
                        break;
                    }
                }
            }
        } else {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    if (!predicate.test(key, tab[i + 1])) {
                        terminated = true;
                        break;
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return !terminated;
    }

    @Nonnull
    @Override
    public LongLongCursor cursor() {
        int mc = modCount();
        if (!noRemoved())
            return new SomeRemovedMapCursor(mc);
        return new NoRemovedMapCursor(mc);
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonLongLongMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalLongLongMapOps m) {
        if (isEmpty())
            return true;
        boolean containsAll = true;
        int mc = modCount();
        long free = freeValue;
        long removed = removedValue;
        long[] tab = table;
        if (noRemoved()) {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    if (!m.containsEntry(key, tab[i + 1])) {
                        containsAll = false;
                        break;
                    }
                }
            }
        } else {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    if (!m.containsEntry(key, tab[i + 1])) {
                        containsAll = false;
                        break;
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return containsAll;
    }

    @Override
    public void reversePutAllTo(InternalLongLongMapOps m) {
        if (isEmpty())
            return;
        int mc = modCount();
        long free = freeValue;
        long removed = removedValue;
        long[] tab = table;
        if (noRemoved()) {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    m.justPut(key, tab[i + 1]);
                }
            }
        } else {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    m.justPut(key, tab[i + 1]);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    @Nonnull
    public HashObjSet<Map.Entry<Long, Long>> entrySet() {
        return new EntryView();
    }

    @Override
    @Nonnull
    public LongCollection values() {
        return new ValueView();
    }


    @Override
    public boolean equals(Object o) {
        return CommonMapOps.equals(this, o);
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        int mc = modCount();
        long free = freeValue;
        long removed = removedValue;
        long[] tab = table;
        if (noRemoved()) {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                long val;
                    hashCode += ((int) (key ^ (key >>> 32))) ^ ((int) ((val = tab[i + 1]) ^ (val >>> 32)));
                }
            }
        } else {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                long val;
                    hashCode += ((int) (key ^ (key >>> 32))) ^ ((int) ((val = tab[i + 1]) ^ (val >>> 32)));
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return hashCode;
    }

    @Override
    public String toString() {
        if (isEmpty())
            return "{}";
        StringBuilder sb = new StringBuilder();
        int elementCount = 0;
        int mc = modCount();
        long free = freeValue;
        long removed = removedValue;
        long[] tab = table;
        if (noRemoved()) {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    sb.append(' ');
                    sb.append(key);
                    sb.append('=');
                    sb.append(tab[i + 1]);
                    sb.append(',');
                    if (++elementCount == 8) {
                        int expectedLength = sb.length() * (size() / 8);
                        sb.ensureCapacity(expectedLength + (expectedLength / 2));
                    }
                }
            }
        } else {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    sb.append(' ');
                    sb.append(key);
                    sb.append('=');
                    sb.append(tab[i + 1]);
                    sb.append(',');
                    if (++elementCount == 8) {
                        int expectedLength = sb.length() * (size() / 8);
                        sb.ensureCapacity(expectedLength + (expectedLength / 2));
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        sb.setCharAt(0, '{');
        sb.setCharAt(sb.length() - 1, '}');
        return sb.toString();
    }


    @Override
    void rehash(int newCapacity) {
        int mc = modCount();
        long free = freeValue;
        long removed = removedValue;
        long[] tab = table;
        initForRehash(newCapacity);
        mc++; // modCount is incremented in initForRehash()
        long[] newTab = table;
        int capacity = newTab.length;
        if (noRemoved()) {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    int index;
                    if (newTab[index = ParallelKVLongKeyMixing.mix(key) % capacity] != free) {
                        int bIndex = index, fIndex = index, step = 2;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if (newTab[bIndex] == free) {
                                index = bIndex;
                                break;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if (newTab[fIndex] == free) {
                                index = fIndex;
                                break;
                            }
                            step += 4;
                        }
                    }
                    newTab[index] = key;
                    newTab[index + 1] = tab[i + 1];
                }
            }
        } else {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    int index;
                    if (newTab[index = ParallelKVLongKeyMixing.mix(key) % capacity] != free) {
                        int bIndex = index, fIndex = index, step = 2;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if (newTab[bIndex] == free) {
                                index = bIndex;
                                break;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if (newTab[fIndex] == free) {
                                index = fIndex;
                                break;
                            }
                            step += 4;
                        }
                    }
                    newTab[index] = key;
                    newTab[index + 1] = tab[i + 1];
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }


    @Override
    public Long put(Long key, Long value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            long[] tab = table;
            long prevValue = tab[index + 1];
            tab[index + 1] = value;
            return prevValue;
        }
    }

    @Override
    public long put(long key, long value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return defaultValue();
        } else {
            // key is present
            long[] tab = table;
            long prevValue = tab[index + 1];
            tab[index + 1] = value;
            return prevValue;
        }
    }

    @Override
    public Long putIfAbsent(Long key, Long value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            return table[index + 1];
        }
    }

    @Override
    public long putIfAbsent(long key, long value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return defaultValue();
        } else {
            // key is present
            return table[index + 1];
        }
    }

    @Override
    public void justPut(long key, long value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return;
        } else {
            // key is present
            table[index + 1] = value;
            return;
        }
    }


    @Override
    public Long compute(Long key,
            BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        long k = key;
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        long free;
        long removed = removedValue;
        if (k == (free = freeValue)) {
            free = changeFree();
        } else if (k == removed) {
            removed = changeRemoved();
        }
        long[] tab = table;
        int capacity, index;
        long cur;
        keyPresent:
        if ((cur = tab[index = ParallelKVLongKeyMixing.mix(k) % (capacity = tab.length)]) != k) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 2;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = tab[bIndex]) == k) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = tab[fIndex]) == k) {
                                index = fIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            }
                            step += 4;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 2;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = tab[bIndex]) == k) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = tab[fIndex]) == k) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 4;
                    }
                }
                // key is absent, removed slot
                Long newValue = remappingFunction.apply(k, null);
                if (newValue != null) {
                    incrementModCount();
                    tab[firstRemoved] = k;
                    tab[firstRemoved + 1] = newValue;
                    postRemovedSlotInsertHook();
                    return newValue;
                } else {
                    return null;
                }
            }
            // key is absent, free slot
            Long newValue = remappingFunction.apply(k, null);
            if (newValue != null) {
                incrementModCount();
                tab[index] = k;
                tab[index + 1] = newValue;
                postFreeSlotInsertHook();
                return newValue;
            } else {
                return null;
            }
        }
        // key is present
        Long newValue = remappingFunction.apply(k, tab[index + 1]);
        if (newValue != null) {
            tab[index + 1] = newValue;
            return newValue;
        } else {
            incrementModCount();
            tab[index] = removed;
            postRemoveHook();
            return null;
        }
    }


    @Override
    public long compute(long key, LongLongToLongFunction remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        long free;
        long removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        long[] tab = table;
        int capacity, index;
        long cur;
        keyPresent:
        if ((cur = tab[index = ParallelKVLongKeyMixing.mix(key) % (capacity = tab.length)]) != key) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 2;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = tab[bIndex]) == key) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = tab[fIndex]) == key) {
                                index = fIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            }
                            step += 4;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 2;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = tab[bIndex]) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = tab[fIndex]) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 4;
                    }
                }
                // key is absent, removed slot
                long newValue = remappingFunction.applyAsLong(key, defaultValue());
                incrementModCount();
                tab[firstRemoved] = key;
                tab[firstRemoved + 1] = newValue;
                postRemovedSlotInsertHook();
                return newValue;
            }
            // key is absent, free slot
            long newValue = remappingFunction.applyAsLong(key, defaultValue());
            incrementModCount();
            tab[index] = key;
            tab[index + 1] = newValue;
            postFreeSlotInsertHook();
            return newValue;
        }
        // key is present
        long newValue = remappingFunction.applyAsLong(key, tab[index + 1]);
        tab[index + 1] = newValue;
        return newValue;
    }


    @Override
    public Long computeIfAbsent(Long key,
            Function<? super Long, ? extends Long> mappingFunction) {
        long k = key;
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        long free;
        long removed = removedValue;
        if (k == (free = freeValue)) {
            free = changeFree();
        } else if (k == removed) {
            removed = changeRemoved();
        }
        long[] tab = table;
        int capacity, index;
        long cur;
        if ((cur = tab[index = ParallelKVLongKeyMixing.mix(k) % (capacity = tab.length)]) == k) {
            // key is present
            return tab[index + 1];
        } else {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 2;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = tab[bIndex]) == k) {
                                // key is present
                                return tab[bIndex + 1];
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = tab[fIndex]) == k) {
                                // key is present
                                return tab[fIndex + 1];
                            } else if (cur == free) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            }
                            step += 4;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 2;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = tab[bIndex]) == k) {
                            // key is present
                            return tab[bIndex + 1];
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = tab[fIndex]) == k) {
                            // key is present
                            return tab[fIndex + 1];
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 4;
                    }
                }
                // key is absent, removed slot
                Long value = mappingFunction.apply(k);
                if (value != null) {
                    incrementModCount();
                    tab[firstRemoved] = k;
                    tab[firstRemoved + 1] = value;
                    postRemovedSlotInsertHook();
                    return value;
                } else {
                    return null;
                }
            }
            // key is absent, free slot
            Long value = mappingFunction.apply(k);
            if (value != null) {
                incrementModCount();
                tab[index] = k;
                tab[index + 1] = value;
                postFreeSlotInsertHook();
                return value;
            } else {
                return null;
            }
        }
    }


    @Override
    public long computeIfAbsent(long key, LongUnaryOperator mappingFunction) {
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        long free;
        long removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        long[] tab = table;
        int capacity, index;
        long cur;
        if ((cur = tab[index = ParallelKVLongKeyMixing.mix(key) % (capacity = tab.length)]) == key) {
            // key is present
            return tab[index + 1];
        } else {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 2;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = tab[bIndex]) == key) {
                                // key is present
                                return tab[bIndex + 1];
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = tab[fIndex]) == key) {
                                // key is present
                                return tab[fIndex + 1];
                            } else if (cur == free) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            }
                            step += 4;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 2;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = tab[bIndex]) == key) {
                            // key is present
                            return tab[bIndex + 1];
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = tab[fIndex]) == key) {
                            // key is present
                            return tab[fIndex + 1];
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 4;
                    }
                }
                // key is absent, removed slot
                long value = mappingFunction.applyAsLong(key);
                incrementModCount();
                tab[firstRemoved] = key;
                tab[firstRemoved + 1] = value;
                postRemovedSlotInsertHook();
                return value;
            }
            // key is absent, free slot
            long value = mappingFunction.applyAsLong(key);
            incrementModCount();
            tab[index] = key;
            tab[index + 1] = value;
            postFreeSlotInsertHook();
            return value;
        }
    }


    @Override
    public Long computeIfPresent(Long key,
            BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        long k = key;
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(k);
        if (index >= 0) {
            // key is present
            long[] tab = table;
            Long newValue = remappingFunction.apply(k, tab[index + 1]);
            if (newValue != null) {
                tab[index + 1] = newValue;
                return newValue;
            } else {
                incrementModCount();
                table[index] = removedValue;
                postRemoveHook();
                return null;
            }
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public long computeIfPresent(long key, LongLongToLongFunction remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(key);
        if (index >= 0) {
            // key is present
            long[] tab = table;
            long newValue = remappingFunction.applyAsLong(key, tab[index + 1]);
            tab[index + 1] = newValue;
            return newValue;
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Long merge(Long key, Long value,
            BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        long k = key;
        if (value == null)
            throw new java.lang.NullPointerException();
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        long free;
        long removed = removedValue;
        if (k == (free = freeValue)) {
            free = changeFree();
        } else if (k == removed) {
            removed = changeRemoved();
        }
        long[] tab = table;
        int capacity, index;
        long cur;
        keyPresent:
        if ((cur = tab[index = ParallelKVLongKeyMixing.mix(k) % (capacity = tab.length)]) != k) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 2;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = tab[bIndex]) == k) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = tab[fIndex]) == k) {
                                index = fIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            }
                            step += 4;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 2;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = tab[bIndex]) == k) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = tab[fIndex]) == k) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 4;
                    }
                }
                // key is absent, removed slot
                incrementModCount();
                tab[firstRemoved] = k;
                tab[firstRemoved + 1] = value;
                postRemovedSlotInsertHook();
                return value;
            }
            // key is absent, free slot
            incrementModCount();
            tab[index] = k;
            tab[index + 1] = value;
            postFreeSlotInsertHook();
            return value;
        }
        // key is present
        Long newValue = remappingFunction.apply(tab[index + 1], value);
        if (newValue != null) {
            tab[index + 1] = newValue;
            return newValue;
        } else {
            incrementModCount();
            tab[index] = removed;
            postRemoveHook();
            return null;
        }
    }


    @Override
    public long merge(long key, long value, LongBinaryOperator remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        long free;
        long removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        long[] tab = table;
        int capacity, index;
        long cur;
        keyPresent:
        if ((cur = tab[index = ParallelKVLongKeyMixing.mix(key) % (capacity = tab.length)]) != key) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 2;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = tab[bIndex]) == key) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = tab[fIndex]) == key) {
                                index = fIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            }
                            step += 4;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 2;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = tab[bIndex]) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = tab[fIndex]) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 4;
                    }
                }
                // key is absent, removed slot
                incrementModCount();
                tab[firstRemoved] = key;
                tab[firstRemoved + 1] = value;
                postRemovedSlotInsertHook();
                return value;
            }
            // key is absent, free slot
            incrementModCount();
            tab[index] = key;
            tab[index + 1] = value;
            postFreeSlotInsertHook();
            return value;
        }
        // key is present
        long newValue = remappingFunction.applyAsLong(tab[index + 1], value);
        tab[index + 1] = newValue;
        return newValue;
    }


    @Override
    public long addValue(long key, long value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return value;
        } else {
            // key is present
            long[] tab = table;
            long newValue = tab[index + 1] + value;
            tab[index + 1] = newValue;
            return newValue;
        }
    }

    @Override
    public long addValue(long key, long addition, long defaultValue) {
        long value = defaultValue + addition;
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return value;
        } else {
            // key is present
            long[] tab = table;
            long newValue = tab[index + 1] + addition;
            tab[index + 1] = newValue;
            return newValue;
        }
    }


    @Override
    public void putAll(@Nonnull Map<? extends Long, ? extends Long> m) {
        CommonLongLongMapOps.putAll(this, m);
    }


    @Override
    public Long replace(Long key, Long value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            long[] tab = table;
            long oldValue = tab[index + 1];
            tab[index + 1] = value;
            return oldValue;
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public long replace(long key, long value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            long[] tab = table;
            long oldValue = tab[index + 1];
            tab[index + 1] = value;
            return oldValue;
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public boolean replace(Long key, Long oldValue, Long newValue) {
        return replace(key.longValue(),
                oldValue.longValue(),
                newValue.longValue());
    }

    @Override
    public boolean replace(long key, long oldValue, long newValue) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            long[] tab = table;
            if (tab[index + 1] == oldValue) {
                tab[index + 1] = newValue;
                return true;
            } else {
                return false;
            }
        } else {
            // key is absent
            return false;
        }
    }


    @Override
    public void replaceAll(
            BiFunction<? super Long, ? super Long, ? extends Long> function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        long free = freeValue;
        long removed = removedValue;
        long[] tab = table;
        if (noRemoved()) {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    tab[i + 1] = function.apply(key, tab[i + 1]);
                }
            }
        } else {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    tab[i + 1] = function.apply(key, tab[i + 1]);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void replaceAll(LongLongToLongFunction function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        long free = freeValue;
        long removed = removedValue;
        long[] tab = table;
        if (noRemoved()) {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    tab[i + 1] = function.applyAsLong(key, tab[i + 1]);
                }
            }
        } else {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    tab[i + 1] = function.applyAsLong(key, tab[i + 1]);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }


    @Override
    public void clear() {
        int mc = modCount() + 1;
        super.clear();
        if (mc != modCount())
            throw new ConcurrentModificationException();
    }


    @Override
    void removeAt(int index) {
        incrementModCount();
        super.removeAt(index);
        postRemoveHook();
    }

    @Override
    public Long remove(Object key) {
        long k = (Long) key;
        long free, removed;
        if (k != (free = freeValue) && k != (removed = removedValue)) {
            long[] tab = table;
            int capacity, index;
            long cur;
            keyPresent:
            if ((cur = tab[index = ParallelKVLongKeyMixing.mix(k) % (capacity = tab.length)]) != k) {
                if (cur == free) {
                    // key is absent, free slot
                    return null;
                } else {
                    int bIndex = index, fIndex = index, step = 2;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = tab[bIndex]) == k) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return null;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = tab[fIndex]) == k) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return null;
                        }
                        step += 4;
                    }
                }
            }
            // key is present
            long val = tab[index + 1];
            incrementModCount();
            tab[index] = removed;
            postRemoveHook();
            return val;
        } else {
            // key is absent
            return null;
        }
    }


    @Override
    public boolean justRemove(long key) {
        long free, removed;
        if (key != (free = freeValue) && key != (removed = removedValue)) {
            long[] tab = table;
            int capacity, index;
            long cur;
            keyPresent:
            if ((cur = tab[index = ParallelKVLongKeyMixing.mix(key) % (capacity = tab.length)]) != key) {
                if (cur == free) {
                    // key is absent, free slot
                    return false;
                } else {
                    int bIndex = index, fIndex = index, step = 2;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = tab[bIndex]) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = tab[fIndex]) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        step += 4;
                    }
                }
            }
            // key is present
            incrementModCount();
            tab[index] = removed;
            postRemoveHook();
            return true;
        } else {
            // key is absent
            return false;
        }
    }



    

    @Override
    public long remove(long key) {
        long free, removed;
        if (key != (free = freeValue) && key != (removed = removedValue)) {
            long[] tab = table;
            int capacity, index;
            long cur;
            keyPresent:
            if ((cur = tab[index = ParallelKVLongKeyMixing.mix(key) % (capacity = tab.length)]) != key) {
                if (cur == free) {
                    // key is absent, free slot
                    return defaultValue();
                } else {
                    int bIndex = index, fIndex = index, step = 2;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = tab[bIndex]) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue();
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = tab[fIndex]) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue();
                        }
                        step += 4;
                    }
                }
            }
            // key is present
            long val = tab[index + 1];
            incrementModCount();
            tab[index] = removed;
            postRemoveHook();
            return val;
        } else {
            // key is absent
            return defaultValue();
        }
    }



    @Override
    public boolean remove(Object key, Object value) {
        return remove(((Long) key).longValue(),
                ((Long) value).longValue()
                );
    }

    @Override
    public boolean remove(long key, long value) {
        long free, removed;
        if (key != (free = freeValue) && key != (removed = removedValue)) {
            long[] tab = table;
            int capacity, index;
            long cur;
            keyPresent:
            if ((cur = tab[index = ParallelKVLongKeyMixing.mix(key) % (capacity = tab.length)]) != key) {
                if (cur == free) {
                    // key is absent, free slot
                    return false;
                } else {
                    int bIndex = index, fIndex = index, step = 2;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = tab[bIndex]) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = tab[fIndex]) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        step += 4;
                    }
                }
            }
            // key is present
            if (tab[index + 1] == value) {
                incrementModCount();
                tab[index] = removed;
                postRemoveHook();
                return true;
            } else {
                return false;
            }
        } else {
            // key is absent
            return false;
        }
    }


    @Override
    public boolean removeIf(LongLongPredicate filter) {
        if (filter == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long removed = removedValue;
        long[] tab = table;
        if (noRemoved()) {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    if (filter.test(key, tab[i + 1])) {
                        incrementModCount();
                        mc++;
                        tab[i] = removed;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    if (filter.test(key, tab[i + 1])) {
                        incrementModCount();
                        mc++;
                        tab[i] = removed;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }






    class EntryView extends AbstractSetView<Map.Entry<Long, Long>>
            implements HashObjSet<Map.Entry<Long, Long>>,
            InternalObjCollectionOps<Map.Entry<Long, Long>> {

        @Nonnull
        @Override
        public Equivalence<Entry<Long, Long>> equivalence() {
            return Equivalence.entryEquivalence(
                    Equivalence.<Long>defaultEquality()
                    ,
                    Equivalence.<Long>defaultEquality()
                    
            );
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return MutableQHashParallelKVLongLongMapGO.this.hashConfig();
        }


        @Override
        public int size() {
            return MutableQHashParallelKVLongLongMapGO.this.size();
        }

        @Override
        public double currentLoad() {
            return MutableQHashParallelKVLongLongMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Long, Long> e = (Map.Entry<Long, Long>) o;
                return containsEntry(e.getKey(), e.getValue());
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        @Nonnull
        public final Object[] toArray() {
            int size = size();
            Object[] result = new Object[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        result[resultIndex++] = new MutableEntry(mc, i, key, tab[i + 1]);
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        result[resultIndex++] = new MutableEntry(mc, i, key, tab[i + 1]);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return result;
        }

        @Override
        @SuppressWarnings("unchecked")
        @Nonnull
        public final <T> T[] toArray(@Nonnull T[] a) {
            int size = size();
            if (a.length < size) {
                Class<?> elementType = a.getClass().getComponentType();
                a = (T[]) java.lang.reflect.Array.newInstance(elementType, size);
            }
            if (size == 0) {
                if (a.length > 0)
                    a[0] = null;
                return a;
            }
            int resultIndex = 0;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        a[resultIndex++] = (T) new MutableEntry(mc, i, key, tab[i + 1]);
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        a[resultIndex++] = (T) new MutableEntry(mc, i, key, tab[i + 1]);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public final void forEach(@Nonnull Consumer<? super Map.Entry<Long, Long>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        action.accept(new MutableEntry(mc, i, key, tab[i + 1]));
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        action.accept(new MutableEntry(mc, i, key, tab[i + 1]));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(@Nonnull  Predicate<? super Map.Entry<Long, Long>> predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        if (!predicate.test(new MutableEntry(mc, i, key, tab[i + 1]))) {
                            terminated = true;
                            break;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (!predicate.test(new MutableEntry(mc, i, key, tab[i + 1]))) {
                            terminated = true;
                            break;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return !terminated;
        }

        @Override
        @Nonnull
        public ObjIterator<Map.Entry<Long, Long>> iterator() {
            int mc = modCount();
            if (!noRemoved())
                return new SomeRemovedEntryIterator(mc);
            return new NoRemovedEntryIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Long, Long>> cursor() {
            int mc = modCount();
            if (!noRemoved())
                return new SomeRemovedEntryCursor(mc);
            return new NoRemovedEntryCursor(mc);
        }

        @Override
        public final boolean containsAll(@Nonnull Collection<?> c) {
            return CommonObjCollectionOps.containsAll(this, c);
        }

        @Override
        public final boolean allContainingIn(ObjCollection<?> c) {
            if (isEmpty())
                return true;
            boolean containsAll = true;
            ReusableEntry e = new ReusableEntry();
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        if (!c.contains(e.with(key, tab[i + 1]))) {
                            containsAll = false;
                            break;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (!c.contains(e.with(key, tab[i + 1]))) {
                            containsAll = false;
                            break;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return containsAll;
        }

        @Override
        public boolean reverseRemoveAllFrom(ObjSet<?> s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            ReusableEntry e = new ReusableEntry();
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        changed |= s.remove(e.with(key, tab[i + 1]));
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        changed |= s.remove(e.with(key, tab[i + 1]));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Long, Long>> c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        changed |= c.add(new MutableEntry(mc, i, key, tab[i + 1]));
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        changed |= c.add(new MutableEntry(mc, i, key, tab[i + 1]));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        public int hashCode() {
            return MutableQHashParallelKVLongLongMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        sb.append(' ');
                        sb.append(key);
                        sb.append('=');
                        sb.append(tab[i + 1]);
                        sb.append(',');
                        if (++elementCount == 8) {
                            int expectedLength = sb.length() * (size() / 8);
                            sb.ensureCapacity(expectedLength + (expectedLength / 2));
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        sb.append(' ');
                        sb.append(key);
                        sb.append('=');
                        sb.append(tab[i + 1]);
                        sb.append(',');
                        if (++elementCount == 8) {
                            int expectedLength = sb.length() * (size() / 8);
                            sb.ensureCapacity(expectedLength + (expectedLength / 2));
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            sb.setCharAt(0, '[');
            sb.setCharAt(sb.length() - 1, ']');
            return sb.toString();
        }

        @Override
        public boolean shrink() {
            return MutableQHashParallelKVLongLongMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Long, Long> e = (Map.Entry<Long, Long>) o;
                long key = e.getKey();
                long value = e.getValue();
                return MutableQHashParallelKVLongLongMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf(@Nonnull Predicate<? super Map.Entry<Long, Long>> filter) {
            if (filter == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        if (filter.test(new MutableEntry(mc, i, key, tab[i + 1]))) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (filter.test(new MutableEntry(mc, i, key, tab[i + 1]))) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public final boolean removeAll(@Nonnull Collection<?> c) {
            if (c instanceof InternalObjCollectionOps) {
                InternalObjCollectionOps c2 = (InternalObjCollectionOps) c;
                if (equivalence().equals(c2.equivalence()) && c2.size() < this.size()) {
                    // noinspection unchecked
                    c2.reverseRemoveAllFrom(this);
                }
            }
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty() || c.isEmpty())
                return false;
            boolean changed = false;
            ReusableEntry e = new ReusableEntry();
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        if (c.contains(e.with(key, tab[i + 1]))) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (c.contains(e.with(key, tab[i + 1]))) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public final boolean retainAll(@Nonnull Collection<?> c) {
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty())
                return false;
            if (c.isEmpty()) {
                clear();
                return true;
            }
            boolean changed = false;
            ReusableEntry e = new ReusableEntry();
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        if (!c.contains(e.with(key, tab[i + 1]))) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (!c.contains(e.with(key, tab[i + 1]))) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public void clear() {
            MutableQHashParallelKVLongLongMapGO.this.clear();
        }
    }


    abstract class LongLongEntry extends AbstractEntry<Long, Long> {

        abstract long key();

        @Override
        public final Long getKey() {
            return key();
        }

        abstract long value();

        @Override
        public final Long getValue() {
            return value();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            long k2;
            long v2;
            try {
                e2 = (Map.Entry) o;
                k2 = (Long) e2.getKey();
                v2 = (Long) e2.getValue();
                return key() == k2
                        
                        &&
                        value() == v2
                        ;
            } catch (ClassCastException e) {
                return false;
            } catch (NullPointerException e) {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Primitives.hashCode(key())
                    
                    ^
                    Primitives.hashCode(value())
                    ;
        }
    }


    class MutableEntry extends LongLongEntry {
        final int modCount;
        private final int index;
        final long key;
        private long value;

        MutableEntry(int modCount, int index, long key, long value) {
            this.modCount = modCount;
            this.index = index;
            this.key = key;
            this.value = value;
        }

        @Override
        public long key() {
            return key;
        }

        @Override
        public long value() {
            return value;
        }

        @Override
        public Long setValue(Long newValue) {
            if (modCount != modCount())
                throw new IllegalStateException();
            long oldValue = value;
            long unwrappedNewValue = newValue;
            value = unwrappedNewValue;
            updateValueInTable(unwrappedNewValue);
            return oldValue;
        }

        void updateValueInTable(long newValue) {
            table[index + 1] = newValue;
        }
    }



    class ReusableEntry extends LongLongEntry {
        private long key;
        private long value;

        ReusableEntry with(long key, long value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public long key() {
            return key;
        }

        @Override
        public long value() {
            return value;
        }
    }


    class ValueView extends AbstractLongValueView {


        @Override
        public int size() {
            return MutableQHashParallelKVLongLongMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return MutableQHashParallelKVLongLongMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return MutableQHashParallelKVLongLongMapGO.this.containsValue(o);
        }

        @Override
        public boolean contains(long v) {
            return MutableQHashParallelKVLongLongMapGO.this.containsValue(v);
        }



        @Override
        public void forEach(Consumer<? super Long> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        action.accept(tab[i + 1]);
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        action.accept(tab[i + 1]);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public void forEach(LongConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        action.accept(tab[i + 1]);
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        action.accept(tab[i + 1]);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(LongPredicate predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        if (!predicate.test(tab[i + 1])) {
                            terminated = true;
                            break;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (!predicate.test(tab[i + 1])) {
                            terminated = true;
                            break;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return !terminated;
        }

        @Override
        public boolean allContainingIn(LongCollection c) {
            if (isEmpty())
                return true;
            boolean containsAll = true;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        if (!c.contains(tab[i + 1])) {
                            containsAll = false;
                            break;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (!c.contains(tab[i + 1])) {
                            containsAll = false;
                            break;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return containsAll;
        }


        @Override
        public boolean reverseAddAllTo(LongCollection c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        changed |= c.add(tab[i + 1]);
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        changed |= c.add(tab[i + 1]);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        @Override
        public boolean reverseRemoveAllFrom(LongSet s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        changed |= s.removeLong(tab[i + 1]);
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        changed |= s.removeLong(tab[i + 1]);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }



        @Override
        @Nonnull
        public LongIterator iterator() {
            int mc = modCount();
            if (!noRemoved())
                return new SomeRemovedValueIterator(mc);
            return new NoRemovedValueIterator(mc);
        }

        @Nonnull
        @Override
        public LongCursor cursor() {
            int mc = modCount();
            if (!noRemoved())
                return new SomeRemovedValueCursor(mc);
            return new NoRemovedValueCursor(mc);
        }

        @Override
        @Nonnull
        public Object[] toArray() {
            int size = size();
            Object[] result = new Object[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        result[resultIndex++] = tab[i + 1];
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        result[resultIndex++] = tab[i + 1];
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return result;
        }

        @Override
        @SuppressWarnings("unchecked")
        @Nonnull
        public <T> T[] toArray(@Nonnull T[] a) {
            int size = size();
            if (a.length < size) {
                Class<?> elementType = a.getClass().getComponentType();
                a = (T[]) java.lang.reflect.Array.newInstance(elementType, size);
            }
            if (size == 0) {
                if (a.length > 0)
                    a[0] = null;
                return a;
            }
            int resultIndex = 0;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        a[resultIndex++] = (T) Long.valueOf(tab[i + 1]);
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        a[resultIndex++] = (T) Long.valueOf(tab[i + 1]);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public long[] toLongArray() {
            int size = size();
            long[] result = new long[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        result[resultIndex++] = tab[i + 1];
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        result[resultIndex++] = tab[i + 1];
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return result;
        }

        @Override
        public long[] toArray(long[] a) {
            int size = size();
            if (a.length < size)
                a = new long[size];
            if (size == 0) {
                if (a.length > 0)
                    a[0] = 0L;
                return a;
            }
            int resultIndex = 0;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        a[resultIndex++] = tab[i + 1];
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        a[resultIndex++] = tab[i + 1];
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = 0L;
            return a;
        }


        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        sb.append(' ').append(tab[i + 1]).append(',');
                        if (++elementCount == 8) {
                            int expectedLength = sb.length() * (size() / 8);
                            sb.ensureCapacity(expectedLength + (expectedLength / 2));
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        sb.append(' ').append(tab[i + 1]).append(',');
                        if (++elementCount == 8) {
                            int expectedLength = sb.length() * (size() / 8);
                            sb.ensureCapacity(expectedLength + (expectedLength / 2));
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            sb.setCharAt(0, '[');
            sb.setCharAt(sb.length() - 1, ']');
            return sb.toString();
        }


        @Override
        public boolean remove(Object o) {
            return removeLong(( Long ) o);
        }

        @Override
        public boolean removeLong(long v) {
            return removeValue(v);
        }



        @Override
        public void clear() {
            MutableQHashParallelKVLongLongMapGO.this.clear();
        }

        
        public boolean removeIf(Predicate<? super Long> filter) {
            if (filter == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        if (filter.test(tab[i + 1])) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (filter.test(tab[i + 1])) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public boolean removeIf(LongPredicate filter) {
            if (filter == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        if (filter.test(tab[i + 1])) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (filter.test(tab[i + 1])) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            if (c instanceof LongCollection)
                return removeAll((LongCollection) c);
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty() || c.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        if (c.contains(tab[i + 1])) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (c.contains(tab[i + 1])) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        private boolean removeAll(LongCollection c) {
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty() || c.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        if (c.contains(tab[i + 1])) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (c.contains(tab[i + 1])) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            if (c instanceof LongCollection)
                return retainAll((LongCollection) c);
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty())
                return false;
            if (c.isEmpty()) {
                clear();
                return true;
            }
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        if (!c.contains(tab[i + 1])) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (!c.contains(tab[i + 1])) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        private boolean retainAll(LongCollection c) {
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty())
                return false;
            if (c.isEmpty()) {
                clear();
                return true;
            }
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long removed = removedValue;
            long[] tab = table;
            if (noRemoved()) {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    if (tab[i] != free) {
                        if (!c.contains(tab[i + 1])) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        if (!c.contains(tab[i + 1])) {
                            incrementModCount();
                            mc++;
                            tab[i] = removed;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

    }



    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Long, Long>> {
        final long[] tab;
        final long free;
        final long removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        MutableEntry next;

        NoRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            long[] tab = this.tab = table;
            long free = this.free = freeValue;
            this.removed = removedValue;
            int nextI = tab.length;
            while ((nextI -= 2) >= 0) {
                long key;
                if ((key = tab[nextI]) != free) {
                    next = new MutableEntry(mc, nextI, key, tab[nextI + 1]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Long, Long>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            long[] tab = this.tab;
            long free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    action.accept(new MutableEntry(mc, i, key, tab[i + 1]));
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public Map.Entry<Long, Long> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                int mc;
                if ((mc = expectedModCount) == modCount()) {
                    index = nextI;
                    long[] tab = this.tab;
                    long free = this.free;
                    MutableEntry prev = next;
                    while ((nextI -= 2) >= 0) {
                        long key;
                        if ((key = tab[nextI]) != free) {
                            next = new MutableEntry(mc, nextI, key, tab[nextI + 1]);
                            break;
                        }
                    }
                    nextIndex = nextI;
                    return prev;
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.util.NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    tab[index] = removed;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Long, Long>> {
        final long[] tab;
        final long free;
        final long removed;
        int expectedModCount;
        int index;
        long curKey;
        long curValue;

        NoRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            long free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Long, Long>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            long[] tab = this.tab;
            long free = this.free;
            int index = this.index;
            for (int i = index - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    action.accept(new MutableEntry(mc, i, key, tab[i + 1]));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public Map.Entry<Long, Long> elem() {
            long curKey;
            if ((curKey = this.curKey) != free) {
                return new MutableEntry(expectedModCount, index, curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                long[] tab = this.tab;
                long free = this.free;
                for (int i = index - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        index = i;
                        curKey = key;
                        curValue = tab[i + 1];
                        return true;
                    }
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            long free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    tab[index] = removed;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedEntryIterator implements ObjIterator<Map.Entry<Long, Long>> {
        final long[] tab;
        final long free;
        final long removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        MutableEntry next;

        SomeRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            long[] tab = this.tab = table;
            long free = this.free = freeValue;
            long removed = this.removed = removedValue;
            int nextI = tab.length;
            while ((nextI -= 2) >= 0) {
                long key;
                if ((key = tab[nextI]) != free && key != removed) {
                    next = new MutableEntry(mc, nextI, key, tab[nextI + 1]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Long, Long>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            long[] tab = this.tab;
            long free = this.free;
            long removed = this.removed;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    action.accept(new MutableEntry(mc, i, key, tab[i + 1]));
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public Map.Entry<Long, Long> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                int mc;
                if ((mc = expectedModCount) == modCount()) {
                    index = nextI;
                    long[] tab = this.tab;
                    long free = this.free;
                    long removed = this.removed;
                    MutableEntry prev = next;
                    while ((nextI -= 2) >= 0) {
                        long key;
                        if ((key = tab[nextI]) != free && key != removed) {
                            next = new MutableEntry(mc, nextI, key, tab[nextI + 1]);
                            break;
                        }
                    }
                    nextIndex = nextI;
                    return prev;
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.util.NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    tab[index] = removed;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedEntryCursor implements ObjCursor<Map.Entry<Long, Long>> {
        final long[] tab;
        final long free;
        final long removed;
        int expectedModCount;
        int index;
        long curKey;
        long curValue;

        SomeRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            long free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Long, Long>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            long[] tab = this.tab;
            long free = this.free;
            long removed = this.removed;
            int index = this.index;
            for (int i = index - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    action.accept(new MutableEntry(mc, i, key, tab[i + 1]));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public Map.Entry<Long, Long> elem() {
            long curKey;
            if ((curKey = this.curKey) != free) {
                return new MutableEntry(expectedModCount, index, curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                long[] tab = this.tab;
                long free = this.free;
                long removed = this.removed;
                for (int i = index - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        index = i;
                        curKey = key;
                        curValue = tab[i + 1];
                        return true;
                    }
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            long free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    tab[index] = removed;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }




    class NoRemovedValueIterator implements LongIterator {
        final long[] tab;
        final long free;
        final long removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        long next;

        NoRemovedValueIterator(int mc) {
            expectedModCount = mc;
            long[] tab = this.tab = table;
            long free = this.free = freeValue;
            this.removed = removedValue;
            int nextI = tab.length;
            while ((nextI -= 2) >= 0) {
                if (tab[nextI] != free) {
                    next = tab[nextI + 1];
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public long nextLong() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                if (expectedModCount == modCount()) {
                    index = nextI;
                    long[] tab = this.tab;
                    long free = this.free;
                    long prev = next;
                    while ((nextI -= 2) >= 0) {
                        if (tab[nextI] != free) {
                            next = tab[nextI + 1];
                            break;
                        }
                    }
                    nextIndex = nextI;
                    return prev;
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.util.NoSuchElementException();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super Long> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            long[] tab = this.tab;
            long free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    action.accept(tab[i + 1]);
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public void forEachRemaining(LongConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            long[] tab = this.tab;
            long free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    action.accept(tab[i + 1]);
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public Long next() {
            return nextLong();
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    tab[index] = removed;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class NoRemovedValueCursor implements LongCursor {
        final long[] tab;
        final long free;
        final long removed;
        int expectedModCount;
        int index;
        long curKey;
        long curValue;

        NoRemovedValueCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            long free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(LongConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            long[] tab = this.tab;
            long free = this.free;
            int index = this.index;
            for (int i = index - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    action.accept(tab[i + 1]);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public long elem() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                long[] tab = this.tab;
                long free = this.free;
                for (int i = index - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        index = i;
                        curKey = key;
                        curValue = tab[i + 1];
                        return true;
                    }
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            long free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    tab[index] = removed;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedValueIterator implements LongIterator {
        final long[] tab;
        final long free;
        final long removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        long next;

        SomeRemovedValueIterator(int mc) {
            expectedModCount = mc;
            long[] tab = this.tab = table;
            long free = this.free = freeValue;
            long removed = this.removed = removedValue;
            int nextI = tab.length;
            while ((nextI -= 2) >= 0) {
                long key;
                if ((key = tab[nextI]) != free && key != removed) {
                    next = tab[nextI + 1];
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public long nextLong() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                if (expectedModCount == modCount()) {
                    index = nextI;
                    long[] tab = this.tab;
                    long free = this.free;
                    long removed = this.removed;
                    long prev = next;
                    while ((nextI -= 2) >= 0) {
                        long key;
                        if ((key = tab[nextI]) != free && key != removed) {
                            next = tab[nextI + 1];
                            break;
                        }
                    }
                    nextIndex = nextI;
                    return prev;
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.util.NoSuchElementException();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super Long> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            long[] tab = this.tab;
            long free = this.free;
            long removed = this.removed;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    action.accept(tab[i + 1]);
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public void forEachRemaining(LongConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            long[] tab = this.tab;
            long free = this.free;
            long removed = this.removed;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    action.accept(tab[i + 1]);
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public Long next() {
            return nextLong();
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    tab[index] = removed;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedValueCursor implements LongCursor {
        final long[] tab;
        final long free;
        final long removed;
        int expectedModCount;
        int index;
        long curKey;
        long curValue;

        SomeRemovedValueCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            long free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(LongConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            long[] tab = this.tab;
            long free = this.free;
            long removed = this.removed;
            int index = this.index;
            for (int i = index - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    action.accept(tab[i + 1]);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public long elem() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                long[] tab = this.tab;
                long free = this.free;
                long removed = this.removed;
                for (int i = index - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        index = i;
                        curKey = key;
                        curValue = tab[i + 1];
                        return true;
                    }
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            long free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    tab[index] = removed;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }



    class NoRemovedMapCursor implements LongLongCursor {
        final long[] tab;
        final long free;
        final long removed;
        int expectedModCount;
        int index;
        long curKey;
        long curValue;

        NoRemovedMapCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            long free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(LongLongConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            long[] tab = this.tab;
            long free = this.free;
            int index = this.index;
            for (int i = index - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    action.accept(key, tab[i + 1]);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public long key() {
            long curKey;
            if ((curKey = this.curKey) != free) {
                return curKey;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public long value() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(long value) {
            if (curKey != free) {
                if (expectedModCount == modCount()) {
                    tab[index + 1] = value;
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                long[] tab = this.tab;
                long free = this.free;
                for (int i = index - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free) {
                        index = i;
                        curKey = key;
                        curValue = tab[i + 1];
                        return true;
                    }
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            long free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    tab[index] = removed;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }

    class SomeRemovedMapCursor implements LongLongCursor {
        final long[] tab;
        final long free;
        final long removed;
        int expectedModCount;
        int index;
        long curKey;
        long curValue;

        SomeRemovedMapCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            long free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(LongLongConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            long[] tab = this.tab;
            long free = this.free;
            long removed = this.removed;
            int index = this.index;
            for (int i = index - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free && key != removed) {
                    action.accept(key, tab[i + 1]);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public long key() {
            long curKey;
            if ((curKey = this.curKey) != free) {
                return curKey;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public long value() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(long value) {
            if (curKey != free) {
                if (expectedModCount == modCount()) {
                    tab[index + 1] = value;
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                long[] tab = this.tab;
                long free = this.free;
                long removed = this.removed;
                for (int i = index - 2; i >= 0; i -= 2) {
                    long key;
                    if ((key = tab[i]) != free && key != removed) {
                        index = i;
                        curKey = key;
                        curValue = tab[i + 1];
                        return true;
                    }
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            long free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    tab[index] = removed;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }
}

