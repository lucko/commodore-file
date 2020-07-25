/*
 * This file is part of commodore, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.commodore.file;

import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class AbstractIterator<T> implements Iterator<T> {
    private State state = State.NOT_READY;

    protected AbstractIterator() {

    }

    private enum State {
        /** We have computed the next element and haven't returned it yet. */
        READY,
        /** We haven't yet computed or have already returned the element. */
        NOT_READY,
        /** We have reached the end of the data and are finished. */
        DONE,
        /** We've suffered an exception and are kaput. */
        FAILED,
    }

    private T next;

    protected abstract T computeNext() throws Exception;

    protected final T endOfData() {
        state = State.DONE;
        return null;
    }

    @Override
    public final boolean hasNext() {
        if (state == State.FAILED) {
            throw new IllegalStateException();
        }

        switch (state) {
            case DONE:
                return false;
            case READY:
                return true;
            default:
        }
        return tryToComputeNext();
    }

    private boolean tryToComputeNext() {
        state = State.FAILED; // temporary pessimism
        try {
            next = computeNext();
        } catch (Exception e) {
            throw new RuntimeException("Exception whilst computing next value", e);
        }
        if (state != State.DONE) {
            state = State.READY;
            return true;
        }
        return false;
    }

    @Override
    public final T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        state = State.NOT_READY;
        T result = next;
        next = null;
        return result;
    }

    public final T peek() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return next;
    }
}
