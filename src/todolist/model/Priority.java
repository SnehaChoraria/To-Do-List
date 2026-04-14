package todolist.model;

//Enum -
public enum Priority {

    LOW("#90EE90", 1), // could change colours (randomly added)
    MEDIUM("#FFD700", 2),
    HIGH("#FF6B6B", 3);

    private final String hexColor;
    private final int level;

    //constructor
    Priority(String hexColor, int level) {
        this.hexColor = hexColor;
        this.level = level;
    }


    // to get the color and level
    public String getHexColor() {
        return hexColor;
    }

    public int getLevel() {
        return level;
    }

    public static Priority fromLevel(int level) {
        for (Priority p : Priority.values()) {
            if (p.level == level) {
                return p;
            }
        }
        return MEDIUM;
    }
}
