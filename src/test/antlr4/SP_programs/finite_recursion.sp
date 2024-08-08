server [ Call Server ]
|
client [ Call Client ]

Server :
    client?request @? "";
    client!response @!"";
    Call Server

Client:
    server !request @!"";
    server ?response @?"";
    If testCdt()
    Then
        Call Client
    Else
        End