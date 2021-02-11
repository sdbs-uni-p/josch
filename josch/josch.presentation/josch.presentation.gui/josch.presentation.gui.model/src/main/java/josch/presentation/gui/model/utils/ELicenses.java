package josch.presentation.gui.model.utils;

/**
 * This enum wraps all used licenses w.r.t. Third Party Software used within the Josch project.
 *
 *
 * @author Kai Dauberschmidt
 */
public enum ELicenses {
    JSON("JSON", "https://www.json.org/license.html"),
    APACHE_2("Apache 2.0", "https://www.apache.org/licenses/LICENSE-2.0"),
    EPL_2("EPL 2.0", "https://www.eclipse.org/legal/epl-2.0/"),
    GPL_2("GPL 2.0", "https://www.gnu.org/licenses/old-licenses/gpl-2.0.html"),
    MIT("MIT", "https://opensource.org/licenses/mit-license.html"),
    HACKOLADE("Hackolade License Agreement", "https://hackolade.com/help/Licenseagreement.html");

    /** The licence name */
    private final String NAME;

    /** The url to the licence */
    private final String URL;

    ELicenses(String name, String url) {
        this.NAME  = name;
        this.URL = url;
    }

    /**
     * Gets the {@code NAME}
     *
     * @return The value of {@code NAME}
     */
    public String getName() {
        return NAME;
    }

    /**
     * Gets the {@code URL}
     *
     * @return The value of {@code URL}
     */
    public String getUrl() {
        return URL;
    }
}
