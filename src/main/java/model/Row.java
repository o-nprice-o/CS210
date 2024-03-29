package model;

import java.util.Collections;
import java.util.List;

public record Row(String key, List<Object> fields) implements Comparable<Row> {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < fields.size(); i++) {
	        sb.append(fields.get(i).toString());
	        if(i < fields.size() - 1) {
		        sb.append(", ");
	        }
        }
        return key + ": [" + sb.toString() + "]" ;
    }
    
	public Row {
		if(fields != null) {
			fields = Collections.unmodifiableList(fields);
		}
	}

    @Override
    public int compareTo(Row other) {
        return this.key.compareTo(other.key());
    }

    @Override
    public int hashCode() {
        return key.hashCode() ^ fields.hashCode();
    }
}