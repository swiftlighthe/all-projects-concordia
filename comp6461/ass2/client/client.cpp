/*a small file client
 Usage: suppose client is running on sd1.encs.concordia.ca and server is running on sd2.encs.concordia.ca
 .Also suppose there is a file called test.txt on the server.
 In the client,issuse "client sd2.encs.concordia.ca test.txt size" and you can get the size of the file.
 In the client,issuse "client sd2.encs.concordia.ca test.txt time" and you can get creation time of the file
 */

/*
 * COMP6461 Assignment2

   Yuan Tao (ID: 5977363) 
   Xiaodong Zhang (ID: 6263879) 
 
   Course Instructor: Amin Ranj Bar 
   Lab Instructor: Steve Morse   
   Lab number: Friday 


 *
 * This file is modified by Yuan Tao (ewan.msn@gmail.com) & Xiaodong Zhang
 * Licensed under GNU GPL v3
 *
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
 *
 */

#include <stdio.h>
#include <iostream>
#include <string>
#include <time.h>

#include "../common/syslogger.h"
#include "../common/protocol.h"
#include "../common/socklib.h"
#include "client.h"

using namespace std;

int SockClient::handshake() {
	HANDSHAKE hs, tmp;
	int	ret = -1;
	
	srand(time(NULL));
	hs.serverSeq = 0;
	hs.clientSeq = rand();			// TODO: htonl, ntohl
	tmp.clientSeq = hs.clientSeq;
	
	// send hand shake request
	SysLogger::inst()->out("Sending Handshake Request...");

	ret = sock_sendto(sock, (char *)&hs, sizeof(HANDSHAKE), 1);
	if (ret) {
		SysLogger::inst()->err("failed to send handshake request.");
		return -1;
	}
	SysLogger::inst()->out("Sent a Handshake Request (%d, %d)", hs.clientSeq, hs.serverSeq);

	// wait for server's response
	hs.serverSeq = 0;
	hs.clientSeq = 0;
	ret = sock_recvfrom(sock, (char *)&hs, sizeof(HANDSHAKE), 1);
	if (ret) {
		SysLogger::inst()->err("failed to get handshake response.");
		return -1;
	}
	SysLogger::inst()->out("Received a Handshake Response (%d, %d)", hs.clientSeq, hs.serverSeq);

	// send hand shake response
	ret = sock_sendto(sock, (char *)&hs, sizeof(HANDSHAKE), 2);
	if (ret) {
		SysLogger::inst()->err("failed to send handshake response.");
		return -1;
	}
	SysLogger::inst()->out("Sent a Handshake Response (%d, %d)\n", hs.clientSeq, hs.serverSeq);

	if (hs.clientSeq == tmp.clientSeq) {
		// handshake OK.
		// save the server's sequence number.
		seq = hs.serverSeq;
		reset_statistics();
		
		return 0;
	}
	return -1;
}

int SockClient::start(const char *filename, const char *opname) {
	if (filename == 0 || opname == 0) {
		SysLogger::inst()->err("msg_send params error");
		return -1;
	}

	// hand shake
	static bool ifHandShake = true;
	if (ifHandShake) {
		if (handshake()) {
			return -1;
		}
		ifHandShake = false;
	}

	SysLogger::inst()->out("Sending request...");
	// create the header of msg
	MSGHEADER header;
	MSGREQUEST request;
	string filefullname = FILE_DIR_ROOT;
	filefullname += filename;

	memset((void *)&header, 0, sizeof(MSGHEADER));
	memset((void *)&request, 0, sizeof(MSGREQUEST));
	header.len = sizeof(request);
	if (strcmp(opname, MSGTYPE_STRGET) == 0)
		header.type = MSGTYPE_REQ_GET;
	else if (strcmp(opname, MSGTYPE_STRPUT) == 0) {
		header.type = MSGTYPE_REQ_PUT;

		//read the size of file to be sent to server
		FILE *pFile = 0;

		pFile = fopen(filefullname.c_str(), "rb");
		if (pFile == NULL) {
			SysLogger::inst()->err("No such a file:%s\n", filefullname.c_str());
			return -1;
		}
		fseek(pFile, 0, SEEK_END);
		header.len += ftell(pFile);
		fclose(pFile);
	}
	else {
		SysLogger::inst()->err("Wrong request type\n");
		return -1;
	}

	//send out the header + filename + hostname
	header.len = htonl(header.len);
	if (sock_sendto(sock, (char *)&header, sizeof(header)) != 0) {
		SysLogger::inst()->err("sock_send error. header.type: %d, len: %d\n", header.type, ntohl(header.len));
		return -1;
	}
	memmove(request.filename, filename, strlen(filename));
	memmove(request.hostname, hostname, strlen(hostname));
	if (sock_sendto(sock, (char *)&request, sizeof(request)) != 0) {
		SysLogger::inst()->err("sock_send error. filename: %s, hostname: %s\n",
				request.filename, request.hostname);
		return -1;
	}

	if (header.type == MSGTYPE_REQ_PUT) {
		// send file to server
		if (SockLib::send_file(sock, filefullname.c_str(), header.len - sizeof(request))) {
			return -1;
		}
	}
	show_statistics(true);

	//receive the response, first get the header
	SysLogger::inst()->out("Receiving response...");
	MSGHEADER header_resp;
	if (sock_recvfrom(sock, (char *)&header_resp, sizeof(header_resp))) {
		SysLogger::inst()->err("failed to get header of response");
		return -1;
	}

	if (header_resp.type != MSGTYPE_RESP_OK) {
		const char *ERROR_MSG[] = {
			"NULL",
			"Fail to receive the request header",
			"Wrong request header",
			"Unknown request type",
			"Fail to receive the request data",
			"Fail to receive the file",
			"No such a file",
		};

		SysLogger::inst()->err("Response ERROR: %d. %s", header_resp.type, ERROR_MSG[header_resp.type]);
		return -1;
	}

	SysLogger::inst()->out("Get an OK response, data length: %d", header_resp.len);

	// get the file from server
	if (header_resp.len > 0) {
		if (SockLib::recv_file(sock, filefullname.c_str(), header_resp.len)) {
			return -1;
		}
		SysLogger::inst()->out("Received a file: %s", filefullname.c_str());
	}
	show_statistics(false);
	return 0;
}

int main(int argc, char *argv[]) {
	// create logger
	if (SysLogger::inst()->set("../logs/client_log.txt")) {
		return -1;
	}
	SysLogger::inst()->wellcome();

	//get input
	string servername, filename, opname = "";

	while (1) {
		SysLogger::inst()->out("\nType name of ftp server (router): ");
		servername = "";
		filename = "";
		opname = "";

//		cin >> servername;
		if (servername == "quit") {
			break;
		}
		SysLogger::inst()->out("Type name of file to be transferred: ");
		cin >> filename;
		SysLogger::inst()->out("Type direction of transfer: ");
		cin >> opname;
 		servername = "Ewan-PC";
// 		filename = "s.txt";
// 		opname = "get";

		//start to connect to the server
		SockClient * tc = new SockClient();

		if (tc->udp_init(CLIENT_RECV_PORT) == 0) {
			if (tc->set_dstAddr(servername.c_str(), SERVER_RECV_PORT) == 0) {
				SysLogger::inst()->out("\nSent request to %s, waiting...\n", servername.c_str());
				
				if (tc->start(filename.c_str(), opname.c_str())) {
					// error
				}
			}
		}
		delete tc;

//		servername = "quit";
	}

	return 0;
}
