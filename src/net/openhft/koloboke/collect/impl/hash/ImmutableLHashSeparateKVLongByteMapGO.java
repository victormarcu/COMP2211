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
import net.openhft.koloboke.function.LongByteConsumer;
import net.openhft.koloboke.function.LongBytePredicate;
import net.openhft.koloboke.function.LongByteToByteFunction;
import net.openhft.koloboke.function.LongToByteFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.openhft.koloboke.function.ByteBinaryOperator;
import net.openhft.koloboke.function.ByteConsumer;
import net.openhft.koloboke.function.BytePredicate;

import javax.annotation.Nonnull;
import java.util.*;


public class ImmutableLHashSeparateKVLongByteMapGO
        extends ImmutableLHashSeparateKVLongByteMapSO {

    @Override
    final void copy(SeparateKVLongByteLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    @Override
    final void move(SeparateKVLongByteLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }


    @Override
    public byte defaultValue() {
        return (byte) 0;
    }

    @Override
    public boolean containsEntry(long key, byte value) {
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
    public Byte get(Object key) {
        int index = index((Long) key);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return null;
        }
    }

    

    @Override
    public byte get(long key) {
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
    public Byte getOrDefault(Object key, Byte defaultValue) {
        int index = index((Long) key);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public byte getOrDefault(long key, byte defaultValue) {
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
    public void forEach(BiConsumer<? super Long, ? super Byte> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        long free = freeValue;
        long[] keys = set;
        byte[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                action.accept(key, vals[i]);
            }
        }
    }

    @Override
    public void forEach(LongByteConsumer action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        long free = freeValue;
        long[] keys = set;
        byte[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                action.accept(key, vals[i]);
            }
        }
    }

    @Override
    public boolean forEachWhile(LongBytePredicate predicate) {
        if (predicate == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return true;
        boolean terminated = false;
        long free = freeValue;
        long[] keys = set;
        byte[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                if (!predicate.test(key, vals[i])) {
                    terminated = true;
                    break;
                }
            }
        }
        return !terminated;
    }

    @Nonnull
    @Override
    public LongByteCursor cursor() {
        
        return new NoRemovedMapCursor();
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonLongByteMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalLongByteMapOps m) {
        if (isEmpty())
            return true;
        boolean containsAll = true;
        long free = freeValue;
        long[] keys = set;
        byte[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                if (!m.containsEntry(key, vals[i])) {
                    containsAll = false;
                    break;
                }
            }
        }
        return containsAll;
    }

    @Override
    public void reversePutAllTo(InternalLongByteMapOps m) {
        if (isEmpty())
            return;
        long free = freeValue;
        long[] keys = set;
        byte[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                m.justPut(key, vals[i]);
            }
        }
    }

    @Override
    @Nonnull
    public HashObjSet<Map.Entry<Long, Byte>> entrySet() {
        return new EntryView();
    }

    @Override
    @Nonnull
    public ByteCollection values() {
        return new ValueView();
    }


    @Override
    public boolean equals(Object o) {
        return CommonMapOps.equals(this, o);
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        long free = freeValue;
        long[] keys = set;
        byte[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                hashCode += ((int) (key ^ (key >>> 32))) ^ vals[i];
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
        long free = freeValue;
        long[] keys = set;
        byte[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            long key;
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
        sb.setCharAt(0, '{');
        sb.setCharAt(sb.length() - 1, '}');
        return sb.toString();
    }




    @Override
    public Byte put(Long key, Byte value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public byte put(long key, byte value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public Byte putIfAbsent(Long key, Byte value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public byte putIfAbsent(long key, byte value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public void justPut(long key, byte value) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public Byte compute(Long key,
            BiFunction<? super Long, ? super Byte, ? extends Byte> remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public byte compute(long key, LongByteToByteFunction remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public Byte computeIfAbsent(Long key,
            Function<? super Long, ? extends Byte> mappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public byte computeIfAbsent(long key, LongToByteFunction mappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public Byte computeIfPresent(Long key,
            BiFunction<? super Long, ? super Byte, ? extends Byte> remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public byte computeIfPresent(long key, LongByteToByteFunction remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public Byte merge(Long key, Byte value,
            BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public byte merge(long key, byte value, ByteBinaryOperator remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public byte addValue(long key, byte value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public byte addValue(long key, byte addition, byte defaultValue) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public void putAll(@Nonnull Map<? extends Long, ? extends Byte> m) {
        CommonLongByteMapOps.putAll(this, m);
    }


    @Override
    public Byte replace(Long key, Byte value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public byte replace(long key, byte value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean replace(Long key, Byte oldValue, Byte newValue) {
        return replace(key.longValue(),
                oldValue.byteValue(),
                newValue.byteValue());
    }

    @Override
    public boolean replace(long key, byte oldValue, byte newValue) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public void replaceAll(
            BiFunction<? super Long, ? super Byte, ? extends Byte> function) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public void replaceAll(LongByteToByteFunction function) {
        throw new java.lang.UnsupportedOperationException();
    }





    @Override
    public Byte remove(Object key) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean justRemove(long key) {
        throw new java.lang.UnsupportedOperationException();
    }



    

    @Override
    public byte remove(long key) {
        throw new java.lang.UnsupportedOperationException();
    }



    @Override
    public boolean remove(Object key, Object value) {
        return remove(((Long) key).longValue(),
                ((Byte) value).byteValue()
                );
    }

    @Override
    public boolean remove(long key, byte value) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean removeIf(LongBytePredicate filter) {
        throw new java.lang.UnsupportedOperationException();
    }






    class EntryView extends AbstractSetView<Map.Entry<Long, Byte>>
            implements HashObjSet<Map.Entry<Long, Byte>>,
            InternalObjCollectionOps<Map.Entry<Long, Byte>> {

        @Nonnull
        @Override
        public Equivalence<Entry<Long, Byte>> equivalence() {
            return Equivalence.entryEquivalence(
                    Equivalence.<Long>defaultEquality()
                    ,
                    Equivalence.<Byte>defaultEquality()
                    
            );
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return ImmutableLHashSeparateKVLongByteMapGO.this.hashConfig();
        }


        @Override
        public int size() {
            return ImmutableLHashSeparateKVLongByteMapGO.this.size();
        }

        @Override
        public double currentLoad() {
            return ImmutableLHashSeparateKVLongByteMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Long, Byte> e = (Map.Entry<Long, Byte>) o;
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
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
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
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    a[resultIndex++] = (T) new ImmutableEntry(key, vals[i]);
                }
            }
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public final void forEach(@Nonnull Consumer<? super Map.Entry<Long, Byte>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(new ImmutableEntry(key, vals[i]));
                }
            }
        }

        @Override
        public boolean forEachWhile(@Nonnull  Predicate<? super Map.Entry<Long, Byte>> predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
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
        public ObjIterator<Map.Entry<Long, Byte>> iterator() {
            
            return new NoRemovedEntryIterator();
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Long, Byte>> cursor() {
            
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
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
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
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    changed |= s.remove(e.with(key, vals[i]));
                }
            }
            return changed;
        }

        @Override
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Long, Byte>> c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    changed |= c.add(new ImmutableEntry(key, vals[i]));
                }
            }
            return changed;
        }


        public int hashCode() {
            return ImmutableLHashSeparateKVLongByteMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                long key;
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
            sb.setCharAt(0, '[');
            sb.setCharAt(sb.length() - 1, ']');
            return sb.toString();
        }

        @Override
        public boolean shrink() {
            return ImmutableLHashSeparateKVLongByteMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Long, Byte> e = (Map.Entry<Long, Byte>) o;
                long key = e.getKey();
                byte value = e.getValue();
                return ImmutableLHashSeparateKVLongByteMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf(@Nonnull Predicate<? super Map.Entry<Long, Byte>> filter) {
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
            ImmutableLHashSeparateKVLongByteMapGO.this.clear();
        }
    }


    abstract class LongByteEntry extends AbstractEntry<Long, Byte> {

        abstract long key();

        @Override
        public final Long getKey() {
            return key();
        }

        abstract byte value();

        @Override
        public final Byte getValue() {
            return value();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            long k2;
            byte v2;
            try {
                e2 = (Map.Entry) o;
                k2 = (Long) e2.getKey();
                v2 = (Byte) e2.getValue();
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


    private class ImmutableEntry extends LongByteEntry {
        private final long key;
        private final byte value;

        ImmutableEntry(long key, byte value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public long key() {
            return key;
        }

        @Override
        public byte value() {
            return value;
        }
    }


    class ReusableEntry extends LongByteEntry {
        private long key;
        private byte value;

        ReusableEntry with(long key, byte value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public long key() {
            return key;
        }

        @Override
        public byte value() {
            return value;
        }
    }


    class ValueView extends AbstractByteValueView {


        @Override
        public int size() {
            return ImmutableLHashSeparateKVLongByteMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return ImmutableLHashSeparateKVLongByteMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return ImmutableLHashSeparateKVLongByteMapGO.this.containsValue(o);
        }

        @Override
        public boolean contains(byte v) {
            return ImmutableLHashSeparateKVLongByteMapGO.this.containsValue(v);
        }



        @Override
        public void forEach(Consumer<? super Byte> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(vals[i]);
                }
            }
        }

        @Override
        public void forEach(ByteConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(vals[i]);
                }
            }
        }

        @Override
        public boolean forEachWhile(BytePredicate predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    if (!predicate.test(vals[i])) {
                        terminated = true;
                        break;
                    }
                }
            }
            return !terminated;
        }

        @Override
        public boolean allContainingIn(ByteCollection c) {
            if (isEmpty())
                return true;
            boolean containsAll = true;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    if (!c.contains(vals[i])) {
                        containsAll = false;
                        break;
                    }
                }
            }
            return containsAll;
        }


        @Override
        public boolean reverseAddAllTo(ByteCollection c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    changed |= c.add(vals[i]);
                }
            }
            return changed;
        }


        @Override
        public boolean reverseRemoveAllFrom(ByteSet s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    changed |= s.removeByte(vals[i]);
                }
            }
            return changed;
        }



        @Override
        @Nonnull
        public ByteIterator iterator() {
            
            return new NoRemovedValueIterator();
        }

        @Nonnull
        @Override
        public ByteCursor cursor() {
            
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
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
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
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    a[resultIndex++] = (T) Byte.valueOf(vals[i]);
                }
            }
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public byte[] toByteArray() {
            int size = size();
            byte[] result = new byte[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    result[resultIndex++] = vals[i];
                }
            }
            return result;
        }

        @Override
        public byte[] toArray(byte[] a) {
            int size = size();
            if (a.length < size)
                a = new byte[size];
            if (size == 0) {
                if (a.length > 0)
                    a[0] = (byte) 0;
                return a;
            }
            int resultIndex = 0;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    a[resultIndex++] = vals[i];
                }
            }
            if (a.length > resultIndex)
                a[resultIndex] = (byte) 0;
            return a;
        }


        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            long free = freeValue;
            long[] keys = set;
            byte[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
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
            return removeByte(( Byte ) o);
        }

        @Override
        public boolean removeByte(byte v) {
            return removeValue(v);
        }



        @Override
        public void clear() {
            ImmutableLHashSeparateKVLongByteMapGO.this.clear();
        }

        
        public boolean removeIf(Predicate<? super Byte> filter) {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(BytePredicate filter) {
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



    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Long, Byte>> {
        final long[] keys;
        final byte[] vals;
        final long free;
        int nextIndex;
        ImmutableEntry next;

        NoRemovedEntryIterator() {
            long[] keys = this.keys = set;
            byte[] vals = this.vals = values;
            long free = this.free = freeValue;
            int nextI = keys.length;
            while (--nextI >= 0) {
                long key;
                if ((key = keys[nextI]) != free) {
                    next = new ImmutableEntry(key, vals[nextI]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Long, Byte>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            long[] keys = this.keys;
            byte[] vals = this.vals;
            long free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
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
        public Map.Entry<Long, Byte> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                long[] keys = this.keys;
                long free = this.free;
                ImmutableEntry prev = next;
                while (--nextI >= 0) {
                    long key;
                    if ((key = keys[nextI]) != free) {
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


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Long, Byte>> {
        final long[] keys;
        final byte[] vals;
        final long free;
        int index;
        long curKey;
        byte curValue;

        NoRemovedEntryCursor() {
            this.keys = set;
            index = keys.length;
            vals = values;
            long free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Long, Byte>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            long[] keys = this.keys;
            byte[] vals = this.vals;
            long free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(new ImmutableEntry(key, vals[i]));
                }
            }
            if (index != this.index) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public Map.Entry<Long, Byte> elem() {
            long curKey;
            if ((curKey = this.curKey) != free) {
                return new ImmutableEntry(curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            long[] keys = this.keys;
            long free = this.free;
            for (int i = index - 1; i >= 0; i--) {
                long key;
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
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }




    class NoRemovedValueIterator implements ByteIterator {
        final long[] keys;
        final byte[] vals;
        final long free;
        int nextIndex;
        byte next;

        NoRemovedValueIterator() {
            long[] keys = this.keys = set;
            byte[] vals = this.vals = values;
            long free = this.free = freeValue;
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
        public byte nextByte() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                long[] keys = this.keys;
                long free = this.free;
                byte prev = next;
                while (--nextI >= 0) {
                    if (keys[nextI] != free) {
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
        public void forEachRemaining(Consumer<? super Byte> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            long[] keys = this.keys;
            byte[] vals = this.vals;
            long free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(vals[i]);
                }
            }
            if (nextI != nextIndex) {
                throw new java.util.ConcurrentModificationException();
            }
            nextIndex = -1;
        }

        @Override
        public void forEachRemaining(ByteConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            long[] keys = this.keys;
            byte[] vals = this.vals;
            long free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if (keys[i] != free) {
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
        public Byte next() {
            return nextByte();
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }


    class NoRemovedValueCursor implements ByteCursor {
        final long[] keys;
        final byte[] vals;
        final long free;
        int index;
        long curKey;
        byte curValue;

        NoRemovedValueCursor() {
            this.keys = set;
            index = keys.length;
            vals = values;
            long free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(ByteConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            long[] keys = this.keys;
            byte[] vals = this.vals;
            long free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(vals[i]);
                }
            }
            if (index != this.index) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public byte elem() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            long[] keys = this.keys;
            long free = this.free;
            for (int i = index - 1; i >= 0; i--) {
                long key;
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
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }



    class NoRemovedMapCursor implements LongByteCursor {
        final long[] keys;
        final byte[] vals;
        final long free;
        int index;
        long curKey;
        byte curValue;

        NoRemovedMapCursor() {
            this.keys = set;
            index = keys.length;
            vals = values;
            long free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(LongByteConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            long[] keys = this.keys;
            byte[] vals = this.vals;
            long free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(key, vals[i]);
                }
            }
            if (index != this.index) {
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
        public byte value() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(byte value) {
            if (curKey != free) {
                vals[index] = value;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            long[] keys = this.keys;
            long free = this.free;
            for (int i = index - 1; i >= 0; i--) {
                long key;
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
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }
}

