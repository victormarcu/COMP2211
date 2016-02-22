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

package net.openhft.koloboke.collect.impl;


public final class IntArrays implements UnsafeConstants {

    public static void replaceAll(int[] a, int oldValue, int newValue) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == oldValue) {
                a[i] = newValue;
            }
        }
    }

    public static void replaceAllKeys(long[] table, int oldKey, int newKey) {
        long base = LONG_BASE + INT_KEY_OFFSET;
        for (long off = LONG_SCALE * (long) table.length; (off -= LONG_SCALE) >= 0L;) {
            if (U.getInt(table, base + off) == oldKey) {
                U.putInt(table, base + off, newKey);
            }
        }
    }

    public static void fillKeys(long[] table, int key) {
        long base = LONG_BASE + INT_KEY_OFFSET;
        for (long off = LONG_SCALE * (long) table.length; (off -= LONG_SCALE) >= 0L;) {
            U.putInt(table, base + off, key);
        }
    }

    private IntArrays() {}
}

