import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class NBTReader {

    public static final int TAG_END = 0;
    public static final int TAG_BYTE = 1;
    public static final int TAG_SHORT = 2;
    public static final int TAG_INT = 3;
    public static final int TAG_LONG = 4;
    public static final int TAG_FLOAT = 5;
    public static final int TAG_DOUBLE = 6;
    public static final int TAG_BYTE_ARRAY = 7;
    public static final int TAG_STRING = 8;
    public static final int TAG_LIST = 9;
    public static final int TAG_COMPOUND = 10;
    public static final int TAG_INT_ARRAY = 11;

    public static Object read(File file) throws IOException {
        InputStream in = new FileInputStream(file);

        // level.dat começa com GZIP
        in = new GZIPInputStream(in);

        DataInputStream data = new DataInputStream(
            new BufferedInputStream(in)
        );

        int type = data.readUnsignedByte();

        if (type != TAG_COMPOUND) {
            throw new IOException(
                "NBT inválido: raiz não é Compound (" + type + ")"
            );
        }

        String name = data.readUTF();

        return readPayload(data, TAG_COMPOUND);
    }

    private static Object readPayload(DataInputStream in, int type)
            throws IOException {

        switch (type) {

            case TAG_END:
                return null;

            case TAG_BYTE:
                return Byte.valueOf(in.readByte());

            case TAG_SHORT:
                return Short.valueOf(in.readShort());

            case TAG_INT:
                return Integer.valueOf(in.readInt());

            case TAG_LONG:
                return Long.valueOf(in.readLong());

            case TAG_FLOAT:
                return Float.valueOf(in.readFloat());

            case TAG_DOUBLE:
                return Double.valueOf(in.readDouble());

            case TAG_STRING:
                return in.readUTF();

            case TAG_BYTE_ARRAY: {
                int len = in.readInt();

                if (len < 0 || len > 100000000) {
                    throw new IOException("ByteArray inválido: " + len);
                }

                byte[] b = new byte[len];
                in.readFully(b);
                return b;
            }

            case TAG_INT_ARRAY: {
                int len = in.readInt();

                if (len < 0 || len > 100000000) {
                    throw new IOException("IntArray inválido: " + len);
                }

                int[] a = new int[len];

                for (int i = 0; i < len; i++) {
                    a[i] = in.readInt();
                }

                return a;
            }

            case TAG_LIST: {
                int childType = in.readUnsignedByte();
                int len = in.readInt();

                if (len < 0 || len > 10000000) {
                    throw new IOException("Lista inválida: " + len);
                }

                ArrayList<Object> list = new ArrayList<Object>();

                for (int i = 0; i < len; i++) {
                    list.add(readPayload(in, childType));
                }

                return list;
            }

            case TAG_COMPOUND: {
                LinkedHashMap<String, Object> map =
                    new LinkedHashMap<String, Object>();

                while (true) {
                    int childType = in.readUnsignedByte();

                    if (childType == TAG_END) {
                        break;
                    }

                    String name = in.readUTF();

                    Object value =
                        readPayload(in, childType);

                    map.put(name, value);
                }

                return map;
            }

            default:
                throw new IOException(
                    "Tipo NBT desconhecido: " + type
                );
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String,Object> load(File file)
            throws IOException {

        Object root = read(file);

        if (!(root instanceof Map)) {
            throw new IOException("NBT raiz inválida");
        }

        return (Map<String,Object>) root;
    }

    public static void dump(Object obj) {
        dump(obj, 0);
    }

    private static void dump(Object obj, int indent) {

        String pad = "";

        for (int i = 0; i < indent; i++) {
            pad += "  ";
        }

        if (obj instanceof Map) {

            Map<?,?> map = (Map<?,?>) obj;

            for (Map.Entry<?,?> e : map.entrySet()) {

                System.out.println(
                    pad + e.getKey() + " ="
                );

                dump(e.getValue(), indent + 1);
            }

        } else if (obj instanceof byte[]) {

            byte[] b = (byte[]) obj;

            System.out.println(
                pad + "[byte array: " + b.length + "]"
            );

        } else if (obj instanceof int[]) {

            int[] a = (int[]) obj;

            System.out.println(
                pad + "[int array: " + a.length + "]"
            );

        } else if (obj instanceof List) {

            List<?> list = (List<?>) obj;

            System.out.println(
                pad + "[list: " + list.size() + "]"
            );

            for (Object x : list) {
                dump(x, indent + 1);
            }

        } else {

            System.out.println(
                pad + String.valueOf(obj)
            );
        }
    }
}
