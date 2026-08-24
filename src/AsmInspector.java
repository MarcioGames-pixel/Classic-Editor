import java.io.*;
import java.util.jar.*;

import org.objectweb.asm.*;

public class AsmInspector {

    public static void main(String[] args)
            throws Exception {

        if (args.length < 2) {

            System.out.println(
                "Uso:"
            );

            System.out.println(
                "java -cp build:asm.jar " +
                "AsmInspector minecraft.jar " +
                "com/mojang/minecraft/level/Level"
            );

            return;
        }

        File jarFile =
            new File(args[0]);

        String className =
            args[1];

        if (!className.endsWith(".class"))
            className += ".class";

        JarFile jar =
            new JarFile(jarFile);

        JarEntry entry =
            jar.getJarEntry(className);

        if (entry == null) {

            System.out.println(
                "[ERRO] Classe não encontrada:"
            );

            System.out.println(
                className
            );

            jar.close();

            return;
        }

        System.out.println(
            "[OK] Classe encontrada:"
        );

        System.out.println(
            className
        );

        System.out.println();

        InputStream input =
            jar.getInputStream(entry);

        ClassReader reader =
            new ClassReader(input);

        reader.accept(
            new ClassVisitor(
                Opcodes.ASM4
            ) {

                @Override
                public void visit(
                    int version,
                    int access,
                    String name,
                    String signature,
                    String superName,
                    String[] interfaces) {

                    System.out.println(
                        "CLASS  : " + name
                    );

                    System.out.println(
                        "SUPER  : " + superName
                    );

                    System.out.println(
                        "VERSION: " + version
                    );

                    System.out.println();
                }

                @Override
                public FieldVisitor visitField(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    Object value) {

                    System.out.println(
                        "FIELD  "
                        + descriptor
                        + " "
                        + name
                        + " = "
                        + value
                    );

                    return null;
                }

                @Override
                public MethodVisitor visitMethod(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    String[] exceptions) {

                    System.out.println(
                        "METHOD "
                        + name
                        + descriptor
                    );

                    return null;
                }
            },
            ClassReader.SKIP_DEBUG
        );

        input.close();
        jar.close();
    }
}
