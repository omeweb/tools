@cd ..
call mvn eclipse:clean
call mvn eclipse:eclipse -DdownloadSources=true
@pause