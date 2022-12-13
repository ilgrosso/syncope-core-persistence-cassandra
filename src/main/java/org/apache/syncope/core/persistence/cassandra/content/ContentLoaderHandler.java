/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.core.persistence.cassandra.content;

import com.datastax.oss.protocol.internal.ProtocolConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.syncope.core.provisioning.api.serialization.POJOHelper;
import org.apache.syncope.core.provisioning.api.utils.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler for generating SQL INSERT statements out of given XML file.
 */
public class ContentLoaderHandler extends DefaultHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ContentLoaderHandler.class);

    private final CassandraOperations operations;

    private final String rootElement;

    private final boolean continueOnError;

    private final Map<String, String> fetches = new HashMap<>();

    private final StringSubstitutor paramSubstitutor;

    public ContentLoaderHandler(
            final CassandraOperations operations,
            final String rootElement,
            final boolean continueOnError,
            final Environment env) {

        this.operations = operations;
        this.rootElement = rootElement;
        this.continueOnError = continueOnError;
        this.paramSubstitutor = new StringSubstitutor(key -> {
            String value = env.getProperty(key, fetches.get(key));
            return StringUtils.isBlank(value) ? null : value;
        });
    }

    private Object[] getParameters(final String tableName, final Attributes attrs) {
        Map<String, Integer> colTypes = operations.getCqlOperations().query(
                "SELECT * FROM \"" + tableName + "\" LIMIT 1", rs -> {
                    rs.getColumnDefinitions();

                    Map<String, Integer> types = new HashMap<>();
                    for (int i = 0; i < rs.getColumnDefinitions().size(); i++) {
                        types.put(
                                rs.getColumnDefinitions().get(i).getName().toString().toUpperCase(),
                                rs.getColumnDefinitions().get(i).getType().getProtocolCode());
                    }
                    return types;
                });

        Object[] parameters = new Object[attrs.getLength()];
        for (int i = 0; i < attrs.getLength(); i++) {
            Integer colType = Objects.requireNonNull(colTypes).get(attrs.getQName(i).toUpperCase());
            if (colType == null) {
                LOG.warn("No column type found for {}", attrs.getQName(i).toUpperCase());
                colType = ProtocolConstants.DataType.VARCHAR;
            }

            String value = paramSubstitutor.replace(attrs.getValue(i));
            if (value == null) {
                LOG.warn("Variable ${} could not be resolved", attrs.getValue(i));
                value = attrs.getValue(i);
            }

            switch (colType) {
                case ProtocolConstants.DataType.INT:
                case ProtocolConstants.DataType.TINYINT:
                case ProtocolConstants.DataType.SMALLINT:
                    try {
                    parameters[i] = Integer.valueOf(value);
                } catch (NumberFormatException e) {
                    LOG.error("Unparsable Integer '{}'", value);
                    parameters[i] = value;
                }
                break;

                case ProtocolConstants.DataType.DECIMAL:
                case ProtocolConstants.DataType.BIGINT:
                    try {
                    parameters[i] = Long.valueOf(value);
                } catch (NumberFormatException e) {
                    LOG.error("Unparsable Long '{}'", value);
                    parameters[i] = value;
                }
                break;

                case ProtocolConstants.DataType.DOUBLE:
                    try {
                    parameters[i] = Double.valueOf(value);
                } catch (NumberFormatException e) {
                    LOG.error("Unparsable Double '{}'", value);
                    parameters[i] = value;
                }
                break;

                case ProtocolConstants.DataType.FLOAT:
                    try {
                    parameters[i] = Float.valueOf(value);
                } catch (NumberFormatException e) {
                    LOG.error("Unparsable Float '{}'", value);
                    parameters[i] = value;
                }
                break;

                case ProtocolConstants.DataType.DATE:
                case ProtocolConstants.DataType.TIME:
                case ProtocolConstants.DataType.TIMESTAMP:
                    try {
                    parameters[i] = FormatUtils.parseDate(value);
                } catch (DateTimeParseException e) {
                    LOG.error("Unparsable Date '{}'", value);
                    parameters[i] = value;
                }
                break;

                case ProtocolConstants.DataType.BOOLEAN:
                    parameters[i] = "1".equals(value) ? Boolean.TRUE : Boolean.FALSE;
                    break;

                case ProtocolConstants.DataType.BLOB:
                    try {
                    parameters[i] = DatatypeConverter.parseHexBinary(value);
                } catch (IllegalArgumentException e) {
                    LOG.warn("Error decoding hex string to specify a blob parameter", e);
                    parameters[i] = value;
                } catch (Exception e) {
                    LOG.warn("Error creating a new blob parameter", e);
                }
                break;

                case ProtocolConstants.DataType.LIST:
                    parameters[i] = POJOHelper.deserialize(value, new TypeReference<List<String>>() {
                    });
                    break;

                default:
                    parameters[i] = value;
            }
        }

        return parameters;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
            throws SAXException {

        // skip root element
        if (rootElement.equals(qName)) {
            return;
        }
        if ("fetch".equalsIgnoreCase(qName)) {
            String value = operations.getCqlOperations().queryForObject(atts.getValue("query"), String.class);
            String key = atts.getValue("key");
            fetches.put(key, value);
        } else {
            StringBuilder query = new StringBuilder("INSERT INTO \"").append(qName).append("\"(");

            StringBuilder values = new StringBuilder();

            for (int i = 0; i < atts.getLength(); i++) {
                query.append(atts.getQName(i));
                values.append('?');
                if (i < atts.getLength() - 1) {
                    query.append(',');
                    values.append(',');
                }
            }
            query.append(") VALUES (").append(values).append(')');

            try {
                operations.getCqlOperations().execute(query.toString(), getParameters(qName, atts));
            } catch (DataAccessException e) {
                LOG.error("While trying to perform {} with params {}", query, getParameters(qName, atts), e);
                if (!continueOnError) {
                    throw e;
                }
            }
        }
    }
}
