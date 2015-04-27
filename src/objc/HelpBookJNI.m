#import <JavaVM/jni.h>
#import <Cocoa/Cocoa.h>

JNIEXPORT void JNICALL Java_edu_harvard_wcfia_yoshikoder_HelpBook_launchHelpViewer (JNIEnv *env, jclass clazz) {
	[[NSApplication sharedApplication] performSelectorOnMainThread:@selector(showHelp:) withObject:NULL waitUntilDone:NO];
}
