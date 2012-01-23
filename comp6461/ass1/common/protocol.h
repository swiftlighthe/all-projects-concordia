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
#ifndef __PROTOCOL_H__
#define __PROTOCOL_H__

#define REQUEST_PORT 5001

//#define RESP_LENGTH 40
#define BUFFER_LENGTH 8196

#define FILENAME_LENGTH 256
#define HOSTNAME_LENGTH 256


//Message type
// request
#define MSGTYPE_STRGET		"get"
#define MSGTYPE_STRPUT		"put"
#define MSGTYPE_REQ_GET			1
#define MSGTYPE_REQ_PUT			2

//#define MSGTYPE_REQ_HOSTNAME	3
//#define MSGTYPE_REQ_FILENAME	4


// response
#define MSGTYPE_RESP_OK		1

#define MSGTYPE_RESP_ERR_BASE 			100
#define MSGTYPE_RESP_FAILTOGETHEADER	MSGTYPE_RESP_ERR_BASE + 1
#define MSGTYPE_RESP_WRONGHEADER		MSGTYPE_RESP_ERR_BASE + 2
#define MSGTYPE_RESP_UNKNOWNTYPE		MSGTYPE_RESP_ERR_BASE + 3
#define MSGTYPE_RESP_FAILTOGETINFO		MSGTYPE_RESP_ERR_BASE + 4
#define MSGTYPE_RESP_FAILTORECVFILE		MSGTYPE_RESP_ERR_BASE + 5
#define MSGTYPE_RESP_NOFILE				MSGTYPE_RESP_ERR_BASE + 6



typedef struct {
	char hostname[HOSTNAME_LENGTH];
	char filename[FILENAME_LENGTH];
} MSGREQUEST, *PMSGREQUEST;

//typedef struct {
//	char response[RESP_LENGTH];
//} MSGRESPONSE, *PMSGRESPONSE; //response

// msg header
typedef struct {
	char type;
	unsigned long len;		// length of the data following this header to be received
} MSGHEADER, PMSGHEADER;



#endif
