package com.recomdata.transmart.externaltool

import com.recomdata.export.IgvFiles
import org.grails.plugins.web.taglib.ApplicationTagLib

class IgvController {

    def springSecurityService;
    def igvDataService

    def launchJNLP = {

        def webRootDir = servletContext.getRealPath("/")
        /*
        // get data first
        //String newIGVLink = new ApplicationTagLib().createLink(controller:'analysis', action:'getGenePatternFile', absolute:true)
        String fileDirName = grailsApplication.config.com.recomdata.analysis.data.file.dir;
        if(fileDirName == null)
        fileDirName = "data";
        String newIGVLink = new ApplicationTagLib().createLink(controller:fileDirName, , absolute:true)

        IgvFiles igvFiles = new IgvFiles(getIgvFileDirName(),newIGVLink)

        // testing data
        def f = new File (webRootDir + "/data/" + "test.vcf")
        igvFiles.addFile(f);

        String userName = springSecurityService.getPrincipal().username;


        // create session file URL
        def sessionfileURL = igvDataService.createSessionURL(igvFiles, userName)

        // create JNLP file


        */

        def sessionFileURL = params.sessionFile;
        log.debug(sessionFileURL)
        def ftext = igvDataService.createJNLPasString(webRootDir, sessionFileURL);


        response.setHeader("Content-Type", "application/x-java-jnlp-file")

        //println(ftext)
        response.outputStream << ftext

    }

    //This URL will be launched with the job ID in the query string.
    def launchIGV = {

        def webRootDir = servletContext.getRealPath("/")

        //Grab the job ID from the query string.
        String jobName = params.jobName
        println params;

        // loop through and find all files
        def resultfileDir = getIgvFileDirName()

        // get data first
        String fileDirName = grailsApplication.config.com.recomdata.analysis.data.file.dir;
        if (fileDirName == null)
            throw new Exception("property com.recomdata.analysis.data.file.dir is not set ")
        String newIGVLink = new ApplicationTagLib().createLink(controller: fileDirName, absolute: true)

        IgvFiles igvFiles = new IgvFiles(getIgvFileDirName(), newIGVLink)

        // find result files -might be multiple
        def pattern = jobName + "*.vcf"
        def dataFiles = new FileNameFinder().getFileNames(resultfileDir, pattern);
        dataFiles.each {
            igvFiles.addFile(new File(it))
        }

        // find param files - should have one
        def parampattern = jobName + "_vcf.params"
        def pFiles = new FileNameFinder().getFileNames(resultfileDir, parampattern);
        println("param files:" + pFiles)
        def paramfile = "";
        def locus = null;
        def gene = null
        def chr = null
        def snp = null;
        if (pFiles != null && !pFiles.isEmpty()) {
            paramfile = pFiles[0]
            log.debug("find paramfile:" + paramfile)
            def f = new File(paramfile)
            if (f.exists()) {
                f.eachLine { line ->
                    if (line.startsWith("Chr=") && chr == null) {
                        chr = line.substring(4);
                    }
                    if (line.startsWith("Gene=") && gene == null) {
                        gene = line.substring(5)
                    }
                    if (line.startsWith("SNP=") && snp == null) {
                        snp = line.substring(4)
                    }
                }
            }

            // try to create locus hint for igv
            if (snp != null) {
                locus = snp;
            } else if (gene != null) {
                locus = gene;
            } else {
                locus = chr;
            }

        }

        // testing 	data
        //	def f = new File (webRootDir + "/data/" + "test.vcf")
        //	igvFiles.addFile(f);

        String userName = springSecurityService.getPrincipal().username;

        // create session file URL
        def sessionfileURL = igvDataService.createSessionURL(igvFiles, userName, locus)

        render(view: "launch", model: [sessionFile: sessionfileURL])


    }

    protected String getIgvFileDirName() {
        String fileDirName = grailsApplication.config.com.recomdata.analysis.data.file.dir;
        def webRootName = servletContext.getRealPath("/");
        if (webRootName.endsWith(File.separator) == false)
            webRootName += File.separator;
        return webRootName + fileDirName;
    }
}
