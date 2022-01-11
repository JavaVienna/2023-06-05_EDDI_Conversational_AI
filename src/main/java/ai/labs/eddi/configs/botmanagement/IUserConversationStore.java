package ai.labs.eddi.configs.botmanagement;

import ai.labs.eddi.models.UserConversation;

import static ai.labs.eddi.datastore.IResourceStore.ResourceAlreadyExistsException;

public interface IUserConversationStore {
    UserConversation readUserConversation(String intent, String userId)
            throws IResourceStore.ResourceStoreException;

    void createUserConversation(UserConversation userConversation)
            throws ResourceAlreadyExistsException, IResourceStore.ResourceStoreException;

    void deleteUserConversation(String intent, String userId) throws IResourceStore.ResourceStoreException;
}
