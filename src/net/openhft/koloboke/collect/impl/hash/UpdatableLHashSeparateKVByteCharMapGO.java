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
import net.openhft.koloboke.function.BytePredicate;
import net.openhft.koloboke.function.ByteCharConsumer;
import net.openhft.koloboke.function.ByteCharPredicate;
import net.openhft.koloboke.function.ByteCharToCharFunction;
import net.openhft.koloboke.function.ByteToCharFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.openhft.koloboke.function.CharBinaryOperator;
import net.openhft.koloboke.function.CharConsumer;
import net.openhft.koloboke.function.CharPredicate;

import javax.annotation.Nonnull;
import java.util.*;


public class UpdatableLHashSeparateKVByteCharMapGO
        extends UpdatableLHashSeparateKVByteCharMapSO {

    @Override
    final void copy(SeparateKVByteCharLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    @Override
    final void move(SeparateKVByteCharLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }


    @Override
    public char defaultValue() {
        return (char) 0;
    }

    @Override
    public boolean containsEntry(byte key, char value) {
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
    public Character get(Object key) {
        int index = index((Byte) key);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return null;
        }
    }

    

    @Override
    public char get(byte key) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Character getOrDefault(Object key, Character defaultValue) {
        int index = index((Byte) key);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public char getOrDefault(byte key, char defaultValue) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public void forEach(BiConsumer<? super Byte, ? super Character> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        char[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                action.accept(key, vals[i]);
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void forEach(ByteCharConsumer action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        char[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                action.accept(key, vals[i]);
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public boolean forEachWhile(ByteCharPredicate predicate) {
        if (predicate == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return true;
        boolean terminated = false;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        char[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                if (!predicate.test(key, vals[i])) {
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
    public ByteCharCursor cursor() {
        int mc = modCount();
        return new NoRemovedMapCursor(mc);
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonByteCharMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalByteCharMapOps m) {
        if (isEmpty())
            return true;
        boolean containsAll = true;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        char[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
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
    public void reversePutAllTo(InternalByteCharMapOps m) {
        if (isEmpty())
            return;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        char[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                m.justPut(key, vals[i]);
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    @Nonnull
    public HashObjSet<Map.Entry<Byte, Character>> entrySet() {
        return new EntryView();
    }

    @Override
    @Nonnull
    public CharCollection values() {
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
        byte free = freeValue;
        byte[] keys = set;
        char[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
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
        byte free = freeValue;
        byte[] keys = set;
        char[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                sb.append(' ');
                sb.append(key);
                sb.append('=');
                sb.append(vals[i]);
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
        byte free = freeValue;
        byte[] keys = set;
        char[] vals = values;
        initForRehash(newCapacity);
        mc++; // modCount is incremented in initForRehash()
        byte[] newKeys = set;
        int capacityMask = newKeys.length - 1;
        char[] newVals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                int index;
                if (newKeys[index = SeparateKVByteKeyMixing.mix(key) & capacityMask] != free) {
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
    public Character put(Byte key, Character value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            char[] vals = values;
            char prevValue = vals[index];
            vals[index] = value;
            return prevValue;
        }
    }

    @Override
    public char put(byte key, char value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return defaultValue();
        } else {
            // key is present
            char[] vals = values;
            char prevValue = vals[index];
            vals[index] = value;
            return prevValue;
        }
    }

    @Override
    public Character putIfAbsent(Byte key, Character value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            return values[index];
        }
    }

    @Override
    public char putIfAbsent(byte key, char value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return defaultValue();
        } else {
            // key is present
            return values[index];
        }
    }

    @Override
    public void justPut(byte key, char value) {
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
    public Character compute(Byte key,
            BiFunction<? super Byte, ? super Character, ? extends Character> remappingFunction) {
        byte k = key;
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        byte free;
        if (k == (free = freeValue)) {
            free = changeFree();
        }
        byte[] keys = set;
        char[] vals = values;
        int capacityMask, index;
        byte cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVByteKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) != k) {
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
            Character newValue = remappingFunction.apply(k, null);
            if (newValue != null) {
                incrementModCount();
                keys[index] = k;
                vals[index] = newValue;
                postInsertHook();
                return newValue;
            } else {
                return null;
            }
        }
        // key is present
        Character newValue = remappingFunction.apply(k, vals[index]);
        if (newValue != null) {
            vals[index] = newValue;
            return newValue;
        } else {
            throw new java.lang.UnsupportedOperationException("Compute operation of updatable map doesn't support removals");
        }
    }


    @Override
    public char compute(byte key, ByteCharToCharFunction remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        byte free;
        if (key == (free = freeValue)) {
            free = changeFree();
        }
        byte[] keys = set;
        char[] vals = values;
        int capacityMask, index;
        byte cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVByteKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) != key) {
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
            char newValue = remappingFunction.applyAsChar(key, defaultValue());
            incrementModCount();
            keys[index] = key;
            vals[index] = newValue;
            postInsertHook();
            return newValue;
        }
        // key is present
        char newValue = remappingFunction.applyAsChar(key, vals[index]);
        vals[index] = newValue;
        return newValue;
    }


    @Override
    public Character computeIfAbsent(Byte key,
            Function<? super Byte, ? extends Character> mappingFunction) {
        byte k = key;
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        byte free;
        if (k == (free = freeValue)) {
            free = changeFree();
        }
        byte[] keys = set;
        char[] vals = values;
        int capacityMask, index;
        byte cur;
        if ((cur = keys[index = SeparateKVByteKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) == k) {
            // key is present
            return vals[index];
        } else {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        // key is present
                        return vals[index];
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            Character value = mappingFunction.apply(k);
            if (value != null) {
                incrementModCount();
                keys[index] = k;
                vals[index] = value;
                postInsertHook();
                return value;
            } else {
                return null;
            }
        }
    }


    @Override
    public char computeIfAbsent(byte key, ByteToCharFunction mappingFunction) {
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        byte free;
        if (key == (free = freeValue)) {
            free = changeFree();
        }
        byte[] keys = set;
        char[] vals = values;
        int capacityMask, index;
        byte cur;
        if ((cur = keys[index = SeparateKVByteKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) == key) {
            // key is present
            return vals[index];
        } else {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        // key is present
                        return vals[index];
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            char value = mappingFunction.applyAsChar(key);
            incrementModCount();
            keys[index] = key;
            vals[index] = value;
            postInsertHook();
            return value;
        }
    }


    @Override
    public Character computeIfPresent(Byte key,
            BiFunction<? super Byte, ? super Character, ? extends Character> remappingFunction) {
        byte k = key;
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(k);
        if (index >= 0) {
            // key is present
            char[] vals = values;
            Character newValue = remappingFunction.apply(k, vals[index]);
            if (newValue != null) {
                vals[index] = newValue;
                return newValue;
            } else {
                throw new java.lang.UnsupportedOperationException("ComputeIfPresent operation of updatable map doesn't support removals");
            }
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public char computeIfPresent(byte key, ByteCharToCharFunction remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(key);
        if (index >= 0) {
            // key is present
            char[] vals = values;
            char newValue = remappingFunction.applyAsChar(key, vals[index]);
            vals[index] = newValue;
            return newValue;
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Character merge(Byte key, Character value,
            BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
        byte k = key;
        if (value == null)
            throw new java.lang.NullPointerException();
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        byte free;
        if (k == (free = freeValue)) {
            free = changeFree();
        }
        byte[] keys = set;
        char[] vals = values;
        int capacityMask, index;
        byte cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVByteKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) != k) {
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
            vals[index] = value;
            postInsertHook();
            return value;
        }
        // key is present
        Character newValue = remappingFunction.apply(vals[index], value);
        if (newValue != null) {
            vals[index] = newValue;
            return newValue;
        } else {
            throw new java.lang.UnsupportedOperationException("Merge operation of updatable map doesn't support removals");
        }
    }


    @Override
    public char merge(byte key, char value, CharBinaryOperator remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        byte free;
        if (key == (free = freeValue)) {
            free = changeFree();
        }
        byte[] keys = set;
        char[] vals = values;
        int capacityMask, index;
        byte cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVByteKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) != key) {
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
            vals[index] = value;
            postInsertHook();
            return value;
        }
        // key is present
        char newValue = remappingFunction.applyAsChar(vals[index], value);
        vals[index] = newValue;
        return newValue;
    }


    @Override
    public char addValue(byte key, char value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return value;
        } else {
            // key is present
            char[] vals = values;
            char newValue = (char) (vals[index] + value);
            vals[index] = newValue;
            return newValue;
        }
    }

    @Override
    public char addValue(byte key, char addition, char defaultValue) {
        char value = (char) (defaultValue + addition);
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return value;
        } else {
            // key is present
            char[] vals = values;
            char newValue = (char) (vals[index] + addition);
            vals[index] = newValue;
            return newValue;
        }
    }


    @Override
    public void putAll(@Nonnull Map<? extends Byte, ? extends Character> m) {
        CommonByteCharMapOps.putAll(this, m);
    }


    @Override
    public Character replace(Byte key, Character value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            char[] vals = values;
            char oldValue = vals[index];
            vals[index] = value;
            return oldValue;
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public char replace(byte key, char value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            char[] vals = values;
            char oldValue = vals[index];
            vals[index] = value;
            return oldValue;
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public boolean replace(Byte key, Character oldValue, Character newValue) {
        return replace(key.byteValue(),
                oldValue.charValue(),
                newValue.charValue());
    }

    @Override
    public boolean replace(byte key, char oldValue, char newValue) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            char[] vals = values;
            if (vals[index] == oldValue) {
                vals[index] = newValue;
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
            BiFunction<? super Byte, ? super Character, ? extends Character> function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        char[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                vals[i] = function.apply(key, vals[i]);
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void replaceAll(ByteCharToCharFunction function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        char[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                vals[i] = function.applyAsChar(key, vals[i]);
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
    public Character remove(Object key) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean justRemove(byte key) {
        throw new java.lang.UnsupportedOperationException();
    }



    

    @Override
    public char remove(byte key) {
        throw new java.lang.UnsupportedOperationException();
    }



    @Override
    public boolean remove(Object key, Object value) {
        return remove(((Byte) key).byteValue(),
                ((Character) value).charValue()
                );
    }

    @Override
    public boolean remove(byte key, char value) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean removeIf(ByteCharPredicate filter) {
        throw new java.lang.UnsupportedOperationException();
    }






    class EntryView extends AbstractSetView<Map.Entry<Byte, Character>>
            implements HashObjSet<Map.Entry<Byte, Character>>,
            InternalObjCollectionOps<Map.Entry<Byte, Character>> {

        @Nonnull
        @Override
        public Equivalence<Entry<Byte, Character>> equivalence() {
            return Equivalence.entryEquivalence(
                    Equivalence.<Byte>defaultEquality()
                    ,
                    Equivalence.<Character>defaultEquality()
                    
            );
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return UpdatableLHashSeparateKVByteCharMapGO.this.hashConfig();
        }


        @Override
        public int size() {
            return UpdatableLHashSeparateKVByteCharMapGO.this.size();
        }

        @Override
        public double currentLoad() {
            return UpdatableLHashSeparateKVByteCharMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Byte, Character> e = (Map.Entry<Byte, Character>) o;
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
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
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
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
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
        public final void forEach(@Nonnull Consumer<? super Map.Entry<Byte, Character>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(@Nonnull  Predicate<? super Map.Entry<Byte, Character>> predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
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
        public ObjIterator<Map.Entry<Byte, Character>> iterator() {
            int mc = modCount();
            return new NoRemovedEntryIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Byte, Character>> cursor() {
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
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
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
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
                    changed |= s.remove(e.with(key, vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Byte, Character>> c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
                    changed |= c.add(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        public int hashCode() {
            return UpdatableLHashSeparateKVByteCharMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
                    sb.append(' ');
                    sb.append(key);
                    sb.append('=');
                    sb.append(vals[i]);
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
            return UpdatableLHashSeparateKVByteCharMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Byte, Character> e = (Map.Entry<Byte, Character>) o;
                byte key = e.getKey();
                char value = e.getValue();
                return UpdatableLHashSeparateKVByteCharMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf(@Nonnull Predicate<? super Map.Entry<Byte, Character>> filter) {
            throw new java.lang.UnsupportedOperationException();
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
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public final boolean retainAll(@Nonnull Collection<?> c) {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public void clear() {
            UpdatableLHashSeparateKVByteCharMapGO.this.clear();
        }
    }


    abstract class ByteCharEntry extends AbstractEntry<Byte, Character> {

        abstract byte key();

        @Override
        public final Byte getKey() {
            return key();
        }

        abstract char value();

        @Override
        public final Character getValue() {
            return value();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            byte k2;
            char v2;
            try {
                e2 = (Map.Entry) o;
                k2 = (Byte) e2.getKey();
                v2 = (Character) e2.getValue();
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


    class MutableEntry extends ByteCharEntry {
        final int modCount;
        private final int index;
        final byte key;
        private char value;

        MutableEntry(int modCount, int index, byte key, char value) {
            this.modCount = modCount;
            this.index = index;
            this.key = key;
            this.value = value;
        }

        @Override
        public byte key() {
            return key;
        }

        @Override
        public char value() {
            return value;
        }

        @Override
        public Character setValue(Character newValue) {
            if (modCount != modCount())
                throw new IllegalStateException();
            char oldValue = value;
            char unwrappedNewValue = newValue;
            value = unwrappedNewValue;
            updateValueInTable(unwrappedNewValue);
            return oldValue;
        }

        void updateValueInTable(char newValue) {
            values[index] = newValue;
        }
    }



    class ReusableEntry extends ByteCharEntry {
        private byte key;
        private char value;

        ReusableEntry with(byte key, char value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public byte key() {
            return key;
        }

        @Override
        public char value() {
            return value;
        }
    }


    class ValueView extends AbstractCharValueView {


        @Override
        public int size() {
            return UpdatableLHashSeparateKVByteCharMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return UpdatableLHashSeparateKVByteCharMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return UpdatableLHashSeparateKVByteCharMapGO.this.containsValue(o);
        }

        @Override
        public boolean contains(char v) {
            return UpdatableLHashSeparateKVByteCharMapGO.this.containsValue(v);
        }



        @Override
        public void forEach(Consumer<? super Character> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public void forEach(CharConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(CharPredicate predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    if (!predicate.test(vals[i])) {
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
        public boolean allContainingIn(CharCollection c) {
            if (isEmpty())
                return true;
            boolean containsAll = true;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
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
        public boolean reverseAddAllTo(CharCollection c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
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
        public boolean reverseRemoveAllFrom(CharSet s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    changed |= s.removeChar(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }



        @Override
        @Nonnull
        public CharIterator iterator() {
            int mc = modCount();
            return new NoRemovedValueIterator(mc);
        }

        @Nonnull
        @Override
        public CharCursor cursor() {
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
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    result[resultIndex++] = vals[i];
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
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    a[resultIndex++] = (T) Character.valueOf(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public char[] toCharArray() {
            int size = size();
            char[] result = new char[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    result[resultIndex++] = vals[i];
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return result;
        }

        @Override
        public char[] toArray(char[] a) {
            int size = size();
            if (a.length < size)
                a = new char[size];
            if (size == 0) {
                if (a.length > 0)
                    a[0] = (char) 0;
                return a;
            }
            int resultIndex = 0;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    a[resultIndex++] = vals[i];
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = (char) 0;
            return a;
        }


        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            char[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    sb.append(' ').append(vals[i]).append(',');
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
            return removeChar(( Character ) o);
        }

        @Override
        public boolean removeChar(char v) {
            return removeValue(v);
        }



        @Override
        public void clear() {
            UpdatableLHashSeparateKVByteCharMapGO.this.clear();
        }

        
        public boolean removeIf(Predicate<? super Character> filter) {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(CharPredicate filter) {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            throw new java.lang.UnsupportedOperationException();
        }


        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            throw new java.lang.UnsupportedOperationException();
        }

    }



    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Byte, Character>> {
        final byte[] keys;
        final char[] vals;
        final byte free;
        int expectedModCount;
        int nextIndex;
        MutableEntry next;

        NoRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            byte[] keys = this.keys = set;
            char[] vals = this.vals = values;
            byte free = this.free = freeValue;
            int nextI = keys.length;
            while (--nextI >= 0) {
                byte key;
                if ((key = keys[nextI]) != free) {
                    next = new MutableEntry(mc, nextI, key, vals[nextI]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Byte, Character>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            byte[] keys = this.keys;
            char[] vals = this.vals;
            byte free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public Map.Entry<Byte, Character> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                int mc;
                if ((mc = expectedModCount) == modCount()) {
                    byte[] keys = this.keys;
                    byte free = this.free;
                    MutableEntry prev = next;
                    while (--nextI >= 0) {
                        byte key;
                        if ((key = keys[nextI]) != free) {
                            next = new MutableEntry(mc, nextI, key, vals[nextI]);
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
            throw new java.lang.UnsupportedOperationException();
        }
    }


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Byte, Character>> {
        final byte[] keys;
        final char[] vals;
        final byte free;
        int expectedModCount;
        int index;
        byte curKey;
        char curValue;

        NoRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            byte free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Byte, Character>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            byte[] keys = this.keys;
            char[] vals = this.vals;
            byte free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public Map.Entry<Byte, Character> elem() {
            byte curKey;
            if ((curKey = this.curKey) != free) {
                return new MutableEntry(expectedModCount, index, curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                byte[] keys = this.keys;
                byte free = this.free;
                for (int i = index - 1; i >= 0; i--) {
                    byte key;
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
            throw new java.lang.UnsupportedOperationException();
        }
    }




    class NoRemovedValueIterator implements CharIterator {
        final byte[] keys;
        final char[] vals;
        final byte free;
        int expectedModCount;
        int nextIndex;
        char next;

        NoRemovedValueIterator(int mc) {
            expectedModCount = mc;
            byte[] keys = this.keys = set;
            char[] vals = this.vals = values;
            byte free = this.free = freeValue;
            int nextI = keys.length;
            while (--nextI >= 0) {
                if (keys[nextI] != free) {
                    next = vals[nextI];
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public char nextChar() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                if (expectedModCount == modCount()) {
                    byte[] keys = this.keys;
                    byte free = this.free;
                    char prev = next;
                    while (--nextI >= 0) {
                        if (keys[nextI] != free) {
                            next = vals[nextI];
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
        public void forEachRemaining(Consumer<? super Character> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            byte[] keys = this.keys;
            char[] vals = this.vals;
            byte free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(vals[i]);
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            nextIndex = -1;
        }

        @Override
        public void forEachRemaining(CharConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            byte[] keys = this.keys;
            char[] vals = this.vals;
            byte free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(vals[i]);
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public Character next() {
            return nextChar();
        }

        @Override
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }
    }


    class NoRemovedValueCursor implements CharCursor {
        final byte[] keys;
        final char[] vals;
        final byte free;
        int expectedModCount;
        int index;
        byte curKey;
        char curValue;

        NoRemovedValueCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            byte free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(CharConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            byte[] keys = this.keys;
            char[] vals = this.vals;
            byte free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(vals[i]);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public char elem() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                byte[] keys = this.keys;
                byte free = this.free;
                for (int i = index - 1; i >= 0; i--) {
                    byte key;
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
            throw new java.lang.UnsupportedOperationException();
        }
    }



    class NoRemovedMapCursor implements ByteCharCursor {
        final byte[] keys;
        final char[] vals;
        final byte free;
        int expectedModCount;
        int index;
        byte curKey;
        char curValue;

        NoRemovedMapCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            byte free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(ByteCharConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            byte[] keys = this.keys;
            char[] vals = this.vals;
            byte free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
                    action.accept(key, vals[i]);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public byte key() {
            byte curKey;
            if ((curKey = this.curKey) != free) {
                return curKey;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public char value() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(char value) {
            if (curKey != free) {
                if (expectedModCount == modCount()) {
                    vals[index] = value;
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
                byte[] keys = this.keys;
                byte free = this.free;
                for (int i = index - 1; i >= 0; i--) {
                    byte key;
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
            throw new java.lang.UnsupportedOperationException();
        }
    }
}

