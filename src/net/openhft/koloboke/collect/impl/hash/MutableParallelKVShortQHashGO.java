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
import net.openhft.koloboke.collect.impl.InternalShortCollectionOps;
import net.openhft.koloboke.collect.set.hash.HashShortSet;
import net.openhft.koloboke.function.ShortConsumer;
import net.openhft.koloboke.function.ShortPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.openhft.koloboke.collect.impl.NotGenerated;

import net.openhft.koloboke.collect.set.ShortSet;
import javax.annotation.Nonnull;

import java.util.*;


public abstract class MutableParallelKVShortQHashGO
        extends MutableParallelKVShortQHashSO {

    @Nonnull
    @Override
    public int[] table() {
        return table;
    }


    @Override
    public int capacity() {
        return table.length;
    }

    public void forEach(Consumer<? super Short> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    action.accept(key);
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    action.accept(key);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    public void forEach(ShortConsumer action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    action.accept(key);
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    action.accept(key);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    public boolean forEachWhile(
            ShortPredicate
            predicate) {
        if (predicate == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return true;
        boolean terminated = false;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    if (!predicate.test(key)) {
                        terminated = true;
                        break;
                    }
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    if (!predicate.test(key)) {
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

    public boolean allContainingIn(ShortCollection c) {
        if (isEmpty())
            return true;
        boolean containsAll = true;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    if (!c.contains(key)) {
                        containsAll = false;
                        break;
                    }
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    if (!c.contains(key)) {
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


    public boolean reverseAddAllTo(ShortCollection c) {
        if (isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    changed |= c.add(key);
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    changed |= c.add(key);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }


    public boolean reverseRemoveAllFrom(ShortSet s) {
        if (isEmpty() || s.isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    changed |= s.removeShort(key);
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    changed |= s.removeShort(key);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }



    public ShortIterator iterator() {
        int mc = modCount();
        if (!noRemoved())
            return new SomeRemovedIterator(mc);
        return new NoRemovedIterator(mc);
    }

    public ShortCursor setCursor() {
        int mc = modCount();
        if (!noRemoved())
            return new SomeRemovedCursor(mc);
        return new NoRemovedCursor(mc);
    }

    @Nonnull
    public Object[] toArray() {
        int size = size();
        Object[] result = new Object[size];
        if (size == 0)
            return result;
        int resultIndex = 0;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    result[resultIndex++] = key;
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    result[resultIndex++] = key;
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return result;
    }

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
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    a[resultIndex++] = (T) Short.valueOf(key);
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    a[resultIndex++] = (T) Short.valueOf(key);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        if (a.length > resultIndex)
            a[resultIndex] = null;
        return a;
    }

    @Nonnull
    public short[] toShortArray() {
        int size = size();
        short[] result = new short[size];
        if (size == 0)
            return result;
        int resultIndex = 0;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    result[resultIndex++] = key;
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    result[resultIndex++] = key;
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return result;
    }

    @Nonnull
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
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    a[resultIndex++] = key;
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    a[resultIndex++] = key;
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        if (a.length > resultIndex)
            a[resultIndex] = (short) 0;
        return a;
    }


    public int setHashCode() {
        int hashCode = 0;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    hashCode += key;
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    hashCode += key;
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return hashCode;
    }

    public String setToString() {
        if (isEmpty())
            return "[]";
        StringBuilder sb = new StringBuilder();
        int elementCount = 0;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    sb.append(' ').append(key).append(',');
                    if (++elementCount == 8) {
                        int expectedLength = sb.length() * (size() / 8);
                        sb.ensureCapacity(expectedLength + (expectedLength / 2));
                    }
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    sb.append(' ').append(key).append(',');
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


    abstract boolean justRemove(short key);

    public boolean removeIf(Predicate<? super Short> filter) {
        if (filter == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    if (filter.test(key)) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, base + off, removed);
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    if (filter.test(key)) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, base + off, removed);
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

    public boolean removeIf(ShortPredicate filter) {
        if (filter == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    if (filter.test(key)) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, base + off, removed);
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    if (filter.test(key)) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, base + off, removed);
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

    boolean removeAll(@Nonnull HashShortSet thisC, @Nonnull Collection<?> c) {
        if (thisC == c)
            throw new IllegalArgumentException();
        if (isEmpty() || c.isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    if (c.contains(key)) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, base + off, removed);
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    if (c.contains(key)) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, base + off, removed);
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

    boolean removeAll(@Nonnull HashShortSet thisC, @Nonnull ShortCollection c) {
        if (thisC == c)
            throw new IllegalArgumentException();
        if (isEmpty() || c.isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    if (c.contains(key)) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, base + off, removed);
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    if (c.contains(key)) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, base + off, removed);
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


    boolean retainAll(@Nonnull HashShortSet thisC, @Nonnull Collection<?> c) {
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
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    if (!c.contains(key)) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, base + off, removed);
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    if (!c.contains(key)) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, base + off, removed);
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
        short removed = removedValue;
        int[] tab = table;
        long base = INT_BASE + SHORT_KEY_OFFSET;
        if (noRemoved()) {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free) {
                    if (!c.contains(key)) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, base + off, removed);
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (long off = ((long) tab.length) << INT_SCALE_SHIFT; (off -= INT_SCALE) >= 0L;) {
                short key;
                if ((key = U.getShort(tab, base + off)) != free && key != removed) {
                    if (!c.contains(key)) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, base + off, removed);
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





    class NoRemovedIterator implements ShortIterator {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        short next;

        NoRemovedIterator(int mc) {
            expectedModCount = mc;
            int[] tab = this.tab = table;
            short free = this.free = freeValue;
            this.removed = removedValue;
            int nextI = tab.length;
            int entry;
            while (--nextI >= 0) {
                short key;
                if ((key = (short) (entry = tab[nextI])) != free) {
                    next = key;
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public short nextShort() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                if (expectedModCount == modCount()) {
                    index = nextI;
                    int[] tab = this.tab;
                    short free = this.free;
                    short prev = next;
                    int entry;
                    while (--nextI >= 0) {
                        short key;
                        if ((key = (short) (entry = tab[nextI])) != free) {
                            next = key;
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
        public void forEachRemaining(Consumer<? super Short> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            int entry;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    action.accept(key);
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            int entry;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    action.accept(key);
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
        public Short next() {
            return nextShort();
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class NoRemovedCursor implements ShortCursor {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index;
        short curKey;

        NoRemovedCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            short free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(ShortConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            int entry;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    action.accept(key);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public short elem() {
            short curKey;
            if ((curKey = this.curKey) != free) {
                return curKey;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                int[] tab = this.tab;
                short free = this.free;
                int entry;
                for (int i = index - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        index = i;
                        curKey = key;
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
            short free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedIterator implements ShortIterator {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        short next;

        SomeRemovedIterator(int mc) {
            expectedModCount = mc;
            int[] tab = this.tab = table;
            short free = this.free = freeValue;
            short removed = this.removed = removedValue;
            int nextI = tab.length;
            int entry;
            while (--nextI >= 0) {
                short key;
                if ((key = (short) (entry = tab[nextI])) != free && key != removed) {
                    next = key;
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public short nextShort() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                if (expectedModCount == modCount()) {
                    index = nextI;
                    int[] tab = this.tab;
                    short free = this.free;
                    short removed = this.removed;
                    short prev = next;
                    int entry;
                    while (--nextI >= 0) {
                        short key;
                        if ((key = (short) (entry = tab[nextI])) != free && key != removed) {
                            next = key;
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
        public void forEachRemaining(Consumer<? super Short> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            short removed = this.removed;
            int entry;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    action.accept(key);
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            short removed = this.removed;
            int entry;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    action.accept(key);
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
        public Short next() {
            return nextShort();
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedCursor implements ShortCursor {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index;
        short curKey;

        SomeRemovedCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            short free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(ShortConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            short removed = this.removed;
            int entry;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    action.accept(key);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public short elem() {
            short curKey;
            if ((curKey = this.curKey) != free) {
                return curKey;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                int[] tab = this.tab;
                short free = this.free;
                short removed = this.removed;
                int entry;
                for (int i = index - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        index = i;
                        curKey = key;
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
            short free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
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

