cd ..
call mvn clean
call mvn clean install -Dmaven.test.skip=true
@pause