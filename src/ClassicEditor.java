import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

public class ClassicEditor {

    static Object level;
    static Object player;

    static File levelFile;
    static File jarFile;

    static Scanner sc = new Scanner(System.in);
    
   static List<Class<?>> cachedEntityClasses =
    new ArrayList<Class<?>>();

static boolean entityClassesLoaded = false;

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        welcome();

        if (args.length < 2) {
            usage();
            return;
        }

        levelFile = new File(args[0]);
        jarFile = new File(args[1]);

        System.out.println();
        System.out.println("Loading world...");

        try {

            System.out.println(
                "Level: " +
                levelFile.getAbsolutePath()
            );

            System.out.println(
                "JAR: " +
                jarFile.getAbsolutePath()
            );

            level = LevelIO.load(
                levelFile,
                jarFile
            );

            player = getField(
                level,
                "player"
            );

            System.out.println();
            System.out.println("World loaded.");
            System.out.println();

            menu();

        } catch (Throwable e) {

            System.out.println();
            System.out.println(
                "Could not load the world."
            );

            System.out.println(
                "Error: " + e
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // WELCOME
    // =========================================================

    static void welcome() {

        System.out.println();
        System.out.println(
            "Welcome to ClassicEditor!"
        );

        System.out.println();
        System.out.println(
            "ClassicEditor 0.0.1.1 Beta"
        );

        System.out.println();
        System.out.println(
            "Powered By"
        );

        System.out.println();

        System.out.println(
            "░█████╗░░█████╗░████████╗██████╗░██╗░░░░░░█████╗░██╗░░██╗"
        );

        System.out.println(
            "██╔══██╗██╔══██╗╚══██╔══╝██╔══██╗██║░░░░░██╔══██╗╚██╗██╔╝"
        );

        System.out.println(
            "██║░░╚═╝███████║░░░██║░░░██████╦╝██║░░░░░██║░░██║░╚███╔╝░"
        );

        System.out.println(
            "██║░░██╗██╔══██║░░░██║░░░██╔══██╗██║░░░░░██║░░██║░██╔██╗░"
        );

        System.out.println(
            "╚█████╔╝██║░░██║░░░██║░░░██████╦╝███████╗╚█████╔╝██╔╝╚██╗"
        );

        System.out.println(
            "░╚════╝░╚═╝░░╚═╝░░░╚═╝░░░╚═════╝░╚══════╝░╚════╝░╚═╝░░╚═╝"
        );

        System.out.println();
    }

    // =========================================================
    // USAGE
    // =========================================================

    static void usage() {

        System.out.println("Usage:");
        System.out.println();

        System.out.println(
            "java -cp \"build:c0.30-1.jar\" " +
            "ClassicEditor level.dat c0.30-1.jar"
        );

        System.out.println();
    }

    // =========================================================
    // MENU
    // =========================================================

    static void menu() throws Exception {

        while (true) {

            System.out.println(
                "--------------------------------------"
            );

            System.out.println(
                "              EDITOR"
            );

            System.out.println(
                "--------------------------------------"
            );

            System.out.println(
                "1 - World Info"
            );

            System.out.println(
                "2 - Edit World"
            );

            System.out.println(
                "3 - Player Info"
            );

            System.out.println(
                "4 - Edit Player"
            );

            System.out.println(
                "5 - Inventory"
            );

            System.out.println(
                "6 - Entities"
            );

            System.out.println(
                "7 - Save"
            );

            System.out.println(
                "0 - Exit"
            );

            System.out.println(
                "--------------------------------------"
            );

            System.out.print("Select: ");

            String op =
                sc.nextLine().trim();

            switch (op) {

                case "1":
                    worldInfo();
                    break;

                case "2":
                    editWorld();
                    break;

                case "3":
                    playerInfo();
                    break;

                case "4":
                    editPlayer();
                    break;

                case "5":
                    inventoryMenu();
                    break;

                case "6":
                    entities();
                    break;

                case "7":
                    save();
                    break;

                case "0":

                    System.out.println(
                        "Goodbye!"
                    );

                    return;

                default:

                    System.out.println(
                        "Invalid option."
                    );
            }

            System.out.println();
        }
    }

    // =========================================================
    // REFLECTION
    // =========================================================

    static Field findField(
            Object object,
            String name
    ) throws Exception {

        Class<?> c =
            object.getClass();

        while (c != null) {

            try {

                Field f =
                    c.getDeclaredField(name);

                f.setAccessible(true);

                return f;

            } catch (NoSuchFieldException e) {

                c =
                    c.getSuperclass();
            }
        }

        throw new NoSuchFieldException(
            object.getClass().getName()
            + "."
            + name
        );
    }

    static Object getField(
            Object object,
            String name
    ) throws Exception {

        return findField(
            object,
            name
        ).get(object);
    }

    static void setField(
            Object object,
            String name,
            Object value
    ) throws Exception {

        findField(
            object,
            name
        ).set(object, value);
    }

    static int getInt(
            Object object,
            String field
    ) throws Exception {

        return ((Number)
            getField(object, field)
        ).intValue();
    }

    static float getFloat(
            Object object,
            String field
    ) throws Exception {

        return ((Number)
            getField(object, field)
        ).floatValue();
    }

    static void setInt(
            Object object,
            String field,
            int value
    ) throws Exception {

        setField(
            object,
            field,
            Integer.valueOf(value)
        );
    }

    static void setFloat(
            Object object,
            String field,
            float value
    ) throws Exception {

        setField(
            object,
            field,
            Float.valueOf(value)
        );
    }

    // =========================================================
    // WORLD
    // =========================================================

    static void worldInfo() throws Exception {

        System.out.println();
        System.out.println(
            "========== WORLD =========="
        );

        showSafe(level, "name");
        showSafe(level, "creator");

        showSafe(level, "width");
        showSafe(level, "height");
        showSafe(level, "depth");

        showSafe(level, "xSpawn");
        showSafe(level, "ySpawn");
        showSafe(level, "zSpawn");
        showSafe(level, "rotSpawn");

        showSafe(level, "waterLevel");

        showSafe(level, "skyColor");
        showSafe(level, "fogColor");
        showSafe(level, "cloudColor");

        showSafe(level, "creativeMode");
        showSafe(level, "growTrees");

        try {

            Object blocks =
                getField(level, "blocks");

            if (blocks instanceof byte[]) {

                System.out.println(
                    "blocks : byte[" +
                    ((byte[]) blocks).length +
                    "]"
                );
            }

        } catch (Throwable ignored) {
        }
    }

    // =========================================================
    // EDIT WORLD
    // =========================================================

    static void editWorld() throws Exception {

        while (true) {

            System.out.println();
            System.out.println(
                "========== EDIT WORLD =========="
            );

            System.out.println("1 - Name");
            System.out.println("2 - Creator");
            System.out.println("3 - Spawn");
            System.out.println("4 - Water level");
            System.out.println("5 - Creative mode");
            System.out.println("6 - Grow trees");
            System.out.println("0 - Back");

            System.out.print("Select: ");

            String op =
                sc.nextLine().trim();

            switch (op) {

                case "1":

                    System.out.print("Name: ");

                    setField(
                        level,
                        "name",
                        sc.nextLine()
                    );

                    System.out.println("[OK]");
                    break;

                case "2":

                    System.out.print("Creator: ");

                    setField(
                        level,
                        "creator",
                        sc.nextLine()
                    );

                    System.out.println("[OK]");
                    break;

                case "3":

                    System.out.print("X: ");

                    int x =
                        Integer.parseInt(
                            sc.nextLine()
                        );

                    System.out.print("Y: ");

                    int y =
                        Integer.parseInt(
                            sc.nextLine()
                        );

                    System.out.print("Z: ");

                    int z =
                        Integer.parseInt(
                            sc.nextLine()
                        );

                    setInt(level, "xSpawn", x);
                    setInt(level, "ySpawn", y);
                    setInt(level, "zSpawn", z);

                    System.out.println("[OK]");
                    break;

                case "4":

                    System.out.print(
                        "Water level: "
                    );

                    setInt(
                        level,
                        "waterLevel",
                        Integer.parseInt(
                            sc.nextLine()
                        )
                    );

                    System.out.println("[OK]");
                    break;

                case "5":

                    System.out.print(
                        "Creative (true/false): "
                    );

                    setField(
                        level,
                        "creativeMode",
                        Boolean.valueOf(
                            sc.nextLine()
                        )
                    );

                    System.out.println("[OK]");
                    break;

                case "6":

                    System.out.print(
                        "Grow trees (true/false): "
                    );

                    setField(
                        level,
                        "growTrees",
                        Boolean.valueOf(
                            sc.nextLine()
                        )
                    );

                    System.out.println("[OK]");
                    break;

                case "0":
                    return;

                default:
                    System.out.println(
                        "Invalid option."
                    );
            }
        }
    }

    // =========================================================
    // PLAYER
    // =========================================================

    static void playerInfo() throws Exception {

        player =
            getField(level, "player");

        if (player == null) {

            System.out.println(
                "Player not found."
            );

            return;
        }

        System.out.println();
        System.out.println(
            "========== PLAYER =========="
        );

        showSafe(player, "health");
        showSafe(player, "lastHealth");
        showSafe(player, "arrows");
        showSafe(player, "score");
        showSafe(player, "userType");

        System.out.println();
        System.out.println("POSITION");

        showSafe(player, "x");
        showSafe(player, "y");
        showSafe(player, "z");

        System.out.println();
        System.out.println("ROTATION");

        showSafe(player, "yRot");
        showSafe(player, "xRot");

        showSafe(player, "fallDistance");
        showSafe(player, "removed");
    }

    // =========================================================
    // EDIT PLAYER
    // =========================================================

    static void editPlayer() throws Exception {

        player =
            getField(level, "player");

        if (player == null) {

            System.out.println(
                "Player not found."
            );

            return;
        }

        while (true) {

            System.out.println();
            System.out.println(
                "========== EDIT PLAYER =========="
            );

            System.out.println("1 - Health");
            System.out.println("2 - Arrows");
            System.out.println("3 - Score");
            System.out.println("4 - Position");
            System.out.println("5 - Rotation");
            System.out.println("0 - Back");

            System.out.print("Select: ");

            String op =
                sc.nextLine().trim();

            switch (op) {

                case "1":

                    System.out.print(
                        "Health: "
                    );

                    setInt(
                        player,
                        "health",
                        Integer.parseInt(
                            sc.nextLine()
                        )
                    );

                    System.out.println("[OK]");
                    break;

                case "2":

                    System.out.print(
                        "Arrows: "
                    );

                    setInt(
                        player,
                        "arrows",
                        Integer.parseInt(
                            sc.nextLine()
                        )
                    );

                    System.out.println("[OK]");
                    break;

                case "3":

                    System.out.print(
                        "Score: "
                    );

                    setInt(
                        player,
                        "score",
                        Integer.parseInt(
                            sc.nextLine()
                        )
                    );

                    System.out.println("[OK]");
                    break;

                case "4":

                    System.out.print("X: ");

                    setFloat(
                        player,
                        "x",
                        Float.parseFloat(
                            sc.nextLine()
                        )
                    );

                    System.out.print("Y: ");

                    setFloat(
                        player,
                        "y",
                        Float.parseFloat(
                            sc.nextLine()
                        )
                    );

                    System.out.print("Z: ");

                    setFloat(
                        player,
                        "z",
                        Float.parseFloat(
                            sc.nextLine()
                        )
                    );

                    System.out.println("[OK]");
                    break;

                case "5":

                    System.out.print(
                        "Y rotation: "
                    );

                    setFloat(
                        player,
                        "yRot",
                        Float.parseFloat(
                            sc.nextLine()
                        )
                    );

                    System.out.print(
                        "X rotation: "
                    );

                    setFloat(
                        player,
                        "xRot",
                        Float.parseFloat(
                            sc.nextLine()
                        )
                    );

                    System.out.println("[OK]");
                    break;

                case "0":
                    return;

                default:
                    System.out.println(
                        "Invalid option."
                    );
            }
        }
    }

    // =========================================================
    // INVENTORY
    // =========================================================

    static Object inventory()
        throws Exception {

        player =
            getField(level, "player");

        if (player == null)
            return null;

        return getField(
            player,
            "inventory"
        );
    }

    static void inventoryMenu()
        throws Exception {

        Object inv =
            inventory();

        if (inv == null) {

            System.out.println(
                "Inventory not found."
            );

            return;
        }

        while (true) {

            System.out.println();
            System.out.println(
                "========== INVENTORY =========="
            );

            System.out.println(
                "1 - Show inventory"
            );

            System.out.println(
                "2 - Edit slot"
            );

            System.out.println(
                "3 - Select slot"
            );

            System.out.println(
                "0 - Back"
            );

            System.out.print("Select: ");

            String op =
                sc.nextLine().trim();

            switch (op) {

                case "1":
                    showInventory(inv);
                    break;

                case "2":
                    editSlot(inv);
                    break;

                case "3":

                    System.out.print(
                        "Selected slot: "
                    );

                    setInt(
                        inv,
                        "selected",
                        Integer.parseInt(
                            sc.nextLine()
                        )
                    );

                    System.out.println("[OK]");
                    break;

                case "0":
                    return;

                default:
                    System.out.println(
                        "Invalid option."
                    );
            }
        }
    }

    static void showInventory(
            Object inv
    ) throws Exception {

        int[] slots =
            (int[]) getField(
                inv,
                "slots"
            );

        int[] count =
            (int[]) getField(
                inv,
                "count"
            );

        int[] popTime = null;

        try {

            popTime =
                (int[]) getField(
                    inv,
                    "popTime"
                );

        } catch (Throwable ignored) {
        }

        int selected = -1;

        try {

            selected =
                getInt(
                    inv,
                    "selected"
                );

        } catch (Throwable ignored) {
        }

        System.out.println();
        System.out.println(
            "Selected slot: " +
            selected
        );

        System.out.println();

        for (
            int i = 0;
            i < slots.length;
            i++
        ) {

            int pop = 0;

            if (
                popTime != null &&
                i < popTime.length
            ) {
                pop = popTime[i];
            }

            System.out.printf(
                "Slot %2d: item=%4d count=%4d pop=%d%n",
                i,
                slots[i],
                count[i],
                pop
            );
        }
    }

    // =========================================================
    // EDIT INVENTORY SLOT
    // =========================================================

    static void editSlot(
            Object inv
    ) throws Exception {

        int[] slots =
            (int[]) getField(
                inv,
                "slots"
            );

        int[] count =
            (int[]) getField(
                inv,
                "count"
            );

        System.out.println();

        System.out.print(
            "Slot (0-" +
            (slots.length - 1) +
            "): "
        );

        int slot;

        try {

            slot =
                Integer.parseInt(
                    sc.nextLine().trim()
                );

        } catch (NumberFormatException e) {

            System.out.println(
                "Invalid slot."
            );

            return;
        }

        if (
            slot < 0 ||
            slot >= slots.length
        ) {

            System.out.println(
                "Invalid slot."
            );

            return;
        }

        System.out.print(
            "Item ID: "
        );

        int item;

        try {

            item =
                Integer.parseInt(
                    sc.nextLine().trim()
                );

        } catch (NumberFormatException e) {

            System.out.println(
                "Invalid item ID."
            );

            return;
        }

        System.out.print(
            "Count: "
        );

        int amount;

        try {

            amount =
                Integer.parseInt(
                    sc.nextLine().trim()
                );

        } catch (NumberFormatException e) {

            System.out.println(
                "Invalid count."
            );

            return;
        }

        slots[slot] = item;
        count[slot] = amount;

        System.out.println();
        System.out.println(
            "[OK] Inventory slot updated."
        );
    }

    // =========================================================
    // ENTITIES
    // =========================================================

    static void entities() throws Exception {

    System.out.println();
    System.out.println(
        "========== ENTITIES =========="
    );

    Object blockMap;

    try {
        blockMap = getField(level, "blockMap");
    } catch (Throwable e) {
        System.out.println(
            "BlockMap not found."
        );
        return;
    }

    if (blockMap == null) {
        System.out.println(
            "BlockMap is null."
        );
        return;
    }

    Object list;

    try {
        list = getField(blockMap, "all");
    } catch (Throwable e) {
        System.out.println(
            "BlockMap entity list not found."
        );
        return;
    }

    if (list == null) {
        System.out.println(
            "No entities found."
        );
        return;
    }

    if (!(list instanceof Collection)) {
        System.out.println(
            "Entity list type: " +
            list.getClass().getName()
        );
        return;
    }

    @SuppressWarnings("unchecked")
Collection<Object> collection =
    (Collection<Object>) list;

    System.out.println(
        "Entities: " +
        collection.size()
    );

    System.out.println();

    int index = 0;

    for (Object entity : collection) {

        System.out.println(
            "--------------------------------------"
        );

        System.out.println(
            "#" + index
        );

        System.out.println(
            "Class: " +
            entity.getClass().getName()
        );

        Class<?> superClass =
            entity.getClass().getSuperclass();

        if (superClass != null) {
            System.out.println(
                "Super: " +
                superClass.getName()
            );
        }

        System.out.println();

        System.out.println("POSITION");

        showSafe(entity, "x");
        showSafe(entity, "y");
        showSafe(entity, "z");

        System.out.println();

        System.out.println("ROTATION");

        showSafe(entity, "yRot");
        showSafe(entity, "xRot");

        System.out.println();

        System.out.println("STATUS");

        showSafe(entity, "health");
        showSafe(entity, "removed");

        System.out.println();

        System.out.println(
            "Type: " +
            entityType(entity)
        );

        index++;
    }

    System.out.println(
        "--------------------------------------"
    );

    entityMenu(collection);
}

    // =========================================================
    // ENTITY TYPE
    // =========================================================

    static String entityType(
            Object entity
    ) {

        String name =
            entity.getClass().getSimpleName();

        if (name == null ||
            name.length() == 0) {

            name =
                entity.getClass().getName();
        }

        return name;
    }

    // =========================================================
    // ENTITY MENU
    // =========================================================

   static void entityMenu(
        Collection<Object> collection
) throws Exception {

    while (true) {

        System.out.println();
        System.out.println(
            "========== ENTITY EDITOR =========="
        );

        System.out.println(
            "1 - Edit entity"
        );

        System.out.println(
            "2 - Spawn entity"
        );

        System.out.println(
            "3 - Change entity class"
        );

        System.out.println(
            "4 - Remove entity"
        );

        System.out.println(
            "5 - Show entity classes"
        );

        System.out.println(
            "0 - Back"
        );

        System.out.print(
            "Select: "
        );

        String op =
            sc.nextLine().trim();

        switch (op) {

            case "1":
                editEntity(collection);
                break;

            case "2":
                spawnEntity(collection);
                break;

            case "3":
                changeEntityClass(collection);
                break;

            case "4":
                removeEntity(collection);
                break;

            case "5":
                showEntityClasses(collection);
                break;

            case "0":
                return;

            default:
                System.out.println(
                    "Invalid option."
                );
        }
    }
}

    // =========================================================
    // EDIT ENTITY
    // =========================================================

    static void editEntity(
            Collection<?> collection
    ) throws Exception {

        if (collection.isEmpty()) {

            System.out.println(
                "No entities."
            );

            return;
        }

        List<?> entities =
            new ArrayList<Object>(collection);

        System.out.print(
            "Entity number: "
        );

        int index;

        try {

            index =
                Integer.parseInt(
                    sc.nextLine().trim()
                );

        } catch (NumberFormatException e) {

            System.out.println(
                "Invalid number."
            );

            return;
        }

        if (
            index < 0 ||
            index >= entities.size()
        ) {

            System.out.println(
                "Invalid entity."
            );

            return;
        }

        Object entity =
            entities.get(index);

        System.out.println();
        System.out.println(
            "Editing #" + index
        );

        System.out.println(
            "Class: " +
            entity.getClass().getName()
        );

        while (true) {

            System.out.println();
            System.out.println(
                "========== EDIT ENTITY =========="
            );

            System.out.println(
                "1 - Position"
            );

            System.out.println(
                "2 - Rotation"
            );

            System.out.println(
                "3 - Health"
            );

            System.out.println(
                "4 - Removed"
            );

            System.out.println(
                "0 - Back"
            );

            System.out.print(
                "Select: "
            );

            String op =
                sc.nextLine().trim();

            switch (op) {

                case "1":

                    editEntityPosition(
                        entity
                    );

                    break;

                case "2":

                    editEntityRotation(
                        entity
                    );

                    break;

                case "3":

                    editEntityHealth(
                        entity
                    );

                    break;

                case "4":

                    editEntityRemoved(
                        entity
                    );

                    break;

                case "0":
                    return;

                default:

                    System.out.println(
                        "Invalid option."
                    );
            }
        }
    }

    // =========================================================
    // ENTITY POSITION
    // =========================================================

    static void editEntityPosition(
            Object entity
    ) throws Exception {

        System.out.print(
            "X: "
        );

        float x =
            Float.parseFloat(
                sc.nextLine().trim()
            );

        System.out.print(
            "Y: "
        );

        float y =
            Float.parseFloat(
                sc.nextLine().trim()
            );

        System.out.print(
            "Z: "
        );

        float z =
            Float.parseFloat(
                sc.nextLine().trim()
            );

        setFloatSafe(
            entity,
            "x",
            x
        );

        setFloatSafe(
            entity,
            "y",
            y
        );

        setFloatSafe(
            entity,
            "z",
            z
        );

        System.out.println(
            "[OK] Position updated."
        );
    }

    // =========================================================
    // ENTITY ROTATION
    // =========================================================

    static void editEntityRotation(
            Object entity
    ) throws Exception {

        System.out.print(
            "Y rotation: "
        );

        float yRot =
            Float.parseFloat(
                sc.nextLine().trim()
            );

        System.out.print(
            "X rotation: "
        );

        float xRot =
            Float.parseFloat(
                sc.nextLine().trim()
            );

        setFloatSafe(
            entity,
            "yRot",
            yRot
        );

        setFloatSafe(
            entity,
            "xRot",
            xRot
        );

        System.out.println(
            "[OK] Rotation updated."
        );
    }

    // =========================================================
    // ENTITY HEALTH
    // =========================================================

    static void editEntityHealth(
            Object entity
    ) throws Exception {

        if (!hasField(entity, "health")) {

            System.out.println(
                "This entity has no health field."
            );

            return;
        }

        System.out.print(
            "Health: "
        );

        int health;

        try {

            health =
                Integer.parseInt(
                    sc.nextLine().trim()
                );

        } catch (NumberFormatException e) {

            System.out.println(
                "Invalid health."
            );

            return;
        }

        setInt(
            entity,
            "health",
            health
        );

        System.out.println(
            "[OK] Health updated."
        );
    }

    // =========================================================
    // ENTITY REMOVED
    // =========================================================

    static void editEntityRemoved(
            Object entity
    ) throws Exception {

        if (!hasField(entity, "removed")) {

            System.out.println(
                "This entity has no removed field."
            );

            return;
        }

        System.out.print(
            "Removed (true/false): "
        );

        boolean removed =
            Boolean.parseBoolean(
                sc.nextLine().trim()
            );

        setField(
            entity,
            "removed",
            Boolean.valueOf(removed)
        );

        System.out.println(
            "[OK] Removed updated."
        );
    }

    // =========================================================
    // REMOVE ENTITY FROM LIST
    // =========================================================

    static void removeEntity(
            Collection<?> collection
    ) throws Exception {

        if (collection.isEmpty()) {

            System.out.println(
                "No entities."
            );

            return;
        }

        List<?> entities =
            new ArrayList<Object>(collection);

        System.out.print(
            "Entity number to remove: "
        );

        int index;

        try {

            index =
                Integer.parseInt(
                    sc.nextLine().trim()
                );

        } catch (NumberFormatException e) {

            System.out.println(
                "Invalid number."
            );

            return;
        }

        if (
            index < 0 ||
            index >= entities.size()
        ) {

            System.out.println(
                "Invalid entity."
            );

            return;
        }

        Object entity =
            entities.get(index);

        System.out.println(
            "Entity: " +
            entityType(entity)
        );

        System.out.print(
            "Remove this entity? (yes/no): "
        );

        String confirm =
            sc.nextLine().trim();

        if (!confirm.equalsIgnoreCase("yes")) {

            System.out.println(
                "Cancelled."
            );

            return;
        }

        try {

            if (hasField(entity, "removed")) {

                setField(
                    entity,
                    "removed",
                    Boolean.TRUE
                );

                System.out.println(
                    "[OK] Entity marked as removed."
                );

            } else {

                collection.remove(entity);

                System.out.println(
                    "[OK] Entity removed from list."
                );
            }

        } catch (UnsupportedOperationException e) {

            System.out.println(
                "[ERROR] Entity collection cannot be modified."
            );
        }
    }

// =========================================================
// ENTITY CLASS DISCOVERY
// =========================================================

static List<Class<?>> getEntityClasses()
        throws Exception {

    if (entityClassesLoaded)
        return cachedEntityClasses;

    cachedEntityClasses.clear();

    Set<String> names =
        new TreeSet<String>();

    /*
     * Primeiro tenta encontrar classes diretamente
     * dentro do JAR fornecido.
     */
    JarFile jar =
        new JarFile(jarFile);

    try {

        Enumeration<JarEntry> entries =
            jar.entries();

        while (entries.hasMoreElements()) {

            JarEntry entry =
                entries.nextElement();

            if (entry.isDirectory())
                continue;

            String name =
                entry.getName();

            if (!name.endsWith(".class"))
                continue;

            if (name.indexOf('$') >= 0)
                continue;

            name =
                name.substring(
                    0,
                    name.length() - 6
                );

            name =
                name.replace('/', '.');

            names.add(name);
        }

    } finally {

        jar.close();
    }

    /*
     * Descobre a classe base das entidades.
     *
     * Usamos uma entidade que já existe no mundo
     * quando possível.
     */
    Class<?> entityBase =
        findEntityBaseClass();

    URL[] urls = {
        jarFile.toURI().toURL()
    };

    URLClassLoader loader =
        new URLClassLoader(
            urls,
            ClassicEditor.class.getClassLoader()
        );

    try {

        for (String name : names) {

            try {

                Class<?> clazz =
                    Class.forName(
                        name,
                        false,
                        loader
                    );

                if (clazz.isInterface())
                    continue;

                if (Modifier.isAbstract(
                        clazz.getModifiers()))
                    continue;

                if (entityBase != null) {

                    if (!entityBase.isAssignableFrom(
                            clazz))
                        continue;
                }

                /*
                 * Evita adicionar a própria classe base.
                 */
                if (clazz == entityBase)
                    continue;

                cachedEntityClasses.add(clazz);

            } catch (Throwable ignored) {
                /*
                 * Muitas classes antigas do Minecraft
                 * podem depender de classes que não precisam
                 * ser carregadas durante a descoberta.
                 */
            }
        }

    } finally {

        try {
            loader.close();
        } catch (Throwable ignored) {
        }
    }

    /*
     * Ordena alfabeticamente pelo nome.
     */
    Collections.sort(
        cachedEntityClasses,
        new Comparator<Class<?>>() {

            public int compare(
                    Class<?> a,
                    Class<?> b
            ) {

                return a.getName().compareTo(
                    b.getName()
                );
            }
        }
    );

    entityClassesLoaded = true;

    return cachedEntityClasses;
}

static Class<?> findEntityBaseClass() {

    try {

        Object blockMap =
            getField(level, "blockMap");

        Object list =
            getField(blockMap, "all");

        if (!(list instanceof Collection))
            return null;

        Collection<?> collection =
            (Collection<?>) list;

        for (Object entity : collection) {

            if (entity == null)
                continue;

            Class<?> c =
                entity.getClass();

            /*
             * Subimos a hierarquia até encontrar
             * uma classe suficientemente genérica.
             *
             * Normalmente isso acaba em Entity,
             * Mob ou uma classe equivalente.
             */
            Class<?> candidate = c;

            while (
                candidate.getSuperclass() != null &&
                candidate.getSuperclass() != Object.class
            ) {

                Class<?> parent =
                    candidate.getSuperclass();

                if (parent.getName().toLowerCase()
                        .indexOf("entity") >= 0) {

                    candidate = parent;
                    break;
                }

                candidate = parent;
            }

            return candidate;
        }

    } catch (Throwable ignored) {
    }

    return null;
}

static Class<?> selectEntityClass()
        throws Exception {

    List<Class<?>> classes =
        getEntityClasses();

    if (classes.isEmpty()) {

        System.out.println(
            "No entity classes found in JAR."
        );

        return null;
    }

    System.out.println();
    System.out.println(
        "========== AVAILABLE ENTITIES =========="
    );

    for (int i = 0; i < classes.size(); i++) {

        Class<?> c =
            classes.get(i);

        System.out.printf(
            "%3d - %s%n",
            i,
            simpleEntityName(c)
        );
    }

    System.out.println();
    System.out.print(
        "Select class: "
    );

    int index;

    try {

        index =
            Integer.parseInt(
                sc.nextLine().trim()
            );

    } catch (NumberFormatException e) {

        System.out.println(
            "Invalid class."
        );

        return null;
    }

    if (
        index < 0 ||
        index >= classes.size()
    ) {

        System.out.println(
            "Invalid class."
        );

        return null;
    }

    return classes.get(index);
}

static String simpleEntityName(
        Class<?> clazz
) {

    String name =
        clazz.getSimpleName();

    if (
        name == null ||
        name.length() == 0
    ) {

        name =
            clazz.getName();

        int p =
            name.lastIndexOf('.');

        if (p >= 0)
            name =
                name.substring(p + 1);
    }

    return name;
}

// =========================================================
// SPAWN ENTITY
// =========================================================

static void spawnEntity(
        Collection<Object> collection
) throws Exception {

    Class<?> clazz =
        selectEntityClass();

    if (clazz == null)
        return;

    System.out.println();
    System.out.println(
        "Spawning: " +
        simpleEntityName(clazz)
    );

    System.out.print("X: ");

    float x;

    try {

        x =
            Float.parseFloat(
                sc.nextLine().trim()
            );

    } catch (NumberFormatException e) {

        System.out.println(
            "Invalid X."
        );

        return;
    }

    System.out.print("Y: ");

    float y;

    try {

        y =
            Float.parseFloat(
                sc.nextLine().trim()
            );

    } catch (NumberFormatException e) {

        System.out.println(
            "Invalid Y."
        );

        return;
    }

    System.out.print("Z: ");

    float z;

    try {

        z =
            Float.parseFloat(
                sc.nextLine().trim()
            );

    } catch (NumberFormatException e) {

        System.out.println(
            "Invalid Z."
        );

        return;
    }

    Object entity;

    try {

        entity =
            createEntity(clazz);

    } catch (Throwable e) {

        System.out.println();
        System.out.println(
            "[ERROR] Could not create entity."
        );

        System.out.println(
            "Class: " +
            clazz.getName()
        );

        System.out.println(
            "Reason: " +
            e
        );

        return;
    }

    if (entity == null) {

        System.out.println(
            "[ERROR] Entity constructor returned null."
        );

        return;
    }

    /*
     * Define o mundo/level caso a classe possua
     * um campo compatível.
     */
    setEntityLevel(entity);

    /*
     * Define posição.
     */
    setFloatSafe(
        entity,
        "x",
        x
    );

    setFloatSafe(
        entity,
        "y",
        y
    );

    setFloatSafe(
        entity,
        "z",
        z
    );

    /*
     * Alguns Entity antigos possuem métodos
     * específicos para posição.
     */
    trySetPositionMethod(
        entity,
        x,
        y,
        z
    );

    /*
     * Adiciona ao BlockMap.
     */
    try {

        collection.add(entity);

    } catch (UnsupportedOperationException e) {

        System.out.println(
            "[ERROR] Entity collection cannot be modified."
        );

        return;
    }

    System.out.println();
    System.out.println(
        "[OK] Entity spawned:"
    );

    System.out.println(
        simpleEntityName(clazz)
    );

    System.out.println(
        "Position: " +
        x + ", " +
        y + ", " +
        z
    );
}

// =========================================================
// CREATE ENTITY
// =========================================================

static Object createEntity(
        Class<?> clazz
) throws Exception {

    Constructor<?>[] constructors =
        clazz.getDeclaredConstructors();

    /*
     * Primeiro tentamos construtores que recebem
     * o Level/World atual.
     */
    for (
        Constructor<?> constructor :
        constructors
    ) {

        Class<?>[] params =
            constructor.getParameterTypes();

        if (params.length != 1)
            continue;

        if (
            level != null &&
            params[0].isAssignableFrom(
                level.getClass()
            )
        ) {

            constructor.setAccessible(true);

            return constructor.newInstance(
                level
            );
        }
    }

    /*
     * Alguns builds podem receber uma classe-base
     * compatível com Level.
     */
    for (
        Constructor<?> constructor :
        constructors
    ) {

        Class<?>[] params =
            constructor.getParameterTypes();

        if (params.length != 1)
            continue;

        if (
            level != null &&
            isCompatibleParameter(
                params[0],
                level
            )
        ) {

            constructor.setAccessible(true);

            return constructor.newInstance(
                level
            );
        }
    }

    /*
     * Por último tentamos construtor vazio.
     */
    for (
        Constructor<?> constructor :
        constructors
    ) {

        if (
            constructor.getParameterTypes().length == 0
        ) {

            constructor.setAccessible(true);

            return constructor.newInstance();
        }
    }

    throw new NoSuchMethodException(
        "No compatible constructor found for " +
        clazz.getName()
    );
}

static boolean isCompatibleParameter(
        Class<?> parameter,
        Object value
) {

    if (value == null)
        return !parameter.isPrimitive();

    return parameter.isAssignableFrom(
        value.getClass()
    );
}

static void setEntityLevel(
        Object entity
) {

    String[] possibleFields = {
        "level",
        "world",
        "worldObj"
    };

    for (String field : possibleFields) {

        try {

            Field f =
                findField(
                    entity,
                    field
                );

            if (
                level != null &&
                f.getType().isAssignableFrom(
                    level.getClass()
                )
            ) {

                f.set(
                    entity,
                    level
                );

                return;
            }

        } catch (Throwable ignored) {
        }
    }
}

static void trySetPositionMethod(
        Object entity,
        float x,
        float y,
        float z
) {

    try {

        Method[] methods =
            entity.getClass().getMethods();

        for (Method method : methods) {

            String name =
                method.getName();

            if (
                !name.equals("setPosition") &&
                !name.equals("setPos")
            )
                continue;

            Class<?>[] params =
                method.getParameterTypes();

            if (params.length != 3)
                continue;

            if (
                params[0] == Float.TYPE &&
                params[1] == Float.TYPE &&
                params[2] == Float.TYPE
            ) {

                method.setAccessible(true);

                method.invoke(
                    entity,
                    Float.valueOf(x),
                    Float.valueOf(y),
                    Float.valueOf(z)
                );

                return;
            }

            if (
                params[0] == Double.TYPE &&
                params[1] == Double.TYPE &&
                params[2] == Double.TYPE
            ) {

                method.setAccessible(true);

                method.invoke(
                    entity,
                    Double.valueOf(x),
                    Double.valueOf(y),
                    Double.valueOf(z)
                );

                return;
            }
        }

    } catch (Throwable ignored) {
    }
}

// =========================================================
// CHANGE ENTITY CLASS
// =========================================================

static void changeEntityClass(
        Collection<Object> collection
) throws Exception {

    if (collection.isEmpty()) {

        System.out.println(
            "No entities."
        );

        return;
    }

    List<Object> entities =
        new ArrayList<Object>();

    for (Object e : collection)
        entities.add(e);

    System.out.println();

    for (int i = 0; i < entities.size(); i++) {

        Object entity =
            entities.get(i);

        System.out.printf(
            "#%d - %s%n",
            i,
            entityType(entity)
        );
    }

    System.out.println();

    System.out.print(
        "Entity number: "
    );

    int index;

    try {

        index =
            Integer.parseInt(
                sc.nextLine().trim()
            );

    } catch (NumberFormatException e) {

        System.out.println(
            "Invalid entity."
        );

        return;
    }

    if (
        index < 0 ||
        index >= entities.size()
    ) {

        System.out.println(
            "Invalid entity."
        );

        return;
    }

    Object oldEntity =
        entities.get(index);

    System.out.println();

    System.out.println(
        "Current class: " +
        oldEntity.getClass().getName()
    );

    Class<?> newClass =
        selectEntityClass();

    if (newClass == null)
        return;

    if (
        newClass ==
        oldEntity.getClass()
    ) {

        System.out.println(
            "Entity already has this class."
        );

        return;
    }

    /*
     * Guarda os dados da entidade antiga.
     */
    float x =
        getFloatSafe(
            oldEntity,
            "x",
            0.0F
        );

    float y =
        getFloatSafe(
            oldEntity,
            "y",
            0.0F
        );

    float z =
        getFloatSafe(
            oldEntity,
            "z",
            0.0F
        );

    float yRot =
        getFloatSafe(
            oldEntity,
            "yRot",
            0.0F
        );

    float xRot =
        getFloatSafe(
            oldEntity,
            "xRot",
            0.0F
        );

    Object newEntity;

    try {

        newEntity =
            createEntity(newClass);

    } catch (Throwable e) {

        System.out.println();
        System.out.println(
            "[ERROR] Could not create new entity."
        );

        System.out.println(
            "Reason: " +
            e
        );

        return;
    }

    if (newEntity == null) {

        System.out.println(
            "[ERROR] New entity is null."
        );

        return;
    }

    setEntityLevel(newEntity);

    /*
     * Copia posição.
     */
    setFloatSafe(
        newEntity,
        "x",
        x
    );

    setFloatSafe(
        newEntity,
        "y",
        y
    );

    setFloatSafe(
        newEntity,
        "z",
        z
    );

    /*
     * Copia rotação.
     */
    setFloatSafe(
        newEntity,
        "yRot",
        yRot
    );

    setFloatSafe(
        newEntity,
        "xRot",
        xRot
    );

    trySetPositionMethod(
        newEntity,
        x,
        y,
        z
    );

    /*
     * Tenta substituir a entidade.
     */
    try {

        boolean removed =
            collection.remove(
                oldEntity
            );

        if (!removed) {

            System.out.println(
                "[ERROR] Could not remove old entity."
            );

            return;
        }

        try {

            collection.add(
                newEntity
            );

        } catch (Throwable addError) {

            /*
             * Se não conseguiu adicionar a nova,
             * tenta devolver a antiga.
             */
            try {
                collection.add(oldEntity);
            } catch (Throwable ignored) {
            }

            System.out.println(
                "[ERROR] Could not add new entity."
            );

            System.out.println(
                "Reason: " +
                addError
            );

            return;
        }

    } catch (UnsupportedOperationException e) {

        System.out.println(
            "[ERROR] Entity collection cannot be modified."
        );

        return;
    }

    /*
     * Marca a antiga como removida quando possível.
     */
    try {

        if (hasField(oldEntity, "removed")) {

            setField(
                oldEntity,
                "removed",
                Boolean.TRUE
            );
        }

    } catch (Throwable ignored) {
    }

    System.out.println();
    System.out.println(
        "[OK] Entity class changed."
    );

    System.out.println(
        "Old: " +
        oldEntity.getClass().getName()
    );

    System.out.println(
        "New: " +
        newEntity.getClass().getName()
    );

    System.out.println(
        "Position preserved: " +
        x + ", " +
        y + ", " +
        z
    );
}

static float getFloatSafe(
        Object object,
        String field,
        float defaultValue
) {

    try {

        return getFloat(
            object,
            field
        );

    } catch (Throwable e) {

        return defaultValue;
    }
}

static void showEntityClasses(
        Collection<?> collection
) {

    System.out.println();

    System.out.println(
        "========== CURRENT ENTITY CLASSES =========="
    );

    Set<String> current =
        new TreeSet<String>();

    for (Object entity : collection) {

        if (entity == null)
            continue;

        current.add(
            entity.getClass().getName()
        );
    }

    if (current.isEmpty()) {

        System.out.println(
            "No entities currently loaded."
        );

    } else {

        for (String name : current) {

            System.out.println(
                name
            );
        }
    }

    System.out.println();

    try {

        List<Class<?>> available =
            getEntityClasses();

        System.out.println(
            "========== AVAILABLE ENTITY CLASSES =========="
        );

        for (
            int i = 0;
            i < available.size();
            i++
        ) {

            System.out.printf(
                "%3d - %s%n",
                i,
                available.get(i).getName()
            );
        }

        System.out.println();

        System.out.println(
            "Available classes: " +
            available.size()
        );

    } catch (Throwable e) {

        System.out.println(
            "[WARN] Could not scan entity classes."
        );

        System.out.println(
            "Reason: " +
            e
        );
    }
}


    // =========================================================

    static void setFloatSafe(
            Object object,
            String field,
            float value
    ) throws Exception {

        if (!hasField(object, field)) {

            System.out.println(
                "[WARN] Field not found: " +
                field
            );

            return;
        }

        setFloat(
            object,
            field,
            value
        );
    }

    // =========================================================
    // SAVE
    // =========================================================

    static void save() throws Exception {

        String output =
            levelFile.getAbsolutePath() +
            ".edited";

        File edited =
            new File(output);

        System.out.println();
        System.out.println(
            "Saving world..."
        );

        LevelIO.save(
            level,
            edited,
            jarFile
        );

        System.out.println();
        System.out.println(
            "[OK] World saved:"
        );

        System.out.println(
            edited.getAbsolutePath()
        );

        System.out.println();
        System.out.println(
            "Original level was not modified."
        );
    }

    // =========================================================
    // HELPERS
    // =========================================================

    static boolean hasField(
            Object object,
            String name
    ) {

        try {

            findField(
                object,
                name
            );

            return true;

        } catch (Throwable e) {

            return false;
        }
    }

    static void showSafe(
            Object object,
            String field
    ) {

        try {

            show(
                object,
                field
            );

        } catch (Throwable ignored) {
        }
    }

    static void show(
            Object object,
            String field
    ) throws Exception {

        Object value =
            getField(
                object,
                field
            );

        if (value instanceof byte[]) {

            System.out.println(
                field +
                " : byte[" +
                ((byte[]) value).length +
                "]"
            );

        } else if (value instanceof int[]) {

            System.out.println(
                field +
                " : int[" +
                ((int[]) value).length +
                "]"
            );

        } else if (value instanceof float[]) {

            System.out.println(
                field +
                " : float[" +
                ((float[]) value).length +
                "]"
            );

        } else if (value instanceof Object[]) {

            System.out.println(
                field +
                " : Object[" +
                ((Object[]) value).length +
                "]"
            );

        } else {

            System.out.println(
                field +
                " : " +
                String.valueOf(value)
            );
        }
    }

}
