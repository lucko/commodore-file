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
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

/**
 * An {@link ArgumentTypeParser} for brigadier argument types.
 */
public class BrigadierArgumentTypeParser implements ArgumentTypeParser {
    public static final BrigadierArgumentTypeParser INSTANCE = new BrigadierArgumentTypeParser();
    
    private BrigadierArgumentTypeParser() {
        
    }
    
    @Override
    public boolean canParse(String namespace, String name) {
        if (!namespace.equals("brigadier")) {
            return false;
        }

        switch (name) {
            case "bool":
            case "string":
            case "integer":
            case "long":
            case "float":
            case "double":
                return true;
            default:
                return false;
        }
    }

    @Override
    public ArgumentType<?> parse(String namespace, String name, TokenStream tokens) throws ParseException {
        switch (name) {
            case "bool":
                return BoolArgumentType.bool();
            case "string":
                return parseStringArgumentType(tokens);
            case "integer":
                return parseIntegerArgumentType(tokens);
            case "long":
                return parseLongArgumentType(tokens);
            case "float":
                return parseFloatArgumentType(tokens);
            case "double":
                return parseDoubleArgumentType(tokens);
        }
        throw new AssertionError();
    }

    private static StringArgumentType parseStringArgumentType(TokenStream tokens) throws ParseException {
        Token token = tokens.next();
        if (!(token instanceof Token.StringToken)) {
            throw tokens.createException("Expected string token for string type but got " + token);
        }

        String stringType = ((Token.StringToken) token).getString();
        switch (stringType) {
            case "single_word":
                return StringArgumentType.word();
            case "quotable_phrase":
                return StringArgumentType.string();
            case "greedy_phrase":
                return StringArgumentType.greedyString();
            default:
                throw tokens.createException("Unknown string type: " + stringType);
        }
    }

    private static IntegerArgumentType parseIntegerArgumentType(TokenStream tokens) throws ParseException {
        if (tokens.peek() instanceof Token.StringToken) {
            int min = parseInt(tokens);
            if (tokens.peek() instanceof Token.StringToken) {
                int max = parseInt(tokens);
                return IntegerArgumentType.integer(min, max);
            }
            return IntegerArgumentType.integer(min);
        }
        return IntegerArgumentType.integer();
    }

    private static LongArgumentType parseLongArgumentType(TokenStream tokens) throws ParseException {
        if (tokens.peek() instanceof Token.StringToken) {
            long min = parseLong(tokens);
            if (tokens.peek() instanceof Token.StringToken) {
                long max = parseLong(tokens);
                return LongArgumentType.longArg(min, max);
            }
            return LongArgumentType.longArg(min);
        }
        return LongArgumentType.longArg();
    }

    private static FloatArgumentType parseFloatArgumentType(TokenStream tokens) throws ParseException {
        if (tokens.peek() instanceof Token.StringToken) {
            float min = parseFloat(tokens);
            if (tokens.peek() instanceof Token.StringToken) {
                float max = parseFloat(tokens);
                return FloatArgumentType.floatArg(min, max);
            }
            return FloatArgumentType.floatArg(min);
        }
        return FloatArgumentType.floatArg();
    }

    private static DoubleArgumentType parseDoubleArgumentType(TokenStream tokens) throws ParseException {
        if (tokens.peek() instanceof Token.StringToken) {
            double min = parseDouble(tokens);
            if (tokens.peek() instanceof Token.StringToken) {
                double max = parseDouble(tokens);
                return DoubleArgumentType.doubleArg(min, max);
            }
            return DoubleArgumentType.doubleArg(min);
        }
        return DoubleArgumentType.doubleArg();
    }

    private static int parseInt(TokenStream tokens) throws ParseException {
        Token token = tokens.next();
        if (!(token instanceof Token.StringToken)) {
            throw tokens.createException("Expected string token for integer but got " + token);
        }
        String value = ((Token.StringToken) token).getString();

        if (value.equals("min")) {
            return Integer.MIN_VALUE;
        }
        if (value.equals("max")) {
            return Integer.MAX_VALUE;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw tokens.createException("Expected int but got " + value, e);
        }
    }

    private static long parseLong(TokenStream tokens) throws ParseException {
        Token token = tokens.next();
        if (!(token instanceof Token.StringToken)) {
            throw tokens.createException("Expected string token for long but got " + token);
        }
        String value = ((Token.StringToken) token).getString();

        if (value.equals("min")) {
            return Long.MIN_VALUE;
        }
        if (value.equals("max")) {
            return Long.MAX_VALUE;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw tokens.createException("Expected long but got " + value, e);
        }
    }

    private static float parseFloat(TokenStream tokens) throws ParseException {
        Token token = tokens.next();
        if (!(token instanceof Token.StringToken)) {
            throw tokens.createException("Expected string token for float but got " + token);
        }
        String value = ((Token.StringToken) token).getString();

        if (value.equals("min")) {
            return Float.MIN_VALUE;
        }
        if (value.equals("max")) {
            return Float.MAX_VALUE;
        }

        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw tokens.createException("Expected float but got " + value, e);
        }
    }

    private static double parseDouble(TokenStream tokens) throws ParseException {
        Token token = tokens.next();
        if (!(token instanceof Token.StringToken)) {
            throw tokens.createException("Expected string token for double but got " + token);
        }
        String value = ((Token.StringToken) token).getString();

        if (value.equals("min")) {
            return Double.MIN_VALUE;
        }
        if (value.equals("max")) {
            return Double.MAX_VALUE;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw tokens.createException("Expected double but got " + value);
        }
    }
}
