package josch.services.factory;

import josch.model.dto.SettingsDto;
import josch.services.comparison.isjsonsubset.IjsComparisonService;
import josch.services.comparison.jsonsubschema.JssComparisonService;
import josch.services.extraction.hackolade.HackoladeExtractionService;
import josch.services.extraction.jsi.JsiExtractionService;
import josch.services.interfaces.*;
import josch.services.persistency.DatabaseService;
import josch.services.validation.ValidationService;

/**
 * the {@code AbstractServiceFactory} is a creational pattern to create the service objects from the {@code
 * josch.logic.services.interfaces} package. The implementation of these services
 * depends on the system's settings that are passed to the factory via the {@code SettingsDto}.
 *
 * @author Kai Dauberschmidt
 * @see SettingsDto
 * @see josch.services.interfaces
 */
public abstract class AbstractServiceFactory {


    /** Returns the implementation of the DatabaseService. */
    public static IDatabaseService getDatabaseService(SettingsDto settings) {
        return new DatabaseService(settings);
    }

    /** Returns the implementation of the ValidationService. */
    public static IValidationService getValidationService(SettingsDto settings) {
        return new ValidationService(settings);
    }

    /** Returns the implementation of the JsiExtractionService. */
    public static IExtractionService getExtractionService(SettingsDto settings) {
        return switch (settings.getExtraction().getTool()) {
            case HACK -> new HackoladeExtractionService(settings);
            case JSI ->  new JsiExtractionService(settings);
        };
    }

    /** Returns the implementation of the selected ComparisonService. */
    public static AbstractComparisonService getComparisonService(SettingsDto settings) {
        return switch (settings.getComparison()) {
            case JSS -> new JssComparisonService(settings);
            case IJS_SUBSET -> new IjsComparisonService(settings);
        };
    }
}
