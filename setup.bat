pushd tools\JsonSubschema && (pipenv install & popd)

pushd tools\IsJsonSchemaSubset && (call yarn install & popd)

pushd josch && (call mvn clean install & popd)

copy josch\josch.presentation\josch.presentation.gui\josch.presentation.gui.controller\target\josch-1.0-jar-with-dependencies.jar Josch.jar
@echo installation complete.