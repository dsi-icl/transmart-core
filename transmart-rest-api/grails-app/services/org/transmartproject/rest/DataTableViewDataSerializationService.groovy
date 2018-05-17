/*
 * Copyright © 2013-2014 The Hyve B.V.
 *
 * This file is part of transmart-core-db.
 *
 * Transmart-core-db is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * transmart-core-db.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.transmartproject.rest

import com.google.common.collect.ImmutableList
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.transmartproject.core.dataquery.DataColumn
import org.transmartproject.core.exceptions.InvalidArgumentsException
import org.transmartproject.core.multidimquery.DataTable
import org.transmartproject.core.multidimquery.MultiDimensionalDataResource
import org.transmartproject.core.multidimquery.StreamingDataTable
import org.transmartproject.core.multidimquery.query.Constraint
import org.transmartproject.core.users.User
import org.transmartproject.rest.serialization.DataSerializer
import org.transmartproject.rest.serialization.Format
import org.transmartproject.rest.serialization.tabular.DataTableTSVSerializer
import org.transmartproject.rest.serialization.tabular.TabularResultSerializer
import org.transmartproject.rest.serialization.tabular.TabularResultTSVSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.zip.ZipOutputStream

@Transactional
@CompileStatic
class DataTableViewDataSerializationService implements DataSerializer {

    static final Logger log = LoggerFactory.getLogger(DataTableViewDataSerializationService.class)

    @Autowired
    MultiDimensionalDataResource multiDimService

    Set<Format> supportedFormats = [Format.TSV] as Set<Format>

    /**
     * Write clinical data to the output stream.
     *
     * @param format
     * @param constraint
     * @param user
     * @param out
     * @param options
     */
    @Override
    void writeClinical(Format format,
                       Constraint constraint,
                       User user,
                       OutputStream out,
                       Map options) {

        def tableArgs = (Map) options.tableConfig
        if(tableArgs == null) throw new InvalidArgumentsException("No 'tableConfig' option provided")

        StreamingDataTable datatable = multiDimService.retrieveStreamingDataTable(
                tableArgs, 'clinical', constraint, user)
        try {
            log.info "Writing tabular data in ${format} format."
            def serializer = new DataTableTSVSerializer(user, out)
            serializer.writeDataTableToZip(datatable)
        } finally {
            datatable.close()
            log.info "Writing tabular data in ${format} format completed."
        }
    }

    @Override
    void writeHighdim(Format format,
                      String type,
                      Constraint assayConstraint,
                      Constraint biomarkerConstraint,
                      String projection,
                      User user,
                      OutputStream out) {
        throw new UnsupportedOperationException("Writing HD data for this view is not supported.")
    }

}
