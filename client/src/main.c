#define WIN32_LEAN_AND_MEAN

#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <time.h>
#include <assert.h>

#define FILE_LEN 32
#define NET_PORT 2556
#define BUFF_SIZE 256
#define KEY_LEN 128

static int processMessage(const char *msg, int size);
static void generateKey(char *buff, const int len);

int main(void)
{
    FILE *keyFile = fopen("key.key", "r");
    char key[KEY_LEN];
    if(!keyFile)
    {
        generateKey(key, KEY_LEN);
        keyFile = fopen("key.key", "w");
        assert(keyFile);
        fputs(key, keyFile);
        fclose(keyFile);
    }
    else
    {
        assert(key);
        fgets(key, sizeof(key), keyFile);
        fclose(keyFile);
    }
    
    printf("%s\n", key);

    FILE* f = fopen("ip.txt", "r");

    if(!f)
    {
        printf("Failed to upen ip.txt file!");
        return 1;
    }

    char ip[FILE_LEN];
    fgets(ip, sizeof(ip), f);
    fclose(f);

    printf("[INFO]: Server IP: %s\n", ip);

    WSADATA wsaData;
    int res  = WSAStartup(MAKEWORD(2,2), &wsaData);

    if (res != 0) 
    {
        printf("[ERROR]: WSAStartup failed with error: %d\n", res);
        return 1;
    }

    
    SOCKET sockfd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);;
    struct sockaddr_in serv_addr;
    const struct hostent *serv = gethostbyname(ip);

    if(serv == NULL)
    {
        printf("[ERROR]: Invalid IP address\n");
        return 1;
    }

    ZeroMemory(&serv_addr, sizeof(serv_addr));
    CopyMemory(&serv->h_addr, &serv_addr.sin_addr.s_addr, serv->h_length);
    serv_addr.sin_addr.s_addr = inet_addr(ip);
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(NET_PORT);

    bool isConnected = false;

    while(!isConnected)
    {
        printf("[INFO]: Connecting...\n");
        res = connect(sockfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr));

        if (res != 0)
        {
            printf("[ERROR]: Connect failed with error: %d\n", WSAGetLastError());
            Sleep(100); 
        }
        else
        {
            isConnected = true;
            break;
        }
    }

    printf("[INFP]: Connection to the server succeed\n");
    res = send(sockfd, key, KEY_LEN, 0);

    while(isConnected)
    {
        char buff[BUFF_SIZE];
        res = recv(sockfd, buff, BUFF_SIZE, 0);
        if(res > 0)
        {
            processMessage(buff, res);
        }
        if(res <= 0)
        {
            isConnected = false;
            printf("[INFO]: Connection closed, last error: %i\n", res);
            break;
        }
    }
    closesocket(sockfd);
    WSACleanup();
    return 0;
}

static int processMessage(const char *msg, int size)
{

}

static void generateKey(char *buff, const int len)
{
    srand((unsigned) time(NULL));

    assert(buff);

    for(int i = 0; i < len; ++i)
    {
        int m = rand() % (122 - 65 + 1) + 65;
        buff[i] = (char) m;
    }
    buff[len - 1] = '\0';
}