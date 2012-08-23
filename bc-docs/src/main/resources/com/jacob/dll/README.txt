JACOB (Java-COM bridge) is hosted on Sourceforge http://sourceforge.net/project/jacob-project

Information about what's new in this release can be found in docs/ReleaseNotes.html

Instructions on building this project can be found in docs/BuildingJacobFromSource.html
Detailed instructions on creating a build configuration file are in build.xml

Put the appropriate DLL for your platform into your runtime library path.
jacob for 32 bit windows is located in /x86.

There is no good usage guide at this time.
--------------------------------------------------------------
Microsoft Visual C++ library dependencies.

Jacob 1.15 is build with VC++ 2005 statically linked into the DLL. This removes the need for a separate msvcr80.dll installation.

Jacob 1.13 is built with VC++ 2005 that creates a dependency on msvcr80.dll. Windows XP and later seem to already include the necessary components. NT/2000 and Server/2003 require that you download the Visual C 2005 redistributable package, vcredist_x86.exe from the Microsoft web site. Microsoft has a download available that supplies the necessary components. It is distributed as a redistributable package.

If you see the following message then you probably don't have the right C++ libraries.

Exception in thread "main" java.lang.UnsatisfiedLinkError: C:\apps\...\jacob.dll: This application has 
failed to start because the application configuration is incorrect. Reinstalling the application may fix this problem 

Microsoft Visual C++ 2005 SP1 Redistributable Package (x86):http://www.microsoft.com/downloads/details.aspx?familyid=200B2FD9-AE1A-4A14-984D-389C36F85647&displaylang=en
Microsoft Visual C++ 2005 SP1 Redistributable Package (x64):http://www.microsoft.com/en-us/download/details.aspx?id=18471
Microsoft Visual C++ 2010 Redistributable Package (x64):http://www.microsoft.com/en-us/download/details.aspx?id=14632
--------------------------------------------------------------
Unit Tests

Jacob must know the location of the DLL when running the unit tests in Eclipse. The simplest way to do this is to add the dll path to the unit as a VM argument. The argument should be specified based on where you installed the jacob source package. If you have jacob unpacked in c:/dev/jacob and built using build.xml, then the vm arguments would be: 
-Djava.library.path=c:/dev/jacob/release/x86 .
--------------------------------------------------------------
--------------------------------------------------------------
--------------------------------------------------------------
--------------------------------------------------------------
