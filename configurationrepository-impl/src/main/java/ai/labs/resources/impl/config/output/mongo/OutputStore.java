package ai.labs.resources.impl.config.output.mongo;

import ai.labs.persistence.ResultManipulator;
import ai.labs.persistence.mongo.HistorizedResourceStore;
import ai.labs.persistence.mongo.MongoResourceStorage;
import ai.labs.resources.rest.config.output.IOutputStore;
import ai.labs.resources.rest.config.output.model.OutputConfiguration;
import ai.labs.resources.rest.config.output.model.OutputConfigurationSet;
import ai.labs.serialization.IDocumentBuilder;
import com.mongodb.client.MongoDatabase;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ai.labs.utilities.RuntimeUtilities.checkCollectionNoNullElements;
import static ai.labs.utilities.RuntimeUtilities.checkNotNull;
import static ai.labs.utilities.RuntimeUtilities.isNullOrEmpty;

/**
 * @author ginccc
 */
public class OutputStore implements IOutputStore {
    private HistorizedResourceStore<OutputConfigurationSet> outputResourceStore;
    private static final OutputComparator OUTPUT_COMPARATOR = new OutputComparator();

    @Inject
    public OutputStore(MongoDatabase database, IDocumentBuilder documentBuilder) {
        checkNotNull(database, "database");
        final String collectionName = "outputs";
        MongoResourceStorage<OutputConfigurationSet> resourceStorage =
                new MongoResourceStorage<>(database, collectionName, documentBuilder, OutputConfigurationSet.class);


        this.outputResourceStore = new HistorizedResourceStore<>(resourceStorage);
    }

    @Override
    public OutputConfigurationSet readIncludingDeleted(String id, Integer version) throws ResourceNotFoundException, ResourceStoreException {
        return outputResourceStore.readIncludingDeleted(id, version);
    }

    @Override
    public IResourceId create(OutputConfigurationSet outputConfigurationSet) throws ResourceStoreException {
        checkCollectionNoNullElements(outputConfigurationSet.getOutputSet(), "outputSets");
        return outputResourceStore.create(outputConfigurationSet);
    }

    @Override
    public OutputConfigurationSet read(String id, Integer version) throws ResourceNotFoundException, ResourceStoreException {
        return outputResourceStore.read(id, version);
    }

    @Override
    public OutputConfigurationSet read(String id, Integer version, String filter, String order, Integer index, Integer limit)
            throws ResourceNotFoundException, ResourceStoreException {

        OutputConfigurationSet outputConfigurationSet = outputResourceStore.read(id, version);

        ResultManipulator<OutputConfiguration> outputManipulator;
        outputManipulator = new ResultManipulator<>(outputConfigurationSet.getOutputSet(), OutputConfiguration.class);

        try {
            if (!isNullOrEmpty(filter)) {
                outputManipulator.filterEntities(filter);
            }
        } catch (ResultManipulator.FilterEntriesException e) {
            throw new ResourceStoreException(e.getLocalizedMessage(), e);
        }

        if (!isNullOrEmpty(order)) {
            outputManipulator.sortEntities(OUTPUT_COMPARATOR, order);
        }
        if (!isNullOrEmpty(index) && !isNullOrEmpty(limit)) {
            outputManipulator.limitEntities(index, limit);
        }

        return outputConfigurationSet;
    }

    @Override
    public List<String> readActions(String id, Integer version, String filter, Integer limit)
            throws ResourceStoreException, ResourceNotFoundException {

        List<String> actions = read(id, version).
                getOutputSet().stream().
                map(OutputConfiguration::getAction).
                collect(Collectors.toList());

        return limit > 0 ? actions.subList(0, limit) : actions;
    }

    @Override
    @ConfigurationUpdate
    public Integer update(String id, Integer version, OutputConfigurationSet outputConfigurationSet)
            throws ResourceStoreException, ResourceModifiedException, ResourceNotFoundException {

        checkCollectionNoNullElements(outputConfigurationSet.getOutputSet(), "outputSets");
        return outputResourceStore.update(id, version, outputConfigurationSet);
    }

    @Override
    @ConfigurationUpdate
    public void delete(String id, Integer version) throws ResourceModifiedException, ResourceNotFoundException {
        outputResourceStore.delete(id, version);
    }

    @Override
    public void deleteAllPermanently(String id) {
        outputResourceStore.deleteAllPermanently(id);
    }

    @Override
    public IResourceId getCurrentResourceId(String id) throws ResourceNotFoundException {
        return outputResourceStore.getCurrentResourceId(id);
    }

    private static class OutputComparator implements Comparator<OutputConfiguration> {
        @Override
        public int compare(OutputConfiguration o1, OutputConfiguration o2) {
            return o1.getAction().compareTo(o2.getAction());
        }
    }
}