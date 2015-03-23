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

package org.wildfly.nest.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Alexey Loubyansky
 */
public class EntryProcessorChain<T> implements Iterable<T> {

    public interface Builder<T> {
        Builder<T> add(T processor);

        EntryProcessorChain<T> done();
    }

    public static <T> Builder<T> create() {
        return new Builder<T>() {

            final EntryProcessorChain<T> chain = new EntryProcessorChain<T>();

            @Override
            public Builder<T> add(T processor) {
                chain.add(processor);
                return this;
            }

            @Override
            public EntryProcessorChain<T> done() {
                chain.freeze();
                return chain;
            }

        };
    }

    private List<T> processors = Collections.emptyList();


    private EntryProcessorChain() {
    }

    EntryProcessorChain<T> add(T processor) {
        if(processor == null) {
            throw new IllegalArgumentException("processor is null");
        }
        switch(processors.size()) {
            case 0:
                processors = Collections.singletonList(processor);
                break;
            case 1:
                processors = new ArrayList<T>(processors);
            default:
                processors.add(processor);
        }
        return this;
    }

    void freeze() {
        processors = Collections.unmodifiableList(processors);
    }

    @Override
    public Iterator<T> iterator() {
        return processors.iterator();
    }

}
