client [
    server!e @!"";
    server?e @?"";
    Call Client ]
|
server [
    client?e @?"";
    client!e @!"";
    Call Server ]

Client:
    If success Then
        server!e @!"";
        Call Client
    Else
        server!e @!"";
        Call Client

Server:
    If success Then
        client?e @?"";
        Call Server
    Else
        client?e @?"";
        Call Server