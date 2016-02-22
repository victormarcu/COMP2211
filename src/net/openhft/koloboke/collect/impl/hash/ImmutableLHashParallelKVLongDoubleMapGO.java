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
import net.openhft.koloboke.function.LongDoubleConsumer;
import net.openhft.koloboke.function.LongDoublePredicate;
import net.openhft.koloboke.function.LongDoubleToDoubleFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;

import javax.annotation.Nonnull;
import java.util.*;


public class ImmutableLHashParallelKVLongDoubleMapGO
        extends ImmutableLHashParallelKVLongDoubleMapSO {

    
    final void copy(ParallelKVLongDoubleLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    
    final void move(ParallelKVLongDoubleLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }


    @Override
    public double defaultValue() {
        return 0.0;
    }

    @Override
    public boolean containsEntry(long key, double value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return table[index + 1] == Double.doubleToLongBits(value);
        } else {
            // key is absent
            return false;
        }
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
    public Double get(Object key) {
        int index = index((Long) key);
        if (index >= 0) {
            // key is present
            return Double.longBitsToDouble(table[index + 1]);
        } else {
            // key is absent
            return null;
        }
    }

    

    @Override
    public double get(long key) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return Double.longBitsToDouble(table[index + 1]);
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Double getOrDefault(Object key, Double defaultValue) {
        int index = index((Long) key);
        if (index >= 0) {
            // key is present
            return Double.longBitsToDouble(table[index + 1]);
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public double getOrDefault(long key, double defaultValue) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return Double.longBitsToDouble(table[index + 1]);
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public void forEach(BiConsumer<? super Long, ? super Double> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        long free = freeValue;
        long[] tab = table;
        for (int i = tab.length - 2; i >= 0; i -= 2) {
            long key;
            if ((key = tab[i]) != free) {
                action.accept(key, Double.longBitsToDouble(tab[i + 1]));
            }
        }
    }

    @Override
    public void forEach(LongDoubleConsumer action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        long free = freeValue;
        long[] tab = table;
        for (int i = tab.length - 2; i >= 0; i -= 2) {
            long key;
            if ((key = tab[i]) != free) {
                action.accept(key, Double.longBitsToDouble(tab[i + 1]));
            }
        }
    }

    @Override
    public boolean forEachWhile(LongDoublePredicate predicate) {
        if (predicate == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return true;
        boolean terminated = false;
        long free = freeValue;
        long[] tab = table;
        for (int i = tab.length - 2; i >= 0; i -= 2) {
            long key;
            if ((key = tab[i]) != free) {
                if (!predicate.test(key, Double.longBitsToDouble(tab[i + 1]))) {
                    terminated = true;
                    break;
                }
            }
        }
        return !terminated;
    }

    @Nonnull
    @Override
    public LongDoubleCursor cursor() {
        
        return new NoRemovedMapCursor();
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonLongDoubleMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalLongDoubleMapOps m) {
        if (isEmpty())
            return true;
        boolean containsAll = true;
        long free = freeValue;
        long[] tab = table;
        for (int i = tab.length - 2; i >= 0; i -= 2) {
            long key;
            if ((key = tab[i]) != free) {
                if (!m.containsEntry(key, tab[i + 1])) {
                    containsAll = false;
                    break;
                }
            }
        }
        return containsAll;
    }

    @Override
    public void reversePutAllTo(InternalLongDoubleMapOps m) {
        if (isEmpty())
            return;
        long free = freeValue;
        long[] tab = table;
        for (int i = tab.length - 2; i >= 0; i -= 2) {
            long key;
            if ((key = tab[i]) != free) {
                m.justPut(key, tab[i + 1]);
            }
        }
    }

    @Override
    @Nonnull
    public HashObjSet<Map.Entry<Long, Double>> entrySet() {
        return new EntryView();
    }

    @Override
    @Nonnull
    public DoubleCollection values() {
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
        long[] tab = table;
        for (int i = tab.length - 2; i >= 0; i -= 2) {
            long key;
            if ((key = tab[i]) != free) {
            long val;
                hashCode += ((int) (key ^ (key >>> 32))) ^ ((int) ((val = tab[i + 1]) ^ (val >>> 32)));
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
        long[] tab = table;
        for (int i = tab.length - 2; i >= 0; i -= 2) {
            long key;
            if ((key = tab[i]) != free) {
                sb.append(' ');
                sb.append(key);
                sb.append('=');
                sb.append(Double.longBitsToDouble(tab[i + 1]));
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
    public Double put(Long key, Double value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public double put(long key, double value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public Double putIfAbsent(Long key, Double value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public double putIfAbsent(long key, double value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public void justPut(long key, double value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public void justPut(long key, long value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public Double compute(Long key,
            BiFunction<? super Long, ? super Double, ? extends Double> remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public double compute(long key, LongDoubleToDoubleFunction remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public Double computeIfAbsent(Long key,
            Function<? super Long, ? extends Double> mappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public double computeIfAbsent(long key, LongToDoubleFunction mappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public Double computeIfPresent(Long key,
            BiFunction<? super Long, ? super Double, ? extends Double> remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public double computeIfPresent(long key, LongDoubleToDoubleFunction remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public Double merge(Long key, Double value,
            BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public double merge(long key, double value, DoubleBinaryOperator remappingFunction) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public double addValue(long key, double value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public double addValue(long key, double addition, double defaultValue) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public void putAll(@Nonnull Map<? extends Long, ? extends Double> m) {
        CommonLongDoubleMapOps.putAll(this, m);
    }


    @Override
    public Double replace(Long key, Double value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public double replace(long key, double value) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean replace(Long key, Double oldValue, Double newValue) {
        return replace(key.longValue(),
                oldValue.doubleValue(),
                newValue.doubleValue());
    }

    @Override
    public boolean replace(long key, double oldValue, double newValue) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public void replaceAll(
            BiFunction<? super Long, ? super Double, ? extends Double> function) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public void replaceAll(LongDoubleToDoubleFunction function) {
        throw new java.lang.UnsupportedOperationException();
    }





    @Override
    public Double remove(Object key) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean justRemove(long key) {
        throw new java.lang.UnsupportedOperationException();
    }



    

    @Override
    public double remove(long key) {
        throw new java.lang.UnsupportedOperationException();
    }



    @Override
    public boolean remove(Object key, Object value) {
        return remove(((Long) key).longValue(),
                ((Double) value).doubleValue()
                );
    }

    @Override
    public boolean remove(long key, double value) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean removeIf(LongDoublePredicate filter) {
        throw new java.lang.UnsupportedOperationException();
    }






    class EntryView extends AbstractSetView<Map.Entry<Long, Double>>
            implements HashObjSet<Map.Entry<Long, Double>>,
            InternalObjCollectionOps<Map.Entry<Long, Double>> {

        @Nonnull
        @Override
        public Equivalence<Entry<Long, Double>> equivalence() {
            return Equivalence.entryEquivalence(
                    Equivalence.<Long>defaultEquality()
                    ,
                    Equivalence.<Double>defaultEquality()
                    
            );
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return ImmutableLHashParallelKVLongDoubleMapGO.this.hashConfig();
        }


        @Override
        public int size() {
            return ImmutableLHashParallelKVLongDoubleMapGO.this.size();
        }

        @Override
        public double currentLoad() {
            return ImmutableLHashParallelKVLongDoubleMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Long, Double> e = (Map.Entry<Long, Double>) o;
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
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    result[resultIndex++] = new ImmutableEntry(key, tab[i + 1]);
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
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    a[resultIndex++] = (T) new ImmutableEntry(key, tab[i + 1]);
                }
            }
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public final void forEach(@Nonnull Consumer<? super Map.Entry<Long, Double>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    action.accept(new ImmutableEntry(key, tab[i + 1]));
                }
            }
        }

        @Override
        public boolean forEachWhile(@Nonnull  Predicate<? super Map.Entry<Long, Double>> predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    if (!predicate.test(new ImmutableEntry(key, tab[i + 1]))) {
                        terminated = true;
                        break;
                    }
                }
            }
            return !terminated;
        }

        @Override
        @Nonnull
        public ObjIterator<Map.Entry<Long, Double>> iterator() {
            
            return new NoRemovedEntryIterator();
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Long, Double>> cursor() {
            
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
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    if (!c.contains(e.with(key, tab[i + 1]))) {
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
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    changed |= s.remove(e.with(key, tab[i + 1]));
                }
            }
            return changed;
        }

        @Override
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Long, Double>> c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    changed |= c.add(new ImmutableEntry(key, tab[i + 1]));
                }
            }
            return changed;
        }


        public int hashCode() {
            return ImmutableLHashParallelKVLongDoubleMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    sb.append(' ');
                    sb.append(key);
                    sb.append('=');
                    sb.append(Double.longBitsToDouble(tab[i + 1]));
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
            return ImmutableLHashParallelKVLongDoubleMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Long, Double> e = (Map.Entry<Long, Double>) o;
                long key = e.getKey();
                double value = e.getValue();
                return ImmutableLHashParallelKVLongDoubleMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf(@Nonnull Predicate<? super Map.Entry<Long, Double>> filter) {
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
            ImmutableLHashParallelKVLongDoubleMapGO.this.clear();
        }
    }


    abstract class LongDoubleEntry extends AbstractEntry<Long, Double> {

        abstract long key();

        @Override
        public final Long getKey() {
            return key();
        }

        abstract long value();

        @Override
        public final Double getValue() {
            return Double.longBitsToDouble(value());
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
                v2 = Double.doubleToLongBits((Double) e2.getValue());
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


    private class ImmutableEntry extends LongDoubleEntry {
        private final long key;
        private final long value;

        ImmutableEntry(long key, long value) {
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
    }


    class ReusableEntry extends LongDoubleEntry {
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


    class ValueView extends AbstractDoubleValueView {


        @Override
        public int size() {
            return ImmutableLHashParallelKVLongDoubleMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return ImmutableLHashParallelKVLongDoubleMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return ImmutableLHashParallelKVLongDoubleMapGO.this.containsValue(o);
        }

        @Override
        public boolean contains(double v) {
            return ImmutableLHashParallelKVLongDoubleMapGO.this.containsValue(v);
        }

        @Override
        public boolean contains(long bits) {
            return ImmutableLHashParallelKVLongDoubleMapGO.this.containsValue(bits);
        }


        @Override
        public void forEach(Consumer<? super Double> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    action.accept(Double.longBitsToDouble(tab[i + 1]));
                }
            }
        }

        @Override
        public void forEach(DoubleConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    action.accept(Double.longBitsToDouble(tab[i + 1]));
                }
            }
        }

        @Override
        public boolean forEachWhile(DoublePredicate predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    if (!predicate.test(Double.longBitsToDouble(tab[i + 1]))) {
                        terminated = true;
                        break;
                    }
                }
            }
            return !terminated;
        }

        @Override
        public boolean allContainingIn(DoubleCollection c) {
            if (c instanceof InternalDoubleCollectionOps)
                return allContainingIn((InternalDoubleCollectionOps) c);
            if (isEmpty())
                return true;
            boolean containsAll = true;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    if (!c.contains(Double.longBitsToDouble(tab[i + 1]))) {
                        containsAll = false;
                        break;
                    }
                }
            }
            return containsAll;
        }

        private boolean allContainingIn(InternalDoubleCollectionOps c) {
            if (isEmpty())
                return true;
            boolean containsAll = true;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    if (!c.contains(tab[i + 1])) {
                        containsAll = false;
                        break;
                    }
                }
            }
            return containsAll;
        }

        @Override
        public boolean reverseAddAllTo(DoubleCollection c) {
            if (c instanceof InternalDoubleCollectionOps)
                return reverseAddAllTo((InternalDoubleCollectionOps) c);
            if (isEmpty())
                return false;
            boolean changed = false;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    changed |= c.add(Double.longBitsToDouble(tab[i + 1]));
                }
            }
            return changed;
        }

        private boolean reverseAddAllTo(InternalDoubleCollectionOps c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    changed |= c.add(tab[i + 1]);
                }
            }
            return changed;
        }

        @Override
        public boolean reverseRemoveAllFrom(DoubleSet s) {
            if (s instanceof InternalDoubleCollectionOps)
                return reverseRemoveAllFrom((InternalDoubleCollectionOps) s);
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    changed |= s.removeDouble(Double.longBitsToDouble(tab[i + 1]));
                }
            }
            return changed;
        }

        private boolean reverseRemoveAllFrom(InternalDoubleCollectionOps s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    changed |= s.removeDouble(tab[i + 1]);
                }
            }
            return changed;
        }


        @Override
        @Nonnull
        public DoubleIterator iterator() {
            
            return new NoRemovedValueIterator();
        }

        @Nonnull
        @Override
        public DoubleCursor cursor() {
            
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
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    result[resultIndex++] = Double.longBitsToDouble(tab[i + 1]);
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
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    a[resultIndex++] = (T) Double.valueOf(Double.longBitsToDouble(tab[i + 1]));
                }
            }
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public double[] toDoubleArray() {
            int size = size();
            double[] result = new double[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    result[resultIndex++] = Double.longBitsToDouble(tab[i + 1]);
                }
            }
            return result;
        }

        @Override
        public double[] toArray(double[] a) {
            int size = size();
            if (a.length < size)
                a = new double[size];
            if (size == 0) {
                if (a.length > 0)
                    a[0] = 0.0;
                return a;
            }
            int resultIndex = 0;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    a[resultIndex++] = Double.longBitsToDouble(tab[i + 1]);
                }
            }
            if (a.length > resultIndex)
                a[resultIndex] = 0.0;
            return a;
        }


        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            long free = freeValue;
            long[] tab = table;
            for (int i = tab.length - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    sb.append(' ').append(Double.longBitsToDouble(tab[i + 1])).append(',');
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
            return removeDouble(( Double ) o);
        }

        @Override
        public boolean removeDouble(double v) {
            return removeValue(v);
        }

        @Override
        public boolean removeDouble(long bits) {
            return removeValue(bits);
        }


        @Override
        public void clear() {
            ImmutableLHashParallelKVLongDoubleMapGO.this.clear();
        }

        
        public boolean removeIf(Predicate<? super Double> filter) {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(DoublePredicate filter) {
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



    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Long, Double>> {
        final long[] tab;
        final long free;
        int nextIndex;
        ImmutableEntry next;

        NoRemovedEntryIterator() {
            long[] tab = this.tab = table;
            long free = this.free = freeValue;
            int nextI = tab.length;
            while ((nextI -= 2) >= 0) {
                long key;
                if ((key = tab[nextI]) != free) {
                    next = new ImmutableEntry(key, tab[nextI + 1]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Long, Double>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            long[] tab = this.tab;
            long free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    action.accept(new ImmutableEntry(key, tab[i + 1]));
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
        public Map.Entry<Long, Double> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                long[] tab = this.tab;
                long free = this.free;
                ImmutableEntry prev = next;
                while ((nextI -= 2) >= 0) {
                    long key;
                    if ((key = tab[nextI]) != free) {
                        next = new ImmutableEntry(key, tab[nextI + 1]);
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


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Long, Double>> {
        final long[] tab;
        final long free;
        int index;
        long curKey;
        long curValue;

        NoRemovedEntryCursor() {
            this.tab = table;
            index = tab.length;
            long free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Long, Double>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            long[] tab = this.tab;
            long free = this.free;
            int index = this.index;
            for (int i = index - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    action.accept(new ImmutableEntry(key, tab[i + 1]));
                }
            }
            if (index != this.index) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public Map.Entry<Long, Double> elem() {
            long curKey;
            if ((curKey = this.curKey) != free) {
                return new ImmutableEntry(curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
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
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }




    class NoRemovedValueIterator implements DoubleIterator {
        final long[] tab;
        final long free;
        int nextIndex;
        double next;

        NoRemovedValueIterator() {
            long[] tab = this.tab = table;
            long free = this.free = freeValue;
            int nextI = tab.length;
            while ((nextI -= 2) >= 0) {
                if (tab[nextI] != free) {
                    next = Double.longBitsToDouble(tab[nextI + 1]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public double nextDouble() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                long[] tab = this.tab;
                long free = this.free;
                double prev = next;
                while ((nextI -= 2) >= 0) {
                    if (tab[nextI] != free) {
                        next = Double.longBitsToDouble(tab[nextI + 1]);
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
        public void forEachRemaining(Consumer<? super Double> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            long[] tab = this.tab;
            long free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    action.accept(Double.longBitsToDouble(tab[i + 1]));
                }
            }
            if (nextI != nextIndex) {
                throw new java.util.ConcurrentModificationException();
            }
            nextIndex = -1;
        }

        @Override
        public void forEachRemaining(DoubleConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            long[] tab = this.tab;
            long free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    action.accept(Double.longBitsToDouble(tab[i + 1]));
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
        public Double next() {
            return nextDouble();
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }


    class NoRemovedValueCursor implements DoubleCursor {
        final long[] tab;
        final long free;
        int index;
        long curKey;
        long curValue;

        NoRemovedValueCursor() {
            this.tab = table;
            index = tab.length;
            long free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(DoubleConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            long[] tab = this.tab;
            long free = this.free;
            int index = this.index;
            for (int i = index - 2; i >= 0; i -= 2) {
                if (tab[i] != free) {
                    action.accept(Double.longBitsToDouble(tab[i + 1]));
                }
            }
            if (index != this.index) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public double elem() {
            if (curKey != free) {
                return Double.longBitsToDouble(curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
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
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }



    class NoRemovedMapCursor implements LongDoubleCursor {
        final long[] tab;
        final long free;
        int index;
        long curKey;
        long curValue;

        NoRemovedMapCursor() {
            this.tab = table;
            index = tab.length;
            long free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(LongDoubleConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            long[] tab = this.tab;
            long free = this.free;
            int index = this.index;
            for (int i = index - 2; i >= 0; i -= 2) {
                long key;
                if ((key = tab[i]) != free) {
                    action.accept(key, Double.longBitsToDouble(tab[i + 1]));
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
        public double value() {
            if (curKey != free) {
                return Double.longBitsToDouble(curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(double value) {
            if (curKey != free) {
                tab[index + 1] = Double.doubleToLongBits(value);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
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
        }

        @Override
        public void remove() {
                throw new java.lang.UnsupportedOperationException();
        }
    }
}

