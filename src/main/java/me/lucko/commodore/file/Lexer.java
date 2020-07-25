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

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

class Lexer extends AbstractIterator<Token> implements TokenStream {
    private final StreamTokenizer tokenizer;
    private boolean end = false;

    Lexer(Reader reader) {
        this.tokenizer = new StreamTokenizer(reader);
        this.tokenizer.resetSyntax();
        this.tokenizer.wordChars('\u0021', '\u007E'); // all ascii characters
        this.tokenizer.quoteChar('"');
        this.tokenizer.whitespaceChars('\u0000', '\u0020');
        "{};".chars().forEach(this.tokenizer::ordinaryChar);
        this.tokenizer.slashSlashComments(true);
        this.tokenizer.slashStarComments(true);
    }

    @Override
    protected Token computeNext() throws ParseException {
        if (this.end) {
            return endOfData();
        }
        try {
            int token = this.tokenizer.nextToken();
            switch (token) {
                case StreamTokenizer.TT_EOF:
                    this.end = true;
                    return Token.ConstantToken.EOF;
                case StreamTokenizer.TT_WORD:
                    return new Token.StringToken(this.tokenizer.sval);
                case '{':
                    return Token.ConstantToken.OPEN_BRACKET;
                case '}':
                    return Token.ConstantToken.CLOSE_BRACKET;
                case ';':
                    return Token.ConstantToken.SEMICOLON;
                default:
                    throw createException("Unknown token: " + ((char) token) + "(" + token + ")");
            }
        } catch (IOException e) {
            throw createException(e);
        }
    }

    @Override
    public ParseException createException(String message) {
        return new ParseException(message, this.tokenizer.lineno());
    }

    @Override
    public ParseException createException(Throwable cause) {
        return new ParseException(cause, this.tokenizer.lineno());
    }

    @Override
    public ParseException createException(String message, Throwable cause) {
        return new ParseException(message, cause, this.tokenizer.lineno());
    }

}
