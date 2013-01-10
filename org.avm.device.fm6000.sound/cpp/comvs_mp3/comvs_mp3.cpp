#include <windows.h>
#include <dshow.h>

#define info printf

#ifdef _DEBUG
#define debug printf
#else
#define debug  printf
#endif


IGraphBuilder *pGraph = NULL;
IMediaControl *pMediaControl = NULL;
IMediaEvent   *pEvent = NULL;

int initialized = FALSE;
int opened = FALSE;
int running = FALSE;

#ifdef __cplusplus
extern "C" {
#endif

void initialize(){
	HRESULT hr = NULL;

	CoInitialize(NULL);

	initialized = TRUE;
	debug("[DSU] mp3 initilized\n");

}

void dispose()
{
	HRESULT hr = NULL;

	initialized = FALSE;

	if(pEvent != NULL)
	{
		pEvent->Release();
		pEvent = NULL;
	}
	
	if(pGraph != NULL)
	{
		pGraph->Release();
		pGraph = NULL;
	}
	
	if(pMediaControl != NULL )
	{
		pMediaControl->Release();
		pMediaControl = NULL;
	}

	CoUninitialize();
}

HANDLE open(const char *name)
{
	HRESULT hr = NULL;
	WCHAR wname [1024];

	if(opened){
		return (HANDLE) -1;
	}

	if(strlen(name)>1024){
		return (HANDLE) -1;
	}

	for(int i=0;i<1024;i++){
		wname [i] = 0;
	}
	MultiByteToWideChar(CP_ACP, MB_PRECOMPOSED, name, strlen(name), wname, 1024);

	hr = CoCreateInstance(CLSID_FilterGraph, NULL, CLSCTX_INPROC, 
		IID_IGraphBuilder, (void **)&pGraph);
	if(FAILED(hr))
	{
		debug("[DSU] mp3 initilize failed error : %d \n", (int) GetLastError());
		return (HANDLE) -1;
	}

	hr = pGraph->QueryInterface(IID_IMediaControl, (void **)&pMediaControl);
	if(FAILED(hr))
	{
		debug("[DSU] mp3 initilize failed error : %d \n", (int) GetLastError());
		return (HANDLE) -1;
	}

	hr = pGraph->QueryInterface(IID_IMediaEvent, (void **)&pEvent);
	if(FAILED(hr))
	{
		debug("[DSU] mp3 initilize failed error : %d \n", (int) GetLastError());
		return (HANDLE) -1;
	}




	if(!initialized){
		return (HANDLE) -1;
	}

	debug("[DSU] open mp3 '%S'\n",wname);
	hr = pGraph->RenderFile(wname, NULL);
	if(FAILED(hr)){
		debug("[DSU] mp3 open failed error : %d (%x)\n",(int) GetLastError(),  hr);
		
	if(pEvent != NULL)
	{
		pEvent->Release();
		pEvent = NULL;
	}
	
	if(pGraph != NULL)
	{
		pGraph->Release();
		pGraph = NULL;
	}
	
	if(pMediaControl != NULL )
	{
		pMediaControl->Release();
		pMediaControl = NULL;
	}
		return (HANDLE) -1;
	}
	
	opened = TRUE;
	debug("[DSU] mp3 %d opened\n",pGraph);
	
	return (HANDLE) hr;
}

void play(HANDLE handle)
{
	HRESULT hr = NULL;
	long evCode = 0;
	if(SUCCEEDED(handle) && opened){
		debug("[DSU] play mp3 %d\n",handle);
		hr = pMediaControl->Run();
		if (SUCCEEDED(hr))
		{
			running = TRUE;
			pEvent->WaitForCompletion(INFINITE, &evCode);
			running = FALSE;
			if(pEvent != NULL)
			{
				pEvent->Release();
				pEvent = NULL;
			}
			
			if(pGraph != NULL)
			{
				pGraph->Release();
				pGraph = NULL;
			}
			
			if(pMediaControl != NULL )
			{
				pMediaControl->Release();
				pMediaControl = NULL;
			}
		}
	}
}

void pause(HANDLE handle)
{
	HRESULT hr = NULL;
	if(SUCCEEDED(handle) && opened){
		debug("[DSU] pause mp3 %d\n",handle);
		hr = pMediaControl->Pause();
	}
}

void resume(HANDLE handle)
{
	play(handle);
}

void stop(HANDLE handle)
{
	HRESULT hr = NULL;
	if(SUCCEEDED(handle) && opened && running){
		debug("[DSU] stop mp3 %d\n",handle);
		hr = pMediaControl->Stop();
	}
}

void close(HANDLE handle)
{
	if(SUCCEEDED(handle) && opened && !running){
		opened = FALSE;
	}
}

#ifdef __cplusplus
}
#endif