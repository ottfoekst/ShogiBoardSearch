cd /D %~dp0
java -cp .\lib\ShogiBoardSearch.jar;.\lib\PetankoShogi_20151104.jar org.petanko.ottfoekst.boardsrch.main.ShogiBoardSearch .\index %1 %2 %3 %4 >.\log\result.log

pause