/*
   COMP6461 Assignment1

   Yuan Tao (ID: 5977363) 
   Xiaodong Zhang (ID: 6263879) 
 
   Course Instructor: Amin Ranj Bar 
   Lab Instructor: Steve Morse   
   Lab number: Friday 

 *
 * This file is created by Yuan Tao (ewan.msn@gmail.com) & Xiaodong Zhang
 * Licensed under GNU GPL v3
 *
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-02-11 23:22:34 -0500 (Sat, 11 Feb 2012) $
 * $Rev: 36 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/comp6461/ass1/common/syslogger.cpp $
 *
 */

#include <windows.h>
#include <stdio.h>
#include <stdarg.h>

#include "syslogger.h"

SysLogger * SysLogger::pInst = NULL;
FILE * SysLogger::pLogFile = NULL;

SysLogger::SysLogger() {
}
SysLogger::~SysLogger() {
	fclose(pLogFile);
}

SysLogger *SysLogger::inst() {
	if (pInst == NULL) {
		pInst = new SysLogger();
	}
	return pInst;
}
	
// set a log file.
int SysLogger::set(char *filename) {
	if (filename == NULL) {
		return -1;
	}
	pLogFile = fopen (filename , "w");
	if (pLogFile == NULL) {
		printf("Failed to fopen\n");
		return -1;
	}
	return 0;
}

void SysLogger::err(char *fmt, ...) {
	if (pLogFile == NULL) {
		return;
	}
	va_list args;
	va_start(args, fmt);
	vfprintf(pLogFile, fmt, args);
	fprintf(pLogFile, "\n");
	vfprintf(stdout, fmt, args);
	fprintf(stdout, "\n");
	va_end(args);

	fprintf(pLogFile, "WSAGetLastError:%d\n", WSAGetLastError());
	//fprintf(stdout, "WSAGetLastError:%d\n", WSAGetLastError());
	fflush(pLogFile);
}

void SysLogger::log(char *fmt, ...) {
	if (pLogFile == NULL) {
		return;
	}
	va_list args;
	va_start(args, fmt);
	vfprintf(pLogFile, fmt, args);
	fprintf(pLogFile, "\n");
	va_end(args);
	fflush(pLogFile);
}

// messages showing to the users
void SysLogger::out(char *fmt, ...) {
	if (pLogFile == NULL) {
		return;
	}
	va_list args;
	va_start(args, fmt);
	vfprintf(pLogFile, fmt, args);
	fprintf(pLogFile, "\n");
	vfprintf(stdout, fmt, args);
	fprintf(stdout, "\n");
	va_end(args);
	fflush(pLogFile);
}

void SysLogger::wellcome() {
	out("Wellcome to COMP6461 assignment 1.");
	out("Developed by Yuan Tao & Xiaodong Zhang.\n");
	out("Root directory of testing files for Client side is $ThisProgram\\client_files_root\\, which already has two sample files: test.txt & test.bin.");
	out("Root directory of testing files for Server side is $ThisProgram\\server_files_root\\, which already has two sample files: tests.txt & tests.bin.");
	out("If any error of Client or Server happens, please check the logs frist, which are placed under $ThisProgram\\logs\\. ");
	out("");
}



