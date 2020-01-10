# install to maven repository
mvn clean
mvn compile -Dmaven.test.skip=true
mvn install -Dmaven.test.skip=true