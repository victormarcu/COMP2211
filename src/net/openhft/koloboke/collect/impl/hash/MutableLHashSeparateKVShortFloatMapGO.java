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
import net.openhft.koloboke.function.ShortPredicate;
import net.openhft.koloboke.function.ShortFloatConsumer;
import net.openhft.koloboke.function.ShortFloatPredicate;
import net.openhft.koloboke.function.ShortFloatToFloatFunction;
import net.openhft.koloboke.function.ShortToFloatFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.openhft.koloboke.function.FloatBinaryOperator;
import net.openhft.koloboke.function.FloatConsumer;
import net.openhft.koloboke.function.FloatPredicate;

import javax.annotation.Nonnull;
import java.util.*;


public class MutableLHashSeparateKVShortFloatMapGO
        extends MutableLHashSeparateKVShortFloatMapSO {

    @Override
    final void copy(SeparateKVShortFloatLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    @Override
    final void move(SeparateKVShortFloatLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }


    @Override
    public float defaultValue() {
        return 0.0f;
    }

    @Override
    public boolean containsEntry(short key, float value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return values[index] == Float.floatToIntBits(value);
        } else {
            // key is absent
            return false;
        }
    }

    @Override
    public boolean containsEntry(short key, int value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return values[index] == value;
        } else {
            // key is absent
            return false;
        }
    }

    @Override
    public Float get(Object key) {
        int index = index((Short) key);
        if (index >= 0) {
            // key is present
            return Float.intBitsToFloat(values[index]);
        } else {
            // key is absent
            return null;
        }
    }

    

    @Override
    public float get(short key) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return Float.intBitsToFloat(values[index]);
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Float getOrDefault(Object key, Float defaultValue) {
        int index = index((Short) key);
        if (index >= 0) {
            // key is present
            return Float.intBitsToFloat(values[index]);
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public float getOrDefault(short key, float defaultValue) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return Float.intBitsToFloat(values[index]);
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public void forEach(BiConsumer<? super Short, ? super Float> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                action.accept(key, Float.intBitsToFloat(vals[i]));
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void forEach(ShortFloatConsumer action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                action.accept(key, Float.intBitsToFloat(vals[i]));
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public boolean forEachWhile(ShortFloatPredicate predicate) {
        if (predicate == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return true;
        boolean terminated = false;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                if (!predicate.test(key, Float.intBitsToFloat(vals[i]))) {
                    terminated = true;
                    break;
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return !terminated;
    }

    @Nonnull
    @Override
    public ShortFloatCursor cursor() {
        int mc = modCount();
        return new NoRemovedMapCursor(mc);
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonShortFloatMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalShortFloatMapOps m) {
        if (isEmpty())
            return true;
        boolean containsAll = true;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                if (!m.containsEntry(key, vals[i])) {
                    containsAll = false;
                    break;
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return containsAll;
    }

    @Override
    public void reversePutAllTo(InternalShortFloatMapOps m) {
        if (isEmpty())
            return;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                m.justPut(key, vals[i]);
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    @Nonnull
    public HashObjSet<Map.Entry<Short, Float>> entrySet() {
        return new EntryView();
    }

    @Override
    @Nonnull
    public FloatCollection values() {
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
        short free = freeValue;
        short[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                hashCode += key ^ vals[i];
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
        short free = freeValue;
        short[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                sb.append(' ');
                sb.append(key);
                sb.append('=');
                sb.append(Float.intBitsToFloat(vals[i]));
                sb.append(',');
                if (++elementCount == 8) {
                    int expectedLength = sb.length() * (size() / 8);
                    sb.ensureCapacity(expectedLength + (expectedLength / 2));
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
        short free = freeValue;
        short[] keys = set;
        int[] vals = values;
        initForRehash(newCapacity);
        mc++; // modCount is incremented in initForRehash()
        short[] newKeys = set;
        int capacityMask = newKeys.length - 1;
        int[] newVals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                int index;
                if (newKeys[index = SeparateKVShortKeyMixing.mix(key) & capacityMask] != free) {
                    while (true) {
                        if (newKeys[(index = (index - 1) & capacityMask)] == free) {
                            break;
                        }
                    }
                }
                newKeys[index] = key;
                newVals[index] = vals[i];
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }


    @Override
    public Float put(Short key, Float value) {
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            int[] vals = values;
            float prevValue = Float.intBitsToFloat(vals[index]);
            vals[index] = Float.floatToIntBits(value);
            return prevValue;
        }
    }

    @Override
    public float put(short key, float value) {
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return defaultValue();
        } else {
            // key is present
            int[] vals = values;
            float prevValue = Float.intBitsToFloat(vals[index]);
            vals[index] = Float.floatToIntBits(value);
            return prevValue;
        }
    }

    @Override
    public Float putIfAbsent(Short key, Float value) {
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            return Float.intBitsToFloat(values[index]);
        }
    }

    @Override
    public float putIfAbsent(short key, float value) {
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return defaultValue();
        } else {
            // key is present
            return Float.intBitsToFloat(values[index]);
        }
    }

    @Override
    public void justPut(short key, float value) {
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return;
        } else {
            // key is present
            values[index] = Float.floatToIntBits(value);
            return;
        }
    }

    @Override
    public void justPut(short key, int value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return;
        } else {
            // key is present
            values[index] = value;
            return;
        }
    }

    @Override
    public Float compute(Short key,
            BiFunction<? super Short, ? super Float, ? extends Float> remappingFunction) {
        short k = key;
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        if (k == (free = freeValue)) {
            free = changeFree();
        }
        short[] keys = set;
        int[] vals = values;
        int capacityMask, index;
        short cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVShortKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) != k) {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        break keyPresent;
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            Float newValue = remappingFunction.apply(k, null);
            if (newValue != null) {
                incrementModCount();
                keys[index] = k;
                vals[index] = Float.floatToIntBits(newValue);
                postInsertHook();
                return newValue;
            } else {
                return null;
            }
        }
        // key is present
        Float newValue = remappingFunction.apply(k, Float.intBitsToFloat(vals[index]));
        if (newValue != null) {
            vals[index] = Float.floatToIntBits(newValue);
            return newValue;
        } else {
            removeAt(index);
            return null;
        }
    }


    @Override
    public float compute(short key, ShortFloatToFloatFunction remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        if (key == (free = freeValue)) {
            free = changeFree();
        }
        short[] keys = set;
        int[] vals = values;
        int capacityMask, index;
        short cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVShortKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) != key) {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        break keyPresent;
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            float newValue = remappingFunction.applyAsFloat(key, defaultValue());
            incrementModCount();
            keys[index] = key;
            vals[index] = Float.floatToIntBits(newValue);
            postInsertHook();
            return newValue;
        }
        // key is present
        float newValue = remappingFunction.applyAsFloat(key, Float.intBitsToFloat(vals[index]));
        vals[index] = Float.floatToIntBits(newValue);
        return newValue;
    }


    @Override
    public Float computeIfAbsent(Short key,
            Function<? super Short, ? extends Float> mappingFunction) {
        short k = key;
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        if (k == (free = freeValue)) {
            free = changeFree();
        }
        short[] keys = set;
        int[] vals = values;
        int capacityMask, index;
        short cur;
        if ((cur = keys[index = SeparateKVShortKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) == k) {
            // key is present
            return Float.intBitsToFloat(vals[index]);
        } else {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        // key is present
                        return Float.intBitsToFloat(vals[index]);
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            Float value = mappingFunction.apply(k);
            if (value != null) {
                incrementModCount();
                keys[index] = k;
                vals[index] = Float.floatToIntBits(value);
                postInsertHook();
                return value;
            } else {
                return null;
            }
        }
    }


    @Override
    public float computeIfAbsent(short key, ShortToFloatFunction mappingFunction) {
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        if (key == (free = freeValue)) {
            free = changeFree();
        }
        short[] keys = set;
        int[] vals = values;
        int capacityMask, index;
        short cur;
        if ((cur = keys[index = SeparateKVShortKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) == key) {
            // key is present
            return Float.intBitsToFloat(vals[index]);
        } else {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        // key is present
                        return Float.intBitsToFloat(vals[index]);
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            float value = mappingFunction.applyAsFloat(key);
            incrementModCount();
            keys[index] = key;
            vals[index] = Float.floatToIntBits(value);
            postInsertHook();
            return value;
        }
    }


    @Override
    public Float computeIfPresent(Short key,
            BiFunction<? super Short, ? super Float, ? extends Float> remappingFunction) {
        short k = key;
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(k);
        if (index >= 0) {
            // key is present
            int[] vals = values;
            Float newValue = remappingFunction.apply(k, Float.intBitsToFloat(vals[index]));
            if (newValue != null) {
                vals[index] = Float.floatToIntBits(newValue);
                return newValue;
            } else {
                removeAt(index);
                return null;
            }
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public float computeIfPresent(short key, ShortFloatToFloatFunction remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(key);
        if (index >= 0) {
            // key is present
            int[] vals = values;
            float newValue = remappingFunction.applyAsFloat(key, Float.intBitsToFloat(vals[index]));
            vals[index] = Float.floatToIntBits(newValue);
            return newValue;
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Float merge(Short key, Float value,
            BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        short k = key;
        if (value == null)
            throw new java.lang.NullPointerException();
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        if (k == (free = freeValue)) {
            free = changeFree();
        }
        short[] keys = set;
        int[] vals = values;
        int capacityMask, index;
        short cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVShortKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) != k) {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        break keyPresent;
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            incrementModCount();
            keys[index] = k;
            vals[index] = Float.floatToIntBits(value);
            postInsertHook();
            return value;
        }
        // key is present
        Float newValue = remappingFunction.apply(Float.intBitsToFloat(vals[index]), value);
        if (newValue != null) {
            vals[index] = Float.floatToIntBits(newValue);
            return newValue;
        } else {
            removeAt(index);
            return null;
        }
    }


    @Override
    public float merge(short key, float value, FloatBinaryOperator remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        if (key == (free = freeValue)) {
            free = changeFree();
        }
        short[] keys = set;
        int[] vals = values;
        int capacityMask, index;
        short cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVShortKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) != key) {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        break keyPresent;
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            incrementModCount();
            keys[index] = key;
            vals[index] = Float.floatToIntBits(value);
            postInsertHook();
            return value;
        }
        // key is present
        float newValue = remappingFunction.applyAsFloat(Float.intBitsToFloat(vals[index]), value);
        vals[index] = Float.floatToIntBits(newValue);
        return newValue;
    }


    @Override
    public float addValue(short key, float value) {
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return value;
        } else {
            // key is present
            int[] vals = values;
            float newValue = Float.intBitsToFloat(vals[index]) + value;
            vals[index] = Float.floatToIntBits(newValue);
            return newValue;
        }
    }

    @Override
    public float addValue(short key, float addition, float defaultValue) {
        float value = defaultValue + addition;
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return value;
        } else {
            // key is present
            int[] vals = values;
            float newValue = Float.intBitsToFloat(vals[index]) + addition;
            vals[index] = Float.floatToIntBits(newValue);
            return newValue;
        }
    }


    @Override
    public void putAll(@Nonnull Map<? extends Short, ? extends Float> m) {
        CommonShortFloatMapOps.putAll(this, m);
    }


    @Override
    public Float replace(Short key, Float value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            int[] vals = values;
            float oldValue = Float.intBitsToFloat(vals[index]);
            vals[index] = Float.floatToIntBits(value);
            return oldValue;
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public float replace(short key, float value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            int[] vals = values;
            float oldValue = Float.intBitsToFloat(vals[index]);
            vals[index] = Float.floatToIntBits(value);
            return oldValue;
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public boolean replace(Short key, Float oldValue, Float newValue) {
        return replace(key.shortValue(),
                oldValue.floatValue(),
                newValue.floatValue());
    }

    @Override
    public boolean replace(short key, float oldValue, float newValue) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            int[] vals = values;
            if (vals[index] == Float.floatToIntBits(oldValue)) {
                vals[index] = Float.floatToIntBits(newValue);
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
            BiFunction<? super Short, ? super Float, ? extends Float> function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                vals[i] = Float.floatToIntBits(function.apply(key, Float.intBitsToFloat(vals[i])));
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void replaceAll(ShortFloatToFloatFunction function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                vals[i] = Float.floatToIntBits(function.applyAsFloat(key, Float.intBitsToFloat(vals[i])));
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
        short free = freeValue;
        short[] keys = set;
        int[] vals = values;
        int capacityMask = keys.length - 1;
        incrementModCount();
        int indexToRemove = index;
        int indexToShift = indexToRemove;
        int shiftDistance = 1;
        while (true) {
            indexToShift = (indexToShift - 1) & capacityMask;
            short keyToShift;
            if ((keyToShift = keys[indexToShift]) == free) {
                break;
            }
            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                keys[indexToRemove] = keyToShift;
                vals[indexToRemove] = vals[indexToShift];
                indexToRemove = indexToShift;
                shiftDistance = 1;
            } else {
                shiftDistance++;
                if (indexToShift == 1 + index) {
                    throw new java.util.ConcurrentModificationException();
                }
            }
        }
        keys[indexToRemove] = free;
        postRemoveHook();
    }

    @Override
    public Float remove(Object key) {
        short k = (Short) key;
        short free;
        if (k != (free = freeValue)) {
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int index;
            short cur;
            keyPresent:
            if ((cur = keys[index = SeparateKVShortKeyMixing.mix(k) & capacityMask]) != k) {
                if (cur == free) {
                    // key is absent
                    return null;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent
                            return null;
                        }
                    }
                }
            }
            // key is present
            int[] vals = values;
            float val = Float.intBitsToFloat(vals[index]);
            incrementModCount();
            int indexToRemove = index;
            int indexToShift = indexToRemove;
            int shiftDistance = 1;
            while (true) {
                indexToShift = (indexToShift - 1) & capacityMask;
                short keyToShift;
                if ((keyToShift = keys[indexToShift]) == free) {
                    break;
                }
                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                    keys[indexToRemove] = keyToShift;
                    vals[indexToRemove] = vals[indexToShift];
                    indexToRemove = indexToShift;
                    shiftDistance = 1;
                } else {
                    shiftDistance++;
                    if (indexToShift == 1 + index) {
                        throw new java.util.ConcurrentModificationException();
                    }
                }
            }
            keys[indexToRemove] = free;
            postRemoveHook();
            return val;
        } else {
            // key is absent
            return null;
        }
    }


    @Override
    public boolean justRemove(short key) {
        short free;
        if (key != (free = freeValue)) {
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int index;
            short cur;
            keyPresent:
            if ((cur = keys[index = SeparateKVShortKeyMixing.mix(key) & capacityMask]) != key) {
                if (cur == free) {
                    // key is absent
                    return false;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent
                            return false;
                        }
                    }
                }
            }
            // key is present
            int[] vals = values;
            incrementModCount();
            int indexToRemove = index;
            int indexToShift = indexToRemove;
            int shiftDistance = 1;
            while (true) {
                indexToShift = (indexToShift - 1) & capacityMask;
                short keyToShift;
                if ((keyToShift = keys[indexToShift]) == free) {
                    break;
                }
                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                    keys[indexToRemove] = keyToShift;
                    vals[indexToRemove] = vals[indexToShift];
                    indexToRemove = indexToShift;
                    shiftDistance = 1;
                } else {
                    shiftDistance++;
                    if (indexToShift == 1 + index) {
                        throw new java.util.ConcurrentModificationException();
                    }
                }
            }
            keys[indexToRemove] = free;
            postRemoveHook();
            return true;
        } else {
            // key is absent
            return false;
        }
    }



    

    @Override
    public float remove(short key) {
        short free;
        if (key != (free = freeValue)) {
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int index;
            short cur;
            keyPresent:
            if ((cur = keys[index = SeparateKVShortKeyMixing.mix(key) & capacityMask]) != key) {
                if (cur == free) {
                    // key is absent
                    return defaultValue();
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent
                            return defaultValue();
                        }
                    }
                }
            }
            // key is present
            int[] vals = values;
            float val = Float.intBitsToFloat(vals[index]);
            incrementModCount();
            int indexToRemove = index;
            int indexToShift = indexToRemove;
            int shiftDistance = 1;
            while (true) {
                indexToShift = (indexToShift - 1) & capacityMask;
                short keyToShift;
                if ((keyToShift = keys[indexToShift]) == free) {
                    break;
                }
                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                    keys[indexToRemove] = keyToShift;
                    vals[indexToRemove] = vals[indexToShift];
                    indexToRemove = indexToShift;
                    shiftDistance = 1;
                } else {
                    shiftDistance++;
                    if (indexToShift == 1 + index) {
                        throw new java.util.ConcurrentModificationException();
                    }
                }
            }
            keys[indexToRemove] = free;
            postRemoveHook();
            return val;
        } else {
            // key is absent
            return defaultValue();
        }
    }



    @Override
    public boolean remove(Object key, Object value) {
        return remove(((Short) key).shortValue(),
                ((Float) value).floatValue()
                );
    }

    @Override
    public boolean remove(short key, float value) {
        short free;
        if (key != (free = freeValue)) {
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int index;
            short cur;
            keyPresent:
            if ((cur = keys[index = SeparateKVShortKeyMixing.mix(key) & capacityMask]) != key) {
                if (cur == free) {
                    // key is absent
                    return false;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent
                            return false;
                        }
                    }
                }
            }
            // key is present
            int[] vals = values;
            if (vals[index] == Float.floatToIntBits(value)) {
                incrementModCount();
                int indexToRemove = index;
                int indexToShift = indexToRemove;
                int shiftDistance = 1;
                while (true) {
                    indexToShift = (indexToShift - 1) & capacityMask;
                    short keyToShift;
                    if ((keyToShift = keys[indexToShift]) == free) {
                        break;
                    }
                    if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                        keys[indexToRemove] = keyToShift;
                        vals[indexToRemove] = vals[indexToShift];
                        indexToRemove = indexToShift;
                        shiftDistance = 1;
                    } else {
                        shiftDistance++;
                        if (indexToShift == 1 + index) {
                            throw new java.util.ConcurrentModificationException();
                        }
                    }
                }
                keys[indexToRemove] = free;
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
    public boolean removeIf(ShortFloatPredicate filter) {
        if (filter == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int capacityMask = keys.length - 1;
        int firstDelayedRemoved = -1;
        short delayedRemoved = (short) 0;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                if (filter.test(key, Float.intBitsToFloat(vals[i]))) {
                    incrementModCount();
                    mc++;
                    closeDeletion:
                    if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                }
                                if (indexToRemove == i) {
                                    i++;
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + i) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                }
            }
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }




    // under this condition - operations, overridden from MutableSeparateKVShortLHashGO
    // when values are objects - in order to set values to null on removing (for garbage collection)
    // when algo is LHash - because shift deletion should shift values to

    @Override
    public boolean removeIf(Predicate<? super Short> filter) {
        if (filter == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int capacityMask = keys.length - 1;
        int firstDelayedRemoved = -1;
        short delayedRemoved = (short) 0;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                if (filter.test(key)) {
                    incrementModCount();
                    mc++;
                    closeDeletion:
                    if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                }
                                if (indexToRemove == i) {
                                    i++;
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + i) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                }
            }
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }

    @Override
    public boolean removeIf(ShortPredicate filter) {
        if (filter == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int capacityMask = keys.length - 1;
        int firstDelayedRemoved = -1;
        short delayedRemoved = (short) 0;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                if (filter.test(key)) {
                    incrementModCount();
                    mc++;
                    closeDeletion:
                    if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                }
                                if (indexToRemove == i) {
                                    i++;
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + i) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                }
            }
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }

    @Override
    public boolean removeAll(@Nonnull HashShortSet thisC, @Nonnull Collection<?> c) {
        if (thisC == c)
            throw new IllegalArgumentException();
        if (isEmpty() || c.isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int capacityMask = keys.length - 1;
        int firstDelayedRemoved = -1;
        short delayedRemoved = (short) 0;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                if (c.contains(key)) {
                    incrementModCount();
                    mc++;
                    closeDeletion:
                    if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                }
                                if (indexToRemove == i) {
                                    i++;
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + i) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                }
            }
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }

    @Override
    boolean removeAll(@Nonnull HashShortSet thisC, @Nonnull ShortCollection c) {
        if (thisC == c)
            throw new IllegalArgumentException();
        if (isEmpty() || c.isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int capacityMask = keys.length - 1;
        int firstDelayedRemoved = -1;
        short delayedRemoved = (short) 0;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                if (c.contains(key)) {
                    incrementModCount();
                    mc++;
                    closeDeletion:
                    if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                }
                                if (indexToRemove == i) {
                                    i++;
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + i) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                }
            }
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }


    @Override
    public boolean retainAll(@Nonnull HashShortSet thisC, @Nonnull Collection<?> c) {
        if (c instanceof ShortCollection)
            return retainAll(thisC, (ShortCollection) c);
        if (thisC == c)
            throw new IllegalArgumentException();
        if (isEmpty())
            return false;
        if (c.isEmpty()) {
            clear();
            return true;
        }
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int capacityMask = keys.length - 1;
        int firstDelayedRemoved = -1;
        short delayedRemoved = (short) 0;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                if (!c.contains(key)) {
                    incrementModCount();
                    mc++;
                    closeDeletion:
                    if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                }
                                if (indexToRemove == i) {
                                    i++;
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + i) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                }
            }
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }

    private boolean retainAll(@Nonnull HashShortSet thisC, @Nonnull ShortCollection c) {
        if (thisC == c)
            throw new IllegalArgumentException();
        if (isEmpty())
            return false;
        if (c.isEmpty()) {
            clear();
            return true;
        }
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short[] keys = set;
        int capacityMask = keys.length - 1;
        int firstDelayedRemoved = -1;
        short delayedRemoved = (short) 0;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            short key;
            if ((key = keys[i]) != free) {
                if (!c.contains(key)) {
                    incrementModCount();
                    mc++;
                    closeDeletion:
                    if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                }
                                if (indexToRemove == i) {
                                    i++;
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + i) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                }
            }
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }


    @Override
    void closeDelayedRemoved(int firstDelayedRemoved
            , short delayedRemoved) {
        short free = freeValue;
        short[] keys = set;
        int[] vals = values;
        int capacityMask = keys.length - 1;
        for (int i = firstDelayedRemoved; i >= 0; i--) {
            if (keys[i] == delayedRemoved) {
                int indexToRemove = i;
                int indexToShift = indexToRemove;
                int shiftDistance = 1;
                while (true) {
                    indexToShift = (indexToShift - 1) & capacityMask;
                    short keyToShift;
                    if ((keyToShift = keys[indexToShift]) == free) {
                        break;
                    }
                    if ((keyToShift != delayedRemoved) && (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance)) {
                        keys[indexToRemove] = keyToShift;
                        vals[indexToRemove] = vals[indexToShift];
                        indexToRemove = indexToShift;
                        shiftDistance = 1;
                    } else {
                        shiftDistance++;
                        if (indexToShift == 1 + i) {
                            throw new java.util.ConcurrentModificationException();
                        }
                    }
                }
                keys[indexToRemove] = free;
                postRemoveHook();
            }
        }
    }



    @Override
    public ShortIterator iterator() {
        int mc = modCount();
        return new NoRemovedKeyIterator(mc);
    }

    @Override
    public ShortCursor setCursor() {
        int mc = modCount();
        return new NoRemovedKeyCursor(mc);
    }


    class NoRemovedKeyIterator extends NoRemovedIterator {
         int[] vals;

        private NoRemovedKeyIterator(int mc) {
            super(mc);
            vals = values;
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    short[] keys = this.keys;
                    int[] vals = this.vals;
                    if (keys == set) {
                        int capacityMask = this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (this.keys == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = nextIndex + 1) > 0) {
                                            this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                this.keys[indexToRemove] = free;
                                            }
                                        }
                                    } else if (indexToRemove == index) {
                                        this.nextIndex = index;
                                        if (indexToShift < index - 1) {
                                            this.next = keyToShift;
                                        }
                                    }
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + index) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(keys[index]);
                    }
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class NoRemovedKeyCursor extends NoRemovedCursor {
         int[] vals;

        private NoRemovedKeyCursor(int mc) {
            super(mc);
            vals = values;
        }

        @Override
        public void remove() {
            short curKey;
            short free;
            if ((curKey = this.curKey) != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    int index = this.index;
                    short[] keys = this.keys;
                    int[] vals = this.vals;
                    if (keys == set) {
                        int capacityMask = this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (this.keys == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                this.keys[indexToRemove] = free;
                                            }
                                        }
                                    } else if (indexToRemove == index) {
                                        this.index = ++index;
                                    }
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + index) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(curKey);
                    }
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }





    class EntryView extends AbstractSetView<Map.Entry<Short, Float>>
            implements HashObjSet<Map.Entry<Short, Float>>,
            InternalObjCollectionOps<Map.Entry<Short, Float>> {

        @Nonnull
        @Override
        public Equivalence<Entry<Short, Float>> equivalence() {
            return Equivalence.entryEquivalence(
                    Equivalence.<Short>defaultEquality()
                    ,
                    Equivalence.<Float>defaultEquality()
                    
            );
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return MutableLHashSeparateKVShortFloatMapGO.this.hashConfig();
        }


        @Override
        public int size() {
            return MutableLHashSeparateKVShortFloatMapGO.this.size();
        }

        @Override
        public double currentLoad() {
            return MutableLHashSeparateKVShortFloatMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Short, Float> e = (Map.Entry<Short, Float>) o;
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
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    result[resultIndex++] = new MutableEntry(mc, i, key, vals[i]);
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
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    a[resultIndex++] = (T) new MutableEntry(mc, i, key, vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public final void forEach(@Nonnull Consumer<? super Map.Entry<Short, Float>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(@Nonnull  Predicate<? super Map.Entry<Short, Float>> predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (!predicate.test(new MutableEntry(mc, i, key, vals[i]))) {
                        terminated = true;
                        break;
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return !terminated;
        }

        @Override
        @Nonnull
        public ObjIterator<Map.Entry<Short, Float>> iterator() {
            int mc = modCount();
            return new NoRemovedEntryIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Short, Float>> cursor() {
            int mc = modCount();
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
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (!c.contains(e.with(key, vals[i]))) {
                        containsAll = false;
                        break;
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
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    changed |= s.remove(e.with(key, vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Short, Float>> c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    changed |= c.add(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        public int hashCode() {
            return MutableLHashSeparateKVShortFloatMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    sb.append(' ');
                    sb.append(key);
                    sb.append('=');
                    sb.append(Float.intBitsToFloat(vals[i]));
                    sb.append(',');
                    if (++elementCount == 8) {
                        int expectedLength = sb.length() * (size() / 8);
                        sb.ensureCapacity(expectedLength + (expectedLength / 2));
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
            return MutableLHashSeparateKVShortFloatMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Short, Float> e = (Map.Entry<Short, Float>) o;
                short key = e.getKey();
                float value = e.getValue();
                return MutableLHashSeparateKVShortFloatMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf(@Nonnull Predicate<? super Map.Entry<Short, Float>> filter) {
            if (filter == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int firstDelayedRemoved = -1;
            short delayedRemoved = (short) 0;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (filter.test(new MutableEntry(mc, i, key, vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion:
                        if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                short keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                }
                                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    }
                                    if (indexToRemove == i) {
                                        i++;
                                    }
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == 1 + i) {
                                        throw new java.util.ConcurrentModificationException();
                                    }
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    }
                }
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
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
            short free = freeValue;
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int firstDelayedRemoved = -1;
            short delayedRemoved = (short) 0;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (c.contains(e.with(key, vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion:
                        if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                short keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                }
                                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    }
                                    if (indexToRemove == i) {
                                        i++;
                                    }
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == 1 + i) {
                                        throw new java.util.ConcurrentModificationException();
                                    }
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    }
                }
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
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
            short free = freeValue;
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int firstDelayedRemoved = -1;
            short delayedRemoved = (short) 0;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (!c.contains(e.with(key, vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion:
                        if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                short keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                }
                                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    }
                                    if (indexToRemove == i) {
                                        i++;
                                    }
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == 1 + i) {
                                        throw new java.util.ConcurrentModificationException();
                                    }
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    }
                }
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public void clear() {
            MutableLHashSeparateKVShortFloatMapGO.this.clear();
        }
    }


    abstract class ShortFloatEntry extends AbstractEntry<Short, Float> {

        abstract short key();

        @Override
        public final Short getKey() {
            return key();
        }

        abstract int value();

        @Override
        public final Float getValue() {
            return Float.intBitsToFloat(value());
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            short k2;
            int v2;
            try {
                e2 = (Map.Entry) o;
                k2 = (Short) e2.getKey();
                v2 = Float.floatToIntBits((Float) e2.getValue());
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


    class MutableEntry extends ShortFloatEntry {
        final int modCount;
        private final int index;
        final short key;
        private int value;

        MutableEntry(int modCount, int index, short key, int value) {
            this.modCount = modCount;
            this.index = index;
            this.key = key;
            this.value = value;
        }

        @Override
        public short key() {
            return key;
        }

        @Override
        public int value() {
            return value;
        }

        @Override
        public Float setValue(Float newValue) {
            if (modCount != modCount())
                throw new IllegalStateException();
            float oldValue = Float.intBitsToFloat(value);
            int unwrappedNewValue = Float.floatToIntBits(newValue);
            value = unwrappedNewValue;
            updateValueInTable(unwrappedNewValue);
            return oldValue;
        }

        void updateValueInTable(int newValue) {
            values[index] = newValue;
        }
    }



    class ReusableEntry extends ShortFloatEntry {
        private short key;
        private int value;

        ReusableEntry with(short key, int value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public short key() {
            return key;
        }

        @Override
        public int value() {
            return value;
        }
    }


    class ValueView extends AbstractFloatValueView {


        @Override
        public int size() {
            return MutableLHashSeparateKVShortFloatMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return MutableLHashSeparateKVShortFloatMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return MutableLHashSeparateKVShortFloatMapGO.this.containsValue(o);
        }

        @Override
        public boolean contains(float v) {
            return MutableLHashSeparateKVShortFloatMapGO.this.containsValue(v);
        }

        @Override
        public boolean contains(int bits) {
            return MutableLHashSeparateKVShortFloatMapGO.this.containsValue(bits);
        }


        @Override
        public void forEach(Consumer<? super Float> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(Float.intBitsToFloat(vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public void forEach(FloatConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(Float.intBitsToFloat(vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(FloatPredicate predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    if (!predicate.test(Float.intBitsToFloat(vals[i]))) {
                        terminated = true;
                        break;
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return !terminated;
        }

        @Override
        public boolean allContainingIn(FloatCollection c) {
            if (c instanceof InternalFloatCollectionOps)
                return allContainingIn((InternalFloatCollectionOps) c);
            if (isEmpty())
                return true;
            boolean containsAll = true;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    if (!c.contains(Float.intBitsToFloat(vals[i]))) {
                        containsAll = false;
                        break;
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return containsAll;
        }

        private boolean allContainingIn(InternalFloatCollectionOps c) {
            if (isEmpty())
                return true;
            boolean containsAll = true;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    if (!c.contains(vals[i])) {
                        containsAll = false;
                        break;
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return containsAll;
        }

        @Override
        public boolean reverseAddAllTo(FloatCollection c) {
            if (c instanceof InternalFloatCollectionOps)
                return reverseAddAllTo((InternalFloatCollectionOps) c);
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    changed |= c.add(Float.intBitsToFloat(vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        private boolean reverseAddAllTo(InternalFloatCollectionOps c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    changed |= c.add(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public boolean reverseRemoveAllFrom(FloatSet s) {
            if (s instanceof InternalFloatCollectionOps)
                return reverseRemoveAllFrom((InternalFloatCollectionOps) s);
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    changed |= s.removeFloat(Float.intBitsToFloat(vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        private boolean reverseRemoveAllFrom(InternalFloatCollectionOps s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    changed |= s.removeFloat(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        @Override
        @Nonnull
        public FloatIterator iterator() {
            int mc = modCount();
            return new NoRemovedValueIterator(mc);
        }

        @Nonnull
        @Override
        public FloatCursor cursor() {
            int mc = modCount();
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
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    result[resultIndex++] = Float.intBitsToFloat(vals[i]);
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
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    a[resultIndex++] = (T) Float.valueOf(Float.intBitsToFloat(vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public float[] toFloatArray() {
            int size = size();
            float[] result = new float[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    result[resultIndex++] = Float.intBitsToFloat(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return result;
        }

        @Override
        public float[] toArray(float[] a) {
            int size = size();
            if (a.length < size)
                a = new float[size];
            if (size == 0) {
                if (a.length > 0)
                    a[0] = 0.0f;
                return a;
            }
            int resultIndex = 0;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    a[resultIndex++] = Float.intBitsToFloat(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = 0.0f;
            return a;
        }


        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    sb.append(' ').append(Float.intBitsToFloat(vals[i])).append(',');
                    if (++elementCount == 8) {
                        int expectedLength = sb.length() * (size() / 8);
                        sb.ensureCapacity(expectedLength + (expectedLength / 2));
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
            return removeFloat(( Float ) o);
        }

        @Override
        public boolean removeFloat(float v) {
            return removeValue(v);
        }

        @Override
        public boolean removeFloat(int bits) {
            return removeValue(bits);
        }


        @Override
        public void clear() {
            MutableLHashSeparateKVShortFloatMapGO.this.clear();
        }

        
        public boolean removeIf(Predicate<? super Float> filter) {
            if (filter == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int firstDelayedRemoved = -1;
            short delayedRemoved = (short) 0;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (filter.test(Float.intBitsToFloat(vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion:
                        if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                short keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                }
                                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    }
                                    if (indexToRemove == i) {
                                        i++;
                                    }
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == 1 + i) {
                                        throw new java.util.ConcurrentModificationException();
                                    }
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    }
                }
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public boolean removeIf(FloatPredicate filter) {
            if (filter == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int firstDelayedRemoved = -1;
            short delayedRemoved = (short) 0;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (filter.test(Float.intBitsToFloat(vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion:
                        if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                short keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                }
                                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    }
                                    if (indexToRemove == i) {
                                        i++;
                                    }
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == 1 + i) {
                                        throw new java.util.ConcurrentModificationException();
                                    }
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    }
                }
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            if (c instanceof FloatCollection)
                return removeAll((FloatCollection) c);
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty() || c.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int firstDelayedRemoved = -1;
            short delayedRemoved = (short) 0;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (c.contains(Float.intBitsToFloat(vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion:
                        if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                short keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                }
                                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    }
                                    if (indexToRemove == i) {
                                        i++;
                                    }
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == 1 + i) {
                                        throw new java.util.ConcurrentModificationException();
                                    }
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    }
                }
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        private boolean removeAll(FloatCollection c) {
            if (c instanceof InternalFloatCollectionOps)
                return removeAll((InternalFloatCollectionOps) c);
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty() || c.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int firstDelayedRemoved = -1;
            short delayedRemoved = (short) 0;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (c.contains(Float.intBitsToFloat(vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion:
                        if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                short keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                }
                                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    }
                                    if (indexToRemove == i) {
                                        i++;
                                    }
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == 1 + i) {
                                        throw new java.util.ConcurrentModificationException();
                                    }
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    }
                }
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        private boolean removeAll(InternalFloatCollectionOps c) {
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty() || c.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int firstDelayedRemoved = -1;
            short delayedRemoved = (short) 0;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (c.contains(vals[i])) {
                        incrementModCount();
                        mc++;
                        closeDeletion:
                        if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                short keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                }
                                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    }
                                    if (indexToRemove == i) {
                                        i++;
                                    }
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == 1 + i) {
                                        throw new java.util.ConcurrentModificationException();
                                    }
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    }
                }
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            if (c instanceof FloatCollection)
                return retainAll((FloatCollection) c);
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
            short free = freeValue;
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int firstDelayedRemoved = -1;
            short delayedRemoved = (short) 0;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (!c.contains(Float.intBitsToFloat(vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion:
                        if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                short keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                }
                                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    }
                                    if (indexToRemove == i) {
                                        i++;
                                    }
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == 1 + i) {
                                        throw new java.util.ConcurrentModificationException();
                                    }
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    }
                }
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        private boolean retainAll(FloatCollection c) {
            if (c instanceof InternalFloatCollectionOps)
                return retainAll((InternalFloatCollectionOps) c);
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
            short free = freeValue;
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int firstDelayedRemoved = -1;
            short delayedRemoved = (short) 0;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (!c.contains(Float.intBitsToFloat(vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion:
                        if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                short keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                }
                                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    }
                                    if (indexToRemove == i) {
                                        i++;
                                    }
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == 1 + i) {
                                        throw new java.util.ConcurrentModificationException();
                                    }
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    }
                }
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        private boolean retainAll(InternalFloatCollectionOps c) {
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
            short free = freeValue;
            short[] keys = set;
            int capacityMask = keys.length - 1;
            int firstDelayedRemoved = -1;
            short delayedRemoved = (short) 0;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    if (!c.contains(vals[i])) {
                        incrementModCount();
                        mc++;
                        closeDeletion:
                        if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                short keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                }
                                if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    }
                                    if (indexToRemove == i) {
                                        i++;
                                    }
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == 1 + i) {
                                        throw new java.util.ConcurrentModificationException();
                                    }
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    }
                }
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }
    }



    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Short, Float>> {
        short[] keys;
        int[] vals;
        final short free;
        final int capacityMask;
        int expectedModCount;
        
        class MutableEntry2 extends MutableEntry {
            MutableEntry2(int modCount, int index, short key, int value) {
                super(modCount, index, key, value);
            }
            
            @Override
            void updateValueInTable(int newValue) {
                if (vals == values) {
                    vals[index] = newValue;
                } else {
                    justPut(key, newValue);
                    if (this.modCount != modCount()) {
                        throw new java.lang.IllegalStateException();
                    }
                }
            }
        }
        
        int index = -1;
        int nextIndex;
        MutableEntry next;

        NoRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            short[] keys = this.keys = set;
            capacityMask = keys.length - 1;
            int[] vals = this.vals = values;
            short free = this.free = freeValue;
            int nextI = keys.length;
            while (--nextI >= 0) {
                short key;
                if ((key = keys[nextI]) != free) {
                    next = new MutableEntry2(mc, nextI, key, vals[nextI]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Short, Float>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            short[] keys = this.keys;
            int[] vals = this.vals;
            short free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    action.accept(new MutableEntry2(mc, i, key, vals[i]));
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
        public Map.Entry<Short, Float> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                int mc;
                if ((mc = expectedModCount) == modCount()) {
                    index = nextI;
                    short[] keys = this.keys;
                    short free = this.free;
                    MutableEntry prev = next;
                    while (--nextI >= 0) {
                        short key;
                        if ((key = keys[nextI]) != free) {
                            next = new MutableEntry2(mc, nextI, key, vals[nextI]);
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
                    short[] keys = this.keys;
                    int[] vals = this.vals;
                    if (keys == set) {
                        int capacityMask = this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (this.keys == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = nextIndex + 1) > 0) {
                                            this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                this.keys[indexToRemove] = free;
                                            }
                                        }
                                    } else if (indexToRemove == index) {
                                        this.nextIndex = index;
                                        if (indexToShift < index - 1) {
                                            this.next = new MutableEntry2(modCount(), indexToShift, keyToShift, vals[indexToShift]);
                                        }
                                    }
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + index) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(keys[index]);
                    }
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Short, Float>> {
        short[] keys;
        int[] vals;
        final short free;
        final int capacityMask;
        int expectedModCount;
        
        class MutableEntry2 extends MutableEntry {
            MutableEntry2(int modCount, int index, short key, int value) {
                super(modCount, index, key, value);
            }
            
            @Override
            void updateValueInTable(int newValue) {
                if (vals == values) {
                    vals[index] = newValue;
                } else {
                    justPut(key, newValue);
                    if (this.modCount != modCount()) {
                        throw new java.lang.IllegalStateException();
                    }
                }
            }
        }
        
        int index;
        short curKey;
        int curValue;

        NoRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            short[] keys = this.keys = set;
            capacityMask = keys.length - 1;
            index = keys.length;
            vals = values;
            short free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Short, Float>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            short[] keys = this.keys;
            int[] vals = this.vals;
            short free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    action.accept(new MutableEntry2(mc, i, key, vals[i]));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public Map.Entry<Short, Float> elem() {
            short curKey;
            if ((curKey = this.curKey) != free) {
                return new MutableEntry2(expectedModCount, index, curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                short[] keys = this.keys;
                short free = this.free;
                for (int i = index - 1; i >= 0; i--) {
                    short key;
                    if ((key = keys[i]) != free) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
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
            short curKey;
            short free;
            if ((curKey = this.curKey) != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    int index = this.index;
                    short[] keys = this.keys;
                    int[] vals = this.vals;
                    if (keys == set) {
                        int capacityMask = this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (this.keys == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                this.keys[indexToRemove] = free;
                                            }
                                        }
                                    } else if (indexToRemove == index) {
                                        this.index = ++index;
                                    }
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + index) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(curKey);
                    }
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }




    class NoRemovedValueIterator implements FloatIterator {
        short[] keys;
        int[] vals;
        final short free;
        final int capacityMask;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        float next;

        NoRemovedValueIterator(int mc) {
            expectedModCount = mc;
            short[] keys = this.keys = set;
            capacityMask = keys.length - 1;
            int[] vals = this.vals = values;
            short free = this.free = freeValue;
            int nextI = keys.length;
            while (--nextI >= 0) {
                if (keys[nextI] != free) {
                    next = Float.intBitsToFloat(vals[nextI]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public float nextFloat() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                if (expectedModCount == modCount()) {
                    index = nextI;
                    short[] keys = this.keys;
                    short free = this.free;
                    float prev = next;
                    while (--nextI >= 0) {
                        if (keys[nextI] != free) {
                            next = Float.intBitsToFloat(vals[nextI]);
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
        public void forEachRemaining(Consumer<? super Float> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            short[] keys = this.keys;
            int[] vals = this.vals;
            short free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(Float.intBitsToFloat(vals[i]));
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public void forEachRemaining(FloatConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            short[] keys = this.keys;
            int[] vals = this.vals;
            short free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(Float.intBitsToFloat(vals[i]));
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
        public Float next() {
            return nextFloat();
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    short[] keys = this.keys;
                    int[] vals = this.vals;
                    if (keys == set) {
                        int capacityMask = this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (this.keys == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = nextIndex + 1) > 0) {
                                            this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                this.keys[indexToRemove] = free;
                                            }
                                        }
                                    } else if (indexToRemove == index) {
                                        this.nextIndex = index;
                                        if (indexToShift < index - 1) {
                                            this.next = Float.intBitsToFloat(vals[indexToShift]);
                                        }
                                    }
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + index) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(keys[index]);
                    }
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class NoRemovedValueCursor implements FloatCursor {
        short[] keys;
        int[] vals;
        final short free;
        final int capacityMask;
        int expectedModCount;
        int index;
        short curKey;
        int curValue;

        NoRemovedValueCursor(int mc) {
            expectedModCount = mc;
            short[] keys = this.keys = set;
            capacityMask = keys.length - 1;
            index = keys.length;
            vals = values;
            short free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(FloatConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            short[] keys = this.keys;
            int[] vals = this.vals;
            short free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(Float.intBitsToFloat(vals[i]));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public float elem() {
            if (curKey != free) {
                return Float.intBitsToFloat(curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                short[] keys = this.keys;
                short free = this.free;
                for (int i = index - 1; i >= 0; i--) {
                    short key;
                    if ((key = keys[i]) != free) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
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
            short curKey;
            short free;
            if ((curKey = this.curKey) != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    int index = this.index;
                    short[] keys = this.keys;
                    int[] vals = this.vals;
                    if (keys == set) {
                        int capacityMask = this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (this.keys == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                this.keys[indexToRemove] = free;
                                            }
                                        }
                                    } else if (indexToRemove == index) {
                                        this.index = ++index;
                                    }
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + index) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(curKey);
                    }
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }



    class NoRemovedMapCursor implements ShortFloatCursor {
        short[] keys;
        int[] vals;
        final short free;
        final int capacityMask;
        int expectedModCount;
        int index;
        short curKey;
        int curValue;

        NoRemovedMapCursor(int mc) {
            expectedModCount = mc;
            short[] keys = this.keys = set;
            capacityMask = keys.length - 1;
            index = keys.length;
            vals = values;
            short free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(ShortFloatConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            short[] keys = this.keys;
            int[] vals = this.vals;
            short free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                short key;
                if ((key = keys[i]) != free) {
                    action.accept(key, Float.intBitsToFloat(vals[i]));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public short key() {
            short curKey;
            if ((curKey = this.curKey) != free) {
                return curKey;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public float value() {
            if (curKey != free) {
                return Float.intBitsToFloat(curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(float value) {
            if (curKey != free) {
                if (expectedModCount == modCount()) {
                    vals[index] = Float.floatToIntBits(value);
                    if (vals != values) {
                        values[index] = Float.floatToIntBits(value);
                    }
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
                short[] keys = this.keys;
                short free = this.free;
                for (int i = index - 1; i >= 0; i--) {
                    short key;
                    if ((key = keys[i]) != free) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
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
            short curKey;
            short free;
            if ((curKey = this.curKey) != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    int index = this.index;
                    short[] keys = this.keys;
                    int[] vals = this.vals;
                    if (keys == set) {
                        int capacityMask = this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            short keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            }
                            if (((SeparateKVShortKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (this.keys == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                this.keys[indexToRemove] = free;
                                            }
                                        }
                                    } else if (indexToRemove == index) {
                                        this.index = ++index;
                                    }
                                }
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == 1 + index) {
                                    throw new java.util.ConcurrentModificationException();
                                }
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(curKey);
                    }
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }
}

