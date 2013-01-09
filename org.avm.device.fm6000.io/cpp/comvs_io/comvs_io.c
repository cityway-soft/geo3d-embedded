#include <windows.h>
#include <COMVS_IOSDriver.h>
#include <stdio.h>

#define info printf

#ifdef _DEBUG
#define debug printf
#else
#define debug //
#endif

HANDLE open(const char* name) {
	HANDLE handle = INVALID_HANDLE_VALUE;
	WCHAR wport[255];
	info("opening port %s \n", name);
	
	wsprintf(wport, L"%S", name);

	handle = CreateFile(wport, GENERIC_READ | GENERIC_WRITE, 0, NULL,OPEN_EXISTING, 0, NULL);
	if(handle == INVALID_HANDLE_VALUE) {
		info("echec open port error : %d \n", (int) GetLastError());
	}
	info("port %s opened, handle : %d \n", name, handle);
	return handle;
}

void close(HANDLE handle) {
	BOOL cr = FALSE;
	info("close port %d\n",handle);
	cr = CloseHandle(handle);
	if (cr == FALSE) {
		info("echec close port error : %u \n", (int) GetLastError());
	}

	info("port closed \n");
}

DWORD read(HANDLE handle, COMVS_IO_PIN data[], DWORD size) {
	DWORD result = 0;
	BOOL cr = 0;
	DWORD i;
	COMVS_IO_PINS buffer;	
	COMVS_IO_PIN pin[8];
	buffer.nbofpins = size;
	buffer.pin = (COMVS_IO_PIN*) &pin;


	debug("read %d io , handle : %d \n", size, handle);
	
	for(i =0; i<size;i++) {
		pin[i].id = data[i].id;
		pin[i].mode = data[i].mode;
		pin[i].state = data[i].state;
		pin[i].change = data[i].change;
		pin[i].option1 = data[i].option1;
		pin[i].option2 = data[i].option2;
	}
	
	cr = ReadFile(handle, &buffer, sizeof(COMVS_IO_PINS), &result, NULL);
	if (cr == FALSE) {
		info("echec read io error : %d \n", (int) GetLastError());
		result = 0;
	}
	
	
	debug("%d io  read \n", buffer.nbofpins);
	for(i =0; i<size;i++) {
		data[i].state = pin[i].state;
		data[i].change = pin[i].change;
	}
	

	return buffer.nbofpins;
}

DWORD write(HANDLE handle, COMVS_IO_PIN data[], DWORD size) {
	DWORD result = 0;
	BOOL cr = 0;
	DWORD i;
	COMVS_IO_PINS buffer;	
	COMVS_IO_PIN pin[8];
	buffer.nbofpins = size;
	buffer.pin = (COMVS_IO_PIN*) &pin;

	debug("write %d io , handle : %d \n", size, handle);
	
	for(i =0; i<size;i++) {
		pin[i].id = data[i].id;
		pin[i].mode = data[i].mode;
		pin[i].state = data[i].state;
	}

	cr = WriteFile(handle, &buffer, sizeof(COMVS_IO_PINS), &result, NULL);
	if (cr == FALSE) {
		info("echec write io error : %d \n", (int) GetLastError());
		result = 0;
	}

	debug("%d io  writed \n", buffer.nbofpins);

	return buffer.nbofpins;
}