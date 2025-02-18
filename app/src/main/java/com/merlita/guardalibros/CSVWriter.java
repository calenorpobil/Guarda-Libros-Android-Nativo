package com.merlita.guardalibros;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVWriter implements AutoCloseable {
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    private static final String DEFAULT_LINE_END = "\n";

    private Writer writer;
    private char separator;
    private char quote;
    private String lineEnd;

    public CSVWriter(Writer writer) {
        this(writer, DEFAULT_SEPARATOR, DEFAULT_QUOTE, DEFAULT_LINE_END);
    }

    public CSVWriter(Writer writer, char separator, char quote, String lineEnd) {
        this.writer = writer;
        this.separator = separator;
        this.quote = quote;
        this.lineEnd = lineEnd;
    }

    public void writeNext(String[] nextLine) throws IOException {
        if (nextLine == null) return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nextLine.length; i++) {
            if (i != 0) sb.append(separator);

            String nextElement = nextLine[i];
            if (nextElement == null) continue;

            if (containsSpecialCharacters(nextElement)) {
                sb.append(quote);
                sb.append(escapeSpecialCharacters(nextElement));
                sb.append(quote);
            } else {
                sb.append(nextElement);
            }
        }

        sb.append(lineEnd);
        writer.write(sb.toString());
    }

    public void writeAll(List<String[]> allLines) throws IOException {
        for (String[] line : allLines) {
            writeNext(line);
        }
    }

    private boolean containsSpecialCharacters(String element) {
        return element.indexOf(separator) != -1 ||
                element.indexOf(quote) != -1 ||
                element.contains("\n") ||
                element.contains("\r");
    }

    private String escapeSpecialCharacters(String element) {
        if (element == null) return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < element.length(); i++) {
            char c = element.charAt(i);
            if (c == quote) {
                sb.append(quote); // Escapar comillas
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        writer.close();
    }
}