package model;

//Nicholas Price\

import java.util.List;

public record Row(String key, List<Object> fields) {
	public String toString() {
		String result = this.key + ": " + this.fields;
		return result;
	}
	public int compareTo(Row other) {
        // Compare rows based on their keys
        return key.compareTo(other.key());
    }

	public int hashCode() {
		return (key.hashCode() ^ fields.hashCode());
	}
}