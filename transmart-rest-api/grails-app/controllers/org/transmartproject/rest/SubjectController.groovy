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

package org.transmartproject.rest

import grails.rest.Link
import org.transmartproject.core.dataquery.Patient
import org.transmartproject.core.dataquery.clinical.PatientsResource
import org.transmartproject.core.exceptions.NoSuchResourceException
import org.transmartproject.core.ontology.OntologyTermsResource
import org.transmartproject.core.ontology.OntologyTerm
import org.transmartproject.rest.marshallers.ContainerResponseWrapper
import org.transmartproject.rest.marshallers.PatientWrapper
import org.transmartproject.rest.ontology.OntologyTermCategory

class SubjectController {

    static responseFormats = ['json', 'hal']

    StudyLoadingService studyLoadingServiceProxy
    PatientsResource    patientsResourceService
    OntologyTermsResource ontologyTermsResourceService

    /** GET request on /v1/studies/XXX/subjects/
     *  This will return the list of subjects for certain study,
     *  where each subject will be rendered in its short format
    */
    def index() {
        Set<Patient> patients = studyLoadingServiceProxy.study.patients
        respond wrapSubjects(patients, selfLinkForStudy())
    }

    /** GET request on /v1/studies/XXX/subjects/${id}
     *  This returns the single subject for certain study.
     *
     *  @param id The is for which to return Data information.
     */
    def show(Integer id) {
        def patient = patientsResourceService.getPatientById(id)

        def studyId = studyLoadingServiceProxy.study.id
        if (patient.trial != studyId) {
            throw new NoSuchResourceException("The patient with id $id " +
                    "does not belong to the study '$studyId'")
        }

        respond new PatientWrapper(apiVersion: 'v1', patient: patient)
    }

    /** GET request on /v1/studies/XXX/concepts/YYY/subjects
     *
     * @return list of subjects for study XXX and Data YYY
     */
    def indexByConcept() {
        use (OntologyTermCategory) {
            def ontologyTermKey = params.conceptId.keyFromURLPart(
                    studyLoadingServiceProxy.study)
            def ontologyTerm = ontologyTermsResourceService.getByKey(ontologyTermKey)
            def patients = ontologyTerm.patients
            def selfLink = selfLinkForConcept ontologyTerm

            respond wrapSubjects(patients, selfLink)
        }
    }

    private def selfLinkForStudy() {
        "/v1/studies/${studyLoadingServiceProxy.studyLowercase}/subjects"
    }

    private def selfLinkForConcept(OntologyTerm term) {
        use (OntologyTermCategory) {
            "/v1/studies/${studyLoadingServiceProxy.studyLowercase}/concepts/" +
                    term.encodeAsURLPart(studyLoadingServiceProxy.study) +
            '/subjects'
        }
    }

    private def wrapSubjects(Collection<Patient> source, String selfLink) {

        new ContainerResponseWrapper(
                key: 'subjects',
                container: source.collect { new PatientWrapper(apiVersion: 'v1', patient: it) },
                componentType: PatientWrapper,
                links: [
                        new Link(grails.rest.render.util.AbstractLinkingRenderer.RELATIONSHIP_SELF,
                                selfLink
                        )
                ]
        )
    }

}
