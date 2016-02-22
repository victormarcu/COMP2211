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
import net.openhft.koloboke.function.ByteConsumer;
import net.openhft.koloboke.function.BytePredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.openhft.koloboke.collect.impl.*;
import net.openhft.koloboke.collect.set.ByteSet;
import net.openhft.koloboke.collect.set.hash.HashByteSet;
import javax.annotation.Nonnull;

import java.util.*;


public abstract class MutableQHashSeparateKVByteKeyMap
        extends MutableSeparateKVByteQHashGO {



    public final boolean containsKey(Object key) {
        return contains(key);
    }

    public boolean containsKey(byte key) {
        return contains(key);
    }


    @Nonnull
    public HashByteSet keySet() {
        return new KeyView();
    }


    abstract boolean justRemove(byte key);


    class KeyView extends AbstractByteKeyView
            implements HashByteSet, InternalByteCollectionOps, SeparateKVByteQHash {


        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return MutableQHashSeparateKVByteKeyMap.this.hashConfig();
        }

        @Override
        public HashConfigWrapper configWrapper() {
            return MutableQHashSeparateKVByteKeyMap.this.configWrapper();
        }

        @Override
        public int size() {
            return MutableQHashSeparateKVByteKeyMap.this.size();
        }

        @Override
        public double currentLoad() {
            return MutableQHashSeparateKVByteKeyMap.this.currentLoad();
        }

        @Override
        public byte freeValue() {
            return MutableQHashSeparateKVByteKeyMap.this.freeValue();
        }

        @Override
        public boolean supportRemoved() {
            return MutableQHashSeparateKVByteKeyMap.this.supportRemoved();
        }

        @Override
        public byte removedValue() {
            return MutableQHashSeparateKVByteKeyMap.this.removedValue();
        }

        @Nonnull
        @Override
        public byte[] keys() {
            return MutableQHashSeparateKVByteKeyMap.this.keys();
        }

        @Override
        public int capacity() {
            return MutableQHashSeparateKVByteKeyMap.this.capacity();
        }

        @Override
        public int freeSlots() {
            return MutableQHashSeparateKVByteKeyMap.this.freeSlots();
        }

        @Override
        public boolean noRemoved() {
            return MutableQHashSeparateKVByteKeyMap.this.noRemoved();
        }

        @Override
        public int removedSlots() {
            return MutableQHashSeparateKVByteKeyMap.this.removedSlots();
        }

        @Override
        public int modCount() {
            return MutableQHashSeparateKVByteKeyMap.this.modCount();
        }

        @Override
        public final boolean contains(Object o) {
            return MutableQHashSeparateKVByteKeyMap.this.contains(o);
        }

        @Override
        public boolean contains(byte key) {
            return MutableQHashSeparateKVByteKeyMap.this.contains(key);
        }



        @Override
        public void forEach(Consumer<? super Byte> action) {
            MutableQHashSeparateKVByteKeyMap.this.forEach(action);
        }

        @Override
        public void forEach(ByteConsumer action) {
            MutableQHashSeparateKVByteKeyMap.this.forEach(action);
        }

        @Override
        public boolean forEachWhile(BytePredicate
                predicate) {
            return MutableQHashSeparateKVByteKeyMap.this.forEachWhile(predicate);
        }

        @Override
        public boolean allContainingIn(ByteCollection c) {
            return MutableQHashSeparateKVByteKeyMap.this.allContainingIn(c);
        }

        @Override
        public boolean reverseAddAllTo(ByteCollection c) {
            return MutableQHashSeparateKVByteKeyMap.this.reverseAddAllTo(c);
        }

        @Override
        public boolean reverseRemoveAllFrom(ByteSet s) {
            return MutableQHashSeparateKVByteKeyMap.this.reverseRemoveAllFrom(s);
        }

        @Override
        @Nonnull
        public ByteIterator iterator() {
            return MutableQHashSeparateKVByteKeyMap.this.iterator();
        }

        @Override
        @Nonnull
        public ByteCursor cursor() {
            return setCursor();
        }

        @Override
        @Nonnull
        public Object[] toArray() {
            return MutableQHashSeparateKVByteKeyMap.this.toArray();
        }

        @Override
        @Nonnull
        public <T> T[] toArray(@Nonnull T[] a) {
            return MutableQHashSeparateKVByteKeyMap.this.toArray(a);
        }

        @Override
        public byte[] toByteArray() {
            return MutableQHashSeparateKVByteKeyMap.this.toByteArray();
        }

        @Override
        public byte[] toArray(byte[] a) {
            return MutableQHashSeparateKVByteKeyMap.this.toArray(a);
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
            return MutableQHashSeparateKVByteKeyMap.this.shrink();
        }


        @Override
        public final boolean remove(Object o) {
            return justRemove((Byte) o);
        }

        @Override
        public boolean removeByte(byte v) {
            return justRemove(v);
        }



        @Override
        public boolean removeIf(Predicate<? super Byte> filter) {
            return MutableQHashSeparateKVByteKeyMap.this.removeIf(filter);
        }

        @Override
        public boolean removeIf(BytePredicate filter) {
            return MutableQHashSeparateKVByteKeyMap.this.removeIf(filter);
        }

        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            if (c instanceof ByteCollection) {
                if (c instanceof InternalByteCollectionOps) {
                    InternalByteCollectionOps c2 = (InternalByteCollectionOps) c;
                    if (c2.size() < this.size()) {
                        
                        return c2.reverseRemoveAllFrom(this);
                    }
                }
                return MutableQHashSeparateKVByteKeyMap.this.removeAll(this, (ByteCollection) c);
            }
            return MutableQHashSeparateKVByteKeyMap.this.removeAll(this, c);
        }

        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            return MutableQHashSeparateKVByteKeyMap.this.retainAll(this, c);
        }

        @Override
        public void clear() {
            MutableQHashSeparateKVByteKeyMap.this.clear();
        }
    }
}

