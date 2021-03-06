/*
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package io.siddhi.distribution.store.api.rest.rest.impl;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.event.Event;
import io.siddhi.distribution.common.common.SiddhiAppRuntimeService;
import io.siddhi.distribution.store.api.rest.rest.ApiResponseMessage;
import io.siddhi.distribution.store.api.rest.rest.NotFoundException;
import io.siddhi.distribution.store.api.rest.rest.SiddhiStoreDataHolder;
import io.siddhi.distribution.store.api.rest.rest.StoresApiService;
import io.siddhi.distribution.store.api.rest.rest.model.ModelApiResponse;
import io.siddhi.distribution.store.api.rest.rest.model.Query;
import io.siddhi.distribution.store.api.rest.rest.model.Record;
import io.siddhi.distribution.store.api.rest.rest.model.RecordDetail;
import io.siddhi.query.api.definition.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;

/**
 * Store API service implementation.
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaMSF4JServerCodegen",
        date = "2017-11-01T11:26:25.925Z")
public class StoresApiServiceImpl extends StoresApiService {

    private static final Logger log = LoggerFactory.getLogger(StoresApiServiceImpl.class);

    private static String removeCRLFCharacters(String str) {
        if (str != null) {
            str = str.replace('\n', '_').replace('\r', '_');
        }
        return str;
    }

    @Override
    public Response query(Query body) throws NotFoundException {
        if (body.getQuery() == null || body.getQuery().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage
                    .ERROR, "Query cannot be empty or null")).build();
        }
        if (body.getAppName() == null || body.getAppName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage
                    .ERROR, "Siddhi app name cannot be empty or null")).build();
        }

        SiddhiAppRuntimeService siddhiAppRuntimeService =
                SiddhiStoreDataHolder.getInstance().getSiddhiAppRuntimeService();
        Map<String, SiddhiAppRuntime> siddhiAppRuntimes = siddhiAppRuntimeService.getActiveSiddhiAppRuntimes();
        SiddhiAppRuntime siddhiAppRuntime = siddhiAppRuntimes.get(body.getAppName());
        if (siddhiAppRuntime == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage
                    .ERROR, "Cannot find an active SiddhiApp with name: " + body.getAppName())).build();
        } else {
            try {
                Event[] events = siddhiAppRuntime.query(body.getQuery());
                List<Record> records = getRecords(events);
                ModelApiResponse response = new ModelApiResponse();
                response.setRecords(records);
                if (body.isDetails()) {
                    Attribute[] attributes = siddhiAppRuntime.getStoreQueryOutputAttributes(body.getQuery());
                    response.setDetails(getRecordDetails(attributes));
                }
                return Response.ok().entity(response).build();
            } catch (Exception e) {
                log.error("Error while querying for siddhiApp: " + removeCRLFCharacters(body.getAppName()) +
                        ", with query: " + removeCRLFCharacters(body.getQuery()) + " Error: " +
                        removeCRLFCharacters(e.getMessage()), e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
                                "Cannot query: " + e.getMessage())).build();
            }
        }
    }

    /**
     * Get record details.
     *
     * @param attributes Set of attributes
     * @return List of record details
     */
    private List<RecordDetail> getRecordDetails(Attribute[] attributes) {
        List<RecordDetail> details = new ArrayList<>();
        for (Attribute attribute : attributes) {
            details.add(new RecordDetail()
                    .name(attribute.getName())
                    .dataType(attribute.getType().toString()));
        }
        return details;
    }

    private List<Record> getRecords(Event[] events) {
        List<Record> records = new ArrayList<>();
        if (events != null) {
            for (Event event : events) {
                Record record = new Record();
                record.addAll(Arrays.asList(event.getData()));
                records.add(record);
            }
        }
        return records;
    }
}
