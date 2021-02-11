(cd tools/JsonSubSchema && pipenv install)

(cd tools/IsJsonSchemaSubset && yarn install)

(cd josch && mvn clean install)
cp josch/josch.presentation/josch.presentation.gui/josch.presentation.gui.controller/target/josch-1.0-jar-with-dependencies.jar Josch.jar
