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

import net.openhft.koloboke.collect.Equivalence;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


final class ImmutableQHashSeparateKVDoubleShortMap
        extends ImmutableQHashSeparateKVDoubleShortMapGO {

    


    static final class WithCustomDefaultValue
            extends ImmutableQHashSeparateKVDoubleShortMapGO {
        short defaultValue;

        

        @Override
        public short defaultValue() {
            return defaultValue;
        }
    }


}

