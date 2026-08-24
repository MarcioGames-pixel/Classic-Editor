import java.io.*;
import java.net.*;

public class SerializationReader {

    public static Object load(File levelFile, File minecraftJar)
            throws Exception {

        URLClassLoader loader =
            new URLClassLoader(
                new URL[] {
                    minecraftJar.toURI().toURL()
                },
                SerializationReader.class.getClassLoader()
            );

        try {

            FileInputStream fis =
                new FileInputStream(levelFile);

            GZIPInputStream gzip =
                new GZIPInputStream(fis);

            /*
             * Formato encontrado:
             *
             * 27 1B B7 88 02
             * AC ED 00 05
             * 73 72 ...
             *
             * Os primeiros 5 bytes pertencem ao
             * formato do level.dat.
             */

            byte[] header = new byte[5];

            readFully(gzip, header);

            System.out.printf(
                "[*] Level header: %02X %02X %02X %02X %02X%n",
                header[0] & 255,
                header[1] & 255,
                header[2] & 255,
                header[3] & 255,
                header[4] & 255
            );

            /*
             * Agora o próximo byte deve ser AC.
             */

            int a = gzip.read();
            int b = gzip.read();
            int c = gzip.read();
            int d = gzip.read();

            System.out.printf(
                "[*] Java stream: %02X %02X %02X %02X%n",
                a, b, c, d
            );

            if (a != 0xAC ||
                b != 0xED ||
                c != 0x00 ||
                d != 0x05) {

                throw new IOException(
                    "Java serialization não encontrada " +
                    "após o cabeçalho do Level."
                );
            }

            /*
             * Recoloca os 4 bytes do magic no stream.
             */

            ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

            buffer.write(a);
            buffer.write(b);
            buffer.write(c);
            buffer.write(d);

            byte[] temp = new byte[8192];
            int n;

            while ((n = gzip.read(temp)) != -1) {
                buffer.write(temp, 0, n);
            }

            gzip.close();

            ByteArrayInputStream data =
                new ByteArrayInputStream(
                    buffer.toByteArray()
                );

            ObjectInputStream in =
                new ObjectInputStream(data) {

                @Override
                protected Class<?> resolveClass(
                        ObjectStreamClass desc)
                        throws IOException,
                        ClassNotFoundException {

                    return Class.forName(
                        desc.getName(),
                        false,
                        loader
                    );
                }
            };

            Object result = in.readObject();

            in.close();

            return result;

        } finally {

            loader.close();
        }
    }

    private static void readFully(
            InputStream in,
            byte[] data)
            throws IOException {

        int pos = 0;

        while (pos < data.length) {

            int n =
                in.read(data, pos,
                        data.length - pos);

            if (n == -1) {
                throw new EOFException(
                    "Fim inesperado do level.dat"
                );
            }

            pos += n;
        }
    }

    public static void dumpFields(Object obj) {

        if (obj == null) {
            System.out.println(
                "[ERRO] Objeto é null"
            );
            return;
        }

        Class<?> c = obj.getClass();

        System.out.println();
        System.out.println(
            "[OK] Objeto carregado:"
        );

        System.out.println(
            "CLASS: " + c.getName()
        );

        while (c != null) {

            System.out.println();
            System.out.println(
                "--------------------------------"
            );

            System.out.println(
                "CLASS: " + c.getName()
            );

            System.out.println(
                "--------------------------------"
            );

            for (java.lang.reflect.Field f :
                    c.getDeclaredFields()) {

                try {

                    f.setAccessible(true);

                    Object value = f.get(obj);

                    System.out.println(
                        f.getType().getName()
                        + " "
                        + f.getName()
                        + " = "
                        + format(value)
                    );

                } catch (Throwable e) {

                    System.out.println(
                        f.getType().getName()
                        + " "
                        + f.getName()
                        + " = <inacessível>"
                    );
                }
            }

            c = c.getSuperclass();
        }
    }

    private static String format(Object value) {

        if (value == null)
            return "null";

        if (value instanceof byte[])
            return "byte[" +
                ((byte[]) value).length +
                "]";

        if (value instanceof int[])
            return "int[" +
                ((int[]) value).length +
                "]";

        if (value instanceof long[])
            return "long[" +
                ((long[]) value).length +
                "]";

        if (value instanceof short[])
            return "short[" +
                ((short[]) value).length +
                "]";

        return String.valueOf(value);
    }
}
