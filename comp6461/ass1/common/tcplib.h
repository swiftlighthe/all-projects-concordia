/*
 * COMP6461 Assignment1
 *
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 *
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
 *
 */
#ifndef __TCPLIB_H__
#define __TCPLIB_H__

#include <windows.h>

#define MAXPENDING 10


class TcpLib {
protected:
	int sock; 		// client socket or server listening socket
	int client_sock; // sockets that accepted by server

	struct sockaddr_in ServerAddr; 		/* server socket address */
	struct sockaddr_in ClientAddr;

	//unsigned short ServPort; /* server port */
	WSADATA wsadata;

	char hostname[HOSTNAME_LENGTH];

public:
	TcpLib() {
	}
	~TcpLib();

	int init();
	int client_init(const char *servername);
	int server_init();
	//void server_start();

	unsigned long resolve_name(const char *name);

	static int sock_send(int sock, char *buf, int length);
	static int sock_recv(int sock, char *buf, int length);

	static int send_file(int sock, const char *filename, int len);
	static int recv_file(int sock, const char *filename, int len);
};



#endif
