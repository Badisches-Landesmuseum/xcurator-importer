package de.dreipc.xcurator.xcuratorimportservice.graphql.mutations;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import de.dreipc.xcurator.xcuratorimportservice.models.LanguageCode;
import de.dreipc.xcurator.xcuratorimportservice.namedentities.MissingEntitiesHandler;
import dreipc.graphql.types.Language;
import org.springframework.stereotype.Component;

@Component
@DgsComponent
public class EntitiesCommandResolver {


    private final MissingEntitiesHandler missingEntitiesHandler;

    public EntitiesCommandResolver(MissingEntitiesHandler missingEntitiesHandler) {
        this.missingEntitiesHandler = missingEntitiesHandler;
    }

    @DgsMutation
    public int analyseXcuratorEntities(@InputArgument Language language) {
        return missingEntitiesHandler.execute(LanguageCode.getLanguageCode(language));
    }


}
