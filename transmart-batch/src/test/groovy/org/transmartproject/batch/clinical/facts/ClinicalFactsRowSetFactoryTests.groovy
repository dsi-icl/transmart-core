package org.transmartproject.batch.clinical.facts

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.transmartproject.batch.clinical.variable.ClinicalVariable
import org.transmartproject.batch.clinical.xtrial.XtrialMappingCollection
import org.transmartproject.batch.concept.ConceptFragment
import org.transmartproject.batch.concept.ConceptPath
import org.transmartproject.batch.concept.ConceptTree
import org.transmartproject.batch.concept.ConceptType
import org.transmartproject.batch.facts.ClinicalFactsRowSet
import org.transmartproject.batch.patient.DemographicVariable
import org.transmartproject.batch.patient.PatientSet
import org.transmartproject.batch.secureobject.Study

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.transmartproject.batch.clinical.variable.ClinicalVariable.*

/**
 * Unit test for building ClinicalFactsRowSet instance from a clinical data file row.
 */
class ClinicalFactsRowSetFactoryTests {

    def topNodePath = new ConceptPath('\\Test\\')
    def fileName = 'foo.txt'
    def studyId = 'test_study'
    Long studyNum = -100

    ClinicalFactsRowSetFactory testee

    List<ClinicalVariable> defaultVariables = buildClinicalVariables([
            [categoryCode: 'Subjects',        columnNumber: 1,  dataLabel: SUBJ_ID],
            [categoryCode: 'Site',            columnNumber: 2,  dataLabel: SITE_ID],
            [categoryCode: 'Visit',           columnNumber: 3,  dataLabel: VISIT_NAME],
            [categoryCode: 'Data_Label',      columnNumber: 4,  dataLabel: DATA_LABEL],
            [categoryCode: 'DATA_LABEL',      columnNumber: 5,  dataLabel: DATA_LABEL],

            /* Templates */
            //Note: legacy placeholders has no underscore in it.
            [categoryCode: 'DataLabel+DATA_LABEL+DATALABEL+smth1',
             columnNumber: 6,  dataLabel: TEMPLATE, dataLabelSource: 5],
            [categoryCode: 'SiteId+SITEID+SITE_ID+VisitName+VISIT_NAME+VISITNAME',
             columnNumber: 7,  dataLabel: TEMPLATE, dataLabelSource: 4],
    ])

    List<ClinicalDataRow> defaultRows = buildClinicalDataRows([
        ['sj01', 'st+01', 'vs_01', 'DATA_LABEL+11', 'Data label 12', 'tmpl_+11', '71', '45', 'male'],
    ])

    @Before
    void init() {
        testee = new ClinicalFactsRowSetFactory()
        testee.with {
            studyId = this.studyId
            study = new Study(studyId: studyId, studyNum: studyNum)
            topNodePath = this.topNodePath
            tree = new ConceptTree(topNodePath: topNodePath)
            patientSet = new PatientSet()
            xtrialMapping = new XtrialMappingCollection(topNode: topNodePath)
        }
    }

    @Test
    void testTemplateConceptPathContainsDataLabel() {
        testee.variables = defaultVariables

        ClinicalFactsRowSet rowSet = testee.create(defaultRows[0])

        def expected = 'DataLabel\\DATA LABEL\\Data label 12\\smth1\\tmpl_+11\\'
        assertThat rowSet, hasProperty('clinicalFacts',
            hasItem(allOf(
                    hasProperty('concept', allOf(
                            hasProperty('path',
                                    equalTo(topNodePath + expected)),
                            hasProperty('type', equalTo(ConceptType.CATEGORICAL))
                    )),
                    hasProperty('value', equalTo('tmpl_+11')),
            ))
        )
    }

    @Test
    void testTemplateConceptPathContainsVisitNameAndSiteId() {
        testee.variables = defaultVariables

        ClinicalFactsRowSet rowSet = testee.create(defaultRows[0])

        def expected = 'SiteId\\st+01\\SITE ID\\VisitName\\VISIT NAME\\vs_01\\DATA_LABEL+11\\'
        assertThat rowSet, hasProperty('clinicalFacts',
                hasItem(allOf(
                        hasProperty('concept', allOf(
                                hasProperty('path',
                                        equalTo(topNodePath + expected)),
                                hasProperty('type', equalTo(ConceptType.NUMERICAL))
                        )),
                        hasProperty('value', equalTo('71')),
                ))
        )
    }

    @Test
    void testOneDataLabelInARow() {
        testee.variables = buildClinicalVariables([
                [categoryCode: 'Subjects',        columnNumber: 1,  dataLabel: SUBJ_ID],
                [categoryCode: 'DATA_LABEL',      columnNumber: 2,  dataLabel: DATA_LABEL],
                [categoryCode: 'foo+DATALABEL',   columnNumber: 3,  dataLabel: TEMPLATE],
        ])

        ClinicalFactsRowSet rowSet = testee.create(buildClinicalDataRows([
                ['sj1', 'data label 1', 'test value']
        ])[0])

        assertThat rowSet, hasProperty('clinicalFacts',
                hasItem(allOf(
                        hasProperty('concept', allOf(
                                hasProperty('path',
                                        equalTo(topNodePath +
                                                'foo\\data label 1\\test value\\')),
                                hasProperty('type', equalTo(ConceptType.CATEGORICAL))
                        )),
                        hasProperty('value', equalTo('test value')),
                ))
        )
    }

    @Test
    void testTwoDataLabelInARowAndNoReference() {
        testee.variables = buildClinicalVariables([
                [categoryCode: 'Subjects',        columnNumber: 1,  dataLabel: SUBJ_ID],
                [categoryCode: 'DATA_LABEL',      columnNumber: 2,  dataLabel: DATA_LABEL],
                [categoryCode: 'DATA_LABEL',      columnNumber: 3,  dataLabel: DATA_LABEL],
                [categoryCode: 'foo+DATALABEL',   columnNumber: 4,  dataLabel: TEMPLATE],
        ])

        try {
            //to trigger calculations
            testee.fileVariablesMap
            Assert.fail('Exception is expected')
        } catch (IllegalArgumentException e) {
            assertThat e.message, equalTo('Declaration of column #4 with data label placeholder in category code has' +
                    ' to point with data label source to a data label column.')
        }
    }

    @Test
    void testIllegalDataLabelSourceReference() {
        testee.variables = buildClinicalVariables([
                [categoryCode: 'Subjects',        columnNumber: 1,  dataLabel: SUBJ_ID],
                [categoryCode: 'DATA_LABEL',      columnNumber: 2,  dataLabel: DATA_LABEL],
                [categoryCode: 'foo+DATALABEL',   columnNumber: 3,  dataLabel: TEMPLATE, dataLabelSource: 1],
                [categoryCode: 'DATA_LABEL',      columnNumber: 4,  dataLabel: DATA_LABEL],
        ])

        try {
            //to trigger calculations
            testee.fileVariablesMap
            Assert.fail('Exception is expected')
        } catch (IllegalArgumentException e) {
            assertThat e.message, equalTo('Data label source of column #3 has to point to existing DATA_LABEL column' +
                    ' number.')
        }
    }

    @Test
    void testNewFields() {
        testee.variables = buildClinicalVariables([
                [categoryCode: 'Subjects', columnNumber: 1, dataLabel: SUBJ_ID],
                [categoryCode: 'foo',      columnNumber: 2, dataLabel: TRIAL_VISIT_LABEL],
                [categoryCode: 'bar',      columnNumber: 3, dataLabel: START_DATE],
                [categoryCode: 'baz',      columnNumber: 4, dataLabel: END_DATE],
                [categoryCode: 'faz',      columnNumber: 5, dataLabel: INSTANCE_NUM],
        ])
        String trialVisitLabel = 'Baseline'
        ClinicalDataRow clinicalDataRow = buildClinicalDataRows([
                ['subj_1', trialVisitLabel, '2016-02-29', '2016-03-14', '3']
        ])[0]

        ClinicalFactsRowSet rowSet = testee.create(clinicalDataRow)

        assertThat rowSet, allOf(
                hasProperty('startDate', is(new GregorianCalendar(2016, Calendar.FEBRUARY, 29).time)),
                hasProperty('endDate', is(new GregorianCalendar(2016, Calendar.MARCH, 14).time)),
                hasProperty('instanceNum', is(3)),
                hasProperty('trialVisit', allOf(
                        hasProperty('studyNum', is(studyNum)),
                        hasProperty('relTimeLabel', is(trialVisitLabel)),
                ))
        )
    }

    private List<ClinicalVariable> buildClinicalVariables(List columnMappings) {
        columnMappings.collect {
            new ClinicalVariable(
                    filename: fileName,
                    /** Calculated @see ClinicalVariableFieldMapper */
                    conceptPath: it.dataLabel in ClinicalVariable.RESERVED ?
                            null
                            : topNodePath + ConceptFragment.decode(it.categoryCode) + it.dataLabel,
                    demographicVariable: DemographicVariable.getMatching(it.dataLabel),
                    *: it)
        }
    }

    private List<ClinicalDataRow> buildClinicalDataRows(List rows) {
        def result = []
        rows.eachWithIndex { it, index ->
            result << new ClinicalDataRow(filename: fileName, index: index + 1, values: it)
        }
        result
    }
}
