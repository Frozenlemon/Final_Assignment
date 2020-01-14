package util;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * @author Phu Le
 */
public class Language {
    /** the current selected Locale. */
    private static final ObjectProperty<Locale> locale;
    private static int numSwitches;
    
    static {
        locale = new SimpleObjectProperty<>(getDefaultLocale());
        locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
        numSwitches = 0;
    }
   
    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(Locale.ENGLISH, Locale.forLanguageTag("vi-VN")));
    }
    
    public static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        return getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }
    
    public static Locale getLocale() {
        return locale.get();
    }
    public static void setLocale(Locale locale) {
        localeProperty().set(locale);
        Locale.setDefault(locale);
    }

    public static void setNumSwitches(int numSwitches) {
        Language.numSwitches = numSwitches;
    }
    
    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }
    /**
     * gets the string with the given key from the resource bundle for the current locale and uses it as first argument
     * to MessageFormat.format, passing in the optional args and returning the result.
     */
    public static String get(final String key, final Object... args) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", getLocale());
        return MessageFormat.format(bundle.getString(key), args);
    }
    /**
     * creates a String binding to a localized String for the given message bundle key
     */
    public static StringBinding createStringBinding(final String key, Object... args)  throws Exception {
        return Bindings.createStringBinding(() -> get(key, args), locale);
    }
    /**
     * creates a String Binding to a localized String that is computed by calling the given func
     * @return StringBinding
     */
    public static StringBinding createStringBinding(Callable<String> func) {
        return Bindings.createStringBinding(func, locale);
    }
    
    public static void setLanguageText(List<Node> listNode) {
        for (Node node : listNode) {
            try {
                if (node instanceof Label) {
                    Label label = (Label) node;
                    label.textProperty().bind(createStringBinding(label.getId(), numSwitches));
                    continue;
                }
                if (node instanceof Button) {
                    Button button = (Button) node;
                    button.textProperty().bind(createStringBinding(button.getId(), numSwitches));
                    continue;
                }

                if (node instanceof Parent) {
                    setLanguageText(((Parent) node).getChildrenUnmodifiable());
                }
            } catch (Exception ex) {
            }
        }
    }

}
