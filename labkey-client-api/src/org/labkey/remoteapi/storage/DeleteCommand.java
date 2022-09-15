package org.labkey.remoteapi.storage;

/**
 * Delete an existing LabKey Freezer Manager storage item. Note that deletion of freezers or locations within the
 * freezer hierarchy will cascade the delete down the hierarchy to remove child locations and terminal storage locations.
 * <p>
 * Storage items can be of the following types: Physical Location, Freezer, Shelf, Rack, Canister,
 * Storage Unit Type, or Terminal Storage Location.
 * <p>
 * Examples:
 * <pre><code>
 *  // TODO ...
 * </code></pre>
 * */
public class DeleteCommand extends BaseStorageCommand
{
    /**
     * Constructs a new DeleteCommand for the given {@link StorageRow}.
     * @param storageRow The details about the storage row to be updated.
     */
    public DeleteCommand(StorageRow storageRow)
    {
        super("delete", storageRow);
    }
}
