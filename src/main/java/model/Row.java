package model;

import java.util.List;

public record Row(String key, List<Object> fields) {
	public String toString() {
		String result = this.key + ": " + this.fields;
		return result;
	}

	public int hashCode() {
		return (key.hashCode() ^ fields.hashCode());
	}
}