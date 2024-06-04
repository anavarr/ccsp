server [
    Call Server
]

|

client [
    Call Client
]

Server :
    client?req @?"";
    client!response @!"";
    Call Server

Client :
    server! request@!"";
    server? response@?"";
    Call Client