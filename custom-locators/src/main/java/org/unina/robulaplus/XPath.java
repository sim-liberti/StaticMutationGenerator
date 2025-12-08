package org.unina.robulaplus;

public class XPath {
    public String value;

    public XPath(String value) {
        this.value = value;
    }

    public boolean startsWith(String value) {
        return this.value.startsWith(value);
    }

    public String substring(int beginIndex) {
        return this.value.substring(beginIndex);
    }
}
