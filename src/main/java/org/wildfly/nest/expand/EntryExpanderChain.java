/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.nest.expand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Alexey Loubyansky
 */
public class EntryExpanderChain<T> implements Iterable<EntryExpander<T>> {

    public interface Builder<T> {
        Builder<T> add(EntryExpander<T> extractor);

        EntryExpanderChain<T> done();
    }

    public static <T> Builder<T> create() {
        return new Builder<T>() {

            final EntryExpanderChain<T> chain = new EntryExpanderChain<T>();

            @Override
            public Builder<T> add(EntryExpander<T> extractor) {
                chain.add(extractor);
                return this;
            }

            @Override
            public EntryExpanderChain<T> done() {
                chain.freeze();
                return chain;
            }

        };
    }

    private List<EntryExpander<T>> extractors = Collections.emptyList();


    private EntryExpanderChain() {
    }

    EntryExpanderChain<T> add(EntryExpander<T> extractor) {
        if(extractor == null) {
            throw new IllegalArgumentException("extractor is null");
        }
        switch(extractors.size()) {
            case 0:
                extractors = Collections.singletonList(extractor);
                break;
            case 1:
                extractors = new ArrayList<EntryExpander<T>>(extractors);
            default:
                extractors.add(extractor);
        }
        return this;
    }

    void freeze() {
        extractors = Collections.unmodifiableList(extractors);
    }

    @Override
    public Iterator<EntryExpander<T>> iterator() {
        return extractors.iterator();
    }

}
