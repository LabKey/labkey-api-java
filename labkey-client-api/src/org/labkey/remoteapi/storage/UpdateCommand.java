package org.labkey.remoteapi.storage;

/**
 * Update an existing LabKey Freezer Manager storage item to change its properties or location within the freezer hierarchy.
 * <p>
 * Storage items can be of the following types: Physical Location, Freezer, Shelf, Rack, Canister,
 * Storage Unit Type, or Terminal Storage Location.
 * <p>
 * Examples:
 * <pre><code>
 *  // TODO ...
 * </code></pre>
 * */
public class UpdateCommand extends BaseStorageCommand
{
    /**
     * Constructs a new UpdateCommand for the given {@link StorageRow}.
     * @param storageRow The details about the storage row to be updated.
     */
    public UpdateCommand(StorageRow storageRow)
    {
        super("update", storageRow);
    }
}
