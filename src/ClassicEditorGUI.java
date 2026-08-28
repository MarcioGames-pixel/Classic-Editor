import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;

public class ClassicEditorGUI extends JFrame {

    private static final Font UI_FONT =
        new Font("SansSerif", Font.PLAIN, 13);

    private static final Font MONO_FONT =
        new Font("Monospaced", Font.PLAIN, 12);

    private Object level;
    private File levelFile;
    private File jarFile;
    
    private final JTextArea log = new JTextArea();
    private final JLabel status = new JLabel(" Ready");
    private final JLabel worldLabel =
        new JLabel("No world loaded");

    private final DefaultListModel<Object> entityModel =
        new DefaultListModel<Object>();

    private final JList<Object> entityList =
        new JList<Object>(entityModel);

    private final JTable entityTable =
        new JTable();

   private final List<Object> visibleEntities =
    new ArrayList<Object>();

private Object selectedEntity;

    private final JTextField searchField =
        new JTextField();

    private final JPanel propertyPanel =
        new JPanel(new BorderLayout());

    private final CardLayout card =
        new CardLayout();

    private final JPanel content =
        new JPanel(card);

    private final Color BG =
        new Color(28, 30, 34);

    private final Color PANEL =
        new Color(36, 39, 44);

    private final Color PANEL2 =
        new Color(43, 46, 52);

    private final Color BORDER =
        new Color(65, 69, 77);

    private final Color TEXT =
        new Color(225, 228, 232);

    private final Color MUTED =
        new Color(155, 160, 170);

    public ClassicEditorGUI() {

        setTitle("ClassicEditor 0.0.1.1 Beta");
        setSize(1100, 700);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        installLookAndFeel();
        buildUI();

        logLine("ClassicEditor ready.");
        logLine("Open a level.dat and Minecraft JAR.");
    }

    private void installLookAndFeel() {

        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Throwable ignored) {
        }

        UIManager.put(
            "ToolTip.background",
            PANEL2
        );

        UIManager.put(
            "ToolTip.foreground",
            TEXT
        );
    }

    private void buildUI() {

        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildMain(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {

        JPanel top =
            new JPanel(new BorderLayout());

        top.setBackground(PANEL);
        top.setBorder(
            new MatteBorder(
                0, 0, 1, 0,
                BORDER
            )
        );

        JPanel left =
            new JPanel(new FlowLayout(
                FlowLayout.LEFT,
                8,
                8
            ));

        left.setOpaque(false);

        JLabel title =
            new JLabel("ClassicEditor");

        title.setForeground(TEXT);
        title.setFont(
            new Font(
                "SansSerif",
                Font.BOLD,
                18
            )
        );

        JLabel version =
            new JLabel("0.0.1.1 Beta");

        version.setForeground(MUTED);

        left.add(title);
        left.add(version);

        JPanel buttons =
            new JPanel(new FlowLayout(
                FlowLayout.RIGHT,
                5,
                6
            ));

        buttons.setOpaque(false);

        JButton open =
            button("Open", "Open level.dat");

        JButton save =
            button("Save", "Save edited world");

        JButton reload =
            button("Reload", "Reload world");

        JButton about =
            button("About", "About ClassicEditor");

        buttons.add(open);
        buttons.add(save);
        buttons.add(reload);
        buttons.add(about);

        top.add(left, BorderLayout.WEST);
        top.add(buttons, BorderLayout.EAST);

        open.addActionListener(
            e -> openWorld()
        );

        save.addActionListener(
            e -> saveWorld()
        );

        reload.addActionListener(
            e -> reloadWorld()
        );

        about.addActionListener(
            e -> showAbout()
        );

        return top;
    }

    private JPanel buildMain() {

        JPanel main =
            new JPanel(new BorderLayout());

        main.setBackground(BG);

        JPanel navigation =
            buildNavigation();

        JPanel center =
            buildContent();

        main.add(
            navigation,
            BorderLayout.WEST
        );

        main.add(
            center,
            BorderLayout.CENTER
        );

        return main;
    }

    private JPanel buildNavigation() {

        JPanel nav =
            new JPanel(new BorderLayout());

        nav.setPreferredSize(
            new Dimension(220, 0)
        );

        nav.setBackground(PANEL);
        nav.setBorder(
            new MatteBorder(
                0, 0, 1, 0,
                BORDER
            )
        );

        JLabel header =
            new JLabel("  WORLD");

        header.setForeground(MUTED);
        header.setFont(
            new Font(
                "SansSerif",
                Font.BOLD,
                11
            )
        );

        nav.add(
            header,
            BorderLayout.NORTH
        );

        JPanel tree =
            new JPanel();

        tree.setLayout(
            new BoxLayout(
                tree,
                BoxLayout.Y_AXIS
            )
        );

        tree.setBackground(PANEL);

        JButton world =
            navButton("🌍  World");

        JButton player =
            navButton("👤  Player");

        JButton inventory =
            navButton("🎒  Inventory");

        JButton entities =
            navButton("◆  Entities");

        JButton logButton =
            navButton("▤  Console");

        tree.add(world);
        tree.add(player);
        tree.add(inventory);
        tree.add(entities);
        tree.add(logButton);

        nav.add(
            tree,
            BorderLayout.CENTER
        );

        world.addActionListener(
            e -> showWorld()
        );

        player.addActionListener(
            e -> showPlayer()
        );

        inventory.addActionListener(
            e -> showInventory()
        );

        entities.addActionListener(
            e -> showEntities()
        );

        logButton.addActionListener(
            e -> showConsole()
        );

        return nav;
    }

    private JPanel buildContent() {

    content.setBackground(BG);

    content.add(
        buildWelcome(),
        "welcome"
    );

    content.add(
        buildWorldPanel(),
        "world"
    );

    content.add(
        buildPlayerPanel(),
        "player"
    );

    content.add(
        buildInventoryPanel(),
        "inventory"
    );

    content.add(
        buildEntitiesPanel(),
        "entities"
    );

    content.add(
        buildConsolePanel(),
        "console"
    );

    card.show(content, "welcome");

    return content;
}

    private JPanel buildWelcome() {

        JPanel p =
            new JPanel(new GridBagLayout());

        p.setBackground(BG);

        JPanel box =
            new JPanel();

        box.setLayout(
            new BoxLayout(
                box,
                BoxLayout.Y_AXIS
            )
        );

        box.setBackground(PANEL);
        box.setBorder(
            new CompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(
                    35, 45, 35, 45
                )
            )
        );

        JLabel title =
            new JLabel("ClassicEditor");

        title.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        title.setForeground(TEXT);
        title.setFont(
            new Font(
                "SansSerif",
                Font.BOLD,
                30
            )
        );

        JLabel sub =
            new JLabel(
                "Minecraft Classic save editor"
            );

        sub.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        sub.setForeground(MUTED);

        JButton open =
            button(
                "Open level.dat",
                "Open a Minecraft Classic world"
            );

        open.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        open.addActionListener(
            e -> openWorld()
        );

        box.add(title);
        box.add(Box.createVerticalStrut(8));
        box.add(sub);
        box.add(Box.createVerticalStrut(25));
        box.add(open);

        p.add(box);

        return p;
    }

    private JPanel buildWorldPanel() {

    JPanel p = basePanel("World Information");

    JPanel grid = new JPanel(
        new GridLayout(0, 2, 8, 8)
    );

    grid.setBackground(BG);
    grid.setBorder(
        new EmptyBorder(15, 15, 15, 15)
    );

    addProperty(grid, "Name", "");
    addProperty(grid, "Creator", "");
    addProperty(grid, "Width", "");
    addProperty(grid, "Height", "");
    addProperty(grid, "Depth", "");
    addProperty(grid, "Spawn X", "");
    addProperty(grid, "Spawn Y", "");
    addProperty(grid, "Spawn Z", "");
    addProperty(grid, "Creative", "");
    addProperty(grid, "Water Level", "");

    p.putClientProperty("worldGrid", grid);

    p.add(
        new JScrollPane(grid),
        BorderLayout.CENTER
    );

    return p;
}

    private JPanel buildPlayerPanel() {

    JPanel p = basePanel("Player");

    JPanel grid = new JPanel(
        new GridLayout(0, 2, 8, 8)
    );

    grid.setBackground(BG);

    grid.setBorder(
        new EmptyBorder(15, 15, 15, 15)
    );

    p.putClientProperty(
        "playerGrid",
        grid
    );

    p.add(
        new JScrollPane(grid),
        BorderLayout.CENTER
    );

    return p;
}

    private JPanel buildInventoryPanel() {

    JPanel p = basePanel("Inventory");

    String[] columns = {
        "Slot",
        "Item ID",
        "Count",
        "Pop"
    };

    DefaultTableModel model =
        new DefaultTableModel(columns, 0) {

            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

    JTable table =
        new JTable(model);

    table.setBackground(PANEL);
    table.setForeground(TEXT);
    table.setGridColor(BORDER);
    table.setRowHeight(25);
    table.setFont(MONO_FONT);

    table.getTableHeader().setBackground(PANEL2);
    table.getTableHeader().setForeground(TEXT);
    table.getTableHeader().setFont(
        new Font(
            "SansSerif",
            Font.BOLD,
            12
        )
    );

    table.setSelectionMode(
        ListSelectionModel.SINGLE_SELECTION
    );

    p.putClientProperty(
        "inventoryTable",
        table
    );

    JPanel top =
        new JPanel(
            new BorderLayout()
        );

    top.setBackground(PANEL);

    JLabel selectedLabel =
        new JLabel(" Selected slot: -");

    selectedLabel.setForeground(MUTED);
    selectedLabel.setFont(UI_FONT);

    top.add(
        selectedLabel,
        BorderLayout.WEST
    );

    p.putClientProperty(
        "inventorySelected",
        selectedLabel
    );

    p.add(
        top,
        BorderLayout.NORTH
    );

    p.add(
        new JScrollPane(table),
        BorderLayout.CENTER
    );

    JPanel bottom =
        new JPanel(
            new FlowLayout(
                FlowLayout.LEFT,
                7,
                7
            )
        );

    bottom.setBackground(PANEL);

    JButton edit =
        button(
            "Edit",
            "Edit selected inventory slot"
        );

    JButton select =
        button(
            "Select",
            "Select inventory slot"
        );

    JButton refresh =
        button(
            "Refresh",
            "Refresh inventory"
        );

    bottom.add(edit);
    bottom.add(select);
    bottom.add(refresh);

    p.add(
        bottom,
        BorderLayout.SOUTH
    );

    edit.addActionListener(
        e -> editSelectedInventorySlot(table)
    );

    select.addActionListener(
        e -> selectInventorySlot(table)
    );

    refresh.addActionListener(
        e -> refreshInventory()
    );

    table.getSelectionModel()
        .addListSelectionListener(
            e -> {

                if (!e.getValueIsAdjusting()) {

                    int row =
                        table.getSelectedRow();

                    if (row >= 0) {

                        logLine(
                            "Selected inventory row: " +
                            row
                        );
                    }
                }
            }
        );

    return p;
}

    private JPanel buildEntitiesPanel() {

        JPanel p =
            new JPanel(new BorderLayout());

        p.setBackground(BG);

        JPanel header =
            new JPanel(new BorderLayout(10, 0));

        header.setBackground(PANEL);
        header.setBorder(
            new EmptyBorder(10, 10, 10, 10)
        );

        JLabel title =
            new JLabel("Entities");

        title.setForeground(TEXT);
        title.setFont(
            new Font(
                "SansSerif",
                Font.BOLD,
                18
            )
        );

        searchField.setPreferredSize(
            new Dimension(240, 30)
        );

        searchField.setToolTipText(
            "Search entity type..."
        );

        header.add(
            title,
            BorderLayout.WEST
        );

        header.add(
            searchField,
            BorderLayout.EAST
        );

        p.add(
            header,
            BorderLayout.NORTH
        );

        String[] columns = {
            "#",
            "Type",
            "X",
            "Y",
            "Z",
            "Y Rot",
            "X Rot",
            "Health",
            "Removed"
        };

        DefaultTableModel model =
            new DefaultTableModel(
                columns,
                0
            ) {
                public boolean isCellEditable(
                        int r,
                        int c
                ) {
                    return false;
                }
            };

        entityTable.setModel(model);
        entityTable.setBackground(PANEL);
        entityTable.setForeground(TEXT);
        entityTable.setGridColor(BORDER);
        entityTable.setRowHeight(25);
        entityTable.setFont(MONO_FONT);

        JTableHeader th =
            entityTable.getTableHeader();

        th.setBackground(PANEL2);
        th.setForeground(TEXT);
        th.setFont(
            new Font(
                "SansSerif",
                Font.BOLD,
                12
            )
        );

        entityTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        entityTable.getSelectionModel()
            .addListSelectionListener(
                e -> {
                    if (!e.getValueIsAdjusting())
                        selectEntityFromTable();
                }
            );

        p.add(
            new JScrollPane(entityTable),
            BorderLayout.CENTER
        );

        JPanel bottom =
            new JPanel(new FlowLayout(
                FlowLayout.LEFT,
                7,
                7
            ));

        bottom.setBackground(PANEL);

        JButton edit =
            button("Edit", "Edit selected entity");

        JButton remove =
            button("Remove", "Remove selected entity");

        JButton refresh =
            button("Refresh", "Refresh entities");

        bottom.add(edit);
        bottom.add(remove);
        bottom.add(refresh);

        p.add(
            bottom,
            BorderLayout.SOUTH
        );

        edit.addActionListener(
            e -> editSelectedEntity()
        );

        remove.addActionListener(
            e -> removeSelectedEntity()
        );

        refresh.addActionListener(
            e -> refreshEntities()
        );

        searchField.getDocument()
            .addDocumentListener(
                new DocumentListener() {

                    public void insertUpdate(
                            DocumentEvent e) {
                        refreshEntities();
                    }

                    public void removeUpdate(
                            DocumentEvent e) {
                        refreshEntities();
                    }

                    public void changedUpdate(
                            DocumentEvent e) {
                        refreshEntities();
                    }
                }
            );

        return p;
    }

    private JPanel buildConsolePanel() {

        JPanel p =
            basePanel("Console");

        log.setEditable(false);
        log.setBackground(
            new Color(20, 21, 24)
        );
        log.setForeground(
            new Color(190, 220, 190)
        );
        log.setFont(MONO_FONT);

        p.add(
            new JScrollPane(log),
            BorderLayout.CENTER
        );

        return p;
    }

    private JPanel basePanel(
            String title
    ) {

        JPanel p =
            new JPanel(new BorderLayout());

        p.setBackground(BG);

        JLabel h =
            new JLabel("  " + title);

        h.setForeground(TEXT);
        h.setFont(
            new Font(
                "SansSerif",
                Font.BOLD,
                18
            )
        );

        h.setBorder(
            new CompoundBorder(
                new MatteBorder(
                    0, 0, 1, 0,
                    BORDER
                ),
                new EmptyBorder(
                    10, 5, 10, 5
                )
            )
        );

        p.add(
            h,
            BorderLayout.NORTH
        );

        return p;
    }

    private JPanel buildStatusBar() {

        JPanel p =
            new JPanel(new BorderLayout());

        p.setBackground(PANEL);
        p.setBorder(
            new MatteBorder(
                1, 0, 0, 0,
                BORDER
            )
        );

        status.setForeground(MUTED);

        worldLabel.setForeground(MUTED);

        p.add(
            status,
            BorderLayout.WEST
        );

        p.add(
            worldLabel,
            BorderLayout.EAST
        );

        return p;
    }

    private JButton button(
            String text,
            String tooltip
    ) {

        JButton b =
            new JButton(text);

        b.setToolTipText(tooltip);
        b.setFocusPainted(false);
        b.setFont(UI_FONT);

        return b;
    }

    private JButton navButton(
            String text
    ) {

        JButton b =
            new JButton(
                text,
                null
            );

        b.setHorizontalAlignment(
            SwingConstants.LEFT
        );

        b.setMaximumSize(
            new Dimension(
                Integer.MAX_VALUE,
                40
            )
        );

        b.setFocusPainted(false);
        b.setBorder(
            new EmptyBorder(
                8, 15, 8, 8
            )
        );

        b.setBackground(PANEL);
        b.setForeground(TEXT);

        return b;
    }

    private void addProperty(
            JPanel p,
            String name,
            String value
    ) {

        JLabel n =
            new JLabel(name);

        n.setForeground(MUTED);

        JLabel v =
            new JLabel(value);

        v.setForeground(TEXT);

        p.add(n);
        p.add(v);
    }

    private void openWorld() {

        JFileChooser chooser =
            new JFileChooser();

        chooser.setDialogTitle(
            "Select Minecraft Classic level.dat"
        );

        if (
            chooser.showOpenDialog(this)
            != JFileChooser.APPROVE_OPTION
        )
            return;

        File selected =
            chooser.getSelectedFile();

        JFileChooser jarChooser =
            new JFileChooser();

        jarChooser.setDialogTitle(
            "Select Minecraft JAR"
        );

        if (
            jarChooser.showOpenDialog(this)
            != JFileChooser.APPROVE_OPTION
        )
            return;

        File jar =
            jarChooser.getSelectedFile();

        try {

            levelFile = selected;
            jarFile = jar;

            logLine(
                "Loading world..."
            );

            level =
                invokeLevelLoad(
                    levelFile,
                    jarFile
                );

            ClassicEditor.level =
                level;

            ClassicEditor.levelFile =
                levelFile;

            ClassicEditor.jarFile =
                jarFile;

            worldLabel.setText(
                " " +
                levelFile.getName() +
                " | " +
                jarFile.getName()
            );

            status.setText(
                " Loaded successfully"
            );

            logLine(
                "[OK] World loaded."
            );

            showWorld();
            refreshEntities();

        } catch (Throwable e) {

            showError(
                "Could not load world",
                e
            );
        }
    }

    private Object invokeLevelLoad(
            File level,
            File jar
    ) throws Exception {

        Method[] methods =
            LevelIO.class.getDeclaredMethods();

        for (Method m : methods) {

            if (
                !m.getName().equals("load") ||
                m.getParameterTypes().length != 2
            )
                continue;

                     Class<?>[] p = m.getParameterTypes();

            try {
                m.setAccessible(true);

                // load(File, File)
                if (p.length == 2 &&
                    p[0] == File.class &&
                    p[1] == File.class) {

                    return m.invoke(
                        null,
                        level,
                        jar
                    );
                }

                // load(String, File)
                if (p.length == 2 &&
                    p[0] == String.class &&
                    p[1] == File.class) {

                    return m.invoke(
                        null,
                        level.getAbsolutePath(),
                        jar
                    );
                }

                // load(File, String)
                if (p.length == 2 &&
                    p[0] == File.class &&
                    p[1] == String.class) {

                    return m.invoke(
                        null,
                        level,
                        jar.getAbsolutePath()
                    );
                }

                // load(String, String)
                if (p.length == 2 &&
                    p[0] == String.class &&
                    p[1] == String.class) {

                    return m.invoke(
                        null,
                        level.getAbsolutePath(),
                        jar.getAbsolutePath()
                    );
                }
            } catch (InvocationTargetException e) {

                Throwable cause =
                    e.getCause();

                if (cause instanceof Exception)
                    throw (Exception) cause;

                throw e;
            }
        }

        throw new NoSuchMethodException(
            "LevelIO.load(...) não possui uma assinatura suportada"
        );
    }

    private void saveWorld() {

        if (level == null) {

            JOptionPane.showMessageDialog(
                this,
                "Nenhum mundo está carregado.",
                "ClassicEditor",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            logLine("Saving world...");

            invokeLevelSave();

            status.setText(
                " Saved successfully"
            );

            logLine(
                "[OK] World saved."
            );

        } catch (Throwable e) {

            showError(
                "Could not save world",
                e
            );
        }
    }

    private void invokeLevelSave()
            throws Exception {

        Method[] methods =
            LevelIO.class.getDeclaredMethods();

        for (Method m : methods) {

            if (!m.getName().equals("save"))
                continue;

            Class<?>[] p =
                m.getParameterTypes();

            m.setAccessible(true);

            try {

                // save(Object, File)
                if (p.length == 2 &&
                    p[0].isAssignableFrom(
                        level.getClass()
                    ) &&
                    p[1] == File.class) {

                    m.invoke(
                        null,
                        level,
                        levelFile
                    );

                    return;
                }

                // save(Object, String)
                if (p.length == 2 &&
                    p[0].isAssignableFrom(
                        level.getClass()
                    ) &&
                    p[1] == String.class) {

                    m.invoke(
                        null,
                        level,
                        levelFile.getAbsolutePath()
                    );

                    return;
                }

                // save(Object)
                if (p.length == 1 &&
                    p[0].isAssignableFrom(
                        level.getClass()
                    )) {

                    m.invoke(
                        null,
                        level
                    );

                    return;
                }

            } catch (InvocationTargetException e) {

                Throwable cause =
                    e.getCause();

                if (cause instanceof Exception)
                    throw (Exception) cause;

                throw e;
            }
        }

        throw new NoSuchMethodException(
            "LevelIO.save(...) não possui uma assinatura suportada"
        );
    }

    private void reloadWorld() {

        if (levelFile == null ||
            jarFile == null) {

            JOptionPane.showMessageDialog(
                this,
                "Nenhum mundo foi carregado.",
                "ClassicEditor",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            logLine("Reloading world...");

            level =
                invokeLevelLoad(
                    levelFile,
                    jarFile
                );

            ClassicEditor.level =
                level;

            status.setText(
                " Reloaded"
            );

            logLine(
                "[OK] World reloaded."
            );

            refreshEntities();

            showWorld();

        } catch (Throwable e) {

            showError(
                "Could not reload world",
                e
            );
        }
    }

    private void showWorld() {

    card.show(content, "world");

    if (level == null) {
        return;
    }

    try {

        JPanel panel = null;

        for (Component c : content.getComponents()) {

            if (c instanceof JPanel) {

                Object obj =
                    ((JPanel)c).getClientProperty(
                        "worldGrid"
                    );

                if (obj instanceof JPanel) {
                    panel = (JPanel)c;
                    break;
                }
            }
        }

        if (panel == null)
            return;

        JPanel grid =
            (JPanel)panel.getClientProperty(
                "worldGrid"
            );

        updateWorldProperties(grid);

        status.setText(" World information");

        logLine(
            "Showing world information."
        );

    } catch (Throwable e) {

        showError(
            "Could not read world information",
            e
        );
    }
}

private void updateWorldProperties(
        JPanel grid
) {

    grid.removeAll();

    addProperty(
        grid,
        "Name",
        String.valueOf(
            getField(level, "name")
        )
    );

    addProperty(
        grid,
        "Creator",
        String.valueOf(
            getField(level, "creator")
        )
    );

    addProperty(
        grid,
        "Width",
        String.valueOf(
            getField(level, "width")
        )
    );

    addProperty(
        grid,
        "Height",
        String.valueOf(
            getField(level, "height")
        )
    );

    addProperty(
        grid,
        "Depth",
        String.valueOf(
            getField(level, "depth")
        )
    );

    addProperty(
        grid,
        "Spawn X",
        String.valueOf(
            getField(level, "xSpawn")
        )
    );

    addProperty(
        grid,
        "Spawn Y",
        String.valueOf(
            getField(level, "ySpawn")
        )
    );

    addProperty(
        grid,
        "Spawn Z",
        String.valueOf(
            getField(level, "zSpawn")
        )
    );

    addProperty(
        grid,
        "Creative",
        String.valueOf(
            getField(level, "creativeMode")
        )
    );

    addProperty(
        grid,
        "Water Level",
        String.valueOf(
            getField(level, "waterLevel")
        )
    );

    grid.revalidate();
    grid.repaint();
}

    private void showPlayer() {

    card.show(content, "player");

    if (level == null)
        return;

    Object player =
        getField(level, "player");

    if (player == null)
        return;

    JPanel grid = null;

    for (Component c : content.getComponents()) {

        if (!(c instanceof JPanel))
            continue;

        Object obj =
            ((JPanel)c).getClientProperty(
                "playerGrid"
            );

        if (obj instanceof JPanel) {
            grid = (JPanel)obj;
            break;
        }
    }

    if (grid == null)
        return;

    grid.removeAll();

    addEditableProperty(
        grid,
        player,
        "x",
        "X"
    );

    addEditableProperty(
        grid,
        player,
        "y",
        "Y"
    );

    addEditableProperty(
        grid,
        player,
        "z",
        "Z"
    );

    addEditableProperty(
        grid,
        player,
        "yRot",
        "Y Rotation"
    );

    addEditableProperty(
        grid,
        player,
        "xRot",
        "X Rotation"
    );

    addEditableProperty(
        grid,
        player,
        "health",
        "Health"
    );

    addEditableProperty(
        grid,
        player,
        "arrows",
        "Arrows"
    );

    addEditableProperty(
        grid,
        player,
        "score",
        "Score"
    );

    addEditableProperty(
        grid,
        player,
        "removed",
        "Removed"
    );

    grid.revalidate();
    grid.repaint();

    status.setText(" Player");
}

    private String getPlayerInfo() {

        if (level == null)
            return "No world loaded.";

        try {

            Object player =
                getField(
                    level,
                    "player"
                );

            if (player == null)
                return "Player: none";

            StringBuilder s =
                new StringBuilder();

            s.append(
                "Class     : "
            ).append(
                player.getClass().getName()
            ).append("\n\n");

            appendField(
                s,
                player,
                "x"
            );

            appendField(
                s,
                player,
                "y"
            );

            appendField(
                s,
                player,
                "z"
            );

            s.append("\n");

            appendField(
                s,
                player,
                "yRot"
            );

            appendField(
                s,
                player,
                "xRot"
            );

            s.append("\n");

            appendField(
                s,
                player,
                "health"
            );

            appendField(
                s,
                player,
                "arrows"
            );

            appendField(
                s,
                player,
                "score"
            );

            appendField(
                s,
                player,
                "removed"
            );

            return s.toString();

        } catch (Throwable e) {

            return
                "Could not read player:\n" +
                e;
        }
    }

    private void showInventory() {

    card.show(
        content,
        "inventory"
    );

    refreshInventory();

    logLine(
        "Inventory editor selected."
    );
}

    private void refreshInventory() {

    if (level == null)
        return;

    try {

        Object player =
            getField(level, "player");

        if (player == null)
            return;

        Object inv =
            getField(player, "inventory");

        if (inv == null)
            return;

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
                ((Number)
                    getField(
                        inv,
                        "selected"
                    )
                ).intValue();

        } catch (Throwable ignored) {
        }

        JTable table =
            getInventoryTable();

        if (table == null)
            return;

        DefaultTableModel model =
            (DefaultTableModel)
                table.getModel();

        model.setRowCount(0);

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

            model.addRow(
                new Object[] {
                    i,
                    slots[i],
                    count[i],
                    pop
                }
            );
        }

        JLabel selectedLabel =
            getInventorySelectedLabel();

        if (selectedLabel != null) {

            selectedLabel.setText(
                " Selected slot: " +
                selected
            );
        }

        if (
            selected >= 0 &&
            selected < table.getRowCount()
        ) {

            table.setRowSelectionInterval(
                selected,
                selected
            );
        }

        status.setText(
            " Inventory: " +
            slots.length +
            " slots"
        );

        table.revalidate();
        table.repaint();

    } catch (Throwable e) {

        showError(
            "Could not read inventory",
            e
        );
    }
}

private JTable getInventoryTable() {

    for (
        Component c :
        content.getComponents()
    ) {

        if (!(c instanceof JPanel))
            continue;

        Object table =
            ((JPanel)c).getClientProperty(
                "inventoryTable"
            );

        if (table instanceof JTable)
            return (JTable)table;
    }

    return null;
}

private JLabel getInventorySelectedLabel() {

    for (
        Component c :
        content.getComponents()
    ) {

        if (!(c instanceof JPanel))
            continue;

        Object label =
            ((JPanel)c).getClientProperty(
                "inventorySelected"
            );

        if (label instanceof JLabel)
            return (JLabel)label;
    }

    return null;
}

private Object getInventoryObject()
        throws Exception {

    if (level == null)
        return null;

    Object player =
        getField(
            level,
            "player"
        );

    if (player == null)
        return null;

    return getField(
        player,
        "inventory"
    );
}

private void editSelectedInventorySlot(
        JTable table
) {

    int row =
        table.getSelectedRow();

    if (row < 0) {

        JOptionPane.showMessageDialog(
            this,
            "Selecione um slot primeiro.",
            "ClassicEditor",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    try {

        Object inv =
            getInventoryObject();

        if (inv == null)
            throw new Exception(
                "Inventory not found."
            );

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

        if (
            row < 0 ||
            row >= slots.length
        )
            throw new Exception(
                "Invalid inventory slot."
            );

        JTextField itemField =
            new JTextField(
                String.valueOf(slots[row])
            );

        JTextField countField =
            new JTextField(
                String.valueOf(count[row])
            );

        JPanel panel =
            new JPanel(
                new GridLayout(
                    0,
                    2,
                    8,
                    8
                )
            );

        panel.add(
            new JLabel("Item ID:")
        );

        panel.add(itemField);

        panel.add(
            new JLabel("Count:")
        );

        panel.add(countField);

        int result =
            JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit Slot " + row,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

        if (
            result !=
            JOptionPane.OK_OPTION
        )
            return;

        int item =
            Integer.parseInt(
                itemField.getText().trim()
            );

        int amount =
            Integer.parseInt(
                countField.getText().trim()
            );

        slots[row] = item;
        count[row] = amount;

        logLine(
            "[OK] Inventory slot " +
            row +
            " updated: item=" +
            item +
            " count=" +
            amount
        );

        refreshInventory();

        status.setText(
            " Inventory slot " +
            row +
            " changed"
        );

    } catch (Throwable e) {

        showError(
            "Could not edit inventory slot",
            e
        );
    }
}

private void selectInventorySlot(
        JTable table
) {

    int row =
        table.getSelectedRow();

    if (row < 0) {

        JOptionPane.showMessageDialog(
            this,
            "Selecione um slot primeiro.",
            "ClassicEditor",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    try {

        Object inv =
            getInventoryObject();

        if (inv == null)
            throw new Exception(
                "Inventory not found."
            );

        setField(
    inv,
    "selected",
    String.valueOf(row)
);

        JLabel selectedLabel =
            getInventorySelectedLabel();

        if (selectedLabel != null) {

            selectedLabel.setText(
                " Selected slot: " +
                row
            );
        }

        logLine(
            "[OK] Selected inventory slot: " +
            row
        );

        status.setText(
            " Selected slot " +
            row
        );

    } catch (Throwable e) {

        showError(
            "Could not select inventory slot",
            e
        );
    }
}

    private void showEntities() {

        card.show(
            content,
            "entities"
        );

        refreshEntities();
    }

    private void showConsole() {

        card.show(
            content,
            "console"
        );
    }

    private void refreshEntities() {

        if (level == null)
            return;

        DefaultTableModel model =
            (DefaultTableModel)
                entityTable.getModel();

        model.setRowCount(0);

        try {

            Object blockMap =
                getField(
                    level,
                    "blockMap"
                );

            if (blockMap == null)
                return;

            Object all =
                getField(
                    blockMap,
                    "all"
                );

            if (!(all instanceof List))
                return;

            List<?> entities =
                (List<?>)all;

            String filter =
                searchField
                    .getText()
                    .trim()
                    .toLowerCase(
                        Locale.ROOT
                    );

            int index = 0;

            for (Object entity : entities) {

                if (entity == null)
                    continue;

                String type =
                    entity.getClass()
                        .getSimpleName();

                if (!filter.isEmpty() &&
                    !type.toLowerCase(
                        Locale.ROOT
                    ).contains(filter))
                    continue;

                Object x =
                    getField(
                        entity,
                        "x"
                    );

                Object y =
                    getField(
                        entity,
                        "y"
                    );

                Object z =
                    getField(
                        entity,
                        "z"
                    );

                Object yRot =
                    getField(
                        entity,
                        "yRot"
                    );

                Object xRot =
                    getField(
                        entity,
                        "xRot"
                    );

                Object health =
                    getField(
                        entity,
                        "health"
                    );

                Object removed =
                    getField(
                        entity,
                        "removed"
                    );

                model.addRow(
                    new Object[] {
                        index,
                        type,
                        x,
                        y,
                        z,
                        yRot,
                        xRot,
                        health == null
                            ? "-"
                            : health,
                        removed
                    }
                );

                index++;
            }

            status.setText(
                " " +
                entities.size() +
                " entities"
            );

        } catch (Throwable e) {

            logLine(
                "[ERROR] " +
                e
            );
        }
    }

    private void selectEntityFromTable() {

    Object entity = getSelectedEntity();

    if (entity == null)
        return;

    selectedEntity = entity;

    logLine(
        "Selected entity: " +
        entity.getClass().getName()
    );

    showEntityProperties(entity);
}

private void addEditableProperty(
        JPanel panel,
        Object object,
        String fieldName,
        String displayName
) {

    JLabel label =
        new JLabel(displayName);

    label.setForeground(MUTED);

    JTextField field =
        new JTextField();

    Object value =
        getField(object, fieldName);

    field.setText(
        value == null
            ? ""
            : String.valueOf(value)
    );

    field.setFont(MONO_FONT);

    JButton apply =
        button(
            "Apply",
            "Apply " + displayName
        );

    apply.addActionListener(
        e -> {

            try {

                setField(
                    object,
                    fieldName,
                    field.getText()
                );

                status.setText(
                    " Changed " +
                    displayName
                );

                logLine(
                    "[OK] Changed player." +
                    fieldName
                );

            } catch (Throwable ex) {

                showError(
                    "Could not change " +
                    displayName,
                    ex
                );
            }
        }
    );

    JPanel valuePanel =
        new JPanel(
            new BorderLayout(5, 0)
        );

    valuePanel.setBackground(BG);

    valuePanel.add(
        field,
        BorderLayout.CENTER
    );

    valuePanel.add(
        apply,
        BorderLayout.EAST
    );

    panel.add(label);
    panel.add(valuePanel);
}

private void showEntityProperties(Object entity) {

    if (entity == null)
        return;

    JDialog dialog = new JDialog(
        this,
        "Entity Properties - " +
        entity.getClass().getSimpleName(),
        true
    );

    dialog.setSize(600, 650);
    dialog.setLocationRelativeTo(this);

    JPanel root = new JPanel(new BorderLayout(10, 10));
    root.setBorder(new EmptyBorder(12, 12, 12, 12));

    JPanel fields = new JPanel(
        new GridLayout(0, 2, 8, 8)
    );

    String[] names = {
        "x",
        "y",
        "z",
        "yRot",
        "xRot",
        "health",
        "removed",
        "fallDistance"
    };

    Map<String, JTextField> editors =
        new LinkedHashMap<String, JTextField>();

    for (String name : names) {

        Field field =
            findField(
                entity.getClass(),
                name
            );

        if (field == null)
            continue;

        JTextField editor =
            fieldFor(entity, name);

        editors.put(name, editor);

        addEditorField(
            fields,
            name,
            editor
        );
    }

    JLabel classLabel =
        new JLabel(
            "Class: " +
            entity.getClass().getName()
        );

    classLabel.setBorder(
        new EmptyBorder(0, 0, 10, 0)
    );

    root.add(
        classLabel,
        BorderLayout.NORTH
    );

    root.add(
        new JScrollPane(fields),
        BorderLayout.CENTER
    );

    JPanel buttons =
        new JPanel(
            new FlowLayout(
                FlowLayout.RIGHT
            )
        );

    JButton cancel =
        button(
            "Cancel",
            "Cancel changes"
        );

    JButton apply =
        button(
            "Apply",
            "Apply changes"
        );

    buttons.add(cancel);
    buttons.add(apply);

    root.add(
        buttons,
        BorderLayout.SOUTH
    );

    cancel.addActionListener(
        e -> dialog.dispose()
    );

    apply.addActionListener(
        e -> {

            try {

                for (
                    Map.Entry<String, JTextField> entry :
                    editors.entrySet()
                ) {

                    setField(
                        entity,
                        entry.getKey(),
                        entry.getValue().getText()
                    );
                }

                selectedEntity = entity;

                logLine(
                    "[OK] Entity updated: " +
                    entity.getClass().getSimpleName()
                );

                refreshEntities();

                int row =
                    entityTable.getSelectedRow();

                if (row >= 0) {
                    entityTable.repaint();
                }

                dialog.dispose();

            } catch (Throwable ex) {

                showError(
                    "Could not update entity",
                    ex
                );
            }
        }
    );

    dialog.setContentPane(root);
    dialog.setVisible(true);
}

    private Object getSelectedEntity() {

        int row =
            entityTable.getSelectedRow();

        if (row < 0 || level == null)
            return null;

        try {

            Object blockMap =
                getField(level, "blockMap");

            if (blockMap == null)
                return null;

            Object all =
                getField(blockMap, "all");

            if (!(all instanceof List))
                return null;

            List<?> entities =
                (List<?>) all;

            String filter =
                searchField
                    .getText()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            int visibleIndex = 0;

            for (Object entity : entities) {

                if (entity == null)
                    continue;

                String type =
                    entity.getClass()
                        .getSimpleName();

                if (!filter.isEmpty() &&
                    !type.toLowerCase(
                        Locale.ROOT
                    ).contains(filter)) {
                    continue;
                }

                if (visibleIndex == row)
                    return entity;

                visibleIndex++;
            }

        } catch (Throwable e) {
            logLine("[ERROR] " + e);
        }

        return null;
    }

    private void editSelectedEntity() {

        Object entity =
            getSelectedEntity();

        if (entity == null) {

            JOptionPane.showMessageDialog(
                this,
                "Selecione uma entidade primeiro.",
                "ClassicEditor",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        showEntityEditor(entity);
    }

    private void showEntityEditor(
            Object entity
    ) {

        JDialog dialog =
            new JDialog(
                this,
                "Edit Entity - " +
                entity.getClass().getSimpleName(),
                true
            );

        dialog.setSize(500, 520);
        dialog.setLocationRelativeTo(this);

        JPanel root =
            new JPanel(new BorderLayout(10, 10));

        root.setBorder(
            new EmptyBorder(12, 12, 12, 12)
        );

        JPanel fields =
            new JPanel(
                new GridLayout(0, 2, 8, 8)
            );

        JTextField x =
            fieldFor(entity, "x");

        JTextField y =
            fieldFor(entity, "y");

        JTextField z =
            fieldFor(entity, "z");

        JTextField yRot =
            fieldFor(entity, "yRot");

        JTextField xRot =
            fieldFor(entity, "xRot");

        JTextField health =
            fieldFor(entity, "health");

        JTextField removed =
            fieldFor(entity, "removed");

        addEditorField(fields, "X", x);
        addEditorField(fields, "Y", y);
        addEditorField(fields, "Z", z);
        addEditorField(fields, "Y Rotation", yRot);
        addEditorField(fields, "X Rotation", xRot);
        addEditorField(fields, "Health", health);
        addEditorField(fields, "Removed", removed);

        root.add(
            new JScrollPane(fields),
            BorderLayout.CENTER
        );

        JPanel buttons =
            new JPanel(
                new FlowLayout(
                    FlowLayout.RIGHT
                )
            );

        JButton cancel =
            button("Cancel", "Cancel changes");

        JButton apply =
            button("Apply", "Apply entity changes");

        buttons.add(cancel);
        buttons.add(apply);

        root.add(
            buttons,
            BorderLayout.SOUTH
        );

        cancel.addActionListener(
            e -> dialog.dispose()
        );

        apply.addActionListener(
            e -> {

                try {

                    setField(
                        entity,
                        "x",
                        x.getText()
                    );

                    setField(
                        entity,
                        "y",
                        y.getText()
                    );

                    setField(
                        entity,
                        "z",
                        z.getText()
                    );

                    setField(
                        entity,
                        "yRot",
                        yRot.getText()
                    );

                    setField(
                        entity,
                        "xRot",
                        xRot.getText()
                    );

                    setField(
                        entity,
                        "health",
                        health.getText()
                    );

                    setField(
                        entity,
                        "removed",
                        removed.getText()
                    );

                    logLine(
                        "[OK] Entity edited: " +
                        entity.getClass()
                            .getSimpleName()
                    );

                    refreshEntities();

                    dialog.dispose();

                } catch (Throwable ex) {

                    showError(
                        "Could not edit entity",
                        ex
                    );
                }
            }
        );

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private JTextField fieldFor(
            Object object,
            String name
    ) {

        Object value =
            getField(object, name);

        JTextField field =
            new JTextField(
                value == null
                    ? ""
                    : String.valueOf(value)
            );

        field.setFont(MONO_FONT);

        return field;
    }

    private void addEditorField(
            JPanel panel,
            String name,
            JTextField field
    ) {

        JLabel label =
            new JLabel(name);

        label.setFont(UI_FONT);

        panel.add(label);
        panel.add(field);
    }

    private void removeSelectedEntity() {

        Object entity =
            getSelectedEntity();

        if (entity == null) {

            JOptionPane.showMessageDialog(
                this,
                "Selecione uma entidade primeiro.",
                "ClassicEditor",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int result =
            JOptionPane.showConfirmDialog(
                this,
                "Remover " +
                entity.getClass()
                    .getSimpleName() +
                "?",
                "Confirmar remoção",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

        if (
            result !=
            JOptionPane.YES_OPTION
        )
            return;

        try {

            Object blockMap =
                getField(
                    level,
                    "blockMap"
                );

            if (blockMap != null) {

                Method remove =
                    blockMap.getClass()
                        .getMethod(
                            "remove",
                            Class.forName(
                                "com.mojang.minecraft.Entity"
                            )
                        );

                remove.invoke(
                    blockMap,
                    entity
                );
            }

            try {
                setField(
                    entity,
                    "removed",
                    "true"
                );
            } catch (Throwable ignored) {
            }

            logLine(
                "[OK] Removed entity: " +
                entity.getClass()
                    .getSimpleName()
            );

            refreshEntities();

        } catch (Throwable e) {

            showError(
                "Could not remove entity",
                e
            );
        }
    }

    private void setField(
            Object object,
            String name,
            String value
    ) throws Exception {

        Field field =
            findField(
                object.getClass(),
                name
            );

        if (field == null)
            return;

        field.setAccessible(true);

        Class<?> type =
            field.getType();

        Object converted;

        if (type == float.class ||
            type == Float.class) {

            converted =
                Float.parseFloat(value);

        } else if (
            type == double.class ||
            type == Double.class
        ) {

            converted =
                Double.parseDouble(value);

        } else if (
            type == int.class ||
            type == Integer.class
        ) {

            converted =
                Integer.parseInt(value);

        } else if (
            type == long.class ||
            type == Long.class
        ) {

            converted =
                Long.parseLong(value);

        } else if (
            type == boolean.class ||
            type == Boolean.class
        ) {

            converted =
                Boolean.parseBoolean(value);

        } else if (
            type == short.class ||
            type == Short.class
        ) {

            converted =
                Short.parseShort(value);

        } else if (
            type == byte.class ||
            type == Byte.class
        ) {

            converted =
                Byte.parseByte(value);

        } else {

            converted = value;
        }

        field.set(object, converted);
    }

    private Object getField(
            Object object,
            String name
    ) {

        if (object == null)
            return null;

        try {

            Field field =
                findField(
                    object.getClass(),
                    name
                );

            if (field == null)
                return null;

            field.setAccessible(true);

            return field.get(object);

        } catch (Throwable e) {

            return null;
        }
    }

    private void appendField(
            StringBuilder s,
            Object object,
            String name
    ) {

        Object value =
            getField(
                object,
                name
            );

        s.append(
            String.format(
                Locale.ROOT,
                "%-14s: %s%n",
                name,
                value == null
                    ? "-"
                    : value
            )
        );
    }

    private Field findField(
            Class<?> clazz,
            String name
    ) {

        Class<?> current =
            clazz;

        while (
            current != null
        ) {

            try {

                return current.getDeclaredField(
                    name
                );

            } catch (
                NoSuchFieldException ignored
            ) {
            }

            current =
                current.getSuperclass();
        }

        return null;
    }

    private void logLine(
            String text
    ) {

        SwingUtilities.invokeLater(
            () -> {

                log.append(
                    text + "\n"
                );

                log.setCaretPosition(
                    log.getDocument()
                        .getLength()
                );
            }
        );
    }

    private void showError(
            String message,
            Throwable error
    ) {

        Throwable cause =
            error;

        if (
            error instanceof
            InvocationTargetException
        ) {

            Throwable c =
                ((InvocationTargetException)
                    error).getCause();

            if (c != null)
                cause = c;
        }

        logLine(
            "[ERROR] " +
            message +
            ": " +
            cause
        );

        JOptionPane.showMessageDialog(
            this,
            message +
            "\n\n" +
            cause,
            "ClassicEditor Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void showAbout() {

        JOptionPane.showMessageDialog(
            this,
            "ClassicEditor 0.0.1.1 Beta\n\n" +
            "Minecraft Classic Save Editor\n" +
            "GUI inspired by NBT-style editors.",
            "About ClassicEditor",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
            () -> {

                ClassicEditorGUI gui =
                    new ClassicEditorGUI();

                gui.setVisible(true);
            }
        );
    }
}
