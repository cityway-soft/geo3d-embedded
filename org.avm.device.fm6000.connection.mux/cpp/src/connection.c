#include <windows.h>
#include "connection.h"

#define IOEXCEPTION -2
#define TIMEOUT 100

#define info printf

#ifdef _DEBUG
#define debug printf
#else
#define debug  //
#endif

typedef struct _IODESCRIPTOR
{
	BOOL opened;
	HANDLE handle;
	HANDLE lock;
	DWORD timeout;
}IODESCRIPTOR;

#define IODESCRIPTOR_SIZE 20
static IODESCRIPTOR _fds[IODESCRIPTOR_SIZE];

void initialize();
void dispose();
HANDLE openImpl(LPCSTR);
void configureImpl(HANDLE, DWORD, BYTE, BYTE, BYTE, BOOL, BOOL, DWORD);
void closeImpl(HANDLE);
DWORD readImpl(HANDLE, byte*, DWORD, DWORD);
DWORD writeImpl(HANDLE, byte*, DWORD, DWORD);
DWORD availableImpl(HANDLE);

static HANDLE _lock;

void sendIOException(JNIEnv* env, char* msg)
{	jclass Exception ;
	info("[DSU] call sendIOException()\n");
	Exception = (*env)->FindClass(env,"java/io/IOException");
	(*env)->ThrowNew(env,Exception, msg);
}

/*
 * Class:     org_avm_device_connection_mux_Connection
 * Method:    openImpl
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_connection_mux_Connection_openImpl
  (JNIEnv *env, jobject obj, jstring jport)
{
	const char* port;
	char wport [256];
	HANDLE handle;
	
	port = (*env)->GetStringUTFChars(env, jport, 0);
	sprintf(wport, "%s", port);
    (*env)->ReleaseStringUTFChars(env, jport, port);

	handle = openImpl(wport);
	if(handle == INVALID_HANDLE_VALUE) 
	{
		sendIOException(env,"");
	}

	return (jint) handle;	
}

/*
 * Class:     org_avm_device_connection_mux_Connection
 * Method:    configureImpl
 * Signature: (IIIIIZZI)V
 */
JNIEXPORT void JNICALL Java_org_avm_device_connection_mux_Connection_configureImpl
  (JNIEnv *env, jobject obj, jint handle, jint baudrate, jint bitsPerChar, jint stopBits, jint parity, jboolean autoRTS, jboolean autoCTS , jint timeout)

{
	configureImpl((HANDLE) handle, baudrate, (BYTE) bitsPerChar, (BYTE)stopBits, (BYTE)parity, autoRTS, autoCTS, timeout);
}


/*
 * Class:     org_avm_device_connection_mux_Connection
 * Method:    closeImpl
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_avm_device_connection_mux_Connection_closeImpl
  (JNIEnv *env, jobject obj, jint handle)
{
	closeImpl((HANDLE) handle);
}

/*
 * Class:     org_avm_device_connection_mux_Connection
 * Method:    readImpl
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_connection_mux_Connection_readImpl
  (JNIEnv *env, jobject obj, jint handle, jbyteArray buffer, jint offset, jint length)
{
	DWORD result = 0;
	BOOL cr = 0;
	byte *b = NULL;

	b = (char*) malloc(length * sizeof(byte));
	if (b == NULL)
		return 0;

	result = readImpl((HANDLE) handle,b,0, length);
	if(result == IOEXCEPTION) 
	{
		free(b);
		sendIOException(env,"");
		return result;	
	}


	(*env)->SetByteArrayRegion(env, buffer, offset, result, b);
	free(b);
		
	return result;	
}


/*
 * Class:     org_avm_device_connection_mux_Connection
 * Method:    writeImpl
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_connection_mux_Connection_writeImpl
  (JNIEnv *env, jobject obj, jint handle, jbyteArray buffer, jint offset, jint length)
{	
	DWORD result = 0;
	BOOL cr = 0;
	byte *b = NULL;	

	b = (byte*) malloc(length * sizeof(byte));
	if (b == NULL)
		return 0;
	(*env)->GetByteArrayRegion(env, buffer, offset, length, b);
	
	result = writeImpl((HANDLE) handle, b, 0, length);
	if(result == IOEXCEPTION) 
	{
		free(b);
		sendIOException(env,"");
		return result;	
	}

	free(b);
	return result;	
}

/*
 * Class:     org_avm_device_connection_mux_Connection
 * Method:    availableImpl
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_avm_device_connection_mux_Connection_availableImpl
  (JNIEnv *env, jobject obj, jint handle)
{
	jint result;

	result = availableImpl((HANDLE) handle);
	if(result == IOEXCEPTION) 
	{
		sendIOException(env,"");
	}

	
    return result;
}

BOOL WINAPI DllMain (HANDLE hinstDLL, DWORD dwReason, LPVOID lpvReserved)
{
    switch (dwReason)
	{
        case DLL_PROCESS_ATTACH:
			initialize();
			break;

    	 case DLL_PROCESS_DETACH:  
    	 	dispose();	
			break;
		
		case DLL_THREAD_ATTACH:
			break;
			
		case DLL_THREAD_DETACH:
			break;
	}
	
    return TRUE;
}

void initialize()
{
	int i = 0;
	info("initilisation library \n");
	for(i=0;i< IODESCRIPTOR_SIZE;i++)
	{
		_fds[i].opened = FALSE;
		_fds[i].handle = NULL;
		_fds[i].lock = NULL;
		_fds[i].timeout = 3000;
	}
}

void dispose()
{
	int i = 0;
	for(i=0; i< IODESCRIPTOR_SIZE; i++)
	{
		if(_fds[i].opened == TRUE)
		{
			CloseHandle(_fds[i].handle);
			CloseHandle(_fds[i].lock);
		}
		_fds[i].opened = FALSE;
		_fds[i].handle = NULL;
		_fds[i].lock = NULL;
		_fds[i].timeout = 3000;
	}
}

HANDLE openImpl(LPCSTR port)
{
	DWORD result = 0;
	BOOL cr = 0;
	HANDLE handle;
	int i = 0;
	int index = -1;

	WCHAR wport[255];

	// open port
	info("opening port %s \n", port);
	
	for(i=0; i< IODESCRIPTOR_SIZE; i++)
	{
		if(_fds[i].opened == FALSE)
		{
			index = i;
			_fds[index].opened = TRUE;
			break;
		}
	}

	if(index == -1)
	{
		info("echec open port error : to much open descriptor \n");
		return INVALID_HANDLE_VALUE;
	}

//	MultiByteToWideChar(CP_ACP, MB_PRECOMPOSED, port, strlen(port), buffer, 255) ;
	wsprintf(wport, L"%S", port);
	handle  = CreateFile(wport, GENERIC_READ | GENERIC_WRITE, 0, NULL, OPEN_EXISTING, 0, NULL);

	if(handle == INVALID_HANDLE_VALUE){
		info("echec open port error : %d \n", (int) GetLastError());
		_fds[index].opened = FALSE;
		return INVALID_HANDLE_VALUE;
	}
	
	_fds[index].handle = handle;
	_fds[index].lock = CreateMutex(NULL, FALSE, NULL);
	
	info("port %s opened, handle : %d \n", port, handle);
	
	return (HANDLE) index;	
}


void configureImpl(HANDLE index, DWORD baudrate, BYTE bitsPerChar, BYTE stopBits, BYTE parity, BOOL autoRTS, BOOL autoCTS , DWORD timeout){

	BOOL cr = 0;
	DCB dcb;
	COMMTIMEOUTS commTimeouts;

	HANDLE handle = _fds[(int)index].handle;
	_fds[(int)index].timeout = timeout;
	
	info("initialisation handle: %d baudrate : %d  bitsPerChar : %d  stopBits : %d parity : %d autoRTS : %d autoCTS : %d timeout :%d \n", handle, baudrate, bitsPerChar, stopBits, parity, autoRTS, autoCTS ,timeout);


	cr = GetCommState(handle, &dcb);
    if(cr == FALSE)
    {
		info("echec GetCommState %d \n", (int) GetLastError());
		closeImpl(handle);
        return;
    }

	dcb.DCBlength = sizeof(DCB);
    dcb.BaudRate = baudrate;		
	dcb.fBinary = TRUE;
    dcb.fParity = TRUE;	
	dcb.fOutxCtsFlow = FALSE;
	dcb.fOutxDsrFlow = FALSE;
	dcb.fDtrControl = DTR_CONTROL_ENABLE;
	dcb.fDsrSensitivity = FALSE;	
	dcb.fTXContinueOnXoff = TRUE;
	dcb.fOutX = FALSE;
	dcb.fInX = FALSE;
	dcb.fErrorChar = FALSE;
	dcb.fNull = FALSE;
	dcb.fRtsControl = RTS_CONTROL_ENABLE ;
	dcb.fAbortOnError = TRUE;

	dcb.XonLim =0;
	dcb.XoffLim = 0;

	dcb.ByteSize = bitsPerChar;       
    dcb.Parity = (parity == 2)? TWOSTOPBITS: ONESTOPBIT;			
    dcb.StopBits = stopBits;		

    cr = SetCommState(handle, &dcb);

	SetCommMask (handle, EV_RXCHAR );

	if(cr == FALSE){
		info("echec SetCommState %d \n", (int) GetLastError());
		closeImpl(handle);
        return;
	}


	GetCommTimeouts(handle, &commTimeouts);

	commTimeouts.ReadIntervalTimeout = MAXDWORD;
	commTimeouts.ReadTotalTimeoutConstant = TIMEOUT;
	commTimeouts.ReadTotalTimeoutMultiplier = 0;
	commTimeouts.WriteTotalTimeoutConstant = TIMEOUT;
	commTimeouts.WriteTotalTimeoutMultiplier = 0;
	
	cr = SetCommTimeouts(handle, &commTimeouts);
	if(cr == FALSE){
		info("echec SetCommTimeouts %d \n", (int) GetLastError());
		closeImpl(handle);
        return;
	}


	info("port initilized handle: %d \n", handle);
}

void closeImpl(HANDLE index)
{
	DWORD result = 0;
	BOOL cr = 0;
	
	HANDLE handle = _fds[(int)index].handle;
	info("closing port \n");

	// close port
	_fds[(int)index].opened = FALSE;
	cr = CloseHandle(handle);
	if(cr == FALSE){
		info("echec close port error : %d \n", (int) GetLastError());	
	}
			
	cr = CloseHandle(_fds[(int)index].lock);

	info("port closed \n");
}

DWORD readImpl(HANDLE index, byte* buffer, DWORD offset, DWORD length){

	DWORD result , loop = 0;
	DWORD status = 0;
    COMSTAT comstat;
    unsigned long error = 0;
	BOOL cr = 0;
	int i =0;
	DWORD to, now, value = 0;

	HANDLE handle = _fds[(int)index].handle;

	debug("read %d bytes offset %d handle : %d \n",length , offset, handle );


	to = GetTickCount();
	now = to;
	loop = 0;
	do{
	
		value = _fds[(int)index].timeout - (now - to);
	
		debug("read to : %d now : %d  timeout : %d\n",to , now , value );

		result = WaitForSingleObject( _fds[(int)index].lock, value);
		if(result != WAIT_OBJECT_0) 
		{
			info("[DSU] readImpl : WaitForSingleObject error\n");
			return IOEXCEPTION;
		}
		debug("[DSU] readImpl : call ReadFile\n");
		cr = ReadFile (handle, buffer, length, &result, 0);
		if (cr == FALSE){	
			cr = ClearCommError(handle, &error, &comstat);
			if (cr == FALSE)
			{
				info("echec ClearCommError \n");
			}
			info("echec ReadFile \n");
			result = IOEXCEPTION;
		}

		ReleaseMutex(_fds[(int)index].lock);

		now = GetTickCount();
		loop++;

	}while( (_fds[(int)index].opened == TRUE) 
		&& (result == 0) 
		&& (now < (to + _fds[(int)index].timeout)));

	info("%d bytes read [%d] loop \n", result,loop );
	
	
	for(i=0; i < (int) result;i++)
	{
		debug("read %d %c \n", buffer[i], buffer[i]);
	}
	

	return result;
}

DWORD writeImpl(HANDLE index, byte* buffer, DWORD offset, DWORD length)
{
	DWORD result = 0;
	BOOL cr = 0;
	COMSTAT comstat;
    unsigned long error = 0;	
	int i =0;

	HANDLE handle = _fds[(int)index].handle;

	debug("write %d bytes \n", length);
	
	for(i=0; i < (int)length;i++)
	{
		debug("write %d\n", buffer[i]);
	}


	result = WaitForSingleObject( _fds[(int)index].lock, _fds[(int)index].timeout);
	if(result != WAIT_OBJECT_0) 
	{
		info("[DSU] writeImpl : WaitForSingleObject error\n");
		return IOEXCEPTION;
	}

	debug("[DSU] writeImpl : call WriteFile\n");
	cr = WriteFile(handle, buffer, length, &result, NULL);
	if (cr == FALSE){	
		cr = ClearCommError(handle, &error, &comstat);
		if (cr == FALSE)
		{
			info("echec ClearCommError \n");
		}
		info("echec WriteFile \n");	
		result = IOEXCEPTION;
	}

	ReleaseMutex(_fds[(int)index].lock);

	debug("%d bytes  writed \n", result);
	
	return result;
} 

DWORD availableImpl(HANDLE index)
{
	DWORD result = 0;
	BOOL cr = 0;
	COMSTAT comstat;
    unsigned long error = 0;

	HANDLE handle = _fds[(int)index].handle;


	result = WaitForSingleObject( _fds[(int)index].lock, _fds[(int)index].timeout);
	if(result != WAIT_OBJECT_0) 
	{
		return 0;
	}

    cr = ClearCommError(handle, &error, &comstat);
    if (cr == FALSE)
	{
		info("echec ClearCommError \n");
		result = IOEXCEPTION;
	}else
	{
		result = comstat.cbInQue;
	}

    ReleaseMutex(_fds[(int)index].lock);

    return result;
}