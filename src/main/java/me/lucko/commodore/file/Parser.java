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
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import me.lucko.commodore.file.Token.StringToken;

import java.util.Arrays;
import java.util.Collection;

class Parser<S> {
    private final Lexer lexer;
    private final Collection<ArgumentTypeParser> argumentTypeParsers;

    Parser(Lexer lexer, Collection<ArgumentTypeParser> argumentTypeParsers) {
        this.lexer = lexer;
        this.argumentTypeParsers = argumentTypeParsers;
    }

    LiteralCommandNode<S> parse() throws ParseException {
        CommandNode<S> node = parseNode();
        if (!(node instanceof LiteralCommandNode)) {
            throw this.lexer.createException("Root command node is not a literal command node");
        }

        if (this.lexer.peek() != Token.ConstantToken.EOF) {
            throw this.lexer.createException("Expected end of file but got " + this.lexer.peek());
        }
        return (LiteralCommandNode<S>) node;
    }

    private CommandNode<S> parseNode() throws ParseException {
        Token token = this.lexer.next();
        if (!(token instanceof StringToken)) {
            throw this.lexer.createException("Expected string token for node name but got " + token);
        }

        String name = ((StringToken) token).getString();
        ArgumentBuilder<S, ?> node;

        if (this.lexer.peek() instanceof StringToken) {
            node = RequiredArgumentBuilder.argument(name, parseArgumentType());
        } else {
            node = LiteralArgumentBuilder.literal(name);
        }

        if (this.lexer.peek() == Token.ConstantToken.OPEN_BRACKET) {
            this.lexer.next();
            while (this.lexer.peek() != Token.ConstantToken.CLOSE_BRACKET) {
                CommandNode<S> child = parseNode();
                node.then(child);
            }
            this.lexer.next();
        } else {
            if (this.lexer.peek() != Token.ConstantToken.SEMICOLON) {
                throw this.lexer.createException("Node definition not ended with semicolon, got " + this.lexer.peek());
            }
            this.lexer.next();
        }

        return node.build();
    }

    private ArgumentType<?> parseArgumentType() throws ParseException {
        Token token = this.lexer.next();
        if (!(token instanceof StringToken)) {
            throw this.lexer.createException("Expected string token for argument type but got " + token);
        }

        String argumentType = ((StringToken) token).getString();

        String[] key = argumentType.split(":");
        if (key.length != 2) {
            throw this.lexer.createException("Invalid key for argument type: " + Arrays.toString(key));
        }

        for (ArgumentTypeParser parser : this.argumentTypeParsers) {
            if (parser.canParse(key[0], key[1])) {
                return parser.parse(key[0], key[1], this.lexer);
            }
        }

        throw this.lexer.createException("Unable to parse argument type: " + argumentType);
    }
}
