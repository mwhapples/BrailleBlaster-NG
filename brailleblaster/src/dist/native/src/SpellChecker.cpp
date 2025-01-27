//The following file was created making minor changes to the JNI tarball, Huncheck, avavilable on the Hunspell website http://hunspell.sourceforge.net/
#include <jni.h>
#include <cstring>
#include <cstdlib>
#include <cstdio>
#include <iostream>
#include <string>

#include "org_brailleblaster_perspectives_braille_spellcheck_SpellChecker.h"
#include "hunspell.hxx"

using namespace std;

static Hunspell *p;
JNIEXPORT jint JNICALL Java_org_brailleblaster_perspectives_braille_spellcheck_SpellChecker_openDict(JNIEnv *env, jclass clss, jstring dictPath, jstring affPath)
{
	const char *dictFile = env->GetStringUTFChars(dictPath, NULL);
	if (NULL == dictFile) return NULL;
	 
	const char *affFile = env->GetStringUTFChars(affPath, NULL);
	if (NULL == affFile) return NULL;
	
	int result;	 
	p = new Hunspell(affFile,dictFile);
	
	if(p)
		result = 1;
	else
		result = 0;
	
	env->ReleaseStringUTFChars(dictPath, dictFile);
	env->ReleaseStringUTFChars(affPath, affFile);
	 
	return result;
}
  
JNIEXPORT jint JNICALL Java_org_brailleblaster_perspectives_braille_spellcheck_SpellChecker_checkWord(JNIEnv *env, jclass clss, jstring jwd) 
{
  int result = 0;
  
  if(p != NULL){
	const char *wd = env->GetStringUTFChars(jwd, 0);
	result = p->spell(wd);
	env->ReleaseStringUTFChars(jwd, wd);
  }
  
  if(result)
     return 1;
  else
    return 0;
}

#define MAXSUG 256
JNIEXPORT jobject JNICALL Java_org_brailleblaster_perspectives_braille_spellcheck_SpellChecker_checkSug(JNIEnv *env, jclass clss, jstring jwd) {
	int result;
	char sug[MAXSUG] = {0};
	jobject jobj;
	jclass jcls;
	jfieldID jfid;
	jmethodID jmid;

	jcls = env->FindClass("org/brailleblaster/perspectives/braille/spellcheck/Suggestions");
	if (jcls == NULL)
    {
		printf("Error FindClass\n");
		return NULL;
    }
	
	jmid = env->GetMethodID( jcls, "<init>","()V");
	if (jmid == NULL)
    {
		printf("Error GetMethodID\n");
		return NULL;
    }
	
	jobj = env->NewObject( jcls, jmid);
	if (jobj == NULL)
    {
		printf("Error NewObject\n");
		return NULL;
    }

	jfid = env->GetFieldID(jcls, "suggestionList", "Ljava/lang/String;");

	const char *wd = env->GetStringUTFChars(jwd, 0);
	
	result = p->spell(wd);
	if(result){
		env->ReleaseStringUTFChars(jwd, wd);
		return NULL;
	}
	else{
		char ** wlst;
		int suglen = 0;
		int ns = p->suggest(&wlst,wd);
		
		for (int i=0; i < ns; i++) {
			if(suglen + strlen(wlst[i]) + 1 < MAXSUG) {
				strcat(sug,wlst[i]);
				strcat(sug, " ");
				suglen = strlen(wlst[i]) + 1;
			}
			free(wlst[i]);
		}
		
		env->SetObjectField( jobj, jfid, env->NewStringUTF(sug ));
		env->ReleaseStringUTFChars(jwd, wd);
		p->free_list(&wlst,0);
		return jobj;	
	}
}

JNIEXPORT jint JNICALL Java_org_brailleblaster_perspectives_braille_spellcheck_SpellChecker_addWord(JNIEnv *env, jclass clss, jstring jwd){
	const char *wd = env->GetStringUTFChars(jwd, 0);
	int result;
	
	result = p->add(wd);
	env->ReleaseStringUTFChars(jwd, wd);
	
	return result;
}

JNIEXPORT void JNICALL Java_org_brailleblaster_perspectives_braille_spellcheck_SpellChecker_closeDict(JNIEnv *env, jclass clss){
	delete(p);
}