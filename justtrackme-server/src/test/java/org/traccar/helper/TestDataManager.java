package org.traccar.helper;

import org.traccar.database.DataManager;
import org.traccar.model.Device;

public class TestDataManager extends DataManager {

    @Override
    public Device getDeviceByUniqueId(String imei) {
        Device device = new Device();
        device.setId(new Long(1));
        device.setUniqueId("123456789012345");
        return device;
    }

}
