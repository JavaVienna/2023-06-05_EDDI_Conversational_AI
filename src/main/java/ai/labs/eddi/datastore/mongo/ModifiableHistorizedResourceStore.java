package ai.labs.eddi.datastore.mongo;

import ai.labs.eddi.datastore.IResourceStorage;
import ai.labs.eddi.datastore.IResourceStore;
import ai.labs.eddi.utils.RuntimeUtilities;
import io.smallrye.mutiny.Uni;

import java.io.IOException;
import java.time.Duration;

/**
 * @author ginccc
 */
public class ModifiableHistorizedResourceStore<T> extends HistorizedResourceStore<T> implements IResourceStore<T> {
    public ModifiableHistorizedResourceStore(IResourceStorage<T> resourceStore) {
        super(resourceStore);
        this.resourceStorage = resourceStore;
    }

    public Uni<Integer> set(String id, Integer version, T content) throws IResourceStore.ResourceStoreException, IResourceStore.ResourceNotFoundException {
        RuntimeUtilities.checkNotNull(id, "id");
        RuntimeUtilities.checkNotNull(version, "version");
        RuntimeUtilities.checkNotNull(content, "content");

        var resource = resourceStorage.read(id, version);
        return Uni.createFrom().publisher(resource.onItem().invoke(res -> {
            var historyLatest = resourceStorage.readHistoryLatest(id);

            historyLatest.ifNoItem().after(Duration.ofMillis(1000)).failWith(createResourceNotFoundException(id, version)).await().indefinitely();

            //it's a update request for a historized resource, so we update the history resource
            IResourceStorage.IResource<T> updatedResource = resourceStorage.newResource(id, version, content);
            IResourceStorage.IHistoryResource<T> updatedHistorizedResource = resourceStorage.newHistoryResourceFor(updatedResource, false);
            resourceStorage.store(updatedHistorizedResource);
            return version;
        }).ifNoItem().after(Duration.ofMillis(1000))
                .recoverWithItem(resourceStorage.newResource(id, version, content)).onItem().transform(res -> {
                    IResourceStorage.IResource<T> updatedResource = resourceStorage.newResource(id, version, content);
                    resourceStorage.store(updatedResource);
                    return version;
                }));
    }

    public IResourceStore.IResourceId create(final String id, final Integer version, T content) throws IResourceStore.ResourceStoreException {
        RuntimeUtilities.checkNotNull(id, "id");
        RuntimeUtilities.checkNotNull(version, "version");
        RuntimeUtilities.checkNotNull(content, "content");

        try {
            IResourceStorage.IResource currentResource = resourceStorage.newResource(id, version, content);
            resourceStorage.store(currentResource);
            return currentResource;
        } catch (IOException e) {
            throw new IResourceStore.ResourceStoreException(e.getLocalizedMessage(), e);
        }
    }

    public IResourceStore.IResourceId createNew(final String id, final Integer version, T content) throws IResourceStore.ResourceStoreException {
        RuntimeUtilities.checkNotNull(id, "id");
        RuntimeUtilities.checkNotNull(version, "version");
        RuntimeUtilities.checkNotNull(content, "content");

        try {
            IResourceStorage.IResource currentResource = resourceStorage.newResource(id, version, content);
            resourceStorage.createNew(currentResource);
            return currentResource;
        } catch (IOException e) {
            throw new IResourceStore.ResourceStoreException(e.getLocalizedMessage(), e);
        }
    }


}
