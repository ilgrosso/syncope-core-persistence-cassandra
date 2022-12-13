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

import static org.apache.syncope.core.persistence.api.content.ContentDealer.ROOT_ELEMENT;

import java.io.IOException;
import java.io.InputStream;
import javax.sql.DataSource;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.syncope.core.persistence.api.content.ContentLoader;
import org.apache.syncope.core.persistence.cassandra.entity.CassandraPlainSchema;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.xml.sax.SAXException;

public class XMLContentLoader implements ContentLoader {

    protected static final Logger LOG = LoggerFactory.getLogger(XMLContentLoader.class);

    protected final CassandraOperations operations;

    protected final Environment env;

    public XMLContentLoader(final CassandraOperations operations, final Environment env) {
        this.operations = operations;
        this.env = env;
    }

    @Override
    public int getOrder() {
        return 400;
    }

    @Override
    public void load(final String domain, final DataSource datasource) {
        LOG.debug("Loading data for domain [{}]", domain);

        boolean existingData;
        try {
            existingData = operations.count(CassandraPlainSchema.class) > 0;
        } catch (DataAccessException e) {
            LOG.error("[{}] Could not access table " + CassandraPlainSchema.TABLE, domain, e);
            existingData = true;
        }

        if (existingData) {
            LOG.info("[{}] Data found in the database, leaving untouched", domain);
        } else {
            LOG.info("[{}] Empty database found, loading default content", domain);

            try {
                InputStream contentXML = ApplicationContextProvider.getBeanFactory().
                        getBean(domain + "ContentXML", InputStream.class);
                loadDefaultContent(domain, contentXML);
            } catch (Exception e) {
                LOG.error("[{}] While loading default content", domain, e);
            }
        }
    }

    protected void loadDefaultContent(
            final String domain, final InputStream contentXML)
            throws IOException, ParserConfigurationException, SAXException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        try (contentXML) {
            SAXParser parser = factory.newSAXParser();
            parser.parse(contentXML, new ContentLoaderHandler(operations, ROOT_ELEMENT, true, env));
            LOG.debug("[{}] Default content successfully loaded", domain);
        }
    }
}
