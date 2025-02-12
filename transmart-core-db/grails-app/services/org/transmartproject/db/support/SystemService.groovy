package org.transmartproject.db.support

import grails.util.Holders
import groovy.transform.CompileStatic
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.transmartproject.core.config.RuntimeConfig
import org.transmartproject.core.config.SystemResource
import org.transmartproject.core.userquery.UserQuerySetResource
import org.transmartproject.core.users.User
import org.transmartproject.db.clinical.AggregateDataOptimisationsService
import org.transmartproject.db.clinical.AggregateDataService
import org.transmartproject.db.clinical.PatientSetService
import org.transmartproject.db.config.RuntimeConfigImpl
import org.transmartproject.core.config.RuntimeConfigRepresentation
import org.transmartproject.db.ontology.MDStudiesService
import org.transmartproject.db.ontology.OntologyTermTagsResourceService
import org.transmartproject.db.ontology.TrialVisitsService
import org.transmartproject.db.tree.TreeCacheService

import javax.validation.Valid

@CompileStatic
class SystemService implements SystemResource {

    private final int DEFAULT_PATIENT_SET_CHUNK_SIZE = 10000

    private final RuntimeConfigImpl runtimeConfig = new RuntimeConfigImpl(
            Holders.config.getProperty('org.transmartproject.system.numberOfWorkers', Integer.class, Runtime.getRuntime().availableProcessors()),
            Holders.config.getProperty('org.transmartproject.system.patientSetChunkSize', Integer.class, DEFAULT_PATIENT_SET_CHUNK_SIZE)
    )

    private final ModelMapper modelMapper = new ModelMapper()

    @Autowired
    AggregateDataService aggregateDataService

    @Autowired
    TreeCacheService treeCacheService

    @Autowired
    OntologyTermTagsResourceService ontologyTermTagsResourceService

    @Autowired
    MDStudiesService studiesService

    @Autowired
    TrialVisitsService trialVisitsService

    @Autowired
    UserQuerySetResource userQuerySetResource

    @Autowired
    AggregateDataOptimisationsService aggregateDataOptimisationsService

    @Autowired
    PatientSetService patientSetService

    RuntimeConfig getRuntimeConfig() {
        return modelMapper.map(runtimeConfig, RuntimeConfigRepresentation.class)
    }

    RuntimeConfig updateRuntimeConfig(@Valid RuntimeConfig config) {
        runtimeConfig.setNumberOfWorkers(config.numberOfWorkers)
        runtimeConfig.setPatientSetChunkSize(config.patientSetChunkSize)
        getRuntimeConfig()
    }

    /**
     * Clears the caches, patient sets, refreshes a materialized view with study_concept bitset
     * and scans for the changes for subscribed user queries.
     * @param currentUser
     */
    void updateAfterDataLoading(User currentUser) {
        clearCaches()
        aggregateDataOptimisationsService.clearPatientSetBitset()
        patientSetService.clearPatientSets()
        userQuerySetResource.scan(currentUser)
    }

    /**
     * Clears the tree node cache, the tags cache, the counts caches and the studies caches.
     * This function should be called after loading, removing or updating
     * tree nodes or observations in the database.
     */
    void clearCaches() {
        treeCacheService.clearAllCacheEntries()
        ontologyTermTagsResourceService.clearTagsCache()
        aggregateDataService.clearCountsCache()
        aggregateDataService.clearCountsPerStudyAndConceptCache()
        studiesService.clearCaches()
        trialVisitsService.clearCache()
    }

}
