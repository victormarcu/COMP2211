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
import net.openhft.koloboke.function.FloatPredicate;
import net.openhft.koloboke.function.FloatShortConsumer;
import net.openhft.koloboke.function.FloatShortPredicate;
import net.openhft.koloboke.function.FloatShortToShortFunction;
import net.openhft.koloboke.function.FloatToShortFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.openhft.koloboke.function.ShortBinaryOperator;
import net.openhft.koloboke.function.ShortConsumer;
import net.openhft.koloboke.function.ShortPredicate;

import javax.annotation.Nonnull;
import java.util.*;


public class ImmutableLHashSeparateKVFloatShortMapGO
        extends ImmutableLHashSeparateKVFloatShortMapSO {

    @Override
    final void copy(SeparateKVFloatShortLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    @Override
    final void move(SeparateKVFloatShortLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }


    @Override
    public short defaultValue() {
        return (short) 0;
    }

    @Override
    public boolean containsEntry(float key, short value) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            return values[index] == value;
        } else {
            // key is absent
            return false;
        }
    }

    @Override
    public boolean containsEntry(int key, short value) {
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
    public Short get(Object key) {
        int k = Float.floatToIntBits((Float) key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return null;
        }
    }

    

    @Override
    public short get(float key) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Short getOrDefault(Object key, Short defaultValue) {
        int k = Float.floatToIntBits((Float) key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public short getOrDefault(float key, short defaultValue) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public void forEach(BiConsumer<? super Float, ? super Short> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int[] keys = set;
        short[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                action.accept(Float.intBitsToFloat(key), vals[i]);
            }
        }
    }

    @Override
    public void forEach(FloatShortConsumer action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int[] keys = set;
        short[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                action.accept(Float.intBitsToFloat(key), vals[i]);
            }
        }
    }

    @Override
    public boolean forEachWhile(FloatShortPredicate predicate) {
        if (predicate == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return true;
        boolean terminated = false;
        int[] keys = set;
        short[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                if (!predicate.test(Float.intBitsToFloat(key), vals[i])) {
                    terminated = true;
                    break;
                }
            }
        }
        return !terminated;
    }

    @Nonnull
    @Override
    public FloatShortCursor cursor() {
        
        return new NoRemovedMapCursor();
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonFloatShortMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalFloatShortMapOps m) {
        if (isEmpty())
            return true;
        boolean containsAll = true;
        int[] keys = set;
        short[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                if (!m.containsEntry(key, vals[i])) {
                    containsAll = false;
                    break;
                }
            }
        }
        return containsAll;
    }

    @Override
    public void reversePutAllTo(InternalFloatShortMapOps m) {
        if (isEmpty())
            return;
        int[] keys = set;
        short[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                m.justPut(key, vals[i]);
            }
        }
    }

    @Override
    @Nonnull
    public HashObjSet<Map.Entry<Float, Short>> entrySet() {
        return new EntryView();
    }

    @Override
    @Nonnull
    public ShortCollection values() {
        return new ValueView();
    }


    @Override
    public boolean equals(Object o) {
        return CommonMapOps.equals(this, o);
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        int[] keys = set;
        short[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                hashCode += key ^ vals[i];
            }
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (isEmpty())
            return "{}";
        StringBuilder sb = new StringBuilder();
        int elementCount = 0;
        int[] keys = set;
        short[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                sb.append(' ');
                sb.append(Float.intBitsToFloat(key));
                sb.append('=');
                sb.append(vals[i]);
                sb.append(',');
                if (++elementCount == 8) {
                    int expectedLength = sb.length() * (size() / 8);
                    sb.ensureCapacity(expectedLength + (expectedLength / 2));
                }
            }
        }
        sb.setCharAt(0, '{');
        sb.setCharAt(sb.length() - 1, '}');
        return sb.toString();
    }




    @Override
    public Short put(Float key, Short value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public short put(float key, short value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public Short putIfAbsent(Float key, Short value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public short putIfAbsent(float key, short value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public void justPut(float key, short value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public void justPut(int key, short value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public Short compute(Float key,
            BiFunction<? super Float, ? super Short, ? extends Short> remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public short compute(float key, FloatShortToShortFunction remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public Short computeIfAbsent(Float key,
            Function<? super Float, ? extends Short> mappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public short computeIfAbsent(float key, FloatToShortFunction mappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public Short computeIfPresent(Float key,
            BiFunction<? super Float, ? super Short, ? extends Short> remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public short computeIfPresent(float key, FloatShortToShortFunction remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public Short merge(Float key, Short value,
            BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public short merge(float key, short value, ShortBinaryOperator remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public short addValue(float key, short value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public short addValue(float key, short addition, short defaultValue) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public void putAll(@Nonnull Map<? extends Float, ? extends Short> m) {
        CommonFloatShortMapOps.putAll(this, m);
    }


    @Override
    public Short replace(Float key, Short value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public short replace(float key, short value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean replace(Float key, Short oldValue, Short newValue) {
        return replace(key.floatValue(),
                oldValue.shortValue(),
                newValue.shortValue());
    }

    @Override
    public boolean replace(float key, short oldValue, short newValue) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public void replaceAll(
            BiFunction<? super Float, ? super Short, ? extends Short> function) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public void replaceAll(FloatShortToShortFunction function) {
        throw new java.lang.UnsupportedOperationException();
    }





    @Override
    public Short remove(Object key) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean justRemove(float key) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean justRemove(int key) {
        throw new java.lang.UnsupportedOperationException();
    }


    

    @Override
    public short remove(float key) {
        throw new java.lang.UnsupportedOperationException();
    }



    @Override
    public boolean remove(Object key, Object value) {
        return remove(((Float) key).floatValue(),
                ((Short) value).shortValue()
                );
    }

    @Override
    public boolean remove(float key, short value) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean removeIf(FloatShortPredicate filter) {
        throw new java.lang.UnsupportedOperationException();
    }






    class EntryView extends AbstractSetView<Map.Entry<Float, Short>>
            implements HashObjSet<Map.Entry<Float, Short>>,
            InternalObjCollectionOps<Map.Entry<Float, Short>> {

        @Nonnull
        @Override
        public Equivalence<Entry<Float, Short>> equivalence() {
            return Equivalence.entryEquivalence(
                    Equivalence.<Float>defaultEquality()
                    ,
                    Equivalence.<Short>defaultEquality()
                    
            );
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return ImmutableLHashSeparateKVFloatShortMapGO.this.hashConfig();
        }


        @Override
        public int size() {
            return ImmutableLHashSeparateKVFloatShortMapGO.this.size();
        }

        @Override
        public double currentLoad() {
            return ImmutableLHashSeparateKVFloatShortMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Float, Short> e = (Map.Entry<Float, Short>) o;
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
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    result[resultIndex++] = new ImmutableEntry(key, vals[i]);
                }
            }
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
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    a[resultIndex++] = (T) new ImmutableEntry(key, vals[i]);
                }
            }
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public final void forEach(@Nonnull Consumer<? super Map.Entry<Float, Short>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    action.accept(new ImmutableEntry(key, vals[i]));
                }
            }
        }

        @Override
        public boolean forEachWhile(@Nonnull  Predicate<? super Map.Entry<Float, Short>> predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    if (!predicate.test(new ImmutableEntry(key, vals[i]))) {
                        terminated = true;
                        break;
                    }
                }
            }
            return !terminated;
        }

        @Override
        @Nonnull
        public ObjIterator<Map.Entry<Float, Short>> iterator() {
            
            return new NoRemovedEntryIterator();
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Float, Short>> cursor() {
            
            return new NoRemovedEntryCursor();
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
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    if (!c.contains(e.with(key, vals[i]))) {
                        containsAll = false;
                        break;
                    }
                }
            }
            return containsAll;
        }

        @Override
        public boolean reverseRemoveAllFrom(ObjSet<?> s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            ReusableEntry e = new ReusableEntry();
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    changed |= s.remove(e.with(key, vals[i]));
                }
            }
            return changed;
        }

        @Override
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Float, Short>> c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    changed |= c.add(new ImmutableEntry(key, vals[i]));
                }
            }
            return changed;
        }


        public int hashCode() {
            return ImmutableLHashSeparateKVFloatShortMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    sb.append(' ');
                    sb.append(Float.intBitsToFloat(key));
                    sb.append('=');
                    sb.append(vals[i]);
                    sb.append(',');
                    if (++elementCount == 8) {
                        int expectedLength = sb.length() * (size() / 8);
                        sb.ensureCapacity(expectedLength + (expectedLength / 2));
                    }
                }
            }
            sb.setCharAt(0, '[');
            sb.setCharAt(sb.length() - 1, ']');
            return sb.toString();
        }

        @Override
        public boolean shrink() {
            return ImmutableLHashSeparateKVFloatShortMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Float, Short> e = (Map.Entry<Float, Short>) o;
                float key = e.getKey();
                short value = e.getValue();
                return ImmutableLHashSeparateKVFloatShortMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf(@Nonnull Predicate<? super Map.Entry<Float, Short>> filter) {
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
            ImmutableLHashSeparateKVFloatShortMapGO.this.clear();
        }
    }


    abstract class FloatShortEntry extends AbstractEntry<Float, Short> {

        abstract int key();

        @Override
        public final Float getKey() {
            return Float.intBitsToFloat(key());
        }

        abstract short value();

        @Override
        public final Short getValue() {
            return value();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            int k2;
            short v2;
            try {
                e2 = (Map.Entry) o;
                k2 = Float.floatToIntBits((Float) e2.getKey());
                v2 = (Short) e2.getValue();
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


    private class ImmutableEntry extends FloatShortEntry {
        private final int key;
        private final short value;

        ImmutableEntry(int key, short value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int key() {
            return key;
        }

        @Override
        public short value() {
            return value;
        }
    }


    class ReusableEntry extends FloatShortEntry {
        private int key;
        private short value;

        ReusableEntry with(int key, short value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public int key() {
            return key;
        }

        @Override
        public short value() {
            return value;
        }
    }


    class ValueView extends AbstractShortValueView {


        @Override
        public int size() {
            return ImmutableLHashSeparateKVFloatShortMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return ImmutableLHashSeparateKVFloatShortMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return ImmutableLHashSeparateKVFloatShortMapGO.this.containsValue(o);
        }

        @Override
        public boolean contains(short v) {
            return ImmutableLHashSeparateKVFloatShortMapGO.this.containsValue(v);
        }



        @Override
        public void forEach(Consumer<? super Short> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    action.accept(vals[i]);
                }
            }
        }

        @Override
        public void forEach(ShortConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    action.accept(vals[i]);
                }
            }
        }

        @Override
        public boolean forEachWhile(ShortPredicate predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    if (!predicate.test(vals[i])) {
                        terminated = true;
                        break;
                    }
                }
            }
            return !terminated;
        }

        @Override
        public boolean allContainingIn(ShortCollection c) {
            if (isEmpty())
                return true;
            boolean containsAll = true;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    if (!c.contains(vals[i])) {
                        containsAll = false;
                        break;
                    }
                }
            }
            return containsAll;
        }


        @Override
        public boolean reverseAddAllTo(ShortCollection c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    changed |= c.add(vals[i]);
                }
            }
            return changed;
        }


        @Override
        public boolean reverseRemoveAllFrom(ShortSet s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    changed |= s.removeShort(vals[i]);
                }
            }
            return changed;
        }



        @Override
        @Nonnull
        public ShortIterator iterator() {
            
            return new NoRemovedValueIterator();
        }

        @Nonnull
        @Override
        public ShortCursor cursor() {
            
            return new NoRemovedValueCursor();
        }

        @Override
        @Nonnull
        public Object[] toArray() {
            int size = size();
            Object[] result = new Object[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    result[resultIndex++] = vals[i];
                }
            }
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
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    a[resultIndex++] = (T) Short.valueOf(vals[i]);
                }
            }
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public short[] toShortArray() {
            int size = size();
            short[] result = new short[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    result[resultIndex++] = vals[i];
                }
            }
            return result;
        }

        @Override
        public short[] toArray(short[] a) {
            int size = size();
            if (a.length < size)
                a = new short[size];
            if (size == 0) {
                if (a.length > 0)
                    a[0] = (short) 0;
                return a;
            }
            int resultIndex = 0;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    a[resultIndex++] = vals[i];
                }
            }
            if (a.length > resultIndex)
                a[resultIndex] = (short) 0;
            return a;
        }


        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int[] keys = set;
            short[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    sb.append(' ').append(vals[i]).append(',');
                    if (++elementCount == 8) {
                        int expectedLength = sb.length() * (size() / 8);
                        sb.ensureCapacity(expectedLength + (expectedLength / 2));
                    }
                }
            }
            sb.setCharAt(0, '[');
            sb.setCharAt(sb.length() - 1, ']');
            return sb.toString();
        }


        @Override
        public boolean remove(Object o) {
            return removeShort(( Short ) o);
        }

        @Override
        public boolean removeShort(short v) {
            return removeValue(v);
        }



        @Override
        public void clear() {
            ImmutableLHashSeparateKVFloatShortMapGO.this.clear();
        }

        
        public boolean removeIf(Predicate<? super Short> filter) {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(ShortPredicate filter) {
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



    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Float, Short>> {
        final int[] keys;
        final short[] vals;
        int nextIndex;
        ImmutableEntry next;

        NoRemovedEntryIterator() {
            int[] keys = this.keys = set;
            short[] vals = this.vals = values;
            int nextI = keys.length;
            while (--nextI >= 0) {
                int key;
                if ((key = keys[nextI]) < FREE_BITS) {
                    next = new ImmutableEntry(key, vals[nextI]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Float, Short>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int[] keys = this.keys;
            short[] vals = this.vals;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    action.accept(new ImmutableEntry(key, vals[i]));
                }
            }
            if (nextI != nextIndex) {
                throw new java.util.ConcurrentModificationException();
            }
            nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public Map.Entry<Float, Short> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                int[] keys = this.keys;
                ImmutableEntry prev = next;
                while (--nextI >= 0) {
                    int key;
                    if ((key = keys[nextI]) < FREE_BITS) {
                        next = new ImmutableEntry(key, vals[nextI]);
                        break;
                    }
                }
                nextIndex = nextI;
                return prev;
            } else {
                throw new java.util.NoSuchElementException();
            }
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Float, Short>> {
        final int[] keys;
        final short[] vals;
        int index;
        int curKey;
        short curValue;

        NoRemovedEntryCursor() {
            this.keys = set;
            index = keys.length;
            vals = values;
            curKey = FREE_BITS;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Float, Short>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int[] keys = this.keys;
            short[] vals = this.vals;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    action.accept(new ImmutableEntry(key, vals[i]));
                }
            }
            if (index != this.index) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = FREE_BITS;
        }

        @Override
        public Map.Entry<Float, Short> elem() {
            int curKey;
            if ((curKey = this.curKey) != FREE_BITS) {
                return new ImmutableEntry(curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            int[] keys = this.keys;
            for (int i = index - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    index = i;
                    curKey = key;
                    curValue = vals[i];
                    return true;
                }
            }
            curKey = FREE_BITS;
            index = -1;
            return false;
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }




    class NoRemovedValueIterator implements ShortIterator {
        final int[] keys;
        final short[] vals;
        int nextIndex;
        short next;

        NoRemovedValueIterator() {
            int[] keys = this.keys = set;
            short[] vals = this.vals = values;
            int nextI = keys.length;
            while (--nextI >= 0) {
                if (keys[nextI] < FREE_BITS) {
                    next = vals[nextI];
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public short nextShort() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                int[] keys = this.keys;
                short prev = next;
                while (--nextI >= 0) {
                    if (keys[nextI] < FREE_BITS) {
                        next = vals[nextI];
                        break;
                    }
                }
                nextIndex = nextI;
                return prev;
            } else {
                throw new java.util.NoSuchElementException();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super Short> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int[] keys = this.keys;
            short[] vals = this.vals;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    action.accept(vals[i]);
                }
            }
            if (nextI != nextIndex) {
                throw new java.util.ConcurrentModificationException();
            }
            nextIndex = -1;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int[] keys = this.keys;
            short[] vals = this.vals;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    action.accept(vals[i]);
                }
            }
            if (nextI != nextIndex) {
                throw new java.util.ConcurrentModificationException();
            }
            nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public Short next() {
            return nextShort();
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }


    class NoRemovedValueCursor implements ShortCursor {
        final int[] keys;
        final short[] vals;
        int index;
        int curKey;
        short curValue;

        NoRemovedValueCursor() {
            this.keys = set;
            index = keys.length;
            vals = values;
            curKey = FREE_BITS;
        }

        @Override
        public void forEachForward(ShortConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int[] keys = this.keys;
            short[] vals = this.vals;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    action.accept(vals[i]);
                }
            }
            if (index != this.index) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = FREE_BITS;
        }

        @Override
        public short elem() {
            if (curKey != FREE_BITS) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            int[] keys = this.keys;
            for (int i = index - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    index = i;
                    curKey = key;
                    curValue = vals[i];
                    return true;
                }
            }
            curKey = FREE_BITS;
            index = -1;
            return false;
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }



    class NoRemovedMapCursor implements FloatShortCursor {
        final int[] keys;
        final short[] vals;
        int index;
        int curKey;
        short curValue;

        NoRemovedMapCursor() {
            this.keys = set;
            index = keys.length;
            vals = values;
            curKey = FREE_BITS;
        }

        @Override
        public void forEachForward(FloatShortConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int[] keys = this.keys;
            short[] vals = this.vals;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    action.accept(Float.intBitsToFloat(key), vals[i]);
                }
            }
            if (index != this.index) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = FREE_BITS;
        }

        @Override
        public float key() {
            int curKey;
            if ((curKey = this.curKey) != FREE_BITS) {
                return Float.intBitsToFloat(curKey);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public short value() {
            if (curKey != FREE_BITS) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(short value) {
            if (curKey != FREE_BITS) {
                vals[index] = value;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            int[] keys = this.keys;
            for (int i = index - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    index = i;
                    curKey = key;
                    curValue = vals[i];
                    return true;
                }
            }
            curKey = FREE_BITS;
            index = -1;
            return false;
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }
}

