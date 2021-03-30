[![New Relic Experimental header](https://github.com/newrelic/opensource-website/raw/master/src/images/categories/Experimental.png)](https://opensource.newrelic.com/oss-category/#new-relic-experimental)

# Executors Instrumentation for Java Agent

This instrumentation is used to instrument the Java ExecutorService and CompletableFutures.

## Installation
Note that because these extensions override the CompletableFutures instrumentation included in the Java Agent, it is necessary to disable that instrumentation.   
   
To install:   
1. Download the latest release jar files.    
2. In the New Relic Java directory (the one containing newrelic.jar), create a directory named extensions if it does not already exist.   
3. Copy the downloaded jars into the extensions directory. 
4. Edit newrelic.yml
5. Find class_transformer: stanza
6. Add  com.newrelic.instrumentation.java.completable-future-jdk8 & com.newrelic.instrumentation.java.completable-future-jdk8u40 with enabled set to false
7. Save newrelic.yml 
8. Restart the application.   

The class_transformer stanza should look like this:   
   
&nbsp;&nbsp;class_transformer:   
&nbsp;&nbsp;&nbsp;&nbsp;# This instrumentation reports the name of the user principal returned from    
&nbsp;&nbsp;&nbsp;&nbsp;# HttpServletRequest.getUserPrincipal() when servlets and filters are invoked.   
&nbsp;&nbsp;&nbsp;&nbsp;com.newrelic.instrumentation.servlet-user:   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;enabled: false  
   
&nbsp;&nbsp;&nbsp;&nbsp;com.newrelic.instrumentation.spring-aop-2:   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;enabled: false  
   
&nbsp;&nbsp;&nbsp;&nbsp;com.newrelic.instrumentation.java.completable-future-jdk8:   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;enabled: false  
   
&nbsp;&nbsp;&nbsp;&nbsp;com.newrelic.instrumentation.java.completable-future-jdk8u40:   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;enabled: false  
   
## Building

Building the extension requires that Gradle is installed.
To build the extension jars from source, follow these steps:
### Build single extension
To build a single extension with name *extension*, do the following:
1. Set an environment variable *NEW_RELIC_EXTENSIONS_DIR* and set its value to the directory where you want the jar file built.
2. Run the command: gradlew *extension*:clean *extension*:install
### Build all extensions
To build all extensions, do the following:
1. Set an environment variable *NEW_RELIC_EXTENSIONS_DIR* and set its value to the directory where you want the jar file built.
2. Run the command: gradlew clean install
 
## Support

New Relic has open-sourced this project. This project is provided AS-IS WITHOUT WARRANTY OR DEDICATED SUPPORT. Issues and contributions should be reported to the project here on GitHub.

We encourage you to bring your experiences and questions to the [Explorers Hub](https://discuss.newrelic.com) where our community members collaborate on solutions and new ideas.

## Contributing

We encourage your contributions to improve Executors Instrumentation for Java Agent. Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. You only have to sign the CLA one time per project. If you have any questions, or to execute our corporate CLA, required if your contribution is on behalf of a company, please drop us an email at opensource@newrelic.com.

**A note about vulnerabilities**

As noted in our [security policy](../../security/policy), New Relic is committed to the privacy and security of our customers and their data. We believe that providing coordinated disclosure by security researchers and engaging with the security community are important means to achieve our security goals.

If you believe you have found a security vulnerability in this project or any of New Relic's products or websites, we welcome and greatly appreciate you reporting it to New Relic through [HackerOne](https://hackerone.com/newrelic).

## License

Executors Instrumentation for Java Agent is licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.
