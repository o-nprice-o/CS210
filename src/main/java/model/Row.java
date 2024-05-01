package model;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public record Row(String key, List<Object> fields) implements Comparable<Row>, Serializable {
	
	private static final int INT_MARKER = -11;
    private static final int DOUBLE_MARKER = -77;
    private static final int TRUE_MARKER = -31;
    private static final int FALSE_MARKER = -30;
    private static final int NULL_MARKER = -420;
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

    @Override
    public int compareTo(Row other) {
        return this.key.compareTo(other.key());
    }

    @Override
    public int hashCode() {
        return key.hashCode() ^ fields.hashCode();
    }
    
    public byte[] getBytes() {
		List<Object> rowList = new ArrayList<>();
		rowList.add(key);
		rowList.addAll(fields);
		var numOfBytes = 0;
		for (Object obj : rowList) {
	        if (obj instanceof String) {
	            numOfBytes += ((String) obj).getBytes().length + Integer.BYTES;
	        } else if (obj instanceof Integer) {
	            numOfBytes += Integer.BYTES + 1;
	        } else if (obj instanceof Double) {
	            numOfBytes += Double.BYTES + 1;
	        } else if (obj instanceof Boolean) {
	            numOfBytes += 1;
	        } else if (obj == null) {
	            numOfBytes += 1;
	        }
	    }
		var bytes = ByteBuffer.allocate(numOfBytes);
		for (Object obj : rowList) {
		    if (obj instanceof String) {
		        bytes.putInt(((String) obj).getBytes().length);
		        bytes.put(((String) obj).getBytes());
		    } else if (obj instanceof Integer) {
		        bytes.put((byte) INT_MARKER);
		        bytes.putInt((Integer) obj);
		    } else if (obj instanceof Double) {
		        bytes.put((byte) DOUBLE_MARKER);
		        bytes.putDouble((Double) obj);
		    } else if (obj instanceof Boolean) {
		        bytes.put((byte) (((Boolean) obj) ? TRUE_MARKER : FALSE_MARKER));
		    } else if (obj == null) {
		        bytes.put((byte) NULL_MARKER);
		    } else {
		        throw new IllegalArgumentException("field type is not recognized");
		    }
		}
		return bytes.array();
	}

	public static Row fromBytes(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.wrap(bytes);
	    List<Object> objects = new ArrayList<>();

	    while (buffer.hasRemaining()) {
	        int tag = buffer.getInt();
	        if (tag >= 0) {
	            byte[] strBytes = new byte[tag];
	            buffer.get(strBytes);
	            objects.add(new String(strBytes));
	        } else if (tag == -1) {
	            objects.add(buffer.getInt());
	        } else if (tag == -2) {
	            objects.add(buffer.getDouble());
	        } else if (tag == -30) {
	            objects.add(true);
	        } else if (tag == -31) {
	            objects.add(false);
	        } else if (tag == -66) {
	            objects.add(null);
	        } else {
	            throw new IllegalArgumentException("field type is not recognized");
	        }
	    }
	    return new Row(objects.get(0).toString(), objects.subList(1, objects.size()));
	}
}
