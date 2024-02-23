package model;

import java.util.List;

public record Row(String key, List<Object> fields) implements Comparable<Row> {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(key).append(": ");
        for (Object field : fields) {
            sb.append(field).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    @Override
    public int compareTo(Row other) {
        return key.compareTo(other.key());
    }

    @Override
    public int hashCode() {
        return key.hashCode() ^ fields.hashCode();
    }
}
