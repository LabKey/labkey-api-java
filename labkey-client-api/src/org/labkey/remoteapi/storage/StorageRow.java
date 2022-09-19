package org.labkey.remoteapi.storage;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The storage properties object used for the storage {@link CreateCommand}, {@link UpdateCommand}, and {@link DeleteCommand}.
 * This object will define the type of the item along with the specific properties for that storage item.
 */
public class StorageRow
{
    private StorageItemTypes _type;
    private Map<String, Object> _props = new HashMap<>();

    public String getType()
    {
        return _type.getTitle();
    }

    /**
     * Storage items can be of the following types: Physical Location, Freezer, Shelf, Rack, Canister,
     * Storage Unit Type, or Terminal Storage Location. One of these values must be set as the "type" for
     * the {@link StorageRow} provided to the {@link CreateCommand}, {@link UpdateCommand}, or {@link DeleteCommand}
     * constructor.
     * @param type Type value for the given storage item/row
     */
    public void setType(StorageItemTypes type)
    {
        _type = type;
    }

    public Map<String, Object> getProps()
    {
        return _props;
    }

    /**
     * The additional properties for the storage item being created/updated can be provided as the "props" for the
     * {@link StorageRow}. The specific set of props will differ for each storage item type:
     * <ul>
     *     <li>Physical Location: name, description, locationId (rowId of the parent Physical Location)</li>
     *     <li>Freezer: name, description, locationId (rowId of the parent Physical Location), manufacturer, freezerModel, temperature, temperatureUnits, serialNumber, sensorName, lossRate, status</li>
     *     <li>Shelf/Rack/Canister: name, description, locationId (rowId of the parent freezer or Shelf/Rack/Canister)</li>
     *     <li>Storage Unit Type: name, description, unitType (one of the following: "Box", "Plate", "Bag", "Cane", "Tube Rack"), rows, cols (required if positionFormat is not "Num"), positionFormat (one of the following: "Num", "AlphaNum", "AlphaAlpha", "NumAlpha", "NumNum"), positionOrder (one of the following: "RowColumn", "ColumnRow")</li>
     *     <li>Terminal Storage Location: name, description, typeId (rowId of the Storage Unit Type), locationId (rowId of the parent freezer or Shelf/Rack/Canister)</li>
     * </ul>
     * @param props Map of properties for the given storage item/row
     */
    public void setProps(Map<String, Object> props)
    {
        _props = props;
    }

    /**
     * For the {@link UpdateCommand} and {@link DeleteCommand}, the "rowId" primary key value is required to be set.
     * This helper method will put the rowId value into the "props" for this {@link StorageRow}.
     * @param rowId Integer rowId primary key for the given storage item/row
     */
    public void setRowId(Integer rowId)
    {
        Map<String, Object> props = new HashMap<>(_props);
        props.put("rowId", rowId);
        setProps(props);
    }

    public JSONObject toJsonObject()
    {
        JSONObject result = new JSONObject();
        result.put("type", this.getType());
        result.put("props", new JSONObject(this.getProps()));
        return result;
    }
}
