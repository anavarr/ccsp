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
    If check(x)
    Then Call Server
    Else End

Client :
    server! request@!"";
    server? response@?"";
    If check(x)
    Then Call Client
    Else End