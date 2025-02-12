package org.transmartproject.rest
/*
 * Copyright 2014 Janssen Research & Development, LLC.
 *
 * This file is part of REST API: transMART's plugin exposing tranSMART's
 * data via an HTTP-accessible RESTful API.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version, along with the following terms:
 *
 *   1. You may convey a work based on this program in accordance with
 *      section 5, provided that you retain the above notices.
 *   2. You may convey verbatim copies of this program code as you receive
 *      it, in any medium, provided that you retain the above notices.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import grails.util.Environment
import groovy.util.logging.Slf4j
import org.transmartproject.db.TestData
import org.transmartproject.db.user.AccessLevelTestData

@Slf4j
class BootStrap {

    static TestData testData
    static AccessLevelTestData accessLevelTestData

    static setupTestData() {
        testData = TestData.createDefault()
        testData.saveAll()
        new org.transmartproject.rest.test.TestData().createTestData()
        accessLevelTestData = AccessLevelTestData.createWithAlternativeConceptData(testData.conceptData)
        accessLevelTestData.saveAll()
    }

    def init = { servletContext ->
        if (Environment.current == Environment.TEST) {
            log.info "Setting up test data..."
            setupTestData()
        }
    }

}
