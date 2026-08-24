import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

public final class LevelIO {

    private LevelIO() {
    }

    /*
     * Carrega level.dat do Minecraft Classic c0.30-1.
     *
     * Formato:
     *
     * GZIP
     *   |
     *   +-- 5 bytes proprietários
     *   |
     *   +-- Java Serialization (AC ED 00 05...)
     */
    public static Object load(
            File levelFile,
            File jarFile
    ) throws Exception {

        System.out.println("[*] Descompactando GZIP...");

        byte[] compressed =
            readFile(levelFile);

        byte[] data =
            gunzip(compressed);

        int offset =
            findSerialization(data);

        if (offset < 0) {
            throw new IOException(
                "Java Serialization não encontrada"
            );
        }

        System.out.println(
            "[OK] Prefixo encontrado: " +
            offset +
            " bytes"
        );

        byte[] serialization =
            Arrays.copyOfRange(
                data,
                offset,
                data.length
            );

        System.out.println(
            "[*] Desserializando Level..."
        );

        URLClassLoader loader =
            new URLClassLoader(
                new URL[] {
                    jarFile.toURI().toURL()
                },
                LevelIO.class.getClassLoader()
            );

        try {

            ObjectInputStream in =
                new ObjectInputStream(
                    new ByteArrayInputStream(
                        serialization
                    )
                ) {

                    @Override
                    protected Class<?> resolveClass(
                            ObjectStreamClass desc
                    )
                            throws IOException,
                                   ClassNotFoundException {

                        try {
                            return Class.forName(
                                desc.getName(),
                                false,
                                loader
                            );
                        } catch (ClassNotFoundException e) {
                            return super.resolveClass(desc);
                        }
                    }
                };

            Object object =
                in.readObject();

            in.close();

            System.out.println(
                "[SUCESSO] Objeto carregado:"
            );

            System.out.println(
                object.getClass().getName()
            );

            return object;

        } finally {

            loader.close();
        }
    }

    /*
     * Salva novamente no formato do Classic.
     */
    public static void save(
            Object level,
            File levelFile,
            File jarFile
    ) throws Exception {

        System.out.println(
            "[*] Serializando Level..."
        );

        ByteArrayOutputStream raw =
            new ByteArrayOutputStream();

        ObjectOutputStream out =
            new ObjectOutputStream(raw);

        out.writeObject(level);
        out.flush();
        out.close();

        byte[] serialization =
            raw.toByteArray();

        System.out.println(
            "[OK] Serialization: " +
            serialization.length +
            " bytes"
        );

        /*
         * O seu level.dat começa, depois do GZIP,
         * com estes 5 bytes:
         *
         * 27 1B B7 88 02
         *
         * Depois vem:
         *
         * AC ED 00 05
         */
        byte[] prefix = {
            0x27,
            0x1B,
            (byte)0xB7,
            (byte)0x88,
            0x02
        };

        ByteArrayOutputStream payload =
            new ByteArrayOutputStream();

        payload.write(prefix);
        payload.write(serialization);

        byte[] uncompressed =
            payload.toByteArray();

        System.out.println(
            "[*] Compactando GZIP..."
        );

        FileOutputStream fos =
            new FileOutputStream(levelFile);

        GZIPOutputStream gzip =
            new GZIPOutputStream(fos);

        gzip.write(uncompressed);
        gzip.finish();
        gzip.close();

        System.out.println(
            "[OK] Level salvo: " +
            levelFile.getAbsolutePath()
        );
    }

    private static byte[] readFile(
            File file
    ) throws IOException {

        FileInputStream in =
            new FileInputStream(file);

        ByteArrayOutputStream out =
            new ByteArrayOutputStream();

        byte[] buffer =
            new byte[8192];

        int n;

        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }

        in.close();

        return out.toByteArray();
    }

    private static byte[] gunzip(
            byte[] input
    ) throws IOException {

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

    private static int findSerialization(
            byte[] data
    ) {

        /*
         * Java Object Serialization:
         *
         * AC ED 00 05
         */

        for (
            int i = 0;
            i + 3 < data.length;
            i++
        ) {

            if (
                (data[i] & 0xFF) == 0xAC &&
                (data[i + 1] & 0xFF) == 0xED &&
                (data[i + 2] & 0xFF) == 0x00 &&
                (data[i + 3] & 0xFF) == 0x05
            ) {

                return i;
            }
        }

        return -1;
    }
}
