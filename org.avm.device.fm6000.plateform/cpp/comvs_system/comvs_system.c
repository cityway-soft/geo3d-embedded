#include <windows.h>
#include <time.h>

#define info printf
#ifdef _DEBUG
#define debug printf
#else
#define debug  //
#endif

long exec(const char* name, const char* args)
{
	PROCESS_INFORMATION process;
	DWORD id = 0;
	WCHAR wname[256];
	WCHAR wargs[256];

	info("[DSU] exec %s %s", name, args);

	wsprintf(wname,L"%S", name);
	wsprintf(wargs,L"%S", args);

	info("[DSU] exec %s %s", name, args);
  
	if (CreateProcess( wname,wargs, NULL, NULL, FALSE,0, NULL, NULL, NULL, &process ))
	{
		id = process.dwProcessId;
	}else{
		info("[DSU] echec  exec %s %s error: %d", name, args, (int) GetLastError());
	}
	return id;
}

int kill(long id)
{
	HANDLE process;
	BOOL result;
	process = OpenProcess (0, 0, id);
	if (process == 0)
	{
	 	return FALSE;
	}
	result = TerminateProcess(process, 0);
	CloseHandle (process);
	return result ;
}

void UnixTimeToFileTime(time_t t, LPFILETIME pft)
{
 // Note that LONGLONG is a 64-bit value
 LONGLONG ll;

 ll = Int32x32To64(t, 10000000) + 116444736000000000;
 pft->dwLowDateTime = (DWORD)ll;
 pft->dwHighDateTime = ll >> 32;
}

void UnixTimeToSystemTime(time_t t, LPSYSTEMTIME pst)
{
 FILETIME ft;

 UnixTimeToFileTime(t, &ft);
 FileTimeToSystemTime(&ft, pst);
}

int settime(long date)
{
	SYSTEMTIME sysTime;
	UnixTimeToSystemTime(date , &sysTime);
	SetSystemTime (&sysTime);
	return TRUE;
}