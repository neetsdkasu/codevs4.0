@setlocal
@if "%~1"=="" goto label1

@set f=".\src\neetsdkasu\%~1.java"

@if exist %f% goto label2

@copy _template_.java %f%
@echo public class %~1 >> %f%
@echo ^{ >> %f%
@echo ^} >> %f%

@call ed.bat %f%

@goto endlabel

:label1
@echo this is a batch file to make new class source file
@goto endlabel

:label2
@echo %~1 is exist
@call ed.bat %f%
@goto endlabel



:endlabel
@endlocal
