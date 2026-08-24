import java.io.*;
import java.net.*;
import java.util.zip.GZIPInputStream;

public class LevelConverter {

    private static final byte[] JAVA_MAGIC = {
        (byte)0xAC,
        (byte)0xED,
        0x00,
        0x05
    };

    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println("       Classic Level Converter");
        System.out.println("======================================");
        System.out.println();

        if (args.length < 1) {
            System.out.println(
                "Uso:"
            );

            System.out.println(
                "java -cp build LevelConverter level.dat [minecraft.jar]"
            );

            return;
        }

        File input = new File(args[0]);

        File jar = args.length >= 2
            ? new File(args[1])
            : null;

        if (!input.isFile()) {
            System.out.println(
                "[ERRO] Arquivo não encontrado: "
                + input
            );
            return;
        }

        try {

            byte[] compressed =
                readFile(input);

            System.out.println(
                "[1] Arquivo original: "
                + compressed.length
                + " bytes"
            );

            System.out.println(
                "[2] Hex inicial:"
            );

            hexDump(
                compressed,
                0,
                Math.min(64, compressed.length)
            );

            byte[] raw =
                gunzip(compressed);

            System.out.println();
            System.out.println(
                "[3] Dados após GZIP: "
                + raw.length
                + " bytes"
            );

            System.out.println();
            System.out.println(
                "[4] Hex após GZIP:"
            );

            hexDump(
                raw,
                0,
                Math.min(128, raw.length)
            );

            int javaOffset =
                findMagic(raw);

            if (javaOffset < 0) {

                System.out.println();
                System.out.println(
                    "[ERRO] AC ED 00 05 não encontrado."
                );

                return;
            }

            System.out.println();
            System.out.println(
                "[OK] Java Serialization encontrada!"
            );

            System.out.println(
                "Offset: "
                + javaOffset
            );

            /*
             * Mostra os bytes anteriores ao stream.
             */

            if (javaOffset > 0) {

                System.out.println();
                System.out.println(
                    "[5] Cabeçalho anterior:"
                );

                hexDump(
                    raw,
                    0,
                    javaOffset
                );
            }

            /*
             * Salva o conteúdo inteiro
             * descompactado.
             */

            File rawFile =
                new File(
                    input.getParentFile(),
                    input.getName() + ".raw"
                );

            writeFile(rawFile, raw);

            System.out.println();
            System.out.println(
                "[OK] RAW salvo:"
            );

            System.out.println(
                rawFile.getAbsolutePath()
            );

            /*
             * Extrai o Java Serialization.
             */

            byte[] serialization =
                new byte[raw.length - javaOffset];

            System.arraycopy(
                raw,
                javaOffset,
                serialization,
                0,
                serialization.length
            );

            File serFile =
                new File(
                    input.getParentFile(),
                    input.getName() + ".ser"
                );

            writeFile(
                serFile,
                serialization
            );

            System.out.println();
            System.out.println(
                "[OK] Serialization salva:"
            );

            System.out.println(
                serFile.getAbsolutePath()
            );

            /*
             * Dump hexadecimal completo
             * do stream Java.
             */

            File hexFile =
                new File(
                    input.getParentFile(),
                    input.getName() + ".hex"
                );

            writeHex(
                serialization,
                hexFile
            );

            System.out.println();
            System.out.println(
                "[OK] HEX salvo:"
            );

            System.out.println(
                hexFile.getAbsolutePath()
            );

            /*
             * Tenta desserializar.
             */

            if (jar != null && jar.isFile()) {

                System.out.println();
                System.out.println(
                    "[6] Tentando desserializar..."
                );

                try {

                    Object obj =
                        deserialize(
                            serialization,
                            jar
                        );

                    System.out.println();
                    System.out.println(
                        "[SUCESSO] Objeto:"
                    );

                    System.out.println(
                        obj.getClass().getName()
                    );

                    dumpObject(obj);

                } catch (Throwable e) {

                    System.out.println();
                    System.out.println(
                        "[AVISO] Não foi possível "
                        + "desserializar ainda:"
                    );

                    e.printStackTrace();
                }
            }

        } catch (Throwable e) {

            System.out.println();
            System.out.println(
                "[ERRO]"
            );

            e.printStackTrace();
        }
    }

    private static byte[] readFile(
            File file)
            throws IOException {

        FileInputStream in =
            new FileInputStream(file);

        try {

            ByteArrayOutputStream out =
                new ByteArrayOutputStream();

            byte[] buffer =
                new byte[8192];

            int n;

            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }

            return out.toByteArray();

        } finally {
            in.close();
        }
    }

    private static byte[] gunzip(
            byte[] input)
            throws IOException {

        ByteArrayInputStream source =
            new ByteArrayInputStream(input);

        GZIPInputStream gzip =
            new GZIPInputStream(source);

        ByteArrayOutputStream out =
            new ByteArrayOutputStream();

        byte[] buffer =
            new byte[8192];

        int n;

        while ((n = gzip.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }

        gzip.close();

        return out.toByteArray();
    }

    private static int findMagic(
            byte[] data) {

        for (int i = 0; i <= data.length - 4; i++) {

            if (data[i] == JAVA_MAGIC[0] &&
                data[i + 1] == JAVA_MAGIC[1] &&
                data[i + 2] == JAVA_MAGIC[2] &&
                data[i + 3] == JAVA_MAGIC[3]) {

                return i;
            }
        }

        return -1;
    }

    private static void writeFile(
            File file,
            byte[] data)
            throws IOException {

        FileOutputStream out =
            new FileOutputStream(file);

        try {
            out.write(data);
        } finally {
            out.close();
        }
    }

    private static void writeHex(
            byte[] data,
            File file)
            throws IOException {

        PrintWriter out =
            new PrintWriter(
                new BufferedWriter(
                    new FileWriter(file)
                )
            );

        try {

            for (int i = 0; i < data.length; i += 16) {

                out.printf(
                    "%08X  ",
                    i
                );

                for (int j = 0; j < 16; j++) {

                    if (i + j < data.length) {

                        out.printf(
                            "%02X ",
                            data[i + j] & 255
                        );

                    } else {
                        out.print("   ");
                    }
                }

                out.print(" ");

                for (int j = 0; j < 16; j++) {

                    if (i + j < data.length) {

                        int c =
                            data[i + j] & 255;

                        if (c >= 32 && c <= 126) {
                            out.print((char)c);
                        } else {
                            out.print('.');
                        }

                    } else {
                        out.print(' ');
                    }
                }

                out.println();
            }

        } finally {
            out.close();
        }
    }

    private static void hexDump(
            byte[] data,
            int offset,
            int length) {

        int end =
            Math.min(
                offset + length,
                data.length
            );

        for (int i = offset; i < end; i += 16) {

            System.out.printf(
                "%08X  ",
                i
            );

            for (int j = 0; j < 16; j++) {

                if (i + j < end) {

                    System.out.printf(
                        "%02X ",
                        data[i + j] & 255
                    );

                } else {

                    System.out.print("   ");
                }
            }

            System.out.print(" ");

            for (int j = 0; j < 16; j++) {

                if (i + j < end) {

                    int c =
                        data[i + j] & 255;

                    if (c >= 32 && c <= 126)
                        System.out.print((char)c);
                    else
                        System.out.print('.');

                } else {

                    System.out.print(' ');
                }
            }

            System.out.println();
        }
    }

    private static Object deserialize(
            byte[] data,
            File jar)
            throws Exception {

        URLClassLoader loader =
            new URLClassLoader(
                new URL[] {
                    jar.toURI().toURL()
                },
                LevelConverter.class
                    .getClassLoader()
            );

        try {

            ByteArrayInputStream bytes =
                new ByteArrayInputStream(data);

            ObjectInputStream in =
                new ObjectInputStream(bytes) {

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

            Object result =
                in.readObject();

            in.close();

            return result;

        } finally {

            loader.close();
        }
    }

    private static void dumpObject(
            Object obj) {

        Class<?> c =
            obj.getClass();

        while (c != null) {

            System.out.println();
            System.out.println(
                "CLASS: " + c.getName()
            );

            for (
                java.lang.reflect.Field f :
                c.getDeclaredFields()
            ) {

                try {

                    f.setAccessible(true);

                    Object value =
                        f.get(obj);

                    System.out.println(
                        "  "
                        + f.getName()
                        + " = "
                        + format(value)
                    );

                } catch (Throwable e) {

                    System.out.println(
                        "  "
                        + f.getName()
                        + " = <erro>"
                    );
                }
            }

            c = c.getSuperclass();
        }
    }

    private static String format(
            Object value) {

        if (value == null)
            return "null";

        if (value instanceof byte[])
            return "byte["
                + ((byte[])value).length
                + "]";

        if (value instanceof int[])
            return "int["
                + ((int[])value).length
                + "]";

        if (value instanceof long[])
            return "long["
                + ((long[])value).length
                + "]";

        return String.valueOf(value);
    }
}
