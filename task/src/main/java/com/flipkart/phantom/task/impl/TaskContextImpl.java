/*
 * Copyright 2012-2015, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.phantom.task.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.phantom.task.spi.Decoder;
import com.flipkart.phantom.task.spi.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Default implementation of {@link TaskContext}
 *
 * @author devashishshankar
 * @version 1.0, 20th March, 2013
 */
public class TaskContextImpl implements TaskContext {

    /** Logger for this class */
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskContextImpl.class);

    /** The default command to get Config  */
    private static final String GET_CONFIG_COMMAND = "getConfig";

    /** Host name for this TaskContext */
    private static String hostName;

    /** ObjectMapper instance */
    private ObjectMapper objectMapper = new ObjectMapper();


    /** The TaskHandlerExecutorRepository instance for getting thrift handler executor instances */
    private TaskHandlerExecutorRepository executorRepository;

    static
    {
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOGGER.error("Unable to find host name", e);
        }
    }

    /**
     * Gets the config from the ConfigTaskHandler (@link{GET_CONFIG_COMMAND}).
     * @param group group name of the object to be fetched
     * @param key the primary key
     * @return the config as string, empty string if not found/error
     */
    public String getConfig(String group, String key, int count) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("group", group);
        params.put("key", key);
        params.put("count", Integer.toString(count));
        TaskResult result = this.executeCommand(GET_CONFIG_COMMAND, null, params);
        if (result == null)
            return "";
        return new String((byte[])result.getData());
    }

    /**
     * Executes a command
     */
    public TaskResult executeCommand(String commandName, byte[] data, Map<String, String> params) throws UnsupportedOperationException {
        TaskRequestWrapper taskRequestWrapper = new TaskRequestWrapper();
        taskRequestWrapper.setData(data);
        taskRequestWrapper.setParams(params);
        return this.executorRepository.executeCommand(commandName, taskRequestWrapper);
    }

    @Override
    public <T> TaskResult<T> executeCommand(String commandName, TaskRequestWrapper taskRequestWrapper, Decoder<T> decoder) throws UnsupportedOperationException
    {
        return this.executorRepository.executeCommand(commandName, taskRequestWrapper,decoder);
    }

    @Override
    public <T> TaskResult<T> executeCommand(String commandName, byte[] data, Map<String, String> params, Decoder<T> decoder) throws UnsupportedOperationException
    {
        TaskRequestWrapper taskRequestWrapper = new TaskRequestWrapper();
        taskRequestWrapper.setData(data);
        taskRequestWrapper.setParams(params);
        return this.executorRepository.executeCommand(commandName, taskRequestWrapper,decoder);
    }

    @Override
    public <T> Future<TaskResult<T>> executeAsyncCommand(String commandName, byte[] data, Map<String, String> params, Decoder<T> decoder) throws UnsupportedOperationException
    {
        TaskRequestWrapper taskRequestWrapper = new TaskRequestWrapper();
        taskRequestWrapper.setData(data);
        taskRequestWrapper.setParams(params);
        return this.executorRepository.executeAsyncCommand(commandName, taskRequestWrapper,decoder);
    }

    /**
     * Executes a command asynchronously
     */
    public Future<TaskResult> executeAsyncCommand(String commandName, byte[] data, Map<String, String> params) throws UnsupportedOperationException {
        TaskRequestWrapper taskRequestWrapper = new TaskRequestWrapper();
        taskRequestWrapper.setData(data);
        taskRequestWrapper.setParams(params);
        return this.executorRepository.executeAsyncCommand(commandName, taskRequestWrapper);
    }

    /** Getter/Setter methods */
    public TaskHandlerExecutorRepository getExecutorRepository() {
        return this.executorRepository;
    }
    public void setExecutorRepository(TaskHandlerExecutorRepository executorRepository) {
        this.executorRepository = executorRepository;
    }
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @Override
    public String getHostName() {
        return hostName;
    }
    /** End Getter/Setter methods */
}
