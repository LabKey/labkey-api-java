package org.labkey.remoteapi.storage;

/**
 * Create a new LabKey Freezer Manager storage item which can then be combined to create a freezer hierarchy.
 * <p>
 * Storage items can be of the following types: Physical Location, Freezer, Shelf, Rack, Canister,
 * Storage Unit Type, or Terminal Storage Location.
 * <p>
 * Examples:
 * <pre><code>
 *  // TODO ...
 * </code></pre>
 * */
public class CreateCommand extends BaseStorageCommand
{
    /**
     * Constructs a new CreateCommand for the given {@link StorageRow}.
     * @param storageRow The details about the storage row to be created.
     */
    public CreateCommand(StorageRow storageRow)
    {
        super("create", storageRow);
    }
}
