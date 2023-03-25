package service.util.env;

/**
 * Various colors for beautiful output to the console
 */
public enum Theme {
    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m");

    private final String color;

    Theme(String envUrl) {
        this.color = envUrl;
    }

    public String getColor() {
        return color;
    }

    public static String paint(String line, Theme color) {
        switch (color) {
            case BLACK -> {
                return BLACK.getColor() + line + RESET.getColor();
            }
            case RED -> {
                return RED.getColor() + line + RESET.getColor();
            }
            case GREEN -> {
                return GREEN.getColor() + line + RESET.getColor();
            }
            case YELLOW -> {
                return YELLOW.getColor() + line + RESET.getColor();
            }
            case BLUE -> {
                return BLUE.getColor() + line + RESET.getColor();
            }
            case PURPLE -> {
                return PURPLE.getColor() + line + RESET.getColor();
            }
            case CYAN -> {
                return CYAN.getColor() + line + RESET.getColor();
            }
            default -> {
                return line;
            }
        }
    }


    public static void print(String line, Theme color) {
        String out;
        switch (color) {
            case BLACK -> {
                out = BLACK.getColor() + line + RESET.getColor();
            }
            case RED -> {
                out = RED.getColor() + line + RESET.getColor();
            }
            case GREEN -> {
                out = GREEN.getColor() + line + RESET.getColor();
            }
            case YELLOW -> {
                out = YELLOW.getColor() + line + RESET.getColor();
            }
            case BLUE -> {
                out = BLUE.getColor() + line + RESET.getColor();
            }
            case PURPLE -> {
                out = PURPLE.getColor() + line + RESET.getColor();
            }
            case CYAN -> {
                out = CYAN.getColor() + line + RESET.getColor();
            }
            default -> {
                out = line;
            }
        }

        System.out.println(out);
    }
}
