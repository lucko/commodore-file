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

import com.mojang.brigadier.arguments.ArgumentType;

/**
 * A parser for {@link ArgumentType}s.
 */
public interface ArgumentTypeParser {

    /**
     * Gets if this parser supports the given type.
     *
     * @param namespace the namespace
     * @param name the type name
     * @return if the parser supports the type
     */
    boolean canParse(String namespace, String name);

    /**
     * Parses an {@link ArgumentType}.
     *
     * @param namespace the namespace
     * @param name the type name
     * @param tokenStream the token stream to parse from
     * @return the parsed argument type
     * @throws ParseException if unable to parse
     */
    ArgumentType<?> parse(String namespace, String name, TokenStream tokenStream) throws ParseException;

}
