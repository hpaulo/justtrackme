/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.ContextFactory;
import org.traccar.model.Permission;

public class PermissionsManager {
	
	private static final Logger LOG = LoggerFactory.getLogger(PermissionsManager.class);
    
    private final Map<Long, Set<Long>> permissions = new HashMap<Long, Set<Long>>();
    
	//TODO: Think about injection
	private DataManager dataManager = ContextFactory.getContext().getDataManager();
    
    private Set<Long> getNotNull(long userId) {
        if (!permissions.containsKey(userId)) {
            permissions.put(userId, new HashSet<Long>());
        }
        return permissions.get(userId);
    }
    
    public PermissionsManager() {
        refresh();
    }
    
    public final void refresh() {
        permissions.clear();
        try {
            for (Permission permission : getDataManager().getPermissions()) {
                getNotNull(permission.getUserId()).add(permission.getDeviceId());
            }
        } catch (SQLException error) {
            LOG.error("Can't get permissions", error);
        }
    }
    
    public Collection<Long> allowedDevices(long userId) {
        return getNotNull(userId);
    }
    
    public void checkDevice(long userId, long deviceId) throws SecurityException {
        if (getNotNull(userId).contains(deviceId)) {
            throw new SecurityException();
        }
    }
    
    public void checkDevices(long userId, Collection<Long> devices) throws SecurityException {
        if (getNotNull(userId).containsAll(devices)) {
            throw new SecurityException();
        }
    }

	public DataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(DataManager dataManager) {
		this.dataManager = dataManager;
	}
}
