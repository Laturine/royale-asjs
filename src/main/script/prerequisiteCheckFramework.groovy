import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import java.lang.reflect.Field
import java.util.regex.Matcher

/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

allConditionsMet = true

baseDirectory = project.model.pomFile.parent

def checkTotalMemory(long requiredMaxMegs) {
    print "Checking available memory:  "
    long curMaxMegs = (long) (Runtime.runtime.maxMemory() / (1024*1024))
    if(curMaxMegs < requiredMaxMegs) {
        println "Failed: To little memory available. "
        println "Please set the MAVEN_OPTS environment variable to allow at least " + requiredMaxMegs + "MB of ram."
        println "Example:"
        if(os == "win") {
            println "set MAVEN_OPTS=-Xmx" + requiredMaxMegs + "m"
        } else {
            println "export MAVEN_OPTS=-Xmx" + requiredMaxMegs + "m"
        }
        allConditionsMet = false
    }
    return true
}

/*
 Checks if a given version number is at least as high as a given reference version.
*/
def checkVersionAtLeast(String current, String minimum) {
    def currentSegments = current.tokenize('.')
    def minimumSegments = minimum.tokenize('.')
    def numSegments = Math.min(currentSegments.size(), minimumSegments.size())
    for (int i = 0; i < numSegments; ++i) {
        def currentSegment = currentSegments[i].toInteger()
        def minimumSegment = minimumSegments[i].toInteger()
        if(currentSegment < minimumSegment) {
            println current.padRight(14) + "FAILED (required " + minimum + ")"
            return false
        } else if(currentSegment > minimumSegment) {
            println current.padRight(14) + "OK"
            return true
        }
    }
    def curNotShorter = currentSegments.size() >= minimumSegments.size()
    if(curNotShorter) {
        println current.padRight(14) + " OK"
    } else {
        println current.padRight(14) + " (required " + minimum + ")"
    }
    curNotShorter
}

/**
 * Version extraction function/macro. It looks for occurrence of x.y or x.y.z
 * in passed input text (likely output from `program --version` command if found).
 *
 * @param input
 * @return
 */
private Matcher extractVersion(input) {
    def matcher = input =~ /(\d+\.\d+(\.\d+)?).*/
    matcher
}

/////////////////////////////////////////////////////
// Find out which OS and arch are bring used.
/////////////////////////////////////////////////////

def osString = project.properties['os.type']
def osMatcher = osString =~ /(.*)/
if(osMatcher.size() == 0) {
    throw new RuntimeException("Currently unsupported OS")
}
os = osMatcher[0][1]
println "Detected OS:                    " + os

flashVersion = project.properties['flash.version']
println "Detected minimum Flash version: " + flashVersion

airVersion = project.properties['air.version']
println "Detected minimum Air version:   " + airVersion

/////////////////////////////////////////////////////
// Find out which profiles are enabled.
/////////////////////////////////////////////////////

println "Enabled profiles:"
def distributionEnabled = false
def examplesEnabled = false
def manualtestsEnabled = false
def uiTestsuiteEnabled = false
def optionWithSwfEnabled = false
def activeProfiles = session.request.activeProfiles
for (def activeProfile : activeProfiles) {
    if(activeProfile == "with-distribution") {
        distributionEnabled = true
        println "distribution"
    } else if(activeProfile == "with-examples") {
        examplesEnabled = true
        println "examples"
    } else if(activeProfile == "with-manualtests") {
        manualtestsEnabled = true
        println "manualtests"
    } else if(activeProfile == "with-ui-testsuite") {
        uiTestsuiteEnabled = true
        println "ui-testsuite"
    }
}
println ""

// - Windows:
//     - Check the length of the path of the base dir as we're having issues with the length of paths being too long.
if(os == "win") {
    File pomFile = project.model.pomFile
    if(pomFile.absolutePath.length() > 100) {
        println "On Windows we encounter problems with maximum path lengths. " +
            "Please move the project to a place it has a shorter base path " +
            "and run the build again."
        allConditionsMet = false
    }
}

/////////////////////////////////////////////////////
// Do the actual checks depending on the enabled
// profiles.
/////////////////////////////////////////////////////

if(examplesEnabled) {
    // Check at least 1024 mb of memory are available to the build.
    checkTotalMemory(1024)
}

if(!allConditionsMet) {
    throw new RuntimeException("Not all conditions met, see log for details.")
}
println ""
println "All known conditions met successfully."
println ""
