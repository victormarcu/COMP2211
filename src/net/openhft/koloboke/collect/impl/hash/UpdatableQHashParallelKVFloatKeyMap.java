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
import net.openhft.koloboke.function.FloatConsumer;
import net.openhft.koloboke.function.FloatPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.openhft.koloboke.collect.impl.*;
import net.openhft.koloboke.collect.set.FloatSet;
import net.openhft.koloboke.collect.set.hash.HashFloatSet;
import javax.annotation.Nonnull;

import java.util.*;


public abstract class UpdatableQHashParallelKVFloatKeyMap
        extends UpdatableParallelKVFloatQHashGO {



    public final boolean containsKey(Object key) {
        return contains(key);
    }

    public boolean containsKey(float key) {
        return contains(key);
    }


    @Nonnull
    public HashFloatSet keySet() {
        return new KeyView();
    }


    abstract boolean justRemove(float key);

    @Override
    abstract boolean justRemove(int key);

    class KeyView extends AbstractFloatKeyView
            implements HashFloatSet, InternalFloatCollectionOps, ParallelKVFloatQHash {


        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return UpdatableQHashParallelKVFloatKeyMap.this.hashConfig();
        }

        @Override
        public HashConfigWrapper configWrapper() {
            return UpdatableQHashParallelKVFloatKeyMap.this.configWrapper();
        }

        @Override
        public int size() {
            return UpdatableQHashParallelKVFloatKeyMap.this.size();
        }

        @Override
        public double currentLoad() {
            return UpdatableQHashParallelKVFloatKeyMap.this.currentLoad();
        }


        @Nonnull
        @Override
        public long[] table() {
            return UpdatableQHashParallelKVFloatKeyMap.this.table();
        }

        @Override
        public int capacity() {
            return UpdatableQHashParallelKVFloatKeyMap.this.capacity();
        }

        @Override
        public int freeSlots() {
            return UpdatableQHashParallelKVFloatKeyMap.this.freeSlots();
        }

        @Override
        public boolean noRemoved() {
            return UpdatableQHashParallelKVFloatKeyMap.this.noRemoved();
        }

        @Override
        public int removedSlots() {
            return UpdatableQHashParallelKVFloatKeyMap.this.removedSlots();
        }

        @Override
        public int modCount() {
            return UpdatableQHashParallelKVFloatKeyMap.this.modCount();
        }

        @Override
        public final boolean contains(Object o) {
            return UpdatableQHashParallelKVFloatKeyMap.this.contains(o);
        }

        @Override
        public boolean contains(float key) {
            return UpdatableQHashParallelKVFloatKeyMap.this.contains(key);
        }

        @Override
        public boolean contains(int bits) {
            return UpdatableQHashParallelKVFloatKeyMap.this.contains(bits);
        }


        @Override
        public void forEach(Consumer<? super Float> action) {
            UpdatableQHashParallelKVFloatKeyMap.this.forEach(action);
        }

        @Override
        public void forEach(FloatConsumer action) {
            UpdatableQHashParallelKVFloatKeyMap.this.forEach(action);
        }

        @Override
        public boolean forEachWhile(FloatPredicate
                predicate) {
            return UpdatableQHashParallelKVFloatKeyMap.this.forEachWhile(predicate);
        }

        @Override
        public boolean allContainingIn(FloatCollection c) {
            return UpdatableQHashParallelKVFloatKeyMap.this.allContainingIn(c);
        }

        @Override
        public boolean reverseAddAllTo(FloatCollection c) {
            return UpdatableQHashParallelKVFloatKeyMap.this.reverseAddAllTo(c);
        }

        @Override
        public boolean reverseRemoveAllFrom(FloatSet s) {
            return UpdatableQHashParallelKVFloatKeyMap.this.reverseRemoveAllFrom(s);
        }

        @Override
        @Nonnull
        public FloatIterator iterator() {
            return UpdatableQHashParallelKVFloatKeyMap.this.iterator();
        }

        @Override
        @Nonnull
        public FloatCursor cursor() {
            return setCursor();
        }

        @Override
        @Nonnull
        public Object[] toArray() {
            return UpdatableQHashParallelKVFloatKeyMap.this.toArray();
        }

        @Override
        @Nonnull
        public <T> T[] toArray(@Nonnull T[] a) {
            return UpdatableQHashParallelKVFloatKeyMap.this.toArray(a);
        }

        @Override
        public float[] toFloatArray() {
            return UpdatableQHashParallelKVFloatKeyMap.this.toFloatArray();
        }

        @Override
        public float[] toArray(float[] a) {
            return UpdatableQHashParallelKVFloatKeyMap.this.toArray(a);
        }


        @Override
        public int hashCode() {
            return setHashCode();
        }

        @Override
        public String toString() {
            return setToString();
        }


        @Override
        public boolean shrink() {
            return UpdatableQHashParallelKVFloatKeyMap.this.shrink();
        }


        @Override
        public final boolean remove(Object o) {
            return justRemove((Float) o);
        }

        @Override
        public boolean removeFloat(float v) {
            return justRemove(v);
        }

        @Override
        public boolean removeFloat(int bits) {
            return justRemove(bits);
        }


        @Override
        public boolean removeIf(Predicate<? super Float> filter) {
            return UpdatableQHashParallelKVFloatKeyMap.this.removeIf(filter);
        }

        @Override
        public boolean removeIf(FloatPredicate filter) {
            return UpdatableQHashParallelKVFloatKeyMap.this.removeIf(filter);
        }

        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            if (c instanceof FloatCollection) {
                if (c instanceof InternalFloatCollectionOps) {
                    InternalFloatCollectionOps c2 = (InternalFloatCollectionOps) c;
                    if (c2.size() < this.size()) {
                        
                        return c2.reverseRemoveAllFrom(this);
                    }
                }
                return UpdatableQHashParallelKVFloatKeyMap.this.removeAll(this, (FloatCollection) c);
            }
            return UpdatableQHashParallelKVFloatKeyMap.this.removeAll(this, c);
        }

        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            return UpdatableQHashParallelKVFloatKeyMap.this.retainAll(this, c);
        }

        @Override
        public void clear() {
            UpdatableQHashParallelKVFloatKeyMap.this.clear();
        }
    }
}

