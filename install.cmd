call mvn clean
call mvn compile -Dmaven.test.skip=true
call mvn install -Dmaven.test.skip=true