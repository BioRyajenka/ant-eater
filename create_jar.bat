rmdir bin /s /q
mkdir bin
javac src\ru\ifmo\ctddev\sushencev\anteater\*.java src\ru\ifmo\ctddev\sushencev\anteatervisualizer\*.java -d bin

cd bin
jar cfm ..\visualizer.jar ..\manifest.txt ru\ifmo\ctddev\sushencev\anteater\*.class ru\ifmo\ctddev\sushencev\anteatervisualizer\*.class 2>..\2.txt