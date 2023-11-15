client [
    server!var @!"";
    server?res @?"";
    End
]
    |
server [
    client?req @? "";
    proxy!req @! "";
    proxy?res @? "";
    client!res @! "";
    End
]