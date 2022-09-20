package org.labkey.remoteapi.storage;

/**
 * Update an existing LabKey Freezer Manager storage item to change its properties or location within the freezer hierarchy.
 * <p>
 * Storage items can be of the following types: Physical Location, Freezer, Shelf, Rack, Canister,
 * Storage Unit Type, or Terminal Storage Location. One of these values must be set as the "type" for
 * the {@link StorageRow} provided to the UpdateCommand constructor.
 * <p>
 * The additional properties for the storage item being updated can be provided as the "props" for the
 * {@link StorageRow}. The specific set of props will differ for each storage item type:
 * <ul>
 *     <li>Physical Location: name, description, locationId (rowId of the parent Physical Location)</li>
 *     <li>Freezer: name, description, locationId (rowId of the parent Physical Location), manufacturer, freezerModel, temperature, temperatureUnits, serialNumber, sensorName, lossRate, status</li>
 *     <li>Shelf/Rack/Canister: name, description, locationId (rowId of the parent freezer or Shelf/Rack/Canister)</li>
 *     <li>Storage Unit Type: name, description, unitType (one of the following: "Box", "Plate", "Bag", "Cane", "Tube Rack"), rows, cols (required if positionFormat is not "Num"), positionFormat (one of the following: "Num", "AlphaNum", "AlphaAlpha", "NumAlpha", "NumNum"), positionOrder (one of the following: "RowColumn", "ColumnRow")</li>
 *     <li>Terminal Storage Location: name, description, typeId (rowId of the Storage Unit Type), locationId (rowId of the parent freezer or Shelf/Rack/Canister)</li>
 * </ul>
 * <p>
 * For the UpdateCommand, the "rowId" primary key value is required to be set within the {@link StorageRow}.
 * This can be set directly in the "props" map or via the {@link StorageRow} setRowId() method.
 * <p>
 * Examples:
 * <pre><code>
 *  // May need to add CONTEXT_PATH for dev instances
 *  Connection cn = new Connection("http://localhost:8080", user, password);
 *
 *  // Create Physical Location for freezer
 *  StorageRow row = new StorageRow();
 *  row.setType("Physical Location");
 *  row.setProps(Map.of("name", "Building #1", "description", "Test physical location"));
 *  CreateCommand cmd = new CreateCommand(row);
 *  StorageCommandResponse response = cmd.execute(cn, "PROJECT_NAME");
 *  Integer buildingRowId = response.getRowId();
 *
 *  // Create a freezer in Building #1
 *  row = new StorageRow();
 *  row.setType("Freezer");
 *  row.setProps(Map.of("name", "Freezer A", "description", "Test freezer from API", "serialNumber", "ABC123", "locationId", buildingRowId));
 *  cmd = new CreateCommand(row);
 *  response = cmd.execute(cn, "PROJECT_NAME");
 *  Integer freezerRowId = response.getRowId();
 *
 *  // Add two shelves to the freezer (note you can add additional non-terminal storage items to create
 *  // the freezer hierarchy that matches your physical freezer)
 *  row = new StorageRow();
 *  row.setType("Shelf");
 *  row.setProps(Map.of("name", "Shelf #1", "description", "Test shelf in Freezer A", "locationId", freezerRowId));
 *  cmd = new CreateCommand(row);
 *  response = cmd.execute(cn, "PROJECT_NAME");
 *  Integer shelf1RowId = response.getRowId();
 *
 *  row = new StorageRow();
 *  row.setType("Shelf");
 *  row.setProps(Map.of("name", "Shelf #2", "description", "Another test shelf in Freezer A", "locationId", freezerRowId));
 *  cmd = new CreateCommand(row);
 *  response = cmd.execute(cn, "PROJECT_NAME");
 *  Integer shelf2RowId = response.getRowId();
 *
 *  // Note that we are going to use an existing storage unit type that is part of the default set.
 *  // You can create custom storage unit types using the CreateCommand with type = "Storage Unit Type".
 *  SelectRowsCommand selectCommand = new SelectRowsCommand("inventory", "BoxType");
 *  selectCommand.setColumns(Arrays.asList("RowId"));
 *  selectCommand.addFilter(new Filter("Name", "96 Well Plate"));
 *  SelectRowsResponse selectResponse = selectCommand.execute(connection, containerPath);
 *  Integer plateTypeRowId = Integer.parseInt(selectResponse.getRows().get(0).get("RowId").toString());
 *
 *  // Add two plates to the shelf
 *  row = new StorageRow();
 *  row.setType("Terminal Storage Location");
 *  row.setProps(Map.of("name", "Plate #1", "description", "Test plate on Shelf #1", "typeId", plateTypeRowId, "locationId", shelf1RowId));
 *  cmd = new CreateCommand(row);
 *  response = cmd.execute(cn, "PROJECT_NAME");
 *  Integer plate1RowId = response.getRowId();
 *
 *  row = new StorageRow();
 *  row.setType("Terminal Storage Location");
 *  row.setProps(Map.of("name", "Plate #2", "description", "Another test plate on Shelf #1", "typeId", plateTypeRowId, "locationId", shelf1RowId));
 *  cmd = new CreateCommand(row);
 *  response = cmd.execute(cn, "PROJECT_NAME");
 *  Integer plate2RowId = response.getRowId();
 *
 *  // Move Plate #2 to Shelf #2
 *  row = new StorageRow();
 *  row.setType("Terminal Storage Location");
 *  row.setProps(Map.of("description", "Plate moved to Shelf #2", "locationId", shelf2RowId));
 *  row.setRowId(plate2RowId);
 *  UpdateCommand updateCmd = new UpdateCommand(row);
 *  updateCmd.execute(cn, "PROJECT_NAME");
 *
 *  // Delete the freezer, which will delete the full hierarchy of non-terminal and terminal storage locations
 *  row = new StorageRow();
 *  row.setType("Freezer");
 *  row.setRowId(freezerRowId);
 *  DeleteCommand deleteCmd = new DeleteCommand(row);
 *  deleteCmd.execute(cn, "PROJECT_NAME");
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
