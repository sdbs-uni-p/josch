package josch.model.enums;

/**
 * This class holds the themes. If you want to add a theme you have to add a respective
 * enumeration in addition to placing a logo and a css file in the respective folder. For
 * examples you can take a look at the already existing themes. Furthermore you have to add
 * a respective switch statement within the {@link EThemes#getTheme(String)}.
 *
 * @author Kai Dauberschmidt
 */
public enum EThemes {
    ORANGE("default", "sq_logo.png"),
    BLUE("blue", "sq_logo_hc.png"),
    DARK("dark", "sq_logo_dark.png"),
    BW("papercut", "sq_logo_bw.png");


    /** The path to the stylesheet of the theme */
    private final String STYLESHEET;

    /** The path to the logo of the theme */
    private final String LOGO;

    private final String NAME;


    /**
     * Constructs a theme enum with a given stylesheet and a given logo. The {@code .css} has to be
     * placed within the directory of {@code view/css/themes} whereas the logo has to be placed
     * within the directory of {@code view/images/}.
     * While technically all stylesheets need to be a .css file, the logo can differ hence you
     * have to add the file extension as well.
     *
     * @param stylesheet The name of the stylesheet, e.g. {@code default.css}.
     * @param logo The logo of the stylesheet, e.g. {@code sq_logo.png}.
     */
    EThemes(String stylesheet, String logo) {
        this.NAME = stylesheet;
        this.STYLESHEET = "view/css/themes/" + stylesheet + ".css";
        this.LOGO = "view/images/" + logo;
    }

    /** Gets the path to the stylesheet. */
    public String getStylesheet() {
        return STYLESHEET;
    }

    /** Gets the path to the logo. */
    public String getLogo() {
        return LOGO;
    }

    /** Gets the name of the theme. */
   public String getName(){
       return NAME;
   }

    /** Gets the theme by its name. */
    public static EThemes getTheme(String name) {
        return switch (name) {
            case "default", "orange" -> ORANGE;
            case "blue" -> BLUE;
            case "dark" -> DARK;
            case "papercut", "bw" -> BW;
            default -> throw new IllegalStateException("Unexpected value: " + name);
        };
    }
}
