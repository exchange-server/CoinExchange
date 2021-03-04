//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.spark.blockchain.util;

public class StringParser {
    private String string;
    int index;

    public int length() {
        return this.string.length() - this.index;
    }

    public StringParser(String string) {
        this.string = string;
        this.index = 0;
    }

    public void forward(int chars) {
        this.index += chars;
    }

    public char poll() {
        char c = this.string.charAt(this.index);
        this.forward(1);
        return c;
    }

    public String poll(int length) {
        String str = this.string.substring(this.index, length + this.index);
        this.forward(length);
        return str;
    }

    private void commit() {
        this.string = this.string.substring(this.index);
        this.index = 0;
    }

    public String pollBeforeSkipDelim(String s) {
        this.commit();
        int i = this.string.indexOf(s);
        if (i == -1) {
            throw new RuntimeException("\"" + s + "\" not found in \"" + this.string + "\"");
        } else {
            String rv = this.string.substring(0, i);
            this.forward(i + s.length());
            return rv;
        }
    }

    public char peek() {
        return this.string.charAt(this.index);
    }

    public String peek(int length) {
        return this.string.substring(this.index, length + this.index);
    }

    public String trim() {
        this.commit();
        return this.string = this.string.trim();
    }

    public char charAt(int pos) {
        return this.string.charAt(pos + this.index);
    }

    public boolean isEmpty() {
        return this.string.length() <= this.index;
    }

    public String toString() {
        this.commit();
        return this.string;
    }
}
