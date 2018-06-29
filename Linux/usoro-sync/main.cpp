#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <thread>

void kurwaodbiur(int newsockfd)
{

}

int main()
{
    int sockfd, newsockfd, portno;
    socklen_t clilen;
    char buffer[256];
    struct sockaddr_in serv_addr, cli_addr;
    int n;

    sockfd = socket(AF_INET, SOCK_STREAM, 0); //obiekt socketu

    bzero((char*) &serv_addr, sizeof(serv_addr)); // wyczyszczenie pamięci dla serv_addr (https://linux.die.net/man/3/bzero)
    portno = 1488; // tak jak dokumentacja mówi

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno); // https://linux.die.net/man/3/htons

    bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr));

    // tutaj taktyczny async (nowy thread)

    listen(sockfd, 5); //https://linux.die.net/man/3/listen
    clilen = sizeof(cli_addr);
    //while(true) bo sobie listenujemy i newsockfd wrzucamy do threadu
    newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);

    bzero(buffer,256); //much low-level yoooooo
    n = read(newsockfd, buffer, 256);
    if(n<0)
    {
        printf("cos sie spierdolilo xD\n");
    }

    printf("buffer = %s\n", buffer);

    n = write(newsockfd, "penis", 5); // addr, wiadomość, długość
    if(n<0)
    {
        printf("znowu cos sie spierdolilo xDDDD\n");
    }

    close(newsockfd);
    close(sockfd);

    printf("Hello world!");
    return 0;
}
