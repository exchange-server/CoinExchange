//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.spark.blockchain.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CrippledJavaScriptParser {
    private static CrippledJavaScriptParser.Keyword[] keywords;

    public CrippledJavaScriptParser() {
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isIdStart(char ch) {
        return ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    private static boolean isId(char ch) {
        return isDigit(ch) || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    private static Object parseJSString(StringParser jsString, char delim) {
        StringBuilder b = new StringBuilder();

        while(!jsString.isEmpty()) {
            char sc = jsString.poll();
            if (sc == '\\') {
                char cc = jsString.poll();
                switch(cc) {
                    case 'b':
                        b.append('\b');
                        break;
                    case 'c':
                    case 'd':
                    case 'e':
                    case 'g':
                    case 'h':
                    case 'i':
                    case 'j':
                    case 'k':
                    case 'l':
                    case 'm':
                    case 'o':
                    case 'p':
                    case 'q':
                    case 's':
                    default:
                        b.append(cc);
                        break;
                    case 'f':
                        b.append('\f');
                        break;
                    case 'n':
                        b.append('\n');
                        break;
                    case 'r':
                        b.append('\r');
                        break;
                    case 't':
                        b.append('\t');
                        break;
                    case 'u':
                        try {
                            char ec = (char)Integer.parseInt(jsString.peek(4), 16);
                            b.append(ec);
                            jsString.forward(4);
                        } catch (NumberFormatException var6) {
                            b.append("\\u");
                        }
                }
            } else {
                if (sc == delim) {
                    break;
                }

                b.append(sc);
            }
        }

        return b.toString();
    }

    private static List parseJSArray(StringParser jsArray) {
        ArrayList rv = new ArrayList();
        jsArray.trim();
        if (jsArray.peek() == ']') {
            jsArray.forward(1);
            return rv;
        } else {
            while(!jsArray.isEmpty()) {
                rv.add(parseJSExpr(jsArray));
                jsArray.trim();
                if (!jsArray.isEmpty()) {
                    char ch = jsArray.poll();
                    if (ch == ']') {
                        return rv;
                    }

                    if (ch != ',') {
                        throw new RuntimeException(jsArray.toString());
                    }

                    jsArray.trim();
                }
            }

            return rv;
        }
    }

    private static String parseId(StringParser jsId) {
        StringBuilder b = new StringBuilder();
        b.append(jsId.poll());

        char ch;
        while(isId(ch = jsId.peek())) {
            b.append(ch);
            jsId.forward(1);
        }

        return b.toString();
    }

    private static HashMap parseJSHash(StringParser jsHash) {
        LinkedHashMap rv = new LinkedHashMap();
        jsHash.trim();
        if (jsHash.peek() == '}') {
            jsHash.forward(1);
            return rv;
        } else {
            Object key;
            Object value;
            for(; !jsHash.isEmpty(); rv.put(key, value)) {
                if (isIdStart(jsHash.peek())) {
                    key = parseId(jsHash);
                } else {
                    key = parseJSExpr(jsHash);
                }

                jsHash.trim();
                if (jsHash.isEmpty()) {
                    throw new IllegalArgumentException();
                }

                if (jsHash.peek() != ':') {
                    throw new RuntimeException(jsHash.toString());
                }

                jsHash.forward(1);
                jsHash.trim();
                value = parseJSExpr(jsHash);
                jsHash.trim();
                if (!jsHash.isEmpty()) {
                    char ch = jsHash.poll();
                    if (ch == '}') {
                        rv.put(key, value);
                        return rv;
                    }

                    if (ch != ',') {
                        throw new RuntimeException(jsHash.toString());
                    }

                    jsHash.trim();
                }
            }

            return rv;
        }
    }

    public static Object parseJSExpr(StringParser jsExpr) {
        if (jsExpr.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            jsExpr.trim();
            char start = jsExpr.poll();
            if (start == '[') {
                return parseJSArray(jsExpr);
            } else if (start == '{') {
                return parseJSHash(jsExpr);
            } else if (start != '\'' && start != '"') {
                if (!isDigit(start) && start != '-' && start != '+') {
                    CrippledJavaScriptParser.Keyword[] var7 = keywords;
                    int var9 = var7.length;

                    for(int var10 = 0; var10 < var9; ++var10) {
                        CrippledJavaScriptParser.Keyword keyword = var7[var10];
                        int keywordlen = keyword.keywordFromSecond.length();
                        if (start == keyword.firstChar && jsExpr.peek(keywordlen).equals(keyword.keywordFromSecond)) {
                            if (jsExpr.length() == keyword.keywordFromSecond.length()) {
                                jsExpr.forward(keyword.keywordFromSecond.length());
                                return keyword.value;
                            }

                            if (!isId(jsExpr.charAt(keyword.keywordFromSecond.length()))) {
                                jsExpr.forward(keyword.keywordFromSecond.length());
                                jsExpr.trim();
                                return keyword.value;
                            }

                            throw new IllegalArgumentException(jsExpr.toString());
                        }
                    }

                    if (start == 'n' && jsExpr.peek("ew Date(".length()).equals("ew Date(")) {
                        jsExpr.forward("ew Date(".length());
                        Number date = (Number)parseJSExpr(jsExpr);
                        jsExpr.trim();
                        if (jsExpr.poll() != ')') {
                            throw new RuntimeException("Invalid date");
                        } else {
                            return new Date(date.longValue());
                        }
                    } else {
                        throw new UnsupportedOperationException("Unparsable javascript expression: \"" + start + jsExpr + "\"");
                    }
                } else {
                    StringBuilder b = new StringBuilder();
                    if (start != '+') {
                        b.append(start);
                    }

                    char psc = 0;
                    boolean exp = false;
                    boolean dot = false;

                    while(true) {
                        char sc;
                        label139: {
                            if (!jsExpr.isEmpty()) {
                                sc = jsExpr.peek();
                                if (isDigit(sc)) {
                                    break label139;
                                }

                                if (sc == 'E' || sc == 'e') {
                                    if (exp) {
                                        throw new NumberFormatException(b.toString() + jsExpr.toString());
                                    }

                                    exp = true;
                                    break label139;
                                }

                                if (sc == '.') {
                                    if (!dot && !exp) {
                                        dot = true;
                                        break label139;
                                    }

                                    throw new NumberFormatException(b.toString() + jsExpr.toString());
                                }

                                if ((sc == '-' || sc == '+') && (psc == 'E' || psc == 'e')) {
                                    break label139;
                                }
                            }

                            return !dot && !exp ? Long.parseLong(b.toString()) : Double.parseDouble(b.toString());
                        }

                        b.append(sc);
                        jsExpr.forward(1);
                        psc = sc;
                    }
                }
            } else {
                return parseJSString(jsExpr, start);
            }
        }
    }

    public static Object parseJSExpr(String jsExpr) {
        return parseJSExpr(new StringParser(jsExpr));
    }

    public static LinkedHashMap<String, Object> parseJSVars(String javascript) {
        try {
            BufferedReader r = new BufferedReader(new StringReader(javascript));
            LinkedHashMap rv = new LinkedHashMap();

            String l;
            while((l = r.readLine()) != null) {
                l = l.trim();
                if (!l.isEmpty() && l.startsWith("var")) {
                    l = l.substring(3).trim();
                    int i = l.indexOf(61);
                    if (i != -1) {
                        String varName = l.substring(0, i).trim();
                        String expr = l.substring(i + 1).trim();
                        if (expr.endsWith(";")) {
                            expr = expr.substring(0, expr.length() - 1).trim();
                        }

                        rv.put(varName, parseJSExpr(expr));
                    }
                }
            }

            return rv;
        } catch (IOException var7) {
            throw new RuntimeException(var7);
        }
    }

    static {
        keywords = new CrippledJavaScriptParser.Keyword[]{new CrippledJavaScriptParser.Keyword("null", (Object)null), new CrippledJavaScriptParser.Keyword("true", Boolean.TRUE), new CrippledJavaScriptParser.Keyword("false", Boolean.FALSE)};
    }

    private static class Keyword {
        public final String keyword;
        public final Object value;
        public final char firstChar;
        public final String keywordFromSecond;

        public Keyword(String keyword, Object value) {
            this.keyword = keyword;
            this.value = value;
            this.firstChar = keyword.charAt(0);
            this.keywordFromSecond = keyword.substring(1);
        }
    }
}
