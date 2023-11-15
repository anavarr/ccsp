client [
    server!var @!"";
    server?res @?"";
    End
]
    |
server [
    client?req @? "";
    client!res @! "";
    End
]