The bbhunspell library is a variation on the jHUnCheck JNI interface available from the Hunspell SourceForge website, http://hunspell.sourceforge.net/.
The library provides the functionality for the spell checker implemented in Brailleblaster.  The library provides access to Hunspell's open, spell, suggest,
addWord, and close functions.  Please note that Hunspell's addWord function only adds to the runtime dictionary and adding words to a user dictionary is 
handled by other parts of the Brailleblaster code.  Current build instructions involve a series of instructions from the command line.  Individuals well-versed
in creating make or nmake files are encouraged to contribute to easing the build process.

Windows Build:
The following steps are used to build a windows dll.  There may be an easier way to build from the command line.  Individuals with g++ and make file experience
are encouraged to contribute to streamline the process.  The current dynamic link library was built using MSys running MingGW64.  

1. Download and unpack the tarball from the Hunspell website,  http://hunspell.sourceforge.net/.
2. Place SpellChecker.h and SpellChecker.c in the Hunspell's src/hunspell directory.
3. In MSys navigate to the src/hunspell folder and run the following command to compile:
	g++ -static-libgcc -static-libstdc++ -c -I"JAVA_PATH/include" -I"JAVA_PATH/win32" SpellChecker.cpp suggestmgr.cxx replist.cxx phonet.cxx hunzip.cxx hunspell.cxx hashmgr.cxx filemgr.cxx dictmgr.cxx csutil.cxx affixmgr.cxx affentry.cxx
4. To link and build the dynamic link library run the following command:
	g++ -Wl,--add-stdcall-alias -static-libgcc -static-libstdc++ -I"JAVA_PATH/include" -I"Java_Path/include/win32" -shared -o bbhunspell.dll SpellChecker.o suggestmgr.o replist.o phonet.o hunzip.o hunspell.o hashmgr.o filemgr.o dictmgr.o csutil.o affixmgr.o affentry.o
	
	*Note that for both command line entries that JAVA_PATH represents the path to your Java jdk.